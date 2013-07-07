package com.logtracking.lib.api;

import android.app.Application;
import com.logtracking.lib.api.config.LogConfiguration;

/**
 *	Class for using default log config.
 */
public class LogApplication  extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
        LogConfiguration logConfiguration = LogConfiguration.LogConfigurationBuilder.newDebugConfiguration(this).build();
        Log.init(logConfiguration,this);
	}
}
