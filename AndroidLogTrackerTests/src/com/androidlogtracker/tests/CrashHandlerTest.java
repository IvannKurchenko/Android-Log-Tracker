package com.androidlogtracker.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.androidlogtracker.tests.util.FakeIssueReporter;
import com.androidlogtracker.usage.TestActivity;
import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.internal.CrashHandler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrashHandlerTest extends ActivityInstrumentationTestCase2<TestActivity> {

    private CrashHandler mCrashHandler;
    private FakeIssueReporter mFakeIssueReporter;

    public CrashHandlerTest() {
        super(TestActivity.class);
    }

    public void setUp(){
        LogContext logContext = new LogContext.LogContextBuilder(getActivity()).build();
        mFakeIssueReporter  = new FakeIssueReporter(logContext);
        mCrashHandler = new CrashHandler(logContext,mFakeIssueReporter);
    }

    public void testCrashDialogShouldAppear(){
        mCrashHandler.uncaughtException(Thread.currentThread(), generateExternalException());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue("Crash dialog not showed", mFakeIssueReporter.isReportIssueActivityStarted());
            }
        });

    }

    public void testCrashDialogShouldNotAppear(){
        mCrashHandler.uncaughtException(Thread.currentThread(), generateInternalException());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue("Crash dialog not showed", !mFakeIssueReporter.isReportIssueActivityStarted());
            }
        });
    }

    public void testCrashListenerShouldBeInvoked(){
        mCrashHandler.uncaughtException(Thread.currentThread(), generateExternalException());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue("Crash dialog not showed", mFakeIssueReporter.isCrashListenerNotified());
            }
        });
    }

    private Throwable generateExternalException(){
        return generateException(5,this.getClass().getSimpleName(),this.getClass().getCanonicalName());
    }

    private Throwable generateInternalException(){
        return  generateException(5, Log.class.getSimpleName(), Log.class.getCanonicalName());
    }

    private Throwable generateException(int traceSize, String cls, String file){
        Exception exception = new Exception();
        StackTraceElement[] stackTrace = new StackTraceElement[traceSize];
        for(int i=0; i<traceSize;i++){
            stackTrace[i] = new StackTraceElement(cls,file,"testMethod",0);
        }
        exception.setStackTrace(stackTrace);
        return exception;
    }

    private void runOnUiThread(final Runnable runnable){
        final Lock lock = new ReentrantLock();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                lock.unlock();
            }
        });
        lock.lock();
    }
}
