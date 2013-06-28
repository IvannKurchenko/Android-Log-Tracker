package com.logtracking.lib.internal;

import java.io.File;

import com.logtracking.lib.internal.upload.LogReportSender;

class LogFileUploadHandler implements LogReportSender.OnFileSendListener {

	private AndroidNotifier mNotifier;
	
	public LogFileUploadHandler(AndroidNotifier notifier) {
        mNotifier = notifier;
	}
	
	@Override
	public void onFileSendSuccess(File file) {
        mNotifier.sendSuccessUploadNotification();
		file.delete();
	}
	
	@Override
	public void onFileSendFail(File file) {
        mNotifier.sendFailUploadNotification();
		file.delete();
	}
}
