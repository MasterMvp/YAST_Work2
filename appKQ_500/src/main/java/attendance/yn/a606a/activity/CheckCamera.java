package attendance.yn.a606a.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.camera.CameraFaceConfig;
import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;
import com.YinanSoft.phoneface.ui.view.FrameFaceView;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import attendance.yn.a606a.R;

/**
 * Created by Administrator on 2017/4/28.
 */
public class CheckCamera extends BaseActivity implements CameraFaceCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CameraSurfaceView mSurfaceView;

    private FrameFaceView mFrameFaceView;

    boolean isMatching = false;

//    private static MyApplication mApp;

    private String mSrcFeatures = "";
    private int cameId = 1;
    private int compareScore = 45;
    private int timeout = 0;

    private Rect rect;
    private Bitmap faceBitmap = null;

    private AlertDialog dialog = null;
    private String lastFeature = "";

    //测试图片放到这个路径下
    private final String saveImagePath = "/sdcard/YinAnFace";
    private final String nirImageFileName = saveImagePath + "/nir.jpg";
    private final String colorImageFileName = saveImagePath + "/color.jpg";
    private File dbgdir = null;
    //-------------------------------------------
    private static final boolean DEBUG = false;    // TODO set false on release
    private int time = 3;
    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};
    /**
     * set true if you want to record movie using MediaSurfaceEncoder
     * (writing frame data into Surface camera from MediaCodec
     * by almost same way as USBCameratest2)
     * set false if you want to record movie using MediaVideoEncoder
     */
    private static final boolean USE_SURFACE_ENCODER = false;
    /**
     * preview resolution(width)
     * if your camera does not support specific resolution and mode,
     * throw exception
     */
    private static final int PREVIEW_WIDTH = 640;
    /**
     * preview resolution(height)
     * if your camera does not support specific resolution and mode,
     * throw exception
     */
    private static final int PREVIEW_HEIGHT = 480;
    /**
     * preview mode
     * if your camera does not support specific resolution and mode,
     * throw exception
     * 0:YUYV, other:MJPEG
     */
    private static final int PREVIEW_MODE = 1;
    protected static final int SETTINGS_HIDE_DELAY_MS = 2500;
    /**
     * for accessing USB
     */
    private USBMonitor mUSBMonitor;
    /**
     * Handler to execute camera related methods sequentially on private thread
     */
    private UVCCameraHandler mCameraHandler;
    /**
     * for camera preview display
     */
    private CameraViewInterface mUVCCameraView;
    private Stfaceattr stfaceattr;
    private Object mSync = new Object();
    private boolean isStillColorImage = false;
    private Bitmap bmLastColorImage;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
//        if (!MyApplication.initFaceDeleSuccess) {
//            ToastUtil.showToast(MainActivity.this, "人脸识别授权失败。");
//            return;
//        }
        initCamera();

    }

    //
    private int getValues(String feature, Map<Integer, String> map) {
        int idcard = -1;
        if (map != null) {
            for (Map.Entry<Integer, String> v : map.entrySet()) {
                if (feature.equals(v.getValue())) {
                    idcard = v.getKey();
                    break;
                }
            }
        }
        return idcard;
    }


    private String getMaxFeatures(Map<Integer, String> map) {

        int initValue = 0;
        // 遍历map集合取最高分数的键值
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (initValue < entry.getKey()) {
                initValue = entry.getKey();
            }
            System.out.println("key= " + entry.getKey());
        }
        return map.get(initValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        mSurfaceView.onResume();
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSurfaceView.startCapture();
            }
        }, 500);
    }

    //初始化摄像头
    private void initCamera() {
        //mipi摄像头初始化
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurface);
        mFrameFaceView = (FrameFaceView) findViewById(R.id.finderV);
        mSurfaceView.attachFrameView(mFrameFaceView);

        CameraFaceConfig config = new CameraFaceConfig.Builder()
                .setCameraId(cameId)
                .setDistanceEyesMin(0)
                .setDistanceEyesMax(300)
                .setIsCheckLive(false)
                .setTime(timeout > 0 ? true : false)
                .setTimeoutS(timeout)
                .build();
        mSurfaceView.setFaceConfig(config);
        mSurfaceView.setFaceCallback(this);
        //--------------------------------------------------------------------
        //uvc摄像头初始化
        View view = findViewById(R.id.camera_view);
        mUVCCameraView = (CameraViewInterface) view;
//        mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);

        if (FaceSDK.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            view.setRotation(90.0F);//修改摄像头预览方向
        else view.setRotation(270.0F);//修改摄像头预览方向

        mCameraHandler = UVCCameraHandler.createHandler(this, mUVCCameraView, USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        mCameraHandler.addCallback(UvcCameraCallback);
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            //Toast.makeText(SingleUvcCamera.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            if (device.getProductId() == 0x3841) {
                if (DEBUG) Log.v(TAG, "onAttach:" + device);
                if (mCameraHandler != null && !mCameraHandler.isOpened()) {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mUSBMonitor.requestPermission(device);
                        }
                    }, 0);
                }
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect:");
//            Toast.makeText(SingleUvcCamera.this, "onConnect", Toast.LENGTH_SHORT).show();
            if (device.getProductId() == 0x3841) {//0x3841
                if (mCameraHandler != null && !mCameraHandler.isOpened()) {
                    if (DEBUG) Log.v(TAG, "onConnect1:" + device);
                    mCameraHandler.addCallback(UvcCameraCallback);
                    mCameraHandler.open(ctrlBlock);
                    final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                    if (st != null) mCameraHandler.startPreview(new Surface(st));
                }
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "onDisconnect:");
            if ((mCameraHandler != null) && !mCameraHandler.isEqual(device) && mCameraHandler.isOpened()) {
                Logs.v("onDisconnect1:" + device);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mCameraHandler.close();
                        mCameraHandler.removeCallback(UvcCameraCallback);
                    }
                }, 0);
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
//            Toast.makeText(UVCCameraActivityOther.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };
    //全屏显示彩色图像及保存彩色图片
    private UVCCameraHandler.CameraCallback UvcCameraCallback = new UVCCameraHandler.CameraCallback() {
        @Override
        public void onOpen() {
        }

        @Override
        public void onClose() {
        }

        @Override
        public void onStartPreview() {
        }

        @Override
        public void onStopPreview() {
            //if(isExit)  ExitProcess();
        }

        @Override
        public void onStartRecording() {
        }

        @Override
        public void onStopRecording() {
        }

        @Override
        public void onError(final Exception e) {
        }


        @Override
        public void onPreview(final ByteBuffer frame) {
            Logs.v("onPreview(1)....");
            ToastUtil.showToast(CheckCamera.this, "onPreview");
//            if (!isStillColorImage) return;

            int i = frame.limit();
            if (i > 0) {
                try {
                    final byte[] arrayOfByte = new byte[i];
                    frame.get(arrayOfByte);
                    Logs.v("onPreview1(4)....");
                    Bitmap[] faceBitmap = new Bitmap[1];
                    int[] locFace = null;
                    stfaceattr = FaceSDK.decodeBitmap(arrayOfByte, 640, 480, 0, faceBitmap);//-1000);

                    Logs.v("onPreview1(5)....");
                    int nStat = FaceSDK.mDecodeStatus;
                    ToastUtil.showToast(CheckCamera.this,"nStat:" + nStat);
                    Logs.v("onPreview1(18)....");


                } catch (final Exception exp) {
                    exp.printStackTrace();
                } finally {
                    isStillColorImage = false;
                }
            }
        }
    };


    @Override
    public void onDecodeSuc(Result obj) {
        mSurfaceView.startPreviewDelay(0);
        Stfaceattr faceAttr = obj.getStfaceattr();
        int[] locFace = faceAttr.getLocFace();
        rect = new Rect(locFace[0], locFace[1], locFace[2], locFace[3]);
        Log.e(TAG, "人脸坐标： " + locFace[0] + " " + locFace[1] + " " + locFace[2] + " " + locFace[3]);
        mFrameFaceView.setLocFace(locFace);
    }

    @Override
    public void onDecodeError(Result obj) {
        mFrameFaceView.setLocFace(null);
        mSurfaceView.startPreviewDelay(0);
    }

    @Override
    public void onFaceBefore() {
        setFlashOnOff(true);
    }

    @Override
    public void onTake() {

    }

    @Override
    public void onFacing(int state) {

    }


    @Override
    public void onCheckingNoFace() {
        Log.i(TAG, "onCheckingNoFace");
        if (dialog != null && dialog.isShowing()) {
            isMatching = false;
            dialog.dismiss();
        }
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
        //动态对比
//        if (mBlackFeatureList == null || isMatching) {
//            bitmap.recycle();
//            return;
//        }
//        isMatching = true;
//        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
//        matchBitmap(scaleBitmap);
    }


    int score = 0;
    int size = 0;
    int count = 0;

    private int sScore = 0;
    private String featureL = "";


    //5s后
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            lastFeature = "";
            dialog.dismiss();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG) Log.v(TAG, "onStart:");
        if (mUSBMonitor != null) mUSBMonitor.register();
        if (mUVCCameraView != null) mUVCCameraView.onResume();
    }

    @Override
    protected void onStop() {
        if (DEBUG) Log.v(TAG, "onStop:");
        if (mCameraHandler != null) mCameraHandler.close();
        if (mUVCCameraView != null) mUVCCameraView.onPause();
        if (mUSBMonitor != null) mUSBMonitor.unregister();
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (mSurfaceView != null)
            mSurfaceView.onPause();

        setFlashOnOff(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.v(TAG, "onDestroy:");

        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraView = null;

        super.onDestroy();
    }

    public void setFlashOnOff(boolean on) {
        Camera cam = mSurfaceView.getCamera();
        if (cam != null) {
            Camera.Parameters paramsBefore = cam.getParameters();
            if (on) {
                paramsBefore.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else paramsBefore.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(paramsBefore);
        }
    }

    private void saveBitmap(Bitmap orcBitmap, String fileName) {
        Logs.i(TAG, "保存图像[" + fileName + "]");
        dbgdir = new File(saveImagePath);
        if (!dbgdir.exists()) {
            dbgdir.mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            orcBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*---------------------------------------------------------------------------------------------------------------*/



}
