package com.iamfmgod.armoredcombatenhancer.modules.utils;

import java.util.Random;

/**
 * UtilsModule provides helper functions such as random chance calculations,
 * debug logging, and common math operations.
 */
public class UtilsModule {

    public static boolean DEBUG = true;
    private static final Random RANDOM = new Random();

    public static boolean chanceSuccessful(double probability) {
        return RANDOM.nextDouble() < probability;
    }

    public static void debugLog(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float randomFloat(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }
}