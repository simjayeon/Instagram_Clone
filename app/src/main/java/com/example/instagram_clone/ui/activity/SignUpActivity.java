package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram_clone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView sign_up_edit_email,
            sign_up_edit_password,
            btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        sign_up_edit_email = (EditText) findViewById(R.id.sign_up_edit_email);
        sign_up_edit_password = (EditText) findViewById(R.id.sign_up_edit_password);
        btn_signUp = (TextView) findViewById(R.id.btn_signup);

        btn_signUp.setOnClickListener(v -> signUp());

    }


    //회원가입 메소드
    public void signUp(){
        firebaseAuth.createUserWithEmailAndPassword(sign_up_edit_email.getText().toString(), sign_up_edit_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            moveMainPage(task.getResult().getUser());
                        }else{
                            Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    //로그인 페이지로 이동
    private void moveMainPage(FirebaseUser user) {
        if(user != null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    //뒤로가기 시 나타나는 이벤트
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("회원가입을 종료하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("아니오", (dialog, which) -> {
                    dialog.dismiss(); //대화상자 닫기
                }).show();
    }
}