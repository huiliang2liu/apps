package com.bar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.utils.ResourceUtils;


public class ShortcutUtils {


	 public static Intent getShortcutToDesktopIntent(Context context) {
		 Intent intent = new Intent();
		 intent.setClass(context, context.getClass());
		 intent.setAction("android.intent.action.MAIN");  
		 intent.addCategory("android.intent.category.LAUNCHER");
		 ResourceUtils.Initialize(context.getPackageName(),"R");
	     Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
	     shortcut.putExtra("duplicate", false);
	     shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, ResourceUtils.getString("app_name"));
	     shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, ResourceUtils.getDrawable("ic_launcher")));
	     shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
	     return shortcut;

	 }
	 

	 public static void deleteShortCut(Context context)
	 {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,ResourceUtils.getString("app_name"));
        Intent intent = new Intent();
        intent.setClass(context, context.getClass());  
        intent.setAction("android.intent.action.MAIN");  
        intent.addCategory("android.intent.category.LAUNCHER");  
        
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
        context.sendBroadcast(shortcut);          
	 }
	 public static boolean hasInstallShortcut(Context context) {
	     boolean hasInstall = false;

	     String AUTHORITY = "com.android.launcher.settings";
	     int systemversion = Build.VERSION.SDK_INT;
	     if(systemversion >= 8){ 
	    	 AUTHORITY = "com.android.launcher2.settings"; 
	     } 
	     Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/favorites?notify=true");

	     Cursor cursor = context.getContentResolver().query(CONTENT_URI,
	             new String[] { "title" }, "title=?",
	             new String[] { context.getString(ResourceUtils.getString("app_name")) }, null);

	     if (cursor != null && cursor.getCount() > 0) {
	         hasInstall = true;
	     }

	     return hasInstall;
	 }
}
