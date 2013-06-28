package com.logtracking.lib.internal.format;

import com.logtracking.lib.api.settings.LogSettings;

public class LogFileFormatterFactory {

	public static LogFileFormatter getFormatter(LogSettings settings){
		switch(settings.getLogFileFormat()){
			case DEFAULT:
				return new NativeLogFileFormatter();
				
			case XML:
				return new XmlLogFileFormatter();
				
			case JSON:
				return new JsonLogFileFormatter();
				
			default:
				return new NativeLogFileFormatter();
		}
	}
}
