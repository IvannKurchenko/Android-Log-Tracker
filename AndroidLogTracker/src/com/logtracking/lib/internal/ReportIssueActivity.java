package com.logtracking.lib.internal;

import android.app.Activity;
import android.os.Bundle;

/**
 * Base class for extending to show custom report issue activity.
 */
public class ReportIssueActivity extends Activity {

    static final String REPORT_MODE = "report_mode";

    /**
     * This mode using to show report dialog about application crash.
     * Usually dialog with this mode should contains just buttons with "send" and "cancel"
     * functionality to send issue report.
     */
    public static final int SHOW_CRASH_REPORT_DIALOG = 0;

    /**
     * This mode using to show simple dialog with possibility to show send issue report with some description.
     * Usually dialog with this mode should contains  buttons with "send" and "cancel" functionality
     * and edit text to describe the problem
     */
    public static final int SHOW_ISSUE_REPORT_DIALOG = 1;

    /**
     * This mode using to show report dialog about application crash, in case if there is no information
     * about sending of issue report.
     * (for example, there is no any setted of {@link com.logtracking.lib.api.config.LogSendingConfiguration} in
     * {@link com.logtracking.lib.api.config.LogConfiguration}).
     * Usually dialog with this mode should contains just button "OK".
     */
    public static final int SHOW_CRASH_NOTIFICATION_DIALOG = 2;

    private int mMode;
    private IssueReporter mIssueReporter;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        init();
    }


    /**
     * Return current mode of report issue dialog.
     * @return current mode of report issue dialog.
     * @see #SHOW_CRASH_NOTIFICATION_DIALOG
     * @see #SHOW_CRASH_REPORT_DIALOG
     * @see #SHOW_ISSUE_REPORT_DIALOG
     */
    protected final int getMode(){
        return mMode;
    }

    /**
     * Notify about sending report of issue report and finish current activity.
     * @param issueMessage message with problem description.
     */
    protected final void onSendIssueReport(String issueMessage){
        switch (mMode) {
            case SHOW_CRASH_REPORT_DIALOG:
                sendCrashReport();
                notifyAndExit();
                break;

            case SHOW_ISSUE_REPORT_DIALOG:
                sendIssueReport(issueMessage);
                break;

            case SHOW_CRASH_NOTIFICATION_DIALOG:
                notifyAndExit();
                break;
        }
    }

    /**
     * Notify about cancel sending of issue report and finish current this activity.
     */
    protected final void onCancel(){
        mIssueReporter.notifyCrashListener();
        finish();
    }

    private void init(){
        mMode = getIntent().getIntExtra(REPORT_MODE, SHOW_ISSUE_REPORT_DIALOG);
        mIssueReporter = StateHolder.getIssueReporter();
    }

    private void sendCrashReport(){
        mIssueReporter.prepareCrashReport();
        mIssueReporter.notifyCrashListener();
    }

    private void sendIssueReport(String issueMessage){
        mIssueReporter.prepareIssueReport(issueMessage);
        finish();
    }

    private void notifyAndExit(){
        mIssueReporter.notifyCrashListener();
        finish();
    }
}
