package edu.northeastern.numad22fa_team23.model;

import java.util.List;

public class ProjectMoment {

    private String groupId;
    private String userName;
    private int userId;
    private int momentId;
    private String musicName;
    private String thought;
    private List<ProjectComment> projectCommentList;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    private String weather;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMomentId() {
        return momentId;
    }

    public void setMomentId(int momentId) {
        this.momentId = momentId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public List<ProjectComment> getCommentList() {
        return projectCommentList;
    }

    public void setCommentList(List<ProjectComment> projectCommentList) {
        this.projectCommentList = projectCommentList;
    }
}
