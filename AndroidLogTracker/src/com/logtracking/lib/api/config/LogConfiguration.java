package com.logtracking.lib.api.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.logtracking.lib.api.Log;
import com.logtracking.lib.internal.DefaultReportIssueActivity;
import com.logtracking.lib.internal.ReportIssueActivity;

import java.io.File;
import java.util.*;

import static com.logtracking.lib.api.Log.*;
import static java.util.Collections.*;

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

        private static final String SNAPSHOT_DIRECTORY = "snapshot" + FILE_NAME_SEPARATOR;

        private static final String DEFAULT_LOG_FILE_NAME = "alt";

        private static final long DEFAULT_LOG_FILE_ROTATION_SIZE = 1 * 1000 * 1024;


        /**
         *  Return new instance of {@link LogConfigurationBuilder} class , with recommended debug parameters.
         *  <br>
         *  Parameters description:
         *      <br> - Allowed android notification about report send result;
         *      <br> - All logging available;
         *      <br> - Log saving mode is {@link LogSavingMode#SAVE_ALL_LOG_IN_FILE};
         *      <br> - Log file rotation is {@link com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType#ROTATION_BY_SIZE}  with size 1 MB;
         *      <br> - Collected default meta-data;
         *      <br> - Log messages filtering by package of current application;
         *      <br> - Folder with log files locate in application cache of in application folder if SD card not exist;
         *      <br> - File format is {@link LogFileFormat#DEFAULT};
         *
         * @param context  application context
         * @return         new instance of default config.
         * @throws NullPointerException if context null.
         */
        public static LogConfigurationBuilder newDebugConfiguration(Context context){
            checkNotNull(context);

            LogConfigurationBuilder debugConfigBuilder = new LogConfigurationBuilder(context);

            debugConfigBuilder.mNotifyAboutReportSendResult = true;

            debugConfigBuilder.mMinLoggingLevelAvailable = Log.VERBOSE;
            debugConfigBuilder.mLogSavingMode = LogSavingMode.SAVE_ALL_LOG_IN_FILE;

            debugConfigBuilder.mLevelFilter = android.util.Log.VERBOSE;
            debugConfigBuilder.mFilterOnlyOwnLogRecord = false;

            debugConfigBuilder.mLogFileRotationType = LogFileRotationType.ROTATION_BY_SIZE;
            debugConfigBuilder.mLogFileRotationSize = DEFAULT_LOG_FILE_ROTATION_SIZE;
            debugConfigBuilder.mLogFileRotationTime = -1;

            debugConfigBuilder.mSnapshotSavingEnable = true;

            return debugConfigBuilder;
        }

        /**
         *  Return new instance of {@link LogConfigurationBuilder} class , with recommended production parameters.
         *  <br>
         *  Parameters description:
         *      <br> - Disallowed android notification about report send result;
         *      <br> - Logging not available;
         *      <br> - Log saving mode is {@link LogSavingMode#SAVE_ONLY_IF_NEEDED};
         *      <br> - Log file rotation is {@link com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType#NONE};
         *      <br> - Collected default meta-data;
         *      <br> - Issue report will be send on by WiFi.
         *      <br> - Log messages filtering by package of current application;
         *      <br> - Folder with log files locate in application cache of in application folder if SD card not exist.
         *      <br> - File format is {@link LogFileFormat#DEFAULT};
         *
         * @param context application context
         * @return        new instance of production config.
         * @throws NullPointerException if context null.
         */
        public static LogConfigurationBuilder newProductionConfiguration(Context context){
            checkNotNull(context);

            LogConfigurationBuilder productionConfigBuilder = new LogConfigurationBuilder(context);

            productionConfigBuilder.mNotifyAboutReportSendResult = false;

            productionConfigBuilder.mMinLoggingLevelAvailable = Log.ERROR;
            productionConfigBuilder.mLogSavingMode = LogSavingMode.SAVE_ONLY_IF_NEEDED;
            productionConfigBuilder.mFilterOnlyOwnLogRecord = true;

            productionConfigBuilder.setSendReportOnlyByWifi(true);

            productionConfigBuilder.mLogFileRotationType = LogFileRotationType.NONE;
            productionConfigBuilder.mLogFileRotationSize = -1;
            productionConfigBuilder.mLogFileRotationTime = -1;

            productionConfigBuilder.mSnapshotSavingEnable = false;

            return productionConfigBuilder;
        }

        private static void checkLoggingLevel(int logLevel){
            checkArgument(logLevel < VERBOSE || logLevel > ASSERT, "Level filter out of bound");
        }

        private static void checkNotNull(Object object){
            if(object == null) {
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
        private int mMinLoggingLevelAvailable;
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
        private String mSnapshotDirectoryName;
        private String  mLogFileName;
        private LogFileFormat mLogFileFormat;
        private LogFileRotationType mLogFileRotationType;
        private long mLogFileRotationSize;
        private long mLogFileRotationTime;
        private List<String> mFilesAttachedToReport = new ArrayList<String>();

        /*
         * Sending config
         */
        private LogSendingConfiguration mSendingSettings;

        /*
         * Snapshot config
         */
        private boolean mSnapshotSavingEnable;
        private Bitmap.CompressFormat mSnapshotFormat;
        private int mSnapshotQuality;

        /*
         * Additional
         */
        private Map<String,String> mMetaData;
        private AfterCrashAction mAfterCrashAction;
        private Class mReportDialogClass;

        private String mApplicationPackage;

        private LogConfigurationBuilder(Context applicationContext){
            File appDir =   applicationContext.getExternalCacheDir() != null ?
                            applicationContext.getExternalCacheDir().getAbsoluteFile() :
                            applicationContext.getFilesDir().getAbsoluteFile();

            mLogDirectoryName = appDir.getPath()  + FILE_NAME_SEPARATOR + LOG_FILES_DIRECTORY;
            mSnapshotDirectoryName = appDir.getPath() + FILE_NAME_SEPARATOR + SNAPSHOT_DIRECTORY;
            mFilesAttachedToReport.add(mSnapshotDirectoryName);
            mLogFileName = mLogDirectoryName + FILE_NAME_SEPARATOR + DEFAULT_LOG_FILE_NAME;
            mLogFileFormat = LogFileFormat.DEFAULT;
            mSnapshotFormat = Bitmap.CompressFormat.PNG;
            mSnapshotQuality = 100;
            mApplicationPackage = applicationContext.getPackageName();
            mMetaData = new MetaDataCollector(applicationContext).getData();
            mReportDialogClass = DefaultReportIssueActivity.class;
        }

        /**
         * Set minimum level of log messages that should be send to system log using
         * {@link com.logtracking.lib.api.Log},
         * {@link com.logtracking.lib.api.LogUtils}.
         *
         * @see com.logtracking.lib.api.Log
         * @see com.logtracking.lib.api.LogUtils
         * @param minLoggingLevelAvailable minimum level of log messages that should be send to  system log.
         * @throws IllegalArgumentException if level filter is out of bound between
         * {@link com.logtracking.lib.api.Log#VERBOSE} and {@link com.logtracking.lib.api.Log#ERROR}
         * @return current instance.
         */
        public LogConfigurationBuilder setMinimumLoggingLevelAvaliable(int minLoggingLevelAvailable){
            checkLoggingLevel(minLoggingLevelAvailable);
            mMinLoggingLevelAvailable = minLoggingLevelAvailable;
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
         * {@link com.logtracking.lib.api.Log#VERBOSE} and {@link com.logtracking.lib.api.Log#ERROR}
         */
        public LogConfigurationBuilder setLevelFilter(int levelFilter){
            checkLoggingLevel(levelFilter);
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
         * Also this method set file rotation mode - {@link com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType#ROTATION_BY_SIZE}.
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
         * Also this method set file rotation mode - {@link com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType#ROTATION_BY_TIME}.
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
         * Set mode file rotation mode - {@link com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType#NONE}.
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
         * Set implementation of config for appropriate service , that will send bug reports.
         *
         * @see LogSendingConfiguration.SendingService
         * @see EmailLogSendingConfiguration
         * @param sendingSettings config of sending service.
         * @return current instance.
         * @throws NullPointerException if sendingSettings is null
         */
        public LogConfigurationBuilder setSendingSettings(LogSendingConfiguration sendingSettings){
            checkNotNull(sendingSettings);
            mSendingSettings = sendingSettings;
            return this;
        }

        /**
         * Set enabling or disabling saving of snapshots through using {@link com.logtracking.lib.api.SnapshotSaver}
         * @param snapshotSavingEnable enabling or disabling saving of snapshots.
         * @return current instance.
         * @see com.logtracking.lib.api.SnapshotSaver
         */
        public LogConfigurationBuilder setSnapshotSavingEnable(boolean snapshotSavingEnable){
            mSnapshotSavingEnable = snapshotSavingEnable;
            return this;
        }

        /**
         * Set the format of the snapshot image. Used for bitmap compressing.
         * @param format format of the snapshot image.
         * @throws NullPointerException if format is null.
         * @return current instance.
         * @see com.logtracking.lib.api.SnapshotSaver
         * @see Bitmap#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream)
         */
        public LogConfigurationBuilder setSnapshotFormat(Bitmap.CompressFormat format){
            checkNotNull(format);
            mSnapshotFormat = format;
            return this;
        }

        /**
         * Set the quality of snapshot image. Used for bitmap compressing.
         * @param quality quality of snapshot image.
         * @throws IllegalArgumentException if quality value out of bound 0-100.
         * @return current instance.
         * @see com.logtracking.lib.api.SnapshotSaver
         * @see Bitmap#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream)
         *
         */
        public LogConfigurationBuilder setSnapshotQuality(int quality){
            checkArgument(quality<0 || quality>100,"Quality value should be between 0 and 100!");
            mSnapshotQuality = quality;
            return this;
        }

        /**
         * Put new meta-data in key-value format that will be saved in issue report.
         *
         * @param key meta-data key
         * @param value meta-data value
         * @throws IllegalArgumentException if meta-data key will be empty.
         * @return current instance.
         */
        public LogConfigurationBuilder putMetaData(String key,String value){
            checkEmpty(key,"Meta-data key shouldn't be null");
            mMetaData.put(key, value != null ? value : "");
            return this;
        }

        /**
         * Set type of action that will be performed after showing library crash dialog.
         *
         * @see AfterCrashAction
         * @param action type action
         * @throws NullPointerException if action is null.
         * @return current instance.
         */
        public LogConfigurationBuilder setAfterCrashAction(AfterCrashAction action){
            checkNotNull(action);
            mAfterCrashAction = action;
            return this;
        }

        /**
         * Set class of custom report issue dialog. Set activity will be showed, when application
         * will crashed or to send issue report, after calling {@link com.logtracking.lib.api.ReportIssueDialog#show()}.
         * Custom report dialog should extends {@link com.logtracking.lib.internal.ReportIssueActivity}
         *
         * @param customReportIssueDialog class of custom report issue dialog.
         * @return current instance.
         * @throws NullPointerException if class is null.
         * @see com.logtracking.lib.internal.ReportIssueActivity
         */
        public LogConfigurationBuilder setReportIssueDialog(Class<? extends ReportIssueActivity> customReportIssueDialog){
            checkNotNull(customReportIssueDialog);
            mReportDialogClass = customReportIssueDialog;
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
    public enum LogSavingMode {

        /**
         * In this mode log messages will be saved in log file while application works.
         * Log file will be rotated time-to-time by choosing rotation mode.
         * Issue report will contains all saved messages.
         *
         * @see com.logtracking.lib.api.config.LogConfiguration.LogFileRotationType
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
        JSON,

        /**
         * File in Html format.
         */
        HTML
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
         * @see LogConfiguration.LogConfigurationBuilder#setLogFileRotationTime(long)
         */
        ROTATION_BY_SIZE,

        /**
         * In this mode log file will be rotated if file size is more then set.
         *
         * @see LogConfiguration.LogConfigurationBuilder#setLogFileRotationSize(long)
         */
        ROTATION_BY_TIME,

        /**
         * In this mode log file will not be rotated  in any conditions.
         */
        NONE
    }

    /**
     * Enum representing actions that will be performed after showing dialog about application crash.
     */
    public enum AfterCrashAction {

        /**
         * This action will close application after crash handling.
         */
        CLOSE_APPLICATION,

        /**
         * This action will relaunch application after crash handling.
         */
        RELAUNCH_APPLICATION
    }

    /*
     * General config
     */
    private final int mMinLoggingLevelAvailable;
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
    private final String mSnapshotDirectoryName;
    private final String mLogFileName;
    private final LogFileFormat mLogFileFormat;
    private final LogFileRotationType mLogFileRotationType;
    private final long mLogFileRotationSize;
    private final long mLogFileRotationTime;
    private final List<String> mFilesAttachedToReport;

    /*
     * Sending config
     */
    private final LogSendingConfiguration mSendingSettings;

    /*
     * Snapshot config
     */
    private final boolean mSnapshotSavingEnable;
    private final Bitmap.CompressFormat mSnapshotFormat;
    private final int mSnapshotQuality;

    /*
     * Additional
     */
    private final Map<String,String> mMetaData;
    private final AfterCrashAction mAfterCrashAction;
    private final Class mReportDialogClass;

    private LogConfiguration(LogConfigurationBuilder builder){
        mMinLoggingLevelAvailable = builder.mMinLoggingLevelAvailable;
        mSendReportOnlyByWifi = builder.mSendReportOnlyByWifi;
        mNotifyAboutReportSendResult = builder.mNotifyAboutReportSendResult;

        mApplicationPackageFilter = unmodifiableList(builder.mApplicationPackageFilter);
        mTagFilter =  unmodifiableList(builder.mTagFilter);
        mLevelFilter = builder.mLevelFilter;
        mFilterOnlyOwnLogRecord = builder.mFilterOnlyOwnLogRecord;

        mLogSavingMode = builder.mLogSavingMode;
        mLogDirectoryName = builder.mLogDirectoryName;
        mSnapshotDirectoryName = builder.mSnapshotDirectoryName;
        mLogFileName =  builder.mLogFileName;
        mLogFileFormat = builder.mLogFileFormat;
        mLogFileRotationType = builder.mLogFileRotationType;
        mLogFileRotationSize = builder.mLogFileRotationSize;
        mLogFileRotationTime = builder.mLogFileRotationTime;
        mFilesAttachedToReport = unmodifiableList(builder.mFilesAttachedToReport);

        mSendingSettings = builder.mSendingSettings;

        mSnapshotSavingEnable = builder.mSnapshotSavingEnable;
        mSnapshotFormat = builder.mSnapshotFormat;
        mSnapshotQuality = builder.mSnapshotQuality;

        mMetaData = unmodifiableMap(builder.mMetaData);
        mAfterCrashAction = builder.mAfterCrashAction;
        mReportDialogClass = builder.mReportDialogClass;
    }

    public int getMinimumLoggingLevelAvailable(){
        return mMinLoggingLevelAvailable;
    }

    public boolean isAnyLoggingAvailable(){
        return mMinLoggingLevelAvailable < Log.ASSERT;
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

    public String getSnapshotDirectoryName(){
        return mSnapshotDirectoryName;
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

    public LogSendingConfiguration getSendingSettings(){
        return mSendingSettings;
    }

    public boolean isSnapshotSavingEnable(){
        return mSnapshotSavingEnable;
    }

    public Bitmap.CompressFormat getSnapshotFormat(){
        return mSnapshotFormat;
    }

    public int getSnapshotQuality(){
        return mSnapshotQuality;
    }

    public Map<String,String> getMetaData(){
        return mMetaData;
    }

    public AfterCrashAction getAfterCrashAction(){
        return mAfterCrashAction;
    }

    public Class getReportDialog(){
        return mReportDialogClass;
    }
}
