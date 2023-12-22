package com.hades.example.android.lib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

//ScreenSizeTool.java
public class WindowUtils {
    public static int width(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int height(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        // 获取窗口管理器
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        // 获得屏幕宽和高
        display.getMetrics(metrics);
        return metrics;
    }
}
