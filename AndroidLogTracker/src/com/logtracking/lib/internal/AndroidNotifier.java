package com.logtracking.lib.internal;


import com.androidlogtracker.R;
import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.settings.LogSettings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

class AndroidNotifier {

	private Context mApplicationContext;
	private NotificationManager mNotificationManager;
	
	private String mNotificationSubject;
	private String mSuccessUploadNotificationMessage;
	private String mFailUploadNotificationMessage;
	
	private LogSettings mSettings;
	
	public AndroidNotifier(LogContext logContext) {
		mApplicationContext = logContext.getApplicationContext();
		mSettings = logContext.getLogSettings();
		mNotificationManager = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationSubject = mApplicationContext.getResources().getString(R.string.alt_report_notification_subject);
		mSuccessUploadNotificationMessage = mApplicationContext.getResources().getString(R.string.alt_report_notification_success_upload_message);
		mFailUploadNotificationMessage = mApplicationContext.getResources().getString(R.string.alt_report_notification_failed_upload_message);
	}
	
	void sendSuccessUploadNotification(){
		if (mSettings.isNotifyAboutReportSendResult()){
			sendNotification(mSuccessUploadNotificationMessage);
		}
	}
	
	void sendFailUploadNotification(){
		if (mSettings.isNotifyAboutReportSendResult()){
			sendNotification(mFailUploadNotificationMessage);
		}
	}
	
	private void sendNotification(String message){
		Notification notification = new NotificationCompat.Builder(mApplicationContext)
		.setContentIntent(PendingIntent.getActivity(mApplicationContext, 0, new Intent(), 0))
		.setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(mNotificationSubject)
        .setContentText(message)
        .build();
    
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		mNotificationManager.notify(0, notification);
	}
}
