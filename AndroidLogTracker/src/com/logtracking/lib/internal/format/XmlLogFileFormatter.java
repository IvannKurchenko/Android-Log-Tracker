package com.logtracking.lib.internal.format;

import java.util.Map;
import java.util.Map.Entry;

import com.logtracking.lib.internal.LogModel;
import org.apache.xerces.util.XMLChar;

import static com.logtracking.lib.internal.format.Tags.*;

public class XmlLogFileFormatter implements LogFileFormatter {

    private static final String DOCUMENT_OPEN_TAG = "<?xml version="+ '"' + "1.0"+'"' + " encoding=" + '"'
                                                    + "UTF-8" + '"' +"?> " + "\n" + "<" + REPORT_TAG + ">";

    private static final String DOCUMENT_CLOSE_TAG =  "</" + REPORT_TAG + ">";

    private static final String META_DATA_OPEN_TAG = "<" + META_DATA_TAG + ">";
    private static final String META_DATA_CLOSE_TAG = "</" + META_DATA_TAG + ">";

    private static final String LOGGING_OPEN_TAG = "<" + LOGGING_TAG + ">";
    private static final String LOGGING_CLOSE_TAG = "</" + LOGGING_TAG + ">";


	private StringBuilder mXmlDocBuilder = new StringBuilder();
	
	@Override
	public String getFileExtension() {
		return ".xml";
	}

	@Override
	public String formatMetaData(Map<String, String> metaData) {
		StringBuilder metaBuilder = new StringBuilder();
		for (Entry<String,String> meta : metaData.entrySet()){
			metaBuilder.append("<");
			metaBuilder.append(META_DATA_TAG);
			addAttribute(metaBuilder,meta.getKey() ,  removeControlCharacters(meta.getValue()) );
			metaBuilder.append("/>\n");
		}
        /*Removing last unnecessary line-separator*/
        metaBuilder.deleteCharAt(metaBuilder.length()-1);
		return metaBuilder.toString();
	}

	@Override
	public String formatLogRecord(LogModel model) {
		clearBuilder();

		mXmlDocBuilder.append("<");
		mXmlDocBuilder.append(RECORD_KEY);
		addAttribute(DATE_KEY , model.getFormattedDate());
		addAttribute(LEVEL_KEY, Character.toString(model.getLevelSymbol()));
		addAttribute(PID_KEY, Integer.toString(model.getPid()));
		addAttribute(TID_KEY, Long.toString(model.getTid()));
		addAttribute(PACKAGE_NAME_KEY, model.getPackageName());
		addAttribute(TAG_KEY, removeControlCharacters(model.getTag()));
		addAttribute(MESSAGE_KEY, removeControlCharacters(model.getMessage()));
		mXmlDocBuilder.append("/>");
		
		return mXmlDocBuilder.toString();
	}

	@Override
	public String getDocumentOpenTag() {
		return DOCUMENT_OPEN_TAG;
	}

	@Override
	public String getMetaDataOpenTag() {
		return META_DATA_OPEN_TAG;
	}

	@Override
	public String getLoggingOpenTag() {
		return LOGGING_OPEN_TAG;
	}

	@Override
	public String getDocumentCloseTag() {
		return DOCUMENT_CLOSE_TAG;
	}

	@Override
	public String getMetaDataCloseTag() {
		return META_DATA_CLOSE_TAG;
	}

	@Override
	public String getLoggingCloseTag() {
		return LOGGING_CLOSE_TAG;
	}

	@Override
	public String formatMessage(String message) {
		return "<" + REPORT_MESSAGE_KEY + " " + MESSAGE_KEY +" = '" + removeControlCharacters(message) + "' />";
	}

    private void clearBuilder(){
        int length = mXmlDocBuilder.length();
        mXmlDocBuilder.delete(0, length);
    }

    private void addAttribute(String key , String value){
        addAttribute(mXmlDocBuilder,key,value);
    }

    private void addAttribute(StringBuilder builder , String key , String value){
        builder.append(" ");
        builder.append(key);
        builder.append("=");

        builder.append("'");
        builder.append(value);
        builder.append("'");
    }

    private String removeControlCharacters(String sourceString){
        if (sourceString == null)
            return "";

        StringBuilder builder = new StringBuilder();
        for (int i=0;i<sourceString.length();i++){
            char character = sourceString.charAt(i);
            if(XMLChar.isValid(character) && character != '<' && character != '>' && character != '/'
                    && character != "'".charAt(0)){

                builder.append(character);
            }
        }
        return  builder.toString();
    }
}
