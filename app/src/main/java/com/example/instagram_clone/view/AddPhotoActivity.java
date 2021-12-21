package com.example.instagram_clone.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityAddPhotoBinding;
import com.example.instagram_clone.model.ContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

public class AddPhotoActivity extends BaseActivity<ActivityAddPhotoBinding> {
    final int PICK_IMAGE_FROM_ALBUM = 0; //requestCode
    private FirebaseStorage firebaseStorage;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    Uri photoUri;
    String message;

    public AddPhotoActivity() {
        super(R.layout.activity_add_photo);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        mBinder.iconBackBtn

        //initiate 인스턴스 생성
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mBinder.imgViewPhoto.setOnClickListener(view -> pickAlbum());

        pickAlbum();


        //TextInputLayout : editText 또는 TextInputEditText를 좀 더 유연하게 보여주기 위한 layout
        //addTextChangedListener : 입력 시점에 따라 이벤트를 설정
        //TextWatcher()를 통해 EditText에 추가하여 텍스트를 변경할 때 호출됨
        mBinder.editContent.addTextChangedListener(new TextWatcher() {
            @Override  //입력하기 전에 변화
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //EditTex에 변경이 생겼을 때 나타나는 메소드
                message = mBinder.editContent.getText().toString();
                if (message.length() == 0) {
                    //입력받은 message가 0일 경우 버튼을 사용할 수 없음
                    //메시지를 입력 받으면 업로드 버튼의 enabled와 색상이 변경됨
                    mBinder.btnUpload.setEnabled(false);
                    mBinder.btnUpload.setBackgroundColor(Color.GRAY);
                } else {
                    //입력받은 message가 0이 아닐 경우 버튼을 사용할 수 있음
                    mBinder.btnUpload.setEnabled(true);
                    mBinder.btnUpload.setBackgroundColor(Color.BLUE);
                }
            }

            @Override //입력이 끝난 후의 변화
            public void afterTextChanged(Editable editable) {
            }
        });

        //업로드 버튼 눌렀을 때 이벤트 -> contentUpload() 호출
        mBinder.btnUpload.setOnClickListener(v -> contentUpload());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == RESULT_OK) { //사진을 선택했을 때
                //사진을 선택했을 때 이미지의 경로가 여기로 넘어옴
                photoUri = data.getData();
                Glide.with(AddPhotoActivity.this).load(photoUri).into(mBinder.imgViewPhoto);
                //imgView_photo.setImageURI(photoUri);

            } else { //취소버튼을 눌렀을 때 작동하는 부분
                finish();
            }
        }
    }

    //게시물 업로드 메소드
    public void contentUpload() {
        //SimpleDateFormat을 이용하여 yyyy(년)MM(월)dd(일)HH(시)mm(분)ss(초)로 timeStamp에 기록함
        //timeStamp는 시스템의 현재 시간을 측정하여 날짜 형식으로 변환함
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
        timeStamp.format(System.currentTimeMillis());
        //timeStamp 앞에 IMAGE_, 뒤에 .png를 붙여 이미지 파일명을 생성함
        String imageFileName = "IMAGE_" + timeStamp.toString() + "+.png";

        //구글 스토리지에서 images에 있는 이미지 파일을 참조함
        StorageReference storageRef = firebaseStorage.getReference().child("images").child(imageFileName);

        //업로드(Callback method)
        //참조한 스토리지에서 photoUri
        storageRef.putFile(photoUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    ContentDTO contentDTO = new ContentDTO();
                    //insert downloadUrl of image
                    contentDTO.imageUrl = uri.toString();
                    //Insert uid of user
                    contentDTO.uid = firebaseAuth.getCurrentUser().getUid(); //현재 접속된 사용자
                    //insert userId
                    contentDTO.userId = firebaseAuth.getCurrentUser().getEmail();
                    //explanin of content
                    contentDTO.explain = mBinder.editContent.getText().toString();
                    //insertTimeStamp
                    contentDTO.timestamp = System.currentTimeMillis();

                    firestore.collection("images").document().set(contentDTO);
                    setResult(RESULT_OK); // 정상적으로 닫혔다는 플래그를 넘겨주기 위해서 Result_ok 값 넘겨줌
                    finish();
                }));
    }

    public void pickAlbum() {
        //외부 저장소의 "image/*" 열기
        Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
        photoPickIntent.setType("image/*");
        //선택한 사진의 값을 받아오기 위해 startActivityForResult()를 사용함
        startActivityForResult(photoPickIntent, PICK_IMAGE_FROM_ALBUM);
    }
}