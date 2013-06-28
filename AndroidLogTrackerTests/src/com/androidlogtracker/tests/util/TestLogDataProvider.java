package com.androidlogtracker.tests.util;


import com.logtracking.lib.internal.LogModel;

import java.util.ArrayList;
import java.util.List;

public class TestLogDataProvider {

    private static final char DEFAULT_LOG_LEVEL = 'V';
    private static final String DEFAULT_LOG_TAG = "TEST";
    private static final String DEFAULT_LOG_MESSAGE = "Test message";
    private static final String DEFAULT_LOG_APPLICATION_PACKAGE = "com.logtracking.lib.test";

    private static final int DEFAULT_PID = 1;
    private static final int DEFAULT_TID = 1;

    public static LogModel generateLogRecord(){
        return generateLogRecord(DEFAULT_LOG_MESSAGE);
    }

    public static LogModel generateLogRecord(String message){
        LogModel record = new LogModel();
        record.setPid(DEFAULT_PID);
        record.setTid(DEFAULT_TID);
        record.setTag(DEFAULT_LOG_TAG);
        record.setMessage(message);
        record.setPackageName(DEFAULT_LOG_APPLICATION_PACKAGE);
        record.setLevelSymbol(DEFAULT_LOG_LEVEL);
        return record;
    }

    public static List<LogModel> generateLogRecords(int count){
        List<LogModel> records = new ArrayList<LogModel>();
        for(int i=0;i<count;i++)
            records.add( generateLogRecord() );
        return records;
    }

    public static List<LogModel> generateLogRecordsWithControlSymbols(int count,String controlSymbol){
        List<LogModel> records = new ArrayList<LogModel>();
        for(int i=0;i<count;i++)
            records.add( generateLogRecord(DEFAULT_LOG_MESSAGE + controlSymbol) );
        return records;
    }
}
