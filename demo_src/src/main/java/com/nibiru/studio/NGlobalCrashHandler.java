package com.nibiru.studio;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 全局异常捕获写入SD卡 nvr 目录
 * Caputures of global crashes will be written in nvr directory of SDCard
 * </br>
 * 	    NGlobalCrashHandler handler = new NGlobalCrashHandler();</br>
 * 		Thread.setDefaultUncaughtExceptionHandler(handler);</br>
 *
 * @author cao.hao
 *
 */
public class NGlobalCrashHandler implements UncaughtExceptionHandler {
	// 需求是 整个应用程序 只有一个 MyCrash-Handler
	// Requirement: the whole app has only one MyCrash-Handler
	public final static String DIR_STRING = "VRXEngine";
	private Context context;
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
	private int id = 0;

	public NGlobalCrashHandler(Context context) {
		this.context = context;
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		id++;
		// 使用Toast来显示异常信息
		// Use Toast to display abnormal info
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "Sorry,error occurred in the program and it will exit. Please check the log in VRXEngine directory of SD Card!", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		writeErrorLog(arg1);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Log.e("ccc", "error : ", e);
		}
		// 干掉当前的程序
		// Kill the current program
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void writeErrorLog(Throwable arg1) {
		System.out.println("Errors occurred. Please check log info in /" + DIR_STRING + "...");
		// 1.获取当前程序的版本号. 版本的id
		// 1.Get the current program version. version ID
		String versioninfo = getVersionInfo();

		// 2.获取手机的硬件信息.
		// 2.Get mobile hardware info
		String mobileInfo = getMobileInfo();

		// 3.把错误的堆栈信息 获取出来
		// 3.Get stack error info
		String errorinfo = getErrorInfo(arg1);

		// 4.把所有的信息 还有信息对应的时间 写入SD卡
		// 4.Write all the info and the corresponding time into SDCard
		File dirFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + DIR_STRING);
		dirFile.mkdir();

		String timeString = dataFormat.format(Calendar.getInstance().getTime());
		String fileName = "crash-" + timeString + "_" + id + ".log";
		File file = new File(dirFile.getAbsolutePath(), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(versioninfo.getBytes());
			fos.write(mobileInfo.getBytes());
			fos.write(errorinfo.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get error info
	 * @param arg1
	 * @return
	 */
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * Get mobile hardware info
	 * @return
	 */
	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		// Get system hardware info through reflection
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息
				// Violent reflection to get private info
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Get mobile version info
	 * @return
	 */
	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return "[versionName=" + info.versionName + ",versionCode=" + info.versionCode + "]";
		} catch (Exception e) {
			e.printStackTrace();
			return "Unknown version";
		}
	}
}
