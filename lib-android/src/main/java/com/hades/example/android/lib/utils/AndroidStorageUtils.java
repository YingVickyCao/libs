package com.hades.example.android.lib.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class AndroidStorageUtils {
    private static final String TAG = AndroidStorageUtils.class.getSimpleName();

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public File getExternalCacheDir(Context context) {
        if (VersionUtil.isVersionNoLessThan2_2()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    // /sdcard/name
    public static String buildFileNameInSD(String name) {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + name;
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to getReusableBitmap4InBitmap
     * @return The space available in bytes
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public long getUsableSpace(File path) {
        if (VersionUtil.isVersionNoLessThan2_3()) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean isExternalStorageRemovable() {
        if (VersionUtil.isVersionNoLessThan2_3()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = checkIsMediaMounted() || checkIsExternalStorageBuiltIn() ? getExternalCacheDirPath(context) : getInternalCacheDirPath(context);
        return new File(cachePath + File.separator + uniqueName);
    }

    private boolean checkIsMediaMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean checkIsExternalStorageBuiltIn() {
        return !isExternalStorageRemovable();
    }

    private String getExternalCacheDirPath(Context context) {
        return getExternalCacheDir(context).getPath();
    }

    private String getInternalCacheDirPath(Context context) {
        return context.getCacheDir().getPath();
    }
}
