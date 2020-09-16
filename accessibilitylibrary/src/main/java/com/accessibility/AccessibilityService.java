package com.accessibility;


import android.app.Notification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
    private static final String TAG = "AccessibilityService";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "连接成功");
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "连接中断");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "连接销毁");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, event.getPackageName().toString());
        AccessibilityNodeInfo parent = getRootInActiveWindow();
        try {
            AccessibilityNodeInfo xiyan = parent.findAccessibilityNodeInfosByText("奚岩").get(0);
            if (xiyan != null) {
                parent = xiyan.getParent();
                xiyan = parent.getParent();
                for (int i = 0; i < xiyan.getChildCount(); i++) {
                    AccessibilityNodeInfo info = xiyan.getChild(i);
                    if (info.equals(parent))
                        continue;
                    for (int j=0;j<info.getChildCount();j++){
                        AccessibilityNodeInfo child=info.getChild(j);
                        if(ImageView.class.isAssignableFrom(Class.forName(child.getClassName().toString()))){
                            if(child.isClickable())
                                child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }

//
//                    if(info.getClassName())
                    Log.d(TAG, info.getClassName().toString());
                }
//                findAccessibilityNodeInfosByViewId("are").get(0);
//                xiyan.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        nodeInfo.getR
//        AccessibilityNodeInfo nodeInfo=;
//        nodeInfo.
    }
}
