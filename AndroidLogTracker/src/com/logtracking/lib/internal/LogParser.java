package com.logtracking.lib.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;


class LogParser{

	private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	
	private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile(
			"^(\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\.\\d+)\\s*(\\d+)\\s*(\\d+)\\s([VDIWEAF])\\s(.*?):\\s+(.*)$");

	private static final int RECORD_POS_DATE = 1;
	private static final int RECORD_POS_PID = 2;
	private static final int RECORD_POS_PRIORITY = 4;
	private static final int RECORD_POS_TAG = 5;
    private static final int RECORD_POS_MESSAGE = 6;
	private static final int RECORD_POS_TID = 3;
	
	protected static Calendar parseDate(String dateString) throws ParseException{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(LOG_DATE_FORMAT.parse(dateString));
		return calendar;
	}
	
	protected LogModel parseLogRecord(String line) throws ParseException {
		Scanner mScanner = new Scanner(line);
		
		if(mScanner.findWithinHorizon(LOG_ENTRY_PATTERN, 0) == null)
            throw new ParseException("Unable to parse: " + line, 0);
        
        MatchResult match = mScanner.match();
        
        LogModel model = new LogModel();
        model.setFormattedDate(match.group(RECORD_POS_DATE));
        model.setPid(Integer.parseInt(match.group(RECORD_POS_PID).replaceAll("\\s","")));
        model.setTid(Integer.parseInt(match.group(RECORD_POS_TID).replaceAll("\\s", "")));
        model.setLevelSymbol(match.group(RECORD_POS_PRIORITY).charAt(0));
        model.setTag(match.group(RECORD_POS_TAG));
        model.setMessage(match.group(RECORD_POS_MESSAGE));
        
        if(model.getMessage().endsWith("\r\n")){
        	model.setMessage(model.getMessage().substring(0, model.getMessage().length() - 2));
        } else if(model.getMessage().endsWith("\n")) {
        	model.setMessage(model.getMessage().substring(0, model.getMessage().length() - 1));
        }
        
        model.setFullLogRecord(line);
        
        return model;
	}

}
