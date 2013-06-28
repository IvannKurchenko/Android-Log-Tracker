package com.androidlogtracker.tests.format;

import android.test.AndroidTestCase;
import com.androidlogtracker.tests.util.TestDocument;
import com.logtracking.lib.api.MetaDataCollector;
import com.logtracking.lib.internal.LogModel;
import com.logtracking.lib.internal.format.LogFileFormatter;

import java.util.List;

import static  com.androidlogtracker.tests.util.TestLogDataProvider.*;
import static  com.androidlogtracker.tests.util.TestMetaDataProvider.*;

public abstract class AbstractFormatterTest extends AndroidTestCase implements FormatTestSuites {

    protected static final String DEFAULT_TEST_MESSAGE = "TEST_MESSAGE";
    protected static final int DEFAULT_META_DATA_COUNT = 10;
    private static final int DEFAULT_LOG_RECORDS_COUNT = 10;

    protected LogFileFormatter mFormatter;

    protected abstract LogFileFormatter setUpFormatter();

    @Override
    protected void setUp() throws Exception {
        mFormatter = setUpFormatter();
    }

    @Override
    protected void tearDown() throws Exception {
        mFormatter = null;
    }

    @Override
    public void testRecordFormattedStringShouldBeNotEmpty() {
        String result = formatLogRecords(1).mFormattedDocument;
        assertNotSame("Empty string for non empty record",result,"");
    }

    @Override
    public void testMetaDataFormattedStringShouldBeNotEmpty() {
        String result = formatMetaData(1).mFormattedDocument;
        assertNotSame("Empty string for non empty meta-data",result, "");
    }

    @Override
    public void testMessageFormattedStringShouldBeNotEmpty() {
        String result = formatMessage().mFormattedDocument;
        assertNotSame("Empty string for non empty meta-data",result, "");
    }

    protected TestDocument formatMessage(){
        return formatDocument(DEFAULT_TEST_MESSAGE,null,null);
    }

    protected TestDocument formatMetaData(){
        return formatMetaData(DEFAULT_META_DATA_COUNT);
    }

    protected TestDocument formatMetaData(int count){
        return formatDocument(null, generateMetaData(count), null);
    }

    protected TestDocument formatLogRecords(){
        return formatLogRecords(DEFAULT_LOG_RECORDS_COUNT);
    }

    protected TestDocument formatLogRecords(int count){
        return formatDocument(null, null, generateLogRecords(count));
    }

    protected TestDocument formatDefaultDocument(){
        return formatDocument(DEFAULT_TEST_MESSAGE, generateMetaData(DEFAULT_META_DATA_COUNT), generateLogRecords(DEFAULT_LOG_RECORDS_COUNT));
    }

    protected TestDocument formatDocumentWithControlSymbols(String controlSymbols){
        return formatDocument(  DEFAULT_TEST_MESSAGE + controlSymbols,
                                generateMetaDataWithControlSymbols(DEFAULT_META_DATA_COUNT,controlSymbols),
                                generateLogRecordsWithControlSymbols(DEFAULT_LOG_RECORDS_COUNT,controlSymbols) );
    }

    protected TestDocument formatDocument(String message , MetaDataCollector metaData , List<LogModel> records){
        StringBuilder builder = new StringBuilder();
        builder.append(mFormatter.getDocumentOpenTag());
        builder.append('\n');

        if(message != null) {
            builder.append(mFormatter.formatMessage(message));
            builder.append('\n');
        }

        if(metaData != null){
            builder.append(mFormatter.getMetaDataOpenTag());
            builder.append('\n');
            builder.append(mFormatter.formatMetaData(metaData.getData()));
            builder.append(mFormatter.getMetaDataCloseTag());
            builder.append('\n');
        }

        if(records != null) {
            builder.append(mFormatter.getLoggingOpenTag());
            builder.append('\n');
            for(LogModel record : records) {
                builder.append(mFormatter.formatLogRecord(record));
                builder.append('\n');
            }
            builder.append(mFormatter.getLoggingCloseTag());
            builder.append('\n');
        }

        builder.append(mFormatter.getDocumentCloseTag());

        return new TestDocument(message,metaData,records,builder.toString());
    }
}
