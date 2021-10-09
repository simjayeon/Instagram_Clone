package com.example.instagram_clone.model;

import java.util.HashMap;
import java.util.Map;

public class AlarmDTO {
    //public을 사용해주지 않으면 다른 액티비티가 참조를 못함 -> 맞는지 확인필요
    public String destinationUid;
    public String userId;
    public String uid;

    //0: 좋아요 알람
    //1: 댓글 알람
    //2: 팔로우 알람
    public int kind;
    public String message;
    public Long timestamp;
}
