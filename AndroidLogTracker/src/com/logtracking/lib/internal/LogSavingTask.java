package com.logtracking.lib.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.logtracking.lib.api.Log;
import com.logtracking.lib.api.config.LogConfiguration;

import static com.logtracking.lib.internal.LogProvider.*;

class LogSavingTask extends BaseLogTask {

	private static final int MAX_BUFFER_SIZE = 100 * 1024;
		
	private List<LogModel> mPrintedCrashStack;
	private StringBuffer mBuffer;
	private LogParser mLogParser;
	private LogFilter mLogFilter;
	
	private volatile boolean mCanWriteInFile;
	private boolean  mSaveDump;
	
	public LogSavingTask(LogContext logContext) {
		super(logContext);
		mLogParser = new LogParser();
		mBuffer = new StringBuffer();
		mLogFile = new File(mLogConfiguration.getLogFileName() + mFileFormatter.getFileExtension());
		mLogFilter = LogFilter.getInstance();
		mCanWriteInFile = true;
	}
	
	public void setSaveDump(boolean saveDump){
		mSaveDump = saveDump;
	}
	
	public void setCaWriteInFile(boolean canWriteInFile){
		mCanWriteInFile = canWriteInFile;
	}
	
	public void setCrashStack(List<LogModel> crashStack){
		mPrintedCrashStack = crashStack;
	}
	
	public void flush(){
		try {
			writeBufferToFile();
			FileDescriptor descriptor = mRandomAccessFile.getFD();
			descriptor.sync();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			if (!mSaveDump){
				LogProvider.executeLogcat(LogProvider.CLEAR_ENTIRE_LOGS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected File doInBackground(Void... arg) {
		try{
			
			createNewLogFile();
			
        	String shellArguments = prepareShellArguments();
            BufferedReader bufferedReader = LogProvider.executeLogcat(shellArguments);
            
            String line;
            
            while ( (line = bufferedReader.readLine()) != null ){ 
            	
            		if (mBuffer.length() >= MAX_BUFFER_SIZE){
            			
            			if (mCanWriteInFile){
                			writeBufferToFile();
                			if (needRotateLogFile()){
                				rotateLogFile();
                			}
            			} else {
                            //TODO : change this part : buffer should be save in temp file,not just cleared!!!
            				clearBuffer();
            			}
            			
            		} else {
            			writeLogRecordToBuffer(line);
            		}
            } 
            
            writeBufferToFile();
            writeCrashToBuffer();
            
            return mSaveDump ? packLogDumpInArchive() : mLogFile;
			
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
        	closeAccessToFile();
        }
	}
	
	@Override
	protected void onCancelled() {
		try {
			writeBufferToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeAccessToFile();
		super.onCancelled();
	}
	
	
	private String prepareShellArguments(){
        StringBuilder arguments = new StringBuilder();

        if(mSaveDump) {
            arguments.append(DUMP_TO_THE_SCREEN);
        }

        arguments.append(FORMAT);
        arguments.append(THREADTIME_FORMAT);

        if(mLogConfiguration.getTagFilter().size()>0) {

           arguments.append(SET_DEFAULT_FILTER);

           char level = LogFilter.getLevelSymbolByCode(mLogConfiguration.getLevelFilter());

           StringBuilder filter = new StringBuilder();
           for(String tag : mLogConfiguration.getTagFilter()){
                filter.append(" ");
                filter.append(tag);
                filter.append(":");
                filter.append(level);
           }
           arguments.append(filter);

        } else if (mLogConfiguration.getLevelFilter() > Log.VERBOSE){
            arguments.append(" *:");
            arguments.append(LogFilter.getLevelSymbolByCode(mLogConfiguration.getLevelFilter()));
        }

    	return arguments.toString();
	}
	
	private boolean needRotateLogFile(){
		LogConfiguration.LogFileRotationType rotationType = mLogConfiguration.getLogFileRotationType();
		switch (rotationType) {
		
			case ROTATION_BY_SIZE:
				return mLogFile.length() >= mLogConfiguration.getLogFileRotationSize();
			
			case ROTATION_BY_TIME:
				return System.currentTimeMillis() - mFileCreationTime >= mLogConfiguration.getLogFileRotationTime();
				
			case NONE:
				return false;
				
			default:
				return false;
		}
		
	}
	
	private void rotateLogFile() throws IOException{
		String previousTempFileName = mPreferences.getString(TEMP_FILE_NAME, null);
		if (previousTempFileName != null){
			File previousTempFile = new File(previousTempFileName);
			previousTempFile.delete();
		}
		
		String tempFileNamePrefix = TEMP_FILE_PREFIX + System.currentTimeMillis();
		String tempLogFilePath = mLogConfiguration.getLogFileName() + tempFileNamePrefix + mFileFormatter.getFileExtension();
		mLogFile.renameTo(new File(tempLogFilePath));

        mPreferences.saveString(TEMP_FILE_NAME, tempLogFilePath);

		closeAccessToFile();
		createNewLogFile();
	}
	
	private void writeBufferToFile() throws IOException{
		if (!mLogFile.exists()){
			createNewLogFile();
		}
		
		seekFilePointerBeforeCloseTags();
		mRandomAccessFile.writeBytes(mBuffer.toString());
		writeClosingTags();
		clearBuffer();
	}
	
	private void clearBuffer(){
		mBuffer.delete(0, mBuffer.length());
	}
	
	private void writeCrashToBuffer(){
		if (mPrintedCrashStack == null)
			return;
		for (LogModel record : mPrintedCrashStack){
			appendRecordToBuffer(record);
		}
	}
	
	private void appendRecordToBuffer(LogModel record){
		mBuffer.append(mFileFormatter.formatLogRecord(record));
		mBuffer.append(LINE_SEPARATOR);
	}
	
	private void writeLogRecordToBuffer(String line) throws IOException{
		try {
			LogModel record = mLogParser.parseLogRecord(line);
			record.setPackageName(mLogFilter.packageNameByPid(record.getPid()));
			boolean needToFilter = mLogFilter.filterAvailable();
			if (!needToFilter || mLogFilter.passFilterRecord(record)) {
					appendRecordToBuffer(record);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private File packLogDumpInArchive() throws IOException{
		File archiveFile = prepareReportArchive();
		mLogFile.delete();
		return archiveFile;
	}
}
