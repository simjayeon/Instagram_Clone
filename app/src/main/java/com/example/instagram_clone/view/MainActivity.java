package com.example.instagram_clone.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {
    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinder.mainDmBtn.setOnClickListener(this);

        //READ_EXTERNAL_STORAGE : 애플리케이션이 외부 저장소에서 읽을 수 있도록 설정
        //외부 저장소를 읽을 수 있도록 매니페스트에 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        Fragment alarmFragment = new AlarmFragment();
        Fragment userFragment = new UserFragment();
        Fragment homeFragment = new HomeFragment();
        Fragment gridFragment = new GridFragment();

        //하단바 네비게이션 선택 이벤트 -> 아이템 클릭할 때 프래그먼트 교체 작업
        mBinder.bottomNaviBar.setOnNavigationItemSelectedListener(item -> {
            int position = item.getItemId();
            Fragment selected = null;
            if (position == R.id.action_home) {
                selected = homeFragment;
            } else if (position == R.id.action_search) {
                selected = gridFragment;
            } else if (position == R.id.action_add_photo) {
//                여기 수정@!!!!
                //권한 요청이 허용이 되었는지 selfCheck 후 권한이 grandted일 경우 AddPhotoActivity로 전환
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, AddPhotoActivity.class));
                }
            } else if (position == R.id.action_favorite_alarm) {
                selected = alarmFragment;
            } else if (position == R.id.action_account) {
                //uid값 넘겨주기
                Bundle bundle = new Bundle();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                bundle.putString("destinationUid", uid);
                userFragment.setArguments(bundle);
                selected = userFragment;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selected).commit();
            return false;
        });

        mBinder.bottomNaviBar.setSelectedItemId(R.id.action_home);
    }

    //데이터베이스에 프로필 이미지 올리기 (storage에서 꺼내오는 법으로 변경이 필요함 -> 파이어스토어에 잘못 저장되고 있음)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.main_dm_btn) {
            Intent intent = new Intent(this, DirectMessageActivity.class);
            startActivity(intent);
        }
    }
}
