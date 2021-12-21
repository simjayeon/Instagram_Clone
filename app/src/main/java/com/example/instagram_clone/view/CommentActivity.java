package com.example.instagram_clone.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.databinding.ActivityCommentBinding;
import com.example.instagram_clone.model.AlarmDTO;
import com.example.instagram_clone.model.ContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CommentActivity extends BaseActivity<ActivityCommentBinding> implements View.OnClickListener {
    ContentDTO.Comment comments = new ContentDTO.Comment();
    String contentUid, destinationUid, image;
    String message;

    public CommentActivity() {
        super(R.layout.activity_comment);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinder.iconBackBtn.setOnClickListener(this);

        image = getIntent().getStringExtra("image");
        contentUid = getIntent().getStringExtra("contentUid");
        destinationUid = getIntent().getStringExtra("destinationUid");

        Glide.with(this).load(image).into(mBinder.commentImageView);

        mBinder.commentMessageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                message = mBinder.commentMessageEdit.getText().toString();
                if (message.length() == 0) {
                    mBinder.btnSend.setEnabled(false);
                    mBinder.btnSend.setBackgroundColor(Color.GRAY);
                } else {
                    mBinder.btnSend.setEnabled(true);
                    mBinder.btnSend.setBackgroundColor(Color.BLUE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //댓글 올리기
        mBinder.btnSend.setOnClickListener(v -> {
            comments.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            comments.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            comments.comment = mBinder.commentMessageEdit.getText().toString();
            comments.timestamp = System.currentTimeMillis();

            FirebaseFirestore.getInstance().collection("images").document(contentUid).collection("comments").document().set(comments);

            commentAlarm(destinationUid, mBinder.commentMessageEdit.getText().toString());
            mBinder.commentMessageEdit.setText(""); //데이터 보낸 후 초기화
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter();
        mBinder.commentRecyclerView.setLayoutManager(layoutManager);
        mBinder.commentRecyclerView.setAdapter(commentRecyclerViewAdapter);

    }

    //댓글 알림 기능
    public void commentAlarm(String destinationUid, String message) {
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.destinationUid = destinationUid;
        alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        alarmDTO.kind = 1;
        alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alarmDTO.timestamp = System.currentTimeMillis();
        alarmDTO.message = message;
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.icon_back_btn) {
            onBackPressed();
        }
    }

    public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<ContentDTO.Comment> commentList = new ArrayList<>();

        public CommentRecyclerViewAdapter() {

            FirebaseFirestore.getInstance().collection("images")
                    .document(contentUid)
                    .collection("comments")
                    .orderBy("timestamp")
                    .addSnapshotListener((value, error) -> {
                        commentList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            commentList.add(doc.toObject(ContentDTO.Comment.class));
                        }
                        notifyDataSetChanged(); //새로고침
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).comment_profile_img.setImageResource(R.drawable.user);
            ((CustomViewHolder) holder).comment_comment_txt.setText(commentList.get(position).comment);
            ((CustomViewHolder) holder).comment_profile_id.setText(commentList.get(position).userId);

        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView comment_profile_img;
            TextView comment_profile_id;
            TextView comment_comment_txt;

            public CustomViewHolder(View view) {
                super(view);
                comment_profile_img = view.findViewById(R.id.commentItem_profileImg);
                comment_profile_id = view.findViewById(R.id.commentItem_userId);
                comment_comment_txt = view.findViewById(R.id.commentItem_text);
            }
        }
    }
}