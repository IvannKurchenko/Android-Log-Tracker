package com.androidlogtracker.tests.util;

import com.logtracking.lib.api.MetaDataCollector;

import java.util.HashMap;
import java.util.Map;

public class FakeMetaDataCollector extends MetaDataCollector {

    private Map<String,String> dummyMap = new HashMap<String, String>();

    public FakeMetaDataCollector() {
        super(null, false);
    }

    @Override
    public void collectDefaultInfo() {
    }

    @Override
    public void put(String key, String value) {
        dummyMap.put(key, value);
    }

    @Override
    public Map<String, String> getData() {
        return dummyMap;
    }

    @Override
    public void remove(String key) {
        dummyMap.remove(key);
    }

    @Override
    public void clearAll() {
        dummyMap.clear();
    }

    @Override
    public boolean isEmpty() {
        return dummyMap.isEmpty();
    }
}
