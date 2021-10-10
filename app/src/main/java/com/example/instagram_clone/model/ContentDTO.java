package com.example.instagram_clone.model;

import java.util.HashMap;
import java.util.Map;

//게시글
public class ContentDTO{

    //public을 사용해주지 않으면 다른 액티비티가 참조를 못함 -> 맞는지 확인필요

    public String explain;
    public String imageUrl;
    public String uid;
    public String userId;
    public long timestamp;
    public int favoriteCount;
    public Map<String, Boolean> favorities = new HashMap<>();


    //댓글
    public class Comment{
        String uid;  // 파이어베이스 uid
        String userId; // 아이디(이메일)
        String comment; // 댓글
        long timestamp; // 타임스탬프
    }
}
