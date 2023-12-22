package com.hades.utility.android.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;

import java.io.FileDescriptor;

public class ImageUtil {
    private static final String TAG = "ImageUtil";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int getBitmapBytesSizeAtBitmapDrawable(BitmapDrawable value) {
        // PO:[Bitmap] get bitmap bytes size - bitmap.getAllocationByteCount()
        Bitmap bitmap = value.getBitmap();
        if (VersionUtil.isVersionNoLessThan4_4()) {
            return bitmap.getAllocationByteCount();
        }

        // PO:[Bitmap] get bitmap bytes size - bitmap.getByteCount()
        if (VersionUtil.isVersionNoLessThan3_1()) {
            return bitmap.getByteCount();
        }

        // PO:[Bitmap] get bitmap bytes size - bitmap.getRowBytes() * bitmap.getHeight()
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public Bitmap decodeResource(Resources res, int resId, int reqWidth, int reqHeight) {
        return decodeResource(res, resId, reqWidth, reqHeight, null);
    }

    /**
     * 如何避免大图出现OOM？
     * BitmapFactory.Options.inJustDecodeBounds=true/false
     * BitmapFactory.Options.inSampleSize = size   // 计算出图片缩放比例.
     */
    public Bitmap decodeResource(Resources res, int resId, int reqWidth, int reqHeight, IInBitmapListener listener) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * inJustDecodeBounds = true: 表示不返回实际的Bitmap，用来查询图片大小信息。即不分配内存来避免内存溢出
         * 将 inJustDecodeBounds 设为 true 进行解码，传递选项，然后使用新的 inSampleSize 值并将 inJustDecodeBounds 设为 false 再次进行解码
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // PO:[Bitmap] BitmapFactory.Options.inSampleSize
        /**
         * options.inSampleSize 缩放比例。
         * (1) inSampleSize可能<0，要做判断。
         * (2) 可以计算缩放比例。也可以不计算，直接给它设定一个值。
         * (3) inSampleSize=2,表示 高度和宽度都是原始的一半
         */
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d(TAG, "scaleSize: viewWidth:" + reqWidth + ",viewHeight:" + reqHeight + "，scaleSize=" + options.inSampleSize);
        if (null != listener) {
            useInBitmap(options, listener);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param reqWidth  ImageView 宽度
     * @param reqHeight ImageView 高度
     * @return 缩放比例
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // https://developer.android.google.cn/topic/performance/graphics/load-bitmap?hl=zh-cn#java

        /**
         * options.outHeight ：原始图片的高度信息
         * options.outWidth 原始图片的宽度信息
         */
        int height = options.outHeight;
        int width = options.outWidth;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            /**
             * 试探最小的缩放比例
             */
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                /**
                 * 用于计算样本大小值，即基于目标宽度和高度的 2 的幂
                 * 根据 inSampleSize 文档，计算 2 的幂的原因是解码器使用的最终值将向下舍入为最接近的 2 的幂。
                 */
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeFile(String filename, int reqWidth, int reqHeight) {
        return decodeFile(filename, reqWidth, reqHeight, null);
    }

    public Bitmap decodeFile(String filename, int reqWidth, int reqHeight, IInBitmapListener listener) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        if (null != listener) {
            useInBitmap(options, listener);
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filename, options);
    }

    public Bitmap decodeFileDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        return decodeFileDescriptor(fileDescriptor, reqWidth, reqHeight);
    }

    public Bitmap decodeFileDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight, IInBitmapListener listener) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        if (null != listener) {
            useInBitmap(options, listener);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    private void useInBitmap(final BitmapFactory.Options options, IInBitmapListener listener) {
        if (VersionUtil.isNoLessThanV3() && null != listener) {
            addInBitmapOptions(options, listener);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addInBitmapOptions(BitmapFactory.Options options, IInBitmapListener listener) {
        if (!VersionUtil.isNoLessThanV3()) {
            return;
        }
        Bitmap inBitmap = listener.getReusableBitmap4InBitmap(options);
        if (inBitmap != null) {
            /**
             * 复用Bitmap
             */
            options.inMutable = true;
            options.inBitmap = inBitmap;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean canUsedForInBitmapReuseWithTargetOptions(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (VersionUtil.isVersionNoLessThan4_4()) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap is smaller than the reusable bitmap candidate allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        } else {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
        }
    }

    // PO:[Bitmap] Bitmap.Config.ARGB_4444
    public int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
