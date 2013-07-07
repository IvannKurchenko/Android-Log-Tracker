package com.androidlogtracker.tests.util;
import com.logtracking.lib.internal.LogModel;

import java.util.List;
import java.util.Map;

public class TestDocument {

    public String mMessage;
    public Map<String,String> mMetaData;
    public List<LogModel> mRecords;
    public String mFormattedDocument;

    public TestDocument(String message , Map<String,String> metaData, List<LogModel> records , String formattedDocument){
        mMessage = message;
        mMetaData = metaData;
        mRecords = records;
        mFormattedDocument = formattedDocument;
    }
}
