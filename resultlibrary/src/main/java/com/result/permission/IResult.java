package com.result.permission;

/**
 * com.forScreen.util.result
 * 2018/9/27 17:05
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface IResult {
    /**
     * 2019/1/8 10:49
     * annotation：请求权限
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void requestPermissions(int requsetCode, PermissionCallback callback, String... permissions);

    /**
     * 2019/1/8 10:49
     * annotation：检查权限
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    String[] check(String... permissions);

    /**
     * 检查WRITE_SETTINGS权限
     * @return
     */
    boolean checkWriteSettings();


    /**
     * 申请WRITE_SETTINGS权限
      */
    void requestWriteSettings(PermissionCallback callback);

    /**
     * 检查SYSTEM_ALERT_WINDOW权限
     * @return
     */
    boolean checkSystemAlertWindow();


    /**
     * 申请SYSTEM_ALERT_WINDOW权限
     */
    void requestSystemAlertWindow(PermissionCallback callback);
}
