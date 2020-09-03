package com.nova.yonggyun_client.item;

public class DailyMissionDetailRecyclerItem {
    String date;
    String userName;

    public DailyMissionDetailRecyclerItem(String date, String userName) {
        this.date = date;
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
