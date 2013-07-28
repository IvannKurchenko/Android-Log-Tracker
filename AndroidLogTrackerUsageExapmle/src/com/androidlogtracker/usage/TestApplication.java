package com.androidlogtracker.usage;

import android.graphics.Bitmap;
import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.config.EmailLogSendingConfiguration;
import com.logtracking.lib.api.config.LogConfiguration;
import android.app.Application;

public class TestApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

        /*
         *  Configuring sending config.
         *
         *  example.email.@gmail.com - email account special for library , that will be used to send emails with bug reports.
         *  pass - password to this account.
         *  developer.email@gmail.com - one of bug reports mail recipient.
         */
        EmailLogSendingConfiguration emailLogSendingSettings = new EmailLogSendingConfiguration("example.email.@gmail.com" ,
                                                                                        "pass",
                                                                                        "developer.email@gmail.com");
        emailLogSendingSettings.addEmailRecipient("another.recipient@email.com");


        LogConfiguration configuration  =   LogConfiguration.LogConfigurationBuilder.newDebugConfiguration(this).

                                            setLogFileFormat(LogConfiguration.LogFileFormat.HTML).
                                            addTagToFilter(TestTags.ERROR_TAG,true).
                                            addTagToFilter(TestTags.VERBOSE_TAG, true).
                                            setLevelFilter(Log.DEBUG).
                                            setLogFileRotationTime(30 * 1000).
                                            addAttachedFileToReport("/data/anr/traces.txt").
                                            setAfterCrashAction(LogConfiguration.AfterCrashAction.CLOSE_APPLICATION).

                                            setSendingSettings(emailLogSendingSettings).

                                            setSnapshotFormat(Bitmap.CompressFormat.PNG).
                                            setSnapshotQuality(50).

                                            putMetaData("meta-key", "meta-value").
                                            putMetaData("meta-key", "meta-value").

                                            build();

		Log.init(configuration,this);
	}
}
