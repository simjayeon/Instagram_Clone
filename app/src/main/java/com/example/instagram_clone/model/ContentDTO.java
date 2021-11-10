package com.example.instagram_clone.model;

import java.util.HashMap;
import java.util.Map;

//게시글
public class ContentDTO{

    //public을 사용해주지 않으면 다른 액티비티가 참조를 못함

    public String explain;
    public String imageUrl;
    public String uid;
    public String userId;
    public long timestamp;
    public int favoriteCount;
    public Map<String, Boolean> favorities = new HashMap<>();
    //public Map<String, Comment> comments;


    //댓글
    public static class Comment{
        public String uid;
        public String userId;
        public String comment;
        public long timestamp;
    }
}
