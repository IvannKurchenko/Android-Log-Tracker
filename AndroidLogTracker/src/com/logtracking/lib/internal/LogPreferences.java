package com.logtracking.lib.internal;

import android.content.Context;
import android.content.SharedPreferences;

public class LogPreferences {

    protected static final String LIB_PREFERENCE_NAME = "alt_log_preferences";

    private static volatile LogPreferences sInstance;

    protected SharedPreferences mPreferences;
    protected SharedPreferences.Editor mPreferencesEditor;

    private LogPreferences(Context appContext){
        mPreferences = appContext.getSharedPreferences(LIB_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mPreferencesEditor = mPreferences.edit();
    }

    public static LogPreferences getInstance(Context appContext){
        if (sInstance == null){
            synchronized (LogPreferences.class){
                if(sInstance == null){
                    sInstance = new LogPreferences(appContext);
                }
            }
        }
        return sInstance;
    }

    public void saveLong(String key,long value){
        mPreferencesEditor.putLong(key,value);
        mPreferencesEditor.commit();
    }

    public long getLong(String key,long defaultValue){
        return mPreferences.getLong(key,defaultValue);
    }

    public void saveString( String key , String value){
        mPreferencesEditor.putString(key,value);
        mPreferencesEditor.commit();
    }

    public String getString(String key,String defaultValue){
        return mPreferences.getString(key,defaultValue);
    }
}
