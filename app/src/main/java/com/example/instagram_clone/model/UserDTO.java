package com.example.instagram_clone.model;

public class UserDTO {

    public UserDTO(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void signUp(String email, String password){
        this.email = email;
        this.password = password;
    }

    private String email;
    private String password;
    private String fcmToken;


}
