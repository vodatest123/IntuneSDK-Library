package com.vodafone.intune_lib.logging;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AppLogger {
	private static int LOG_LEVEL = 15;
	private static String TAG = "";
	
	private static String LOG_V = " V/";
	private static String LOG_D = " D/";
	private static String LOG_I = " I/";
	private static String LOG_E = " E/";


	public enum LogLevel {
		LOG_VERBOSE(1),
		LOG_DEBUG(2),
		LOG_INFO(4),
		LOG_ERROR(8),
		LOG_RELEASE(16);

		private int logLevel = 1;

		private LogLevel(int  logLevel) { 
			this.logLevel  = logLevel; 
		}

		public int getLogLevel() {
			return logLevel;
		}
	}

	public static void printLog(Context context,LogLevel logLevel, String tag, String msg, Exception... optional) {
		TAG = tag;
		if ((LOG_LEVEL & logLevel.getLogLevel()) != logLevel.getLogLevel()) {
			return;
		}
		switch(logLevel) {
		case LOG_VERBOSE:
			verboseLog(context,tag, msg);
			break;
		case LOG_DEBUG:
			debugLog(context,tag, msg, optional);
			break;
		case LOG_INFO:
			infoLog(context,tag, msg);
			break;
		case LOG_ERROR:
			errorLog(context,tag, msg, optional);
			break;
		case LOG_RELEASE:
			break;
		default:
			debugLog(context,tag, msg, optional);
			break;
		}
	}

	private static void verboseLog(Context context,String tag, String msg) {
		Log.v(tag, String.format("%s", msg));
		writeToLogFile(context,LOG_V, tag, msg);
	}
	
	private static void infoLog(Context context,String tag, String msg) {
		Log.i(tag, String.format("%s", msg));
		writeToLogFile(context,LOG_I, tag, msg);
	}

	private static void debugLog(Context context,String tag, String msg, Exception... optional) {
		Exception e = optional == null || optional.length == 0 ? null : optional[0];
		if (e == null) {
			Log.d(tag,String.format("%s", msg));
			writeToLogFile(context,LOG_D, tag, msg);
		}
		else {
			Log.d(tag,String.format("%s -- Error message = %s", msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
			writeToLogFile(context,LOG_D, tag, (e.getCause() != null ? e.getCause() : new Throwable(e.getMessage())));
		}
	}

	private static void errorLog(Context context,String tag, String msg, Exception... optional) {
		Exception e = optional == null || optional.length == 0 ? null : optional[0];
		if (e == null) {
			Log.e(tag, String.format("%s", msg));
			writeToLogFile(context,LOG_E, tag, msg);
		}
		else {
			Log.e(tag, String.format("%s -- Error message = %s", msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
			writeToLogFile(context,LOG_E, tag, (e.getCause() != null ? e.getCause() : new Throwable(e.getMessage())));
		}
	}
	public static String getStackTraceString(Throwable tr) 
	{
		return ""+Log.getStackTraceString(tr);
	}
	
	private static File openLogFile(Context context) {
		 
		Date date = new Date();
		String mLocal = null; 
   		mLocal = Utility.getLogSDPath(context);
   		String versionName="none";
		try {
			versionName = context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			Log.d(TAG,"Unable to retrieve version number");
			e1.printStackTrace();
		}
		
   		File dir = new File(mLocal);
        if(!dir.exists() && dir.mkdirs()) 
        {	        	
        } 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
		File intunePOC_LogFile = new File(mLocal, dateFormat.format(date) +"_V_"+versionName+ "_Intune_poc_Log.txt") ;
		try {
			intunePOC_LogFile.createNewFile();
			return intunePOC_LogFile;
		} catch (IOException e) {
			Log.e(TAG, getMethodName()+" Exception ==>", e);
			return null;
		}finally{
			deleteLogs(context,mLocal);
		}
		
	}
	
	private static void deleteLogs(Context context,String logsDir){
		try{
		File logFolder = new File(logsDir);
		String logs[] = logFolder.list();
		if(logs != null){
			Arrays.sort(logs);
			for(int i=0;i<logs.length-7;i++){
				File logfile = new File(logsDir+logs[i]);
				Log.d(TAG,"deleting logfile==>"+logfile.getAbsolutePath());
				logfile.delete();
			}
		}
		}catch(Exception e){
			printLog(context, LogLevel.LOG_ERROR,TAG, getMethodName()+" Exception ==>", e);
		}
	}
	
	private static void writeToLogFile(Context context,String logType, String tag, String log){
		writeToLogFile(context,logType,tag,log,null);
	}
	
	private static void writeToLogFile(Context context,String logType, String tag,Throwable tr){
		writeToLogFile(context,logType,tag,null,tr);
	}
	
	/*private static ThreadLocal<String> prefix = new ThreadLocal<String>() {
		protected synchronized String initialValue() {
			int processId = android.os.Process.myPid();
			long threadId = Thread.currentThread().getId();
			return " ["+String.valueOf(processId)+"/"+String.valueOf(threadId)+"] ";
		}
	};*/
	
	private synchronized static void writeToLogFile(Context context,String logType, String tag,String log,Throwable tr){
		try {
			File signalVP_LogFile = openLogFile(context);
			if(signalVP_LogFile == null){
				return;
			}
			FileWriter fstream = new FileWriter(signalVP_LogFile,true);
			BufferedWriter out = new BufferedWriter(fstream);
			
			if (tag == null) tag = NULLMSG;
			if (log == null) log = NULLMSG;
			out.write((new Date()).toString());
			//out.write(prefix.get());
			out.write(logType);
			out.write(tag);
			out.write(": ");
			out.write(log);
			out.newLine();
			if(tr !=null)
			{
				out.write(getStackTraceString(tr));
				out.newLine();
			}

			out.close();
			fstream.close();
		}
		catch(Exception e)
		{
			Log.e(TAG, "Error while logging", e);
		}
	}

	private static String NULLMSG = "(null)";
	
	private static String getMethodName(){
		try{
			return "["+new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName()+"]";
		}catch(Exception e){
			return "";
		}
	}
	

}
