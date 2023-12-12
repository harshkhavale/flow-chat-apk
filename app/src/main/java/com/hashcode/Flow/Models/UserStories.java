package com.hashcode.Flow.Models;

import java.util.ArrayList;

public class UserStories {
    private String name,profileImg;
    private long lastUpdated;
    private ArrayList<Story> stories;

    public UserStories() {
    }

    public UserStories(String name, String profileImg, long lastUpdated, ArrayList<Story> stories) {
        this.name = name;
        this.profileImg = profileImg;
        this.lastUpdated = lastUpdated;
        this.stories = stories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }
}
