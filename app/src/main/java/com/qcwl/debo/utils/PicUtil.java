package com.qcwl.debo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.List;

import id.zelory.compressor.Compressor;

public class PicUtil {
    public static final int REQUEST_IMAGE = 1;
    public static final int CROP_IMAGE = 2;

    public static File compressedImage(Activity activity, int w, int h, String file) {
        return new Compressor.Builder(activity)
                .setMaxWidth(w)
                .setMaxHeight(h)
                .setQuality(85)
                //.setCompressFormat(Bitmap.CompressFormat.PNG)//WEBP
                .setDestinationDirectoryPath(PicUtil.getPictureDir(activity))
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

        //以1280*960为标准大小，大于的宽高等比例缩放，小于的使用原始大小
        int w = 0;
        int h = 0;
        //以高作为判断基准
        boolean bool = options.outHeight >= options.outWidth ? true : false;
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

    // 根目录名称
    private static final String mainDir = "schoolServer";

    /**
     * 文件根目录
     */
    public static final String getMainDir() {
        return Environment.getExternalStorageDirectory() + "/" + mainDir;
    }

    // 图片缓存目录
    public static final String getCacheDir() {
        return getMainDir() + "/cache";
    }

    //视频目录
    public static final String getVideoPath() {
        return getMainDir() + "/video";
    }

    //获取视频第一帧图片
    public static Bitmap getImage(String videoPath) {
        //创建MediaMetadataRetriever对象
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        //设置资源位置
        //String path = "/storage/sdcard1" + "/Movies" + "/XiaomiPhone.mp4";
        //绑定资源
        mmr.setDataSource(videoPath);
        //获取第一帧图像的bitmap对象
        Bitmap bitmap = mmr.getFrameAtTime();
        return bitmap;
        //加载到ImageView控件上
        //img.setImageBitmap(bitmap);
    }

    //获取视频第一帧图片
    public static String getVideoThubmImage(Context context, String videoPath) {
        Bitmap bitmap = PicUtil.getImage(videoPath);
        String filename = System.currentTimeMillis() + ".jpg";
        saveBitmap(bitmap, getPictureDir(context), filename);
        return getPictureDir(context) + File.separator + filename;
    }

    /**
     * 缓存Bitmap 写入到本地
     */
    public static String cacheBitmapToFile(Context context, Bitmap bitmap,
                                           String fileName) {
        String cacheFilepath = PicUtil.getCropDir(context) + "/" + fileName;
        if (bitmap == null || cacheFilepath == null) {
            return null;
        }
        try {
            File file = new File(cacheFilepath);
            if (file.exists() == false) {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
            fout.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存图片
     */
    public static void saveBitmap(Bitmap bitmap, String path, String fileName) {
        if (bitmap == null) {
            return;
        }
        // 创建目录
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSucc = dir.mkdirs();
            if (!isSucc) {
                return;
            }
        }
        File imgFile = new File(path, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFile);
            boolean isSucc = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    fos);
            if (isSucc) {
                fos.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            imgFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            imgFile.delete();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取照片exif信息中的旋转角度<br/>
     * http://www.eoeandroid.com/thread-196978-1-1.html
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateImage(String realPath) {
        Bitmap bmp = BitmapFactory.decodeFile(realPath);
        if (bmp != null) {
            int degree = PicUtil.readPictureDegree(realPath);
            if (degree <= 0) {
                return bmp;
            } else {
                // 创建操作图片是用的matrix对象
                Matrix matrix = new Matrix();
                // 旋转图片动作
                matrix.postRotate(degree);
                // 创建新图片
                Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0,
                        bmp.getWidth(), bmp.getHeight(), matrix, true);
                return resizedBitmap;
            }
        }
        return bmp;
    }

    /**
     * 拍照保存的目录
     */
    public static final String getPictureDir(Context context) {
        String dir = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            dir = getMainDir() + "/picture";
        } else {
            // /data/data/pkg/cache
            dir = context.getCacheDir().toString();
        }
        return dir;
    }

    /**
     * 裁剪保存的目录
     */
    public static final String getCropDir(Context context) {
        String dir = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            dir = getMainDir() + "/crop";
        } else {
            // /data/data/pkg/cache
            dir = context.getCacheDir().toString();
        }
        return dir;
    }

    /**
     * apk保存路径
     */
    public static final String getApkDir() {
        return getMainDir() + "/apk";
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        deleteFile(getMainDir());
    }

    private static void deleteFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] child = file.listFiles();
            for (int i = 0; i < child.length; i++) {
                if (child[i].isDirectory()) {
                    File[] subChild = child[i].listFiles();
                    for (int j = 0; j < subChild.length; j++) {
                        subChild[j].delete();
                    }
                } else {
                    child[i].delete();
                }
            }
        }
    }

    public static long getCacheSize(File file) throws Exception {
        long space = 0;
        if (file.isDirectory()) {
            File[] child = file.listFiles();
            for (int i = 0; i < child.length; i++) {
                if (child[i].isDirectory()) {
                    space += getCacheSize(child[i]);
                } else {
                    space += child[i].length();
                }
            }
        } else {
            space = file.length();
        }
        return space;
    }

    /**
     * 获取缓存大小
     */
    public static String getCacheSize() throws Exception {
        long space = getCacheSize(new File(getMainDir()));
        DecimalFormat df = new DecimalFormat("0.00");
        String size = "0B";
        if (space >= 0 && space < 1000) {
            size = space + "B";
        } else if (space >= 1000 && space < 1000 * 1000) {
            size = df.format((space / 1000.0)) + "KB";
        } else if (space >= 1000 * 1000 && space < 1000 * 1000 * 1000) {
            size = df.format((space / 1000 / 1000.0)) + "MB";
        } else {// space >= 1000 * 1000 * 1000
            size = df.format((space / 1000 / 1000 / 1000.0)) + "GB";
        }
        return size;
    }

    public static String getFileSize(File file) throws Exception {
        long space = getCacheSize(file);
        DecimalFormat df = new DecimalFormat("0.00");
        String size = "0B";
        if (space >= 0 && space < 1000) {
            size = space + "B";
        } else if (space >= 1000 && space < 1000 * 1000) {
            size = df.format((space / 1000.0)) + "KB";
        } else if (space >= 1000 * 1000 && space < 1000 * 1000 * 1000) {
            size = df.format((space / 1000 / 1000.0)) + "MB";
        } else {// space >= 1000 * 1000 * 1000
            size = df.format((space / 1000 / 1000 / 1000.0)) + "GB";
        }
        return size;
    }


    /**
     * 创建文件或者目录<br>
     * 注意：需要权限 android.permission.WRITE_EXTERNAL_STORAGE<br>
     *
     * @param path
     * @return
     */
    public static File createNewFileOrDir(String path) {
        File file = new File(path);
        try {
            path = decode(path);
            if (file.exists() == false) {
                if (file.isDirectory()) {
                    file.mkdirs();
                } else {// 创建多级目录
                    String parentPath = path
                            .substring(0, path.lastIndexOf("/"));
                    File parent = new File(parentPath);
                    if (parent.exists() == false) {
                        parent.mkdirs();
                    }
                    file.createNewFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 解密文件路径 一般用于将%20等符号转化为空格，等
     *
     * @param path :
     * @return : 返回解码后的文件路径
     */
    private static String decode(String path) {
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    // ==============================================================
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (isDocumentUri(context, uri)) {
            return getKitkatPath(context, uri);
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isDocumentUri(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            return DocumentsContract.isDocumentUri(context, uri);
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getKitkatPath(final Context context, final Uri uri) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/"
                        + split[1];
            }
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{split[1]};

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static Uri getImageContentUri(Context context, String filePath) {
        //String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (new File(filePath).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void setVideoFile(final List<String> list, File file) {// 获得视频文件
        //File file = Environment.getExternalStorageDirectory();
        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")) {
                        list.add(file.getAbsolutePath());
                        return true;
                    }
                } else if (file.isDirectory()) {
                    setVideoFile(list, file);
                }
                return false;
            }
        });
    }

    public static boolean isExistFile(String parentPath, final String filename) {
        if (TextUtils.isEmpty(parentPath) || TextUtils.isEmpty(filename)) {
            return false;
        }
        new File(parentPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (filename.equals(name)) {
                    return true;
                }
                return false;
            }
        });
        return false;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startPhotoZoom(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // aspectX aspectY 是宽高的比例 // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 350);// 输出是X方向的比例
        intent.putExtra("outputY", 350);
        intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
        // intent.putExtra("noFaceDetection", true);// 取消人脸识别

//设置EXTRA_OUTPUT提示文件找不到，不知道什么问题
//        File file = new File(cropImage);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
//            file.delete();
//        }
//        // 同一个地址下 裁剪的图片覆盖拍照的图片
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, CROP_IMAGE);
    }

    /**
     * 按1：1裁剪图片方法实现
     *
     * @param uri
     */
    public static void startPhotoZooms(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // aspectX aspectY 是宽高的比例 // 裁剪框的比例，1：1
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);// 输出是X方向的比例
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
        // intent.putExtra("noFaceDetection", true);// 取消人脸识别

//设置EXTRA_OUTPUT提示文件找不到，不知道什么问题
//        File file = new File(cropImage);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
//            file.delete();
//        }
//        // 同一个地址下 裁剪的图片覆盖拍照的图片
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, CROP_IMAGE);
    }



    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }



}
