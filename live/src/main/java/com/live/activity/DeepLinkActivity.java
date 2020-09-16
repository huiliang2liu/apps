package com.live.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.base.BaseActivity;
import com.google.android.gms.appindexing.AndroidAppUri;

import androidx.annotation.Nullable;

public class DeepLinkActivity extends BaseActivity {
    private final static String TAG = "DeepLinkActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT > 21) {
            Intent intent = this.getIntent();
            Uri referrerUri = intent.getData();
        String packageName = getReferrerPackage();
        getIntent().getStringExtra(Intent.EXTRA_REFERRER_NAME);
//        Uri referrerUri = this.getReferrer();
        if (referrerUri != null) {
            AndroidAppUri appUri = AndroidAppUri.newAndroidAppUri(referrerUri);
//                String referrerPackage = appUri.getPackageName();
            Log.d(TAG, referrerUri.getAuthority());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

//        }
    }
}
