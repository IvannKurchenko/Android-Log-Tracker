package com.logtracking.lib.api;

import android.app.Application;

/**
 *	Class for using default log settings.
 */
public class LogApplication  extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
        LogContext.LogContextBuilder builder = new LogContext.LogContextBuilder(this);
		Log.init(builder.build());
	}
}
