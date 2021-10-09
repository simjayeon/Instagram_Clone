package com.example.instagram_clone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.ContentDTO;
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
    ArrayList<String> contentUidList;
    Context context;



    public RecyclerViewAdapter(ArrayList<ContentDTO> contentDTOS, Context context){
        this.contentDTOS = new ArrayList<>();
        this.context = context;
        contentUidList = new ArrayList<>();

        firestore.collection("images").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                RecyclerViewAdapter.this.contentDTOS.clear();
                contentUidList.clear();

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
        System.out.println("여기 오니?");
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        System.out.println("여기 오니?2");
        holder = ((CustomViewHolder) holder);
        ((CustomViewHolder) holder).detail_user_name.setText(contentDTOS.get(position).userId); //userName
        Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_profile_img); //프로필이미지
        Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_content_img); //콘텐츠이미지
        ((CustomViewHolder) holder).detail_favorit_count.setText(contentDTOS.get(position).favoriteCount+"명이 좋아합니다"); //userName
        ((CustomViewHolder) holder).detail_content_txt.setText(contentDTOS.get(position).explain); //userName
    }

    @Override
    public int getItemCount() {
        System.out.println("여기 오니?3");
        return contentDTOS.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView detail_profile_img;
        ImageView detail_content_img;
        TextView detail_user_name;
        TextView detail_favorit_count;
        TextView detail_content_txt;

        public CustomViewHolder(View view) {
            super(view);
            System.out.println("여기 오니?4");
            detail_profile_img = (ImageView) view.findViewById(R.id.detail_profile_img);
            detail_content_img = (ImageView) view.findViewById(R.id.detail_content_img);
            detail_user_name = (TextView) view.findViewById(R.id.detail_profile_name);
            detail_favorit_count = (TextView) view.findViewById(R.id.detail_favorit_count);
            detail_content_txt = (TextView) view.findViewById(R.id.detail_content_txt);
        }
    }
}
