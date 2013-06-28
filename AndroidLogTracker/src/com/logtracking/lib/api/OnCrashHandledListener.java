package com.logtracking.lib.api;

import android.content.Context;

/**
* Listener that provide possibility to notify about handling of crash.
*/
public interface OnCrashHandledListener{
	
	/**
     * Callback that will be invoked after user close dialog window ,that will showed when fatal exception
     * in application occurred.
	 * 
	 * @param applicationContext - application context
	 * @param crashedThread - thread where fatal exception occurs
	 * @param exception - fatal exception
	 */
	public void onCrashHandled(Context applicationContext,Thread crashedThread,Throwable exception);
}