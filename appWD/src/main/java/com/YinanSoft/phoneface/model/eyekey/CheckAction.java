package com.YinanSoft.phoneface.model.eyekey;

/**
 * 检活动作
 * <p>
 * Created by wangzhi on 2015/12/14.
 */
public enum CheckAction {

    NONE(Action.ACTION_NONE),

    /**
     * 眨眼
     */
    CLOSE_EYE(Action.ACTION_CLOSE_EYE),

    /**
     * 左歪头
     */
    LEFT_WRYNECK(Action.ACTION_LEFT_WRYNECK),

    /**
     * 右歪头
     */
    RIGHT_WRYNECK(Action.ACTION_RIGHT_WRYNECK),

    /**
     * 左转头
     */
    TURN_LEFT(Action.ACTION_TURN_LEFT),

    /**
     * 右转头
     */
    TURN_RIGHT(Action.ACTION_TURN_RIGHT),

    /**
     * 转头
     */
    TURNED(Action.ACTION_TURNED),

    /**
     * 张嘴
     */
    OPEN_MOUTH(Action.ACTION_OPEN_MOUTH),

    /**
     * 点头
     */
    NOD(Action.ACTION_NOD),

    /**
     * 直视摄像头
     */
    FRONT(Action.ACTION_FRONT);

    int mCode;

    public static class Action {
        public static final int ACTION_NONE = -1;
        public static final int ACTION_CLOSE_EYE = 0;
        public static final int ACTION_LEFT_WRYNECK = 1;
        public static final int ACTION_RIGHT_WRYNECK = 2;
        public static final int ACTION_TURN_LEFT = 3;
        public static final int ACTION_TURN_RIGHT = 4;
        public static final int ACTION_TURNED = 5;
        public static final int ACTION_OPEN_MOUTH = 6;
        public static final int ACTION_NOD = 7;
        public static final int ACTION_NEAR = 8;
        public static final int ACTION_FAR_FROM = 9;
        public static final int ACTION_VERFY_SUCCESS = 10;
        public static final int ACTION_FRONT = 20;
    }

    private CheckAction(int code) {
        this.mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
