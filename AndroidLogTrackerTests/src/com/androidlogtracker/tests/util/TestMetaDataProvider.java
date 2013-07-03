package com.androidlogtracker.tests.util;


import com.logtracking.lib.api.MetaDataCollector;

public class TestMetaDataProvider {

    private static final String DEFAULT_TEST_KEY = "TEST_KEY";
    private static final String DEFAULT_TEST_VALUE = "TEST_VALUE";

    public static MetaDataCollector generateMetaData(int count){
        MetaDataCollector collector = new MetaDataCollector();
        for(int i=0; i<count; i++)
            collector.put(DEFAULT_TEST_KEY + i, DEFAULT_TEST_VALUE + i);
        return collector;
    }

    public static MetaDataCollector generateMetaDataWithControlSymbols(int count,String controlSymbols){
        MetaDataCollector collector = new MetaDataCollector();
        for(int i=0; i<count; i++)
            collector.put(DEFAULT_TEST_KEY + i, controlSymbols  + DEFAULT_TEST_VALUE + i);
        return collector;
    }
}
