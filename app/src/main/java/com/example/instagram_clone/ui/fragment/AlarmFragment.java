package com.example.instagram_clone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.internal.DiskLruCache;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class AlarmFragment extends Fragment {
    ArrayList<AlarmDTO> alarmDTO = new ArrayList<>();
    TextView commentviewitem_textview_comment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm,container,false);
        //alarmfragment_recyclerview.setAdapter(new AlarmRecyclerviewAdapter());
        //alarmfragment_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

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

                    /*for(querySnapshot in querySnapshot.getDocuments()){
                        alarmDTOList.add(querySnapshot.toObjects(AlarmDTO.class));
                    }*/

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
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

         @Override
         public int getItemCount() {
             return alarmDTOList.size();
         }

         @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;

            FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList.get(position).uid).get().addOnCompleteListener(task -> )

            switch (alarmDTOList.get(position).kind){
                case 0:
                    String str_0 = alarmDTOList.get(position).userId + getString(R.string.alarm_favorite);
                    commentviewitem_textview_comment.setText(str_0);
                case 1:
                    String str_1 = alarmDTOList.get(position).userId + " " + getString(R.string.alarm_comment) + " of " + alarmDTOList.get(position).message;
                    commentviewitem_textview_comment.setText(str_1);
                case 2:
                    String str_2 = alarmDTOList.get(position).userId + " " + getString(R.string.alarm_follow);
                    commentviewitem_textview_comment.setText(str_2);
            }
            commentviewitem_textview_comment.setVisibility(VISIBLE);
        }
    }
}
