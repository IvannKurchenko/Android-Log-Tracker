package com.logtracking.lib.internal.upload;

import com.logtracking.lib.api.LogContext;

public class LogFileSenderFactory {

	public static LogReportSender newSender(LogContext logContext){
		switch(logContext.getLogSettings().getSendingSettings().getServiceType()){
				
			case E_MAIL:
				return new EmailLogReportSender(logContext);
				
			default : 
				return null;
		}
	}
}
