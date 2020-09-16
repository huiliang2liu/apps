package com.nibiru.studio.arscene;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;


public class LauncherHelper {
	static SimpleDateFormat mFormat;
	public static int batteryLevelValue = 100;
	/**
	 * 获取当前时间
	 * Get current time
	 * @return
	 */
	public static String getCurTimeStr() {
		if (mFormat == null) {
			mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
			mFormat.setTimeZone(TimeZone.getDefault());
		}
		String time = mFormat.format(Calendar.getInstance().getTime());
		return time;
	}

	public final static int ICON_SIZE = 72;

	public static Bitmap iconDrawableToBitmap(Drawable drawable) {
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
		drawable.draw(canvas);
		return bitmap;
	}


	public static class DisplayTimeComparator implements Comparator<ResolveInfo> {
		public DisplayTimeComparator(PackageManager pm) {
			mPM = pm;
		}

		@SuppressLint("NewApi")
		public final int compare(ResolveInfo a, ResolveInfo b) {
			long timeA = 0, timeB = 0;
			try {
				timeA = mPM.getPackageInfo(a.activityInfo.packageName, 0).firstInstallTime;

				timeB = mPM.getPackageInfo(b.activityInfo.packageName, 0).firstInstallTime;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return timeA == timeB ? 0 : timeA > timeB ? 1 : -1;
		}

		private PackageManager mPM;
	}

	public static String subStringCN(final String str, final int maxLength) {
		if (str == null) {
			return str;
		}
		String suffix = "...";
		final StringBuffer sbuffer = new StringBuffer();
		final char[] chr = str.trim().toCharArray();
		int len = 0;
		for (int i = 0; i < chr.length; i++) {
			// 如果num >= 0xa1,说明输入的是汉字
			if (chr[i] >= 0xa1) {
				len += 2;
			} else {
				len++;
			}
		}
		if (len <= maxLength) {
			return str;
		}
		len = 0;
		for (int i = 0; i < chr.length; i++) {
			if (chr[i] >= 0xa1) {
				len += 2;
				if (len > maxLength) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			} else {
				len++;
				if (len > maxLength) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			}
		}
		sbuffer.append(suffix);
		return sbuffer.toString();
	}

}
