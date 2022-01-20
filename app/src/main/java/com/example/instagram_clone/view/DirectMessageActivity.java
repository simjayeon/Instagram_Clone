package com.example.instagram_clone.view;

import android.os.Bundle;
import android.view.View;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityDirectMessageBinding;

public class DirectMessageActivity extends BaseActivity<ActivityDirectMessageBinding> implements View.OnClickListener {

    public DirectMessageActivity() {
        super(R.layout.activity_direct_message);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinder.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_back) {
            onBackPressed();
        }
    }
}