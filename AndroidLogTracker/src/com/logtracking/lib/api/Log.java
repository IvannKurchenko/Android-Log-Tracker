package com.logtracking.lib.api;

import com.logtracking.lib.api.settings.LogSettings;
import com.logtracking.lib.api.settings.LogSettings.LogSavingMode;
import com.logtracking.lib.internal.CrashHandler;
import com.logtracking.lib.internal.IssueReporter;
import com.logtracking.lib.internal.LogFileManager;
import com.logtracking.lib.internal.LogFilter;
import com.logtracking.lib.internal.StateHolder;

import android.text.TextUtils;

/**
 * A wrapper class over android.util.Log class with additional functionality.
 * Before use should be initialized using init() method.
 *
 * @see LogContext
 * @see LogUtils
 */

public final class Log {
	
	/**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = android.util.Log.VERBOSE;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = android.util.Log.DEBUG;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = android.util.Log.INFO;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = android.util.Log.WARN;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = android.util.Log.ERROR;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = android.util.Log.ASSERT;
    
    public static final String LIBRARY_FILTER_TAG = "[ALT]";
	
    private static final String[] EMPTY_LOCATION = {"",""};
	
	private static LogSettings sLogSettings;
	
	/**
	 * Cannot be instantiated
	 */
	private Log(){
	}

	/**
	 * Initialize log with given {@link LogContext}.
     *
	 * @param logContext log context
	 */
	public static void init(LogContext logContext){
		if ( sLogSettings == null ){

			sLogSettings = logContext.getLogSettings();

			LogFilter.getInstance().initFilter(logContext);
			
			LogFileManager logFileManager = new LogFileManager(logContext);
			logFileManager.startLogSaving();

            IssueReporter issueReporter = new IssueReporter(logContext,logFileManager);

			Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(logContext, issueReporter));

            ReportIssueDialog.init(issueReporter);

			StateHolder.init(logFileManager, issueReporter);
		}
	}
	

	/**
     * Send a {@link #DEBUG} log message if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static int d(String tag, String msg){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.d(LIBRARY_FILTER_TAG + tag , msg) : -1;
	}
	
	/**
     * Send a {@link #DEBUG} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	
	public static int d(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.d(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}
	
	/**
     * Send an {@link #ERROR} log message if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static int e(String tag, String msg ){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.e(LIBRARY_FILTER_TAG + tag , msg ) : -1;
	}
	
	/**
     * Send a {@link #ERROR} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int e(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.e(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}
	
	/**
     * Send an {@link #INFO} log message if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static int i(String tag, String msg ){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.i(LIBRARY_FILTER_TAG + tag , msg ) : -1;
	}
	
	/**
     * Send a {@link #INFO} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int i(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.i(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}
	
	/**
     * Send a {@link #VERBOSE} log message if log available.
     * 
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	
	public static int v(String tag, String msg ){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.v(LIBRARY_FILTER_TAG + tag , msg ) : -1;
	}
	
	/**
     * Send a {@link #VERBOSE} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int v(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.v(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}

	 /**
     * Send a {@link #WARN} log message if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static int w(String tag, String msg ){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.w(LIBRARY_FILTER_TAG + tag , msg ) : -1;
	}

    /**
     * Send a {@link #WARN} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param tr An exception to log
     */
	public static int w(String tag,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.w(LIBRARY_FILTER_TAG + tag , tr) : -1;
	}
	
	/**
     * Send a {@link #WARN} log message and log the exception if log available.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int w(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.w(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}


    /**
     * Send a log message and log the exception if log available.
     * What a Terrible Failure: Report a condition that should never happen.
     * The error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
	public static int wtf(String tag, String msg ){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.wtf(LIBRARY_FILTER_TAG + tag , msg ) : -1;
	}

    /**
     * Send a log message and log the exception if log available.
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, String)}, with an exception to log.
     * @param tag Used to identify the source of a log message.
     * @param tr An exception to log.
     */
	public static int wtf(String tag,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.wtf(LIBRARY_FILTER_TAG + tag , tr) : -1;
	}

    /**
     * Send a log message and log the exception if log available.
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, Throwable)}, with a message as well.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.  May be null.
     */
	public static int wtf(String tag, String msg ,Throwable tr){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.wtf(LIBRARY_FILTER_TAG + tag , msg , tr) : -1;
	}
	
	/**
     * Low-level logging call.
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return The number of bytes written.
     */
	public static int println(int priority,String tag, String msg){
		return sLogSettings.isLoggingAvailable() ? android.util.Log.println(priority, tag, msg) : -1;
	}
	
	 /**
     * Checks to see whether or not a log for the specified tag is loggable at the specified level
     * or log available .
     * 
     *  The default level of any tag is set to INFO. This means that any level above and including
     *  INFO will be logged. Before you make any calls to a logging method you should check to see
     *  if your tag should be logged. You can change the default level by setting a system property:
     *      'setprop log.tag.&lt;YOUR_LOG_TAG> &lt;LEVEL>'
     *  Where level is either VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, or SUPPRESS. SUPRESS will 
     *  turn off all logging for your tag. You can also create a local.prop file that with the
     *  following in it:
     *      'log.tag.&lt;YOUR_LOG_TAG>=&lt;LEVEL>'
     *  and place that in /data/local.prop.
     *  
     * @param tag The tag to check.
     * @param level The level to check.
     * @return Logging turn on and whether or not that this is allowed to be logged.
     * @throws IllegalArgumentException is thrown if the tag.length() > 23.
     */
	public static boolean isLoggable(String tag , int level){
		return sLogSettings.isLoggingAvailable() && android.util.Log.isLoggable(tag, level);
	}
	
	 /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
	public static String getStackTraceString(Throwable tr){
		return android.util.Log.getStackTraceString(tr);
	}
	
	/**
     * Send a {@link #DEBUG} log message if log available with location of code.
     * Log message location format:
     * d(className,[ methodName : codeLineNumber ] : message)
     * 
     * @param msg The message you would like logged.
     */
	public static int d(String msg) {
		String[] location = getLocation();
        return d(location[0],location[1]+msg);
    }
	
	/**
     * end a {@link #DEBUG} log message and log the exception if log available with location of code.
     * Log message location format:
     * d(className,[ methodName : codeLineNumber ] : message ,tr)
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
	public static int  d(String msg , Throwable tr) {
		String[] location = getLocation();
		return d(location[0],location[1]+msg, tr);
    }
	
	/**
     * Send an {@link #ERROR} log message if log available with location of code.
     * Log message location format:
     * e(className,[ methodName : codeLineNumber ] : message)
     * 
     * @param msg The message you would like logged.
     */
	public static int e(String msg) {
		String[] location = getLocation();
        return e(location[0],location[1]+msg);
    }
	
	/**
     * Send a {@link #ERROR} log message and log the exception if log available with location of code.
     * Log message location format:
     * e(className,[ methodName : codeLineNumber ] : message , tr)
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int e(String msg , Throwable tr) {
		String[] location = getLocation();
        return e(location[0],location[1]+msg , tr);
    }
	
	/**
     * Send an {@link #INFO} log message if log available with location of code.
     * Log message location format:
     * i(className,[ methodName : codeLineNumber ] : message)
     * 
     * @param msg The message you would like logged.
     */
	public static int i(String msg) {
		String[] location = getLocation();
        return i(location[0],location[1]+msg);
    }
	
	/**
     * Send a {@link #INFO} log message and log the exception if log available with location of code.
     * Log message location format:
     * i(className,[ methodName : codeLineNumber ] : message)
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
	public static int i(String msg ,  Throwable tr) {
		String[] location = getLocation();
        return i(location[0],location[1]+msg, tr);
    }
	
	/**
     * Send a {@link #WARN} log message and log the exception if log available with location of code.
     * Log message location format:
     * d(className,[ methodName : codeLineNumber ] : message)
     * 
     * @param msg The message you would like logged.
     */
	public static int w(String msg) {
		String[] location = getLocation();
        return w(location[0],location[1]+msg);
    }

    /**
     *
     * Send a log message and log the exception if log available with location of code.
     * Log message location format:
     * wtf(className,[ methodName : codeLineNumber ] : message)
     *
     * @param msg The message you would like logged.
     */
	public static int wtf(String msg) {
		String[] location = getLocation();
		return wtf(location[0],location[1]+msg);
    }
	
	/**
     * Send a {@link #DEBUG} log message if log available with array of object arguments as message separated by comma.
     * 
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     *        
     * @param args array of objects to log
     */
	public static int d(String tag,Object... args){
		return d(tag,concatArgs(args));
	}
	
	/**
     * Send an {@link #ERROR} log message if log available with array of object arguments as message separated by comma.
     * 
     * @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     *
     * @param args The data you would like logged.
     */
	public static int e(String tag,Object... args){
		return e(tag,concatArgs(args));
	}
	
	/**
     * Send an {@link #INFO} log message if log available with array of object arguments as message separated by comma.
     *
     * @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     *
     * @param args The data you would like logged.
     */
	public static int i(String tag,Object... args){
		return i(tag,concatArgs(args));
	}
	
	/**
     * Send an {@link #VERBOSE} log message if log available with array of object arguments as message separated by comma.
     * 
     * @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     *
     * @param args The data you would like logged.
     */
	public static int v(String tag,Object... args){
		return v(tag,concatArgs(args));
	}
	
	/**
     * Send an {@link #WARN} log message if log available with array of object arguments as message separated by comma.
     * 
     *  @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     *
     *  @param args The data you would like logged.
     */
	public static int w(String tag,Object... args){
		return w(tag,concatArgs(args));
	}

    /**
     * Send an wtf log message if log available with array of object arguments as message separated by comma.
     *
     *  @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     *
     *  @param args The data you would like logged.
     */
	public static int wtf(String tag,Object... args){
		return wtf(tag,concatArgs(args));
	}
	
	private static String concatArgs(Object... args){
		StringBuilder builder = new StringBuilder();
		for (Object arg : args){
			builder.append(",");
			builder.append(arg);
		}
		return builder.toString();
	}
	
	/**
	 * Search of called code location.
	 * 
	 * @return array of two elements - first - class name , second - method name and code line number 
	 */
	private static String[] getLocation() {
		if(!sLogSettings.isLoggingAvailable())
			return EMPTY_LOCATION;
		
		String[] location = {LIBRARY_FILTER_TAG,""};
		
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        location[0] = getClassName(clazz);
                        location[1] = "[ " + trace.getMethodName() + " : " + trace.getLineNumber() + " ]: ";
                        return location;
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return location;
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }
            return getClassName(clazz.getEnclosingClass());
        }
        return "";
    }
}
