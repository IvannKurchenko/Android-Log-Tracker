package com.logtracking.lib.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.text.TextUtils;
import com.logtracking.lib.api.Log;

public class LogModel {
	
	protected static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	private static final Calendar CALENDAR = Calendar.getInstance();

	private String mDate;
	private Character mLevelSymbol;
	private int mPid;
	private long mTid;
	private String mTag;
	private String mMessage;
	private String mPackageName;
	private String mLogRecord;
	
	public LogModel(){
        mDate = LOG_DATE_FORMAT.format(CALENDAR.getTime());
	}

	public LogModel(int pid, long tid, int level, String packageName ,String tag,  String message){
		mDate = LOG_DATE_FORMAT.format(CALENDAR.getTime());
        mPid = pid;
		mTid = tid;
        mLevelSymbol = LogFilter.getLevelSymbolByCode(level);
		mPackageName = packageName;
		mTag = tag;
		mMessage = message;
        buildLogRecord();
	}

	public void setFormattedDate(String dateStr) {
		mDate = dateStr;
	}

	public void setLevelSymbol(Character levelSymbol) {
		mLevelSymbol = levelSymbol;
	}

	public void setPid(int pid) {
		mPid = pid;
	}

	public void setTid(long tid) {
		mTid = tid;
	}

	public void setTag(String tag) {
		mTag = tag;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	public void setFullLogRecord(String logRecordStr) {
		mLogRecord = logRecordStr;
	}

	public String getFormattedDate() {
		return mDate;
	}

	public Character getLevelSymbol() {
		return mLevelSymbol;
	}

	public int getPid() {
		return mPid;
	}

	public long getTid() {
		return mTid;
	}

	public String getTag() {
		return mTag;
	}

	public String getMessage() {
		return mMessage;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getFullLogRecord() {
        if(TextUtils.isEmpty(mLogRecord)){
            buildLogRecord();
        }
		return mLogRecord;
	}

    @Override
    public String toString(){
        return getFullLogRecord();
    }

    private void buildLogRecord(){
        StringBuilder builder = new StringBuilder();
        builder.append(mDate);
        builder.append(" ");
        builder.append(mPid);
        builder.append(" ");
        builder.append(mTid);
        builder.append(" ");
        builder.append(mLevelSymbol);
        builder.append(" ");
        builder.append(": ");
        builder.append(mMessage);
        mLogRecord = builder.toString();
    }
}
