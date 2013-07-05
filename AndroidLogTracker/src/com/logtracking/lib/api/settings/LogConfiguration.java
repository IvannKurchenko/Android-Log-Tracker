package com.logtracking.lib.api.settings;

import android.content.Context;
import android.text.TextUtils;
import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.MetaDataCollector;

import java.io.File;
import java.util.*;

import static com.logtracking.lib.api.Log.*;
import static com.logtracking.lib.api.settings.NonModifiableCollections.newNonModifiableList;

/**
 * Class representing global configuration of logging, creating and formatting issue reports,strategies of crash handling, etc.
 * Object of {@link LogConfiguration} is immutable and it
 * cannot be instantiated directly - use {@link LogConfigurationBuilder} to create and configure of object of configuration.
 */
public final class LogConfiguration {

    /**
     * Class helps to build object of {@link LogConfiguration} with custom parameters.
     * Cannot be instantiated directly - use {@link #newDebugConfiguration} or {@link #newProductionConfiguration} method
     * to instantiate pre-configured {@link LogConfigurationBuilder}.
     */
    public static final class LogConfigurationBuilder {

        private static final String FILE_NAME_SEPARATOR = System.getProperty("file.separator");

        private static final String LOG_FILES_DIRECTORY = "log" + FILE_NAME_SEPARATOR;

        private static final String DEFAULT_LOG_FILE_NAME = "alt";

        private static final long DEFAULT_LOG_FILE_ROTATION_SIZE = 1 * 1000 * 1024;


        /**
         *  Return new instance of {@link LogConfigurationBuilder} class , with recommended debug parameters.
         *  <br>
         *  Parameters description:
         *      <br> - Allowed android notification about report send result;
         *      <br> - All logging available;
         *      <br> - Log saving mode is {@link LogSavingMode#SAVE_ALL_LOG_IN_FILE};
         *      <br> - Log file rotation is {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#ROTATION_BY_SIZE}  with size 1 MB;
         *      <br> - Saving of default meta-data;
         *      <br> - Log messages filtering by package of current application;
         *      <br> - Folder with log files locate in application cache of in application folder if SD card not exist;
         *      <br> - File format is {@link LogFileFormat#DEFAULT};
         *
         * @param context  application context
         * @return         new instance of default settings.
         * @throws NullPointerException if context null.
         */
        public static LogConfigurationBuilder newDebugConfiguration(Context context){
            checkNotNull(context);

            LogConfigurationBuilder debugBuilder = new LogConfigurationBuilder(context);

            debugBuilder.mNotifyAboutReportSendResult = true;

            debugBuilder.mLoggingAvailable = true;
            debugBuilder.mLogSavingMode = LogSavingMode.SAVE_ALL_LOG_IN_FILE;

            debugBuilder.mLevelFilter = android.util.Log.VERBOSE;
            debugBuilder.mFilterOnlyOwnLogRecord = false;

            debugBuilder.mLogFileRotationType = LogFileRotationType.ROTATION_BY_SIZE;
            debugBuilder.mLogFileRotationSize = DEFAULT_LOG_FILE_ROTATION_SIZE;
            debugBuilder.mLogFileRotationTime = -1;

            return debugBuilder;
        }

        /**
         *  Return new instance of {@link LogConfigurationBuilder} class , with recommended production parameters.
         *  <br>
         *  Parameters description:
         *      <br> - Disallowed android notification about report send result;
         *      <br> - Logging not available;
         *      <br> - Log saving mode is {@link LogSavingMode#SAVE_ONLY_IF_NEEDED};
         *      <br> - Log file rotation is {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#NONE};
         *      <br> - Saving of default meta-data;
         *      <br> - Issue report will be send on by WiFi.
         *      <br> - Log messages filtering by package of current application;
         *      <br> - Folder with log files locate in application cache of in application folder if SD card not exist.
         *      <br> - File format is {@link LogFileFormat#DEFAULT};
         *
         * @param context application context
         * @return        new instance of production settings.
         * @throws NullPointerException if context null.
         */
        public static LogConfigurationBuilder newProductionConfiguration(Context context){
            checkNotNull(context);

            LogConfigurationBuilder productionSettings = new LogConfigurationBuilder(context);

            productionSettings.mNotifyAboutReportSendResult = false;

            productionSettings.mLoggingAvailable = false;
            productionSettings.mLogSavingMode = LogSavingMode.SAVE_ONLY_IF_NEEDED;
            productionSettings.mFilterOnlyOwnLogRecord = true;

            productionSettings.setSendReportOnlyByWifi(true);

            productionSettings.mLogFileRotationType = LogFileRotationType.NONE;
            productionSettings.mLogFileRotationSize = -1;
            productionSettings.mLogFileRotationTime = -1;

            return productionSettings;
        }

        private static void checkNotNull(Object obj){
            if(obj == null) {
                throw new NullPointerException();
            }
        }

        private static void checkArgument(boolean illegal,String errorMessage){
            if(illegal) {
                throw new IllegalArgumentException(errorMessage);
            }
        }

        private static void checkEmpty(String stringToCheck,String errorMessage){
            checkArgument(TextUtils.isEmpty(stringToCheck),errorMessage);
        }

        /*
         * General config
         */
        private boolean mLoggingAvailable;
        private boolean mSendReportOnlyByWifi;
        private boolean mNotifyAboutReportSendResult;

        /*
         * Filter config
         */
        private List<String> mApplicationPackageFilter = new ArrayList<String>();
        private List<String> mTagFilter = new ArrayList<String>();
        private int mLevelFilter;
        private boolean mFilterOnlyOwnLogRecord;

        /*
         * File config
         */
        private LogSavingMode mLogSavingMode;
        private String mLogDirectoryName;
        private String mLogFileName;
        private LogFileFormat mLogFileFormat;
        private LogFileRotationType mLogFileRotationType;
        private long mLogFileRotationSize;
        private long mLogFileRotationTime;
        private List<String> mFilesAttachedToReport = new ArrayList<String>();

        private MetaDataCollector mMetaDataCollector;

        /*
         * Sending config
         */
        private LogSendingSettings mSendingSettings;

        private String mApplicationPackage;

        private LogConfigurationBuilder(Context applicationContext){
            File appDir =   applicationContext.getExternalCacheDir() != null ?
                            applicationContext.getExternalCacheDir().getAbsoluteFile() :
                            applicationContext.getFilesDir().getAbsoluteFile();

            mLogDirectoryName = appDir.getPath()  + FILE_NAME_SEPARATOR + LOG_FILES_DIRECTORY;
            mLogFileFormat = LogFileFormat.DEFAULT;
            mApplicationPackage = applicationContext.getPackageName();
            mMetaDataCollector = new MetaDataCollector(applicationContext);
        }

        /**
         * Turn on or off passing  all log messages in log using
         * {@link com.logtracking.lib.api.Log},
         * {@link com.logtracking.lib.api.LogUtils}.
         *
         * @see com.logtracking.lib.api.Log
         * @see com.logtracking.lib.api.LogUtils
         * @param loggingAvailable available passing log messages through {@link com.logtracking.lib.api.Log}.
         * @return current instance.
         */
        public LogConfigurationBuilder setLogginAvaliable(boolean loggingAvailable){
            this.mLoggingAvailable = loggingAvailable;
            return this;
        }

        /**
         * Set true if you want to send report using only WiFi network.
         *
         * @param sendReportOnlyByWifi way of uploading or sending log file.
         * @return current instance.
         */
        public LogConfigurationBuilder setSendReportOnlyByWifi(boolean sendReportOnlyByWifi){
            mSendReportOnlyByWifi = sendReportOnlyByWifi;
            return this;
        }

        /**
         * Allow or disallow android notification about result of report sending.
         *
         * @param notify allow send android notification.
         * @return current instance.
         */
        public LogConfigurationBuilder setNotifyAboutReportSendResult(boolean notify){
            mNotifyAboutReportSendResult = notify;
            return this;
        }

        /**
         * Add package to filter of messages saved in file.
         *
         * @param applicationPackage package that should pass filter.
         * @return current instance.
         * @throws IllegalArgumentException if applicationPackage is empty
         */
        public LogConfigurationBuilder addApplicationPackageToFilter(String applicationPackage){
            checkEmpty(applicationPackage, "Package shouldn't be empty") ;
            mApplicationPackageFilter.add(applicationPackage);
            return this;
        }

        /**
         * Add tag to filter messages , that will be saved in file.
         *
         * @param tag      tag that should pass filter.
         * @param internal is tag relate to some internal part off application.
         * @return current instance.
         * @throws IllegalArgumentException if tag is empty
         */
        public LogConfigurationBuilder addTagToFilter(String tag , boolean internal){
            checkEmpty(tag, "Tag shouldn't be empty") ;
            mTagFilter.add(internal ?  Log.LIBRARY_FILTER_TAG + tag : tag);
            return this;
        }

        /**
         * Set level filter to save log messages in file only with this level or higher.
         *
         * @param levelFilter  level of log messages that should pass filter.
         * @see Log#VERBOSE
         * @see Log#DEBUG
         * @see Log#INFO
         * @see Log#WARN
         * @see Log#ERROR
         * @see Log#ASSERT
         * @return current instance.
         * @throws IllegalArgumentException if level filter is out of bound between
         * {@link com.logtracking.lib.api.Log.VERBOSE} and {@link com.logtracking.lib.api.Log.ERROR}
         */
        public LogConfigurationBuilder setLevelFilter(int levelFilter){
            checkArgument(levelFilter < VERBOSE || levelFilter > ASSERT, "Level filter out of bound");
            mLevelFilter = levelFilter;
            return this;
        }

        /**
         * Set true to filter only messages containing library tag.
         *
         * @param onlyOwnLogRecord  true is you want to save messages from Log class.
         * @return current instance.
         */
        public LogConfigurationBuilder setFilterOnlyOwnRecord(boolean onlyOwnLogRecord){
            mFilterOnlyOwnLogRecord = onlyOwnLogRecord;
            mApplicationPackageFilter = new ArrayList<String>();
            mApplicationPackageFilter.add(mApplicationPackage);
            mTagFilter.clear();
            return this;
        }

        /**
         * Set mode of saving log messages ing log file.
         *
         * @param logSavingMode mode of saving log messages in log file
         * @return current instance.
         * @see LogSavingMode
         * @throws NullPointerException if logSavingMode is null.
         */
        public LogConfigurationBuilder setSaveLogInFileMode(LogSavingMode logSavingMode){
            checkNotNull(logSavingMode);
            mLogSavingMode = logSavingMode;
            return this;
        }

        /**
         * Set full path to directory , where log file will be created.
         *
         * @param logDirectoryName full path to log file.
         * @return current instance.
         * @throws IllegalArgumentException is logDirectoryName is empty.
         */
        public LogConfigurationBuilder setLogDirectoryName(String logDirectoryName){
            checkEmpty(logDirectoryName,"Log directory name couldn't be empty");
            mLogDirectoryName = logDirectoryName;
            mLogFileName = mLogDirectoryName + FILE_NAME_SEPARATOR + DEFAULT_LOG_FILE_NAME;
            return this;
        }

        /**
         * Set format of log file.
         *
         * @param logFileFormat format of log file
         * @return current instance.
         * @throws NullPointerException of logFileFormat is null.
         */
        public LogConfigurationBuilder setLogFileFormat(LogFileFormat logFileFormat){
            checkNotNull(logFileFormat);
            mLogFileFormat = logFileFormat;
            return this;
        }

        /**
         * Set max log file size. If log file size is more, that was set,  file rotation started.
         * Also this method set file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#ROTATION_BY_SIZE}.
         *
         * @param fileRotationSize max size of file in bytes.
         * @return current instance.
         * @throws IllegalArgumentException if fileRotationSize less then 0.
         */
        public LogConfigurationBuilder setLogFileRotationSize(long fileRotationSize){
            checkArgument(fileRotationSize<=0,"Log file rotation size couldn't be less then 0");
            mLogFileRotationSize = fileRotationSize;
            mLogFileRotationType = LogFileRotationType.ROTATION_BY_SIZE;
            mLogFileRotationTime = -1;
            return this;
        }

        /**
         * Set max log file live time in milliseconds. If log file live time is more , that was set, file rotation started.
         * Also this method set file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#ROTATION_BY_TIME}.
         *
         * @param fileRotationTime max time of file living if milliseconds.
         * @return current instance.
         */
        public LogConfigurationBuilder setLogFileRotationTime(long fileRotationTime){
            checkArgument(fileRotationTime<=0,"Log file rotation time couldn't be less then 0");
            mLogFileRotationTime = fileRotationTime;
            mLogFileRotationType = LogFileRotationType.ROTATION_BY_TIME;
            mLogFileRotationSize = -1;
            return this;
        }


        /**
         * Set mode file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#NONE}.
         * During save messages in file, old messages will not be deleted.
         *
         * @return current instance.
         */
        public LogConfigurationBuilder setLogFileNoRotation(){
            this.mLogFileRotationType = LogFileRotationType.NONE;
            this.mLogFileRotationSize = -1;
            this.mLogFileRotationTime = -1;
            return this;
        }


        /**
         * Add full path to file that should be attached to archive with report.
         *
         * @param fileToAttach full path to file that should be attached to report.
         * @return current instance.
         * @throws IllegalArgumentException if fileToAttach is empty.
         */
        public LogConfigurationBuilder addAttachedFileToReport(String fileToAttach){
            checkEmpty(fileToAttach,"Path to file couldn't be empty");
            mFilesAttachedToReport.add(fileToAttach);
            return this;
        }

        /**
         * Set implementation of settings for appropriate service , that will send bug reports.
         *
         * @see LogSendingSettings.SendingService
         * @see EmailLogSendingSettings
         * @param sendingSettings settings of sending service.
         * @return current instance.
         * @throws NullPointerException if sendingSettings is null
         */
        public LogConfigurationBuilder setSendingSettings(LogSendingSettings sendingSettings){
            checkNotNull(sendingSettings);
            mSendingSettings = sendingSettings;
            return this;
        }

        /**
         * Builds instance of {@link LogConfiguration}.
         * @return new  configured instance {@link LogConfiguration}.
         */
        public LogConfiguration build(){
            return new LogConfiguration(this);
        }
    }

    /**
     *Enum representing mode of saving log messages in file.
     */
    public enum LogSavingMode{

        /**
         * In this mode log messages will be saved in log file while application works.
         * Log file will be rotated time-to-time by choosing rotation mode.
         * Issue report will contains all saved messages.
         *
         * @see com.logtracking.lib.api.settings.LogSettings.LogFileRotationType
         */
        SAVE_ALL_LOG_IN_FILE,

        /**
         * In this mode log messages will be saved in log file only before issue report preparation.
         * Log file will be not rotate in this mode.
         * Issue report , that will be send , will contains only dumped messages from system log ring buffer.
         */
        SAVE_ONLY_IF_NEEDED
    }

    /**
     * Enum representing formats of log file.
     */
    public enum LogFileFormat {

        /**
         * Default format , as in logcat.
         */
        DEFAULT,

        /**
         * File in XML format.
         */
        XML,

        /**
         * File in JSON format.
         */
        JSON
    }

    /**
     * Enum representing mode of log file rotation.
     * Rotation is used to avoid possible growing of log file to very large size.
     * When current log file should be rotated in some moment, which depends on mode, current file will be renamed
     * to temporary log file, and new log file will be created, in which log messages will continue to save.
     * In case if in moment  of log file rotation previous temporary log file will be exist it will be removed.
     *
     * Rotation used only in log saving mode {@link LogSavingMode#SAVE_ALL_LOG_IN_FILE}
     */
    public enum LogFileRotationType {

        /**
         * In this mode log file will be rotated if file time of living is more then set.
         *
         * @see LogSettings#setLogFileRotationTime(long)
         */
        ROTATION_BY_SIZE,

        /**
         * In this mode log file will be rotated if file size is more then set.
         *
         * @see LogSettings#setLogFileRotationSize(long)
         */
        ROTATION_BY_TIME,

        /**
         * In this mode log file will not be rotated  in any conditions.
         */
        NONE
    }

    /*
     * General config
     */
    private final boolean mLoggingAvailable;
    private final boolean mSendReportOnlyByWifi;
    private final boolean mNotifyAboutReportSendResult;

    /*
     * Filter config
     */
    private final List<String> mApplicationPackageFilter;
    private final List<String> mTagFilter;
    private final int mLevelFilter;
    private boolean mFilterOnlyOwnLogRecord;

    /*
     * File config
     */
    private final LogSavingMode mLogSavingMode;
    private final String mLogDirectoryName;
    private final String mLogFileName;
    private final LogFileFormat mLogFileFormat;
    private final LogFileRotationType mLogFileRotationType;
    private final long mLogFileRotationSize;
    private final long mLogFileRotationTime;
    private final List<String> mFilesAttachedToReport;

    /*
     * Sending config
     */
    private final LogSendingSettings mSendingSettings;

    private LogConfiguration(LogConfigurationBuilder builder){
        mLoggingAvailable = builder.mLoggingAvailable;
        mSendReportOnlyByWifi = builder.mSendReportOnlyByWifi;
        mNotifyAboutReportSendResult = builder.mNotifyAboutReportSendResult;

        mApplicationPackageFilter = newNonModifiableList(builder.mApplicationPackageFilter);
        mTagFilter =  newNonModifiableList(builder.mTagFilter);
        mLevelFilter = builder.mLevelFilter;
        mFilterOnlyOwnLogRecord = builder.mFilterOnlyOwnLogRecord;

        mLogSavingMode = builder.mLogSavingMode;
        mLogDirectoryName = builder.mLogDirectoryName;
        mLogFileName =  builder.mLogFileName;
        mLogFileFormat = builder.mLogFileFormat;
        mLogFileRotationType = builder.mLogFileRotationType;
        mLogFileRotationSize = builder.mLogFileRotationSize;
        mLogFileRotationTime = builder.mLogFileRotationTime;
        mFilesAttachedToReport = newNonModifiableList(builder.mFilesAttachedToReport);

        mSendingSettings = builder.mSendingSettings;
    }

    public boolean isLoggingAvailable(){
        return mLoggingAvailable;
    }

    public boolean isSendReportOnlyByWifi(){
        return mSendReportOnlyByWifi;
    }

    public boolean isNotifyAboutReportSendResult(){
        return mNotifyAboutReportSendResult;
    }

    public List<String> getApplicationPackageFilter(){
        return mApplicationPackageFilter;
    }

    public List<String> getTagFilter(){
        return mTagFilter;
    }

    public int getLevelFilter(){
        return mLevelFilter;
    }

    public boolean isFilterOnlyOwnRecord(){
        return mFilterOnlyOwnLogRecord;
    }

    public LogSavingMode getLogSavingMode(){
        return mLogSavingMode;
    }

    public String getLogDirectoryName(){
        return mLogDirectoryName;
    }

    public String getLogFileName(){
        return mLogFileName;
    }

    public LogFileFormat getLogFileFormat(){
        return mLogFileFormat;
    }

    public LogFileRotationType getLogFileRotationType(){
        return mLogFileRotationType;
    }

    public long getLogFileRotationSize(){
        return mLogFileRotationSize;
    }

    public long getLogFileRotationTime(){
        return mLogFileRotationTime;
    }

    public List<String> getAttachedFilesToReport(){
        return mFilesAttachedToReport;
    }

    public LogSendingSettings getSendingSettings(){
        return mSendingSettings;
    }
}
