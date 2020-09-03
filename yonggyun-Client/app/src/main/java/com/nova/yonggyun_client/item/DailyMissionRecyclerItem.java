package com.nova.yonggyun_client.item;

import java.io.Serializable;

public class DailyMissionRecyclerItem implements Serializable {
    private String Img;
    private String description;
    private String title;
    private int completCount;
    private int participantCount;
    //public DailyMissionRecyclerItem(String img ,String description, String title, String completCount, String participantCount) {
    public DailyMissionRecyclerItem(String img ,String description, String title) {
        this.Img = img;
        this.description = description;
        this.title = title;
//        this.completCount = Integer.parseInt(String.valueOf(completCount));
//        this.participantCount = Integer.parseInt(String.valueOf(participantCount));
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCompletCount() {
        return completCount;
    }

    public void setCompletCount(int completCount) {
        this.completCount = completCount;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
