package com.androidlogtracker.tests.util;

import com.logtracking.lib.api.MetaDataCollector;
import com.logtracking.lib.internal.LogModel;

import java.util.List;

public class TestDocument {

    public String mMessage;
    public MetaDataCollector mMetaDataCollector;
    public List<LogModel> mRecords;
    public String mFormattedDocument;

    public TestDocument(String message , MetaDataCollector metaData, List<LogModel> records , String formattedDocument){
        mMessage = message;
        mMetaDataCollector = metaData;
        mRecords = records;
        mFormattedDocument = formattedDocument;
    }
}
