package com.vst.greendaotest;

import android.content.Context;

class Test {
    public static Test test;
    public Context context;
    private Test(Context context){
        this.context=context;
    }
    public static Test init(Context context) {
        if (test == null)
            synchronized (Test.class) {
                if (test == null)
                    test = new Test(context);
            }
        return test;
    }
}
