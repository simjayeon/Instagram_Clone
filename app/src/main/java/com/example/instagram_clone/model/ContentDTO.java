package com.example.instagram_clone.model;

import java.util.HashMap;
import java.util.Map;

public class ContentDTO{

    //public을 사용해주지 않으면 다른 액티비티가 참조를 못함 -> 맞는지 확인필요

    public String explain;
    public String imageUrl;
    public String uid;
    public String userId;
    public long timestamp;
    public  int favoriteCount;
    public Map<String, Boolean> favorities = new HashMap<>();

    public class Comment{
        String uid;
        String userId;
        String comment;
        long timestamp;
    }
}
