package com.qcwl.debo.ui.circle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

import id.zelory.compressor.Compressor;

/**
 * Created by Administrator on 2017/5/27.
 */

public class Luban {


    private static final String TAG = "Luban";

    private static Luban INSTANCE;//volatile

    private File mFile; //原图

    private static Activity mContext;

    private String largeFilePath;//原图片绝对路径

    private Luban() {

    }

    public static Luban get(Activity context) {
        mContext = context;
        if (INSTANCE == null) {
            INSTANCE = new Luban();
        }
        return INSTANCE;
    }

    public File launch(File file) {
        checkNotNull(mFile, "the image file cannot be null");
        mFile = file;
        if (file.length() / 1024 < 350) return file;
        largeFilePath = file.getAbsolutePath();
        int[] arr = getImageWidthAndHeight(largeFilePath);
        if (arr == null) return file;
        int w = arr[0];
        int h = arr[1];
        return compressedImage(mContext, w, h, largeFilePath);
    }

    private File compressedImage(Activity activity, int w, int h, String file) {
        return new Compressor.Builder(activity)
                .setMaxWidth(w)
                .setMaxHeight(h)
                .setQuality(100)
                //.setCompressFormat(Bitmap.CompressFormat.PNG)//WEBP
                //.setDestinationDirectoryPath(FileUtil.getPictureDir(activity))
                .build()
                .compressToFile(new File(file));
    }

    public static int[] getImageWidthAndHeight(String file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options); // 此时返回的bitmap为null

        //以1280为标准大小，大于的宽高等比例缩放，小于的使用原始大小

        int w = 0;
        int h = 0;
        //以高作为判断基准
        boolean bool = options.outHeight >= options.outWidth;
        if (bool) {//高大于宽
            if (options.outHeight <= 1280) {
                w = options.outWidth;
                h = options.outHeight;
            } else {
                float scale = 1.0f * options.outWidth / options.outHeight;
                h = 1280;
                w = (int) (h * scale);
            }
        } else {
            if (options.outWidth <= 1280) {
                w = options.outWidth;
                h = options.outHeight;
            } else {
                float scale = 1.0f * options.outHeight / options.outWidth;
                w = 1280;
                h = (int) (w * scale);
            }
        }
        return new int[]{w, h};
    }

    /**
     * 压缩图片
     * obtain the thumbnail that specify the size
     * <p>
     * //@param imagePath the target image path
     *
     * @param width  the width of thumbnail
     * @param height the height of thumbnail
     * @return {@link Bitmap}
     */
    private Bitmap compress(int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(largeFilePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(largeFilePath, options);
    }

    private void checkNotNull(Object object, String s) {
        if (object == null) {
            Log.e(TAG, "" + s);
            return;
        }
    }
}
