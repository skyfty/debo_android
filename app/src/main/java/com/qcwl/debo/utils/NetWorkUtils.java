package com.qcwl.debo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络相关辅助类?
 * @author Administrator
 *
 */
public class NetWorkUtils {

	/**
	 * 判断是否连接网络
	 */
	public static boolean isConnected(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivityManager) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (null != networkInfo && networkInfo.isConnected()) {
				if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 判断当前网络连接的是否是wifi
	 */
	public static boolean isWifi(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivityManager) {
			return false;
		}
		return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	}
	/**
	 * 打开网络设置
	 */
	public static void openSetting(Activity activity){
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.setting","com.android.setting.WirelessSetting");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
}
