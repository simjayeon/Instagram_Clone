package com.example.instagram_clone.ui.fragment;

import android.content.Intent;
import android.media.MediaDrm;
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
import com.example.instagram_clone.ui.activity.LoginActivity;
import com.example.instagram_clone.ui.activity.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class UserFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    UserFragmentAdapter userFragmentAdapter;
    RecyclerView recyclerView;
    String uid, currentUserId, selectUid;
    ImageView btn_back, toolbar_logo, account_iv_profile;
    Button btn_follow;
    TextView toolbar_user_id;
    BottomNavigationView bottomNavigationView;
    int PICK_PROFILE_FROM_ALBUM = 55;


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
        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        toolbar_user_id = view.findViewById(R.id.toolbar_user_id);
        bottomNavigationView = view.findViewById(R.id.bottom_NaviBar);
        toolbar_logo = view.findViewById(R.id.toolbar_logo);


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Bundle bundle = this.getArguments();
        if(bundle != null){
            uid = bundle.getString("destinationUid");
            selectUid = bundle.getString("userId");
        }else{
            System.out.println("값이 안왔슈");
        }

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        System.out.println("currentUserId이요"+currentUserId);

        //프로필이 나일 때
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
            //프로필이 다른 사람일 때
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

        return view;
    }


    //데이터베이스에서 이미지 가져오기
    public void getProfileImage(){
        firestore.collection("profileImages").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getData() != null){
                    String url = value.getData().toString();
                    Glide.with(getActivity()).load(url).centerCrop().into(account_iv_profile);
                }
            }
        });
    }



    //이따가 설정
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
                        contentDTOS.add(doc.toObject(ContentDTO.class));
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

}
