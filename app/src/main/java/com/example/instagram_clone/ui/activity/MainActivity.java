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

import com.example.instagram_clone.R;
import com.example.instagram_clone.ui.fragment.AlarmFragment;
import com.example.instagram_clone.ui.fragment.DetailViewFragment;
import com.example.instagram_clone.ui.fragment.GridFragment;
import com.example.instagram_clone.ui.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;

    BottomNavigationView bottomNavigationView;
    Fragment fragment_detail;
    Fragment fragment_user;
    Fragment fragment_alarm;
    Fragment fragment_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        fragment_alarm = new AlarmFragment();
        fragment_user = new UserFragment();
        fragment_detail = new DetailViewFragment();
        fragment_grid = new GridFragment();


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_NaviBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_detail).commit();
                        break;
                    case R.id.action_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_grid).commit();
                        break;
                    case R.id.action_add_photo:
                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED){
                            startActivity(new Intent(MainActivity.this, AddPhotoActivity.class));
                        }
                        break;
                    case R.id.action_favorite_alarm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_alarm).commit();
                        break;
                    case R.id.action_account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_user).commit();
                        break;


                }
                return false;
            }
        });

    }
}