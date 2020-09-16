package com.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.result.Result;
import com.result.ResultImpl;

public abstract class BaseFragment extends Fragment {
    private final static String TAG = "BaseFragment";
    private boolean visible = false;
    protected Result result;
    protected BaseApplication application;
    protected View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = new ResultImpl(this);
        application = (BaseApplication) getContext().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(layout(), null);
            bindView();
        }
        return view;
    }

    public void bindView() {

    }


    public abstract int layout();


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (visible)
                inVisible();
            visible = false;
        } else {
            if (!visible)
                visible();
            visible = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!visible)
                visible();
            visible = true;
        } else {
            if (visible)
                inVisible();
            visible = false;
        }
    }

    protected void visible() {
        Log.e(TAG, "visible");
    }

    protected void inVisible() {
        Log.e(TAG, "invisible");
    }
}
