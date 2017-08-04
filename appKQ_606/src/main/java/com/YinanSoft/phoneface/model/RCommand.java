package com.YinanSoft.phoneface.model;


import com.YinanSoft.phoneface.ui.view.CameraSurfaceView.FaceAction;

public class RCommand {

    private String faceId = "0001";  //表示人脸的ID值，网络通讯传参用
    private FaceAction faceAction = FaceAction.CHECK; //操作的动作值，通讯指令
    private boolean showConfirmMenu = false;//照片回显
    private boolean isNetMode = false;//联网
    private boolean isCheckLive = true;//检活
    private boolean isVoiceTip = true;//检活声音提示
    private boolean isTextTip = true;//检活文字提示
    private boolean isDistanceTip = true;


    public boolean isDistanceTip() {
        return isDistanceTip;
    }

    public void setDistanceTip(boolean isDistanceTip) {
        this.isDistanceTip = isDistanceTip;
    }

    public boolean isTextTip() {
        return isTextTip;
    }

    public void setTextTip(boolean isTextTip) {
        this.isTextTip = isTextTip;
    }

    public boolean isVoiceTip() {
        return isVoiceTip;
    }

    public void setVoiceTip(boolean isVoiceTip) {
        this.isVoiceTip = isVoiceTip;
    }

    public boolean isNetMode() {
        return isNetMode;
    }

    public void setNetMode(boolean isNetMode) {
        this.isNetMode = isNetMode;
    }


    public boolean isCheckLive() {
        return isCheckLive;
    }

    public void setCheckLive(boolean isCheckLive) {
        this.isCheckLive = isCheckLive;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public FaceAction getFaceAction() {
        return faceAction;
    }

    public void setFaceAction(FaceAction faceAction) {
        this.faceAction = faceAction;
    }

    public Boolean getShowConfirmMenu() {
        return showConfirmMenu;
    }

    public void setShowConfirmMenu(Boolean showConfirmMenu) {
        this.showConfirmMenu = showConfirmMenu;
    }

}
