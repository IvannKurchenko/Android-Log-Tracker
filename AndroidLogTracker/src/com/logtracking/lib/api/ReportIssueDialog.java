package com.logtracking.lib.api;

import com.logtracking.lib.internal.IssueReporter;

public class ReportIssueDialog {

    private static IssueReporter sIssueReporter;

    static void init(IssueReporter issueReporter){
        sIssueReporter = issueReporter;
    }

    /**
     * Shows issue reporting dialog.
     * Should be called only after Log.init() method.
     */
    public static void show(){
        sIssueReporter.showReportIssueDialog();
    }

}
