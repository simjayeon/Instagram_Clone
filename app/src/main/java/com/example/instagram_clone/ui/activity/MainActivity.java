package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagram_clone.R;
import com.example.instagram_clone.ui.fragment.AlarmFragment;
import com.example.instagram_clone.ui.fragment.DetailViewFragment;
import com.example.instagram_clone.ui.fragment.GridFragment;
import com.example.instagram_clone.ui.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragment_detail;
    Fragment fragment_user;
    Fragment fragment_alarm;
    Fragment fragment_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_detail).commit(); //수정필요
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