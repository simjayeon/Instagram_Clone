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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.google.firebase.auth.FirebaseAuth;
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
    ArrayList<AlarmDTO> alarmDTO = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm,container,false);
        alarmfragment_recyclerview = view.findViewById(R.id.alarmfragment_recyclerview);
        alarmfragment_recyclerview.setAdapter(new AlarmRecyclerviewAdapter());
        alarmfragment_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    class AlarmRecyclerviewAdapter extends RecyclerView.Adapter{
        ArrayList<AlarmDTO> alarmDTOList = new ArrayList<>();

        public AlarmRecyclerviewAdapter() {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationId", uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    QuerySnapshot querySnapshot = null;
                    alarmDTOList.clear();

                    if(querySnapshot == null){
                    }

                    for(QueryDocumentSnapshot doc : value){
                        alarmDTOList.add(doc.toObject(AlarmDTO.class));
                    }

                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomViewHolder(view);
        }
        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView commentviewitem_imageview_profile;
            TextView commentviewitem_textview_profile;
            TextView commentviewitem_textview_comment;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                commentviewitem_imageview_profile = itemView.findViewById(R.id.commentviewitem_imageview_profile);
                commentviewitem_textview_profile = itemView.findViewById(R.id.commentviewitem_textview_profile);
                commentviewitem_textview_comment = itemView.findViewById(R.id.commentviewitem_textview_comment);

            }
        }

        @Override
        public int getItemCount() {
            return alarmDTOList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ImageView profileImage = ((CustomViewHolder) holder).commentviewitem_imageview_profile;

            FirebaseFirestore.getInstance().collection("profileImages")
                    .document(alarmDTOList.get(position).uid)
                    .get().addOnCompleteListener(task -> {
                String url = task.getResult().toString();
                Glide.with(getActivity()).load(url).centerCrop().into(profileImage);
            });

            switch (alarmDTOList.get(position).kind){
                case 0:
                    String str_0 = alarmDTOList.get(position).userId + getString(R.string.alarm_favorite);
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_0);
                case 1:
                    String str_1 = alarmDTOList.get(position).userId + " " + getString(R.string.alarm_comment) + " of " + alarmDTOList.get(position).message;
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_1);
                case 2:
                    String str_2 = alarmDTOList.get(position).userId + " " + getString(R.string.alarm_follow);
                    ((CustomViewHolder) holder).commentviewitem_textview_comment.setText(str_2);
            }
        }
    }
}
