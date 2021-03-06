package com.example.instagram_clone.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.model.FollowDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    UserFragmentAdapter userFragmentAdapter;
    RecyclerView recyclerView;
    String uid, currentUserId, selectUserId;
    ImageView account_iv_profile, btnFollowRequireAlarm, btnDirectMessage;
    Button btn_follow;
    TextView account_tv_following_count,
            account_tv_follower_count, account_post_count,
            user_page_id, followRequireBadge;
    BottomNavigationView bottomNavigationView;
    int PICK_PROFILE_FROM_ALBUM = 55;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user, container, false);

        //파이어베이스
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userFragmentAdapter = new UserFragmentAdapter(uid);

        recyclerView = view.findViewById(R.id.account_recyclerView);
        btn_follow = view.findViewById(R.id.btn_follow);
        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        bottomNavigationView = view.findViewById(R.id.bottom_NaviBar);
        account_tv_following_count = view.findViewById(R.id.account_tv_following_count);
        account_tv_follower_count = view.findViewById(R.id.account_tv_follower_count);
        account_post_count = view.findViewById(R.id.account_tv_post_count);
        user_page_id = view.findViewById(R.id.user_page_id);
        followRequireBadge = view.findViewById(R.id.follow_require_alarm_badge);
        btnDirectMessage = view.findViewById(R.id.btn_direct_message);
        btnFollowRequireAlarm = view.findViewById(R.id.btn_follow_require_alarm);

        Bundle bundle = this.getArguments();
        uid = bundle.getString("destinationUid"); //프로필 이미지의 유저 uid
        selectUserId = bundle.getString("userId");  //유저의 email

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        //상대 프로필인지 나인지 확인하기
        if (uid != null && uid.equals(currentUserId)) {
            //프로필이 나일 때
            getProfileMe();
        } else if (uid != null) {
            //프로필이 다른 사람일 때
            user_page_id.setText(selectUserId);
            btnDirectMessage.setVisibility(View.VISIBLE);
            btn_follow.setText(R.string.follow);
            btn_follow.setOnClickListener(v -> requestFollow());
        }

        if (bundle == null) {
        }

        btnDirectMessage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MessageRoomActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("email", selectUserId);
            startActivity(intent);
        });

        btnFollowRequireAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FollowRequireAlarmActivity.class);
            startActivity(intent);
        });

        //유저프래그먼트에 프로필 사진 올리기
        account_iv_profile.setOnClickListener(v -> {
            Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
            photoPickIntent.setType("image/*");
            startActivityForResult(photoPickIntent, PICK_PROFILE_FROM_ALBUM);
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userFragmentAdapter);
        getProfileImage();
//        getFollowerAndFollowing();

        return view;
    }

    //todo : 팔로우 요청 왔을 때 배지 값 띄우기
    @SuppressLint("SetTextI18n")
    public void getFollowBadge() {
        FirebaseFirestore.getInstance().collection("follow").document(currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (value == null) {
                        //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                    }

                    FollowDTO followDTO = value.toObject(FollowDTO.class);
                    int badgeCount = followDTO.followersRequire.size();
                    if (badgeCount == 0) {
                        followRequireBadge.setVisibility(View.GONE);
                    } else {
                        followRequireBadge.setVisibility(View.VISIBLE);
                        followRequireBadge.setText(Integer.toString(badgeCount));
                    }
                });
    }

    public void getProfileMe() {
        user_page_id.setText(firebaseAuth.getCurrentUser().getEmail()); //프로필 이름 변경
        btn_follow.setText(R.string.signout); //로그아웃으로 변경
        btn_follow.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            firebaseAuth.signOut();  //종료되는거 수정필요
        });
        btnDirectMessage.setVisibility(View.GONE);
    }


    //데이터베이스에서 이미지 가져오기 (오류가 여기인가)
    public void getProfileImage() {
        firestore.collection("profileImages").document(uid)
                .addSnapshotListener((value, error) -> {
                    if (value.getData() != null) {
                        String url = value.getData().toString();

                        // 여기 오류
                        //Glide.with(getActivity()).load(url).apply(new RequestOptions().circleCrop()).into(account_iv_profile);
                    } else {

                    }
                });
    }

//    //팔로워 값 변경
//    public void getFollowerAndFollowing() {
//        firestore.collection("users").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                followDTO = value.toObject(FollowDTO.class);
//
//                if (followDTO.followingCount != null) {
//                    account_tv_following_count.setText(String.valueOf(followDTO.followingCount));
//                }
//                if (followDTO.followerCount != null) {
//                    account_tv_follower_count.setText(String.valueOf(followDTO.followerCount));
//
//                    //내가 팔로워를 하고 있으면
//                    if (followDTO.followers.containsKey(currentUserId)) {
//                        btn_follow.setText(getString(R.string.follow_cancel));
//                    } else {
//                        btn_follow.setText(getString(R.string.follow));
//                    }
//                }
//            }
//        });
//    }


    //상대방 계정에는 또다른 팔로워
    //내 계정에는 상대방 누구를 팔로워 하는지
    //과정의 트랜잭션
    //먼저 팔로워 리스트에 값이 들어가야할 것 같음 -> 내가 누구를 팔로워/팔로잉 했는지
    public void requestFollow() {
        Bundle bundle = this.getArguments();
        String selectedUid = bundle.getString("destinationUid");
        String selectedEmail = bundle.getString("userId");

        String currentFollowUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().runTransaction(transaction -> {
            DocumentReference doFollowing = FirebaseFirestore.getInstance().collection("follow").document(currentFollowUid); //팔로으 누른 사람(현재 로그인 한 사람)
            FollowDTO followDTO = transaction.get(doFollowing).toObject(FollowDTO.class);
            //get을 put으로
            //documentRef 쓰는게 맞는지
            //디비에 안 넣어지는게 문제임
            //조건 1

            if (followDTO.followings.size() == 0) {
                followDTO.followings.put(selectedEmail, true);
                followDTO.followingCount += 1;
                btn_follow.setText(R.string.follow_cancel);
            } else {
                //조건 2
                if (followDTO.followings.containsKey(selectedUid)) {
                    //이미 팔로워 된 uid일 경우
                    followDTO.followingCount -= 1; //팔로워 취소
                    followDTO.followings.remove(selectedEmail); //uid삭제
                    btn_follow.setText(R.string.follow);
                } else {
                    //팔로워가 되어있지 않은 uid일 경우
                    followDTO.followingCount -= 1; //팔로워
                    followDTO.followings.put(selectedEmail, true); //uid등록
                    followerAlarm(uid);
                    btn_follow.setText(R.string.follow_cancel);
                }
            }
            transaction.set(FirebaseFirestore.getInstance().collection("follow").document(), followDTO); //db에 저장
            return null;
        });


//        내가 팔로잉 한 상대방 계정
        FirebaseFirestore.getInstance().runTransaction(transaction -> {
            DocumentReference doFollower = FirebaseFirestore.getInstance().collection("follow").document(selectedUid);
            FollowDTO followDTO = transaction.get(doFollower).toObject(FollowDTO.class);
            if (followDTO.followers.size() == 0) {
                followDTO.followerCount += 1;
                followDTO.followers.put(currentFollowUid, true);
                followDTO.followersRequire.put(firebaseAuth.getCurrentUser().getEmail(), true);
            } else {
                if (followDTO.followers.containsKey(currentFollowUid)) {
                    followDTO.followerCount -= 1;
                    followDTO.followers.remove(firebaseAuth.getCurrentUser().getEmail());
                    followDTO.followersRequire.remove(firebaseAuth.getCurrentUser().getEmail());
                } else {
                    followDTO.followerCount += 1;
                    followDTO.followers.put(firebaseAuth.getCurrentUser().getEmail(), true);
                    followDTO.followersRequire.put(firebaseAuth.getCurrentUser().getEmail(), true);
//                    followerAlarm(this.currentUserId);
                }
            }
            transaction.set(FirebaseFirestore.getInstance().collection("follow").document(selectedUid), followDTO); //db에 저장
            return null;
        });

    }

    //팔로워 알람
    public void followerAlarm(String destinationUid) {
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

        public UserFragmentAdapter(String uid) {
            contentDTOS = new ArrayList<>();
            this.uid = uid;
            //파이어스토어에서 데이터 값 읽어오기
            //내가 올린 이미지만 뜰 수 있도록.whereEqualTo("uid", uid) -> uid 값이 내 uid값 일때만

            firestore.collection("images").whereEqualTo("uid", uid)
                    .addSnapshotListener((value, error) -> {
                        if (value == null) {
                            //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                        }

                        //데이터를 받아주는 부분
                        for (QueryDocumentSnapshot doc : value) {
                            contentDTOS.add(doc.toObject(ContentDTO.class));
                        }
                        account_post_count.setText(String.valueOf(contentDTOS.size()));
                        notifyDataSetChanged(); //새로고침
                    });

//            firestore.collection("follow").document(currentUserId).addSnapshotListener(((value, error) -> {
//                if (value == null) {
//                    //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
//                }
//
//                FollowDTO followDTO = value.toObject(FollowDTO.class);
//                int badgeCount = followDTO.followersRequire.size();
//                if (badgeCount == 0) {
//                    followRequireBadge.setVisibility(View.GONE);
//                } else {
//                    followRequireBadge.setVisibility(View.VISIBLE);
//                    followRequireBadge.setText(Integer.toString(badgeCount));
//                }
//            }));
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
