package com.example.instagram_clone.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.model.FollowDTO;
import com.example.instagram_clone.ui.activity.LoginActivity;
import com.example.instagram_clone.ui.activity.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class UserFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    UserFragmentAdapter userFragmentAdapter;
    RecyclerView recyclerView;
    String uid, currentUserId, selectUserid;
    ImageView btn_back, toolbar_logo, account_iv_profile;
    Button btn_follow;
    TextView toolbar_user_id, account_tv_following_count,
            account_tv_follower_count, account_post_count,
            user_page_id;
    BottomNavigationView bottomNavigationView;
    int PICK_PROFILE_FROM_ALBUM = 55;

    FollowDTO followDTO = new FollowDTO();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user,container,false);

        //파이어베이스
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.account_recyclerView);
        userFragmentAdapter = new UserFragmentAdapter(uid);
        btn_follow = view.findViewById(R.id.btn_follow);
        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        bottomNavigationView = view.findViewById(R.id.bottom_NaviBar);
        toolbar_logo = view.findViewById(R.id.toolbar_logo);
        account_tv_following_count = view.findViewById(R.id.account_tv_following_count);
        account_tv_follower_count = view.findViewById(R.id.account_tv_follower_count);
        account_post_count = view.findViewById(R.id.account_tv_post_count);
        user_page_id = view.findViewById(R.id.user_page_id);



        Bundle bundle = this.getArguments();
        if(bundle != null){
            uid = bundle.getString("destinationUid");
            selectUserid = bundle.getString("userId");
        }

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        //상대 프로필잉ㄴ지 나인지 확인하기
        if (uid != null && uid.equals(currentUserId)){
            //프로필이 나일 때
            user_page_id.setText(selectUserid); //프로필 이름 변경
            btn_follow.setText(R.string.signout); //로그아웃으로 변경
            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    firebaseAuth.signOut();  //종료되는거 수정필요
                }
            });
        }else if(uid != null && !uid.equals(currentUserId)){
            //프로필이 다른 사람일 때
            user_page_id.setText(selectUserid);
            btn_follow.setText(R.string.follow);
            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFollow(uid, currentUserId);
                }
            });
        }


        //유저프래그먼트에 프로필 사진 올리기
        account_iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                startActivityForResult(photoPickIntent, PICK_PROFILE_FROM_ALBUM);
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userFragmentAdapter);
        getProfileImage();
        ///getFollowerAndFollowing();

        return view;
    }


    //데이터베이스에서 이미지 가져오기 (오류가 여기인가)
    public void getProfileImage(){
        firestore.collection("profileImages").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getData() != null){
                    String url = value.getData().toString();
                    Glide.with(getActivity()).load(url).apply(new RequestOptions().circleCrop()).into(account_iv_profile);
                }
            }
        });
    }
/*

    //팔로워 값 변경
    public void getFollowerAndFollowing(){
        firestore.collection("users").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                followDTO = value.toObject(FollowDTO.class);

                if (followDTO.followingCount != null ){
                    account_tv_following_count.setText(String.valueOf(followDTO.followingCount));
                }
                if (followDTO.followerCount != null){
                    account_tv_follower_count.setText(String.valueOf(followDTO.followerCount));

                    //내가 팔로워를 하고 있으면면
                   if(followDTO.followers.containsKey(currentUserId)){
                       btn_follow.setText(getString(R.string.follow_cancel));
                   }else{
                       btn_follow.setText(getString(R.string.follow));
                   }
                }
            }
        });
    }
 */

    //상대방 계정에는 또다른 팔로워
    // 내 계정에는 상대방 누구를 팔로워 하는지
    // 과정ㅇ의 트랜잭션
    //먼저 팔로워 리스트에 값이 들어가야할 것 같음 -> 내가 누구를 팔로워/팔로잉 했는지
    public void requestFollow(String uid, String currentUserId){
        // ㅐㄴ 팔로워
        this.currentUserId = currentUserId; //현재 사용자
        this.uid = uid;
        firestore.runTransaction(transaction -> {
            DocumentReference doFollowing = firestore.collection("users").document(this.currentUserId);
            FollowDTO followDTO = transaction.get(doFollowing).toObject(FollowDTO.class);

            System.out.println(this.currentUserId+"current---------"+ followDTO + "몇?");

            //get을 put으로
            //documentRef 쓰는게 맞는지
            //디비에 안 넣어지는게 문제임
            //조건 1
            if(followDTO == null){
                followDTO.followingCount = 1;
                followDTO.followers.get(this.uid);
                transaction.set(doFollowing, followDTO); //db에 담는 것
            }

            //조건 2
            if(followDTO.followings.containsKey(this.uid)){
                //이미 팔로워 된 uid일 경우
                followDTO.followingCount = followDTO.followingCount - 1; //팔로워 취소
                followDTO.followers.remove(this.uid); //uid삭제
            }else{
                //팔로워가 되어있지 않은 uid일 경우
                followDTO.followingCount = followDTO.followingCount + 1; //팔로워
                followDTO.followers.put(this.uid, true); //uid등록
                followerAlarm(this.uid);
            }

            return transaction.set(doFollowing, followDTO); //db에 저장
        });




        //내가 팔로잉 한 상대방 계정
        DocumentReference doFollower = firestore.collection("users").document(this.uid);
        firestore.runTransaction(transaction -> {
            followDTO = transaction.get(doFollower).toObject(FollowDTO.class);
            if(followDTO == null){
                followDTO.followerCount = 1;
                followDTO.followers.get(this.currentUserId);
                transaction.set(doFollower, followDTO);
            }

            if(followDTO.followers.containsKey(this.currentUserId)){
                followDTO.followerCount = followDTO.followerCount - 1;
                followDTO.followers.remove(this.currentUserId);
            }else{
                followDTO.followerCount = followDTO.followerCount + 1;
                followDTO.followers.put(this.currentUserId, true);
                followerAlarm(this.currentUserId);
            }
            return transaction.set(doFollower, followDTO);
        });

    }

    //팔로워 알람
    public void followerAlarm(String destinationUid){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.destinationUid = destinationUid;
        alarmDTO.userId = firebaseAuth.getCurrentUser().getEmail();
        alarmDTO.uid = firebaseAuth.getCurrentUser().getUid();
        alarmDTO.kind = 2;
        alarmDTO.timestamp = System.currentTimeMillis();
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);

    }

    //////////////////////////리사이클러뷰 어댑터///////////////////////////////////////
    public class UserFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<ContentDTO> contentDTOS;
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
                        contentDTOS.add(doc.toObject(ContentDTO.class));
                    }
                    account_post_count.setText(String.valueOf(contentDTOS.size()));
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
            ImageView imageView_profile;

            public CustomViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView = imageView; //??
                imageView_profile = imageView.findViewById(R.id.account_iv_profile);

            }
        }
    }
}
