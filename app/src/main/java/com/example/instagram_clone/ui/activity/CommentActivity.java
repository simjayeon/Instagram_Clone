package com.example.instagram_clone.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagram_clone.R;
import com.example.instagram_clone.model.AlarmDTO;
import com.example.instagram_clone.model.ContentDTO;
import com.example.instagram_clone.ui.fragment.DetailViewFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    Button btn_send;
    ContentDTO.Comment comments = new ContentDTO.Comment();
    EditText comment_message;
    String contentUid, destinationUid;
    RecyclerView commentRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        contentUid = getIntent().getStringExtra("contentUid");
        destinationUid = getIntent().getStringExtra("destinationUid");

        comment_message = findViewById(R.id.comment_message_edit);
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        btn_send = findViewById(R.id.btn_send);


        //댓글 올리기
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                comments.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                comments.comment = comment_message.getText().toString();
                comments.timestamp = System.currentTimeMillis();

                FirebaseFirestore.getInstance().collection("images").document(contentUid).collection("comments").document().set(comments);

                commentAlarm(destinationUid, comment_message.getText().toString());
                comment_message.setText(""); //데이터 보낸 후 초기화
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter();
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.setAdapter(commentRecyclerViewAdapter);

    }

    //댓글 알림 기능
    public void commentAlarm(String destinationUid, String message){
        AlarmDTO alarmDTO  = new AlarmDTO();
        alarmDTO.destinationUid = destinationUid;
        alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        alarmDTO.kind = 1;
        alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alarmDTO.timestamp = System.currentTimeMillis();
        alarmDTO.message = message;
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);
    }





    public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        ArrayList<ContentDTO.Comment> commentList = new ArrayList<>();

        public CommentRecyclerViewAdapter(){

            FirebaseFirestore.getInstance().collection("images")
                    .document(contentUid)
                    .collection("comments")
                    .orderBy("timestamp")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            commentList.clear();

                            for (QueryDocumentSnapshot doc : value) {
                                commentList.add(doc.toObject(ContentDTO.Comment.class));
                            }
                            notifyDataSetChanged(); //새로고침
                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomerViewHolder) holder).comment_comment_txt.setText(commentList.get(position).comment);
            ((CustomerViewHolder) holder).comment_profile_id.setText(commentList.get(position).userId);

            RecyclerView.ViewHolder finalHolder = holder;
            FirebaseFirestore.getInstance().collection("profileImages")
                    .document(commentList.get(position).uid).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            String url = task.getResult().toString();
                            System.out.println(url+"테스크");
                            Glide.with(finalHolder.itemView.getContext()).load(url).into(((CustomerViewHolder) finalHolder).comment_profile_img);
                        }else{
                            System.out.println("노성공");
                        }
                    });

        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        private class CustomerViewHolder extends RecyclerView.ViewHolder {
            ImageView comment_profile_img;
            TextView comment_profile_id;
            TextView comment_comment_txt;

            public CustomerViewHolder(View view) {
                super(view);
                comment_profile_img = view.findViewById(R.id.commentItem_profileImg);
                comment_profile_id = view.findViewById(R.id.commentItem_userId);
                comment_comment_txt = view.findViewById(R.id.commentItem_text);

            }
        }
    }
}