package com.qcwl.debo.ui.found.joke.ruidong_video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.cjt2325.cameralibrary.util.FileUtil;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.jinshanyun.ReleasePassageActivity;
import com.qcwl.debo.ui.found.joke.JokeActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class SDKUtils {

    /**
     * 是否为有效文件
     *
     * @param checkPath 文件
     * @return 返回true文件有效
     */
    public static boolean isValidFile(String checkPath) {
        if (!TextUtils.isEmpty(checkPath)) {
            File file = new File(checkPath);
            return file.exists() && file.length() > 0;
        } else {
            return false;
        }
    }

    /**
     * 将asset文件保存为指定文件
     */
    public static boolean assetRes2File(AssetManager am, String assetFile,
                                        String dstFile) {
        if (isValidFile(dstFile)) {
            return false;
        }
        if (TextUtils.isEmpty(dstFile)) {
            return false;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(dstFile);
            byte[] pBuffer = new byte[1024];
            int nReadLen;
            if (null == am) {
                return false;
            }
            InputStream is = am.open(assetFile);
            while ((nReadLen = is.read(pBuffer)) != -1) {
                os.write(pBuffer, 0, nReadLen);
            }
            os.flush();
            os.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new File(dstFile).delete();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            new File(dstFile).delete();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 绘制片尾图片 并将其保存为临时文件
     *
     * @param author      作者名字
     * @param videoHeight 视频高度
     * @return 图片路径
     */
    public static String createVideoTrailerImage(Activity context,
                                                 String author, int videoHeight, int horiPadding, int dataWidth) {
        String path = Environment.getExternalStorageDirectory()
                + "/trailer_logo.png";

        Bitmap bmpLogo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.video_trailer_logo);
        Bitmap bmpDate = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.video_trailer_date);
        Bitmap bmpAuthor = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.video_trailer_author);

        Paint bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bmpPaint.setFilterBitmap(true);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setARGB(255, 175, 171, 170);
        linePaint.setStrokeWidth(2);

        textPaint.setARGB(255, 232, 232, 232);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String date = sDateFormat.format(new java.util.Date());

        int textSize = 150;
        while (true) {
            textPaint.setTextSize(textSize);
            float textHeight = textPaint.getFontMetrics().descent
                    - textPaint.getFontMetrics().ascent;

            if (textHeight > bmpDate.getHeight()) {
                textSize -= 1;
            } else {
                break;
            }
        }

        try {
            int authorLength = author.length();
            int authorByte = author.getBytes("GBK").length;
            while (authorByte > 16) {
                authorLength -= 1;
                author = author.substring(0, authorLength) + "...";
                authorByte = author.getBytes("GBK").length;
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        int logoWidth = bmpLogo.getWidth();
        int messageWidth = (int) (bmpDate.getWidth() + bmpAuthor.getWidth()
                + dataWidth + 40 + textPaint.measureText(date) + textPaint
                .measureText(author));

        int logoDateHeight = (bmpDate.getHeight() + bmpLogo.getHeight() + 80);

        float scale = (float) videoHeight / (logoDateHeight * 2);

        int bmpWidth;
        int logoLeft = 0;
        int top = (int) ((videoHeight - logoDateHeight * scale) / (2 * scale));
        int messageLeft = 0;
        if (logoWidth >= messageWidth) {
            bmpWidth = (int) (logoWidth * scale);
            messageLeft = (logoWidth - messageWidth) / 2;
        } else {
            bmpWidth = (int) (messageWidth * scale);
            logoLeft = (messageWidth - logoWidth) / 2;
        }

        Bitmap bmp = Bitmap.createBitmap(bmpWidth + 2 * horiPadding,
                videoHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmp);
        canvas.scale(scale, scale);
        logoLeft += horiPadding / scale;
        messageLeft += horiPadding / scale;
        canvas.drawBitmap(bmpLogo, logoLeft, top, bmpPaint);

        top = top + bmpLogo.getHeight() + 40;
        canvas.drawLine(logoLeft, top, logoLeft + bmpLogo.getWidth(), top,
                linePaint);
        top = top + 40;
        int baseline = (int) ((bmpDate.getHeight()
                - textPaint.getFontMetrics().bottom - textPaint
                .getFontMetrics().top) / 2);
        int textTop = top + baseline;

        canvas.drawBitmap(bmpDate, messageLeft, top, bmpPaint);
        messageLeft = messageLeft + bmpDate.getWidth() + 20;

        canvas.drawText(date, messageLeft, textTop, textPaint);

        messageLeft = (int) (messageLeft + textPaint.measureText(date) + dataWidth);
        canvas.drawBitmap(bmpAuthor, messageLeft, top, bmpPaint);

        messageLeft = messageLeft + bmpAuthor.getWidth() + 20;
        canvas.drawText(author, messageLeft, textTop, textPaint);
        File file = new File(path);
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bmp.recycle();
        bmp = null;
        return path;
    }

    /**
     * 播放视频
     *
     * @param path
     */
    public static void onPlayVideo(Context context, String path) {
        Log.i("SDKUtils","......onPlayVideo="+context.getPackageName());
       /* Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.ACTION_PATH, path);
        context.startActivity(intent);*/

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);// path 本地视频的路径
        Bitmap bitmap  = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC );
        Log.i("SDKUtils","..........bitmap="+bitmap);
        //获取视频路径
        String thumbPath = FileUtil.saveBitmap("JCamera", bitmap);
        Intent intent = new Intent();
        intent.setClass(context, ReleasePassageActivity.class);
        intent.putExtra("publishType", 1);
        intent.putExtra("thumbPath", thumbPath);
        intent.putExtra("videoPath", path);
        context.startActivity(intent);
    }
}
