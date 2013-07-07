package com.logtracking.lib.internal;

import com.logtracking.lib.api.config.LogConfiguration;
import com.logtracking.lib.internal.upload.LogFileSenderFactory;
import com.logtracking.lib.internal.upload.LogReportSender;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.logtracking.lib.internal.ReportIssueActivity.*;

public class IssueReporter {
	
	private LogFileManager.ReportPrepareListener onReportPrepareListener = new LogFileManager.ReportPrepareListener() {
		
		@Override	
		public void onReportPrepared(final IssueReport report) {
            mSendingService.execute(new Runnable() {
                @Override
                public void run() {
                    mLogFileSender.sendReport(report);
                }
            });
		}
		
		@Override
		public void onReportPreparationFail() {
			mNotifier.sendFailUploadNotification();
		}
	};

    private final int POOL_SIZE = 3;

	private Context mApplicationContext;
	private LogConfiguration mLogConfiguration;
	private LogReportSender mLogFileSender;
    private LogFileManager mLogFileManager;
	private AndroidNotifier mNotifier;
	private ExecutorService mSendingService;

	private Thread mCrashedThread;
	private Throwable mUncaughtException;
	private int mReportIssueDialogMode;
	
	public IssueReporter(LogContext logContext , LogFileManager fileManager) {
		mApplicationContext = logContext.getApplicationContext();
        mLogConfiguration = logContext.getLogConfiguration();
        mLogFileManager = fileManager;
		mNotifier = new AndroidNotifier(logContext);
        mSendingService = Executors.newFixedThreadPool(POOL_SIZE);

        if(isSendingSupport()){
            mLogFileSender = LogFileSenderFactory.newSender(logContext);
            mLogFileSender.setListener(new LogFileUploadHandler(mNotifier));
        }
	}

	public void showReportIssueDialog(){
		if (isSendingSupport()) {
			showReportDialog(SHOW_ISSUE_REPORT_DIALOG);
		}
	}
	
	protected void showCrashReportDialog(){
		if (isSendingSupport()) {
			showReportDialog(SHOW_CRASH_REPORT_DIALOG);
		} else {
			showReportDialog(SHOW_CRASH_NOTIFICATION_DIALOG);
		}
	}

	protected void setCrashData(Thread crashedThread , Throwable uncaughtException){
		mCrashedThread = crashedThread;
        mUncaughtException = uncaughtException;
	}

	protected void showReportDialog(int mode){
		mReportIssueDialogMode = mode;
		Intent intent = new Intent(mApplicationContext , ReportIssueActivity.class);
		if (mReportIssueDialogMode == SHOW_CRASH_REPORT_DIALOG){
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ReportIssueActivity.REPORT_MODE, mode);
		mApplicationContext.startActivity(intent);
	}
	
	protected void notifyCrashListener(){
		if (mReportIssueDialogMode == SHOW_CRASH_REPORT_DIALOG){
			OnCrashHandledAction.ActionResolver.getAction(mLogConfiguration).onCrashHandled(mApplicationContext);
		}
	}
	
	protected void prepareIssueReport(String message){
        mLogFileManager.prepareIssueReport(message, onReportPrepareListener);
	}
	
	protected void prepareCrashReport(){
        mLogFileManager.prepareCrashReport(mCrashedThread, mUncaughtException, onReportPrepareListener);
	}

	protected boolean isSendingSupport(){
		return mLogConfiguration.getSendingSettings() != null;
	}

}
