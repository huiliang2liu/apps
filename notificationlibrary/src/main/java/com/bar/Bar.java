package com.bar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * User：liuhuiliang
 * Date:2019-12-19
 * Time:10:41
 * Descripotion：修改桌面图标上的数字
 */
public interface Bar {
    /**
     * description：修改桌面图标上的数字
     *
     * @param count 显示的数字
     */
    void sendCount(int count);

    /**
     * description：清空桌面图标的数字
     */
    void clearCount();

    class BarBuilder {
        /**
         * description：zuk设备的launcher
         */
        private static final List<String> ZUI = Collections.singletonList("com.zui.launcher");
       /**
        * description：小米设备的launcher
        */
        private static final List<String> XIAO_MI = Arrays.asList(
                "com.miui.miuilite",
                "com.miui.home",
                "com.miui.miuihome",
                "com.miui.miuihome2",
                "com.miui.mihome",
                "com.miui.mihome2"

        );
        /**
         * description：索尼设备的launcher
         */
        private static final List<String> SONY = Collections.singletonList("com.sonyericsson.home");
        /**
         * description：三星设备的launcher
         */
        private static final List<String> SAM_SUNG = Arrays.asList(
                "com.sec.android.app.launcher",
                "com.sec.android.app.twlauncher"
        );
        /**
         * description：oppo设备的launcher
         */
        private static final List<String> OPPO = Collections.singletonList("com.oppo.launcher");
        /**
         * description：vivo设备的launcher
         */
        private static final List<String> VIVO = Collections.singletonList("com.vivo.launcher");
        /**
         * description：nova设备的launcher
         */
        private static final List<String> NOVA = Collections.singletonList("com.teslacoilsw.launcher");
        /**
         * description：htc设备的launcher
         */
        private static final List<String> HTC = Collections.singletonList("com.htc.launcher");
        /**
         * description：lg设备的launcher
         */
        private static final List<String> LGE = Arrays.asList(
                "com.lge.launcher",
                "com.lge.launcher2"
        );
        /**
         * description：华为设备的launcher
         */
        private static final List<String> HUA_WEI = Collections.singletonList("com.huawei.android.launcher");
       /**
        * description：asus设备的launcher
        */
        private static final List<String> ASUS = Collections.singletonList("com.asus.launcher");
        /**
         * description：apex设备的launcher
         */
        private static final List<String> APEX = Collections.singletonList("com.anddoes.launcher");
        /**
         * description：adw设备的launcher
         */
        private static final List<String> ADW = Arrays.asList(
                "org.adw.launcher",
                "org.adwfreak.launcher"
        );
        /**
         * description：everything设备的launcher
         */
        private static final List<String> EVERYTHING = Collections.singletonList("me.everything.launcher");
        private Context mContext;

        public BarBuilder context(Context context) {
            mContext = context;
            return this;
        }

        public Bar build() {
            if (Build.MANUFACTURER.equalsIgnoreCase("HUAWEI"))
                return new HuaweiBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                return new XiaomiBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("OPPO"))
                return new OppoBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("VIVO"))
                return new VivoBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("ZUK"))
                return new ZuiBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("ZTE"))
                return new ZteBar(mContext);
            if (Build.MANUFACTURER.equalsIgnoreCase("LG"))
                return new LgeBar(mContext);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo != null && !resolveInfo.activityInfo.name.toLowerCase().contains("resolver")) {
                String currentHomePackage = resolveInfo.activityInfo.packageName;
                if (HUA_WEI.indexOf(currentHomePackage) >= 0)
                    return new HuaweiBar(mContext);
                if (XIAO_MI.indexOf(currentHomePackage) >= 0)
                    return new XiaomiBar(mContext);
                if (OPPO.indexOf(currentHomePackage) >= 0)
                    return new OppoBar(mContext);
                if (VIVO.indexOf(currentHomePackage) >= 0)
                    return new VivoBar(mContext);
                if (SAM_SUNG.indexOf(currentHomePackage) >= 0)
                    return new SamsungBar(mContext);
                if (SONY.indexOf(currentHomePackage) >= 0)
                    return new SonyBar(mContext);
                if (NOVA.indexOf(currentHomePackage) >= 0)
                    return new NovaBar(mContext);
                if (HTC.indexOf(currentHomePackage) >= 0)
                    return new HtcBar(mContext);
                if (ZUI.indexOf(currentHomePackage) >= 0)
                    return new ZuiBar(mContext);
                if (LGE.indexOf(currentHomePackage) >= 0)
                    return new LgeBar(mContext);
                if (ASUS.indexOf(currentHomePackage) >= 0)
                    return new AsusBar(mContext);
                if (APEX.indexOf(currentHomePackage) >= 0)
                    return new ApexBar(mContext);
                if (ADW.indexOf(currentHomePackage) >= 0)
                    return new AdwBar(mContext);
                if (EVERYTHING.indexOf(currentHomePackage) >= 0)
                    return new EverythingBar(mContext);
            }
            return new DefaultBar(mContext);
        }
    }
}
