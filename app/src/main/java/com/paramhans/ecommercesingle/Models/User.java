package com.paramhans.ecommercesingle.Models;

public class User
{
    private String id,name,email,profile_picture,mobile,user_type,device_id,fcm_token;

    public User(){}

    public User(String id, String name, String email, String profile_picture, String mobile, String user_type, String device_id, String fcm_token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile_picture = profile_picture;
        this.mobile = mobile;
        this.user_type = user_type;
        this.device_id = device_id;
        this.fcm_token = fcm_token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
