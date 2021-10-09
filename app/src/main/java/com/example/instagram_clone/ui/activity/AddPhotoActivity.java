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
import com.example.instagram_clone.model.ContentDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

public class AddPhotoActivity extends AppCompatActivity {
    int PICK_IMAGE_FROM_ALBUM = 0; //requestCode
    private FirebaseStorage firebaseStorage;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    Uri photoUri;
    ImageView imgView_photo;
    TextView btn_add_Photo, edit_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        imgView_photo = (ImageView) findViewById(R.id.imgView_photo);
        btn_add_Photo = (TextView) findViewById(R.id.btn_upload);
        edit_content = (TextView) findViewById(R.id.edit_content);

        //initiate 인스턴스 생성
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

        //업로드(Callback method)
        storageRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                  ContentDTO contentDTO = new ContentDTO();

                  //insert downloadUrl of image
                  contentDTO.imageUrl = uri.toString();

                  //Insert uid of user
                   contentDTO.uid = firebaseAuth.getCurrentUser().getUid(); //현재 접속된 사용자

                   //insert userId
                   contentDTO.userId = firebaseAuth.getCurrentUser().getEmail();

                   //explanin of content
                   contentDTO.explain = edit_content.getText().toString();

                   //insertTimeStamp
                   contentDTO.timestamp = System.currentTimeMillis();

                   firestore.collection("images").document().set(contentDTO);
                   setResult(RESULT_OK); // 정상적으로 닫혔다는 플래그를 넘겨주기 위해서 Result_ok 값 넘겨줌
                   finish(); //창이 닫힘

                   //업로드에는 2가지 방식이 있다
                   //첫번째는 콜백, 두번째는 promise방식

               });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}