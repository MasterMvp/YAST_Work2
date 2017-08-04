package com.YinanSoft.phoneface.ui.camera.live;

import android.graphics.Bitmap;

import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;

/**
 * 检活接口回掉
 *
 * Created by wangzhi on 2015/12/4.
 */
public interface CameraFaceCallback {

    /**
     * 检活前
     */
    void onFaceBefore();

    /**
     * 检活中（可以获取检活过程中的状态）
     */
    void onFacing(int state);

    /**
     * 解码人脸成功
     * @param obj
     */
    void onDecodeSuc(Result obj);

    /**
     * 解码人脸失败（没有人脸）
     * @param obj
     */
    void onDecodeError(Result obj);

    /**
     * 检活中丢帧
     */
    void onCheckingNoFace();

    /**
     * 检测人脸状态成功
     */
    void onCheckSuc(CheckAction action);

    /**
     * 检活后
     */
    void onFaceAfter();

    /**
     * 检活超时
     */
    void onFaceTimeOut();

    /**
     * 检活结果照片(检活成功后返回照片3张)
     */
    void onResult(Result result, Bitmap bitmap);

    /**
     * 拍照（期间可以增加拍照声音）
     */
    void onTake();

}
