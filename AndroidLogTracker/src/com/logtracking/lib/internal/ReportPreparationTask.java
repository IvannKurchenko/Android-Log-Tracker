package com.logtracking.lib.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.logtracking.lib.api.settings.LogSettings;
import com.logtracking.lib.api.MetaDataCollector;

import android.content.Context;

class ReportPreparationTask extends BaseLogTask{
	
	protected ReportPreparationTask(Context applicationContext , MetaDataCollector metaDataCollector, LogSettings settings) {
		super(applicationContext, metaDataCollector, settings);
	}
		
	private void prepareFullReport() throws IOException{
		createNewLogFile();

		String rotatedFile = mPreferences.getString(TEMP_FILE_NAME, null);
		mergeLogFile(rotatedFile);
		
		String currentLogFile = mSettings.getLogFileName() + mFileFormatter.getFileExtension();
		mergeLogFile(currentLogFile);
	}
	
	private void mergeLogFile(String sourceFile) throws IOException {
		if (!isLogFileExist(sourceFile))
			return;
		
		RandomAccessFile source = new RandomAccessFile(sourceFile, "rw");
		
		String openLoggingTag = mFileFormatter.getLoggingOpenTag();
		String closeLoggingTag = mFileFormatter.getLoggingCloseTag();
		
		boolean loggingStart = false;
		String line = source.readLine();
		
		while (line != null){
			
			if(!loggingStart && line.equals(openLoggingTag)){
				
				seekFilePointerBeforeCloseTags();
				loggingStart = true;
				
			} else if (line.equals(closeLoggingTag)){
				
				writeClosingTags();
				break;
				
			} else if (loggingStart){
				
				writeLineToFile(line);
				
			}
			
			line = source.readLine();
		}
		
		source.close();
	}
	
	@Override
	protected File doInBackground(Void... params) {
		try {
			
			prepareFullReport();
			File archiveFile = prepareReportArchive();
			mLogFile.delete();
			return archiveFile;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
			
		} finally {
        	closeAccessToFile();
        }
	}
	
}
