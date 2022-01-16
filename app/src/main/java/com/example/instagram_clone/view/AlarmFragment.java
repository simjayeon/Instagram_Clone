package com.example.instagram_clone.view;

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

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {
    //대량의 데이터 Set을 효율적으로 표시할 수 있음
    //항목이 스크롤되어 화면에서 벗어나도 뷰를 제거하지 않고 재사용함
    //뷰의 재사용으로 인해 응답성 개선 및 전력 소모를 줄여 성능을 향상함
    RecyclerView alarmfragment_recyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm, container, false);

        alarmfragment_recyclerview = view.findViewById(R.id.alarmfragment_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        AlarmRecyclerviewAdapter alarmRecyclerviewAdapter = new AlarmRecyclerviewAdapter();
        alarmfragment_recyclerview.setAdapter(alarmRecyclerviewAdapter);
        alarmfragment_recyclerview.setLayoutManager(layoutManager);

        return view;
    }

    class AlarmRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<AlarmDTO> alarmDTOArrayList = new ArrayList<>(); // 인스턴스화, 초기화

        public AlarmRecyclerviewAdapter() {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //whereEqualTo("destinationUid", uid) -> collection 속 alarms의 uid와 현재 접속한 사용자의 uid가 같을 때의 값을 가져옴
            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
                    .addSnapshotListener((value, error) -> {

                        value.getDocuments();
                        alarmDTOArrayList.clear();

                        if (value == null) {
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            AlarmRecyclerviewAdapter.this.alarmDTOArrayList.add(doc.toObject(AlarmDTO.class));
                        }
                        notifyDataSetChanged();
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return alarmDTOArrayList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).comment_profile_id.setVisibility(View.GONE);

            switch (alarmDTOArrayList.get(position).kind) {
                case 0:
                    ((CustomViewHolder) holder).comment_profile_img.setImageResource(R.drawable.noun_like_1638902);
                    String str_0 = alarmDTOArrayList.get(position).userId + " " + getString(R.string.alarm_favorite);
                    ((CustomViewHolder) holder).comment_comment_txt.setText(str_0);
                    break;
                case 1:
                    ((CustomViewHolder) holder).comment_profile_img.setImageResource(R.drawable.noun_commend);
                    String str_1 = alarmDTOArrayList.get(position).userId + " " + getString(R.string.alarm_comment) + "\n\"" + alarmDTOArrayList.get(position).message + "\"";
                    ((CustomViewHolder) holder).comment_comment_txt.setText(str_1);
                    break;
                case 2:
                    ((CustomViewHolder) holder).comment_profile_img.setImageResource(R.drawable.user);
                    String str_2 = alarmDTOArrayList.get(position).userId + " " + getString(R.string.alarm_follow);
                    ((CustomViewHolder) holder).comment_comment_txt.setText(str_2);
                    break;
            }
        }


        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView comment_profile_img;
            TextView comment_profile_id;
            TextView comment_comment_txt;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                comment_profile_img = itemView.findViewById(R.id.commentItem_profileImg);
                comment_profile_id = itemView.findViewById(R.id.commentItem_userId);
                comment_comment_txt = itemView.findViewById(R.id.commentItem_text);
            }
        }
    }
}
