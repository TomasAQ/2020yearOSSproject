package com.nova.yonggyun_client.item;

public class CounselingRecyclerItem {
    private String idx;
    private String petName;
    private String petImg;
    private String petIdx;
    private String counselingClassification;
    private String counselingContent;
    private String counselingImg;
    private String date;



    public CounselingRecyclerItem(String idx, String petName, String petImg, String petIdx, String counselingClassification, String counselingContent, String counselingImg) {
        this.idx = idx;
        this.petName = petName;
        this.petImg = petImg;
        this.petIdx = petIdx;
        this.counselingClassification = counselingClassification;
        this.counselingContent = counselingContent;
        this.counselingImg = counselingImg;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetImg() {
        return petImg;
    }

    public void setPetImg(String petImg) {
        this.petImg = petImg;
    }

    public String getPetIdx() {
        return petIdx;
    }

    public void setPetIdx(String petIdx) {
        this.petIdx = petIdx;
    }

    public String getCounselingClassification() {
        return counselingClassification;
    }

    public void setCounselingClassification(String counselingClassification) {
        this.counselingClassification = counselingClassification;
    }

    public String getCounselingContent() {
        return counselingContent;
    }

    public void setCounselingContent(String counselingContent) {
        this.counselingContent = counselingContent;
    }

    public String getCounselingImg() {
        return counselingImg;
    }

    public void setCounselingImg(String counselingImg) {
        this.counselingImg = counselingImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
