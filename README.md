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

Set appropriate format you can using method <code>setLogFileFormat</code> in <code>LogConfigurationBuilder</code> class. 

##Log saving and log file rotating
Library supports two mode for saving log messages in separate file , it's : 
- <code>LogConfiguration.LogSavingMode.SAVE_ALL_LOG_IN_FILE</code>  - saves filter log messages into separate file while application works and rotate this file time-to-time.
 
- <code>LogConfiguration.LogSavingMode.SAVE_ONLY_IF_NEEDED</code> – saves filtered log messages 	into separate file , only before bug or crash report preparation.

To avoid growing of log file while application next mode's of log file rotating could be used : 
- Rotating by timeout – rotate log file by setted timeout of current log file "life time". Use method <code>setLogFileRotationTime()</code> in <code>LogConfigurationBuilder</code> class.

- Rotating by file size – rotate log file by setted maximum size of current log file. Use method <code>setLogFileRotationSize()</code>  in <code>LogConfigurationBuilder</code> class.


##Usage example
Before any logging using , you should initialize log by invocation method <code>Log.init(LogConfiguration logConfiguration,Context applicationContext)</code>. For this, recommended create custom application class , and initialize log in <code>onCreate</code> method. 

<pre><code> public class YourApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
	
		EmailLogSendingConfiguration emailLogSendingConfiguration = new EmailLogSendingConfiguration(
                                            "example.email.@gmail.com" ,
                                            "pass",
                                            "developer.email@gmail.com");

        emailLogSendingConfiguration.addEmailRecipient("another.recipient@email.com");

        LogConfiguration configuration =    LogConfiguration.LogConfigurationBuilder.newDebugConfiguration(this).

                                            setLogFileFormat(LogConfiguration.LogFileFormat.XML).
                                            addTagToFilter(TestTags.ERROR_TAG,true).
                                            addTagToFilter(TestTags.VERBOSE_TAG, true).
                                            setLevelFilter(Log.DEBUG).
                                            setLogFileRotationTime(30 * 1000).
                                            addAttachedFileToReport("/data/anr/traces.txt").
                                            setAfterCrashAction(LogConfiguration.AfterCrashAction.RELAUNCH_APPLICATION).

                                            setSendingConfiguration(emailLogSendingConfiguration).

                                            putMetaData("meta-key", "meta-value").
                                            putMetaData("meta-key", "meta-value").

                                            build();
		Log.init(configuration,this);
	}
}
</code></pre>

And register in your AndroidManifest.xml file special activity for issue reporting:  
<pre><code> &lt;activity android:name="com.logtracking.lib.internal.DefaultReportIssueActivity"
            	  android:theme="@android:style/Theme.NoTitleBar"/> </code></pre>
(for more details about activity for issue reporting, see "Bug report dialog" below )            	  

After this you could use as usual, for example: <code>Log.i(TAG,”Message”)</code>

**Note:** now it's need to import <code>com.logtracking.lib.api.Log</code> , instead of <code>android.util.Log</code>!

For more details, you can see “AndroidLogTrackerUsageExapmle” demo application and javadoc for main API classes :  <code>LogConfiguration, Log, LogUtils</code>.

##Issue report dialog
This dialog provide possibility to send issue report about application.
Issue report dialog showing in case of application crash or invokation of method :
<pre><code> ReportIssueDialog.show(); </code></pre>
Recomended to create special buttom in application,like "Report about problem", where invoke described method to provide 
for user possibility to report about problems in your application.
See "AndroidLogTrackerUsageExapmle" as example to use.
<br>Library provide default implementation of this dialog. Screenshot examples : 
![Alt text](http://s24.postimg.org/bhsj72jgl/Untitled.png)
<br>1. Simple bug report dialog, showed by invoking <pre><code> ReportIssueDialog.show(); </code></pre>
2. Crash report dialog, when sending configuration provided
<br>3. Crash report dialog,without sending configuration

<br>In case if you want to implement your custom issue report activity, you need to :
* Create activity that extends from <code>ReportIssueActivity</code>.
In this activity for sending issue report you should call <code>onSendIssueReport(String issueMessage)</code> with
message description about problem or <code>onCancel()</code> otherwise, if "cancel" button was clicked.
See, <code>com.logtracking.lib.internal.DefaultReportIssueActivity</code> as example of implemntation.
* Set class of custom activity in <code>LogConfigurationBuilder.setReportIssueDialog(customReportIssueDialog)</code>
* Of course,register this activity in your <code>AndroidManifest.xml</code> file.

##Bug report sending
At this moment library supports sending prepared bug reports by e-mail. For sending bug reports on your e-mail, you need to create and configure  EmailLogSendingConfiguration as was showed previously : 

<pre><code>EmailLogSendingConfiguration emailLogSendingConfiguration = new EmailLogSendingConfiguration(
                                            "example.email.@gmail.com" ,
                                            "pass",
                                            "developer.email@gmail.com");

LogConfiguration configuration = LogConfiguration.LogConfigurationBuilder.newDebugConfiguration(this).
                                            setSendingConfiguration(emailLogSendingConfiguration).
                                            build();
</code></pre>

By default library support “GMAIL”  host, for other email host need to set host address in 
<code>setEmailHost(String emailHost)</code>. 

##Languages support
At this moment library support only English, Russian and Ukrainian languages.

##Require permissions
Using of library required next permissions for your application , depending of logging configuration.

* <code>android.permission.INTERNET</code> – required for sending reports by choses service. This permission required if you will send bug reports from application.

* <code>android.permission.WRITE_EXTERNAL_STORAGE</code> – required for saving log messages and bug reports on devices external storage. In case if path setted in <code>LogConfigurationBuilder.setLogDirectoryName()</code> is placed not on external storage,you could not use this permission. But it's better to use dirrectory in external storage.

* <code>android.permission.READ_LOGS</code>  - required permission for reading and handling logs inside the library.

* <code>android.permission.ACCESS_NETWORK_STATE</code> -  required permission for  checks what kind of networks works.   

##Third party libs and code
This product includes software developed by the  Apache Software Foundation (http://www.apache.org/).  Also this library used e-mail port library for Android by address : http://code.google.com/p/javamail-android/.

