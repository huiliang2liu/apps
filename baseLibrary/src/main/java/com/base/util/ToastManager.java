package com.base.util;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastManager {
    private Application application;

    public ToastManager(Context context) {
        application = (Application) context.getApplicationContext();
    }

    public Toast createDefault(String text) {
        return Toast.makeText(application, text, Toast.LENGTH_SHORT);
    }

    public Toast createDefault(int stringId) {
        return Toast.makeText(application, stringId, Toast.LENGTH_SHORT);
    }


    public Toast createToast() {
        return new Toast(application);
    }

    public void setGravity(Toast toast, int gravity, int startX, int startY) {
        toast.setGravity(gravity, startX, startY);
    }

    public void setDuration(Toast toast, int duration) {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG)
            duration = Toast.LENGTH_LONG;
        toast.setDuration(duration);
    }

    public void setView(Toast toast, View view) {
        toast.setView(view);
    }

//    public void toast(String text){
//        Toast toast=Toast.makeText(application,text,Toast.LENGTH_SHORT);
//        View view=toast.getView();
//        TextView textView=view.findViewById(android.R.id.message);
//    }

}
