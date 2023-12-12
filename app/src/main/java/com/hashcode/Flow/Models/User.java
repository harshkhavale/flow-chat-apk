package com.hashcode.Flow.Models;

public class User {
    String uId;
    String name;
    String profilePic;
    String phoneNumber,token;


    public User(String uId, String name, String profilePic, String phoneNumber) {
        this.uId = uId;
        this.name = name;
        this.profilePic = profilePic;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
