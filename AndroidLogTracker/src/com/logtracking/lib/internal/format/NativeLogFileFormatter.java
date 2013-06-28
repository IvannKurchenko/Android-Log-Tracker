package com.logtracking.lib.internal.format;

import java.util.Map;
import java.util.Map.Entry;

import com.logtracking.lib.internal.LogModel;

import static com.logtracking.lib.internal.format.Tags.*;

public class NativeLogFileFormatter implements LogFileFormatter {

	private static final String TAB = "	";
	@Override
	public String getFileExtension() {
		return ".log";
	}
	
	@Override
	public String formatMetaData(Map<String, String> metaData) {
		StringBuilder builder = new StringBuilder();
		for (Entry<String,String> metaDataEntry : metaData.entrySet()){
			builder.append(TAB);
			builder.append(metaDataEntry.getKey());
			builder.append("=");
			builder.append(metaDataEntry.getValue());
			builder.append(";");
			builder.append(LINE_SEPARATOR);
		}
		builder.deleteCharAt(builder.length()-1);	
		return builder.toString();
	}

	@Override
	public String formatLogRecord(LogModel model) {
		return model.getFullLogRecord();
	}

	@Override
	public String getDocumentOpenTag() {
		return "";
	}

	@Override
	public String getMetaDataOpenTag() {
		return META_DATA_TAG + ":";
	}

	@Override
	public String getLoggingOpenTag() {
		return LOGGING_TAG + ":";
	}

	@Override
	public String getDocumentCloseTag() {
		return "";
	}

	@Override
	public String getMetaDataCloseTag() {
		return ":"+META_DATA_TAG;
	}

	@Override
	public String getLoggingCloseTag() {
		return ":"+LOGGING_TAG;
	}

	@Override
	public String formatMessage(String message) {
		return REPORT_MESSAGE_KEY +" : " + LINE_SEPARATOR + TAB + message + LINE_SEPARATOR + " : " + REPORT_MESSAGE_KEY;
	}

}
