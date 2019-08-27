package com.qcwl.debo.http;

import android.util.Log;

/**
 * log日志管理类
 * @author wj
 *
 */
public class Logger {
	/**
	 * 工具类中的log
	 */
	public static final String Log_Utils = "utls";
	/**
	 * 整个项目中的日志
	 */
	public static final String Log_MoDuoKe = "moduoke";
	public static final String Log_NetData = "netdata";
	
	private static int LOGLEVEL = 7;
	private static int VERBOSE = 1;
	private static int DEBUG = 2;
	private static int INFO = 3;
	private static int WARN = 4;
	private static int ERROR = 5;

	public static void v(String tag, String msg) {
		if (LOGLEVEL > VERBOSE)
			Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (LOGLEVEL > DEBUG)
			Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (LOGLEVEL > INFO)
			Log.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (LOGLEVEL > WARN)
			Log.w(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (LOGLEVEL > ERROR)
			Log.e(tag, msg);
	}
}
