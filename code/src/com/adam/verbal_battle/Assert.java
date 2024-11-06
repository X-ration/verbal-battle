package com.adam.verbal_battle;

public class Assert {

    public static void assertTrue(boolean exp, String message) {
        if(!exp) {
            ConsoleUtils.printErrorAndExit("Assertion failed" + (message != null ? ":" + message : ""));
        }
    }

}
