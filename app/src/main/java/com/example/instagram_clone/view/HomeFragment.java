package com.example.instagram_clone.view;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ContentDTO> contentDTOS = new ArrayList<>();

    //프래그먼트 간의 데이터 전송을 위해
    Fragment fragment_user = new UserFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);
        FirebaseApp.initializeApp(getActivity()); //???

        firestore = FirebaseFirestore.getInstance(); //파이어스토어 인스턴스 초기화

        //RecyclerView 어댑터 설정 및 레이아웃 계획
        recyclerView = view.findViewById(R.id.detail_recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(contentDTOS);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    //게시물을 띄울 RecyclerView의 Adapter
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //content의 model
        ArrayList<ContentDTO> contentDTOS;
        //content를 작성한 uid를 저장할 리스트
        //ArrayList이기 때문에 순서, 중복 상관없음
        ArrayList<String> contentUidList = new ArrayList<>();

        //adapter 초기 실행문
        public RecyclerViewAdapter(ArrayList<ContentDTO> contentDTOS) {
            this.contentDTOS = new ArrayList<>();

            //Query.Direction.DESCDING을 통해 내림차순으로 데이터를 value에 저장
            firestore.collection("images").orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        //리스트를 비움
                        RecyclerViewAdapter.this.contentDTOS.clear();
                        contentUidList.clear();
                        if (value == null) {
                            //쿼리값이 없을 때 바로 종료시키는 것 (오류 방지)
                        }

                        //doc의 object를 contentDTOS에 추가
                        for (QueryDocumentSnapshot doc : value) {
                            RecyclerViewAdapter.this.contentDTOS.add(doc.toObject(ContentDTO.class));
                            contentUidList.add(doc.getId());
                        }

                        //Adapter에게 RecyclerView의 리스트 데이터가 바뀌었으니 모든 항목을 업데이트하라는 신호가 전달됨
                        notifyDataSetChanged(); //새로고침
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
            //((CustomViewHolder)holder)한 이유는 holder가 본인이 CustomViewHolder인지 모르기 때문에 캐스팅해줌
            //텍스트뷰에 게시글 작성자 email 담기
            ((CustomViewHolder) holder).detail_user_name.setText(contentDTOS.get(position).userId);
            //이미지뷰에 게시글 이미지 담기
            Glide.with(holder.itemView.getContext()).load(contentDTOS.get(position).imageUrl).into(((CustomViewHolder) holder).detail_content_img);
            //텍스트뷰에 좋아요 개수 담기
            ((CustomViewHolder) holder).detail_favorit_count.setText(contentDTOS.get(position).favoriteCount + "명이 좋아합니다");
            //텍스트뷰에 게시글 내용 담기
            ((CustomViewHolder) holder).detail_content_txt.setText(contentDTOS.get(position).explain);

            //좋아요 버튼 클릭 이벤트
            ((CustomViewHolder) holder).btn_favorite.setOnClickListener(v -> favoritEvent(position));

            //좋아요 하트 채워지기 이벤트
            //conentDTOS의 favorites 리스트에 저장된 containKey가 false 나오면 채워진 하트 이미지로 변경 (리스트에 uid가 없기 때문에 좋아요를 안 누른 것으로 판단)
            if (contentDTOS.get(position).favorities.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite);
            } else {
                //conentDTOS의 favorites 리스트에 저장된 containKey가 true가 나오면 빈 하트 이미지로 변경 (리스트에 uid가 있기 때문에 좋아요를 누른 것으로 판단)
                ((CustomViewHolder) holder).btn_favorite.setImageResource(R.drawable.ic_favorite_border);
            }

            //메인 피드에서 프로필 사진 클릭했을 때 이벤트
            ((CustomViewHolder) holder).detail_profile_img.setOnClickListener(v -> {
                //프래그먼트 간의 데이터 이동을 위해 Bundle객체 선언 및 초기화
                Bundle bundle = new Bundle();
                //bundle에 각각의 키와 데이터를 저장함
                bundle.putString("destinationUid", contentDTOS.get(position).uid);
                bundle.putString("userId", contentDTOS.get(position).userId);
                //데이터를 전송할 프래그먼트에 bundle을 setArguments함
                fragment_user.setArguments(bundle);

                //getActivity()가 null이 아닐 때 UserFragment로 전환
                if (getActivity() != null) {
                    getActivity()
                            .getSupportFragmentManager() //프래그먼트의 추가, 삭제, 교체를 관리
                            .beginTransaction()
                            .replace(R.id.main_content, fragment_user)
                            .addToBackStack(null)
                            .commit();
                }
            });

            //댓글 아이콘 누르면 실행되는 이벤트
            ((CustomViewHolder) holder).btn_comment.setOnClickListener(v -> {
                //CommentActivity로 intent함
                Intent intent = new Intent(getView().getContext(), CommentActivity.class);
                //intent할 때 contentUidList와 contentDTOS의 uid값과 함께 이동함
                //아이콘을 선택한 사용자의 uid와 댓글이 달리는 게시물의 작성자의 uid를 확인하기 위해서 함께 이동함
                intent.putExtra("image", contentDTOS.get(position).imageUrl);
                intent.putExtra("contentUid", contentUidList.get(position));
                intent.putExtra("destinationUid", contentDTOS.get(position).uid);
                startActivity(intent); //이동
            });
        }

        //contentDTOS의 size
        @Override
        public int getItemCount() {
            return contentDTOS.size();
        }

        //뷰홀더 : 각 View를 보관하는 객체로, 각 구성 요소를 저장하여 반복적으로 조회하지 않고도 즉시 액세스할 수 있도록 하는 역할
        //메모리낭비가 적으며, 액세스 속도가 향상함
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView detail_profile_img;
            ImageView detail_content_img;
            ImageView btn_favorite;
            ImageView btn_comment;
            ImageView detail_more;
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
                detail_more = (ImageView) view.findViewById(R.id.detail_more);
                btn_favorite = (ImageView) view.findViewById(R.id.btn_favorite);
                btn_comment = (ImageView) view.findViewById(R.id.btn_comment);
            }
        }

        //좋아요 누르기 이벤트
        public void favoritEvent(int position) {

            firestore.runTransaction(transaction -> {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ContentDTO contentDTO = transaction.get(firestore.collection("images")
                        .document(contentUidList.get(position))).toObject(ContentDTO.class);

                //맵에서 인자로 보낸 키 -> containsKey
                if (contentDTO.favorities.containsKey(uid)) {
                    //좋아요가 눌렸을 때 - 좋아요를 취소하는 이벤트
                    //눌린 상태여서 취소해야하기 때문에 좋아요 개수 -1과 좋아요 누른 유저의 정보를 삭제해야 함
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1;
                    contentDTO.favorities.remove(uid);
                } else {
                    //좋아요가 눌려있지 않을 때 - 좋아요를 누르는 이벤트
                    //좋아요가 눌리지 않은 상태라서 좋아요를 누르면 개수 +1과 좋아요 누른 유저의 정보가 등록되어야 함
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1;
                    //누른 사람의 uid를 contentDTO.favorities에 put 해야 구분할 수 있음
                    contentDTO.favorities.put(uid, true);
                    RecyclerViewAdapter.this.favoriteAlarm(contentDTOS.get(position).uid); //카운터가 올라가는 사람이름 알림
                }

                //트랜잭션을 다시 서버로 돌려준다.
                return transaction.set(firestore.collection("images")
                        .document(contentUidList.get(position)), contentDTO);
            });
        }

        //좋아요 알람
        public void favoriteAlarm(String destinationUid) {
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
