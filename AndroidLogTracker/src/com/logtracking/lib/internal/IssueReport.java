package com.logtracking.lib.internal;

import java.io.File;

public class IssueReport {

    private File mReportFile;
    private String mIssueMessage;

    public IssueReport(File file , String message){
        mReportFile = file;
        mIssueMessage = message;
    }

    public File getReportFile(){
        return mReportFile.getAbsoluteFile();
    }

    public String getIssueMessage(){
        return mIssueMessage;
    }
}