package com.YinanSoft.phoneface.ui.camera;

import android.graphics.RectF;

import com.YinanSoft.phoneface.ui.fragment.BaseCameraHandler;

/**
 * 人脸检测view状态回掉接口
 *
 * Created by wangzhi on 2015/12/3.
 */
public interface CameraViewStateCallback {

    void onViewStateCallback(int state, int faceRedraw, RectF faceRect, String liveString, BaseCameraHandler handler);
}
