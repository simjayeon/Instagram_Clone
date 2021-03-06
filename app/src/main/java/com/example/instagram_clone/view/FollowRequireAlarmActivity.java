package com.example.instagram_clone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityFollowRequireAlarmBinding;
import com.example.instagram_clone.model.FollowDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowRequireAlarmActivity extends BaseActivity<ActivityFollowRequireAlarmBinding> implements View.OnClickListener {
    RecyclerViewAdapter recyclerViewAdapter;

    public FollowRequireAlarmActivity() {
        super(R.layout.activity_follow_require_alarm);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinder.btnBack.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinder.followRequireAlarmList.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        mBinder.followRequireAlarmList.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_back) {
            onBackPressed();
        }
    }
}


class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    ArrayList<FollowDTO> arrayList = new ArrayList();

    public RecyclerViewAdapter() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("follow").document(uid)
                .addSnapshotListener((value, error) -> {
                    arrayList.clear();
                    FollowDTO followDTO = value.toObject(FollowDTO.class);

                    arrayList.add(followDTO);
                    notifyDataSetChanged();
                });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_require_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = arrayList.get(position).followersRequire.toString();
        String str2 = str.substring(1, str.lastIndexOf("="));
        holder.email.setText(str2);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            email = itemView.findViewById(R.id.item_follow_require_alarm_email);
        }
    }
}