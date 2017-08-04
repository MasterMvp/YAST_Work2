package attendance.yn.a606a.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;
import attendance.yn.a606a.Utils.DateUtil;
import attendance.yn.a606a.Utils.SoundPoolAudioClip;
import attendance.yn.a606a.bean.UserBean;

public class MainActivity extends BaseActivity implements CameraFaceCallback {
    //    private DrawerLayout drawer;
    private LinearLayout lin;
    private float startX, startY;

    /*-----------------------------------------------------------------------------------------*/
    private static final String TAG = MainActivity.class.getSimpleName();

    private CameraSurfaceView mSurfaceView;

    private FrameFaceView mFrameFaceView;

    boolean isMatching = false;

//    private static MyApplication mApp;

    private String mSrcFeatures = "";
    private int cameId = 1;
    private int timeout = 0;

    private Rect rect;
    private Bitmap faceBitmap = null;

    private AlertDialog dialog = null;
    private boolean isShowing = false;

    //测试图片放到这个路径下
    private final String saveImagePath = "/sdcard/YinAnFace";
    private final String nirImageFileName = saveImagePath + "/nir.jpg";
    private final String colorImageFileName = saveImagePath + "/color.jpg";
    private File dbgdir = null;
    //-------------------------------------------
    private static final boolean DEBUG = true;    // TODO set false on release
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
    private Bitmap bmLastColorImage = null;

    private Map<Integer, String> mapAllFeatrues = null;
    private Map<Integer, String> mapBlackFeatrues = null;
    private Map<Integer, String> mapColorFeatrues = null;
    private Map<Integer, String> mapFingerFeatures = null;
    private List<String> mBlackFeatureList = null;
    private List<String> mColorFeatureList = null;
    private List<String> mFingerFeatureList = null;
    private boolean isFindFace = false;
    private boolean isFaceSuccess = false;
    private boolean isFingerSuccess = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11://没找到脸
//                    if (isShowing) return;
//                    isShowing = true;
//                    isFindFace= false;
                    if (popupWindow != null && popupWindow.isShowing()) return;
                    showPopupWindow();
//                    handler.removeCallbacks(runable);
//                    mSurfaceView.postDelayed(runable, 10000);
                    break;
                case 12://找到脸
//                    if (isFindFace) return;
//                    isFindFace = true;
//                    isShowing = false;
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        isFindFace = false;
                    }
                    ;
                    break;
            }
        }
    };

    Runnable runable = new Runnable() {
        @Override
        public void run() {
            showPopupWindow();
        }
    };
    private boolean isExitVaildFinger = false;

    /*-----------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
//        if (!MyApplication.initFaceDeleSuccess) {
//            ToastUtil.showToast(MainActivity.this, "人脸识别授权失败。");
//            return;
//        }
        initCamera();
//        setFlashOnOff(true);//开灯
//        drawerInit();
//        setValues();
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
//                new Thread(runableValidFinger).start();
            }
        }, 2500);
//        mSurfaceView.postDelayed(runableValidFinger, 2500);
//        new Thread(runableValidFinger).start();
        setValues();//程序启动重新获取数据
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
                .setFrontTurnedMin(-30)
                .setFrontTurnedMax(30)
                .setFrontNodMinDegree(-5)
                .setFrontNodMaxDegree(10)
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
//        mCameraHandler.addCallback(UvcCameraCallback);
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            //Toast.makeText(SingleUvcCamera.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            if (device.getProductId() == 0x3841) {//10035
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
            if (device.getProductId() == 0x3841) {
                if (mCameraHandler != null) {// && !mCameraHandler.isOpened()) {
                    if (DEBUG) Log.v(TAG, "onConnect1:" + device);
                    mCameraHandler.addCallback(UvcCameraCallback);
                    mCameraHandler.open(ctrlBlock);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int count = 0;
                            SurfaceTexture st = null;
                            while (st == null && count++ <= 100) {
                                try {
                                    Thread.sleep(100);
                                    st = mUVCCameraView.getSurfaceTexture();
                                    if (DEBUG) Log.v(TAG, "onConnect5:" + count);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (st != null) {
                                mCameraHandler.startPreview(new Surface(st));
                                if (DEBUG) Log.v(TAG, "onConnect4:");
                            } else if (DEBUG) Log.v(TAG, "onConnect3:");
                        }
                    }).start();
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
            if (!isStillColorImage) return;

            int i = frame.limit();
            if (i > 0) {
                try {
                    final byte[] arrayOfByte = new byte[i];
                    frame.get(arrayOfByte);
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                YuvImage img = new YuvImage(arrayOfByte, ImageFormat.NV21, PREVIEW_WIDTH, PREVIEW_HEIGHT, null);
                                ByteArrayOutputStream output = new ByteArrayOutputStream();

                                img.compressToJpeg(new Rect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT), 100, output);
                                Bitmap faceBitmap = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size());
                                Matrix m = new Matrix();

                                if (FaceSDK.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                                    m.setRotate(90);
                                else m.setRotate(270);

                                if (bmLastColorImage != null) bmLastColorImage.recycle();
                                bmLastColorImage = Bitmap.createBitmap(faceBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, m, true);
                                saveBitmap(bmLastColorImage, colorImageFileName);
                            } finally {
                                synchronized (mSync) {
                                    mSync.notifyAll();
                                }
                            }
                        }
                    }, 0);
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
        try {
            mSurfaceView.startPreviewDelay(0);
        } catch (Exception e) {
            Log.e(TAG, "");
        }

        Stfaceattr faceAttr = obj.getStfaceattr();
        int[] locFace = faceAttr.getLocFace();
//        rect = new Rect(locFace[0], locFace[1], locFace[2], locFace[3]);
//        Log.e(TAG, "人脸坐标： " + locFace[0] + " " + locFace[1] + " " + locFace[2] + " " + locFace[3]);
        mFrameFaceView.setLocFace(locFace);
        Log.e(TAG, "onDecodeSuc111");
//        handler.removeMessages(11);
//        handler.sendEmptyMessage(12);
    }

    @Override
    public void onDecodeError(Result obj) {
        mFrameFaceView.setLocFace(null);
        mSurfaceView.startPreviewDelay(0);
//        Log.e(TAG, "onDecodeError222:" + isFindFace);
//        if (isFindFace) return;
//        isFindFace = true;
//        Log.e(TAG, "onDecodeError333");
//        handler.removeMessages(11);
//        handler.sendEmptyMessageDelayed(11, 5000);
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
        Logs.i(TAG, "onCheckingNoFace");
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
        if (mBlackFeatureList == null || mBlackFeatureList.size() == 0 || isMatching) {
            bitmap.recycle();
            return;
        }
        isMatching = true;
        if (Build.MODEL.toUpperCase().toString().equals("JWZD-500")) {
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
            matchBitmap(scaleBitmap, mBlackFeatureList, false);
        } else if (Build.MODEL.toUpperCase().toString().equals("SK-S600")) {
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
            matchBitmap(scaleBitmap, mBlackFeatureList, false);
        } else {
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
            matchBitmap(scaleBitmap, mBlackFeatureList, true);
        }
    }


    int score = 0;
    int size = 0;
    int count = 0;

    private int sScore = 0;
    private String featureL = "";

    private void matchBitmap(final Bitmap bitmap, List<String> featureList, boolean isCountinueCompare) {

        final String mSrcFeaturesL = FaceSDK.getFeature(bitmap);
//        Log.e(TAG, "Feature:" + mSrcFeaturesL);
//        Log.e(TAG, "lastFeature:" + lastFeature);
//        //同一特征值不会再次匹配人脸
//        if (lastFeature.equals(mSrcFeaturesL)) {
//            isMatching = false;
//            Log.e(TAG, "equals");
//            return;
//        }
        int compareScore = MyApplication.comcompareScore;
        if (isCountinueCompare) compareScore = MyApplication.comcompareScore + 5;
        featureL = "";
//        mapAllFeatrues = new HashMap<>();
//        size = featureList.size();
        Log.e(TAG, "特征值总库大小：" + featureList.size());
        for (int i = 0; i < featureList.size(); i++) {

            Log.e(TAG, "第：" + (i + 1) + "次比较");
            mSrcFeatures = featureList.get(i);
            final long start = System.currentTimeMillis();
//            Log.e("Log", "3333::" + mSrcFeatures);
            score = FaceSDK.match(mSrcFeatures, mSrcFeaturesL);
            mFrameFaceView.setresult("相似度:" + score);
//            ToastUtil.showToast(MainActivity.this, "分数：" + score);
            if (score > compareScore) {
                compareScore = score;
                featureL = mSrcFeatures;
//                mapAllFeatrues.put(score, mSrcFeatures);
                Log.e(TAG, (i + 1) + "次签到成功。" + score);
//                ToastUtil.showToast(MainActivity.this, "id:" + getValues(mSrcFeatures) + "，签到成功" + score);

            } else {
//                lastFeature = "";
                Log.e(TAG, (i + 1) + "次签到失败" + score);
//                isMatching = false;
            }

        }

        Log.e(TAG, "last:" + compareScore);
        //特质值比完后重新预览
        if (!"".equals(featureL)) {

            final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_show_userinfo, null);
            ImageView u_img = (ImageView) view.findViewById(R.id.u_img);
            TextView u_name = (TextView) view.findViewById(R.id.u_name);
            TextView u_department = (TextView) view.findViewById(R.id.u_department);
            TextView u_title = (TextView) view.findViewById(R.id.u_title);
            final Button button = (Button) view.findViewById(R.id.u_btn);
            UserBean userBean = MyApplication.dbManager.selectUserById(getValues(featureL, mapBlackFeatrues, mapColorFeatrues));//getMaxFeatures(mapAllFeatrues)
            u_img.setImageBitmap(userBean.getPhotoBitmap() != null ? userBean.getPhotoBitmap() : userBean.getPhotoBitmapBlack());
            u_name.setText(userBean.getName());
//                u_department.setText(userBean.getDepartment());
            u_title.setText("签到成功:" + compareScore);
            mFrameFaceView.setLocFace(null);

            MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.thanks);//签到成功

            userBean.setCreateTime(DateUtil.getNow(DateUtil.FORMAT_LONG));
            MyApplication.dbManager.addRecond(userBean);

            mSurfaceView.onPause();//停止预览

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setView(view)
                            .setCancelable(false)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    score = 0;
                                    mFrameFaceView.setLocFace(null);
                                    isMatching = false;
//                                    isExitVaildFinger = false;
                                    mSurfaceView.onResume();
                                    mSurfaceView.startCapture();

//                                    handler.removeCallbacks(runnable);
//                                    handler.postDelayed(runnable, 3000);
                                }
                            })
                            .show();
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();

                        }
                    });


                }
            });

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5000);

//                    lastFeature = getMaxFeatures(mapAllFeatrues);

        } else {
            if (isCountinueCompare) {
                isStillColorImage = true;
                try {
                    synchronized (mSync) {
                        //个别情况导致uvc摄像头没有启动，所以要设置一个超时时间。
                        mSync.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bmLastColorImage != null) {
                    if (mColorFeatureList == null || mColorFeatureList.size() == 0) {
                        isMatching = false;
                        return;
                    }
                    matchBitmap(bmLastColorImage, mColorFeatureList, false);
                } else {
                    Log.e(TAG, "bmLastColorImage为null。");
                }
            } else {
                isMatching = false;
            }

        }

    }

    //5s后
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            lastFeature = "";
            if (dialog != null && dialog.isShowing()) dialog.dismiss();

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
        if (DEBUG) Log.v(TAG, "onPause:");
        if (mSurfaceView != null)
            mSurfaceView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.v(TAG, "onDestroy:");
        setFlashOnOff(false);//关灯
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraView = null;

        handler.removeCallbacks(runable);
        handler.removeCallbacks(runnable);
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
//        releaseFingerPrinter();//释放指纹
        isExitVaildFinger = true;
        handler.removeCallbacks(runableValidFinger);
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


//    public void releaseAll() {
//        if (mSurfaceView != null)
//            mSurfaceView.onPause();
//
//        if (mCameraHandler != null) mCameraHandler.close();
//        if (mUVCCameraView != null) mUVCCameraView.onPause();
//        if (mUSBMonitor != null) mUSBMonitor.unregister();
//        setFlashOnOff(false);
//
//
//        if (DEBUG) Log.v(TAG, "releaseAll:");
//
//        if (mCameraHandler != null) {
//            mCameraHandler.release();
//            mCameraHandler = null;
//        }
//        if (mUSBMonitor != null) {
//            mUSBMonitor.destroy();
//            mUSBMonitor = null;
//        }
//        mUVCCameraView = null;
//    }


    //获取人脸特征值的map集合
    private void setValues() {
        mapBlackFeatrues = MyApplication.dbManager.selectUserBlackFeature();
        mapColorFeatrues = MyApplication.dbManager.selectUserColorFeature();
        mapFingerFeatures = MyApplication.dbManager.selectUserFingerFeature();
        mBlackFeatureList = new ArrayList<String>(mapBlackFeatrues.values());
        mColorFeatureList = new ArrayList<String>(mapColorFeatrues.values());
        mFingerFeatureList = new ArrayList<>(mapFingerFeatures.values());
        if (mapBlackFeatrues == null || mapBlackFeatrues.size() == 0 || mapColorFeatrues == null || mapColorFeatrues.size() == 0) {
            new AlertDialog.Builder(MainActivity.this)
                    .setCancelable(false)
                    .setTitle("未发现录入员工信息，请先去录入员工...")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    //
    private int getValues(String feature, Map<Integer, String> map, Map<Integer, String> map1) {
        int idcard = -1;
        if (map != null && map.size() > 0) {
            for (Map.Entry<Integer, String> v : map.entrySet()) {
                if (feature.equals(v.getValue())) {
                    idcard = v.getKey();
                    break;
                }
            }
            if (map1 != null && map1.size() > 0) {
                for (Map.Entry<Integer, String> v : map1.entrySet()) {
                    if (feature.equals(v.getValue())) {
                        idcard = v.getKey();
                        break;
                    }
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

    /*---------------------------------------------------------------------------------------------------------------*/

    private Runnable runableValidFinger = new Runnable() {
        @Override
        public void run() {
            validFingerTC();
        }
    };

    /**
     * 指纹比对
     *
     * @return
     */
    private void validFingerTC() {
        if (!MyApplication.initFingerSuccess) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    com.YinanSoft.phoneface.util.ToastUtil.showToast(MainActivity.this, "指纹初始化失败。");
                }
            });
            return;
        }
        isExitVaildFinger = false;
        try {
            int results = 0;
            int time = 10000;
            long start = System.currentTimeMillis();
//            handler.sendEmptyMessage(3);//请按指纹
//            Log.e("MianActivity", "isExitVaildFinger:" + isExitVaildFinger);
            while (!isExitVaildFinger) {
                Log.e(TAG, "isExitVaildFinger-while:" + isExitVaildFinger);
                isExitVaildFinger = true;
                Log.e(TAG, "isExitVaildFinger-while1:" + isExitVaildFinger);
                //采集指纹图像和特征值
                Main2Activity.fingerExt.fingerprint.featureBuffer = new byte[300];
                Main2Activity.fingerExt.fingerprint.featureBuffer0x30 = new byte[513];
                Main2Activity.fingerExt.fingerprint.nResult = Main2Activity.fingerExt.fingerprint.FP_FeatureAndTESOImageExtractAll(1,
                        Main2Activity.fingerExt.fingerprint.featureBufferHex,
                        Main2Activity.fingerExt.fingerprint.featureBuffer0x30,
                        Main2Activity.fingerExt.fingerprint.featureBuffer,
                        Main2Activity.fingerExt.fingerprint.imageBuffer,
                        Main2Activity.fingerExt.fingerprint.TESOimageBuffer,
                        Main2Activity.fingerExt.fingerprint.ImageAttr);
                if (Main2Activity.fingerExt.fingerprint.nResult >= 0) {
                    if (isFingerSuccess) return;
                    isMatching = true;//停止人脸检测
                    isExitVaildFinger = true;
                    Log.e("app", "ImageAttr[2] = " + Main2Activity.fingerExt.fingerprint.ImageAttr[2]);
                    Main2Activity.fingerExt.fingerprint.imgSize = Main2Activity.fingerExt.fingerprint.ImageAttr[0] * Main2Activity.fingerExt.fingerprint.ImageAttr[1] + 1024 + 54;
//                    bitmapFinger = BitmapFactory.decodeByteArray(Main2Activity.fingerExt.fingerprint.imageBuffer, 0, Main2Activity.fingerExt.fingerprint.imgSize);

                    System.arraycopy(Main2Activity.fingerExt.fingerprint.featureBuffer, 0,
                            Main2Activity.fingerExt.featureBuffer0, 0,
                            Main2Activity.fingerExt.fingerprint.featureBuffer.length);

                    float[] score = new float[]{0};
                    byte[] f1 = new byte[512];
                    byte[] f2 = new byte[512];
                    if (mFingerFeatureList != null && mFingerFeatureList.size() > 0) {
                        Log.e(TAG, "指纹Size:" + mFingerFeatureList.size());
                        String featuresF = "";
                        for (int i = 0; i < mFingerFeatureList.size(); i++) {
                            featuresF = mFingerFeatureList.get(i);
                            if ("".equals(featuresF)) continue;
                            Log.e(TAG, "指纹特征值：" + featuresF);
                            Log.e(TAG, (i + 1) + "次指纹比对。");
                            System.arraycopy(Main2Activity.fingerExt.featureBuffer0, 0, f1, 0, 512);
                            System.arraycopy(Main2Activity.fingerExt.featureBuffer0, 512, f2, 0, 512);//Base64.decode(featuresF)
                            //特征值比对
                            int result = Main2Activity.fingerExt.fingerprint.FP_FeatureMatch(Main2Activity.fingerExt.featureBuffer0,
                                    f1, score);
                            if (result >= 0 && score[0] <= 0)
                                result = Main2Activity.fingerExt.fingerprint.FP_FeatureMatch(Main2Activity.fingerExt.featureBuffer0,
                                        f2, score);
                            //比对结果处理
                            if (result >= 0) {
                                if (score[0] > 0 && !"".equals(featuresF)) {
                                    if (dialog != null && dialog.isShowing()) continue;
                                    Log.e(TAG, (i + 1) + "次指纹签到成功。");
                                    isFingerSuccess = false;
                                    Log.e(TAG, "isFingerSuccess2:" + isFingerSuccess);
                                    final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_show_userinfo, null);
                                    ImageView u_img = (ImageView) view.findViewById(R.id.u_img);
                                    TextView u_name = (TextView) view.findViewById(R.id.u_name);
                                    TextView u_department = (TextView) view.findViewById(R.id.u_department);
                                    TextView u_title = (TextView) view.findViewById(R.id.u_title);
                                    final Button button = (Button) view.findViewById(R.id.u_btn);
                                    UserBean userBean = MyApplication.dbManager.selectUserById(getValues(featuresF, mapFingerFeatures, null));//getMaxFeatures(mapAllFeatrues)
                                    Log.d("userBean", getValues(featuresF, mapFingerFeatures, null) + "");
                                    u_img.setImageBitmap(userBean.getPhotoBitmap() != null ? userBean.getPhotoBitmap() : userBean.getPhotoBitmapBlack());
                                    u_name.setText(userBean.getName());
//                u_department.setText(userBean.getDepartment());
                                    u_title.setText("指纹签到成功!");

                                    userBean.setCreateTime(DateUtil.getNow(DateUtil.FORMAT_LONG));
                                    MyApplication.dbManager.addRecond(userBean);

                                    Main2Activity.fingerExt.fingerprint.FP_Beep();
                                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.thanks);//签到成功

                                    mSurfaceView.onPause();//停止预览

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog = new AlertDialog.Builder(MainActivity.this)
                                                    .setView(view)
                                                    .setCancelable(false)
                                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                        @Override
                                                        public void onDismiss(DialogInterface dialog) {
                                                            isMatching = false;
                                                            isExitVaildFinger = false;
                                                            mSurfaceView.onResume();
                                                            mSurfaceView.startCapture();
                                                        }
                                                    })
                                                    .show();
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (dialog != null && dialog.isShowing())
                                                        dialog.dismiss();
                                                }
                                            });
                                        }
                                    });

                                    handler.removeCallbacks(runnable);
                                    handler.postDelayed(runnable, 5000);

                                } else {
                                    Log.e(TAG, (i + 1) + "次指纹签到失败。");
                                }
                            }
                        }
                    } else {
                        Thread.sleep(1000);
                        isExitVaildFinger = false;
                    }
                } else {
                    isExitVaildFinger = false;
                    Log.e(TAG, "未有指纹录入。");
                    Thread.sleep(1000);
                }
            }//for循环
        } catch (Exception e) {
            isMatching = false;
            isExitVaildFinger = false;
            Log.e(TAG, "指纹返回：" + e.getMessage());
        }
    }

    /**
     * 侧滑
     */
//    private void drawerInit() {
//        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
//        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                View mContent = drawer.getChildAt(0);
//                View mMenu = drawerView;
//                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;
//
//
//                float leftScale = 1 - 0.3f * scale;
//
//                ViewHelper.setScaleX(mMenu, leftScale);
//                ViewHelper.setScaleY(mMenu, leftScale);
//                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//                ViewHelper.setTranslationX(mContent,
//                        mMenu.getMeasuredWidth() * (1 - scale));
//                ViewHelper.setPivotX(mContent, 0);
//                ViewHelper.setPivotY(mContent,
//                        mContent.getMeasuredHeight() / 2);
//                mContent.invalidate();
//                ViewHelper.setScaleX(mContent, rightScale);
//                ViewHelper.setScaleY(mContent, rightScale);
//            }
//
//            Intent intent = null;
//
//            @Override
//            public void onDrawerOpened(View v) {
//                if (mSurfaceView != null)
//                    mSurfaceView.onPause();
//
//                Button query = (Button) v.findViewById(R.id.drawer_query);
//                Button add = (Button) v.findViewById(R.id.drawer_add);
//                Button delete = (Button) v.findViewById(R.id.drawer_delete);
//                Button qiyeName = (Button) v.findViewById(R.id.drawer_qiyeName);
//                Button time = (Button) v.findViewById(R.id.drawer_time);
//                Button pass = (Button) v.findViewById(R.id.drawer_pass);
//                qiyeName.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onSetting(1);
//                    }
//                });
//                time.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onSetting(2);
//                    }
//                });
//                pass.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onSetting(3);
//                    }
//                });
//                query.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        drawer.closeDrawers();
////                        releaseAll();
//                        intent = new Intent(MainActivity.this, QueryAct.class);
//                        startActivity(intent);
//                    }
//                });
//                add.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        drawer.closeDrawers();
////                        releaseAll();
//                        intent = new Intent(MainActivity.this, AddUserAct.class);
//                        startActivity(intent);
//                    }
//                });
//                delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        drawer.closeDrawers();
////                        releaseAll();
//                        intent = new Intent(MainActivity.this, AddUserAct.class);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//
//            public void onSetting(int type) {
//                drawer.closeDrawers();
//                intent = new Intent(MainActivity.this, SettingAct.class);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                if (mSurfaceView != null) {
//                    mSurfaceView.onResume();
//                    mSurfaceView.startCapture();
//                }
////                popupWindow.dismiss();
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//
//        });
//    }
//
//    /**
//     * 设置按钮的跳转
//     *
//     * @param v
//     */
//    public void onSettingClick(View v) {
//        drawer.openDrawer(Gravity.LEFT);
////        Intent intent = new Intent(MainActivity.this, SettingAct.class);
////        startActivity(intent);
//    }


    static TextView dialog_time1;
    static TextView dialog_time2;
    static PopupWindow popupWindow;

    private void showPopupWindow() {
        try {
            //        if (mSurfaceView != null) {
//            mSurfaceView.onPause();
//        }

            // 一个自定义的布局，作为显示的内容
            View contentView = LayoutInflater.from(this).inflate(
                    R.layout.time_dialog, null);
            dialog_time1 = (TextView) contentView.findViewById(R.id.dialog_time1);
            dialog_time2 = (TextView) contentView.findViewById(R.id.dialog_time2);
            // 设置按钮的点击事件

            popupWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("mengdd", "onTouch : ");
                    popupWindow.dismiss();
                    // 这里如果返回true的话，touch事件将被拦截
                    return false;
                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                }
            });
            ColorDrawable dw = new ColorDrawable(this.getResources().getColor(R.color.white));
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            popupWindow.setBackgroundDrawable(dw);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isFindFace = false;
                }
            });
            View view = LayoutInflater.from(this).inflate(
                    R.layout.act_main, null);
            // 设置好参数之后再show
            popupWindow.showAsDropDown(view);
        } catch (Exception e) {
            Log.e(TAG, "popupWindow 崩溃异常：" + e.toString());
        }

    }

    public static void setTime(String time1, int time2) {
        if (!popupWindow.isShowing()) {
            return;
        }
        dialog_time1.setText(time1);
        String week = "";
        switch (time2) {
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
            case 7:
                week = "星期日";
                break;
        }
        dialog_time2.setText(week);
    }

}
