package com.example.instagram_clone.model;

import java.util.HashMap;
import java.util.Map;

public class FollowDTO {
    //팔로워 수
    public int followerCount;
    //팔로워 리스트 (중복 팔로워를 방지하기 위해)
    public Map<String, Boolean> followers = new HashMap<>();
    //팔로우 요청 리스트
    public Map<String, Boolean> followersRequire = new HashMap<>();


    //팔로잉 수
    public int followingCount;
    //팔로잉 리스트 (중복 팔로잉을 방지하기 위해)
    public Map<String, Boolean> followings = new HashMap<>();


}
