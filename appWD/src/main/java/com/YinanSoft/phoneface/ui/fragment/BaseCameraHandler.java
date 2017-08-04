package com.YinanSoft.phoneface.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.YinanSoft.phoneface.common.MessageManager;
import com.YinanSoft.phoneface.decode.DecodeThread;
import com.YinanSoft.phoneface.decode.IConstants;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.camera.CameraManager;
import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;
//import com.techshino.tesoface.Algorithm;

/**
 * Fragment接收message并处理的handler
 *
 * @author James
 *         Created by James on 10/15/13
 */
public class BaseCameraHandler extends Handler implements IConstants {

    private static final String TAG = BaseCameraHandler.class.getSimpleName();

    private final DecodeThread decodeThread; //用于识别的真正线程
    private final CameraManager cameraManager; //摄像头管理器
    public final CameraSurfaceView mSurfaceView; //摄像头碎片的引用
    private final CameraFaceCallback mFaceCallback;

    private Handler mHandler;
    private State state;  //当前状态 枚举类型
    private Bitmap faceBitmap;
    Bitmap[] tempBitmap;
    /**
     * @author James
     *         枚举类型的状态值，
     *         预览，成功，完成
     */
    private enum State {
        PREVIEW, SUCCESS, DONE, PAUSE, ERROR
    }

    /**
     * 默认构造，只允许包内调用
     *
     * @param surfaceView
     */
    public BaseCameraHandler(CameraSurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
        this.mFaceCallback = surfaceView.getFaceCallback();
        this.cameraManager = mSurfaceView.getCameraManager();
        //传入SurfaceView和显示回调
        decodeThread = new DecodeThread(mSurfaceView, mFaceCallback);
        state = State.SUCCESS;//将状态置为success
        mHandler = this;
    }

    public void startCaptrue() {
        state = State.SUCCESS;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    /**
     * 启动解析线程并开始拍照
     */
    public void startDecodeThread() {
        mSurfaceView.getFaceCallback().onFaceBefore();
        decodeThread.start();
        cameraManager.startPreview();//这里才开始启动摄像头，并显示内容
//        restartPreviewAndDecode();
        //这里开始抓取图像
    }

    /* (non-Javadoc)
     * @see android.os.Handler#handleMessage(android.os.Message)
     * 系统核心业务逻辑一 捕捉人脸后业务逻辑处理
     */
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case RESTART_PREVIEW: //重启抓怕进程
                restartPreviewAndDecode();
                break;
            case DECODE_SUCCEDED: //找到一张脸，目前内容传回但是不做处理
                Log.v(TAG, "Decode SUCCEEDED");
                if (state != State.PAUSE) {
                    state = State.SUCCESS;
                    mFaceCallback.onDecodeSuc((Result) message.obj);
//                    restartPreviewAndDecode();
                }
                break;
            case DECODE_FAILED: //寻找失败，重启开始抓拍（状态不用改变）
                if (state != State.PAUSE) {
                    state = State.ERROR;
                    mFaceCallback.onDecodeError((Result) message.obj);
//                    restartPreviewAndDecode();
                }
                break;
            case PHOTO_VERFY_SUCCESS: //经过验照片之后的人脸，为最终的，准备发送的结果
//                pauseCamera();
                if (state == State.PAUSE)
                    return;
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                if (bundle != null) {
                    faceBitmap = (Bitmap) bundle.getParcelable(DecodeThread.FACE_BITMAP);
                }
                mFaceCallback.onResult((Result) message.obj, faceBitmap);
                mFaceCallback.onFaceAfter();
                break;
            case CHECKING_NO_FACE:
//                pauseCamera();
                mFaceCallback.onCheckingNoFace();
                mFaceCallback.onFaceAfter();
                break;
            case CAMERA_TIME_OUT:
                Log.i(TAG, "收到超时信息");
                pauseCamera();
                mFaceCallback.onFaceTimeOut();
                mFaceCallback.onFaceAfter();
                break;
            case NET_TIME_OUT:
                Log.i(TAG, "联网超时信息");
                pauseCamera();
                break;
            default:
                Log.v(TAG, "Unknown message: " + message.what);
                break;
        }
    }

    /**
     * 停止线程并退出
     */
    public void quitSynchronously() {
        Log.d(TAG, "停止线程并退出");
        state = State.DONE;
        cameraManager.stopPreview();

        MessageManager.sendToTarget(decodeThread.getHandler(), QUIT);
        try {
            decodeThread.join(500L);//等待500毫秒，然后将线程停止
        } catch (InterruptedException e) {
            // continue
        }
        clearMessage();
    }

    /**
     * 暂停摄像头并阻止消息，不停止线程
     */
    public void pauseCamera() {
        Log.d(TAG, "暂停摄像头并阻止消息");
        state = State.PAUSE;
        cameraManager.stopPreview();
        clearMessage();
    }

    private void clearMessage() {
        removeMessages(RESTART_PREVIEW);
        removeMessages(PHOTO_VERFY_SUCCESS);
        removeMessages(DECODE_SUCCEDED);
        removeMessages(DECODE_FAILED);
        removeMessages(CHECKING_NO_FACE);
        removeMessages(CAMERA_TIME_OUT);
    }

    /**
     * 重启摄像头并开始寻找人脸
     */
    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DECODE);
        } else if (state == State.PAUSE) {
            Log.d(TAG, "超时返回" + state + "：重新启动摄像头");
            cameraManager.startPreview();
            state = State.PREVIEW;
            Message onTimer = Message.obtain(decodeThread.getHandler(), ONTIMER);
            onTimer.sendToTarget();
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DECODE);
        } else if (state == State.ERROR) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DECODE);
        }
    }
}
