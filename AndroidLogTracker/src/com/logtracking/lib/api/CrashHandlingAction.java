package com.logtracking.lib.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import static android.content.Intent.*;

/**
 * Enum representing default implementation of actions that will be called after handling of crash.
 * @see OnCrashHandledListener
 */
public enum CrashHandlingAction implements OnCrashHandledListener {
	
	/**
	 * This action will close application after crash handling.
	 */
	CLOSE_APP_ACTION {
		@Override
		public void onCrashHandled(Context applicationContext, Thread thread, Throwable exception) {
			startLaunchIntent(applicationContext, FLAG_ACTIVITY_CLEAR_TOP);
		}
	},
	
	/**
	 * This action will relaunch application after crash handling.
	 */
	RELAUNCH_APP_ACTION {
		@Override
		public void onCrashHandled(Context applicationContext, Thread thread, Throwable exception) {
			startLaunchIntent(applicationContext, FLAG_ACTIVITY_CLEAR_TOP , FLAG_ACTIVITY_NEW_TASK);
		}
			
	};

	private static void startLaunchIntent(Context applicationContext, int... intentFlags){
		PackageManager pm = applicationContext.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(applicationContext.getPackageName());
		if (intent != null){
			for (int flag : intentFlags)
			intent.addFlags(flag);
			applicationContext.startActivity(intent);
		}
	}
}
