package com.logtracking.lib.internal;

import java.io.File;

import com.logtracking.lib.internal.upload.LogReportSender;

class LogFileUploadHandler implements LogReportSender.OnFileSendListener {

	private AndroidNotifier mNotifier;
	private SnapshotHelper mSnapshotHelper;

	public LogFileUploadHandler(AndroidNotifier notifier, SnapshotHelper snapshotHelper) {
        mNotifier = notifier;
        mSnapshotHelper = snapshotHelper;
	}
	
	@Override
	public void onFileSendSuccess(IssueReport issueReport) {
        mNotifier.sendSuccessUploadNotification();
        issueReport.getReportFile().delete();
        mSnapshotHelper.removeSnapshots();
	}
	
	@Override
	public void onFileSendFail(IssueReport issueReport) {
        mNotifier.sendFailUploadNotification();
        issueReport.getReportFile().delete();
	}
}
