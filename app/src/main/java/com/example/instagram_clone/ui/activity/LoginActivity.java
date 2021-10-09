package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram_clone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; // FirebaseAuth 객체의 공유 인스턴스
    private GoogleSignInClient googleSignInClient; // 구글
    private GoogleSignInOptions gso;

    EditText edit_email, edit_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        TextView btn_signUp = (TextView) findViewById(R.id.btn_signup);
        TextView btn_login_google = (TextView) findViewById(R.id.btn_google);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);



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

        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin();
            }
        });


    }

    //자동로그인
    @Override
    protected void onStart() {
        super.onStart();
        moveMainPage(firebaseAuth.getCurrentUser());
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
                            //다이얼로그로 변경하기
                            Toast.makeText(LoginActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void googleLogin(){
        Intent intent = googleSignInClient.getSignInIntent(); //구글 클라이언트에서 로그인화면으로 이동
        startActivityForResult(intent, 1);
    }

    public void moveMainPage(FirebaseUser user){
        if (user != null){ //파이어베이스 유저상태가 있을 경우 아래 실행
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    //로그인 요청
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                //로그인 성공일 경우
                try{
                    GoogleSignInAccount acct = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(acct.getIdToken());

                }catch (ApiException e){

                }

            }else{
                //로그인 실패일 경우
            }
        }
    }

    //파이어베이스로 값 저장 (뺄까 고민중)
    private void firebaseAuthWithGoogle(String idToken) {

    }

}
















