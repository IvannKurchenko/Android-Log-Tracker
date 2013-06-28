package com.logtracking.lib.api;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Class provide additional functionality to log standard system and runtime data.
 * All messages has {@link Log#INFO} level.
 *
 * @see Log
 */

public class LogUtils {
	
	private static final String TAG = LogUtils.class.getSimpleName();
	/**
	 * Cannot be instantiated
	 */
	private LogUtils(){
	}

	/**
	 * Log info about free and total info in format: Memory info : Free = free Total = total
	 * Log message level - {@link Log#INFO}.
	 * 
	 * @param tag tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
	 */
	public static int logMemoryInfo(String tag){
		Runtime runtime = Runtime.getRuntime();
		return Log.i(tag, "Memory info : Free = "+runtime.freeMemory()+" Total = "+runtime.totalMemory());
	}
	
	/**
	 * Log info about free and total info in format: Memory info : Free = free Total = total
	 * Log message level - {@link Log#INFO}.
	 */
	public static int logMemoryInfo(){
		return logMemoryInfo(TAG);
	}
	
	/**
	 * Log info about current thread in format : Current thread : Name = name Id = id Priority = priority
	 * Log message level - {@link Log#INFO}.
	 * 
	 *@param tag tag Used to identify the source of a log message.  It usually identifies
     *       the class or activity where the log call occurs.
	 */
	public static int logCurrentThreadInfo(String tag){
		Thread thread = Thread.currentThread();
		return Log.i(tag, "Current thread : Name = " + thread.getName() + " Id = " + 
				thread.getId() +" Priority = " + thread.getPriority());
	}
	
	/**
	 * Log info about current thread in format : Current thread : Name = name Id = id Priority = priority
	 * Log message level - {@link Log#INFO}.
	 */
	public static void logCurrentThreadInfo(){
		logCurrentThreadInfo(TAG);
	}
	
	/**
	 * Log info about count of running threads in format : Running threads count = count.
	 * Log message level - {@link Log#INFO}.
	 * 
	 *@param tag tag Used to identify the source of a log message.  It usually identifies
     *       the class or activity where the log call occurs.
	 */
	public static int logRunningThreadsCount(String tag){
		int count = Thread.activeCount();
		return Log.i(tag, "Running threads count = " + count);
	}
	
	/**
	 * Log info about count of running threads in format : Running threads count = count.
	 * Log message level - {@link Log#INFO}.
	 */
	public static void logRunningThreadsCount(){
		logRunningThreadsCount(TAG);
	}
	
	/**
	 * Log info about current locale and time zone in format : Current locale = locale  Current time zone = time zone
	 * Log message level - {@link Log#INFO}.
	 * 
	 *@param tag tag Used to identify the source of a log message.  It usually identifies
     *       the class or activity where the log call occurs.
	 */
	public static int logLocationAndTimeZoneInfo(String tag){
		return Log.i(tag,"Current locale = " + Locale.getDefault().getDisplayName() + 
				" Current time zone = " + TimeZone.getDefault().getDisplayName());
	}
	
	/**
	 * Log info about current locale and time zone in format : Current locale = locale  Current time zone = time zone
	 * Log message level - {@link Log#INFO}.
	 */
	public static int logLocationAndTimeZoneInfo(){
		return logLocationAndTimeZoneInfo(TAG);
	}
	
	/**
	 * Log info about caller class and method in format : Caller : Class = class Method = method Code line number = line 
	 * Log message level - {@link Log#INFO}.
	 * 
	 * @param tag tag Used to identify the source of a log message.  It usually identifies
     *       the class or activity where the log call occurs.
	 */
	public static int logCallerClassAndMethod(String tag){
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = trace[trace.length > 3 ? 3 : trace.length];
		return Log.i(tag, "Caller : Class = " + caller.getClassName() + " Method = " + caller.getMethodName() + 
					" Code line number = "+caller.getLineNumber());
	}
	
	/**
	 * Log info about caller class and method in format : Caller : Class = class Method = method Code line number = line 
	 * Log message level - {@link Log#INFO}.
	 */
	public static int logCallerClassAndMethod(){
		return logCallerClassAndMethod(TAG);
	}
}
