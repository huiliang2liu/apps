package com.electricity;

import android.app.Activity;

import com.plug.Plug;

public class ElectrityPlugListener implements Plug.PlugListener {
    @Override
    public String transformNew(String newClass) {
        return null;
    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onNewIntent(Activity activity) {

    }
}
