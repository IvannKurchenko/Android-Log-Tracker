package com.logtracking.lib.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.logtracking.lib.api.config.LogConfiguration;
import com.logtracking.lib.internal.format.LogFileFormatter;
import com.logtracking.lib.internal.format.LogFileFormatterFactory;

@SuppressLint("NewApi")
public abstract class BaseLogTask extends AsyncTask <Void,Void,File> {
	
	protected final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	protected static final String TEMP_FILE_PREFIX = "_temp_";
	protected static final String FILE_CREATION_TIME = "log_file_creation_time";
	protected static final String TEMP_FILE_NAME = "log_temp_file_name";
	protected static final String CRASH_REPORT_MESSAGE = "Crash report";

	protected LogConfiguration mLogConfiguration;
    protected Context mApplicationContext;

	protected RandomAccessFile mRandomAccessFile;
	protected LogFileFormatter mFileFormatter;
	
	protected File mLogFile;
	
	protected String mReportMessage;
	protected LogFileManager.ReportPrepareListener onReportPrepareListener;

    protected LogPreferences mPreferences;

	protected long mFileCreationTime;
	
	protected BaseLogTask(LogContext logContext) {
		super();
		mLogConfiguration = logContext.getLogConfiguration();
        mApplicationContext = logContext.getApplicationContext();
		mFileFormatter = LogFileFormatterFactory.getFormatter(logContext);
        mPreferences = LogPreferences.getInstance(logContext.getApplicationContext());
	}
	
	protected void setReportPrepareListener(LogFileManager.ReportPrepareListener listener){
		onReportPrepareListener = listener;
	}
	
	protected void setReportMessage(String reportMessage){
		mReportMessage = reportMessage;
	}
	
	protected void setLogFile(File logFile){
		mLogFile = logFile;
	}
	
	@Override
	protected void onPostExecute(File resultFile) {
		super.onPostExecute(resultFile);
		if (onReportPrepareListener != null){
			if (resultFile != null){
                IssueReport report = new IssueReport(resultFile,mReportMessage);
				onReportPrepareListener.onReportPrepared(report);
			} else {
				onReportPrepareListener.onReportPreparationFail();
			}
		}
	}
	
	/*Method provide possibility to work two or more AsyncTask's in parallel on Android v3.0 and higher.*/
	protected void startProcess(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(10);
			executeOnExecutor(new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, workQueue));
		} else {
			execute();
		}
	}
	
	protected int getWriteLineLength(String line){
		return TextUtils.isEmpty(line) ? 0 :line.length() + 1; 
	}
	
	protected boolean isLogFileExist(String logFile){
		if (TextUtils.isEmpty(logFile))
			return false;
		File file = new File(logFile);
		return file.exists();
	}
	
	protected void createNewLogFile() throws IOException{
		
		File parentDirectory = new File(mLogFile.getParent());
		if(!parentDirectory.exists()){
			parentDirectory.mkdirs();
		}
		
		if(!mLogFile.exists()){
			mLogFile.createNewFile();
			mPreferences.saveLong(FILE_CREATION_TIME, System.currentTimeMillis());
			mFileCreationTime = mPreferences.getLong(FILE_CREATION_TIME, 0);
		}
		
		mRandomAccessFile = new RandomAccessFile(mLogFile,"rw");
		
		if (isLogFileEmpty()){
			writeLineToFile(mFileFormatter.getDocumentOpenTag());
			writeReportMessage();
			writeMetaData();
			writeLineToFile(mFileFormatter.getLoggingOpenTag());
			writeLineToFile(mFileFormatter.getLoggingCloseTag());
			writeLineToFile(mFileFormatter.getDocumentCloseTag());
		}
	}
	
	private boolean isLogFileEmpty() throws IOException{
		FileReader reader = new FileReader(mLogFile);
		boolean logFileIsEmpty = reader.read() == -1;
		reader.close();
		return logFileIsEmpty;
	}

	private void writeReportMessage() throws IOException{
		if (!TextUtils.isEmpty(mReportMessage)){
			writeLineToFile(mFileFormatter.formatMessage(mReportMessage));
		}
	}

    private void writeMetaData() throws IOException {
        writeLineToFile(mFileFormatter.getMetaDataOpenTag());
        writeLineToFile(mFileFormatter.formatMetaData(mLogConfiguration.getMetaData()));
        writeLineToFile(mFileFormatter.getMetaDataCloseTag());
    }

    protected void seekFilePointerBeforeCloseTags() throws IOException{
		long closeTagsLength = getWriteLineLength(mFileFormatter.getLoggingCloseTag()) +
				   getWriteLineLength(mFileFormatter.getDocumentCloseTag());

		long posBeforeCloseTag = mRandomAccessFile.length() - closeTagsLength;
		mRandomAccessFile.seek(posBeforeCloseTag);	
	}
	
	protected void writeClosingTags() throws IOException{
		writeLineToFile(mFileFormatter.getLoggingCloseTag());
		writeLineToFile(mFileFormatter.getDocumentCloseTag());
	}
	
	protected void writeLineToFile(String str) throws IOException{
		if (!TextUtils.isEmpty(str)){
			mRandomAccessFile.writeBytes(str);
			mRandomAccessFile.writeBytes(LINE_SEPARATOR);
		}
	}
	
	protected void closeAccessToFile(){
		if (mRandomAccessFile != null){
			try {
				mRandomAccessFile.close();
			} catch (IOException closeExc) {
				closeExc.printStackTrace();
			}
		}
	}
	
	protected File prepareReportArchive() throws IOException{
		String logFileName = mLogFile.getAbsolutePath();
		String archiveName = logFileName.substring(0,logFileName.length()-5);

        List<File> reportFiles = new ArrayList<File>();
        reportFiles.add(mLogFile);
		for(String attachFile : mLogConfiguration.getAttachedFilesToReport()){
            reportFiles.add(new File(attachFile));
		}

        ZipArchiveHelper archive = new ZipArchiveHelper(archiveName);
        archive.packFiles(reportFiles);

		return archive.getArchiveFile();
	}
}