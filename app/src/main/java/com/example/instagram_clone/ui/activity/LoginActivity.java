package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram_clone.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; // FirebaseAuth 객체의 공유 인스턴스
    private GoogleSignInClient googleSignInClient; // 구글
    private GoogleSignInOptions gso;
    private CallbackManager callbackManager;
    //private CallbackManager callbackManager;
    TextView btn_login, btn_signUp, btn_login_google, btn_login_facebook;
    EditText edit_email, edit_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (TextView) findViewById(R.id.btn_login);
        btn_signUp = (TextView) findViewById(R.id.btn_signup_start);
        btn_login_google = (TextView) findViewById(R.id.btn_google);
        btn_login_facebook = (TextView) findViewById(R.id.btn_facebook);

        firebaseAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        btn_login.setOnClickListener(this::onClick);
        btn_signUp.setOnClickListener(this::onClick);
        btn_login_google.setOnClickListener(this::onClick);
        btn_login_facebook.setOnClickListener(this::onClick);

    }


    //로그인, 회원가입 onClick 이벤트
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                signIn();
                break;
            case R.id.btn_google:
                googleLogin();
                break;
            case R.id.btn_facebook:
                facebookLogin();
                break;
            case R.id.btn_signup_start:
                signUp();
                break;

        }
    }

    //자동 로그인
    @Override
    protected void onStart() {
        super.onStart();
        moveMainPage(firebaseAuth.getCurrentUser());
    }

    //회원가입 페이지로 이동
    private void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    //로그인
    public void signIn(){
        firebaseAuth.signInWithEmailAndPassword(edit_email.getText().toString(), edit_password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        //로그인 성공 시
                        moveMainPage(task.getResult().getUser());
                        System.out.println(task.getResult().getUser()+"어떤가");
                    }else{
                        //회원가입 실패시
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }

                });
    }


    public void googleLogin(){
        Intent intent = googleSignInClient.getSignInIntent(); //구글 클라이언트에서 로그인화면으로 이동
        startActivityForResult(intent, 1);
    }

    //구글 로그인 요청
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
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
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //구글 로그인 정보 파이어베이스에 저장
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        moveMainPage(task.getResult().getUser());
                        finish();
                    }else {
                        System.out.println("계정 인증 실패");
                    }
                });
    }


    private void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        moveMainPage(task.getResult().getUser());
                    }
                });
    }




    //메인 페이지로 이동 메소드
    public void moveMainPage(FirebaseUser user){
        if (user != null){ //파이어베이스 유저상태가 있을 경우 아래 실행
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }



}
















