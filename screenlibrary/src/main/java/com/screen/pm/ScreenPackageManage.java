/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.screen.pm;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.ComponentInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserHandle;
import android.util.Log;

import com.screen.PackageParser;
import com.screen.entities.ApkEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ScreenPackageManage extends PackageManager {
    private static final String TAG = "ScreenPackageManage";
    private ApkEntity apkEntity;
    private PackageManager packageManager;

    public ScreenPackageManage(ApkEntity apkEntity, PackageManager packageManager) {
        this.apkEntity = apkEntity;
        this.packageManager = packageManager;
    }
    @TargetApi(26)
    @Override
    public boolean canRequestPackageInstalls() {
        return packageManager.canRequestPackageInstalls();
    }
    @TargetApi(21)
    @Override
    public PackageInstaller getPackageInstaller() {
        return packageManager.getPackageInstaller();
    }
    @TargetApi(26)
    @Override
    public void setApplicationCategoryHint(String packageName, int categoryHint) {
        packageManager.setApplicationCategoryHint(packageName, categoryHint);
    }
    @TargetApi(17)
    @Override
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        packageManager.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
    }
    @TargetApi(21)
    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return packageManager.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity);
    }
    @TargetApi(21)
    @Override
    public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
        return packageManager.getUserBadgedIcon(icon, user);
    }
    @TargetApi(21)
    @Override
    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return packageManager.getUserBadgedLabel(label, user);
    }
    @TargetApi(20)
    @Override
    public Drawable getApplicationBanner(ApplicationInfo info) {
        if (info.packageName.equals(apkEntity.mPackageName))
            return apkEntity.mResources.getDrawable(info.banner);
        return packageManager.getApplicationBanner(info);
    }
    @TargetApi(20)
    @Override
    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        if (packageName.equals(apkEntity.mPackageName))
            return apkEntity.mResources.getDrawable(apkEntity.mPackageInfo.applicationInfo.banner);
        return packageManager.getApplicationBanner(packageName);
    }
    @TargetApi(20)
    @Override
    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        if (activityName.getPackageName().equals(apkEntity.mPackageName)) {
            List<PackageParser.Activity> activities = apkEntity.mPackage.activities;
            if (activities != null && activities.size() > 0)
                for (PackageParser.Activity activity : activities) {
                    if (activity.className.equals(activityName.getClassName())) {
                        return apkEntity.mResources.getDrawable(activity.info.banner);
                    }
                }
        }
        return packageManager.getActivityBanner(activityName);
    }
    @TargetApi(20)
    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        ComponentName activityName = intent.getComponent();
        if (activityName.getPackageName().equals(apkEntity.mPackageName)) {
            List<PackageParser.Activity> activities = apkEntity.mPackage.activities;
            if (activities != null && activities.size() > 0)
                for (PackageParser.Activity activity : activities) {
                    if (activity.className.equals(activityName.getClassName())) {
                        return apkEntity.mResources.getDrawable(activity.info.banner);
                    }
                }
        }
        return packageManager.getActivityBanner(intent);
    }
    @TargetApi(19)
    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return packageManager.queryIntentContentProviders(intent, flags);
    }
    @TargetApi(24)
    @Override
    public boolean hasSystemFeature(String name, int version) {
        return packageManager.hasSystemFeature(name, version);
    }
    @TargetApi(26)
    @Override
    public ChangedPackages getChangedPackages(int sequenceNumber) {
        return packageManager.getChangedPackages(sequenceNumber);
    }
    @TargetApi(26)
    @Override
    public List<SharedLibraryInfo> getSharedLibraries(int flags) {
        return packageManager.getSharedLibraries(flags);
    }
    @TargetApi(26)
    @Override
    public void updateInstantAppCookie(byte[] cookie) {
        packageManager.updateInstantAppCookie(cookie);
    }
    @TargetApi(26)
    @Override
    public void clearInstantAppCookie() {
        packageManager.clearInstantAppCookie();
    }
    @TargetApi(26)
    @Override
    public int getInstantAppCookieMaxBytes() {
        return packageManager.getInstantAppCookieMaxBytes();
    }
    @TargetApi(26)
    @Override
    public byte[] getInstantAppCookie() {
        return packageManager.getInstantAppCookie();
    }
    @TargetApi(26)
    @Override
    public boolean isInstantApp(String packageName) {
        return packageManager.isInstantApp(packageName);
    }
    @TargetApi(26)
    @Override
    public boolean isInstantApp() {
        return packageManager.isInstantApp();
    }
    @TargetApi(23)
    @Override
    public boolean isPermissionRevokedByPolicy(String permName, String pkgName) {
        return packageManager.isPermissionRevokedByPolicy(permName, pkgName);
    }
    @TargetApi(26)
    @Override
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int flags) throws NameNotFoundException {
        return packageManager.getPackageInfo(versionedPackage, flags);
    }
    @TargetApi(21)
    @Override
    public Intent getLeanbackLaunchIntentForPackage(String packageName) {
        return packageManager.getLeanbackLaunchIntentForPackage(packageName);
    }

    @Override
    public int[] getPackageGids(String packageName, int flags) throws NameNotFoundException {
        return packageManager.getPackageGids(packageName, flags);
    }

    @Override
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException {
        return packageManager.getPackageUid(packageName, flags);
    }

    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        return packageManager.getPackagesHoldingPermissions(permissions, flags);
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags)
            throws NameNotFoundException {
        if (apkEntity.mPackageName.equals(packageName))
            return apkEntity.mPackageInfo;
        return packageManager.getPackageInfo(packageName, flags);
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] names) {
        return packageManager.currentToCanonicalPackageNames(names);
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] names) {
        return packageManager.canonicalToCurrentPackageNames(names);
    }

    @Override
    public Intent getLaunchIntentForPackage(String packageName) {
        // First see if the package has an INFO activity; the existence of
        // such an activity is implied to be the desired front-door for the
        // overall package (such as if it has multiple launcher entries).
        Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
        intentToResolve.addCategory(Intent.CATEGORY_INFO);
        intentToResolve.setPackage(packageName);
        List<ResolveInfo> ris = queryIntentActivities(intentToResolve, 0);

        // Otherwise, try to find a main launcher activity.
        if (ris == null || ris.size() <= 0) {
            // reuse the intent instance
            intentToResolve.removeCategory(Intent.CATEGORY_INFO);
            intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
            intentToResolve.setPackage(packageName);
            ris = queryIntentActivities(intentToResolve, 0);
        }
        if (ris == null || ris.size() <= 0) {
            return null;
        }
        Intent intent = new Intent(intentToResolve);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(ris.get(0).activityInfo.packageName,
                ris.get(0).activityInfo.name);
        return intent;
    }

    @Override
    public int[] getPackageGids(String packageName)
            throws NameNotFoundException {
        return packageManager.getPackageGids(packageName);
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags)
            throws NameNotFoundException {
        return packageManager.getPermissionInfo(name, flags);
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags)
            throws NameNotFoundException {
        return packageManager.queryPermissionsByGroup(group, flags);
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws NameNotFoundException {
        return packageManager.getPermissionGroupInfo(name, flags);
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        return packageManager.getAllPermissionGroups(flags);
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags)
            throws NameNotFoundException {
        if (apkEntity.mPackageName.equals(packageName))
            return apkEntity.mPackageInfo.applicationInfo;
        return packageManager.getApplicationInfo(packageName, flags);
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName className, int flags)
            throws NameNotFoundException {
        ActivityInfo[] activityInfos = apkEntity.mPackageInfo.activities;
        if (activityInfos != null && activityInfos.length > 0)
            for (ActivityInfo activityInfo : activityInfos)
                if (matchComponent(className, activityInfo.name))
                    return activityInfo;
        return packageManager.getActivityInfo(className, flags);
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName className, int flags)
            throws NameNotFoundException {
        ActivityInfo[] activityInfos = apkEntity.mPackageInfo.receivers;
        if (activityInfos != null && activityInfos.length > 0)
            for (ActivityInfo activityInfo : activityInfos)
                if (matchComponent(className, activityInfo.name))
                    return activityInfo;
        return packageManager.getReceiverInfo(className, flags);
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName className, int flags)
            throws NameNotFoundException {
        ServiceInfo[] serviceInfos = apkEntity.mPackageInfo.services;
        if (serviceInfos != null && serviceInfos.length > 0)
            for (ServiceInfo serviceInfo : serviceInfos)
                if (matchComponent(className, serviceInfo.name))
                    return serviceInfo;
        return packageManager.getServiceInfo(className, flags);
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName className, int flags)
            throws NameNotFoundException {
        ProviderInfo[] providerInfos = apkEntity.mPackageInfo.providers;
        if (providerInfos != null && providerInfos.length > 0)
            for (ProviderInfo providerInfo : providerInfos)
                if (matchComponent(className, providerInfo.name))
                    return providerInfo;

        return packageManager.getProviderInfo(className, flags);
    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        return packageManager.getSystemSharedLibraryNames();
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        return packageManager.getSystemAvailableFeatures();
    }

    @Override
    public boolean hasSystemFeature(String name) {
        return packageManager.hasSystemFeature(name);
    }

    @Override
    public int checkPermission(String permName, String pkgName) {
        return packageManager.checkPermission(permName, pkgName);
    }

    @Override
    public boolean addPermission(PermissionInfo info) {
        return packageManager.addPermission(info);
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo info) {
        return packageManager.addPermissionAsync(info);
    }

    @Override
    public void removePermission(String name) {
        packageManager.removePermission(name);
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) {
        return packageManager.checkSignatures(pkg1, pkg2);
    }

    @Override
    public int checkSignatures(int uid1, int uid2) {
        return packageManager.checkSignatures(uid1, uid2);
    }

    @Override
    public String[] getPackagesForUid(int uid) {
        return packageManager.getPackagesForUid(uid);
    }

    @Override
    public String getNameForUid(int uid) {
        return packageManager.getNameForUid(uid);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<PackageInfo> getInstalledPackages(int flags) {
        return packageManager.getInstalledPackages(flags);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        return packageManager.getInstalledApplications(flags);
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        List<PackageParser.Activity> activities = apkEntity.mPackage.activities;
        if (activities != null && activities.size() > 0) {
            ComponentName componentName = intent.getComponent();
            for (PackageParser.Activity activity : activities)
                if (componentName != null) {
                    if (matchComponent(componentName, activity.className)) {
                        ResolveInfo resolveInfo = new ResolveInfo();
                        resolveInfo.activityInfo = activity.info;
                        resolveInfo.filter = activity.getIntentInfo();
                        return resolveInfo;
                    }
                } else {
                    List<PackageParser.ActivityIntentInfo> activityIntentInfos = activity.intents;
                    if (activityIntentInfos == null || activityIntentInfos.size() <= 0)
                        continue;
                    for (PackageParser.ActivityIntentInfo activityIntentInfo : activityIntentInfos) {
                        if (match(intent, activityIntentInfo)) {
                            ResolveInfo resolveInfo = new ResolveInfo();
                            resolveInfo.activityInfo = activity.info;
                            resolveInfo.filter = activityIntentInfo;
                            return resolveInfo;
                        }
                    }
                }
        }
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent,
                                                   int flags) {
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        List<ResolveInfo> m = packageManager.queryIntentActivities(intent, flags);
        ResolveInfo resolveInfo = resolveActivity(intent, flags);
        resolveInfos.addAll(m);
        if (m.indexOf(resolveInfo) < 0) {
            resolveInfos.add(resolveInfo);
        }
        return resolveInfos;
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller, Intent[] specifics, Intent intent,
            int flags) {
        return packageManager.queryIntentActivityOptions(caller, specifics, intent, flags);
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        List<PackageParser.Activity> activities = apkEntity.mPackage.receivers;
        PackageParser.Activity t = null;
        IntentFilter intentFilter = null;
        if (activities != null && activities.size() > 0) {
            ComponentName componentName = intent.getComponent();
            for (PackageParser.Activity activity : activities) {
                if (componentName != null) {
                    if (matchComponent(componentName, activity.className))
                        t = activity;
                } else {
                    List<PackageParser.ActivityIntentInfo> intentInfos = activity.intents;
                    if (intentInfos != null && intentInfos.size() > 0) {
                        for (PackageParser.ActivityIntentInfo intentInfo : intentInfos) {
                            if (match(intent, intentInfo)) {
                                t = activity;
                                intentFilter = intentInfo;
                                break;
                            }
                        }
                    }
                }
                if (t != null)
                    break;
            }

        }
        if (t == null)
            return packageManager.queryBroadcastReceivers(intent, flags);
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        resolveInfos.addAll(packageManager.queryBroadcastReceivers(intent, flags));
        ResolveInfo e = new ResolveInfo();
        e.activityInfo = t.info;
        if (intentFilter == null)
            e.filter = t.getIntentInfo();
        else
            e.filter = intentFilter;
        resolveInfos.add(e);
        return resolveInfos;
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags) {
        List<PackageParser.Service> services = apkEntity.mPackage.services;
        if (services != null && services.size() > 0) {
            ComponentName componentName = intent.getComponent();
            for (PackageParser.Service service : services) {
                if (componentName != null) {
                    if (matchComponent(componentName, service.className)) {
                        ResolveInfo resolveInfo = new ResolveInfo();
                        resolveInfo.serviceInfo = service.info;
                        resolveInfo.filter = service.getIntentInfo();
                        return resolveInfo;
                    }
                } else {
                    List<PackageParser.ServiceIntentInfo> intentInfos = service.intents;
                    if (intentInfos != null && intentInfos.size() > 0) {
                        for (PackageParser.ServiceIntentInfo intentInfo : intentInfos) {
                            if (match(intent, intentInfo)) {
                                ResolveInfo resolveInfo = new ResolveInfo();
                                resolveInfo.serviceInfo = service.info;
                                resolveInfo.filter = service.getIntentInfo();
                                return resolveInfo;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        ResolveInfo resolveInfo = resolveService(intent, flags);
        List<ResolveInfo> p = packageManager.queryIntentServices(intent, flags);
        if (p.indexOf(resolveInfo) > 0)
            return p;
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        resolveInfos.addAll(p);
        resolveInfos.add(resolveInfo);
        return resolveInfos;
    }

    @Override
    public ProviderInfo resolveContentProvider(String name,
                                               int flags) {
        ProviderInfo[] providerInfos = apkEntity.mPackageInfo.providers;
        Log.d(TAG, name);
        if (providerInfos != null && providerInfos.length > 0) {
            for (ProviderInfo providerInfo : providerInfos)
                if (providerInfo.authority.equals(name))
                    return providerInfo;
        } else
            Log.d(TAG, String.format("%s,not provider", apkEntity.mPackageName));
        return packageManager.resolveContentProvider(name, flags);
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName,
                                                    int uid, int flags) {
        return packageManager.queryContentProviders(processName, uid, flags);
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(
            ComponentName className, int flags)
            throws NameNotFoundException {
        return packageManager.getInstrumentationInfo(className, flags);
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(
            String targetPackage, int flags) {
        return packageManager.queryInstrumentation(targetPackage, flags);
    }

    @Override
    public Drawable getDrawable(String packageName, int resid,
                                ApplicationInfo appInfo) {
        return packageManager.getDrawable(packageName, resid, appInfo);
    }

    @Override
    public Drawable getActivityIcon(ComponentName activityName)
            throws NameNotFoundException {
        return getActivityInfo(activityName, 0).loadIcon(this);
    }

    @Override
    public Drawable getActivityIcon(Intent intent)
            throws NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityIcon(intent.getComponent());
        }

        ResolveInfo info = resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info != null) {
            return info.activityInfo.loadIcon(this);
        }

        throw new NameNotFoundException(intent.toUri(0));
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        return packageManager.getDefaultActivityIcon();
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo info) {
        return info.loadIcon(this);
    }

    @Override
    public Drawable getApplicationIcon(String packageName)
            throws NameNotFoundException {
        return getApplicationIcon(getApplicationInfo(packageName, 0));
    }

    @Override
    public Drawable getActivityLogo(ComponentName activityName)
            throws NameNotFoundException {
        return getActivityInfo(activityName, 0).loadLogo(this);
    }

    @Override
    public Drawable getActivityLogo(Intent intent)
            throws NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityLogo(intent.getComponent());
        }

        ResolveInfo info = resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info != null) {
            return info.activityInfo.loadLogo(this);
        }

        throw new NameNotFoundException(intent.toUri(0));
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo info) {
        return info.loadLogo(this);
    }

    @Override
    public Drawable getApplicationLogo(String packageName)
            throws NameNotFoundException {
        return getApplicationLogo(getApplicationInfo(packageName, 0));
    }

    @Override
    public Resources getResourcesForActivity(
            ComponentName activityName) throws NameNotFoundException {
        if (activityName.getPackageName().equals(apkEntity.mPackageName))
            return apkEntity.mResources;
        return getResourcesForApplication(
                getActivityInfo(activityName, 0).applicationInfo);
    }

    @Override
    public Resources getResourcesForApplication(
            ApplicationInfo app) throws NameNotFoundException {
        if (app.packageName.equals(apkEntity.mPackageName))
            return apkEntity.mResources;
        return packageManager.getResourcesForApplication(app);
    }

    @Override
    public Resources getResourcesForApplication(
            String appPackageName) throws NameNotFoundException {
        return getResourcesForApplication(
                getApplicationInfo(appPackageName, 0));
    }


    @Override
    public boolean isSafeMode() {
        return packageManager.isSafeMode();
    }

    @Override
    public CharSequence getText(String packageName, int resid,
                                ApplicationInfo appInfo) {
        if (packageName.equals(apkEntity.mPackageName)) {
            return apkEntity.mResources.getText(resid);
//            return packageManager.getText(packageName, resid, apkEntity.mPackageInfo.applicationInfo);
        }
        return packageManager.getText(packageName, resid, appInfo);
    }

    @Override
    public XmlResourceParser getXml(String packageName, int resid,
                                    ApplicationInfo appInfo) {
        if (appInfo == null) {
            try {
                appInfo = getApplicationInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                return null;
            }
        }
        try {
            Resources r = getResourcesForApplication(appInfo);
            return r.getXml(resid);
        } catch (RuntimeException e) {
            // If an exception was thrown, fall through to return
            // default icon.
            Log.w("PackageManager", "Failure retrieving xml 0x"
                    + Integer.toHexString(resid) + " in package "
                    + packageName, e);
        } catch (NameNotFoundException e) {
            Log.w("PackageManager", "Failure retrieving resources for "
                    + appInfo.packageName);
        }
        return null;
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return info.loadLabel(this);
    }


    @Override
    public void verifyPendingInstall(int id, int response) {
        packageManager.verifyPendingInstall(id, response);
    }

    @Override
    public void setInstallerPackageName(String targetPackage,
                                        String installerPackageName) {
        packageManager.setInstallerPackageName(targetPackage, installerPackageName);
    }

    @Override
    public String getInstallerPackageName(String packageName) {
        return packageManager.getInstallerPackageName(packageName);
    }


    @Override
    public void addPackageToPreferred(String packageName) {
        packageManager.addPackageToPreferred(packageName);
    }

    @Override
    public void removePackageFromPreferred(String packageName) {
        packageManager.removePackageFromPreferred(packageName);
    }

    @Override
    public List<PackageInfo> getPreferredPackages(int flags) {
        return packageManager.getPreferredPackages(flags);
    }

    @Override
    public void addPreferredActivity(IntentFilter filter,
                                     int match, ComponentName[] set, ComponentName activity) {
        packageManager.addPreferredActivity(filter, match, set, activity);
    }

    @Override
    public void clearPackagePreferredActivities(String packageName) {
        packageManager.clearPackagePreferredActivities(packageName);
    }

    @Override
    public int getPreferredActivities(List<IntentFilter> outFilters,
                                      List<ComponentName> outActivities, String packageName) {
        return packageManager.getPreferredActivities(outFilters, outActivities, packageName);
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName,
                                           int newState, int flags) {
        packageManager.setComponentEnabledSetting(componentName, newState, flags);
    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        return packageManager.getComponentEnabledSetting(componentName);
    }

    @Override
    public void setApplicationEnabledSetting(String packageName,
                                             int newState, int flags) {
        packageManager.setApplicationEnabledSetting(packageName, newState, flags);
    }

    @Override
    public int getApplicationEnabledSetting(String packageName) {
        return packageManager.getApplicationEnabledSetting(packageName);
    }

    protected boolean match(Intent intent, PackageParser.IntentInfo intentInfo) {
        if (matchCategories(intent, intentInfo)) {
            if (matchAction(intent, intentInfo)) {
                if (matchDate(intent, intentInfo)) {
                    return true;
                } else {
                    Log.d(TAG, "matchDate error");
                }
            } else {
                Log.d(TAG, "matchAction error");
            }
        } else {
            Log.d(TAG, "matchCategories error");
        }
        return false;
    }

    protected boolean matchComponent(ComponentName componentName, String name) {
        if (name.equals(componentName.getClassName()))
            return true;
        return false;
    }

    private boolean matchAction(Intent intent, PackageParser.IntentInfo intentInfo) {
        String action = intent.getAction();
        if (action != null && !action.isEmpty())
            if (!intentInfo.hasAction(action))
                return false;
        return true;
    }

    private boolean matchDate(Intent intent, PackageParser.IntentInfo intentInfo) {

        Uri data = intent.getData();
        if (data != null)
            if (!intentInfo.hasDataAuthority(data)) {
                return false;
            }
        String type = intent.getType();
        if (type != null && !type.isEmpty())
            if (!intentInfo.hasDataType(type)) {
                Log.e(TAG, "type 匹配失败");
                return false;
            }
        Log.e(TAG, "=======");
        return true;
    }

    private boolean matchCategories(Intent intent, PackageParser.IntentInfo intentInfo) {
        Set<String> intentSet = intent.getCategories();
        if (intentSet != null && intentSet.size() > 0)
            for (String set : intentSet) {
                if (!intentInfo.hasCategory(set))
                    return false;
            }
        return true;
    }
}
