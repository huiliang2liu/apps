package com.io.file;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.io.PublicFiles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.core.content.ContextCompat;

@TargetApi(value = 24)
public class PathStrategy24 implements PathStrategy {
    private final String mAuthority;
    private final HashMap<String, File> mRoots = new HashMap<String, File>();
    private static final String TAG_ROOT_PATH = "root-path";
    private static final String TAG_FILES_PATH = "files-path";
    private static final String TAG_CACHE_PATH = "cache-path";
    private static final String TAG_EXTERNAL = "external-path";
    private static final String TAG_EXTERNAL_FILES = "external-files-path";
    private static final String TAG_EXTERNAL_CACHE = "external-cache-path";
    private static final File DEVICE_ROOT = new File("/");

    public PathStrategy24(Context context) {
        mAuthority = context.getPackageName() + ".com.io.FileProvider";
        Context context1 = context.getApplicationContext();
        if (context1 instanceof PublicFiles) {
            PublicFiles publicFiles = (PublicFiles) context1;
            File[] files = publicFiles.publicFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file == null)
                        continue;
                    addRoot(String.valueOf(file.hashCode()), buildPath(file, "."));
                }
            }
        } else {
            addRoot(TAG_ROOT_PATH, buildPath(DEVICE_ROOT, "."));
            addRoot(TAG_FILES_PATH, buildPath(context.getFilesDir(), "."));
            addRoot(TAG_CACHE_PATH, buildPath(context.getCacheDir(), "."));
            addRoot(TAG_EXTERNAL, buildPath(Environment.getExternalStorageDirectory(), ""));
            File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null);
            if (externalFilesDirs.length > 0) {
                addRoot(TAG_EXTERNAL_FILES, buildPath(externalFilesDirs[0], "."));
            }
            File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(context);
            if (externalCacheDirs.length > 0) {
                addRoot(TAG_EXTERNAL_CACHE, buildPath(externalCacheDirs[0], "."));
            }
        }
    }


    public void addRoot(String name, File root) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name must not be empty");
        }

        try {
            // Resolve to canonical path to keep path checking fast
            root = root.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to resolve canonical path for " + root, e);
        }
        Log.d("FileProvider", name + "  " + root.getAbsolutePath());
        mRoots.put(name, root);
    }

    @Override
    public Uri getUriForFile(File file) {
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }
        // Find the most-specific root path
        Map.Entry<String, File> mostSpecific = null;
        for (Map.Entry<String, File> root : mRoots.entrySet()) {
            final String rootPath = root.getValue().getPath();
            if (path.startsWith(rootPath) && (mostSpecific == null
                    || rootPath.length() > mostSpecific.getValue().getPath().length())) {
                mostSpecific = root;
            }
        }

        if (mostSpecific == null) {
            throw new IllegalArgumentException(
                    "Failed to find configured root that contains " + path);
        }

        // Start at first char of path under root
        final String rootPath = mostSpecific.getValue().getPath();
        if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }

        // Encode the tag and path separately
        path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
        return new Uri.Builder().scheme("content")
                .authority(mAuthority).encodedPath(path).build();
    }

    @Override
    public File getFileForUri(Uri uri) {

        String path = uri.getEncodedPath();
//uri.
        final int splitIndex = path.indexOf('/', 1);
        final String tag = Uri.decode(path.substring(1, splitIndex));
        path = Uri.decode(path.substring(splitIndex + 1));
        Log.d("getFileForUri", uri.getPath() + "  " + uri.getEncodedPath() + "  " + tag + "  " + path);
        final File root = mRoots.get(tag);
        if (root == null) {
            throw new IllegalArgumentException("Unable to find configured root for " + uri);
        }

        File file = new File(root, path);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        if (!file.getPath().startsWith(root.getPath())) {
            throw new SecurityException("Resolved path jumped beyond configured root");
        }

        return file;
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (segment != null) {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }
}
