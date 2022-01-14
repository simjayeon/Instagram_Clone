package com.example.instagram_clone.view;

import android.os.Bundle;
import android.view.View;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityFollowRequireAlarmBinding;

public class FollowRequireAlarmActivity extends BaseActivity<ActivityFollowRequireAlarmBinding> implements View.OnClickListener {

    public FollowRequireAlarmActivity() {
        super(R.layout.activity_follow_require_alarm);
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