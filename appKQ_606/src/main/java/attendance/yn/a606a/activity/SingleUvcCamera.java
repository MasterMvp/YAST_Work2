package attendance.yn.a606a.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;
import com.YinanSoft.phoneface.ui.view.FrameFaceView;
import com.YinanSoft.phoneface.util.FileUtils;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;

public class SingleUvcCamera extends BaseActivity implements com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback {
    private static final String TAG = SingleUvcCamera.class.getSimpleName();
    private CameraSurfaceView mSurfaceView;
    private FrameFaceView mFrameFaceView;
    private ImageView mTakeBtn;
    boolean isMatching = false;
    private String mSrcFeatures;
    private boolean bIsFirstFlash = false;
    private long timeout = 30;
    private int cameId = 1;

    private ImageView imgPhoto;
    private int failTimes = 10;
    private int failCounter;
    private Bitmap bmLastImage = null;
    int score = -2;
    //测试图片放到这个路径下
    private final String saveImagePath = "/sdcard/YinAnFace";
    private final String nirImageFileName = saveImagePath + "/nir.jpg";
    private final String colorImageFileName = saveImagePath + "/color.jpg";
    private File dbgdir = null;
    //-------------------------------------------
    private static final boolean DEBUG = true;    // TODO set false on release
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
    private int what_type = 1;

    //初始化摄像头
    private void initCamera() {
        //mipi摄像头初始化
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurface);
        mFrameFaceView = (FrameFaceView) findViewById(R.id.finderV);
        mSurfaceView.attachFrameView(mFrameFaceView);

        imgPhoto = (ImageView) findViewById(R.id.img1);

        com.YinanSoft.phoneface.ui.camera.CameraFaceConfig config = new com.YinanSoft.phoneface.ui.camera.CameraFaceConfig.Builder()
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

        if (!Build.MODEL.toUpperCase().toString().equals("JWZD-500") && !Build.MODEL.toUpperCase().toString().equals("SK-S600")) {//what_type 0拍照、1打开uvc、2不打开uvc
            View view = findViewById(R.id.camera_view);
            mUVCCameraView = (CameraViewInterface) view;
            mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);

            if (FaceSDK.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                view.setRotation(90.0F);//修改摄像头预览方向
            else view.setRotation(270.0F);//修改摄像头预览方向

            mCameraHandler = UVCCameraHandler.createHandler(this, mUVCCameraView, USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);

            mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        }
    }

    //UsbDevice mDevice = null;

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {

            //Toast.makeText(SingleUvcCamera.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            if (device.getProductId() == 0x3841) {
                if (DEBUG) Log.v(TAG, "onAttach:" + device);
                if (mCameraHandler != null) {// && !mCameraHandler.isOpened()) {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            //mDevice = device;
                            mUSBMonitor.requestPermission(device);
                        }
                    }, 0);
                }
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect:");
            try {
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
            } catch (Exception ex) {
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

    //-----------------------------------------------------------------------------
    private void matchBitmap(final Bitmap bitmap) {
        if (mSrcFeatures == null) {
            bitmap.recycle();
            Logs.i("AA", "AAAAAAA");
            return;
        }

//        final long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                score = FaceSDK.match(FaceSDK.getFeature(bitmap), mSrcFeatures);
                dealRes(bitmap, score);
            }
        }).start();
    }

    private void dealRes(Bitmap bitmap, int score) {
        mFrameFaceView.setresult("相似度:" + score);
        if (score >= MyApplication.comcompareScore - 20) {
            Logs.i(TAG, "finish...");
            exitProcess(score, nirImageFileName, colorImageFileName);
        } else {
            //bitmap.recycle();
            if (++failCounter >= failTimes) {
//                exitProcess(score, nirImageFileName, colorImageFileName);
                Log.e(TAG, "finish1...");
                if (!Build.MODEL.toUpperCase().toString().equals("SK-S600")) {
                    compareNext(colorImageFileName);
                } else {
                    mSurfaceView.startCapture();
                    Logs.i(TAG, "next...");
                    isMatching = false;
                }
            } else {
                mSurfaceView.startCapture();
                Logs.i(TAG, "next...");
                isMatching = false;
            }
        }
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

    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.v("onCreate....");
        try {
            setContentView(R.layout.activity_face_test);
            if (getIntent() != null) {
                //CAMERA_FACING_FRONT=1;CAMERA_FACING_BACK = 0;
                FaceSDK.facing = getIntent().getIntExtra("camera_facing", 0);
                mSrcFeatures = getIntent().getStringExtra(AddUserAct.ARG_FEATURES);
                what_type = getIntent().getIntExtra("what_type", 1);
                isStillColorImage = false;
                if (!TextUtils.isEmpty(mSrcFeatures)) {
                    FileUtils.deleteFile(colorImageFileName);
                    FileUtils.deleteFile(nirImageFileName);
                }
                failCounter = 0;
            } else {
                ToastUtil.showToast(SingleUvcCamera.this, "未获取到人脸特征值。");
                finish();
            }
            initCamera();
        } catch (Exception exp) {
            exp.printStackTrace();
            finish();
        }
    }

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
    protected void onResume() {
        super.onResume();
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //启动近红外人脸比对
                mSurfaceView.onResume();
                mSurfaceView.startCapture();
            }
        }, 2500);
//        if (mDevice != null) {
//            mUSBMonitor.requestPermission(mDevice);
//        }
    }

    @Override
    protected void onPause() {
        if (mSurfaceView != null)
            mSurfaceView.onPause();
        if (mCameraHandler != null) {
            mCameraHandler.close();
        }
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

    @Override
    public void onFaceBefore() {
        setFlashOnOff(true);
    }

    @Override
    public void onFacing(int state) {
    }

    @Override
    public void onDecodeSuc(Result obj) {
        mSurfaceView.startPreviewDelay(0);
        Stfaceattr faceAttr = obj.getStfaceattr();
        int[] locFace = faceAttr.getLocFace();
//        rect = new Rect(locFace[0], locFace[1], locFace[2], locFace[3]);
//        Log.e(TAG, "人脸坐标： " + locFace[0] + " " + locFace[1] + " " + locFace[2] + " " + locFace[3]);
        mFrameFaceView.setLocFace(locFace);
    }

    @Override
    public void onDecodeError(Result obj) {
        mFrameFaceView.setLocFace(null);
        mSurfaceView.startPreviewDelay(0);
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

    void exitProcess(int back_info, String face_picture, String face_picture_) {
        try {
            Intent intent = new Intent();

            if (face_picture.length() > 0 && bmLastImage != null && !bmLastImage.isRecycled()) {
                saveBitmap(bmLastImage, face_picture);
                bmLastImage.recycle();
                intent.putExtra("face_picture", face_picture);
            } else intent.putExtra("face_picture", "");

            if (face_picture_.length() > 0) {
//                if (++failCounter <= failTimes + 10){
//
//                }
                isStillColorImage = true;
                try {
                    synchronized (mSync) {
                        //个别情况导致uvc摄像头没有启动，所以要设置一个超时时间。
                        mSync.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //比较彩色照片一次
                if (bmLastColorImage != null) {

                    int score_ = -2;//-2拍照返回

//                    if (what_type == 0) {
//                        score_ = FaceSDK.match(FaceSDK.getFeature(bmLastColorImage), mSrcFeatures);
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(SingleUvcCamera.this, "比较彩色照片一次" + score_, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    intent.putExtra("back_info_", what_type == 0 ? score_ : score);
                    intent.putExtra("face_picture_", face_picture_);
                } else {
                    intent.putExtra("back_info_", -2);
                    intent.putExtra("face_picture_", "");
                }
            } else {
                intent.putExtra("back_info_", 0);
                intent.putExtra("face_picture_", "");
            }
//            intent.putExtra("back_info_", 0);
//            intent.putExtra("face_picture_", "");
            intent.putExtra("back_info", back_info);
            setResult(RESULT_OK, intent);
            //解决有时保存的彩色图片花屏问题
//            synchronized (mSync) {
//
//            }
            finish();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    void compareNext(String face_picture_) {
        try {
            if (face_picture_.length() > 0) {

                isStillColorImage = true;
                try {
                    synchronized (mSync) {
                        //个别情况导致uvc摄像头没有启动，所以要设置一个超时时间。
                        mSync.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //比较彩色照片一次
                if (bmLastColorImage != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            score = FaceSDK.match(FaceSDK.getFeature(bmLastColorImage), mSrcFeatures);
//                            dealRes(bmLastColorImage, score);
                            mFrameFaceView.setresult("相似度:" + score);
                            if (score >= MyApplication.comcompareScore - 16) {
                                Logs.i(TAG, "finish2...");
                                exitProcess(score, nirImageFileName, colorImageFileName);
                            } else {
                                //bitmap.recycle();
                                if (++failCounter >= failTimes + 10) {
                                    exitProcess(score, nirImageFileName, colorImageFileName);
                                    Logs.i(TAG, "finish3...");
                                } else {
                                    compareNext(colorImageFileName);
                                    Logs.i(TAG, "next1...");
                                }
                            }
                        }
                    }).start();
                } else {
                    exitProcess(score, nirImageFileName, colorImageFileName);
                    Log.e(TAG, "bmLastColorImage == null");
                }
            } else {
                exitProcess(score, nirImageFileName, colorImageFileName);
                Log.e(TAG, "face_picture_ == null");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    @Override
    public void onFaceTimeOut() {
        exitProcess(-1, "", "");
    }

    @Override
    public void onResult(Result result, Bitmap bitmap) {
        try {
            //动态对比
            if (isMatching) {
                bitmap.recycle();
                return;
            }

            isMatching = true;
            //if (bmLastImage != null) bmLastImage.recycle();
            bmLastImage = Bitmap.createScaledBitmap(bitmap, 480, 640, true);//Bitmap.createScaledBitmap(bitmap, 480, 640, true);
            if (what_type == 0) {//拍照返回
                exitProcess(score, nirImageFileName, colorImageFileName);
            } else {
                matchBitmap(bitmap);
            }

        } catch (Exception exp) {
        }
    }

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
    public void onTake() {
    }

    @Override
    public void onBackPressed() {
        exitProcess(-3, "", "");
        super.onBackPressed();
    }

//    public static String bitmaptoString(Bitmap bitmap) {
//
//        // 将Bitmap转换成字符串
//        String string = null;
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bStream);
//        byte[] bytes = bStream.toByteArray();
//        string = Base64.encodeToString(bytes, Base64.DEFAULT);
//        return string;
//    }
//
//    public Bitmap convertStringToIcon(String st) {
//        // OutputStream out;
//        Bitmap bitmap = null;
//        try {
//            byte[] bitmapArray;
//            bitmapArray = Base64.decode(st, Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
//                    bitmapArray.length);
//            return bitmap;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//    private ProgressDialog pd = null;
//
//    private ProgressDialog initPd() {
//        if (pd == null) pd = new ProgressDialog(SingleUvcCamera.this);
//        pd.setMessage("处理中，请稍等...");
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setCanceledOnTouchOutside(false);
//        return pd;
//    }
}
