package com.plug.load;

import com.plug.entities.ApkEntity;

public interface ILoadApk extends ILoad {
    public ApkEntity name2apkEntity(String packageName);
}
