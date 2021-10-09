package com.example.instagram_clone.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.RecyclerViewAdapter;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.ui.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class DetailViewFragment extends Fragment {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ContentDTO> contentDTOS = new ArrayList<>();
    ArrayList<String> contentUidList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail,container,false);

        firestore = FirebaseFirestore.getInstance(); //인스턴스 초기화
        recyclerView = view.findViewById(R.id.detail_recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(contentDTOS, getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }
}
