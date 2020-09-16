package com.plug.load;

import android.content.Context;

import com.plug.entities.ApkEntity;

import java.util.ArrayList;
import java.util.List;

class LoadApk extends ALoad implements ILoadApk {
    private List<ApkEntity> apkEntities;
    private Context context;

    LoadApk(Context context) {
        super(context);
        this.context = context;
        apkEntities = new ArrayList<>();
    }

    @Override
    public boolean load(String plugPath) {
        ApkEntity apkEntity = ApkEntity.apkPath2apkEntity(plugPath, context);
        if (apkEntity == null)
            return false;
        apkEntities.add(apkEntity);
        return true;
    }

    @Override
    public ApkEntity name2apkEntity(String packageName) {
        for (ApkEntity apkEntity : apkEntities) {
            if (apkEntity.mPackageName.equals(packageName))
                return apkEntity;
        }
        return null;
    }
}
