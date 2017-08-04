package com.YinanSoft.CardReaders;

import android.graphics.Bitmap;

public class IDCardInfo extends beans.IDCardInfo {
    private String address = null;
    private String birthday = null;
    private String cardNum = null;
    private String gender = null;
    private String name = null;
    private String nation = null;
    private Bitmap photo = null;
    private Bitmap PreviewBitmap = null;
    private String registInstitution = null;
    private String validEndDate = null;
    private String validStartDate = null;
    private String newAddress = null;
    private String state = null; //人证识别成功失败状态
    private String compareScore = null;
    private String time = null;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompareScore() {
        return compareScore;
    }

    public void setCompareScore(String compareScore) {
        this.compareScore = compareScore;
    }

    public int getNationcode() {
        return nationcode;
    }

    public void setNationcode(int nationcode) {
        this.nationcode = nationcode;
    }

    private int nationcode = 0;
    private byte[] fingerInfo = null;

    public byte[] getFingerInfo() {
        return this.fingerInfo;
    }

    public String getNewAddress() {
        return this.newAddress;
    }

    public String getAddress() {
        return this.address;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public String getCardNum() {
        return this.cardNum;
    }

    public String getGender() {
        return this.gender;
    }

    public String getName() {
        return this.name;
    }

    public String getNation() {
        return this.nation;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public String getRegistInstitution() {
        return this.registInstitution;
    }

    public String getValidEndDate() {
        return this.validEndDate;
    }

    public String getValidStartDate() {
        return this.validStartDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //---------------------------------------------------------------------------------------
    //
    public void setFingerInfo(byte[] paramByte) {
        this.fingerInfo = paramByte;
    }

    public void setNewAddress(String paramString) {
        this.newAddress = paramString;
    }

    public void setAddress(String paramString) {
        this.address = paramString;
    }

    public void setBirthday(String paramString) {
        this.birthday = paramString;
    }

    public void setCardNum(String paramString) {
        this.cardNum = paramString;
    }

    public void setGender(String paramString) {
        this.gender = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setNation(String paramString) {
        this.nation = paramString;
    }

    public void setPhoto(Bitmap paramBitmap) {
        this.photo = paramBitmap;
    }

    public void setRegistInstitution(String paramString) {
        this.registInstitution = paramString;
    }

    public void setValidEndDate(String paramString) {
        this.validEndDate = paramString;
    }

    public void setValidStartDate(String paramString) {
        this.validStartDate = paramString;
    }

    public Bitmap getPreviewBitmap() {
        return PreviewBitmap;
    }

    public void setPreviewBitmap(Bitmap previewBitmap) {
        PreviewBitmap = previewBitmap;
    }
}

  