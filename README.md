#Android Log Tracker

##Library summary description
Android Log Tracker – is a library for collecting, filtering, saving and formating log messages for creating and sending bug reports. Library  saves log messages while application works or just when need, format bug report with problem description message and meta-data in key-value format, pack report with additional files in ZIP archive and send to developer full bug or crash report.
##Report structure and formats
General report structure is next :

<pre><code>DOCUMENT_OPEN_TAG
	REPORT_MESSAGE
	META_DATA_OPEN_TAG
		META_KEY : META_VALUE
	META_DATA_CLOSE_TAG
	LOGGING_OPEN_TAG
		LOG_RECORD
		…
		LOG_RECORD
	LOGGING_CLOSE_TAG
DOCUMENT_CLOSE_TAG </code></pre>

But, some of formats could not support some kind of tags.
<br>Library supports formating of bug report in default, XML and JSON formats, here is example of formated reports: 

- **Default**

<pre><code>report_message : 
		Example message
: report_message 

meta_data: 
		device_screen_density_key=0.8125; 
		device_os_sdk_level=21; 
:meta_data 

logging: 
		06-23 13:07:48.469  3378  3397 V [ALT]ERROR: test message # 0 
		06-23 13:07:48.469  3378  3397 V [ALT]ERROR: test message # 1 
		06-23 13:07:48.469  3378  3397 V [ALT]ERROR: test message # 2 
:logging </code> </pre>

- **XML**

<pre><code>&lt;?xml version="1.0" encoding="UTF-8"?> 
&lt;report> 
&lt;report_message msg = 'Example message' /> 
&lt;meta_data> 
	&lt;meta_data device_screen_density_key='0.8125'/> 
	&lt;meta_data device_os_sdk_level='21'/> 
&lt;/meta_data> 
&lt;logging> 
	&lt;record date='06-23 13:07:48.469' level='V' pid='3378' tid='3397' 	package_name='com.loglibrarryusage' tag='[ALT]ERROR' msg='test message # 0'/> 
	&lt;record date='06-23 13:07:48.469' level='V' pid='3378' tid='3397' 	package_name='com.loglibrarryusage' tag='[ALT]ERROR' msg='test message # 1'/> 
	&lt;record date='06-23 13:07:48.469' level='V' pid='3378' tid='3397' 	package_name='com.loglibrarryusage' tag='[ALT]ERROR' msg='test message # 2'/> 
&lt;/logging> 
&lt;/report> </code> </pre>

- **JSON**

<pre> <code>{ 
"report_message":"Example message", 

"meta_data": [ 
{ "meta_data":{"device_screen_density_key":"0.8125","device_os_sdk_level":"21"}] , 

"logging":[ 
{"record":{"pid":3378,"tid":3397,"level":"V","date":"06-23 13:07:48.469","msg":"test message # 0","tag":"[ALT]ERROR"}}, 
{"record":{"pid":3378,"tid":3397,"level":"V","date":"06-23 13:07:48.469","msg":"test message # 1","tag":"[ALT]ERROR"}}] 

}</code> </pre>

Set appropriate format you can using method <code>setLogFileFormat</code> in <code>LogSettings</code> class. 

##Log saving and log file rotating
Library supports two mode for saving log messages in separate file , it's : 
- <code>LogSettings.LogSavingMode.SAVE_ALL_LOG_IN_FILE</code>  - saves filter log messages into separate file while application works and rotate this file time-to-time.
 
- <code>LogSettings.LogSavingMode.SAVE_ONLY_IF_NEEDED</code> – saves filtered log messages 	into separate file , only before bug or crash report preparation.

To avoid growing of log file while application next mode's of log file rotating could be used : 
- Rotating by timeout – rotate log file by setted timeout in log settings. Use method <code>setLogFileRotationTime()</code> in <code>LogSettings</code> class.

- Rotating by file size – rotate log file by setted maximum size of current log file. Use method <code>setLogFileRotationSize()</code>  in <code>LogSettings</code> class.


##Usage example
Before any logging using , you should initialize log by invocation method <code>Log.init(LogContext logContext)</code>. For this, recommended create custom application class , and initialize log in <code>onCreate</code> method. 

<pre><code> public class YourApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
	
		//Create new  log settings  and configure it. 
		LogSettings settings = LogSettings.newDebugSettings(this);

		EmailLogSendingSettings emailSettings = new EmailLogSendingSettings("your.email@gmail.com", "password", “report.recipient@gmail.com");
		settings.setSendingSettings(emailSettings);
		
		MetaDataCollector metaDataCollector = new MetaDataCollector(this);
		metaDataCollector.put("meta-key","meta-value");
        
		LogContext logContext = new LogContext.LogContextBuilder(this).
														setSettings(settings).
														setMetaDataCollector(metaDataCollector).
														build();
    
		Log.init(logContext);
	}
}
</code></pre>

And register in your AndroidManifest.xml file special activity for bug reporting:  
<pre><code> &lt;activity android:name="com.logtracking.lib.internal.ReportIssueActivity"
            	  android:theme="@android:style/Theme.NoTitleBar"/> </code></pre>

After this you could use as usual, for example: <code>Log.i(TAG,”Message”)</code>

**Note:** now it's need to import <code>com.logtracking.lib.api.Log</code> , instead of <code>android.util.Log</code>!

For showing “bug report dialog”, you need to invoke :
<pre><code> ReportIssueDialog.show(); </code></pre>

For more details, you can see “AndroidLogTrackerUsageExapmle” demo application and javadoc for main API classes :  <code>LogSettings, LogContext, MetaDataCollector, OnCrashHandledListener, Log, LogUtils</code>.

##Bug report sending
At this moment library supports sending prepared bug reports by e-mail. For sending bug reports on your e-mail, you need to create and configure  EmailLogSendingSettings as was showed previously : 

<pre><code>LogSettings settings = LogSettings.newDebugSettings(this);
EmailLogSendingSettings emailLogSendingSettings = new EmailLogSendingSettings("your.email@gmail.com" , "password", “report.recipient@gmail.com");
emailLogSendingSettings.addEmailRecipient("another@email.com");
emailLogSendingSettings.
settings.setSendingSettings(emailLogSendingSettings);
</code></pre>

By default library support “GMAIL”  host, for other email host need to set host address in EmailLogSendingSettings . 

##Languages support
Ay this moment library support only English, Russian and Ukrainian languages.

##Require permissions
Using of library required next permissions for your application , depending of logging settings.

* <code>android.permission.INTERNET</code> – required for sending reports by choses service. This permission required if you will send bug reports from application (if you set some of LogSendingSettings to LogSettings.setSendingSettings() )

* <code>android.permission.WRITE_EXTERNAL_STORAGE</code> – required for saving log messages and bug reports on devices external storage. In case if path setted in LogSettings.setLogDirectoryName() is placed not on external storage,you could not use this permission. But it's better to use dirrectory in external storage.

* <code>android.permission.READ_LOGS</code>  - required permission for reading and handling logs inside the library.

* <code>android.permission.ACCESS_NETWORK_STATE</code> -  required permission for  checks what kind of networks works.   

##Third party libs and code
This product includes software developed by the  Apache Software Foundation (http://www.apache.org/).  Also this library used e-mail port library for Android by address : http://code.google.com/p/javamail-android/.

