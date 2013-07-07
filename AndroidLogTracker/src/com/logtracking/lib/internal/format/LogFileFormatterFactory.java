package com.logtracking.lib.internal.format;


import com.logtracking.lib.api.config.LogConfiguration;

public class LogFileFormatterFactory {

	public static LogFileFormatter getFormatter(LogConfiguration configuration){
		switch(configuration.getLogFileFormat()){
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
