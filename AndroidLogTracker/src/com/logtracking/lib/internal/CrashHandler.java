package com.logtracking.lib.internal;

import java.lang.Thread.UncaughtExceptionHandler;

import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.settings.LogSettings;

import android.os.Handler;
import android.os.Looper;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final String LIB_PACKAGE = "com.logtracking.lib";
	private static final int STACK_DEPTH_CHECK = 5;
	
	private UncaughtExceptionHandler mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	private IssueReporter mIssueReporter;
	private LogSettings mLogSettings; 
	
	private Thread mCrashedThread;
	private Throwable mUncaughtException;
	private Handler mUiHandler;
	
	public CrashHandler(LogContext logContext, IssueReporter issueReporter) {
		mLogSettings = logContext.getLogSettings();
		mIssueReporter = issueReporter;	
		mUiHandler = new Handler(Looper.getMainLooper());
	}

    @Override
	public void uncaughtException(Thread thread, Throwable exception) {
		try{

			mCrashedThread = thread;
            mUncaughtException = exception;

            checkIsInternalError();

			printStackTrace();
			handleCrash();
			resumeMainLooper();

		} catch (Throwable e){
			mDefaultExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
	}
	
	private void handleCrash(){
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				try{
					
					mIssueReporter.setCrashData(mCrashedThread , mUncaughtException);
					mIssueReporter.showCrashReportDialog();
					
				} catch (Throwable e){
					mDefaultExceptionHandler.uncaughtException(Thread.currentThread(), e);
				}
			}
		});
	}
	
	private void printStackTrace(){
		if (mLogSettings.isLoggingAvailable()){
            mUncaughtException.printStackTrace();
		}
	}
	
	private void resumeMainLooper(){
		try{
			if (isMainThread()){
				Looper.loop();
			}
		} catch (Throwable exception){
				uncaughtException(Thread.currentThread(), exception);
		}
	}

    /* Checks is crash on library side to prevent infinity showing of crash window.*/
    private void checkIsInternalError(){
        if ( isInternalException(mUncaughtException.getCause()) ||
             isInternalException(mUncaughtException) ) {

            mDefaultExceptionHandler.uncaughtException(mCrashedThread,mUncaughtException);

        }
    }

	private boolean isInternalException(Throwable exception){
        if (exception == null)
            return false;

		int targetCheckDepth =  exception.getStackTrace().length > STACK_DEPTH_CHECK  ? STACK_DEPTH_CHECK :
                                exception.getStackTrace().length-1;

		for (int i = 0;i<targetCheckDepth;i++){
			String classLocation = exception.getStackTrace()[i].getClassName();
			if (classLocation.contains(LIB_PACKAGE))
				return true;
		}
		return false;
	}
	
	private boolean isMainThread(){
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
}
