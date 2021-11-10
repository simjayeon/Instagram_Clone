package com.example.instagram_clone.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.instagram_clone.R;
import com.example.instagram_clone.ui.fragment.AlarmFragment;
import com.example.instagram_clone.ui.fragment.DetailViewFragment;
import com.example.instagram_clone.ui.fragment.GridFragment;
import com.example.instagram_clone.ui.fragment.UserFragment;
import com.facebook.login.Login;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragment_detail;
    Fragment fragment_user;
    Fragment fragment_alarm;
    Fragment fragment_grid;

    Button btn_follow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //READ_EXTERNAL_STORAGE : 애플리케이션이 외부 저장소에서 읽을 수 있도록 설정
        //외부 저장소를 읽을 수 있도록 매니페스트에 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        fragment_alarm = new AlarmFragment();
        fragment_user = new UserFragment();
        fragment_detail = new DetailViewFragment();
        fragment_grid = new GridFragment();

        btn_follow = findViewById(R.id.btn_follow);

        //하단바 네비게이션 선택 이벤트 -> 아이템 클릭할 때 프래그먼트 교체 작업
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_NaviBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_detail).commit();
                    return true;
                case R.id.action_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_grid).commit();
                    return true;
                case R.id.action_add_photo:

                    //권한 요청이 허용이 되었는지 selfCheck 후 권한이 grandted일 경우 AddPhotoActivity로 전환
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED){
                        startActivity(new Intent(MainActivity.this, AddPhotoActivity.class));
                    }
                    return true;
                case R.id.action_favorite_alarm:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_alarm).commit();
                    return true;
                case R.id.action_account:
                    //uid값 넘겨주기
                    Bundle bundle = new Bundle();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    bundle.putString("destinationUid", uid);
                    fragment_user.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_user).commit();
                    return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    //데이터베이스에 프로필 이미지 올리기 (storage에서 꺼내오는 법으로 변경이 필요함 -> 파이어스토어에 잘못 저장되고 있음)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //String profileFileName = "IMAGE_" + uid + ".png";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userProfileImages").child(uid);
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                //uri인지 url인지 수정 필요
                String url = storageRef.getDownloadUrl().toString();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(uid, url);
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map);
            });


        }
    }

}
