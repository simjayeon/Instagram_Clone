package com.example.instagram_clone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.ContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<ContentDTO> contentDTOS;
    ArrayList<String> contentUidList = new ArrayList<>();
    String uid;




    public UserFragmentAdapter(String uid){
        contentDTOS = new ArrayList<>();
        this.uid = uid;
        //파이어스토어에서 데이터 값 읽어오기
        //내가 올린 이미지만 뜰 수 있도록.whereEqualTo("uid", uid) -> uid 값이 내 uid값 일때만

        firestore.collection("images").whereEqualTo("uid", uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null){
                    //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                }

                //e데이터를 받아주는 부분
                for(QueryDocumentSnapshot doc : value){
                    UserFragmentAdapter.this.contentDTOS.add(doc.toObject(ContentDTO.class));
                }
                notifyDataSetChanged(); //새로고치ㅣㅁ되도록
            }
        });
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //화면의 폭을 가져옴
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;

        //폭의 값을 이미지뷰에 넣음 (정사각형 이미지)
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

        return new CustomViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load(contentDTOS.get(position).imageUrl)
                .centerCrop()
                .into(((CustomViewHolder) holder).imageView);

    }

    @Override
    public int getItemCount() {
        return contentDTOS.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CustomViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView; //??

        }
    }
}
