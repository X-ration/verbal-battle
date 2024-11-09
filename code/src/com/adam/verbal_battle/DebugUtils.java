package com.adam.verbal_battle;

public class DebugUtils {

    private static boolean DEBUG_ENABLED = true;

    public static void debugPrintln(String msg) {
        if(DEBUG_ENABLED) {
            ConsoleUtils.println(msg);
        }
    }

}
