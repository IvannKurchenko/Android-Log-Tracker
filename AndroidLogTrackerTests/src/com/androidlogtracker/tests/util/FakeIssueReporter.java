package com.androidlogtracker.tests.util;

import com.logtracking.lib.internal.LogContext;
import com.logtracking.lib.internal.IssueReporter;

public class FakeIssueReporter extends IssueReporter{

    private boolean reportIssueAcclivityStarted;
    private boolean crashListenerNotified;

    public FakeIssueReporter(LogContext logContext) {
        super(logContext, null, null);
    }

    @Override
    public void showReportIssueDialog() {
        reportIssueAcclivityStarted = true;
    }

    @Override
    protected void showCrashReportDialog() {
        reportIssueAcclivityStarted = true;
    }

    @Override
    protected void setCrashData(Thread crashedThread, Throwable uncaughtException) {
    }

    @Override
    protected void showReportDialog(int mode) {
        reportIssueAcclivityStarted = true;
    }

    @Override
    protected void notifyCrashListener() {
        crashListenerNotified = true;
    }

    @Override
    protected void prepareIssueReport(String message) {
    }

    @Override
    protected void prepareCrashReport() {
        super.prepareCrashReport();
    }

    @Override
    protected boolean isSendingSupport() {
        return false;
    }

    public boolean isReportIssueActivityStarted(){
        return reportIssueAcclivityStarted;
    }

    public boolean isCrashListenerNotified(){
        return crashListenerNotified;
    }
}
