package com.logtracking.lib.internal;

import android.content.Context;
import com.logtracking.lib.api.config.*;


public final class LogContext {

    private final LogConfiguration mConfiguration;
    private final Context mApplicationContext;

    public LogContext(LogConfiguration configuration, Context appContext){
        mConfiguration = configuration;
        mApplicationContext = appContext;
    }

    public Context getApplicationContext(){
        return mApplicationContext;
    }


    public LogConfiguration getLogConfiguration(){
        return mConfiguration;
    }

}
