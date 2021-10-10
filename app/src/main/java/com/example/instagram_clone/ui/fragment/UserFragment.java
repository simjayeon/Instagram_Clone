package com.example.instagram_clone.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.UserFragmentAdapter;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.ui.activity.LoginActivity;
import com.example.instagram_clone.ui.activity.MainActivity;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    UserFragmentAdapter userFragmentAdapter;
    RecyclerView recyclerView;
    String uid, currentUserId, selectUid;
    ImageView btn_back, toolbar_logo;
    Button btn_follow;
    TextView toolbar_user_id;
    BottomNavigationView bottomNavigationView;

    MainActivity mainActivity = new MainActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user,container,false);

        //파이어베이스
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.account_recyclerView);
        userFragmentAdapter = new UserFragmentAdapter(uid);
        btn_back = view.findViewById(R.id.btn_back);
        btn_follow = view.findViewById(R.id.btn_follow);
        toolbar_user_id = view.findViewById(R.id.toolbar_user_id);
        bottomNavigationView = view.findViewById(R.id.bottom_NaviBar);
        toolbar_logo = view.findViewById(R.id.toolbar_logo);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            uid = bundle.getString("destinationUid");
            selectUid = bundle.getString("userId");
        }else{
            System.out.println("값이 안왔슈");
        }

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        System.out.println("currentUserId이요"+currentUserId);
        if (uid != null && uid.equals(currentUserId)){
            //Mypage
            btn_follow.setText(R.string.signout);
            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    firebaseAuth.signOut();  //종료되는거 수정필요
                }
            });
        }else{
            //uid값 넘겨주기
            //otherUserpage
            btn_follow.setText(R.string.follow);

            //메인액티비티 xml 가져와야함(오류)
            toolbar_user_id.setText(uid);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomNavigationView.setSelectedItemId(R.id.action_home);
                    toolbar_logo.setVisibility(View.GONE);
                    toolbar_user_id.setVisibility(View.VISIBLE);
                    btn_back.setVisibility(View.VISIBLE);
                }
            });
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userFragmentAdapter);

        return view;
    }
}
