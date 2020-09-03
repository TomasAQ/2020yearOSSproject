package com.nova.yonggyun_client.item;

public class PetsRecyclerItem {
    private String idx;
    private String description;
    private String imageurl;
    private boolean Choice = false;


    public PetsRecyclerItem(String idx, String imageurl, String description) {
        this.idx = idx;
        this.imageurl = imageurl;
        this.description = description;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChoice() {
        return Choice;
    }

    public void setChoice(boolean choice) {
        Choice = choice;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
