package com.example.instagram_clone.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

public class AddPhotoActivity extends AppCompatActivity {
    int PICK_IMAGE_FROM_ALBUM = 0; //requestCode
    private FirebaseStorage firebaseStorage;
    Uri photoUri;
    ImageView imgView_photo;
    TextView btn_add_Photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        imgView_photo = (ImageView) findViewById(R.id.imgView_photo);
        btn_add_Photo = (TextView) findViewById(R.id.btn_upload);

        //initiate storage 인스턴스 생성
        firebaseStorage = FirebaseStorage.getInstance();

        //ㅐㅇㄹ범 열기
        Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
        photoPickIntent.setType("image/*");
        //photoPickIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoPickIntent, PICK_IMAGE_FROM_ALBUM );

        btn_add_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentUpload();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == RESULT_OK){ //사진을 선택했을 때
                //사진을 선택했을 때 이미지의 경로가 여기로 넘어옴
                photoUri = data.getData();
                Glide.with(AddPhotoActivity.this).load(photoUri).into(imgView_photo);
                //imgView_photo.setImageURI(photoUri);


            }else{ //취소버튼을 눌렀을 때 작동하는 부분
               // onDestroy();
            }
        }
    }

    public void contentUpload(){
        //make file name
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyymmdd_HHmmss");
        timeStamp.format(System.currentTimeMillis());
        String imageFileName = "IMAGE_" + timeStamp.toString() + "+.png";

        StorageReference storageRef = firebaseStorage.getReference().child("images").child(imageFileName);

        //업로드
        storageRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddPhotoActivity.this, "업로드 성공",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}