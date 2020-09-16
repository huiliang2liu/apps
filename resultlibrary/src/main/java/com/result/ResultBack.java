package com.result;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import com.result.activity.ResultCallback;
import com.result.permission.PermissionCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

/**
 * com.result
 * 2018/10/30 11:05
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
class ResultBack {
    private Map<Integer, ResultCallback[]> callbackMap;
    private Map<Integer, PermissionCallback> permissionCallbackMap;

    {
        callbackMap = new HashMap<>();
        permissionCallbackMap = new HashMap<>();
    }

    public void registerActivityResult(int requsetCode, ResultCallback... callbacks) {
        if (callbacks == null || callbacks.length <= 0)
            return;
        callbackMap.put(requsetCode, callbacks);
    }

    public void activityResult(int requsetCode, int resultCode, Intent intent) {
        if (callbackMap.containsKey(requsetCode)) {
            for (ResultCallback callback : callbackMap.get(requsetCode))
                callback.onActivityResult(requsetCode, resultCode, intent);
            callbackMap.remove(requsetCode);
        }
    }

    public void registerPermissionsResult(int requsetCode, PermissionCallback callback) {
        if (callback == null)
            return;
        permissionCallbackMap.put(requsetCode, callback);
    }

    public void permissionsResult(Context context, int requestCode, @NonNull String[] permissions) {
        if (permissionCallbackMap.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbackMap.get(requestCode);
            callback.result(check(context, permissions));
            permissionCallbackMap.remove(requestCode);
        }
    }

    public String[] check(Context context, String... permissions) {
        if (context == null)
            return permissions;
        if (permissions == null || permissions.length <= 0) {
            try {
                return check(context, context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
            } catch (Exception e) {
                return permissions;
            }
        }
        List<String> no = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    no.add(permission);
            }
        } else {
            try {
                String[] manifestPermissions = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
                for (String permission : permissions) {
                    boolean is = false;
                    for (String manifestPermission : manifestPermissions) {
                        if (manifestPermission.equals(permission)) {
                            is = true;
                            break;
                        }
                    }
                    if (!is)
                        no.add(permission);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return no.toArray(new String[no.size()]);
    }

    public boolean checkWriteSettings(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context != null)
                return Settings.System.canWrite(context);
            return false;
        }
        String p[] = check(context, Manifest.permission.WRITE_SETTINGS);
        return p == null || p.length <= 0;
    }

    public boolean checkSystemAlertWindow(Context context) {
        if (Build.VERSION.SDK_INT > 22) {
            if (context != null)
                return Settings.canDrawOverlays(context);
            return false;
        }
        String c[] = check(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
        return c == null || c.length <= 0;
    }
    public static Intent photo2PhotoAlbum() {
        Intent pickIt = new Intent(Intent.ACTION_PICK, null);
        pickIt.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        return pickIt;
    }
    public static Intent openCamera(@NonNull Context context, @NonNull File file, String authority) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (file != null) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists())
                parentFile.mkdirs();
            Uri imageUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(context, authority, file);//通过FileProvider创建一个content类型的Uri
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            } else
                imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        }

        return intent;
    }
    public static Intent screenshots(@NonNull Context context, @NonNull File imageFile, String authorityImage, @NonNull File saveFile, String authoritySave, int aspectX, int aspectY, int outputX,
                                     int outputY) {
        File parent = saveFile.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        Uri imageUri = null;
        Uri saveUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context, authorityImage, imageFile);
//            saveUri = FileProvider.getUriForFile(context, authoritySave, saveFile);
            //裁剪整个流程，估计授权一次就好outputUri不需要ContentUri,否则失败
            saveUri = Uri.fromFile(saveFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
            saveUri = Uri.fromFile(saveFile);
        }
        return screenshots(context, imageUri, saveUri, aspectX, aspectY, outputX, outputY);
    }

    public static Intent screenshots(@NonNull Context context, @NonNull Uri imageUri, @NonNull Uri saveUri, int aspectX, int aspectY, int outputX,
                                     int outputY) {
        Intent intent1 = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent1.setDataAndType(imageUri, "image/*");
        intent1.putExtra("crop", "true");
        intent1.putExtra("aspectX", aspectX);
        intent1.putExtra("aspectY", aspectY);
        intent1.putExtra("outputX", outputX);
        intent1.putExtra("outputY", outputY);
        intent1.putExtra("scale", true);
        // intent1.putExtra("scale", true);//黑边
        intent1.putExtra("scaleUpIfNeeded", true);// 黑边
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        intent1.putExtra("return-data", true);
        intent1.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent1.putExtra("noFaceDetection", true); // no face detection
        return intent1;
    }
}
