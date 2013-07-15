package com.logtracking.lib.internal.format;

import com.logtracking.lib.internal.LogContext;

public class LogFileFormatterFactory {

	public static LogFileFormatter getFormatter(LogContext logContext){
		switch(logContext.getLogConfiguration().getLogFileFormat()){
			case DEFAULT:
				return new NativeLogFileFormatter();
				
			case XML:
				return new XmlLogFileFormatter();
				
			case JSON:
				return new JsonLogFileFormatter();

            case HTML:
                return new HtmlLogFileFormatter(logContext.getApplicationContext());
				
			default:
				return new NativeLogFileFormatter();
		}
	}
}
