package com.example.instagram_clone.ui.fragment;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagram_clone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GridFragment extends Fragment {
    View fragmentView;
    FirebaseFirestore firestore;
    String uid;
    FirebaseAuth auth;
    String currentUserUid;
    Button account_btn_follow_signout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_grid, container, false);
     /*   uid = getArguments().getString("destinationUid");
        firestore = FirebaseFirestore.getInstance();
        currentUserUid = auth.getCurrentUser().getUid();

        if(uid == currentUserUid){
            // 회원정보 페이지
            account_btn_follow_signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }*/

        return fragmentView;
    }
}
