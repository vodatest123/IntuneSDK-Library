package com.vodafone.intune_lib.logging;

import android.content.Context;

import java.util.Date;

public class AppLoggerWrapper {
	private static boolean inTestMode(){
		return true;
	}

	public static void infoLog(Context context,String tag, String msg) {
		if (inTestMode()) {
			logForTestMode(context,tag, msg);
			return;
		}
	}

	private static void logForTestMode(Context context, String tag, String msg, Exception... optional) {
		Exception e = optional == null || optional.length == 0 ? null : optional[0];
		if (e == null) {
			AppLogger.printLog(context,AppLogger.LogLevel.LOG_DEBUG,tag,String.format("%s - %s - %s %s", new Date().toString(), tag, msg,System.getProperty("line.separator")));
		}
		else {
			AppLogger.printLog(context,AppLogger.LogLevel.LOG_DEBUG,tag,String.format("%s - %s - %s  Error message = %s %s", new Date().toString(), tag, msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage(),System.getProperty("line.separator")));
		}
	}

	public static void errorLog(Context context, String tag, String msg, Exception... optional) {
		if (inTestMode()) {
			Exception e = optional == null || optional.length == 0 ? null : optional[0];
			if (e == null) {
				AppLogger.printLog(context,AppLogger.LogLevel.LOG_ERROR,tag, String.format("%s %s", msg,System.getProperty("line.separator")));
			}
			else {
				AppLogger.printLog(context,AppLogger.LogLevel.LOG_ERROR,tag, String.format("%s  Error message = %s %s", msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage(),System.getProperty("line.separator")));
			}
			return;
		}
	}
}
