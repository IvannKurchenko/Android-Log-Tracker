package com.logtracking.lib.internal.upload;

import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.settings.LogSettings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConnectionChecker {

	public static boolean isNetworkConnected(LogContext logContext){
        if (logContext.getLogSettings().isSendReportOnlyByWifi()){
            return   isWifiConnected(logContext.getApplicationContext());
        }  else {
            return isWifiConnected(logContext.getApplicationContext()) ||
                   isMobileNetConnected(logContext.getApplicationContext());
        }
	}
	
	private static boolean isWifiConnected(Context context){
		ConnectivityManager connMgr = (ConnectivityManager)
			     context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi =  connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi != null && wifi.isConnected();
	}
	
	private static boolean isMobileNetConnected(Context context){
		ConnectivityManager connMgr = (ConnectivityManager)
			     context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobile != null && mobile.isConnected();
	}
}
