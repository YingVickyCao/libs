package com.hades.example.android.lib.utils;

import androidx.annotation.IdRes;

public class BlockQuickTap {

    private static String lastUniqueKey = "";
    private static long lastTs;

    public static long MIN_DURATION_1 = 200;    // 0.2 秒
    public static long MIN_DURATION_2 = 500;    // 0.5 秒
    public static long MIN_DURATION_3 = 800;    // 0.5 秒
    public static long MIN_DURATION_1_SECOND = 1000;   // 1 秒
    public static long MIN_DURATION_2_SECOND = 2000;   // 2 秒

    public static boolean isRepeatShowKeyboard() {
        return isUserFastOperation(String.valueOf(-1), MIN_DURATION_1);
    }

    public static boolean isFastClickBtn(@IdRes int btnId) {
        return isUserFastOperation(String.valueOf(btnId), MIN_DURATION_2_SECOND);
    }

    public static boolean isFastClickBtn() {
        return isUserFastOperation(String.valueOf(-1), MIN_DURATION_2_SECOND);
    }

    public static boolean isRepeatShowDialog(String uniqueKey) {
        return isUserFastOperation(uniqueKey, MIN_DURATION_1_SECOND);
    }

    public static boolean isUserFastOperation(String uniqueKey, long durationMs) {
        long current = System.currentTimeMillis();
        long duration = current - lastTs;
        if ((lastUniqueKey.equalsIgnoreCase(uniqueKey)) && (0 < duration) && (duration < durationMs)) {
            return true;
        }
        lastUniqueKey = String.valueOf(uniqueKey);
        lastTs = current;
        return false;
    }
}