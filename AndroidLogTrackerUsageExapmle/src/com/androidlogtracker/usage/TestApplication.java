package com.androidlogtracker.usage;

import com.logtracking.lib.api.CrashHandlingAction;
import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.MetaDataCollector;
import com.logtracking.lib.api.settings.EmailLogSendingSettings;
import com.logtracking.lib.api.settings.LogSettings;
import com.logtracking.lib.api.settings.LogSettings.LogFileFormat;
import android.app.Application;

public class TestApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

        /*
         * Creating pre-configured log settings.
         */
		LogSettings settings = LogSettings.newDebugSettings(this);

		settings.setLogFileFormat(LogFileFormat.DEFAULT);
        settings.addTagToFilter(TestTags.ERROR_TAG,true);
        settings.addTagToFilter(TestTags.VERBOSE_TAG,true);
        settings.setLevelFilter(Log.DEBUG);
        settings.setLogFileRotationTime(30 * 1000);
		settings.setNotifyAboutReportSendResult(true);
        settings.addAttachedFileToReport("/data/anr/traces.txt");

        /*
         *  Configuring sending settings.
         *
         *  example.email.@gmail.com - email account special for library , that will be used to send emails with bug reports.
         *  pass - password to this account.
         *  developer.email@gmail.com - one of bug reports mail recipient.
         */
        EmailLogSendingSettings emailLogSendingSettings = new EmailLogSendingSettings(  "example.email.@gmail.com" ,
                                                                                        "pass",
                                                                                        "developer.email@gmail.com");
        emailLogSendingSettings.addEmailRecipient("another.recipient@email.com");
        settings.setSendingSettings(emailLogSendingSettings);

        /*
         * Creating pre-configuring meta-data set, that will be saved in bug report.
         */
        MetaDataCollector metaDataCollector = new MetaDataCollector(this);
        metaDataCollector.put("meta-key","meta-value");

        LogContext logContext = new LogContext.LogContextBuilder(this).
                                                setSettings(settings).
                                                setMetaDataCollector(metaDataCollector).
                                                setOnCrashHandledListener(CrashHandlingAction.RELAUNCH_APP_ACTION).
                                                build();

		Log.init(logContext);
	}

}
