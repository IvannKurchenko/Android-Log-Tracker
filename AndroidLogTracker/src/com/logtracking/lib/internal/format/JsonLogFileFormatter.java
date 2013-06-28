package com.logtracking.lib.internal.format;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.logtracking.lib.internal.LogModel;

import static com.logtracking.lib.internal.format.Tags.*;

public class JsonLogFileFormatter implements LogFileFormatter {

	@Override
	public String getFileExtension() {
		return ".json";
	}
	
	@Override
	public String formatMetaData(Map<String, String> metaData) {
		try {

			JSONObject metaDataArray = new JSONObject();
			for (Entry<String,String> metaDataPair : metaData.entrySet()){
				metaDataArray.put(metaDataPair.getKey(), metaDataPair.getValue());
			}
			return new JSONObject().put(META_DATA_TAG, metaDataArray).toString();

		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	public String formatLogRecord(LogModel model) {
		try {

			JSONObject formattedModel = new JSONObject();
			formattedModel.put(DATE_KEY, model.getFormattedDate());
			formattedModel.put(LEVEL_KEY, model.getLevelSymbol());
			formattedModel.put(PID_KEY, model.getPid());
			formattedModel.put(TID_KEY, model.getTid());
			formattedModel.put(PACKAGE_NAME_KEY, model.getPackageName());
			formattedModel.put(TAG_KEY, model.getTag());
			formattedModel.put(MESSAGE_KEY, model.getMessage());

			return new JSONObject().put(RECORD_KEY, formattedModel).toString()  + ",";

		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
		
	}

	@Override
	public String getDocumentOpenTag() {
		return "{";
	}

	@Override
	public String getMetaDataOpenTag() {
		return '"' + META_DATA_TAG + '"' + ":[";
	}

	@Override
	public String getLoggingOpenTag() {
		return '"' + LOGGING_TAG + '"' + ":[";
	}

	@Override
	public String getDocumentCloseTag() {
		return "}";
	}

	@Override
	public String getMetaDataCloseTag() {
		return "] , ";
	}

	@Override
	public String getLoggingCloseTag() {
		return " {} ]";
	}

	@Override
	public String formatMessage(String message) {
        return '"' + REPORT_MESSAGE_KEY + '"' + ":" + '"' + message + '"' + ",";
	}

}
