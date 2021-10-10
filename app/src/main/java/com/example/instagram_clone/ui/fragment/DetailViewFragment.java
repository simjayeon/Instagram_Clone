package com.example.instagram_clone.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.ui.activity.CommentActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DetailViewFragment extends Fragment {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ContentDTO> contentDTOS = new ArrayList<>();
    ArrayList<String> contentUidList = new ArrayList<>();
    Fragment fragment_user = new UserFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, container, false);
        FirebaseApp.initializeApp(getActivity());

        firestore = FirebaseFirestore.getInstance(); //인스턴스 초기화
        recyclerView = view.findViewById(R.id.detail_recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(contentDTOS, getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);


        return view;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<ContentDTO> contentDTOS;
        ArrayList<String> contentUidList = new ArrayList<>();
        Context context;
        String uid;


        public RecyclerViewAdapter(ArrayList<ContentDTO> contentDTOS, Context context) {
            this.contentDTOS = new ArrayList<>();
            this.context = context;


            firestore.collection("images").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    RecyclerViewAdapter.this.contentDTOS.clear();
                    contentUidList.clear();
                    if (value == null) {
                        //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                    }

                    for (QueryDocumentSnapshot doc : value) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            holder = ((CustomViewHolder) holder);
            ((CustomViewHolder) holder).detail_user_name.setText(contentDTOS.get(position).userId); //userName
            //메인 프로필사진
            ///Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_profile_img); //프로필이미지
            Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_content_img); //콘텐츠이미지
            ((CustomViewHolder) holder).detail_favorit_count.setText(contentDTOS.get(position).favoriteCount + "명이 좋아합니다"); //userName
            ((CustomViewHolder) holder).detail_content_txt.setText(contentDTOS.get(position).explain); //userName

            //좋아요 버튼 클릭
            ((CustomViewHolder) holder).btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoritEvent(position);
                }
            });

            //좋아요 하트 채워지기 이벤트(수정필요)
            if (contentDTOS.get(position).favorities.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite);
            } else {
                ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite_border);
            }

            ((CustomViewHolder) holder).detail_profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("destinationUid", contentDTOS.get(position).uid);
                    bundle.putString("userId", contentDTOS.get(position).userId);
                    System.out.println(contentDTOS.get(position).userId + "userId랑 uid랑" + contentDTOS.get(position).uid);
                    fragment_user.setArguments(bundle);

                    //getActivity값을 못 받아옴
                    if (getActivity() != null) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content, fragment_user)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        System.out.println(getActivity() + "값 안옴");
                    }
                }
            });


            /*댓글 버튼 누르면 실행되는 이벤트*/

            ((CustomViewHolder) holder).btn_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getView().getContext(), CommentActivity.class);
                    intent.putExtra("contentUid", contentUidList.get(position));
                    intent.putExtra("destinationUid", contentDTOS.get(position).uid);
                    startActivity(intent);
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
            ImageView btn_comment;
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
                btn_comment = (ImageView) view.findViewById(R.id.btn_comment);
            }
        }






        //좋아요 누르기 이벤트
        //좋아요 한번 ㄴ씩만 누를 수 있게 수정 필요
        public void favoritEvent(int position) {
            firestore = FirebaseFirestore.getInstance();

            firestore.runTransaction(transaction -> {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ContentDTO contentDTO = transaction.get(firestore.collection("images")
                        .document(contentUidList.get(position))).toObject(ContentDTO.class);

                if (contentDTO.favorities.containsKey(uid)) {
                    //좋아요가 눌렸을 때 - 좋아요를 취소하는 이벤트
                    //눌린 상태여서 취소해야하기 때문에 좋아요 개수 -1과 좋아요 누른 유저의 정보를 삭제해야함
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1;
                    contentDTO.favorities.remove(uid);
                } else {
                    //좋아요가 눌려있지 않을 때 - 좋아요를 누르는 이벤트
                    //좋아요가 눌리지 않은 상태라서 좋아요를 누르면 개수 +1과 좋아요 누른 유저의 정보가 등록되어야 함
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1;
                    contentDTO.favorities.get(uid);
                    favoriteAlarm(contentDTOS.get(position).uid); //카운터가 올라가는 사람이름 알림
                }

                //트랜잭션을 다시 서버로 돌려준다.\

                return transaction.set(firestore.collection("images")
                        .document(contentUidList.get(position)), contentDTO);
            });
        }



        //좋아요 알람
        public void favoriteAlarm(String destinationUid){
            AlarmDTO alarmDTO = new AlarmDTO();
            alarmDTO.destinationUid = destinationUid;
            alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            alarmDTO.kind = 0;
            alarmDTO.timestamp = System.currentTimeMillis();
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);
        }
    }
}
