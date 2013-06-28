package com.logtracking.lib.api.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import com.logtracking.lib.api.Log;

/**
 * Class representing settings of logging , saving and uploading log file. 
 * Cannot be instantiated directly - use {@link #newDebugSettings} or {@link #newProductionSettings} method.
 */
public final class LogSettings {
	
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

	private static final String FILE_NAME_SEPARATOR = System.getProperty("file.separator");
	
	private static final String LOG_FILES_DIRECTORY = "log" + FILE_NAME_SEPARATOR;
	
	private static final String DEFAULT_LOG_FILE_NAME = "alt";
	
	private static final long DEFAULT_LOG_FILE_ROTATION_SIZE = 1 * 1000 * 1024;

	/**
	 * General settings
	 */
	private boolean mLoggingAvailable;
	private boolean mSendReportOnlyByWifi;
	private boolean mNotifyAboutReportSendResult;
	
	/**
	 * File settings
	 */
	private LogSavingMode mLogSavingMode;
	private boolean mSaveInFileMetaData;
	private List<String> mApplicationPackageFilter = new ArrayList<String>();
	private List<String> mTagFilter = new ArrayList<String>();
	private int mLevelFilter;
	private boolean mFilterOnlyOwnLogRecord;
	
	private String mLogDirectoryName;
	private LogFileFormat mLogFileFormat;
	private LogFileRotationType mLogFileRotationType;
	private long mFileRotationSize;
	private long mFileRotationTime;
	private List<String> mFilesAttachedToReport = new ArrayList<String>();

    private LogSendingSettings mSendingSettings;

	private Context mApplicationContext;


	/**
	 * cannot be instantiated directly.
	 * @param context is application context
	 */
	private LogSettings(Context context){
		mApplicationContext = context;

		File appDir = context.getExternalCacheDir() != null ? context.getExternalCacheDir().getAbsoluteFile() :
                                                              context.getFilesDir().getAbsoluteFile();

		mLogDirectoryName = appDir.getPath()  + FILE_NAME_SEPARATOR + LOG_FILES_DIRECTORY;
		mLogFileFormat = LogFileFormat.DEFAULT;
	}
	
	private void setDefaultApplicationPackageFilter(){
		mApplicationPackageFilter = new ArrayList<String>();
		mApplicationPackageFilter.add(mApplicationContext.getPackageName());
	}
	
	/**
	 *  Return new instance of LogSettings class , with recommended debug parameters.
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
	 */
	public static LogSettings newDebugSettings(Context context){
		LogSettings debugSettings = new LogSettings(context);

		debugSettings.mNotifyAboutReportSendResult = true;
		
		debugSettings.mLoggingAvailable = true;
		debugSettings.mLogSavingMode = LogSavingMode.SAVE_ALL_LOG_IN_FILE;
		debugSettings.mSaveInFileMetaData = true;
		
		debugSettings.setDefaultApplicationPackageFilter();
		
		debugSettings.mLevelFilter = android.util.Log.VERBOSE;
		debugSettings.mFilterOnlyOwnLogRecord = false;
		
		debugSettings.mLogFileRotationType = LogFileRotationType.ROTATION_BY_SIZE;
		debugSettings.mFileRotationSize = DEFAULT_LOG_FILE_ROTATION_SIZE;
		debugSettings.mFileRotationTime = -1;
		
		return debugSettings;
	}
	
	/**
	 *  Return new instance of LogSettings class , with recommended production parameters.
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
	 */
	public static LogSettings newProductionSettings(Context context){
		LogSettings productionSettings = new LogSettings(context);
		
		productionSettings.mNotifyAboutReportSendResult = false;
		
		productionSettings.mLoggingAvailable = false;
		productionSettings.mLogSavingMode = LogSavingMode.SAVE_ONLY_IF_NEEDED;
		productionSettings.mSaveInFileMetaData = true;
		productionSettings.mFilterOnlyOwnLogRecord = true;
		
		productionSettings.setSendReportOnlyByWifi(true);
		
		productionSettings.mLogFileRotationType = LogFileRotationType.NONE;
		productionSettings.mFileRotationSize = -1;
		productionSettings.mFileRotationTime = -1;
		
		return productionSettings;
	}


	/**
	 * Turn on or off passing  log messages in log.
     *
	 * @see com.logtracking.lib.api.Log
	 * @see com.logtracking.lib.api.LogUtils
	 */
	public void setLogginAvaliable(boolean loggingAvailable){
		this.mLoggingAvailable = loggingAvailable;
	}
	
	public boolean isLoggingAvailable(){
		return mLoggingAvailable;
	}
	
	/**
	 * Turn on or off saving log messages in log-file.
	 * Log messages will be save in file while application is lived.
	 */
	public void setSaveLogInFileMode(LogSavingMode logSavingMode){
		this.mLogSavingMode = logSavingMode;
	}
	
	public LogSavingMode getLogSavingMode(){
		return mLogSavingMode;
	}
	
	/**
	 * Turn on or off saving of meta-data in log-file.
     *
	 * @see com.logtracking.lib.api.MetaDataCollector
	 */
	public void setSaveMetaDataInFile(boolean saveInFileMetaData){
		this.mSaveInFileMetaData = saveInFileMetaData;
	}
	
	public boolean isSaveMetaDataInFile(){
		return mSaveInFileMetaData;
	}
	
	/**
	 * Add package to filter of messages saved in file.
	 * 
	 * @param applicationPackageFilter - package that should pass filter.
	 */
	public void addApplicationPackageToFilter(String applicationPackageFilter){
		if (applicationPackageFilter	!=null && applicationPackageFilter.length()>0){
			mApplicationPackageFilter.add(applicationPackageFilter);
		}
	}
	
	public List<String> getApplicationPackageFilter(){
		return mApplicationPackageFilter;
	}
	
	/**
	 *Add tag to filter messages , that will be saved in file.
	 *
	 * @param tag      tag that should pass filter.
	 * @param internal is tag relate to some internal part off application.
	 */
	public void addTagToFilter(String tag , boolean internal){
		if (tag!=null && tag.length()>0){
			this.mTagFilter.add(internal ?  Log.LIBRARY_FILTER_TAG + tag : tag);
		}
	}
	
	public List<String> getTagFilter(){
		return mTagFilter;
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
	 * 
	 */
	public void setLevelFilter(int levelFilter){
		if (levelFilter>=Log.VERBOSE && levelFilter<=Log.ASSERT){
			this.mLevelFilter = levelFilter;
		}
	}
	
	public int getLevelFilter(){
		return mLevelFilter;
	}
	
	/**
	 * Set true to save in log file messages containing library tag.
	 * 
	 * @param onlyOwnLogRecord  true is you want to save messages from Log class.
	 */
	public void setSaveOnlyOwnRecord(boolean onlyOwnLogRecord){
		this.mFilterOnlyOwnLogRecord = onlyOwnLogRecord;
		setDefaultApplicationPackageFilter();
		mTagFilter.clear();
	}
	
	public boolean isSaveOnlyOwnRecord(){
		return mFilterOnlyOwnLogRecord;
	}

    /**
     * Remove all possible filters for log.
     */
    public void setNoLogFilter(){
        mFilterOnlyOwnLogRecord = false;
        mApplicationPackageFilter.clear();
        mTagFilter.clear();
        mLevelFilter = android.util.Log.VERBOSE;
    }
	/**
	 * Set full path to directory , where log file will be created.
	 * 
	 * @param logFileName full path to log file.
	 */
	public void setLogDirectoryName(String logFileName){
		if (!TextUtils.isEmpty(logFileName)){
			this.mLogDirectoryName = logFileName;
		}
	}
	
	public String getLogDirectoryName(){
		return mLogDirectoryName;
	}
	
	public String getLogFileName(){
		return mLogDirectoryName + FILE_NAME_SEPARATOR + DEFAULT_LOG_FILE_NAME;
	}
	
	/**
	 * Set format of log file.
	 * 
	 * @param logFileFormat format of log file
	 */
	public void setLogFileFormat(LogFileFormat logFileFormat){
		if (logFileFormat != null) {
			this.mLogFileFormat = logFileFormat;
		}
	}
	
	public LogFileFormat getLogFileFormat(){
		return mLogFileFormat;
	}
	
	/**
	 * Set max log file size. If log file size is more, that was set,  file rotation started.
	 * Also this method set file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#ROTATION_BY_SIZE}.
	 * 
	 * @param fileRotationSize max size of file in bytes.
	 */
	public void setLogFileRotationSize(long fileRotationSize){
		if (fileRotationSize>0){
			this.mFileRotationSize = fileRotationSize;
			this.mLogFileRotationType = LogFileRotationType.ROTATION_BY_SIZE;
			this.mFileRotationTime = -1;
		}
	}
	
	public long getLogFileRotationSize(){
		return mFileRotationSize;
	}
	
	/**
	 * Set max log file live time in milliseconds. If log file live time is more , that was set, file rotation started.
	 * Also this method set file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#ROTATION_BY_TIME}.
	 * 
	 * @param fileRotationTime max time of file living if milliseconds.
	 */
	public void setLogFileRotationTime(long fileRotationTime){
		if (fileRotationTime>0){
			this.mFileRotationTime = fileRotationTime;
			this.mLogFileRotationType = LogFileRotationType.ROTATION_BY_TIME;
			this.mFileRotationSize = -1;
		}
	}
	
	public long getLogFileRotationTime(){
		return mFileRotationTime;
	}
	
	/**
	 * Set mode file rotation mode - {@link com.logtracking.lib.api.settings.LogSettings.LogFileRotationType#NONE}.
	 * During save messages in file, old messages will not be deleted.
	 */
	public void setLogFileNoRotation(){
		this.mLogFileRotationType = LogFileRotationType.NONE;
		this.mFileRotationSize = -1;
		this.mFileRotationTime = -1;
	}
	
	public LogFileRotationType getLogFileRotationType(){
		return mLogFileRotationType;
	}
	
	/**
	 * Add full path to file that should be attached to archive with report.
	 * 
	 * @param fullFilePath full path to file that should be attached to report.
	 */
	public void addAttachedFileToReport(String fullFilePath){
		if (!TextUtils.isEmpty(fullFilePath)){
			mFilesAttachedToReport.add(fullFilePath);
		}
	}
	
	public List<String> getAttachedFilesToReport(){
		return mFilesAttachedToReport;
	}
	
	/**
	 * Set true if you want to send report using only WiFi network.
	 * 
	 * @param sendReportOnlyByWifi way of uploading or sending log file.
	 */
	public void setSendReportOnlyByWifi(boolean sendReportOnlyByWifi){
		mSendReportOnlyByWifi = sendReportOnlyByWifi;
	}
	
	public boolean isSendReportOnlyByWifi(){
		return mSendReportOnlyByWifi;
	}
	
	
	/**
	 * Allow or disallow android notification about result of report sending.
	 * 
	 * @param notify allow send android notification.
	 */
	public void setNotifyAboutReportSendResult(boolean notify){
		mNotifyAboutReportSendResult = notify;
	}
	
	public boolean isNotifyAboutReportSendResult(){
		return mNotifyAboutReportSendResult;
	}

    /**
     * Set implementation of settings for appropriate service , that will send bug reports.
     *
     * @see LogSendingSettings.SendingService
     *
     * @param sendingSettings settings of sending service.
     */
    public void setSendingSettings(LogSendingSettings sendingSettings){
        mSendingSettings = sendingSettings;
    }

    public LogSendingSettings getSendingSettings(){
        return mSendingSettings;
    }

}
