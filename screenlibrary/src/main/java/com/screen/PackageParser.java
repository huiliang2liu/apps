/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.screen;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PathPermission;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:17
 * Descripotion：外部包解析
 * {@hide}
 */
public class PackageParser {
    private static final boolean DEBUG_JAR = false;
    private static final boolean DEBUG_PARSER = false;
    private static final boolean DEBUG_BACKUP = false;
    private static final String TAG = "PackageParser";
    private static Class internalStyleable;

    static {
        try {
            internalStyleable = Class.forName("com.android.internal.R$styleable");
        } catch (Exception e) {
            Log.e(TAG, "没找到internalStyleable类");
        }
    }

    /**
     * File name in an APK for the Android manifest.
     */
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";

    /**
     * @hide
     */
    public static class NewPermissionInfo {
        public final String name;
        public final int sdkVersion;
        public final int fileVersion;

        public NewPermissionInfo(String name, int sdkVersion, int fileVersion) {
            this.name = name;
            this.sdkVersion = sdkVersion;
            this.fileVersion = fileVersion;
        }
    }

    /**
     * List of new permissions that have been added since 1.0.
     * NOTE: These must be declared in SDK version order, with permissions
     * added to older SDKs appearing before those added to newer SDKs.
     *
     * @hide
     */
    public static final PackageParser.NewPermissionInfo NEW_PERMISSIONS[] =
            new PackageParser.NewPermissionInfo[]{
                    new PackageParser.NewPermissionInfo(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.os.Build.VERSION_CODES.DONUT, 0),
                    new PackageParser.NewPermissionInfo(android.Manifest.permission.READ_PHONE_STATE,
                            android.os.Build.VERSION_CODES.DONUT, 0)
            };

    private String mArchiveSourcePath;
    private String[] mSeparateProcesses;
    private boolean mOnlyCoreApps;
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final String SDK_CODENAME = "REL".equals(Build.VERSION.CODENAME)
            ? null : Build.VERSION.CODENAME;

    private int mParseError = 1;

    private static final Object mSync = new Object();
    private static WeakReference<byte[]> mReadBuffer;

    private static boolean sCompatibilityModeEnabled = true;
    private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
    private static final String ANDROID_RESOURCES
            = "http://schemas.android.com/apk/res/android";
    private static String value(XmlPullParser pullParser,String name){
      return pullParser.getAttributeValue(ANDROID_RESOURCES,name);
    }
    static class ParsePackageItemArgs {
        final Package owner;
        final String[] outError;
        final int nameRes;
        final int labelRes;
        final int iconRes;
        final int logoRes;
        final XmlPullParser xmlPullParser;

        String tag;
        TypedArray sa;

        ParsePackageItemArgs(Package _owner, String[] _outError,
                             int _nameRes, int _labelRes, int _iconRes, int _logoRes,XmlPullParser xmlPullParser) {
            owner = _owner;
            outError = _outError;
            nameRes = _nameRes;
            labelRes = _labelRes;
            iconRes = _iconRes;
            logoRes = _logoRes;
            this.xmlPullParser=xmlPullParser;
        }
    }

    static class ParseComponentArgs extends ParsePackageItemArgs {
        final String[] sepProcesses;
        final int processRes;
        final int descriptionRes;
        final int enabledRes;
        int flags;

        ParseComponentArgs(Package _owner, String[] _outError,
                           int _nameRes, int _labelRes, int _iconRes, int _logoRes,
                           String[] _sepProcesses, int _processRes,
                           int _descriptionRes, int _enabledRes,XmlPullParser xmlPullParser) {
            super(_owner, _outError, _nameRes, _labelRes, _iconRes, _logoRes,xmlPullParser);
            sepProcesses = _sepProcesses;
            processRes = _processRes;
            descriptionRes = _descriptionRes;
            enabledRes = _enabledRes;
        }
    }

    /* Light weight package info.
     * @hide
     */
    public static class PackageLite {
        public final String packageName;
        public final int installLocation;

        public PackageLite(String packageName, int installLocation) {
            this.packageName = packageName;
            this.installLocation = installLocation;
        }
    }

    private ParsePackageItemArgs mParseInstrumentationArgs;
    private ParseComponentArgs mParseActivityArgs;
    private ParseComponentArgs mParseActivityAliasArgs;
    private ParseComponentArgs mParseServiceArgs;
    private ParseComponentArgs mParseProviderArgs;

    /**
     * If set to true, we will only allow package files that exactly match
     * the DTD.  Otherwise, we try to get as much from the package as we
     * can without failing.  This should normally be set to false, to
     * support extensions to the DTD in future versions.
     */
    private static final boolean RIGID_PARSER = false;


    public PackageParser(String archiveSourcePath) {
        mArchiveSourcePath = archiveSourcePath;
    }

    public void setSeparateProcesses(String[] procs) {
        mSeparateProcesses = procs;
    }

    public void setOnlyCoreApps(boolean onlyCoreApps) {
        mOnlyCoreApps = onlyCoreApps;
    }

    private static final boolean isPackageFilename(String name) {
        return name.endsWith(".apk");
    }

    public static PackageInfo generatePackageInfo(PackageParser.Package p,
                                                  int gids[], long firstInstallTime, long lastUpdateTime) {

        PackageInfo pi = new PackageInfo();
        pi.packageName = p.packageName;
        pi.versionCode = p.mVersionCode;
        pi.versionName = p.mVersionName;
        pi.sharedUserId = p.mSharedUserId;
        pi.sharedUserLabel = p.mSharedUserLabel;
        pi.applicationInfo = generateApplicationInfo(p);
        pi.installLocation = p.installLocation;
        pi.firstInstallTime = firstInstallTime;
        pi.lastUpdateTime = lastUpdateTime;
        pi.gids = gids;
        int N = p.configPreferences.size();
        if (N > 0) {
            pi.configPreferences = new ConfigurationInfo[N];
            p.configPreferences.toArray(pi.configPreferences);
        }
        N = p.reqFeatures != null ? p.reqFeatures.size() : 0;
        if (N > 0) {
            pi.reqFeatures = new FeatureInfo[N];
            p.reqFeatures.toArray(pi.reqFeatures);
        }
        N = p.activities.size();
        if (N > 0) {
            pi.activities = new ActivityInfo[N];
            for (int i = 0, j = 0; i < N; i++) {
                final Activity activity = p.activities.get(i);
                if (activity.info.enabled) {
                    pi.activities[j++] = generateActivityInfo(p.activities.get(i));
                }
            }
        }
        N = p.receivers.size();
        if (N > 0) {
            pi.receivers = new ActivityInfo[N];
            for (int i = 0, j = 0; i < N; i++) {
                final Activity activity = p.receivers.get(i);
                if (activity.info.enabled) {
                    pi.receivers[j++] = generateActivityInfo(p.receivers.get(i));
                }
            }
        }
        N = p.services.size();
        if (N > 0) {
            pi.services = new ServiceInfo[N];
            for (int i = 0, j = 0; i < N; i++) {
                final Service service = p.services.get(i);
                if (service.info.enabled) {
                    pi.services[j++] = generateServiceInfo(p.services.get(i));
                }
            }
        }
        N = p.providers.size();
        if (N > 0) {
            pi.providers = new ProviderInfo[N];
            for (int i = 0, j = 0; i < N; i++) {
                final Provider provider = p.providers.get(i);
                if (provider.info.enabled) {
                    pi.providers[j++] = generateProviderInfo(p.providers.get(i));
                }
            }
        }
        N = p.instrumentation.size();
        if (N > 0) {
            pi.instrumentation = new InstrumentationInfo[N];
            for (int i = 0; i < N; i++) {
                pi.instrumentation[i] = generateInstrumentationInfo(
                        p.instrumentation.get(i));
            }
        }
        N = p.permissions.size();
        if (N > 0) {
            pi.permissions = new PermissionInfo[N];
            for (int i = 0; i < N; i++) {
                pi.permissions[i] = generatePermissionInfo(p.permissions.get(i));
            }
        }
        N = p.requestedPermissions.size();
        if (N > 0) {
            pi.requestedPermissions = new String[N];
            for (int i = 0; i < N; i++) {
                pi.requestedPermissions[i] = p.requestedPermissions.get(i);
            }
        }
        N = (p.mSignatures != null) ? p.mSignatures.length : 0;
        if (N > 0) {
            pi.signatures = new Signature[N];
            System.arraycopy(p.mSignatures, 0, pi.signatures, 0, N);
        }
        return pi;
    }

    private Certificate[] loadCertificates(JarFile jarFile, JarEntry je,
                                           byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            InputStream is = new BufferedInputStream(jarFile.getInputStream(je));
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {

        } catch (RuntimeException e) {

        }
        return null;
    }

    public final static int PARSE_IS_SYSTEM = 1 << 0;
    public final static int PARSE_CHATTY = 1 << 1;
    public final static int PARSE_MUST_BE_APK = 1 << 2;
    public final static int PARSE_IGNORE_PROCESSES = 1 << 3;
    public final static int PARSE_FORWARD_LOCK = 1 << 4;
    public final static int PARSE_ON_SDCARD = 1 << 5;
    public final static int PARSE_IS_SYSTEM_DIR = 1 << 6;

    public int getParseError() {
        return mParseError;
    }

    public Package parsePackage(File sourceFile, String destCodePath, AssetManager assmgr, Resources res, int cookie) {
        mParseError = 1;

        mArchiveSourcePath = sourceFile.getPath();
        if (!sourceFile.isFile()) {
            mParseError = -100;
            return null;
        }
        XmlResourceParser parser = null;
        boolean assetError = true;
        try {
            if (cookie != 0) {
                parser = assmgr.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
                assetError = false;
            } else {
            }
        } catch (Exception e) {
        }
        if (assetError) {
            mParseError = -101;
            return null;
        }
        String[] errorText = new String[1];
        Package pkg = null;
        try {
            pkg = parsePackage(res, parser, errorText);
        } catch (Exception e) {
            mParseError = -102;
        }


        if (pkg == null) {
            if (!mOnlyCoreApps || mParseError != 1) {
                if (mParseError == 1) {
                    mParseError = -108;
                }
            }
            parser.close();
            Log.e(TAG, "error");
            return null;
        }

        parser.close();
        pkg.mPath = destCodePath;
        pkg.mScanPath = mArchiveSourcePath;
        pkg.mSignatures = null;

        return pkg;
    }

    public boolean collectCertificates(Package pkg, int flags) {
        pkg.mSignatures = null;

        WeakReference<byte[]> readBufferRef;
        byte[] readBuffer = null;
        synchronized (mSync) {
            readBufferRef = mReadBuffer;
            if (readBufferRef != null) {
                mReadBuffer = null;
                readBuffer = readBufferRef.get();
            }
            if (readBuffer == null) {
                readBuffer = new byte[8192];
                readBufferRef = new WeakReference<byte[]>(readBuffer);
            }
        }

        try {
            JarFile jarFile = new JarFile(mArchiveSourcePath);

            Certificate[] certs = null;

            if ((flags & PARSE_IS_SYSTEM) != 0) {
                JarEntry jarEntry = jarFile.getJarEntry(ANDROID_MANIFEST_FILENAME);
                certs = loadCertificates(jarFile, jarEntry, readBuffer);
                if (certs == null) {
                    jarFile.close();
                    mParseError = -103;
                    return false;
                }
            } else {
                Enumeration<JarEntry> entries = jarFile.entries();
                final Manifest manifest = jarFile.getManifest();
                while (entries.hasMoreElements()) {
                    final JarEntry je = entries.nextElement();
                    if (je.isDirectory()) continue;

                    final String name = je.getName();

                    if (name.startsWith("META-INF/"))
                        continue;
//                    if (ANDROID_MANIFEST_FILENAME.equals(name)) {
//                        final Attributes attributes = manifest.getAttributes(name);
//                        pkg.manifestDigest = ManifestDigest.fromAttributes(attributes);
//                    }
                    final Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);


                    if (localCerts == null) {
                        jarFile.close();
                        mParseError = -103;
                        return false;
                    } else if (certs == null) {
                        certs = localCerts;
                    } else {
                        // Ensure all certificates match.
                        for (int i = 0; i < certs.length; i++) {
                            boolean found = false;
                            for (int j = 0; j < localCerts.length; j++) {
                                if (certs[i] != null &&
                                        certs[i].equals(localCerts[j])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found || certs.length != localCerts.length) {
                                jarFile.close();
                                mParseError = -104;
                                return false;
                            }
                        }
                    }
                }
            }
            jarFile.close();

            synchronized (mSync) {
                mReadBuffer = readBufferRef;
            }

            if (certs != null && certs.length > 0) {
                final int N = certs.length;
                pkg.mSignatures = new Signature[certs.length];
                for (int i = 0; i < N; i++) {
                    pkg.mSignatures[i] = new Signature(
                            certs[i].getEncoded());
                }
            } else {
                mParseError = -103;
                return false;
            }
        } catch (CertificateEncodingException e) {
            mParseError = -105;
            return false;
        } catch (IOException e) {
            mParseError = -105;
            return false;
        } catch (RuntimeException e) {
            mParseError = -102;
            return false;
        }

        return true;
    }


    public static PackageLite parsePackageLite(String packageFilePath, AssetManager assmgr, Resources res, int cookie) {
        final XmlResourceParser parser;
        try {
            if (cookie == 0) {
                return null;
            }

            final DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            res = new Resources(assmgr, metrics, null);
            parser = assmgr.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
        } catch (Exception e) {
            if (assmgr != null) assmgr.close();
            return null;
        }

        final AttributeSet attrs = parser;
        final String errors[] = new String[1];
        PackageLite packageLite = null;
        try {
            packageLite = parsePackageLite(res, parser, attrs, errors);
        } catch (IOException e) {
        } catch (XmlPullParserException e) {
        } finally {
            if (parser != null) parser.close();
            if (assmgr != null) assmgr.close();
        }
        if (packageLite == null) {
            return null;
        }
        return packageLite;
    }

    private static String validateName(String name, boolean requiresSeparator) {
        final int N = name.length();
        boolean hasSep = false;
        boolean front = true;
        for (int i = 0; i < N; i++) {
            final char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                front = false;
                continue;
            }
            if (!front) {
                if ((c >= '0' && c <= '9') || c == '_') {
                    continue;
                }
            }
            if (c == '.') {
                hasSep = true;
                front = true;
                continue;
            }
            return "bad character '" + c + "'";
        }
        return hasSep || !requiresSeparator
                ? null : "must have at least one '.' separator";
    }

    private static String parsePackageName(XmlPullParser parser,
                                           AttributeSet attrs, String[] outError)
            throws IOException, XmlPullParserException {

        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT) {
            ;
        }

        if (type != XmlPullParser.START_TAG) {
            outError[0] = "No start tag found";
            return null;
        }
        if (!parser.getName().equals("manifest")) {
            outError[0] = "No <manifest> tag";
            return null;
        }
        String pkgName = attrs.getAttributeValue(null, "package");
        if (pkgName == null || pkgName.length() == 0) {
            outError[0] = "<manifest> does not specify package";
            return null;
        }
        String nameError = validateName(pkgName, true);
        if (nameError != null && !"android".equals(pkgName)) {
            outError[0] = "<manifest> specifies bad package name \""
                    + pkgName + "\": " + nameError;
            return null;
        }

        return pkgName.intern();
    }

    private static PackageLite parsePackageLite(Resources res, XmlPullParser parser,
                                                AttributeSet attrs, String[] outError) throws IOException,
            XmlPullParserException {

        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT) {
            ;
        }

        if (type != XmlPullParser.START_TAG) {
            outError[0] = "No start tag found";
            return null;
        }
        if (!parser.getName().equals("manifest")) {
            outError[0] = "No <manifest> tag";
            return null;
        }
        String pkgName = attrs.getAttributeValue(null, "package");
        if (pkgName == null || pkgName.length() == 0) {
            outError[0] = "<manifest> does not specify package";
            return null;
        }
        String nameError = validateName(pkgName, true);
        if (nameError != null && !"android".equals(pkgName)) {
            outError[0] = "<manifest> specifies bad package name \""
                    + pkgName + "\": " + nameError;
            return null;
        }
        int installLocation = PARSE_DEFAULT_INSTALL_LOCATION;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attr = attrs.getAttributeName(i);
            if (attr.equals("installLocation")) {
                installLocation = attrs.getAttributeIntValue(i,
                        PARSE_DEFAULT_INSTALL_LOCATION);
                break;
            }
        }
        final int searchDepth = parser.getDepth() + 1;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() >= searchDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            if (parser.getDepth() == searchDepth && "package-verifier".equals(parser.getName())) {
            }
        }

        return new PackageLite(pkgName.intern(), installLocation);
    }

    /**
     * Temporary.
     */
    static public Signature stringToSignature(String str) {
        final int N = str.length();
        byte[] sig = new byte[N];
        for (int i = 0; i < N; i++) {
            sig[i] = (byte) str.charAt(i);
        }
        return new Signature(sig);
    }

    private Package parsePackage(
            Resources res, XmlResourceParser parser, String[] outError)
            throws XmlPullParserException, IOException {
        AttributeSet attrs = parser;
        Log.e(TAG, "parse");
        mParseInstrumentationArgs = null;
        mParseActivityArgs = null;
        mParseServiceArgs = null;
        mParseProviderArgs = null;

        String pkgName = parsePackageName(parser, attrs, outError);
        if (pkgName == null) {
            mParseError = -106;
            Log.e(TAG, "pagName is empty");
            return null;
        }
        int type;

        if (mOnlyCoreApps) {
            boolean core = attrs.getAttributeBooleanValue(null, "coreApp", false);
            if (!core) {
                mParseError = 1;
                Log.e(TAG, "core is false");
                return null;
            }
        }
        Log.e(TAG, "parse package");
        final Package pkg = new Package(pkgName);
        boolean foundApp = false;

        TypedArray sa = null;
        try {
            sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifest").get(null));
            try {
                pkg.mVersionCode = sa.getInteger(
                        internalStyleable.getField("AndroidManifest_versionCode").getInt(null), 0);
            } catch (Exception e) {
            }
            try {
                pkg.mVersionName = sa.getString(internalStyleable.getField("AndroidManifest_versionName").getInt(null));
                if (pkg.mVersionName != null) {
                    pkg.mVersionName = pkg.mVersionName.intern();
                }
            } catch (Exception e) {
            }
            try {
                pkg.installLocation = sa.getInteger(
                        internalStyleable.getField("AndroidManifest_installLocation").getInt(null),
                        PARSE_DEFAULT_INSTALL_LOCATION);
            } catch (Exception e) {
            }
            sa.recycle();
        } catch (Exception e) {
            Log.e(TAG, "解析资源错误", e);
        }

        int supportsSmallScreens = 1;
        int supportsNormalScreens = 1;
        int supportsLargeScreens = 1;
        int supportsXLargeScreens = 1;
        int resizeable = 1;
        int anyDensity = 1;

        int outerDepth = parser.getDepth();
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            String tagName = parser.getName();
            if (tagName.equals("application")) {
                if (foundApp) {
                    if (RIGID_PARSER) {
                        outError[0] = "<manifest> has more than one <application>";
                        Log.e(TAG, "<manifest> has more than one <application>");
                        mParseError = -108;
                        return null;
                    } else {
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                }

                foundApp = true;
                if (!parseApplication(pkg, res, parser, attrs, outError)) {
                    Log.e(TAG, "parse application error");
                    return null;
                }
            } else if (tagName.equals("permission-group")) {
                if (parsePermissionGroup(pkg, res, parser, attrs, outError) == null) {
                    Log.e(TAG, "parse permissionGroup error");
                    return null;
                }
            } else if (tagName.equals("permission")) {
                if (parsePermission(pkg, res, parser, attrs, outError) == null) {
                    Log.e(TAG, "parse permission error");
                    return null;
                }
            } else if (tagName.equals("permission-tree")) {
                if (parsePermissionTree(pkg, res, parser, attrs, outError) == null) {
                    Log.e(TAG, "parse permissionTree error");
                    return null;
                }
            } else if (tagName.equals("uses-permission")) {
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestUsesPermission").get(null));
                    String name = null;
                    try {
                        name = sa.getNonResourceString(
                                internalStyleable.getField("AndroidManifestUsesPermission_name").getInt(null));
                    } catch (Exception e) {
                    }
                    sa.recycle();
                    if (name != null && !pkg.requestedPermissions.contains(name)) {
                        pkg.requestedPermissions.add(name.intern());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestUsesPermission错误", e);
                }
                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("uses-configuration")) {
                ConfigurationInfo cPref = new ConfigurationInfo();
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestUsesConfiguration").get(null));
                    try {
                        cPref.reqTouchScreen = sa.getInt(
                                internalStyleable.getField("AndroidManifestUsesConfiguration_reqTouchScreen").getInt(null),
                                Configuration.TOUCHSCREEN_UNDEFINED);
                    } catch (Exception e) {
                    }
                    try {
                        cPref.reqKeyboardType = sa.getInt(
                                internalStyleable.getField("AndroidManifestUsesConfiguration_reqKeyboardType").getInt(null),
                                Configuration.KEYBOARD_UNDEFINED);
                    } catch (Exception e) {
                    }
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestUsesConfiguration_reqHardKeyboard").getInt(null),
                                false)) {
                            cPref.reqInputFeatures |= ConfigurationInfo.INPUT_FEATURE_HARD_KEYBOARD;
                        }
                    } catch (Exception e) {
                    }
                    try {
                        cPref.reqNavigation = sa.getInt(
                                internalStyleable.getField("AndroidManifestUsesConfiguration_reqNavigation").getInt(null),
                                Configuration.NAVIGATION_UNDEFINED);
                    } catch (Exception e) {
                    }
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestUsesConfiguration_reqFiveWayNav").getInt(null),
                                false)) {
                            cPref.reqInputFeatures |= ConfigurationInfo.INPUT_FEATURE_FIVE_WAY_NAV;
                        }
                    } catch (Exception e) {
                    }
                    sa.recycle();
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestUsesConfiguration错误", e);
                }
                pkg.configPreferences.add(cPref);

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("uses-feature")) {
                FeatureInfo fi = new FeatureInfo();
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestUsesFeature").get(null));
                    // Note: don't allow this value to be a reference to a resource
                    // that may change.
                    try {
                        fi.name = sa.getNonResourceString(
                                internalStyleable.getField("AndroidManifestUsesFeature_name").getInt(null));
                    } catch (Exception e) {
                    }
                    if (fi.name == null) {
                        try {
                            fi.reqGlEsVersion = sa.getInt(
                                    internalStyleable.getField("AndroidManifestUsesFeature_glEsVersion").getInt(null),
                                    FeatureInfo.GL_ES_VERSION_UNDEFINED);
                        } catch (Exception e) {
                        }
                    }
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestUsesFeature_required").getInt(null),
                                true)) {
                            fi.flags |= FeatureInfo.FLAG_REQUIRED;
                        }
                    } catch (Exception e) {
                    }
                    sa.recycle();
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestUsesFeature错误", e);
                }
                if (pkg.reqFeatures == null) {
                    pkg.reqFeatures = new ArrayList<FeatureInfo>();
                }
                pkg.reqFeatures.add(fi);

                if (fi.name == null) {
                    ConfigurationInfo cPref = new ConfigurationInfo();
                    cPref.reqGlEsVersion = fi.reqGlEsVersion;
                    pkg.configPreferences.add(cPref);
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("uses-sdk")) {
                if (SDK_VERSION > 0) {
                    try {
                        sa = res.obtainAttributes(attrs,
                                (int[]) internalStyleable.getField("AndroidManifestUsesSdk").get(null));

                        int minVers = 0;
                        String minCode = null;
                        int targetVers = 0;
                        String targetCode = null;

                        TypedValue val = null;
                        try {
                            val = sa.peekValue(
                                    internalStyleable.getField("AndroidManifestUsesSdk_minSdkVersion").getInt(null));
                        } catch (Exception e) {
                        }
                        if (val != null) {
                            if (val.type == TypedValue.TYPE_STRING && val.string != null) {
                                targetCode = minCode = val.string.toString();
                            } else {
                                targetVers = minVers = val.data;
                            }
                        }

                        try {
                            val = sa.peekValue(
                                    internalStyleable.getField("AndroidManifestUsesSdk_targetSdkVersion").getInt(null));
                        } catch (Exception e) {
                        }
                        if (val != null) {
                            if (val.type == TypedValue.TYPE_STRING && val.string != null) {
                                targetCode = minCode = val.string.toString();
                            } else {
                                targetVers = val.data;
                            }
                        }

                        sa.recycle();

                        if (minCode != null) {
                            if (!minCode.equals(SDK_CODENAME)) {
                                mParseError = -12;
                                Log.e(TAG, "minCode " + minCode);
                                return null;
                            }
                        } else if (minVers > SDK_VERSION) {
                            outError[0] = "Requires newer sdk version #" + minVers
                                    + " (current version is #" + SDK_VERSION + ")";
                            Log.e(TAG, outError[0]);
                            mParseError = -12;
                            return null;
                        }

                        if (targetCode != null) {
                            if (!targetCode.equals(SDK_CODENAME)) {
                                if (SDK_CODENAME != null) {
                                    outError[0] = "Requires development platform " + targetCode
                                            + " (current platform is " + SDK_CODENAME + ")";
                                } else {
                                    outError[0] = "Requires development platform " + targetCode
                                            + " but this is a release platform.";
                                }
                                mParseError = -12;
                                Log.e(TAG, outError[0]);
                                return null;
                            }
                            // If the code matches, it definitely targets this SDK.
                            pkg.applicationInfo.targetSdkVersion
                                    = android.os.Build.VERSION_CODES.CUR_DEVELOPMENT;
                        } else {
                            pkg.applicationInfo.targetSdkVersion = targetVers;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "获取资源AndroidManifestUsesSdk错误", e);
                    }
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("supports-screens")) {
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestSupportsScreens").get(null));

                    try {
                        pkg.applicationInfo.requiresSmallestWidthDp = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_requiresSmallestWidthDp").getInt(null),
                                0);
                    } catch (Exception e) {
                    }
                    try {
                        pkg.applicationInfo.compatibleWidthLimitDp = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_compatibleWidthLimitDp").getInt(null),
                                0);
                    } catch (Exception e) {

                    }
                    try {
                        pkg.applicationInfo.largestWidthLimitDp = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_largestWidthLimitDp").getInt(null),
                                0);
                    } catch (Exception e) {
                    }
                    try {
                        supportsSmallScreens = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_smallScreens").getInt(null),
                                supportsSmallScreens);
                    } catch (Exception e) {
                    }
                    try {
                        supportsNormalScreens = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_normalScreens").getInt(null),
                                supportsNormalScreens);
                    } catch (Exception e) {
                    }
                    try {
                        supportsLargeScreens = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_largeScreens").getInt(null),
                                supportsLargeScreens);
                    } catch (Exception e) {
                    }
                    try {
                        supportsXLargeScreens = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_xlargeScreens").getInt(null),
                                supportsXLargeScreens);
                    } catch (Exception e) {
                    }
                    try {
                        resizeable = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_resizeable").getInt(null),
                                resizeable);
                    } catch (Exception e) {
                    }
                    try {
                        anyDensity = sa.getInteger(
                                internalStyleable.getField("AndroidManifestSupportsScreens_anyDensity").getInt(null),
                                anyDensity);
                    } catch (Exception e) {
                    }

                    sa.recycle();
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestSupportsScreens错误", e);
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("protected-broadcast")) {
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestProtectedBroadcast").get(null));
                    String name = null;
                    try {
                        name = sa.getNonResourceString(
                                internalStyleable.getField("AndroidManifestProtectedBroadcast_name").getInt(null));
                    } catch (Exception e) {
                    }

                    sa.recycle();

                    if (name != null) {
                        if (pkg.protectedBroadcasts == null) {
                            pkg.protectedBroadcasts = new ArrayList<String>();
                        }
                        if (!pkg.protectedBroadcasts.contains(name)) {
                            pkg.protectedBroadcasts.add(name.intern());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestProtectedBroadcast错误", e);
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("instrumentation")) {
                if (parseInstrumentation(pkg, res, parser, attrs, outError) == null) {
                    Log.e(TAG, "parse instrumentation error");
                    return null;
                }

            } else if (tagName.equals("original-package")) {
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestOriginalPackage").get(null));

                    String orig = null;
                    try {
                        orig = sa.getString(
                                internalStyleable.getField("AndroidManifestOriginalPackage_name").getInt(null));
                    } catch (Exception e) {
                    }
                    if (!pkg.packageName.equals(orig)) {
                        if (pkg.mOriginalPackages == null) {
                            pkg.mOriginalPackages = new ArrayList<String>();
                            pkg.mRealPackage = pkg.packageName;
                        }
                        pkg.mOriginalPackages.add(orig);
                    }

                    sa.recycle();
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestOriginalPackage错误", e);
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("adopt-permissions")) {
                try {
                    sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestOriginalPackage").get(null));

                    String name = null;
                    try {
                        name = sa.getString(
                                internalStyleable.getField("AndroidManifestOriginalPackage_name").getInt(null));
                    } catch (Exception e) {
                    }

                    sa.recycle();

                    if (name != null) {
                        if (pkg.mAdoptPermissions == null) {
                            pkg.mAdoptPermissions = new ArrayList<String>();
                        }
                        pkg.mAdoptPermissions.add(name);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestOriginalPackage错误", e);
                }

                XmlUtils.skipCurrentTag(parser);

            } else if (tagName.equals("uses-gl-texture")) {
                // Just skip this tag
                XmlUtils.skipCurrentTag(parser);
                continue;

            } else if (tagName.equals("compatible-screens")) {
                // Just skip this tag
                XmlUtils.skipCurrentTag(parser);
                continue;

            } else if (tagName.equals("eat-comment")) {
                // Just skip this tag
                XmlUtils.skipCurrentTag(parser);
                continue;

            } else if (RIGID_PARSER) {
                outError[0] = "Bad element under <manifest>: "
                        + parser.getName();
                mParseError = -108;
                Log.e(TAG, outError[0]);
                return null;

            } else {
                XmlUtils.skipCurrentTag(parser);
                continue;
            }
        }

        if (!foundApp && pkg.instrumentation.size() == 0) {
            outError[0] = "<manifest> does not contain an <application> or <instrumentation>";
            mParseError = -109;
        }

        final int NP = PackageParser.NEW_PERMISSIONS.length;
        StringBuilder implicitPerms = null;
        for (int ip = 0; ip < NP; ip++) {
            final PackageParser.NewPermissionInfo npi
                    = PackageParser.NEW_PERMISSIONS[ip];
            if (pkg.applicationInfo.targetSdkVersion >= npi.sdkVersion) {
                break;
            }
            if (!pkg.requestedPermissions.contains(npi.name)) {
                if (implicitPerms == null) {
                    implicitPerms = new StringBuilder(128);
                    implicitPerms.append(pkg.packageName);
                    implicitPerms.append(": compat added ");
                } else {
                    implicitPerms.append(' ');
                }
                implicitPerms.append(npi.name);
                pkg.requestedPermissions.add(npi.name);
            }
        }

        if (supportsSmallScreens < 0 || (supportsSmallScreens > 0
                && pkg.applicationInfo.targetSdkVersion
                >= android.os.Build.VERSION_CODES.DONUT)) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS;
        }
        if (supportsNormalScreens != 0) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS;
        }
        if (supportsLargeScreens < 0 || (supportsLargeScreens > 0
                && pkg.applicationInfo.targetSdkVersion
                >= android.os.Build.VERSION_CODES.DONUT)) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS;
        }
        if (supportsXLargeScreens < 0 || (supportsXLargeScreens > 0
                && pkg.applicationInfo.targetSdkVersion
                >= android.os.Build.VERSION_CODES.GINGERBREAD)) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS;
        }
        if (resizeable < 0 || (resizeable > 0
                && pkg.applicationInfo.targetSdkVersion
                >= android.os.Build.VERSION_CODES.DONUT)) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS;
        }
        if (anyDensity < 0 || (anyDensity > 0
                && pkg.applicationInfo.targetSdkVersion
                >= android.os.Build.VERSION_CODES.DONUT)) {
            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES;
        }

        return pkg;
    }

    private static String buildClassName(String pkg, CharSequence clsSeq,
                                         String[] outError) {
        if (clsSeq == null || clsSeq.length() <= 0) {
            outError[0] = "Empty class name in package " + pkg;
            return null;
        }
        String cls = clsSeq.toString();
        char c = cls.charAt(0);
        if (c == '.') {
            return (pkg + cls).intern();
        }
        if (cls.indexOf('.') < 0) {
            StringBuilder b = new StringBuilder(pkg);
            b.append('.');
            b.append(cls);
            return b.toString().intern();
        }
        if (c >= 'a' && c <= 'z') {
            return cls.intern();
        }
        outError[0] = "Bad class name " + cls + " in package " + pkg;
        return null;
    }

    private static String buildCompoundName(String pkg,
                                            CharSequence procSeq, String type, String[] outError) {
        String proc = procSeq.toString();
        char c = proc.charAt(0);
        if (pkg != null && c == ':') {
            if (proc.length() < 2) {
                outError[0] = "Bad " + type + " name " + proc + " in package " + pkg
                        + ": must be at least two characters";
                return null;
            }
            String subName = proc.substring(1);
            String nameError = validateName(subName, false);
            if (nameError != null) {
                outError[0] = "Invalid " + type + " name " + proc + " in package "
                        + pkg + ": " + nameError;
                return null;
            }
            return (pkg + proc).intern();
        }
        String nameError = validateName(proc, true);
        if (nameError != null && !"system".equals(proc)) {
            outError[0] = "Invalid " + type + " name " + proc + " in package "
                    + pkg + ": " + nameError;
            return null;
        }
        return proc.intern();
    }

    private static String buildProcessName(String pkg, String defProc,
                                           CharSequence procSeq, String[] separateProcesses,
                                           String[] outError) {
        if (!"system".equals(procSeq)) {
            return defProc != null ? defProc : pkg;
        }
        if (separateProcesses != null) {
            for (int i = separateProcesses.length - 1; i >= 0; i--) {
                String sp = separateProcesses[i];
                if (sp.equals(pkg) || sp.equals(defProc) || sp.equals(procSeq)) {
                    return pkg;
                }
            }
        }
        if (procSeq == null || procSeq.length() <= 0) {
            return defProc;
        }
        return buildCompoundName(pkg, procSeq, "process", outError);
    }

    private static String buildTaskAffinityName(String pkg, String defProc,
                                                CharSequence procSeq, String[] outError) {
        if (procSeq == null) {
            return defProc;
        }
        if (procSeq.length() <= 0) {
            return null;
        }
        return buildCompoundName(pkg, procSeq, "taskAffinity", outError);
    }

    private PermissionGroup parsePermissionGroup(Package owner, Resources res,
                                                 XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        PermissionGroup perm = new PermissionGroup(owner);
        try {

            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestPermissionGroup").get(null));

            try {
                if (!parsePackageItemInfo(owner, perm.info, outError,
                        "<permission-group>", sa,
                        internalStyleable.getField("AndroidManifestPermissionGroup_name").getInt(null),
                        internalStyleable.getField("AndroidManifestPermissionGroup_label").getInt(null),
                        internalStyleable.getField("AndroidManifestPermissionGroup_icon").getInt(null),
                        internalStyleable.getField("AndroidManifestPermissionGroup_logo").getInt(null))) {
                    sa.recycle();
                    mParseError = -108;
                    return null;
                }
            } catch (Exception e) {
            }

            try {
                perm.info.descriptionRes = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestPermissionGroup_description").getInt(null),
                        0);
            } catch (Exception e) {
            }

            sa.recycle();
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestPermissionGroup错误", e);
        }

        if (!parseAllMetaData(res, parser, attrs, "<permission-group>", perm,
                outError)) {
            mParseError = -108;
            return null;
        }

        owner.permissionGroups.add(perm);

        return perm;
    }

    private Permission parsePermission(Package owner, Resources res,
                                       XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestPermission").get(null));

            try {
                if (!parsePackageItemInfo(owner, perm.info, outError,
                        "<permission>", sa,
                        internalStyleable.getField("AndroidManifestPermission_name").getInt(null),
                        internalStyleable.getField("AndroidManifestPermission_label").getInt(null),
                        internalStyleable.getField("AndroidManifestPermission_icon").getInt(null),
                        internalStyleable.getField("AndroidManifestPermission_logo").getInt(null))) {
                    sa.recycle();
                    mParseError = -108;
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                perm.info.group = sa.getNonResourceString(
                        internalStyleable.getField("AndroidManifestPermission_permissionGroup").getInt(null));
            } catch (Exception e) {
            }
            if (perm.info.group != null) {
                perm.info.group = perm.info.group.intern();
            }

            try {
                perm.info.descriptionRes = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestPermission_description").getInt(null),
                        0);
            } catch (Exception e) {
            }

            try {
                perm.info.protectionLevel = sa.getInt(
                        internalStyleable.getField("AndroidManifestPermission_protectionLevel").getInt(null),
                        PermissionInfo.PROTECTION_NORMAL);
            } catch (Exception e) {
            }

            sa.recycle();
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestPermission错误", e);
        }

        if (perm.info.protectionLevel == -1) {
            outError[0] = "<permission> does not specify protectionLevel";
            mParseError = -108;
            return null;
        }

        if (!parseAllMetaData(res, parser, attrs, "<permission>", perm,
                outError)) {
            mParseError = -108;
            return null;
        }

        owner.permissions.add(perm);

        return perm;
    }

    private Permission parsePermissionTree(Package owner, Resources res,
                                           XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestPermissionTree").get(null));
            if (!parsePackageItemInfo(owner, perm.info, outError,
                    "<permission-tree>", sa,
                    internalStyleable.getField("AndroidManifestPermissionTree_name").getInt(null),
                    internalStyleable.getField("AndroidManifestPermissionTree_label").getInt(null),
                    internalStyleable.getField("AndroidManifestPermissionTree_icon").getInt(null),
                    internalStyleable.getField("AndroidManifestPermissionTree_logo").getInt(null))) {
                sa.recycle();
                mParseError = -108;
                return null;
            }

            sa.recycle();
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestPermissionTree错误", e);
        }

        int index = perm.info.name.indexOf('.');
        if (index > 0) {
            index = perm.info.name.indexOf('.', index + 1);
        }
        if (index < 0) {
            outError[0] = "<permission-tree> name has less than three segments: "
                    + perm.info.name;
            mParseError = -108;
            return null;
        }

        perm.info.descriptionRes = 0;
        perm.info.protectionLevel = PermissionInfo.PROTECTION_NORMAL;
        perm.tree = true;

        if (!parseAllMetaData(res, parser, attrs, "<permission-tree>", perm,
                outError)) {
            mParseError = -108;
            return null;
        }

        owner.permissions.add(perm);

        return perm;
    }

    private Instrumentation parseInstrumentation(Package owner, Resources res,
                                                 XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestInstrumentation").get(null));

            try {
                if (mParseInstrumentationArgs == null) {
                    mParseInstrumentationArgs = new ParsePackageItemArgs(owner, outError,
                            internalStyleable.getField("AndroidManifestInstrumentation_name").getInt(null),
                            internalStyleable.getField("AndroidManifestInstrumentation_label").getInt(null),
                            internalStyleable.getField("AndroidManifestInstrumentation_icon").getInt(null),
                            internalStyleable.getField("AndroidManifestInstrumentation_logo").getInt(null),parser);
                    mParseInstrumentationArgs.tag = "<instrumentation>";
                }
            } catch (Exception e) {
            }

            mParseInstrumentationArgs.sa = sa;

            Instrumentation a = new Instrumentation(mParseInstrumentationArgs,
                    new InstrumentationInfo());
            if (outError[0] != null) {
                sa.recycle();
                mParseError = -108;
                return null;
            }
            String str = null;
            try {
                str = sa.getNonResourceString(
                        internalStyleable.getField("AndroidManifestInstrumentation_targetPackage").getInt(null));
            } catch (Exception e) {
            }
            a.info.targetPackage = str != null ? str.intern() : null;

            try {
                a.info.handleProfiling = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestInstrumentation_handleProfiling").getInt(null),
                        false);
            } catch (Exception e) {
            }

            try {
                a.info.functionalTest = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestInstrumentation_functionalTest").getInt(null),
                        false);
            } catch (Exception e) {
            }

            sa.recycle();

            if (a.info.targetPackage == null) {
                outError[0] = "<instrumentation> does not specify targetPackage";
                mParseError = -108;
                return null;
            }

            if (!parseAllMetaData(res, parser, attrs, "<instrumentation>", a,
                    outError)) {
                mParseError = -108;
                return null;
            }
            owner.instrumentation.add(a);
            return a;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestInstrumentation错误", e);
            return null;
        }
    }

    private boolean parseApplication(Package owner, Resources res,
                                     XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        final ApplicationInfo ai = owner.applicationInfo;
        final String pkgName = owner.applicationInfo.packageName;
        try {

            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestApplication").get(null));

            String name = null;
            try {
                name = sa.getString(
                        internalStyleable.getField("AndroidManifestApplication_name").getInt(null));
            } catch (Exception e) {
            }
            if (name != null) {
                ai.className = buildClassName(pkgName, name, outError);
                if (ai.className == null) {
                    sa.recycle();
                    mParseError = -108;
                    Log.e(TAG, "create application error");
                    return false;
                }
            }

            String manageSpaceActivity = null;
            try {
                manageSpaceActivity = sa.getString(
                        internalStyleable.getField("AndroidManifestApplication_manageSpaceActivity").getInt(null));
            } catch (Exception e) {
            }
            if (manageSpaceActivity != null) {
                ai.manageSpaceActivityName = buildClassName(pkgName, manageSpaceActivity,
                        outError);
            }

            boolean allowBackup = true;
            try {
                allowBackup = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_allowBackup").getInt(null), true);
            } catch (Exception e) {
            }
            if (allowBackup) {
                ai.flags |= ApplicationInfo.FLAG_ALLOW_BACKUP;

                // backupAgent, killAfterRestore, and restoreAnyVersion are only relevant
                // if backup is possible for the given application.
                String backupAgent = null;
                try {
                    backupAgent = sa.getString(
                            internalStyleable.getField("AndroidManifestApplication_backupAgent").getInt(null));
                } catch (Exception e) {
                }
                if (backupAgent != null) {
                    ai.backupAgentName = buildClassName(pkgName, backupAgent, outError);
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestApplication_killAfterRestore").getInt(null),
                                true)) {
                            ai.flags |= ApplicationInfo.FLAG_KILL_AFTER_RESTORE;
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestApplication_restoreAnyVersion").getInt(null),
                                false)) {
                            ai.flags |= ApplicationInfo.FLAG_RESTORE_ANY_VERSION;
                        }
                    } catch (Exception e) {
                    }
                }
            }

            TypedValue v = null;
            try {
                v = sa.peekValue(
                        internalStyleable.getField("AndroidManifestApplication_label").getInt(null));
            } catch (Exception e) {
            }
            if (v != null && (ai.labelRes = v.resourceId) == 0) {
                ai.nonLocalizedLabel = v.coerceToString();
            }

            try {
                ai.icon = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestApplication_icon").getInt(null), 0);
            } catch (Exception e) {
            }
            try {
                ai.logo = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestApplication_logo").getInt(null), 0);
            } catch (Exception e) {
            }
            try {
                ai.theme = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestApplication_theme").getInt(null), 0);
            } catch (Exception e) {
            }
            try {
                ai.descriptionRes = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestApplication_description").getInt(null), 0);
            } catch (Exception e) {
            }
            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_persistent").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_PERSISTENT;
                }
            } catch (Exception e) {
            }
            ai.flags |= 1 << 2;
            ai.flags |= ApplicationInfo.FLAG_EXTERNAL_STORAGE;
            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_debuggable").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_DEBUGGABLE;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_vmSafeMode").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_VM_SAFE_MODE;
                }
            } catch (Exception e) {
            }

            boolean hardwareAccelerated = false;
            try {
                hardwareAccelerated = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_hardwareAccelerated").getInt(null),
                        owner.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_hasCode").getInt(null),
                        true)) {
                    ai.flags |= ApplicationInfo.FLAG_HAS_CODE;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_allowTaskReparenting").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_allowClearUserData").getInt(null),
                        true)) {
                    ai.flags |= ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA;
                }
            } catch (Exception e) {
            }
            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_testOnly").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_TEST_ONLY;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestApplication_largeHeap").getInt(null),
                        false)) {
                    ai.flags |= ApplicationInfo.FLAG_LARGE_HEAP;
                }
            } catch (Exception e) {
            }

            String str = null;
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestApplication_permission").getInt(null));
            } catch (Exception e) {
            }
            ai.permission = (str != null && str.length() > 0) ? str.intern() : null;

            if (owner.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.FROYO) {
                try {
                    str = sa.getString(
                            internalStyleable.getField("AndroidManifestApplication_taskAffinity").getInt(null));
                } catch (Exception e) {
                }
            } else {
                try {
                    str = sa.getNonResourceString(
                            internalStyleable.getField("AndroidManifestApplication_taskAffinity").getInt(null));
                } catch (Exception e) {
                }
            }
            ai.taskAffinity = buildTaskAffinityName(ai.packageName, ai.packageName,
                    str, outError);

            if (outError[0] == null) {
                CharSequence pname = null;
                if (owner.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.FROYO) {
                    try {
                        pname = sa.getString(
                                internalStyleable.getField("AndroidManifestApplication_process").getInt(null));
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        pname = sa.getNonResourceString(
                                internalStyleable.getField("AndroidManifestApplication_process").getInt(null));
                    } catch (Exception e) {
                    }
                }
                ai.processName = buildProcessName(ai.packageName, null, pname, mSeparateProcesses, outError);

                try {
                    ai.enabled = sa.getBoolean(
                            internalStyleable.getField("AndroidManifestApplication_enabled").getInt(null), true);
                } catch (Exception e) {
                }

                if (false) {
                    try {
                        if (sa.getBoolean(
                                internalStyleable.getField("AndroidManifestApplication_cantSaveState").getInt(null),
                                false)) {
                            ai.flags |= 1 << 1;
                            if (ai.processName != null && ai.processName != ai.packageName) {
                                outError[0] = "cantSaveState applications can not use custom processes";
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            try {
                ai.uiOptions = sa.getInt(
                        internalStyleable.getField("AndroidManifestApplication_uiOptions").getInt(null), 0);
            } catch (Exception e) {
            }

            sa.recycle();

            if (outError[0] != null) {
                mParseError = -108;
                Log.e(TAG, "outError is not empty");
                return false;
            }

            final int innerDepth = parser.getDepth();

            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                String tagName = parser.getName();
                if (tagName.equals("activity")) {
                    Activity a = parseActivity(owner, res, parser, attrs, outError, false,
                            hardwareAccelerated);
                    if (a == null) {
                        Log.e(TAG, "parse activity error");
                        mParseError = -108;
                        return false;
                    }

                    owner.activities.add(a);

                } else if (tagName.equals("receiver")) {
                    Activity a = parseActivity(owner, res, parser, attrs, outError, true, false);
                    if (a == null) {
                        mParseError = -108;
                        Log.e(TAG, "parse receiver error");
                        return false;
                    }

                    owner.receivers.add(a);

                } else if (tagName.equals("service")) {
                    Service s = parseService(owner, res, parser, attrs, outError);
                    if (s == null) {
                        mParseError = -108;
                        Log.e(TAG, "parse service error");
                        return false;
                    }

                    owner.services.add(s);

                } else if (tagName.equals("provider")) {
                    Provider p = parseProvider(owner, res, parser, attrs, outError);
                    if (p == null) {
                        mParseError = -108;
                        Log.e(TAG, "parse provider error");
                        return false;
                    }

                    owner.providers.add(p);

                } else if (tagName.equals("activity-alias")) {
                    Activity a = parseActivityAlias(owner, res, parser, attrs, outError);
                    if (a == null) {
                        mParseError = -108;
                        Log.e(TAG, "parse activity-alias error");
                        return false;
                    }

                    owner.activities.add(a);

                } else if (parser.getName().equals("meta-data")) {
                    if ((owner.mAppMetaData = parseMetaData(res, parser, attrs, owner.mAppMetaData,
                            outError)) == null) {
                        mParseError = -108;
                        Log.e(TAG, "parse meta-data error");
                        return false;
                    }

                } else if (tagName.equals("uses-library")) {
                    try {
                        sa = res.obtainAttributes(attrs,
                                (int[]) internalStyleable.getField("AndroidManifestUsesLibrary").get(null));
                    } catch (Exception e) {
                    }

                    // Note: don't allow this value to be a reference to a resource
                    // that may change.
                    String lname = null;
                    try {
                        lname = sa.getNonResourceString(
                                internalStyleable.getField("AndroidManifestUsesLibrary_name").getInt(null));
                    } catch (Exception e) {
                    }
                    boolean req = false;
                    try {
                        req = sa.getBoolean(
                                internalStyleable.getField("AndroidManifestUsesLibrary_required").getInt(null),
                                true);
                    } catch (Exception e) {
                    }

                    sa.recycle();

                    if (lname != null) {
                        if (req) {
                            if (owner.usesLibraries == null) {
                                owner.usesLibraries = new ArrayList<String>();
                            }
                            if (!owner.usesLibraries.contains(lname)) {
                                owner.usesLibraries.add(lname.intern());
                            }
                        } else {
                            if (owner.usesOptionalLibraries == null) {
                                owner.usesOptionalLibraries = new ArrayList<String>();
                            }
                            if (!owner.usesOptionalLibraries.contains(lname)) {
                                owner.usesOptionalLibraries.add(lname.intern());
                            }
                        }
                    }

                    XmlUtils.skipCurrentTag(parser);

                } else if (tagName.equals("uses-package")) {
                    XmlUtils.skipCurrentTag(parser);

                } else {
                    if (!RIGID_PARSER) {
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    } else {
                        outError[0] = "Bad element under <application>: " + tagName;
                        mParseError = -108;
                        Log.e(TAG, outError[0]);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestApplication错误", e);
            return false;
        }

        return true;
    }

    private boolean parsePackageItemInfo(Package owner, PackageItemInfo outInfo,
                                         String[] outError, String tag, TypedArray sa,
                                         int nameRes, int labelRes, int iconRes, int logoRes) {
        String name = sa.getString(nameRes);
        if (name == null) {
            outError[0] = tag + " does not specify android:name";
            return false;
        }

        outInfo.name
                = buildClassName(owner.applicationInfo.packageName, name, outError);
        if (outInfo.name == null) {
            return false;
        }

        int iconVal = sa.getResourceId(iconRes, 0);
        if (iconVal != 0) {
            outInfo.icon = iconVal;
            outInfo.nonLocalizedLabel = null;
        }

        int logoVal = sa.getResourceId(logoRes, 0);
        if (logoVal != 0) {
            outInfo.logo = logoVal;
        }

        TypedValue v = sa.peekValue(labelRes);
        if (v != null && (outInfo.labelRes = v.resourceId) == 0) {
            outInfo.nonLocalizedLabel = v.coerceToString();
        }

        outInfo.packageName = owner.packageName;

        return true;
    }

    private Activity parseActivity(Package owner, Resources res,
                                   XmlPullParser parser, AttributeSet attrs, String[] outError,
                                   boolean receiver, boolean hardwareAccelerated)
            throws XmlPullParserException, IOException {
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestActivity").get(null));

            try {
                if (mParseActivityArgs == null) {
                    mParseActivityArgs = new ParseComponentArgs(owner, outError,
                            internalStyleableGetInt("AndroidManifestActivity_name"),
                            internalStyleableGetInt("AndroidManifestActivity_label"),
                            internalStyleableGetInt("AndroidManifestActivity_icon"),
                            internalStyleableGetInt("AndroidManifestActivity_logo"),
                            mSeparateProcesses,
                            internalStyleableGetInt("AndroidManifestActivity_process"),
                            internalStyleableGetInt("AndroidManifestActivity_description"),
                            internalStyleableGetInt("AndroidManifestActivity_enabled"),parser);
                }
            } catch (Exception e) {
            }

            mParseActivityArgs.tag = receiver ? "<receiver>" : "<activity>";
            mParseActivityArgs.sa = sa;
            Activity a = new Activity(mParseActivityArgs, new ActivityInfo());
            if (outError[0] != null) {
                sa.recycle();
                return null;
            }

            boolean setExported = false;
            try {
                setExported = sa.hasValue(
                        internalStyleable.getField("AndroidManifestActivity_exported").getInt(null));
            } catch (Exception e) {
            }
            if (setExported) {
                try {
                    a.info.exported = sa.getBoolean(
                            internalStyleable.getField("AndroidManifestActivity_exported").getInt(null), false);
                } catch (Exception e) {
                }
            }

            try {
                a.info.theme = sa.getResourceId(
                        internalStyleable.getField("AndroidManifestActivity_theme").getInt(null), 0);
            } catch (Exception e) {
            }

            try {
                a.info.uiOptions = sa.getInt(
                        internalStyleable.getField("AndroidManifestActivity_uiOptions").getInt(null),
                        a.info.applicationInfo.uiOptions);
            } catch (Exception e) {
            }

            String str = null;
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestActivity_permission").getInt(null));
            } catch (Exception e) {
            }
            if (str == null) {
                a.info.permission = owner.applicationInfo.permission;
            } else {
                a.info.permission = str.length() > 0 ? str.toString().intern() : null;
            }

            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestActivity_taskAffinity").getInt(null));
            } catch (Exception e) {
            }
            a.info.taskAffinity = buildTaskAffinityName(owner.applicationInfo.packageName,
                    owner.applicationInfo.taskAffinity, str, outError);

            a.info.flags = 0;
            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_multiprocess").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_MULTIPROCESS;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_finishOnTaskLaunch").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_FINISH_ON_TASK_LAUNCH;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_clearTaskOnLaunch").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_noHistory").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_NO_HISTORY;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_alwaysRetainTaskState").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_ALWAYS_RETAIN_TASK_STATE;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_stateNotNeeded").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_STATE_NOT_NEEDED;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_excludeFromRecents").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_allowTaskReparenting").getInt(null),
                        (owner.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING) != 0)) {
                    a.info.flags |= ActivityInfo.FLAG_ALLOW_TASK_REPARENTING;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_finishOnCloseSystemDialogs").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS;
                }
            } catch (Exception e) {
            }

            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestActivity_immersive").getInt(null),
                        false)) {
                    a.info.flags |= ActivityInfo.FLAG_IMMERSIVE;
                }
            } catch (Exception e) {
            }

            if (!receiver) {
                try {
                    if (sa.getBoolean(
                            internalStyleable.getField("AndroidManifestActivity_hardwareAccelerated").getInt(null),
                            hardwareAccelerated)) {
                        a.info.flags |= ActivityInfo.FLAG_HARDWARE_ACCELERATED;
                    }
                } catch (Exception e) {
                }

                try {
                    a.info.launchMode = sa.getInt(
                            internalStyleable.getField("AndroidManifestActivity_launchMode").getInt(null),
                            ActivityInfo.LAUNCH_MULTIPLE);
                } catch (Exception e) {
                }
                try {
                    a.info.screenOrientation = sa.getInt(
                            internalStyleable.getField("AndroidManifestActivity_screenOrientation").getInt(null),
                            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                } catch (Exception e) {
                }
                try {
                    a.info.configChanges = sa.getInt(
                            internalStyleable.getField("AndroidManifestActivity_configChanges").getInt(null),
                            0);
                } catch (Exception e) {
                }
                try {
                    a.info.softInputMode = sa.getInt(
                            internalStyleable.getField("AndroidManifestActivity_windowSoftInputMode").getInt(null),
                            0);
                } catch (Exception e) {
                }
            } else {
                a.info.launchMode = ActivityInfo.LAUNCH_MULTIPLE;
                a.info.configChanges = 0;
            }

            sa.recycle();

//            if (receiver && (owner.applicationInfo.flags & 1 << 1) != 0) {
//                // A heavy-weight application can not have receives in its main process
//                // We can do direct compare because we intern all strings.
//                if (a.info.processName == owner.packageName) {
//                    outError[0] = "Heavy-weight applications can not have receivers in main process";
//                }
//            }

            if (outError[0] != null) {
                return null;
            }

            int outerDepth = parser.getDepth();
            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG
                    || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                if (parser.getName().equals("intent-filter")) {
                    ActivityIntentInfo intent = new ActivityIntentInfo(a);
                    if (!parseIntent(res, parser, attrs, intent, outError, !receiver)) {
                        return null;
                    }
                    if (intent.countActions() == 0) {
                    } else {
                        a.intents.add(intent);
                    }
                } else if (parser.getName().equals("meta-data")) {
                    if ((a.metaData = parseMetaData(res, parser, attrs, a.metaData,
                            outError)) == null) {
                        return null;
                    }
                } else {
                    if (!RIGID_PARSER) {
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    } else {
                        if (receiver) {
                            outError[0] = "Bad element under <receiver>: " + parser.getName();
                        } else {
                            outError[0] = "Bad element under <activity>: " + parser.getName();
                        }
                        return null;
                    }
                }
            }

            if (!setExported) {
                a.info.exported = a.intents.size() > 0;
            }

            return a;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestActivity错误", e);
            return null;
        }
    }

    private Activity parseActivityAlias(Package owner, Resources res,
                                        XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestActivityAlias").get(null));

            String targetActivity = null;
            try {
                targetActivity = sa.getString(
                        internalStyleable.getField("AndroidManifestActivityAlias_targetActivity").getInt(null));
            } catch (Exception e) {
            }
            if (targetActivity == null) {
                outError[0] = "<activity-alias> does not specify android:targetActivity";
                sa.recycle();
                return null;
            }

            targetActivity = buildClassName(owner.applicationInfo.packageName,
                    targetActivity, outError);
            if (targetActivity == null) {
                sa.recycle();
                return null;
            }

            try {
                if (mParseActivityAliasArgs == null) {
                    mParseActivityAliasArgs = new ParseComponentArgs(owner, outError,
                            internalStyleableGetInt("AndroidManifestActivityAlias_name"),
                            internalStyleableGetInt("AndroidManifestActivityAlias_label"),
                            internalStyleableGetInt("AndroidManifestActivityAlias_icon"),
                            internalStyleableGetInt("AndroidManifestActivityAlias_logo"),
                            mSeparateProcesses,
                            0,
                            internalStyleableGetInt("AndroidManifestActivityAlias_description"),
                            internalStyleableGetInt("AndroidManifestActivityAlias_enabled"),parser);
                    mParseActivityAliasArgs.tag = "<activity-alias>";
                }
            } catch (Exception e) {
            }

            mParseActivityAliasArgs.sa = sa;
            Activity target = null;
            final int NA = owner.activities.size();
            for (int i = 0; i < NA; i++) {
                Activity t = owner.activities.get(i);
                if (targetActivity.equals(t.info.name)) {
                    target = t;
                    break;
                }
            }

            if (target == null) {
                outError[0] = "<activity-alias> target activity " + targetActivity
                        + " not found in manifest";
                sa.recycle();
                return null;
            }

            ActivityInfo info = new ActivityInfo();
            info.targetActivity = targetActivity;
            info.configChanges = target.info.configChanges;
            info.flags = target.info.flags;
            info.icon = target.info.icon;
            info.logo = target.info.logo;
            info.labelRes = target.info.labelRes;
            info.nonLocalizedLabel = target.info.nonLocalizedLabel;
            info.launchMode = target.info.launchMode;
            info.processName = target.info.processName;
            if (info.descriptionRes == 0) {
                info.descriptionRes = target.info.descriptionRes;
            }
            info.screenOrientation = target.info.screenOrientation;
            info.taskAffinity = target.info.taskAffinity;
            info.theme = target.info.theme;
            info.softInputMode = target.info.softInputMode;
            info.uiOptions = target.info.uiOptions;

            Activity a = new Activity(mParseActivityAliasArgs, info);
            if (outError[0] != null) {
                sa.recycle();
                return null;
            }

            boolean setExported = false;
            try {
                setExported = sa.hasValue(
                        internalStyleable.getField("AndroidManifestActivityAlias_exported").getInt(null));
            } catch (Exception e) {
            }
            if (setExported) {
                try {
                    a.info.exported = sa.getBoolean(
                            internalStyleable.getField("AndroidManifestActivityAlias_exported").getInt(null), false);
                } catch (Exception e) {
                }
            }

            String str = null;
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestActivityAlias_permission").getInt(null));
            } catch (Exception e) {
            }
            if (str != null) {
                a.info.permission = str.length() > 0 ? str.toString().intern() : null;
            }

            sa.recycle();

            if (outError[0] != null) {
                return null;
            }

            int outerDepth = parser.getDepth();
            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG
                    || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                if (parser.getName().equals("intent-filter")) {
                    ActivityIntentInfo intent = new ActivityIntentInfo(a);
                    if (!parseIntent(res, parser, attrs, intent, outError, true)) {
                        return null;
                    }
                    if (intent.countActions() == 0) {
                    } else {
                        a.intents.add(intent);
                    }
                } else if (parser.getName().equals("meta-data")) {
                    if ((a.metaData = parseMetaData(res, parser, attrs, a.metaData,
                            outError)) == null) {
                        return null;
                    }
                } else {
                    if (!RIGID_PARSER) {
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    } else {
                        outError[0] = "Bad element under <activity-alias>: " + parser.getName();
                        return null;
                    }
                }
            }

            if (!setExported) {
                a.info.exported = a.intents.size() > 0;
            }

            return a;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestActivityAlias错误", e);
            return null;
        }
    }

    private int internalStyleableGetInt(String name) {
        try {
            return internalStyleable.getField(name).getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }

    private Provider parseProvider(Package owner, Resources res,
                                   XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestProvider").get(null));
            try {
                if (mParseProviderArgs == null) {
                    int name = internalStyleableGetInt("AndroidManifestProvider_name");
                    int label = internalStyleableGetInt("AndroidManifestProvider_label");
                    int icon = internalStyleableGetInt("AndroidManifestProvider_icon");
                    int logo = internalStyleableGetInt("AndroidManifestProvider_logo");
                    int process = internalStyleableGetInt("AndroidManifestProvider_process");
                    int description = internalStyleableGetInt("AndroidManifestProvider_description");
                    int enabled = internalStyleableGetInt("AndroidManifestProvider_enabled");
                    mParseProviderArgs = new ParseComponentArgs(owner, outError, name, label,
                            icon, logo, mSeparateProcesses, process, description, enabled,parser);
                    mParseProviderArgs.tag = "<provider>";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mParseProviderArgs.sa = sa;
            Provider p = new Provider(mParseProviderArgs, new ProviderInfo());
            if (outError[0] != null) {
                sa.recycle();
                Log.e(TAG, outError[0]);
                Log.e(TAG, "outError is not empty");
                return null;
            }

            try {
                p.info.exported = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestProvider_exported").getInt(null), true);
            } catch (Exception e) {
                p.info.exported=Boolean.valueOf(value(parser,"exported"));
            }

            String cpname = null;
            try {
                cpname = sa.getString(
                        internalStyleable.getField("AndroidManifestProvider_authorities").getInt(null));
            } catch (Exception e) {
                cpname=value(parser,"authorities");
            }

            try {
                p.info.isSyncable = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestProvider_syncable").getInt(null),
                        false);
            } catch (Exception e) {
            }p.info.isSyncable=Boolean.valueOf(value(parser,"syncable"));

            String permission = null;
            try {
                permission = sa.getString(
                        internalStyleable.getField("AndroidManifestProvider_permission").getInt(null));
            } catch (Exception e) {
                permission=value(parser,"permission");
            }
            String str = null;
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestProvider_readPermission").getInt(null));
            } catch (Exception e) {
                str=value(parser,"readPermission");
            }
            if (str == null) {
                str = permission;
            }
            if (str == null) {
                p.info.readPermission = owner.applicationInfo.permission;
            } else {
                p.info.readPermission =
                        str.length() > 0 ? str.toString().intern() : null;
            }
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestProvider_writePermission").getInt(null));
            } catch (Exception e) {
                str=value(parser,"writePermission");
            }
            if (str == null) {
                str = permission;
            }
            if (str == null) {
                p.info.writePermission = owner.applicationInfo.permission;
            } else {
                p.info.writePermission =
                        str.length() > 0 ? str.toString().intern() : null;
            }

            try {
                p.info.grantUriPermissions = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestProvider_grantUriPermissions").getInt(null),
                        false);
            } catch (Exception e) {
                p.info.grantUriPermissions=Boolean.valueOf(value(parser,"grantUriPermissions"));
            }

            try {
                p.info.multiprocess = sa.getBoolean(
                        internalStyleable.getField("AndroidManifestProvider_multiprocess").getInt(null),
                        false);
            } catch (Exception e) {
                p.info.multiprocess=Boolean.valueOf(value(parser,"multiprocess"));
            }

            try {
                p.info.initOrder = sa.getInt(
                        internalStyleable.getField("AndroidManifestProvider_initOrder").getInt(null),
                        0);
            } catch (Exception e) {
            }

            sa.recycle();

//            if ((owner.applicationInfo.flags & 1 << 1) != 0) {
//                // A heavy-weight application can not have providers in its main process
//                // We can do direct compare because we intern all strings.
//                if (p.info.processName == owner.packageName) {
//                    outError[0] = "Heavy-weight applications can not have providers in main process";
//                    Log.e(TAG,outError[0]);
//                    return null;
//                }
//            }

            if (cpname == null) {
                outError[0] = "<provider> does not incude authorities attribute";
                Log.e(TAG, outError[0]);
                return null;
            }
            p.info.authority = cpname.intern();

            if (!parseProviderTags(res, parser, attrs, p, outError)) {
                Log.e(TAG, "parse provider tags error");
                return null;
            }

            return p;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestProvider错误", e);
            return null;
        }
    }

    private boolean parseProviderTags(Resources res,
                                      XmlPullParser parser, AttributeSet attrs,
                                      Provider outInfo, String[] outError)
            throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG
                || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            if (parser.getName().equals("meta-data")) {
                if ((outInfo.metaData = parseMetaData(res, parser, attrs,
                        outInfo.metaData, outError)) == null) {
                    return false;
                }

            } else if (parser.getName().equals("grant-uri-permission")) {
                try {
                    TypedArray sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestGrantUriPermission").get(null));

                    PatternMatcher pa = null;

                    String str = null;
                    try {
                        str = sa.getString(
                                internalStyleable.getField("AndroidManifestGrantUriPermission_path").getInt(null));
                    } catch (Exception e) {
                    }
                    if (str != null) {
                        pa = new PatternMatcher(str, PatternMatcher.PATTERN_LITERAL);
                    }

                    try {
                        str = sa.getString(
                                internalStyleable.getField("AndroidManifestGrantUriPermission_pathPrefix").getInt(null));
                    } catch (Exception e) {
                    }
                    if (str != null) {
                        pa = new PatternMatcher(str, PatternMatcher.PATTERN_PREFIX);
                    }

                    try {
                        str = sa.getString(
                                internalStyleable.getField("AndroidManifestGrantUriPermission_pathPattern").getInt(null));
                    } catch (Exception e) {
                    }
                    if (str != null) {
                        pa = new PatternMatcher(str, PatternMatcher.PATTERN_SIMPLE_GLOB);
                    }

                    sa.recycle();

                    if (pa != null) {
                        if (outInfo.info.uriPermissionPatterns == null) {
                            outInfo.info.uriPermissionPatterns = new PatternMatcher[1];
                            outInfo.info.uriPermissionPatterns[0] = pa;
                        } else {
                            final int N = outInfo.info.uriPermissionPatterns.length;
                            PatternMatcher[] newp = new PatternMatcher[N + 1];
                            System.arraycopy(outInfo.info.uriPermissionPatterns, 0, newp, 0, N);
                            newp[N] = pa;
                            outInfo.info.uriPermissionPatterns = newp;
                        }
                        outInfo.info.grantUriPermissions = true;
                    } else {
                        if (!RIGID_PARSER) {
                            XmlUtils.skipCurrentTag(parser);
                            continue;
                        } else {
                            outError[0] = "No path, pathPrefix, or pathPattern for <path-permission>";
                            return false;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestGrantUriPermission错误", e);
                }
                XmlUtils.skipCurrentTag(parser);

            } else if (parser.getName().equals("path-permission")) {
                try {
                    TypedArray sa = res.obtainAttributes(attrs,
                            (int[]) internalStyleable.getField("AndroidManifestPathPermission").get(null));

                    PathPermission pa = null;

                    String permission = null;
                    try {
                        permission = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_permission").getInt(null));
                    } catch (Exception e) {
                    }
                    String readPermission = null;
                    try {
                        readPermission = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_readPermission").getInt(null));
                    } catch (Exception e) {
                    }
                    if (readPermission == null) {
                        readPermission = permission;
                    }
                    String writePermission = null;
                    try {
                        writePermission = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_writePermission").getInt(null));
                    } catch (Exception e) {
                    }
                    if (writePermission == null) {
                        writePermission = permission;
                    }

                    boolean havePerm = false;
                    if (readPermission != null) {
                        readPermission = readPermission.intern();
                        havePerm = true;
                    }
                    if (writePermission != null) {
                        writePermission = writePermission.intern();
                        havePerm = true;
                    }

                    if (!havePerm) {
                        if (!RIGID_PARSER) {
                            XmlUtils.skipCurrentTag(parser);
                            continue;
                        } else {
                            outError[0] = "No readPermission or writePermssion for <path-permission>";
                            return false;
                        }
                    }

                    String path = null;
                    try {
                        path = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_path").getInt(null));
                    } catch (Exception e) {
                    }
                    if (path != null) {
                        pa = new PathPermission(path,
                                PatternMatcher.PATTERN_LITERAL, readPermission, writePermission);
                    }

                    try {
                        path = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_pathPrefix").getInt(null));
                    } catch (Exception e) {
                    }
                    if (path != null) {
                        pa = new PathPermission(path,
                                PatternMatcher.PATTERN_PREFIX, readPermission, writePermission);
                    }

                    try {
                        path = sa.getString(
                                internalStyleable.getField("AndroidManifestPathPermission_pathPattern").getInt(null));
                    } catch (Exception e) {
                    }
                    if (path != null) {
                        pa = new PathPermission(path,
                                PatternMatcher.PATTERN_SIMPLE_GLOB, readPermission, writePermission);
                    }

                    sa.recycle();

                    if (pa != null) {
                        if (outInfo.info.pathPermissions == null) {
                            outInfo.info.pathPermissions = new PathPermission[1];
                            outInfo.info.pathPermissions[0] = pa;
                        } else {
                            final int N = outInfo.info.pathPermissions.length;
                            PathPermission[] newp = new PathPermission[N + 1];
                            System.arraycopy(outInfo.info.pathPermissions, 0, newp, 0, N);
                            newp[N] = pa;
                            outInfo.info.pathPermissions = newp;
                        }
                    } else {
                        if (!RIGID_PARSER) {
                            XmlUtils.skipCurrentTag(parser);
                            continue;
                        }
                        outError[0] = "No path, pathPrefix, or pathPattern for <path-permission>";
                        return false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取资源AndroidManifestPathPermission错误", e);
                }
                XmlUtils.skipCurrentTag(parser);

            } else {
                if (!RIGID_PARSER) {
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else {
                    outError[0] = "Bad element under <provider>: " + parser.getName();
                    return false;
                }
            }
        }
        return true;
    }

    private Service parseService(Package owner, Resources res,
                                 XmlPullParser parser, AttributeSet attrs, String[] outError)
            throws XmlPullParserException, IOException {
        try {
            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestService").get(null));

            try {
                if (mParseServiceArgs == null) {
                    mParseServiceArgs = new ParseComponentArgs(owner, outError,
                            internalStyleableGetInt("AndroidManifestService_name"),
                            internalStyleableGetInt("AndroidManifestService_label"),
                            internalStyleableGetInt("AndroidManifestService_icon"),
                            internalStyleableGetInt("AndroidManifestService_logo"),
                            mSeparateProcesses,
                            internalStyleableGetInt("AndroidManifestService_process"),
                            internalStyleableGetInt("AndroidManifestService_description"),
                            internalStyleableGetInt("AndroidManifestService_enabled"),parser);
                    mParseServiceArgs.tag = "<service>";
                }
            } catch (Exception e) {
            }
            mParseServiceArgs.sa = sa;
            Service s = new Service(mParseServiceArgs, new ServiceInfo());
            if (outError[0] != null) {
                sa.recycle();
                return null;
            }

            boolean setExported = false;
            try {
                setExported = sa.hasValue(
                        internalStyleable.getField("AndroidManifestService_exported").getInt(null));
            } catch (Exception e) {
            }
            if (setExported) {
                try {
                    s.info.exported = sa.getBoolean(
                            internalStyleable.getField("AndroidManifestService_exported").getInt(null), false);
                } catch (Exception e) {
                }
            }

            String str = null;
            try {
                str = sa.getString(
                        internalStyleable.getField("AndroidManifestService_permission").getInt(null));
            } catch (Exception e) {
            }
            if (str == null) {
                s.info.permission = owner.applicationInfo.permission;
            } else {
                s.info.permission = str.length() > 0 ? str.toString().intern() : null;
            }

            s.info.flags = 0;
            try {
                if (sa.getBoolean(
                        internalStyleable.getField("AndroidManifestService_stopWithTask").getInt(null),
                        false)) {
                    s.info.flags |= ServiceInfo.FLAG_STOP_WITH_TASK;
                }
            } catch (Exception e) {
            }

            sa.recycle();

//            if ((owner.applicationInfo.flags & 1 << 1) != 0) {
//                // A heavy-weight application can not have services in its main process
//                // We can do direct compare because we intern all strings.
//                if (s.info.processName == owner.packageName) {
//                    outError[0] = "Heavy-weight applications can not have services in main process";
//                    return null;
//                }
//            }

            int outerDepth = parser.getDepth();
            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG
                    || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                if (parser.getName().equals("intent-filter")) {
                    ServiceIntentInfo intent = new ServiceIntentInfo(s);
                    if (!parseIntent(res, parser, attrs, intent, outError, false)) {
                        return null;
                    }

                    s.intents.add(intent);
                } else if (parser.getName().equals("meta-data")) {
                    if ((s.metaData = parseMetaData(res, parser, attrs, s.metaData,
                            outError)) == null) {
                        return null;
                    }
                } else {
                    if (!RIGID_PARSER) {
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    } else {
                        outError[0] = "Bad element under <service>: " + parser.getName();
                        return null;
                    }
                }
            }

            if (!setExported) {
                s.info.exported = s.intents.size() > 0;
            }

            return s;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestService错误", e);
            return null;
        }
    }

    private boolean parseAllMetaData(Resources res,
                                     XmlPullParser parser, AttributeSet attrs, String tag,
                                     Component outInfo, String[] outError)
            throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG
                || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            if (parser.getName().equals("meta-data")) {
                if ((outInfo.metaData = parseMetaData(res, parser, attrs,
                        outInfo.metaData, outError)) == null) {
                    return false;
                }
            } else {
                if (!RIGID_PARSER) {
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else {
                    outError[0] = "Bad element under " + tag + ": " + parser.getName();
                    return false;
                }
            }
        }
        return true;
    }

    private Bundle parseMetaData(Resources res,
                                 XmlPullParser parser, AttributeSet attrs,
                                 Bundle data, String[] outError)
            throws XmlPullParserException, IOException {
        try {

            TypedArray sa = res.obtainAttributes(attrs,
                    (int[]) internalStyleable.getField("AndroidManifestMetaData").get(null));

            if (data == null) {
                data = new Bundle();
            }

            String name = null;
            try {
                name = sa.getString(
                        internalStyleable.getField("AndroidManifestMetaData_name").getInt(null));
            } catch (Exception e) {
            }
            if (name == null) {
                outError[0] = "<meta-data> requires an android:name attribute";
                sa.recycle();
                return null;
            }

            name = name.intern();

            TypedValue v = null;
            try {
                v = sa.peekValue(
                        internalStyleable.getField("AndroidManifestMetaData_resource").getInt(null));
            } catch (Exception e) {
            }
            if (v != null && v.resourceId != 0) {
                //Slog.i(TAG, "Meta data ref " + name + ": " + v);
                data.putInt(name, v.resourceId);
            } else {
                try {
                    v = sa.peekValue(
                            internalStyleable.getField("AndroidManifestMetaData_value").getInt(null));
                } catch (Exception e) {
                }
                //Slog.i(TAG, "Meta data " + name + ": " + v);
                if (v != null) {
                    if (v.type == TypedValue.TYPE_STRING) {
                        CharSequence cs = v.coerceToString();
                        data.putString(name, cs != null ? cs.toString().intern() : null);
                    } else if (v.type == TypedValue.TYPE_INT_BOOLEAN) {
                        data.putBoolean(name, v.data != 0);
                    } else if (v.type >= TypedValue.TYPE_FIRST_INT
                            && v.type <= TypedValue.TYPE_LAST_INT) {
                        data.putInt(name, v.data);
                    } else if (v.type == TypedValue.TYPE_FLOAT) {
                        data.putFloat(name, v.getFloat());
                    } else {
                        if (!RIGID_PARSER) {
                        } else {
                            outError[0] = "<meta-data> only supports string, integer, float, color, boolean, and resource reference types";
                            data = null;
                        }
                    }
                } else {
                    outError[0] = "<meta-data> requires an android:value or android:resource attribute";
                    data = null;
                }
            }

            sa.recycle();

            XmlUtils.skipCurrentTag(parser);

            return data;
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestMetaData错误", e);
            return null;
        }
    }



    private boolean parseIntent(Resources res,
                                XmlPullParser parser, AttributeSet attrs,
                                IntentInfo outInfo, String[] outError, boolean isActivity)
            throws XmlPullParserException, IOException {

        try {
            TypedArray sa;
            try {
                sa = res.obtainAttributes(attrs,
                        (int[]) internalStyleable.getField("AndroidManifestIntentFilter").get(null));

                int priority = 0;
                try {
                    priority = sa.getInt(
                            internalStyleable.getField("AndroidManifestIntentFilter_priority").getInt(null), 0);
                } catch (Exception e) {
                }
                outInfo.setPriority(priority);

                TypedValue v = null;
                try {
                    v = sa.peekValue(
                            internalStyleable.getField("AndroidManifestIntentFilter_label").getInt(null));
                } catch (Exception e) {
                }
                if (v != null && (outInfo.labelRes = v.resourceId) == 0) {
                    outInfo.nonLocalizedLabel = v.coerceToString();
                }

                try {
                    outInfo.icon = sa.getResourceId(
                            internalStyleable.getField("AndroidManifestIntentFilter_icon").getInt(null), 0);
                } catch (Exception e) {
                }

                try {
                    outInfo.logo = sa.getResourceId(
                            internalStyleable.getField("AndroidManifestIntentFilter_logo").getInt(null), 0);
                } catch (Exception e) {
                }

                sa.recycle();
            } catch (Exception e) {
                Log.e(TAG, "获取资源AndroidManifestIntentFilter错误", e);
            }

            int outerDepth = parser.getDepth();
            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                String nodeName = parser.getName();
                if (nodeName.equals("action")) {
                    String value = attrs.getAttributeValue(
                            ANDROID_RESOURCES, "name");
                    if (value == null || value == "") {
                        outError[0] = "No value supplied for <android:name>";
                        return false;
                    }
                    XmlUtils.skipCurrentTag(parser);

                    outInfo.addAction(value);
                } else if (nodeName.equals("category")) {
                    String value = attrs.getAttributeValue(
                            ANDROID_RESOURCES, "name");
                    if (value == null || value == "") {
                        outError[0] = "No value supplied for <android:name>";
                        return false;
                    }
                    XmlUtils.skipCurrentTag(parser);

                    outInfo.addCategory(value);

                } else if (nodeName.equals("data")) {
                    try {
                        sa = res.obtainAttributes(attrs,
                                (int[]) internalStyleable.getField("AndroidManifestData").get(null));

                        String str = null;
                        try {
                            str = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_mimeType").getInt(null));
                        } catch (Exception e) {
                        }
                        if (str != null) {
                            try {
                                outInfo.addDataType(str);
                            } catch (IntentFilter.MalformedMimeTypeException e) {
                                outError[0] = e.toString();
                                sa.recycle();
                                return false;
                            }
                        }

                        try {
                            str = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_scheme").getInt(null));
                        } catch (Exception e) {
                        }
                        if (str != null) {
                            outInfo.addDataScheme(str);
                        }

                        String host = null;
                        try {
                            host = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_host").getInt(null));
                        } catch (Exception e) {
                        }
                        String port = null;
                        try {
                            port = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_port").getInt(null));
                        } catch (Exception e) {
                        }
                        if (host != null) {
                            outInfo.addDataAuthority(host, port);
                        }

                        try {
                            str = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_path").getInt(null));
                        } catch (Exception e) {
                        }
                        if (str != null) {
                            outInfo.addDataPath(str, PatternMatcher.PATTERN_LITERAL);
                        }

                        try {
                            str = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_pathPrefix").getInt(null));
                        } catch (Exception e) {
                        }
                        if (str != null) {
                            outInfo.addDataPath(str, PatternMatcher.PATTERN_PREFIX);
                        }

                        try {
                            str = sa.getString(
                                    internalStyleable.getField("AndroidManifestData_pathPattern").getInt(null));
                        } catch (Exception e) {
                        }
                        if (str != null) {
                            outInfo.addDataPath(str, PatternMatcher.PATTERN_SIMPLE_GLOB);
                        }

                        sa.recycle();
                    } catch (Exception e) {
                        Log.e(TAG, "获取资源AndroidManifestData错误", e);
                    }
                    XmlUtils.skipCurrentTag(parser);
                } else if (!RIGID_PARSER) {
                    XmlUtils.skipCurrentTag(parser);
                } else {
                    outError[0] = "Bad element under <intent-filter>: " + parser.getName();
                    return false;
                }
            }

            outInfo.hasDefault = outInfo.hasCategory(Intent.CATEGORY_DEFAULT);

            if (DEBUG_PARSER) {
                final StringBuilder cats = new StringBuilder("Intent d=");
                cats.append(outInfo.hasDefault);
                cats.append(", cat=");

                final Iterator<String> it = outInfo.categoriesIterator();
                if (it != null) {
                    while (it.hasNext()) {
                        cats.append(' ');
                        cats.append(it.next());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取资源AndroidManifestData错误", e);
            return false;
        }

        return true;
    }

    public final static class Package {
        public String packageName;

        // For now we only support one application per package.
        public final ApplicationInfo applicationInfo = new ApplicationInfo();

        public final ArrayList<Permission> permissions = new ArrayList<Permission>(0);
        public final ArrayList<PermissionGroup> permissionGroups = new ArrayList<PermissionGroup>(0);
        public final ArrayList<Activity> activities = new ArrayList<Activity>(0);
        public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
        public final ArrayList<Provider> providers = new ArrayList<Provider>(0);
        public final ArrayList<Service> services = new ArrayList<Service>(0);
        public final ArrayList<Instrumentation> instrumentation = new ArrayList<Instrumentation>(0);

        public final ArrayList<String> requestedPermissions = new ArrayList<String>();

        public ArrayList<String> protectedBroadcasts;

        public ArrayList<String> usesLibraries = null;
        public ArrayList<String> usesOptionalLibraries = null;
        public String[] usesLibraryFiles = null;

        public ArrayList<String> mOriginalPackages = null;
        public String mRealPackage = null;
        public ArrayList<String> mAdoptPermissions = null;

        // We store the application meta-data independently to avoid multiple unwanted references
        public Bundle mAppMetaData = null;

        // If this is a 3rd party app, this is the path of the zip file.
        public String mPath;

        // The version code declared for this package.
        public int mVersionCode;

        // The version name declared for this package.
        public String mVersionName;

        // The shared user id that this package wants to use.
        public String mSharedUserId;

        // The shared user label that this package wants to use.
        public int mSharedUserLabel;

        // Signatures that were read from the package.
        public Signature mSignatures[];

        // For use by package manager service for quick lookup of
        // preferred up order.
        public int mPreferredOrder = 0;

        // For use by the package manager to keep track of the path to the
        // file an app came from.
        public String mScanPath;

        // For use by package manager to keep track of where it has done dexopt.
        public boolean mDidDexOpt;

        // User set enabled state.
        public int mSetEnabled = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;

        // Whether the package has been stopped.
        public boolean mSetStopped = false;

        // Additional data supplied by callers.
        public Object mExtras;

        // Whether an operation is currently pending on this package
        public boolean mOperationPending;

        /*
         *  Applications hardware preferences
         */
        public final ArrayList<ConfigurationInfo> configPreferences =
                new ArrayList<ConfigurationInfo>();

        /*
         *  Applications requested features
         */
        public ArrayList<FeatureInfo> reqFeatures = null;

        public int installLocation;

//        /**
//         * Digest suitable for comparing whether this package's manifest is the
//         * same as another.
//         */
//        public ManifestDigest manifestDigest;

        public Package(String _name) {
            packageName = _name;
            applicationInfo.packageName = _name;
            applicationInfo.uid = -1;
        }

        public void setPackageName(String newName) {
            packageName = newName;
            applicationInfo.packageName = newName;
            for (int i = permissions.size() - 1; i >= 0; i--) {
                permissions.get(i).setPackageName(newName);
            }
            for (int i = permissionGroups.size() - 1; i >= 0; i--) {
                permissionGroups.get(i).setPackageName(newName);
            }
            for (int i = activities.size() - 1; i >= 0; i--) {
                activities.get(i).setPackageName(newName);
            }
            for (int i = receivers.size() - 1; i >= 0; i--) {
                receivers.get(i).setPackageName(newName);
            }
            for (int i = providers.size() - 1; i >= 0; i--) {
                providers.get(i).setPackageName(newName);
            }
            for (int i = services.size() - 1; i >= 0; i--) {
                services.get(i).setPackageName(newName);
            }
            for (int i = instrumentation.size() - 1; i >= 0; i--) {
                instrumentation.get(i).setPackageName(newName);
            }
        }

        public String toString() {
            return "Package{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + packageName + "}";
        }
    }

    public static class Component<II extends IntentInfo> {
        public final Package owner;
        public final ArrayList<II> intents;
        public final String className;
        public Bundle metaData;

        ComponentName componentName;
        String componentShortName;

        public IntentInfo getIntentInfo() {
            if (intents != null && intents.size() > 0)
                return intents.get(0);
            return null;
        }

        public Component(Package _owner) {
            owner = _owner;
            intents = null;
            className = null;
        }

        public Component(final ParsePackageItemArgs args, final PackageItemInfo outInfo) {
            owner = args.owner;
            intents = new ArrayList<II>(0);
            String name = args.sa.getString(args.nameRes);
            if (name == null) {
                name=value(args.xmlPullParser,"name");
                if(name==null){
                    className = null;
                    args.outError[0] = args.tag + " does not specify android:name";
                    return;
                }
            }

            outInfo.name
                    = buildClassName(owner.applicationInfo.packageName, name, args.outError);
            if (outInfo.name == null) {
                className = null;
                args.outError[0] = args.tag + " does not have valid android:name";
                return;
            }

            className = outInfo.name;

            int iconVal = args.sa.getResourceId(args.iconRes, 0);
            if (iconVal != 0) {
                outInfo.icon = iconVal;
                outInfo.nonLocalizedLabel = null;
            }

            int logoVal = args.sa.getResourceId(args.logoRes, 0);
            if (logoVal != 0) {
                outInfo.logo = logoVal;
            }

            TypedValue v = args.sa.peekValue(args.labelRes);
            if (v != null && (outInfo.labelRes = v.resourceId) == 0) {
                outInfo.nonLocalizedLabel = v.coerceToString();
            }

            outInfo.packageName = owner.packageName;
        }

        public Component(final ParseComponentArgs args, final ComponentInfo outInfo) {
            this(args, (PackageItemInfo) outInfo);
            if (args.outError[0] != null) {
                return;
            }

            if (args.processRes != 0) {
                CharSequence pname;
                if (owner.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.FROYO) {
                    pname = args.sa.getString(args.processRes);
                } else {
                    // Some older apps have been seen to use a resource reference
                    // here that on older builds was ignored (with a warning).  We
                    // need to continue to do this for them so they don't break.
                    pname = args.sa.getNonResourceString(args.processRes);
                }
                outInfo.processName = buildProcessName(owner.applicationInfo.packageName,
                        owner.applicationInfo.processName, pname,
                        args.sepProcesses, args.outError);
            }

            if (args.descriptionRes != 0) {
                outInfo.descriptionRes = args.sa.getResourceId(args.descriptionRes, 0);
            }

            outInfo.enabled = args.sa.getBoolean(args.enabledRes, true);
        }

        public Component(Component<II> clone) {
            owner = clone.owner;
            intents = clone.intents;
            className = clone.className;
            componentName = clone.componentName;
            componentShortName = clone.componentShortName;
        }

        public ComponentName getComponentName() {
            if (componentName != null) {
                return componentName;
            }
            if (className != null) {
                componentName = new ComponentName(owner.applicationInfo.packageName,
                        className);
            }
            return componentName;
        }

        public String getComponentShortName() {
            if (componentShortName != null) {
                return componentShortName;
            }
            ComponentName component = getComponentName();
            if (component != null) {
                componentShortName = component.flattenToShortString();
            }
            return componentShortName;
        }

        public void setPackageName(String packageName) {
            componentName = null;
            componentShortName = null;
        }
    }

    public final static class Permission extends Component<IntentInfo> {
        public final PermissionInfo info;
        public boolean tree;
        public PermissionGroup group;

        public Permission(Package _owner) {
            super(_owner);
            info = new PermissionInfo();
        }

        public Permission(Package _owner, PermissionInfo _info) {
            super(_owner);
            info = _info;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "Permission{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + info.name + "}";
        }
    }

    public final static class PermissionGroup extends Component<IntentInfo> {
        public final PermissionGroupInfo info;

        public PermissionGroup(Package _owner) {
            super(_owner);
            info = new PermissionGroupInfo();
        }

        public PermissionGroup(Package _owner, PermissionGroupInfo _info) {
            super(_owner);
            info = _info;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "PermissionGroup{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + info.name + "}";
        }
    }

    private static boolean copyNeeded(int flags, Package p, Bundle metaData) {
        if (p.mSetEnabled != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            boolean enabled = p.mSetEnabled == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            if (p.applicationInfo.enabled != enabled) {
                return true;
            }
        }
        if ((flags & PackageManager.GET_META_DATA) != 0
                && (metaData != null || p.mAppMetaData != null)) {
            return true;
        }
        if ((flags & PackageManager.GET_SHARED_LIBRARY_FILES) != 0
                && p.usesLibraryFiles != null) {
            return true;
        }
        return false;
    }

    public static ApplicationInfo generateApplicationInfo(Package p) {
        if (p == null) return null;

        // Make shallow copy so we can store the metadata/libraries safely
        ApplicationInfo ai = new ApplicationInfo(p.applicationInfo);
        ai.metaData = p.mAppMetaData;
        ai.sharedLibraryFiles = p.usesLibraryFiles;
        if (p.mSetStopped) {
            p.applicationInfo.flags |= ApplicationInfo.FLAG_STOPPED;
        } else {
            p.applicationInfo.flags &= ~ApplicationInfo.FLAG_STOPPED;
        }
        if (p.mSetEnabled == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            ai.enabled = true;
        } else if (p.mSetEnabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || p.mSetEnabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
            ai.enabled = false;
        }
        return ai;
    }

    public static final PermissionInfo generatePermissionInfo(
            Permission p) {
        if (p == null) return null;
        PermissionInfo pi = new PermissionInfo(p.info);
        pi.metaData = p.metaData;
        return pi;
    }

    public static final PermissionGroupInfo generatePermissionGroupInfo(
            PermissionGroup pg, int flags) {
        if (pg == null) return null;
        if ((flags & PackageManager.GET_META_DATA) == 0) {
            return pg.info;
        }
        PermissionGroupInfo pgi = new PermissionGroupInfo(pg.info);
        pgi.metaData = pg.metaData;
        return pgi;
    }

    public final static class Activity extends Component<ActivityIntentInfo> {
        public final ActivityInfo info;

        public Activity(final ParseComponentArgs args, final ActivityInfo _info) {
            super(args, _info);
            info = _info;
            info.applicationInfo = args.owner.applicationInfo;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "Activity{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + getComponentShortName() + "}";
        }
    }

    public static final ActivityInfo generateActivityInfo(Activity a) {
        if (a == null) return null;
        ActivityInfo ai = new ActivityInfo(a.info);
        ai.metaData = a.metaData;
        ai.applicationInfo = generateApplicationInfo(a.owner);
        return ai;
    }

    public final static class Service extends Component<ServiceIntentInfo> {
        public final ServiceInfo info;

        public Service(final ParseComponentArgs args, final ServiceInfo _info) {
            super(args, _info);
            info = _info;
            info.applicationInfo = args.owner.applicationInfo;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "Service{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + getComponentShortName() + "}";
        }
    }

    public static final ServiceInfo generateServiceInfo(Service s) {
        if (s == null) return null;
        ServiceInfo si = new ServiceInfo(s.info);
        si.metaData = s.metaData;
        si.applicationInfo = generateApplicationInfo(s.owner);
        return si;
    }

    public final static class Provider extends Component {
        public final ProviderInfo info;
        public boolean syncable;

        public Provider(final ParseComponentArgs args, final ProviderInfo _info) {
            super(args, _info);
            info = _info;
            info.applicationInfo = args.owner.applicationInfo;
            syncable = false;
        }

        public Provider(Provider existingProvider) {
            super(existingProvider);
            this.info = existingProvider.info;
            this.syncable = existingProvider.syncable;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "Provider{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + info.name + "}";
        }
    }

    public static final ProviderInfo generateProviderInfo(Provider p) {
        if (p == null) return null;
        ProviderInfo pi = new ProviderInfo(p.info);
        pi.metaData = p.metaData;
        pi.applicationInfo = generateApplicationInfo(p.owner);
        return pi;
    }

    public final static class Instrumentation extends Component {
        public final InstrumentationInfo info;

        public Instrumentation(final ParsePackageItemArgs args, final InstrumentationInfo _info) {
            super(args, _info);
            info = _info;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            info.packageName = packageName;
        }

        public String toString() {
            return "Instrumentation{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + getComponentShortName() + "}";
        }
    }

    public static final InstrumentationInfo generateInstrumentationInfo(
            Instrumentation i) {
        if (i == null) return null;
        InstrumentationInfo ii = new InstrumentationInfo(i.info);
        ii.metaData = i.metaData;
        return ii;
    }

    public static class IntentInfo extends IntentFilter {
        public boolean hasDefault;
        public int labelRes;
        public CharSequence nonLocalizedLabel;
        public int icon;
        public int logo;
    }

    public final static class ActivityIntentInfo extends IntentInfo {
        public final Activity activity;

        public ActivityIntentInfo(Activity _activity) {
            activity = _activity;
        }

        public String toString() {
            return "ActivityIntentInfo{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + activity.info.name + "}";
        }
    }

    public final static class ServiceIntentInfo extends IntentInfo {
        public final Service service;

        public ServiceIntentInfo(Service _service) {
            service = _service;
        }

        public String toString() {
            return "ServiceIntentInfo{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " " + service.info.name + "}";
        }
    }

    /**
     * @hide
     */
    public static void setCompatibilityModeEnabled(boolean compatibilityModeEnabled) {
        sCompatibilityModeEnabled = compatibilityModeEnabled;
    }
}
