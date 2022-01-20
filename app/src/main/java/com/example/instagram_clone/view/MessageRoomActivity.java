package com.example.instagram_clone.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityMessageRoomBinding;
import com.example.instagram_clone.model.MessageDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MessageRoomActivity extends BaseActivity<ActivityMessageRoomBinding> implements View.OnClickListener {
    private RecyclerViewAdapter recyclerViewAdapter;
    private String uid, email, currentUid, currentEmail;

    public MessageRoomActivity() {
        super(R.layout.activity_message_room);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinder.btnBack.setOnClickListener(this);
        mBinder.activityMessageRoomBtnSend.setOnClickListener(this);

        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");
        email = intent.getExtras().getString("email");
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mBinder.activityMessageRoomTitle.setText(email);

        recyclerViewAdapter = new RecyclerViewAdapter();
        mBinder.activityMessageRoomList.setLayoutManager(new LinearLayoutManager(this));
        mBinder.activityMessageRoomList.setAdapter(recyclerViewAdapter);
    }

    //보내는 메시지
    public void postMessage() {
        String etPostMessage = mBinder.activityMessageRoomEt.getText().toString();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setEmail(currentEmail);
        messageDTO.setMessage(etPostMessage);
        messageDTO.setTimestamp(System.currentTimeMillis());
        messageDTO.setPostEmail(currentEmail);
        messageDTO.setRecvEmail(email);

        //내 채팅 데이터
        FirebaseFirestore.getInstance().collection("DM").document(currentUid).collection(email).document().set(messageDTO);
        //상대 채팅 데이터
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        ArrayList<MessageDTO> messageDTOS = new ArrayList<>();

        public RecyclerViewAdapter() {
            FirebaseFirestore.getInstance().collection("DM").document(currentUid).collection(email).orderBy("timestamp")
                    .addSnapshotListener((value, error) -> {
                        messageDTOS.clear();
                        if (value == null) {
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            messageDTOS.add(doc.toObject(MessageDTO.class));
                        }
                        notifyDataSetChanged();
                    });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_room_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.postEmail.setText(messageDTOS.get(position).email);
            holder.postMessage.setText(messageDTOS.get(position).message);
        }

        @Override
        public int getItemCount() {
            return messageDTOS.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView postEmail, postMessage;
            TextView recvEmail, recvMessage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                postEmail = itemView.findViewById(R.id.item_message_room_post_email);
                postMessage = itemView.findViewById(R.id.item_message_room_post_message);
                recvEmail = itemView.findViewById(R.id.item_message_room_profile_email);
                recvMessage = itemView.findViewById(R.id.item_message_room_message);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_back) {
            onBackPressed();
        } else if (viewId == R.id.activity_message_room_btn_send) {
            postMessage();
        }
    }
}