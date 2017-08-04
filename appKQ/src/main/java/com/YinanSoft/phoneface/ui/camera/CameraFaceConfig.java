package com.YinanSoft.phoneface.ui.camera;

import com.YinanSoft.phoneface.model.eyekey.CheckAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 图像检活配置
 * <p>
 * Created by wangzhi on 2015/12/4.
 */
public class CameraFaceConfig {

    public static final int CAMERA_BACK = 0;
    public static final int CAMERA_FRONT = 1;

    /**
     * 默认阈值
     */
    public static final int FRONT_MOUTH_DEGREE = 20;// 直视摄像头张嘴阈值
    public static final int FRONT_EYE_DEGREE = 3;// 直视摄像头睁眼阈值
    public static final int FRONT_TURNED_MIN = -15;// 直视摄像头转头最小阈值
    public static final int FRONT_TURNED_MAX = 15;// 直视摄像头转头最大阈值
    public static final int FRONT_NOD_MAX = -5;
    public static final int FRONT_NOD_MIN = -22;

    public static final double DISTANCE_EYES_MIN = 80;// 眼睛间距最小距离
    public static final double DISTANCE_EYES_MAX = 180;// 眼睛间距最大距离
    public static final int MOUTH_DEGREE = 18;// 张嘴阈值
    public static final int CLOSE_EYE_DEGREE = 18;// 闭眼阈值
    public static final int CIRCL_LEFT_DEGREE = 8;// 左转阈值
    public static final int CIRCL_RIGHT_DEGREE = 8;// 右转阈值
    public static final int NOD_DEGREE = 8;// 点头阈值
    public static final int TURNED_DEGREE = 8;// 转头阈值
    public static final long TIME_OUT_DELAY_S = 30;// 检测超时

    private int mFrontMouthDegree;// 直视摄像头张嘴阈值
    private int mFrontEyeDegree;// 直视摄像头睁眼阈值
    private int mFrontTurnedMinDegree;// 直视摄像头转头最小阈值
    private int mFrontTurnedMaxDegree;// 直视摄像头转头最大阈值
    private int mFrontNodMinDegree;
    private int mFrontNodMaxDegree;
    private double mDistanceEyesMin;// 眼睛间距最小距离
    private double mDistanceEyesMax;// 眼睛间距最大距离
    private int mOpenMouthDegree;// 张嘴阈值
    private int mCloseEyeDegree;// 闭眼阈值
    private int mCirclLeftDegree;// 左转阈值
    private int mCirclRightDegree;// 右转阈值
    private int mNodDegree;// 点头阈值
    private int mTurnedDegree;// 转头阈值
    private long mTimeoutS;// 超时时间设置
    private boolean isTime;

    private String faceId; // 表示人脸的ID值，网络通讯传参用
    private boolean isNetMode;// 联网
    private boolean isCheckLive;// 检活
    private int cameraId;
    private boolean isRandomCheck;
    private List<CheckAction> mCheckActionList;

    protected CameraFaceConfig() {
    }

    public int getCirclRightDegree() {
        return mCirclRightDegree;
    }

    public void setCirclRightDegree(int circlRightDegree) {
        mCirclRightDegree = circlRightDegree;
    }

    public int getCirclLeftDegree() {
        return mCirclLeftDegree;
    }

    protected void setCirclLeftDegree(int circlLeftDegree) {
        mCirclLeftDegree = circlLeftDegree;
    }

    public int getCloseEyeDegree() {
        return mCloseEyeDegree;
    }

    protected void setCloseEyeDegree(int closeEyeDegree) {
        mCloseEyeDegree = closeEyeDegree;
    }

    public int getOpenMouthDegree() {
        return mOpenMouthDegree;
    }

    protected void setOpenMouthDegree(int openMouthDegree) {
        mOpenMouthDegree = openMouthDegree;
    }

    public double getDistanceEyesMin() {
        return mDistanceEyesMin;
    }

    public void setDistanceEyesMin(double distanceEyesMin) {
        mDistanceEyesMin = distanceEyesMin;
    }

    public double getDistanceEyesMax() {
        return mDistanceEyesMax;
    }

    public void setDistanceEyesMax(double distanceEyesMax) {
        mDistanceEyesMax = distanceEyesMax;
    }

    protected void setFrontEyeDegree(int frontEyeDegree) {
        mFrontEyeDegree = frontEyeDegree;
    }

    protected void setFrontMouthDegree(int frontMouthDegree) {
        mFrontMouthDegree = frontMouthDegree;
    }

    public int getFrontMouthDegree() {
        return mFrontMouthDegree;
    }

    public int getFrontEyeDegree() {
        return mFrontEyeDegree;
    }

    protected void setFrontTurnedMaxDegree(int frontTurnedMaxDegree) {
        mFrontTurnedMaxDegree = frontTurnedMaxDegree;
    }

    public int getFrontTurnedMaxDegree() {
        return mFrontTurnedMaxDegree;
    }

    protected void setFrontTurnedMinDegree(int frontTurnedMinDegree) {
        mFrontTurnedMinDegree = frontTurnedMinDegree;
    }

    public int getFrontTurnedMinDegree() {
        return mFrontTurnedMinDegree;
    }

    public int getNodDegree() {
        return mNodDegree;
    }

    protected void setNodDegree(int nodDegree) {
        mNodDegree = nodDegree;
    }

    public int getTurnedDegree() {
        return mTurnedDegree;
    }

    protected void setTurnedDegree(int turnedDegree) {
        mTurnedDegree = turnedDegree;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public boolean isNetMode() {
        return isNetMode;
    }

    public void setIsNetMode(boolean isNetMode) {
        this.isNetMode = isNetMode;
    }

    public boolean isCheckLive() {
        return isCheckLive;
    }

    public void setIsCheckLive(boolean isCheckLive) {
        this.isCheckLive = isCheckLive;
    }

    public List<CheckAction> getCheckActionList() {
        return mCheckActionList;
    }

    public void setCheckActionList(List<CheckAction> checkActionList) {
        mCheckActionList = checkActionList;
    }

    public void setIsRandomCheck(boolean isRandomCheck) {
        this.isRandomCheck = isRandomCheck;
    }

    protected void setTimeoutS(long timeoutS) {
        mTimeoutS = timeoutS;
    }

    protected void setTime(boolean time) {
        isTime = time;
    }

    public boolean isTime() {
        return isTime;
    }

    public long getTimeoutS() {
        return mTimeoutS;
    }

    protected void setFrontNodMaxDegree(int frontNodMaxDegree) {
        mFrontNodMaxDegree = frontNodMaxDegree;
    }

    public int getFrontNodMaxDegree() {
        return mFrontNodMaxDegree;
    }

    protected void setFrontNodMinDegree(int frontNodMinDegree) {
        mFrontNodMinDegree = frontNodMinDegree;
    }

    public int getFrontNodMinDegree() {
        return mFrontNodMinDegree;
    }

    public static class Builder {

        private double mDistanceEyesMin = DISTANCE_EYES_MIN;// 眼睛间距最小距离
        private double mDistanceEyesMax = DISTANCE_EYES_MAX;// 眼睛间距最大距离
        private int mFrontMouthDegree = FRONT_MOUTH_DEGREE;// 直视摄像头张嘴阈值
        private int mFrontEyeDegree = FRONT_EYE_DEGREE;// 直视摄像头睁眼阈值
        private int mFrontTurnedMin = FRONT_TURNED_MIN;// 直视摄像头转头最小阈值
        private int mFrontTurnedMax = FRONT_TURNED_MAX;// 直视摄像头转头最大阈值
        private int mFrontNodMinDegree = FRONT_NOD_MIN;
        private int mFrontNodMaxDegree = FRONT_NOD_MAX;

        private int mOpenMouthDegree = MOUTH_DEGREE;// 张嘴阈值
        private int mCloseEyeDegree = CLOSE_EYE_DEGREE;// 闭眼阈值
        private int mCirclLeftDegree = CIRCL_LEFT_DEGREE;// 左转阈值
        private int mCirclRightDegree = CIRCL_RIGHT_DEGREE;// 右转阈值
        private int mNodDegree = NOD_DEGREE;// 点头阈值
        private int mTurnedDegree = TURNED_DEGREE;// 转头阈值
        private long mTimeoutS = TIME_OUT_DELAY_S;// 检测超时
        private boolean isTime = false;// 是否超时检测

        private String faceId = "0001"; // 表示人脸的ID值，网络通讯传参用
        private boolean isNetMode = false;// 联网
        private boolean isCheckLive = true;// 检活
        private boolean isRandomCheck = true;// 是否随机检活
        private int cameraId = CAMERA_FRONT;// 默认是前置摄像头
        private List<CheckAction> mCheckActionList = new ArrayList<CheckAction>();

        public Builder setCirclRightDegree(int circlRightDegree) {
            mCirclRightDegree = circlRightDegree;
            return this;
        }

        public Builder setCirclLeftDegree(int circlLeftDegree) {
            mCirclLeftDegree = circlLeftDegree;
            return this;
        }

        public Builder setCloseEyeDegree(int closeEyeDegree) {
            mCloseEyeDegree = closeEyeDegree;
            return this;
        }

        public Builder setOpenMouthDegree(int openMouthDegree) {
            mOpenMouthDegree = openMouthDegree;
            return this;
        }

        public Builder setFaceId(String faceId) {
            this.faceId = faceId;
            return this;
        }

        public Builder setIsNetMode(boolean isNetMode) {
            this.isNetMode = isNetMode;
            return this;
        }

        public Builder setIsCheckLive(boolean isCheckLive) {
            this.isCheckLive = isCheckLive;
            return this;
        }

        public Builder setCameraId(int cameraId) {
            this.cameraId = cameraId;
            return this;
        }

        public Builder addCheckActions(CheckAction... checkActions) {
            mCheckActionList.addAll(Arrays.asList(checkActions));
            return this;
        }

        public Builder addCheckActions(List<CheckAction> checkActions) {
            mCheckActionList.addAll(checkActions);
            return this;
        }

        public Builder addCheckAction(CheckAction checkAction) {
            mCheckActionList.add(checkAction);
            return this;
        }

        public Builder setIsRandomCheck(boolean isRandomCheck) {
            this.isRandomCheck = isRandomCheck;
            return this;
        }

        public Builder setFrontEyeDegree(int frontEyeDegree) {
            mFrontEyeDegree = frontEyeDegree;
            return this;
        }

        public Builder setFrontMouthDegree(int frontMouthDegree) {
            mFrontMouthDegree = frontMouthDegree;
            return this;
        }

        public Builder setFrontTurnedMin(int frontTurnedMin) {
            mFrontTurnedMin = frontTurnedMin;
            return this;
        }

        public Builder setFrontTurnedMax(int frontTurnedMax) {
            mFrontTurnedMax = frontTurnedMax;
            return this;
        }

        public Builder setDistanceEyesMin(double distanceEyesMin) {
            mDistanceEyesMin = distanceEyesMin;
            return this;
        }

        public Builder setDistanceEyesMax(double distanceEyesMax) {
            mDistanceEyesMax = distanceEyesMax;
            return this;
        }

        public Builder setNodDegree(int nodDegree) {
            mNodDegree = nodDegree;
            return this;
        }

        public Builder setTurnedDegree(int turnedDegree) {
            mTurnedDegree = turnedDegree;
            return this;
        }

        public Builder setFrontNodMinDegree(int frontNodMinDegree) {
            mFrontNodMinDegree = frontNodMinDegree;
            return this;
        }

        public Builder setFrontNodMaxDegree(int frontNodMaxDegree) {
            mFrontNodMaxDegree = frontNodMaxDegree;
            return this;
        }

        public Builder setTimeoutS(long timeoutS) {
            mTimeoutS = timeoutS;
            return this;
        }

        public Builder setTime(boolean time) {
            isTime = time;
            return this;
        }

        public long getTimeoutS() {
            return mTimeoutS;
        }

        public Builder randomCheck() {
            if (!isRandomCheck)
                return this;
            Collections.shuffle(mCheckActionList);
            return this;
        }

        public CameraFaceConfig build() {
            CameraFaceConfig config = new CameraFaceConfig();
            config.setFaceId(faceId);
            config.setIsCheckLive(isCheckLive);
            config.setIsNetMode(isNetMode);
            config.setCameraId(cameraId);
            config.setIsRandomCheck(isRandomCheck);
            config.setCheckActionList(mCheckActionList);
            config.setFrontEyeDegree(mFrontEyeDegree);
            config.setFrontMouthDegree(mFrontMouthDegree);
            config.setFrontTurnedMinDegree(mFrontTurnedMin);
            config.setFrontTurnedMaxDegree(mFrontTurnedMax);
            config.setDistanceEyesMax(mDistanceEyesMax);
            config.setDistanceEyesMin(mDistanceEyesMin);
            config.setOpenMouthDegree(mOpenMouthDegree);
            config.setCloseEyeDegree(mCloseEyeDegree);
            config.setCirclLeftDegree(mCirclLeftDegree);
            config.setCirclRightDegree(mCirclRightDegree);
            config.setNodDegree(mNodDegree);
            config.setFrontNodMaxDegree(mFrontNodMaxDegree);
            config.setFrontNodMinDegree(mFrontNodMinDegree);
            config.setTurnedDegree(mTurnedDegree);
            config.setTimeoutS(mTimeoutS);
            config.setTime(isTime);
            return config;
        }
    }
}
