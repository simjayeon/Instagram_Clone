package com.example.instagram_clone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.internal.DiskLruCache;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {
    RecyclerView alarmfragment_recyclerview;
    String uid;
    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm,container,false);

        alarmfragment_recyclerview = view.findViewById(R.id.alarmfragment_recyclerview);
        alarmfragment_recyclerview.setAdapter(new AlarmRecyclerviewAdapter());
        alarmfragment_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    class AlarmRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        ArrayList<AlarmDTO> alarmDTOArrayList = new ArrayList<>(); // 인스턴스화, 초기화

        public AlarmRecyclerviewAdapter() {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationId", uid)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            /*
                            QuerySnapshot querySnapshot = null;
                            final DocumentReference docRef = firestore.collection("alarms").document();
                            querySnapshot.getDocuments();
                            alarmDTOArrayList.clear();

                            if(querySnapshot == null){
                            }

                    for(alarmDTOArrayList : querySnapshot)
                    {
                        alarmDTOArrayList.add(docRef.toObject(AlarmDTO.class));
                        System.out.println("오느냐..4");
                    }*/
                            notifyDataSetChanged();
                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            System.out.println("오느냐..5");
            return new CustomViewHolder(view);
        }

        @Override
        public int getItemCount() {
            System.out.println("오느냐..6!!"+alarmDTOArrayList.size());
            return alarmDTOArrayList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ImageView profileImage = ((CustomViewHolder) holder).commentviewitem_imageview_profile;
            System.out.println("오느냐..7");
            FirebaseFirestore.getInstance().collection("profileImages")
                    .document(alarmDTOArrayList.get(position).uid).get().addOnCompleteListener(task -> {
                String url = task.getResult().toString();
                //centerCrop : 비율을 유지하며 가운데를 중심으로 자른다 (이미지 스케일을 조절)
                Glide.with(getActivity()).load(url).centerCrop().into(profileImage);
            });
            System.out.println("오느냐..8");
            switch (alarmDTOArrayList.get(position).kind){
                case 0:
                    String str_0 = alarmDTOArrayList.get(position).userId + getString(R.string.alarm_favorite);
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_0);
                case 1:
                    String str_1 = alarmDTOArrayList.get(position).userId + " " + getString(R.string.alarm_comment) + " of " + alarmDTOArrayList.get(position).message;
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_1);
                case 2:
                    String str_2 = alarmDTOArrayList.get(position).userId + " " + getString(R.string.alarm_follow);
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_2);
            }
        }


        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView commentviewitem_imageview_profile;
            TextView commentviewitem_textview_profile;
            TextView commentviewitem_textview_comment;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                System.out.println("오느냐..9");
                commentviewitem_imageview_profile = itemView.findViewById(R.id.commentviewitem_imageview_profile);
                commentviewitem_textview_profile = itemView.findViewById(R.id.commentviewitem_textview_profile);
                commentviewitem_textview_comment = itemView.findViewById(R.id.commentviewitem_textview_comment);
            }
        }
    }
}
