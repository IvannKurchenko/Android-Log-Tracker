package com.logtracking.lib.internal;


/**
 * Internal class, do not use directly.
 */
public class StateHolder {
	
	private static LogFileManager sLogFileManager;
	
	private static IssueReporter sIssueReporter;
	
	public static void init(LogFileManager logFileManager, IssueReporter issueReporter){
		if (sLogFileManager == null && sIssueReporter == null){
			sLogFileManager = logFileManager;
			sIssueReporter = issueReporter;
		}
	}

	protected static LogFileManager getLogFileManager(){
		return sLogFileManager;
	}
	protected static IssueReporter getIssueReporter(){
		return sIssueReporter;
	}
}
