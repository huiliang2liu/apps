package com.result.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class PermissionUtils {

    private static final String TAG = "PERMISSION_UTILS";

    @StringDef({POST_NOTIFICATION, SYSTEM_ALERT_WINDOW,
            GET_USAGE_STATS, AUTO_START,
            BACKGROUND_START_ACTIVITY, SHOW_ON_LOCK,
            IGNORE_BATTERY_OPTIMIZATION_SETTINGS
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionName {
    }

    public static final String POST_NOTIFICATION = "POST_NOTIFICATION";
    public static final String SYSTEM_ALERT_WINDOW = "SYSTEM_ALERT_WINDOW"; // 悬浮窗
    public static final String GET_USAGE_STATS = "GET_USAGE_STATS";
    public static final String AUTO_START = "AUTO_START";
    public static final String BACKGROUND_START_ACTIVITY = "BACKGROUND_START_ACTIVITY";
    public static final String SHOW_ON_LOCK = "SHOW_ON_LOCK";
    public static final String IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "IGNORE_BATTERY_OPTIMIZATION_SETTINGS";

    @IntDef({GRANTED, DENIED, UNKNOWN, NOT_FOUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionResult {
    }

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int UNKNOWN = 2;
    public static final int NOT_FOUND = 3;
    private static String sName;
    private static String sVersion;
    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    private static final String KEY_VERSION_GIONEE = "ro.gn.sv.version";
    private static final String KEY_VERSION_LENOVO = "ro.lenovo.lvp.version";
    private static final String KEY_VERSION_FLYME = "ro.build.display.id";

    @PermissionResult
    public static int checkPermission(@PermissionName String permissionName,Context context) {
        if (TextUtils.isEmpty(permissionName)) {
            return NOT_FOUND;
        }

        // 常规方式可判断
        if (POST_NOTIFICATION.equals(permissionName)) {
//            if (NotificationManagerCompat.from(BaseApplication.getContext()).areNotificationsEnabled()) {
//                return GRANTED;
//            } else {
                return DENIED;
//            }
        }

        // 特殊方式获取
        switch (permissionName) {
            case AUTO_START:
                return checkAutoStart(context);

            case SYSTEM_ALERT_WINDOW:
                return checkSystemAlertWindow(context);

            case BACKGROUND_START_ACTIVITY:
                return checkBgStartActivity(context);

            case SHOW_ON_LOCK:
                return checkShowOnLock();

            case IGNORE_BATTERY_OPTIMIZATION_SETTINGS:
                return UNKNOWN;

            case GET_USAGE_STATS:
                return checkOpByAppOpsManager(GET_USAGE_STATS,context);
        }

        return NOT_FOUND;
    }

    public static boolean jumpPermissionPage(@NonNull Context context, @PermissionName String opName) {
        if (TextUtils.isEmpty(opName)) {
            return false;
        }

        switch (opName) {
            case POST_NOTIFICATION:
                return jumpPermissionPostNotificationOrAppDetail(context);

            case GET_USAGE_STATS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    if (!(context instanceof Activity)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    }
                    context.startActivity(intent);
                }
                return true;

            case SYSTEM_ALERT_WINDOW:
                return jumpPermissionSystemAlertWindow(context);

            case AUTO_START:
                return jumpPermissionAutoStart(context);

            case IGNORE_BATTERY_OPTIMIZATION_SETTINGS:
                return jumpIgnoreBatteryOptimizationSettings(context);

            case BACKGROUND_START_ACTIVITY:
                return jumpPermissionBackgroundStartActivity(context);

            case SHOW_ON_LOCK:
                return jumpPermissionShowOnLock(context);
        }

        return false;
    }

    public static boolean startMiuiPermissionEditorActivity(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString("com.miui.securitycenter/com.miui.permcenter.permissions.PermissionsEditorActivity"));
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);

            return true;
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static boolean startVivoPermissionDetailActivity(Context context) {
        try {
            Intent intent = new Intent("permission.intent.action.softPermissionDetail");
            intent.putExtra("packagename", context.getPackageName());
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);

            return true;
        } catch (Throwable throwable) {
            try {
                Intent intent = new Intent();
                intent.setComponent(ComponentName.unflattenFromString("com.vivo.permissionmanager/.activity.PurviewTabActivity"));
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private static boolean jumpPermissionSystemAlertWindow(Context context) {
        if (check(ROM_VIVO) && startVivoPermissionDetailActivity(context)) {
            return true;
        }
        return false;
    }


    private static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }


    private static String getProp(String name) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    private static boolean jumpPermissionPostNotificationOrAppDetail(Context context) {

        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);

            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));

            } else {
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean jumpPermissionAutoStart(Context context) {
        if (check(ROM_MIUI)) {
            try {
                Intent intent = new Intent("miui.intent.action.OP_AUTO_START");
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

                return true;
            } catch (Throwable throwable) {
                return jumpAppDetail(context);
            }
        }

        if (check(ROM_EMUI)) {
            try {
                Intent intent = new Intent();
                intent.setComponent(ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity"));
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

                return true;

            } catch (Exception e) {
                return jumpAppDetail(context);
            }
        }

        if (check(ROM_OPPO)) {
            try {
                Intent intent = new Intent();
                intent.setComponent(ComponentName.unflattenFromString("com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity"));
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

                return true;
            } catch (Exception e) {
                return jumpAppDetail(context);
            }
        }

        if (check(ROM_VIVO)) {
            return startVivoPermissionDetailActivity(context) || jumpAppDetail(context);
        }

        return jumpAppDetail(context);
    }

    private static boolean jumpIgnoreBatteryOptimizationSettings(Context context) {
        if (check(ROM_OPPO)) {
            return jumpAppDetail(context);
        }

        try {
            Intent intent = new Intent("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            return jumpAppDetail(context);
        }
    }

    private static boolean jumpPermissionBackgroundStartActivity(Context context) {
        if (check(ROM_MIUI)) {
            return startMiuiPermissionEditorActivity(context) || jumpAppDetail(context);
        }

        if (check(ROM_VIVO)) {
            return startVivoPermissionDetailActivity(context) || jumpAppDetail(context);
        }

        return jumpAppDetail(context);
    }

    private static boolean jumpPermissionShowOnLock(Context context) {
        if (check(ROM_MIUI)) {
            return startMiuiPermissionEditorActivity(context) || jumpAppDetail(context);
        }

        if (check(ROM_VIVO)) {
            return startVivoPermissionDetailActivity(context) || jumpAppDetail(context);
        }

        return jumpAppDetail(context);
    }

    public static boolean jumpAppDetail(Context context) {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));

            } else {
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // AutoStart
    @PermissionResult
    private static int checkAutoStart(Context context) {
        if (check(ROM_EMUI)) {
            return UNKNOWN;
        }

        if (check(ROM_MIUI)) {
            int result = checkOpByAppOpsManager(AUTO_START,context);
            return result == NOT_FOUND ? UNKNOWN : result;
        }

        if (check(ROM_VIVO)) {
            return checkVivoAutoStart(context);
        }

        if (check(ROM_OPPO)) {
            return UNKNOWN;
        }

        return checkOpByAppOpsManager(AUTO_START,context);
    }

    @PermissionResult
    private static int checkVivoAutoStart(Context context) {
        int result = UNKNOWN;

        Uri uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/bg_start_up_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);

            if (cursor == null) {
                result = checkVivoAutoStartByOldMethod(context);
            } else if (cursor.moveToFirst()) {
                int currentMode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                result = currentMode == 0 ? GRANTED : DENIED;
            }

        } catch (Exception e) {
            result = checkVivoAutoStartByOldMethod(context);
        }

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }

    @PermissionResult
    private static int checkVivoAutoStartByOldMethod(Context context) {
        Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/forbidbgstartappslist");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);

            if (cursor == null) {
                return UNKNOWN;
            }

            if (cursor.moveToNext()) {
                return GRANTED;
            }

            return DENIED;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.close();
        }

        return UNKNOWN;
    }

    // float window
    @PermissionResult
    private static int checkSystemAlertWindow(Context context) {
        if (check(ROM_VIVO) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkVivoSystemAlertWindow(context);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                return GRANTED;
            } else {
                return DENIED;
            }
        }

        if ((Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor"))
                && Build.VERSION.SDK_INT < 19) {
            return GRANTED;
        }

        return checkOpByAppOpsManager(SYSTEM_ALERT_WINDOW,context);
    }

    @PermissionResult
    private static int checkVivoSystemAlertWindow(Context context) {
        Uri uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);

            if (cursor == null || !cursor.moveToFirst()) {
                return checkVivoSystemAlertWindowForOldVersion(context);
            } else {
                return cursor.getInt(cursor.getColumnIndex("currentmode")) == 0 ? GRANTED : DENIED;
            }

        } catch (Exception e) {
            return checkVivoSystemAlertWindowForOldVersion(context);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @PermissionResult
    private static int checkVivoSystemAlertWindowForOldVersion(Context context) {
        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("currentlmode")) == 0 ? GRANTED : DENIED;
            }

        } catch (Exception e) {
            return UNKNOWN;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return UNKNOWN;
    }

    // Op BgStartActivity
    @PermissionResult
    private static int checkBgStartActivity(Context context) {
        if (check(ROM_EMUI)) {
            return checkOpByAppOpsManager(BACKGROUND_START_ACTIVITY,context);
        }

        if (check(ROM_VIVO)) {
            return checkVivoBgStartActivity(context);
        }

        if (check(ROM_OPPO)) {
            return NOT_FOUND;
        }

        if (check(ROM_MIUI)) {
            int result = checkOpByAppOpsManager(BACKGROUND_START_ACTIVITY,context);
            return result == NOT_FOUND ? UNKNOWN : result;
        }

        return checkOpByAppOpsManager(BACKGROUND_START_ACTIVITY,context);
    }

    @PermissionResult
    private static int checkVivoBgStartActivity(Context context) {
        int result = UNKNOWN;

        Uri uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};

        Cursor cursor = null;
        try {
            cursor =context.getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                int currentMode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                result = currentMode == 0 ? GRANTED : DENIED;
            } else if (cursor == null) {
                result = GRANTED;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }

    @PermissionResult
    private static int checkShowOnLock() {
        if (!check(ROM_MIUI) && !check(ROM_VIVO)) {
            return NOT_FOUND;
        }

        return UNKNOWN;
    }

    // Base
    private static String permissionName2Declared(@PermissionName String opName) {
        return "OP_" + opName;
    }

    @PermissionResult
    private static int checkOpByAppOpsManager(@PermissionName String opName,Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return GRANTED;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOps == null) {
            return UNKNOWN;
        }

        Class<?> appOpsClass = appOps.getClass();

        try {
            Field field = appOpsClass.getDeclaredField(permissionName2Declared(opName));
            int op = (int) field.get(Integer.class);

            Method method = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            int mode = (int) method.invoke(appOps, op, Process.myUid(), context.getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                return GRANTED;
            } else {
                return DENIED;
            }

        } catch (Throwable e) {
            return NOT_FOUND;
        }
    }

    public static void printAppOpsManagerInfo(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }

        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOpsManager == null) {
            return;
        }

        Class<?> appOpsClass = appOpsManager.getClass();

        try {
            Field sOpNamesField = appOpsClass.getDeclaredField("sOpNames");
            sOpNamesField.setAccessible(true);
            String[] sOpNames = (String[]) sOpNamesField.get(appOpsManager);

            Field sOpPermsField = appOpsClass.getDeclaredField("sOpPerms");
            sOpPermsField.setAccessible(true);
            String[] sOpPerms = (String[]) sOpPermsField.get(appOpsManager);

            Field sOpToStringField = appOpsClass.getDeclaredField("sOpToString");
            sOpToStringField.setAccessible(true);
            String[] sOpToString = (String[]) sOpToStringField.get(appOpsManager);

            Field[] fields = appOpsClass.getDeclaredFields();
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

            for (int i = 0; i < sOpNames.length; i++) {
                String constName = "";
                for (Field field : fields) {
                    if (field.getName().startsWith("OP_") && field.getInt(appOpsManager) == i) {
                        constName = field.getName();
                        break;
                    }
                }

                String modeDes = "unknown";
                int mode = (int) checkOpNoThrowMethod.invoke(appOpsManager, i, Process.myUid(),
                        context.getPackageName());

                switch (mode) {
                    case AppOpsManager.MODE_ALLOWED:
                        modeDes = "allow";
                        break;
                    case AppOpsManager.MODE_IGNORED:
                        modeDes = "ignore";
                        break;
                    case AppOpsManager.MODE_ERRORED:
                        modeDes = "error";
                        break;
                    case AppOpsManager.MODE_DEFAULT:
                        modeDes = "default";
                        break;
                }

            }

        } catch (Throwable e) {
            e.printStackTrace();

        }
    }

    public static boolean shouldEnforceUsageAccessPermission(Context context) {
        return !isUsageAccessPermissionGranted(context);
    }

    /*低版本的手机上没有UsageAccess模块。但从使用的角度来说，低版本不影响我们获取TopApp，遂等同于获取了UsageAccess权限*/
    public static boolean isUsageAccessPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && !hasUsageAccessModule(context)) {
            return true;
        } else {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);

                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                return false;
            }
        }
    }

    @SuppressLint("NewApi")
    public static boolean hasUsageAccessModule(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    public static boolean shouldAskForFloatWindowPermission(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isFloatWindowValid(context);
    }

    public static boolean isFloatWindowValid(Context context) {
//        if ("vivo".equalsIgnoreCase(Build.BRAND) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return isFloatWindowValidForVivo(BaseApplication.getContext());
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return judgeFloatWindowUseAppOps(context);
        }
    }

    private static boolean judgeFloatWindowUseAppOps(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        }

        int mode;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                @SuppressLint("WrongConstant")
                Object object =context.getSystemService("appops");
                if (object == null) {
                    return true;
                }
                Method method = object.getClass().getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                mode = (int) method.invoke(object, 24, Binder.getCallingUid(),context.getPackageName());

            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                assert appOpsManager != null;
                Method method = appOpsManager.getClass().getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                mode = (int) method.invoke(appOpsManager, 24, Binder.getCallingUid(), context.getPackageName());

            } else {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                assert appOpsManager != null;
                mode = appOpsManager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mode == AppOpsManager.MODE_ALLOWED;
        } else {
            return mode == 0;
        }
    }

    /**
     * vivo获取悬浮窗权限状态
     */
    private static boolean isFloatWindowValidForVivo(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = null;
        try {
            cursor = context
                    .getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.getColumnNames();
            if (cursor.moveToFirst()) {
                int currentMode = cursor.getInt(cursor.getColumnIndex("currentlmode"));
                cursor.close();
                return currentMode == 0;
            } else {
                cursor.close();
                return isFloatWindowValidBackupForVivo(context);//查不到会调用isFloatWindowValidBackupForVivo
            }

        } else {
            return isFloatWindowValidBackupForVivo(context);//查不到会调用isFloatWindowValidBackupForVivo
        }
    }

    /**
     * vivo比较新的悬浮窗权限获取方法
     */
    private static boolean isFloatWindowValidBackupForVivo(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentMode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                    return currentMode == 0;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
}
