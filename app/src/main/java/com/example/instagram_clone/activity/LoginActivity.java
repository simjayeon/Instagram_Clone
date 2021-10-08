package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram_clone.R;
import com.example.instagram_clone.data.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; // FirebaseAuth 객체의 공유 인스턴스
    EditText edit_email, edit_password;
    //UserInfo userInfo;
    //SignUpActivity signUpActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        TextView btn_signUp = (TextView) findViewById(R.id.btn_signup);

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 및 회원가입 버튼을 클릭했을 때 아래 실행
                signUp();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    //어떻게 나눌지
    public void signUp(){
        firebaseAuth.createUserWithEmailAndPassword(edit_email.getText().toString(), edit_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //회원가입 성공시
                            //FirebaseUser user = firebaseAuth.getCurrentUser();
                            moveMainPage(task.getResult().getUser()); // 뭔지 알아내기
                        }else{
                            //회원가입 실패시
                            Toast.makeText(LoginActivity.this, "회원가입 실패", Toast.LENGTH_SHORT);
                        }

                    }
                });
    }

    public void signIn(){
        firebaseAuth.signInWithEmailAndPassword(edit_email.getText().toString(), edit_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //로그인 성공 시
                            moveMainPage(task.getResult().getUser()); // 뭔지 알아내기
                        }else{
                            //회원가입 실패시
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT);
                        }

                    }
                });
    }

    public void moveMainPage(FirebaseUser user){
        if (user != null){ //파이어베이스 유저상태가 있을 경우 아래 실행
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}