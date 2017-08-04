package com.YinanSoft.phoneface.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.decode.IConstants;
import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.camera.CameraFaceConfig;
import com.YinanSoft.phoneface.ui.camera.CameraManager;
import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.fragment.BaseCameraHandler;

import java.io.IOException;

/**
 * 拍照预览SurfaceView
 *
 * @author Xiaozhi
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, IConstants {

    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    //
    private static int sWidth = 480;
    private static int sHeight = 640;

    private boolean hasSurface; // 是否存在摄像头显示层
    private boolean isPortrait;
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private CameraManager mCameraManager;
    private BaseCameraHandler handler; // 这个是解码的回调句柄
    //    private Algorithm algorithm;
    private CameraFaceCallback mFaceCallback = DEFAULT_FACECALLBACK;
    private CameraFaceConfig mFaceConfig;
    private boolean isNetAccess = false;

    private int mPreviewWidth = 640;
    private int mPreviewHeight = 480;
    private CameraPreview mCameraPreviewSize;

    public void setPreviewSize(int previewWidth, int previewHeight) {
        this.mPreviewWidth = previewWidth;
        this.mPreviewHeight = previewHeight;
    }

    public enum FaceAction {
        VERIFY, ENROLL, CHECK, MODIFY
    }

    public enum ActionState {
        NULL, LOCAL, NET
    }

    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        mCameraManager = CameraManager.newInstance(context, this);
        mCameraManager.setPreviewSize(mPreviewWidth, mPreviewHeight);
        mFaceConfig = new CameraFaceConfig.Builder().build();
        initOrientation();
    }

    private void initOrientation() {
    }

    public void setIsPortrait(boolean isPortrait) {
        this.isPortrait = isPortrait;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Logs.i(TAG, "获取显示区域参数");
        int desiredWidth = sWidth;
        int desiredHeight = sHeight;

        float radio = (float) desiredWidth / (float) desiredHeight;

        Log.i(TAG, "获取显示区域参数 radio:" + radio);

        /**
         * 每个MeasureSpec均包含两种数据，尺寸和设定类型，需要通过 MeasureSpec.getMode和getSize进行提取
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 参考值竖屏 800 1214
        // 参考值横屏 1280 734
        int layout_width = 0;
        int layout_height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            // 精确值情况
            layout_width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // 范围值情况，哪个小取哪个？
            layout_width = Math.min(desiredWidth, widthSize);
        } else {
            // 没设定就是默认的了，呵呵
            layout_width = desiredWidth;
        }
        // 高度设定同上
        if (heightMode == MeasureSpec.EXACTLY) {
            layout_height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // height = Math.min(desiredHeight, heightSize);
            layout_height = Math.min(desiredWidth, widthSize) * 4 / 3;
        } else {
            layout_height = desiredHeight;
        }

        float layout_radio = (float) layout_width / (float) layout_height;

        if (layout_radio > radio) {
            layout_height = (int) (layout_width / radio);
        } else {
            layout_width = (int) (layout_height * radio);
        }

        setMeasuredDimension(layout_width, layout_height);
        Logs.i(TAG, "CSV设定宽度:" + widthSize + "  设定高度:" + heightSize);// 让我们来输出他们
        Logs.i(TAG, "CSV实际宽度:" + layout_width + "  实际高度:" + layout_height);// 让我们来输出他们
        Logs.i(TAG, "显示比例：" + ((float) layout_width / (float) layout_height));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logs.i(TAG, "surfaceCreated..." + hasSurface);
        if (!hasSurface && holder != null && !isNetAccess) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.closeDriver();
        hasSurface = false;
        Logs.i(TAG, "surfaceDestroyed..." + hasSurface);
    }

    public void onResume() {
        Logs.i(TAG, "onResume..." + hasSurface);
//        handler = null;// 清空handler
        if (hasSurface) {
            // 当activity暂停，但是并未停止的时候，surface仍然存在，所以 surfaceCreated()
            // 并不会调用，需要在此处初始化摄像头
            initCamera(mSurfaceHolder);
        } else {
            // 设置回调，等待 surfaceCreated() 初始化摄像头
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    public void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        mCameraManager.closeDriver();
        Logs.i(TAG, "onPause..." + hasSurface);
        if (!hasSurface) {
            mSurfaceHolder.removeCallback(this);
        }
    }

    public void setFaceCallback(CameraFaceCallback faceCallback) {
        if (faceCallback != null) {
            mFaceCallback = faceCallback;
        }
    }

    public CameraFaceCallback getFaceCallback() {
        return mFaceCallback;
    }

    public CameraFaceConfig getFaceConfig() {
        return mFaceConfig;
    }

    public void setFaceConfig(CameraFaceConfig faceConfig) {
        mFaceConfig = faceConfig;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    /**
     * 获取消息句柄
     *
     * @return
     */
    public BaseCameraHandler getHandler() {
        return handler;
    }

    public void pauseCamera() {
        if (handler != null)
            handler.pauseCamera();
    }

    /**
     * 延时，作为捕捉图片的时间间隔
     *
     * @param delayMS
     */
    public void startPreviewDelay(long delayMS) {
        if (handler != null) {
            Logs.i(TAG, "startPreviewDelay...");
            handler.sendEmptyMessageDelayed(RESTART_PREVIEW, delayMS);
        }
    }

    public void startCapture() {
        if (handler == null)
            return;
        handler.startCaptrue();
    }

    public Camera getCamera() {
        return mCameraManager.getCamera();
    }

    /**
     * 初始化摄像头，较为关键的内容
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("SurfaceHolder is null");
        }
        if (mCameraManager.isOpen()) {
            Logs.w(TAG, "Camera is opened！");
            return;
        }


        try {
            mCameraManager.setManualCameraId(mFaceConfig.getCameraId());

//            //绘制之前先对画布进行翻转
//            Canvas canvas = surfaceHolder.lockCanvas();
//            canvas.scale(-1,1, getWidth()/2,getHeight()/2);

            mCameraManager.openDriver(surfaceHolder);

            callPreviewSize();
            if (handler == null) {
                handler = new BaseCameraHandler(this);
                handler.startDecodeThread();
                Logs.e(TAG, "开启拍照线程...");
            }
        } catch (IOException ioe) {
            Logs.e(TAG, ioe);
            Logs.e(TAG, "出异常了");
        } catch (RuntimeException e) {
            Logs.e(TAG, "摄像头初始化失败", e);
            Logs.e(TAG, "摄像头初始化失败出异常了");
        }
    }

    public void attachFrameView(CameraPreview cameraPreviewSize) {
        if (cameraPreviewSize != null)
            mCameraPreviewSize = cameraPreviewSize;
    }
    
    private void callPreviewSize() {
        if (mCameraPreviewSize == null)
            return;

        //larry modify 20170410 如果在606a等竖屏设备上打开设置会导致预览变形
        if (Build.MODEL.toUpperCase().equals("JWZD-500")) {
            sWidth = mCameraManager.getWidth();
            sHeight = mCameraManager.getHeight();
        }
        requestLayout();
        mCameraPreviewSize.onPreviewSize(sWidth, sHeight);
    }

    public void setIsNetAccess(boolean isNetAccess) {
        this.isNetAccess = isNetAccess;
    }

    public boolean isNetAccess() {
        return isNetAccess;
    }

    public static final CameraFaceCallback DEFAULT_FACECALLBACK = new CameraFaceCallback() {

        @Override
        public void onFaceBefore() {

        }

        @Override
        public void onFacing(int state) {

        }

        @Override
        public void onDecodeSuc(Result obj) {

        }

        @Override
        public void onDecodeError(Result obj) {

        }

        @Override
        public void onCheckingNoFace() {

        }

        @Override
        public void onCheckSuc(CheckAction action) {

        }

        @Override
        public void onFaceAfter() {

        }

        @Override
        public void onFaceTimeOut() {

        }

        @Override
        public void onResult(Result result, Bitmap bitmap) {

        }

        @Override
        public void onTake() {

        }
    };

}
