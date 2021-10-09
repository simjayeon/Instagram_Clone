package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.ui.fragment.AlarmFragment;
import com.example.instagram_clone.ui.fragment.DetailViewFragment;
import com.example.instagram_clone.ui.fragment.GridFragment;
import com.example.instagram_clone.ui.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;

    BottomNavigationView bottomNavigationView;
    Fragment fragment_detail;
    Fragment fragment_user;
    Fragment fragment_alarm;
    Fragment fragment_grid;

    ImageView btn_back, toolbar_logo;
    Button btn_follow;
    TextView toolbar_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        fragment_alarm = new AlarmFragment();
        fragment_user = new UserFragment();
        fragment_detail = new DetailViewFragment();
        fragment_grid = new GridFragment();

        btn_back =findViewById(R.id.btn_back);
        btn_follow = findViewById(R.id.btn_follow);
        toolbar_user_id = findViewById(R.id.toolbar_user_id);
        toolbar_logo = findViewById(R.id.toolbar_logo);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_NaviBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setToolbarDefault();
                switch (item.getItemId()){
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_detail).commit();
                        return true;
                    case R.id.action_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_grid).commit();
                        return true;
                    case R.id.action_add_photo:
                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED){
                            startActivity(new Intent(MainActivity.this, AddPhotoActivity.class));
                        }
                        return true;
                    case R.id.action_favorite_alarm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_alarm).commit();
                        return true;
                    case R.id.action_account:
                        //uid값 넘겨주기
                        Bundle bundle = new Bundle(); //??
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        bundle.putString("destinationUid", uid);
                        fragment_user.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_user).commit();
                        return true;


                }
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    public void setToolbarDefault(){
        toolbar_user_id.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        toolbar_logo.setVisibility(View.VISIBLE);
    }
}