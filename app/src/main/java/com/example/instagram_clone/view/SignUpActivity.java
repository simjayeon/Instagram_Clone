package com.example.instagram_clone.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding> {
    FirebaseAuth firebaseAuth;

    public SignUpActivity() {
        super(R.layout.activity_sign_up);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        mBinder.btnSignup.setOnClickListener(v -> signUp());
    }


    //회원가입 메소드
    public void signUp() {
        firebaseAuth.createUserWithEmailAndPassword(mBinder.signUpEditEmail.getText().toString(), mBinder.signUpEditPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        moveMainPage(task.getResult().getUser());
                    } else {
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }

                });
    }


    //로그인 페이지로 이동
    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    //뒤로가기 시 나타나는 이벤트
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("회원가입을 종료하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("아니오", (dialog, which) -> {
                    dialog.dismiss(); //대화상자 닫기
                }).show();
    }
}