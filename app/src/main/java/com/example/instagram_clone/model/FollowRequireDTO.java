package com.example.instagram_clone.model;

public class FollowRequireDTO {
    //팔로우 요청
    public String uid;
    public String userId;
    public long timestamp;

    public void setFollowRequire(String uid, String userId, long timestamp){
        this.uid = uid;
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
