package com.logtracking.lib.internal.format;

import java.util.Map;

import com.logtracking.lib.internal.LogModel;

public interface LogFileFormatter {
	
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	String getFileExtension();
	
	String getDocumentOpenTag();
	
	String formatMessage(String message);
	
	String getMetaDataOpenTag();
	
	String getMetaDataCloseTag(); 
	
	String formatMetaData(Map<String,String> metaData);
	
	String getLoggingOpenTag();
	
	String getLoggingCloseTag(); 
	
	String formatLogRecord(LogModel model);
	
	String getDocumentCloseTag();
}
