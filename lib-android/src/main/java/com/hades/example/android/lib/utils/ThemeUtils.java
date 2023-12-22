package com.hades.example.android.lib.utils;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class ThemeUtils {
    private static int mStatusHeight = -1;

    /**
     * @param context
     * @param attrResId e.g. R.attr.color1
     * @return e.g. Light: <item name="color1">@android:color/holo_red_light</item> Dark : <item name="color1">@android:color/holo_orange_dark</item>
     */
    public static int getResourceIdByAttrId(Context context, int attrResId) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, tv, true);
        return tv.resourceId;
    }

    /**
     * @param context
     * @param attrResId e.g. android.R.attr.actionBarSize (TYPE_DIMENSION)
     * @return e.g. dp
     */
    public static int getDataInValueIdByAttrId(Context context, int attrResId) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, tv, true);
        return tv.data;
    }

    /**
     * 将颜色整数转换为十六进制字符串
     *
     * @param color 0xAARRGGBB -> #RRGGBB
     *              (1) 0x80FF0000 -> #ff0000
     *              (2) 0xFF00FF00 -> #00ff00
     * @return
     */
    public static String convertIntColorToSting_RGB(int color) {
        return String.format("#%06x", (0xFFFFFF & color));
    }

    /**
     * 将颜色整数转换为十六进制字符串
     *
     * @param color (1) 0x80FF0000 -> #80ff0000
     *              (2) 0xFF00FF00 -> #ff00ff00
     * @return
     */
    public static String convertIntColorToSting_ARGB(int color) {
        return ("#" + Integer.toHexString(color));
    }

    /**
     * @param color (1) #80ff0000 -> 0x80FF0000
     *              (2) #ff00ff00 -> 0xFF00FF00
     * @return
     */
    public static int convertStringColorToInt(String color) {
        return Color.parseColor(color);
    }

    /**
     * setTextSize(textView,R.dimen.text_size_30,context)
     *
     * @param textView
     * @param testSize text size dimen resource
     */
    public static void setTextSize(TextView textView, @DimenRes int testSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getContext().getResources().getDimensionPixelSize(testSize));
    }

    /**
     * setTextSizeUnitPx(textView,getResources().getDimensionPixelSize(R.dimen.text_size_30))
     *
     * @param textView
     * @param size     px size
     */
    public static void setTextSizeUnitPx(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * setTextSizeUnitSp(textView,30);
     *
     * @param textView
     * @param size     sp size
     */
    public static void setTextSizeUnitSp(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public static Float px2dp(Context context, float pixels) {
        final float density = context.getResources().getDisplayMetrics().density;
        return pixels / density;
    }

    public static int dp2px(Context ctx, float dpValue) {
        final float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int sp2px(Context ctx, float spValue) {
        final float scaledDensity = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return mStatusHeight
     */
    public static int getStatusHeight(Context context) {
        if (mStatusHeight != -1) {
            return mStatusHeight;
        }
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mStatusHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStatusHeight;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return bp
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, bmp.getWidth(), bmp.getHeight() - statusBarHeight);
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return bp;
    }

    /**
     * 获取actionbar的像素高度，默认使用android官方兼容包做actionbar兼容
     *
     * @return
     */
    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        if (context instanceof AppCompatActivity && ((AppCompatActivity) context).getSupportActionBar() != null) {
            Log.d("isAppCompatActivity", "==AppCompatActivity");
            actionBarHeight = ((AppCompatActivity) context).getSupportActionBar().getHeight();
        } else if (context instanceof Activity && ((Activity) context).getActionBar() != null) {
            Log.d("isActivity", "==Activity");
            actionBarHeight = ((Activity) context).getActionBar().getHeight();
        } else if (context instanceof ActivityGroup) {
            Log.d("ActivityGroup", "==ActivityGroup");
            if (((ActivityGroup) context).getCurrentActivity() instanceof AppCompatActivity && ((AppCompatActivity) ((ActivityGroup) context).getCurrentActivity()).getSupportActionBar() != null) {
                actionBarHeight = ((AppCompatActivity) ((ActivityGroup) context).getCurrentActivity()).getSupportActionBar().getHeight();
            } else if (((ActivityGroup) context).getCurrentActivity() instanceof Activity && ((Activity) ((ActivityGroup) context).getCurrentActivity()).getActionBar() != null) {
                actionBarHeight = ((Activity) ((ActivityGroup) context).getCurrentActivity()).getActionBar().getHeight();
            }
        }
        if (actionBarHeight != 0)
            return actionBarHeight;
        final TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
            if (context.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            if (context.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        Log.d("actionBarHeight", "====" + actionBarHeight);
        return actionBarHeight;
    }

    /**
     * 设置view margin
     *
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void printTypedArray(String tag, TypedArray typedArray) {
        if (null == typedArray) {
            Log.d(tag, "printTypedArray: typedArray = null");
            return;
        }
        Log.d(tag, "printTypedArray,typedArray.getIndexCount()=" + typedArray.getIndexCount());
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int index = typedArray.getIndex(i);
            TypedValue typedValue = new TypedValue();
            typedArray.getValue(index, typedValue);
            Log.e(tag, "printTypedArray ,index=" + index + ",type=" + typedArray.getType(index) + "," + typedValue.data);
        }
    }

    public static void printAttributeSet(String tag, AttributeSet attrs) {
        if (null == attrs) {
            Log.d(tag, "printAttributeSet, AttributeSet is null.");
            return;
        }

        int attributeCount = attrs.getAttributeCount();
        Log.i(tag, "printAttributeSet,当前属性个数为：" + attributeCount);
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            Log.d(tag, String.format("printAttributeSet,当前属性索引为：%d,索引名为：%s", i, attributeName) + ",当前属性值为：：" + attributeValue);
        }
    }

    public static String getValueFromAttributeSet(String tag, AttributeSet attrs) {
        if (null == attrs) {
            return null;
        }
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            if (attributeName.equals("theme_mode")) {
                return attributeValue;
            }
        }
        return null;
    }

    public static void getValuesFromAttributeSet(AttributeSet attrs, @NonNull Map<String, String> target) {
        if (null == attrs) {
            return;
        }
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            if (target.containsKey(attributeName)) {
                target.put(attributeName, attributeValue);
            }
        }
    }
}
