package com.qcwl.debo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by EDZ on 2018/1/30.
 */
public class DeviceUtil {
    public static final String TAG = "DeviceUtil";

    /**
     * 设备信息
     * @param context
     */
    public static  String getDisplay(Context context){
        StringBuilder sb = new StringBuilder();
        sb.append("Version code is  \n");
        sb.append("设备的Android版本号:");//设备的Android版本号
        sb.append(getSDKInt() + " "+getSDKVersion()+"\t");//设备的Android版本号
        sb.append("设备型号:");//设备型号
        sb.append(getDeviceModel()+"\t");//设备型号
        sb.append("设备厂商:");//设备型号
        sb.append(getDeviceBrand()+"\t");//设备型号
        sb.append("程序版本号:\t"+getAppVersionCode(context)+" "+getAppVersionName(context));//程序版本号
        String str = sb.toString()+" \n";
        str+=getDisplayInfomation(context)+" \n";
        str+=getDensity(context)+" \n";
        str+=getScreenSizeOfDevice(context)+" \n";
        str+=getScreenSizeOfDevice2(context)+" \n";
        str+=getAndroiodScreenProperty(context)+"\n";
        return str;
    }

    /**
     * 设备型号
     * @return
     */
    public static String getDeviceModel(){
        return Build.MODEL;
    }

    /**
     * 设备厂商
     * @return
     */
    public static String getDeviceBrand(){
        return Build.BRAND;
    }

    /**
     * 设备的Android版本号
     * @return
     */
    public static int getSDKInt(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * 设备的Android版本号
     * @return
     */
    public static String getSDKVersion(){
        return Build.VERSION.RELEASE;
    }

    /**
     * 程序版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 程序版本号
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 屏幕像素px
     */
    public static   String getDisplayInfomation(Context context) {
        Point point = new Point();
        ((Activity)context).getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(TAG,"the screen size is "+point.toString());
        ((Activity)context).getWindowManager().getDefaultDisplay().getRealSize(point);
        Log.d(TAG,"the screen real size is "+point.toString());
        return  point.toString();
    }

    /**
     * 屏幕信息
     * @param context
     * @return
     */
    public static String getAndroiodScreenProperty(Context context) {
        int width = getScreenPixel(context).widthPixels;         // 屏幕宽度（像素）
        int height = getScreenPixel(context).heightPixels;       // 屏幕高度（像素）
        float density = getDensity(getScreenPixel(context));         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = getDpi(context);     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = getDp(context)[0];  // 屏幕宽度(dp)
        int screenHeight = getDp(context)[1];// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);  // 屏幕适配文件夹（例：layout-sw300dp），是以该属性为准则
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);

        StringBuilder sb = new StringBuilder();
        sb.append( "屏幕宽度（像素）：" + width+"\n");
        sb.append( "屏幕高度（像素）：" + height+"\n");
        sb.append( "屏幕密度（0.75 / 1.0 / 1.5）：" + density+"\n");
        sb.append( "屏幕密度dpi（120 / 160 / 240）：" + densityDpi+"\n");
        sb.append( "屏幕宽度（dp）：" + screenWidth+"\n");
        sb.append( "屏幕高度（dp）：" + screenHeight+"\n");
        return sb.toString();

    }


    /**
     * 屏幕密度dpi
     */
    public static int getDpi(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
    }


    /**
     * 屏幕像素px
     */
    public static Point getDisplayPixel(Context context) {
        Point point = new Point();
        ((Activity)context).getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(TAG,"the screen size is "+point.toString());
        ((Activity)context).getWindowManager().getDefaultDisplay().getRealSize(point);
        Log.d(TAG,"the screen real size is "+point.toString());
        return  point;
    }

    /**
     * 屏幕像素px
     */
    public static DisplayMetrics getScreenPixel(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return  dm;
    }

    /**
     * 屏幕dp
     */
    public static   int[] getDp(Context context) {
        int width = getScreenPixel(context).widthPixels;         // 屏幕宽度（像素）
        int height = getScreenPixel(context).heightPixels;       // 屏幕高度（像素）
        float density = getDensity(getScreenPixel(context));         // 屏幕密度（0.75 / 1.0 / 1.5）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        return new int[]{screenWidth,screenHeight};
    }

    /**
     * 屏幕密度
     */
    public static   float getDensity(DisplayMetrics dm) {
        return dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）;
    }

    /**
     * 屏幕密度dpi
     */
    public static   String getDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Log.d(TAG,"Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+
                " width: "+displayMetrics.widthPixels);
        return "Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+
                " width: "+displayMetrics.widthPixels;
    }

    /**
     * 屏幕尺寸inch
     */
    public static   String getScreenSizeOfDevice(Context context) {
        DisplayMetrics dm =  context.getResources().getDisplayMetrics();
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double x = Math.pow(width,2);
        double y = Math.pow(height,2);
        double diagonal = Math.sqrt(x+y);

        int dens=dm.densityDpi;
        double screenInches = diagonal/(double)dens;
        Log.d(TAG,"The screenInches "+screenInches);
        return "The screenInches: "+screenInches;
    }

    /**
     * 屏幕尺寸inch
     */
    public static   String getScreenSizeOfDevice2(Context context) {
        Point point = new Point();
        ((Activity)context).getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm =  context.getResources().getDisplayMetrics();
        double x = Math.pow(point.x/ dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        Log.d(TAG, "Screen inches : " + screenInches);
        return "The inches: "+screenInches;
    }

    /**
     * dp获取dip
     * @param dp
     * @return
     */
    public static   int convertDpToPixel(int dp,Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)(dp*displayMetrics.density);
    }

    /***
     * px获取dip
     * @param pixel
     * @return
     */
    public static   int convertPixelToDp(int pixel,Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)(pixel/displayMetrics.density);
    }

}
