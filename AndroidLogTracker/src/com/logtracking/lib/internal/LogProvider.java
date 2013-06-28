package com.logtracking.lib.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class LogProvider {

	protected static final String VERBOSE = "V";
	protected static final String DEBUG = "D";
	protected static final String INFO = "I";
	protected static final String WARNING = "V";
	protected static final String ERROR = "V";
	protected static final String FATAL = "F";
	protected static final String SILENT = "S";
	
	protected static final String LOGCAT = "logcat";
	protected static final String CLEAR_ENTIRE_LOGS = " -c";
	protected static final String DUMP_TO_THE_SCREEN = " -d ";
	protected static final String WRITE_TO_FILE = " -f ";
	protected static final String SIZE_OF_LOG_BUFFER = " -g ";
	protected static final String SET_DEFAULT_FILTER = " -s ";
	protected static final String FORMAT = " -v ";
	
	/**
	 * Display all metadata fields and separate messages with blank lines.
	 */
	protected static final String LONG_FORMAT = "long";
	
	/**
	 * Display the date, invocation time, priority/tag, and PID of the process issuing the message.
	 */
	protected static final String TIME_FORMAT = "time";
	
	/**
	 * Display priority/tag and PID of the process issuing the message (the default format)
	 */
	protected static final String BRIEF_FORMAT = "brief";
	
	/**
	 *  Display PID only.
	 */
	protected static final String PROCESS_FORMAT = "process";
	
	/**
	 *  Display the priority/tag only.
	 */
	protected static final String TAG_FORMAT = "tag";
	
	/**
	 * Display the raw log message, with no other metadata fields.
	 */
	protected static final String RAW_FORMAT = "raw";
	
	/**
	 * Display the date, invocation time, priority, tag, and the PID and TID of the thread issuing the message.
	 */
	protected static final String THREADTIME_FORMAT = "threadtime";
	
	protected static BufferedReader executeLogcat(String... arguments) throws IOException{
        StringBuilder commandLine = new StringBuilder(LOGCAT);
        if (null != arguments){
        	for (String argument : arguments)
        		commandLine.append(argument);
        }
        
        Process process = Runtime.getRuntime().exec(commandLine.toString());
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
	}
}
