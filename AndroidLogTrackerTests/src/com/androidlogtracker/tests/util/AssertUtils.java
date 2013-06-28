package com.androidlogtracker.tests.util;

public class AssertUtils {

    public static void throwAssertError(Exception exception){
        throw new AssertionError(exception);
    }
}
