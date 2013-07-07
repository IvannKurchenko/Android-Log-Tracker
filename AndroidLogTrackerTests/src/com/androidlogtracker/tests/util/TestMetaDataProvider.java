package com.androidlogtracker.tests.util;

import java.util.HashMap;
import java.util.Map;

public class TestMetaDataProvider {

    private static final String DEFAULT_TEST_KEY = "TEST_KEY";
    private static final String DEFAULT_TEST_VALUE = "TEST_VALUE";

    public static Map<String,String> generateMetaData(int count){
        Map<String,String> metaData = new HashMap<String, String>();
        for(int i=0; i<count; i++)
            metaData.put(DEFAULT_TEST_KEY + i, DEFAULT_TEST_VALUE + i);
        return metaData;
    }

    public static Map<String,String> generateMetaDataWithControlSymbols(int count,String controlSymbols){
        Map<String,String> metaData = new HashMap<String, String>();
        for(int i=0; i<count; i++)
            metaData.put(DEFAULT_TEST_KEY + i, controlSymbols  + DEFAULT_TEST_VALUE + i);
        return metaData;
    }
}
