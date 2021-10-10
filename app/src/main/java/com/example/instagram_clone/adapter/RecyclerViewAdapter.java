package com.example.instagram_clone.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.ui.fragment.UserFragment;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<ContentDTO> contentDTOS;
    ArrayList<String> contentUidList = new ArrayList<>();
    Context context;
    String uid;
    public Fragment fragment_user = new Fragment();

    public RecyclerViewAdapter(ArrayList<ContentDTO> contentDTOS, Context context){
        this.contentDTOS = new ArrayList<>();
        this.context = context;


        firestore.collection("images").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                RecyclerViewAdapter.this.contentDTOS.clear();
                contentUidList.clear();
                if(value == null){
                    //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                }

                for(QueryDocumentSnapshot doc : value){
                    RecyclerViewAdapter.this.contentDTOS.add(doc.toObject(ContentDTO.class));
                    contentUidList.add(doc.getId());
                }
                notifyDataSetChanged(); //새로고치ㅣㅁ되도록
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder = ((CustomViewHolder) holder);
        ((CustomViewHolder) holder).detail_user_name.setText(contentDTOS.get(position).userId); //userName
        Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_profile_img); //프로필이미지
        Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_content_img); //콘텐츠이미지
        ((CustomViewHolder) holder).detail_favorit_count.setText(contentDTOS.get(position).favoriteCount+"명이 좋아합니다"); //userName
        ((CustomViewHolder) holder).detail_content_txt.setText(contentDTOS.get(position).explain); //userName

        //좋아요 버튼 클릭
        ((CustomViewHolder) holder).btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritEvent(position);
            }
        });

        //좋아요 하트 채워지기 이벤트(수정필요)
        if(contentDTOS.get(position).favorities.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite);
        }else{
            ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite_border);
        }

        ((CustomViewHolder) holder).detail_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("destinataionUid", contentDTOS.get(position).uid);
                bundle.putString("userId", contentDTOS.get(position).userId);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                fragment_user.setArguments(bundle);
                fragment_user.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment_user).addToBackStack(null).commit();
                System.out.println("########################################################################################");
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentDTOS.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView detail_profile_img;
        ImageView detail_content_img;
        ImageView btn_favorite;
        TextView detail_user_name;
        TextView detail_favorit_count;
        TextView detail_content_txt;

        public CustomViewHolder(View view) {
            super(view);
            detail_profile_img = (ImageView) view.findViewById(R.id.detail_profile_img);
            detail_content_img = (ImageView) view.findViewById(R.id.detail_content_img);
            detail_user_name = (TextView) view.findViewById(R.id.detail_profile_name);
            detail_favorit_count = (TextView) view.findViewById(R.id.detail_favorit_count);
            detail_content_txt = (TextView) view.findViewById(R.id.detail_content_txt);
            btn_favorite = (ImageView) view.findViewById(R.id.btn_favorite);
        }
    }


    //좋아요 누르기 이벤트
    public void favoritEvent(int position){
        firestore  = FirebaseFirestore.getInstance();

        firestore.runTransaction(transaction -> {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ContentDTO contentDTO = transaction.get(firestore.collection("images")
                    .document(contentUidList.get(position))).toObject(ContentDTO.class);

            if(contentDTO.favorities.containsKey(uid)){
                //좋아요가 눌렸을 때 - 좋아요를 취소하는 이벤트
                //눌린 상태여서 취소해야하기 때문에 좋아요 개수 -1과 좋아요 누른 유저의 정보를 삭제해야함
                contentDTO.favoriteCount = contentDTO.favoriteCount -1;
                contentDTO.favorities.remove(uid);
            }else{
                //좋아요가 눌려있지 않을 때 - 좋아요를 누르는 이벤트
                //좋아요가 눌리지 않은 상태라서 좋아요를 누르면 개수 +1과 좋아요 누른 유저의 정보가 등록되어야 함
                contentDTO.favoriteCount = contentDTO.favoriteCount +1;
                contentDTO.favorities.get(uid);
            }

            //트랜잭션을 다시 서버로 돌려준다.\

            return  transaction.set(firestore.collection("images")
                    .document(contentUidList.get(position)),contentDTO);
        });
    }
}
