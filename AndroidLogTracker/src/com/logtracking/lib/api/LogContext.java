package com.logtracking.lib.api;

import android.content.Context;
import com.logtracking.lib.api.settings.LogSettings;

/**
 * Class representing main contextual components required for {@link Log}.
 * Cannot be instantiated directly, use {@link LogContextBuilder} instead.
 *
 * @see LogSettings
 * @see MetaDataCollector
 * @see OnCrashHandledListener
 * @see CrashHandlingAction
 */
public final class LogContext {

    public static class LogContextBuilder{

        private Context mApplicationContext;
        private MetaDataCollector mMetaDataCollector;
        private LogSettings mLogSettings;
        private OnCrashHandledListener mOnCrashListener;

        /**
         * Creates builder with default parameters.
         * @param context application context
         */
        public LogContextBuilder(Context context){
            mApplicationContext = context;
            mMetaDataCollector = new MetaDataCollector(context);
            mLogSettings = LogSettings.newDebugSettings(context);
            mOnCrashListener = CrashHandlingAction.RELAUNCH_APP_ACTION;
        }

        /**
         * Set logging settings.
         * @param settings logging setting
         * @return current instance of LogContextBuilder
         */
        public LogContextBuilder setSettings(LogSettings settings){
            checkNotNull(settings);
            mLogSettings = settings;
            return this;
        }

        /**
         * Set meta-data that will be saved in bug report file
         * @param collector meta-data that will be saved in file
         * @return current instance of LogContextBuilder
         */
        public LogContextBuilder setMetaDataCollector(MetaDataCollector collector){
            checkNotNull(collector);
            mMetaDataCollector = collector;
            return this;
        }

        /**
         * Set listener that will handle callback after handling of crash
         * @param listener  action that will called after handling of crash
         * @return current instance of LogContextBuilder
         * @see CrashHandlingAction
         */
        public LogContextBuilder setOnCrashHandledListener(OnCrashHandledListener listener){
            checkNotNull(listener);
            mOnCrashListener = listener;
            return this;
        }

        /**
         * Creates new instance of {@link LogContext} by previously setted parameters.
         * @return new instance of {@link LogContext}
         */
        public LogContext build(){
            return new LogContext(this);
        }

        private void checkNotNull(Object object){
            if(object == null)
                throw new NullPointerException(object.getClass().getSimpleName() + " cannot be null!");
        }
    }

    private final Context mApplicationContext;
    private final MetaDataCollector mMetaDataCollector;
    private final LogSettings mLogSettings;
    private final OnCrashHandledListener mOnCrashListener;

    private LogContext(LogContextBuilder builder){
        mApplicationContext = builder.mApplicationContext;
        mMetaDataCollector = builder.mMetaDataCollector;
        mLogSettings = builder.mLogSettings;
        mOnCrashListener = builder.mOnCrashListener;
    }

    /**
     * Return current application context.
     * @return application context
     */
    public Context getApplicationContext(){
        return mApplicationContext;
    }

    /**
     * Return current metadata collector.
     * @return current metadata collector.
     */
    public MetaDataCollector getMetaDataCollector(){
        return mMetaDataCollector;
    }

    /**
     * Return current log settings.
     * @return current log settings.
     */
    public LogSettings getLogSettings(){
        return mLogSettings;
    }

    /**
     * Return current OnCrashHandledListener
     * @return current OnCrashHandledListener.
     */
    public OnCrashHandledListener getOnCrashListener(){
        return mOnCrashListener;
    }
}
