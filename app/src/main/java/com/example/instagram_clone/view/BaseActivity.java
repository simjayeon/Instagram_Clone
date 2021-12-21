package com.example.instagram_clone.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.ArrayList;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    T mBinder;
    protected final String TAG;
    protected int layoutId;

    protected static final ArrayList<Activity> ActivityList = new ArrayList<>();

    public BaseActivity(int layoutId) {
        this.layoutId = layoutId;
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = (T) DataBindingUtil.setContentView(this, layoutId);
        ActivityList.add(this);
        initView(savedInstanceState);
    }

    protected abstract void initView(Bundle savedInstanceState);
}