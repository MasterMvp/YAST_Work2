package com.YinanSoft.phoneface.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class GResult implements Serializable {

    private String resultDetail;//结果细节
    private String resultInfo;//结果信息
    private Bitmap bitmap;//设置图片
    private int result;
    private String userName;
    private int matchScore;


    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getResultDetail() {
        return resultDetail;
    }

    public void setResultDetail(String resultDetail) {
        this.resultDetail = resultDetail;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private static final long serialVersionUID = 1L;
    private String idCard;                // 身份证号
    private String imageBase;             // 照片BASE64
    private String terminalType;          // 终端类型  2.苹果设备   ,3.Android设备
    private String resultCode;               // 标示成功，失败 1:1  0 成功  1 失败   |||||||，1：N 身份证号或1.失败
    private float verificationScore;      // 比对的分数或错误码（ 便于查找问题）

    public GResult() {
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public float getVerificationScore() {
        return verificationScore;
    }

    public void setVerificationScore(float verificationScore) {
        this.verificationScore = verificationScore;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getImageBase() {
        return imageBase;
    }

    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    @Override
    public String toString() {
        System.out.println("resultCode=" + getResultCode() + ",idCard=" + getIdCard());
        return super.toString();
    }
}