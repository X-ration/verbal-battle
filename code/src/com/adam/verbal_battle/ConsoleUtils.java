package com.adam.verbal_battle;

import java.util.Scanner;

public class ConsoleUtils {

    private static Scanner scanner = new Scanner(System.in);

    public static void println(String message) {
        System.out.println(message);
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static int inputWithRangeWithExclusion(String message, int min, int max, int exclusion) {
        Assert.assertTrue(min <= max, "inputWithRangeWithExclusion invalid min or max");
        Assert.assertTrue(exclusion >= min && exclusion <= max, "inputWithRangeWithExclusion invalid exclusion");
        int input = 0;
        do {
            System.out.print(message);
            input = scanner.nextInt();
        } while(input < min || input > max || input == exclusion);
        return input;
    }

    public static int inputWithRange(String message, int min, int max) {
        Assert.assertTrue(min <= max, "inputWithRange invalid min or max");
        int input = 0;
        do {
            System.out.print(message);
            input = scanner.nextInt();
        } while(input < min || input > max);
        return input;
    }

    public static void printErrorAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

}
