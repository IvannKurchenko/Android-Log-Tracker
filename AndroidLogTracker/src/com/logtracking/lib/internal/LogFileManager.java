
package com.logtracking.lib.internal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.config.LogConfiguration;
import com.logtracking.lib.internal.format.LogFileFormatter;
import com.logtracking.lib.internal.format.LogFileFormatterFactory;
import com.logtracking.lib.api.config.LogConfiguration.*;

import android.os.AsyncTask;

public class LogFileManager {
	
	private static final String FILE_NAME_SEPARATOR = System.getProperty("file.separator");
	protected static final String REPORT_DIRECTORY_NAME = "reports";
	private static final String REPORT_FILE_NAME = "report";
	private static final String ANDROID_RUNTIME_TAG = "AndroidRuntime";

	/**
	 * Should be called only from UI thread!
	 */
	public interface ReportPrepareListener {
		public void onReportPrepared(IssueReport report);
		public void onReportPreparationFail();
	}

	private ReportPrepareListener mInternalOnReportPreparationsListener = new ReportPrepareListener() {
		
		@Override
		public void onReportPrepared(IssueReport report) {
			resumeLogSaving();
			if (mOnReportPrepareListener != null){
				mOnReportPrepareListener.onReportPrepared(report);
			}
		}
		
		@Override
		public void onReportPreparationFail() {
			resumeLogSaving();
			if (mOnReportPrepareListener != null){
				mOnReportPrepareListener.onReportPreparationFail();
			}
		}
		
		private void resumeLogSaving(){
			mPreparationTasksCount--;
			if( needToResumeLogSaving() ){
				mSaveLogTask.setCaWriteInFile(true);
			}
		}
		
		private boolean needToResumeLogSaving(){
			return (mLogContext.getLogConfiguration().getLogSavingMode() == LogConfiguration.LogSavingMode.SAVE_ALL_LOG_IN_FILE) &&
				   (mPreparationTasksCount == 0 ) &&
				   (mSaveLogTask != null); 
		}
	};
	
	private LogContext mLogContext;
	private LogFilter mLogFilter;
	private LogSavingTask mSaveLogTask;
	private SimpleDateFormat mDateFormat;
	private ReportPrepareListener mOnReportPrepareListener;
	private File mLogFilesDirectory;
	private File mReportDeprecatory;
	private LogFileFormatter mFileFormatter;
	
	private int mPreparationTasksCount;
	
	public LogFileManager(LogContext logContext){
        mLogContext = logContext;
		mLogFilter = LogFilter.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
		mLogFilesDirectory = new File(mLogContext.getLogConfiguration().getLogDirectoryName());
		String reportsDirectory = mLogContext.getLogConfiguration().getLogDirectoryName() + FILE_NAME_SEPARATOR + REPORT_DIRECTORY_NAME;
		mReportDeprecatory = new File(reportsDirectory);
		mFileFormatter = LogFileFormatterFactory.getFormatter(mLogContext.getLogConfiguration());
	}
	
	public void startLogSaving(){
		if (mLogContext.getLogConfiguration().getLogSavingMode() == LogSavingMode.SAVE_ALL_LOG_IN_FILE && !isSaving()){
			prepareLogDirectory();
			mSaveLogTask = new LogSavingTask(mLogContext);
			mSaveLogTask.startProcess();
		}
	}
	
	public void stopLogSaving(){
		if (isSaving()){
			mSaveLogTask.flush();
			mSaveLogTask.cancel(true);
		}
	}
	
	/**
	 * Should be called only from UI thread!
	 */
	protected void prepareCrashReport(Thread crashedThread , Throwable uncaughtException , ReportPrepareListener onReportPrepareListener){
		prepareReportDirectory();
		
		if(!isSaving()){
			
			if (mLogContext.getLogConfiguration().isLoggingAvailable()){
				saveLogDump(null,BaseLogTask.CRASH_REPORT_MESSAGE,onReportPrepareListener);
			} else {
				saveCrash(crashedThread, uncaughtException, onReportPrepareListener);
			}
			
		} else  {
			prepareReport(BaseLogTask.CRASH_REPORT_MESSAGE , onReportPrepareListener);
		}
	}
	
	/**
	 * Should be called only from UI thread!
	 */
	protected void prepareIssueReport(String message , ReportPrepareListener onReportPrepareListener){
		prepareReportDirectory();
		
		if(!isSaving()){
			
			if (mLogContext.getLogConfiguration().isLoggingAvailable()){
				saveLogDump(null,BaseLogTask.CRASH_REPORT_MESSAGE,onReportPrepareListener);
			}
			
		} else  {
			prepareReport(message , onReportPrepareListener);
		}
	}

	private void saveCrash(Thread thread,Throwable throwable,ReportPrepareListener onReportPrepareListener){
		String crashStr = Log.getStackTraceString(throwable);
		List<LogModel> crashList = new ArrayList<LogModel>();
		String [] crashStackTrace = crashStr.split("\n");
		
		String packageName = mLogContext.getApplicationContext().getApplicationInfo().packageName;
		int pid = mLogFilter.getPidByPackageName(packageName);

        for (String crashStackTraceItem : crashStackTrace) {
            crashList.add(new LogModel(pid, thread.getId(), Log.ERROR, packageName, ANDROID_RUNTIME_TAG, crashStackTraceItem));
        }
		saveLogDump(crashList,BaseLogTask.CRASH_REPORT_MESSAGE,onReportPrepareListener);
	}
	
	private void saveLogDump(List<LogModel> crashList,String message,ReportPrepareListener onReportPrepareListener){
		LogSavingTask dumpLogTask = new LogSavingTask(mLogContext);
		dumpLogTask.setCrashStack(crashList);
        dumpLogTask.setReportMessage(message);
		dumpLogTask.setSaveDump(true);
		dumpLogTask.setReportPrepareListener(onReportPrepareListener);
		dumpLogTask.setLogFile(getReportFileName());
		dumpLogTask.startProcess();
	}
	
	private void prepareReport(String reportMessage , ReportPrepareListener onReportPrepareListener){
		mOnReportPrepareListener = onReportPrepareListener;
		mSaveLogTask.flush();
		mSaveLogTask.setCaWriteInFile(false);
		
		ReportPreparationTask reportTask = new ReportPreparationTask(mLogContext);
		reportTask.setReportMessage(reportMessage);
		reportTask.setReportPrepareListener(mInternalOnReportPreparationsListener);
		reportTask.setLogFile(getReportFileName());	
		reportTask.startProcess();
		
		mPreparationTasksCount++;
	}
	
	private File getReportFileName(){
		return new File(mReportDeprecatory.getAbsolutePath() +
						FILE_NAME_SEPARATOR + 
						REPORT_FILE_NAME + 
						"_" + mDateFormat.format(new Date()) + 
						mFileFormatter.getFileExtension());
	}
	
	private boolean isSaving(){
		return (mSaveLogTask!=null && mSaveLogTask.getStatus() == AsyncTask.Status.RUNNING);
	}
	
	private void prepareLogDirectory(){
		createDirectory(mLogFilesDirectory);
	}
	
	private void prepareReportDirectory(){
		createDirectory(mReportDeprecatory);
	}
	
	private void createDirectory(File dir){
		if (!dir.exists()){
			dir.mkdirs();
		}
	}
}
