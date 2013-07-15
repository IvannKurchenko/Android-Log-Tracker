package com.logtracking.lib.api.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;

class MetaDataCollector {
		
	/**
	 * The name of the industrial device design.
	 */
	private static final String DEVICE_NAME_KEY = "device_name";
	
	/**
	 * The end-user-visible name for the end product.
	 */
    private static final String DEVICE_MODEL_KEY = "device_model";
	
	/**
	 * The name of the hardware (from the kernel command line or /proc).
	 */
    private static final String DEVICE_HARDWARE_INFO_KEY = "device_hardware_info";
	
	/**
	 * The user-visible SDK version of the framework
	 */
    private static final String DEVICE_OS_SDK_LEVEL_KEY = "device_os_sdk_level";
	
	/**
	 * Comma-separated tags describing the build, like "unsigned,debug".
	 */
    private static final String DEVISE_OS_TAGS_INFO_KEY = "device_os_info";
	
	/**
	 * Default device locale .
	 */
    private static final String DEVICE_LOCALE_KEY = "device_locale";
	
	/**
	 * Default device timezone.
	 */
    private static final String DEVICE_TIMEZONE_KEY = "device_timezone";
	
	/**
	 * The available memory on the system.
	 */
    private static final String DEVICE_SYSTEM_MEMORY_STATE_KEY = "device_system_memory_state";
	
	/**
	 * The external available memory.
	 */
    private static final String DEVICE_SD_CARD_MEMORY_STATE_KEY = "device_sd_card_memory_state";
	
	/**
	 * Memory class for application.
	 */
    private static final String DEVICE_OPERATING_MEMORY_CLASS_KEY = "device_operating_memory_class";

    /**
     * Memory class for application that use large heap size.
     */
    private static final String DEVICE_OPERATING_LARGE_MEMORY_CLASS_KEY = "device_operating_large_memory_class";

	
	/**
	 * Device screen size in pixels.
	 */
    private static final String DEVICE_SCREEN_SIZE_KEY = "screen_size_density";
	
	/**
	 * Device screen density.
	 */
    private static final String DEVICE_SCREEN_DENSITY_KEY = "device_screen_density";
	
	/**
	 * Name of current application.
	 */
    private static final String APP_NAME_KEY = "app_name";
	
	/**
	 * Version number of current application.
	 */
    private static final String APP_VERSION_NUMBER_KEY = "app_version_number";
	
	/**
	 * Version name of current application.
	 */
    private static final String APP_VERSION_NAME_KEY = "app_version_name";
	
	/**
	 * Time of first install of current application
	 */
    private static final String APP_FIRST_TIME_INSTALLED_KEY = "app_first_time_installed";
	
	/**
	 * Time of last update of current application
	 */
    private static final String APP_LAST_TIME_UPDATED_KEY = "app_last_time_updated";
	
	/**
	 * Current application package.
	 */
    private static final String APP_PACKAGE_NAME_KEY = "app_package_name";

	private Map<String,String> metaDataMap;

    /**
     * Constructs object with collected default meta-data.
     *
     * @param context - application context
     */
	public MetaDataCollector(Context context) {
		metaDataMap = new HashMap<String,String>();
		collectDefaultInfo(context);
	}

	/**
	 * Collect all info described in class keys. 
	 */
	private void collectDefaultInfo(Context context){
		metaDataMap.put(DEVICE_NAME_KEY, android.os.Build.DEVICE);
		metaDataMap.put(DEVICE_MODEL_KEY, android.os.Build.MODEL);
		metaDataMap.put(DEVICE_HARDWARE_INFO_KEY, android.os.Build.HARDWARE);
		metaDataMap.put(DEVICE_OS_SDK_LEVEL_KEY, Integer.toOctalString(android.os.Build.VERSION.SDK_INT));
		metaDataMap.put(DEVISE_OS_TAGS_INFO_KEY, android.os.Build.TAGS);
		metaDataMap.put(DEVICE_LOCALE_KEY, Locale.getDefault().toString());
		metaDataMap.put(DEVICE_TIMEZONE_KEY, TimeZone.getDefault().getDisplayName());
		
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		metaDataMap.put(DEVICE_OPERATING_MEMORY_CLASS_KEY, Integer.toString(activityManager.getMemoryClass()));
        metaDataMap.put(DEVICE_OPERATING_LARGE_MEMORY_CLASS_KEY, Integer.toString(activityManager.getLargeMemoryClass()));
		
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		metaDataMap.put(DEVICE_SYSTEM_MEMORY_STATE_KEY, Long.toString(memoryInfo.availMem) );
		
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long externalMemoryAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
		metaDataMap.put(DEVICE_SD_CARD_MEMORY_STATE_KEY, Long.toString(externalMemoryAvailable));
		
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		metaDataMap.put(APP_NAME_KEY, applicationInfo.name);
		metaDataMap.put(APP_PACKAGE_NAME_KEY, applicationInfo.packageName);
		
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			metaDataMap.put(APP_VERSION_NUMBER_KEY, Integer.toString(packageInfo.versionCode) );
			metaDataMap.put(APP_VERSION_NAME_KEY, packageInfo.versionName );
			
			SimpleDateFormat formatter = new SimpleDateFormat();
			metaDataMap.put(APP_FIRST_TIME_INSTALLED_KEY , formatter.format(new Date(packageInfo.firstInstallTime)));
			metaDataMap.put(APP_LAST_TIME_UPDATED_KEY , formatter.format(new Date(packageInfo.lastUpdateTime)));
			
		} catch (NameNotFoundException e) {
			metaDataMap.put(APP_VERSION_NUMBER_KEY, "none" );
			metaDataMap.put(APP_VERSION_NAME_KEY, "none");
			metaDataMap.put(APP_FIRST_TIME_INSTALLED_KEY , "none");
			metaDataMap.put(APP_LAST_TIME_UPDATED_KEY , "none");
		}
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		
		metaDataMap.put(DEVICE_SCREEN_SIZE_KEY,screenHeight +"x"+screenWidth);
		metaDataMap.put(DEVICE_SCREEN_DENSITY_KEY,	Float.toString(displayMetrics.density));
	}
	
	/**
	 * Return copy of meta-data
	 * @return Map<String,String> of key-value pairs of meta-data
	 */
	public Map<String,String> getData(){
		return new HashMap<String, String>(metaDataMap);
	}
}
