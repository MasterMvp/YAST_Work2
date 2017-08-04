/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package attendance.yn.a606a.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;

import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
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
import java.util.Timer;
import java.util.TimerTask;

import attendance.yn.a606a.R;

public final class DoubleUvcCameraActivity extends BaseActivity {
    private static final boolean DEBUG = true;    // TODO set false on release
    public static final String TAG = "DoubleUvcCamera";
    private static final boolean USE_SURFACE_ENCODER = true;
    private static final int PREVIEW_WIDTH = 640;
    private static final int PREVIEW_HEIGHT = 480;
    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};
    private USBMonitor mUSBMonitor = null;
    private UVCCameraHandler mCameraHandlerAll = null;
    private CameraViewInterface mUVCCameraViewAll = null;
    private Surface mUVCCameraSurfaceAll = null;
    private int mCameraAllPid = 8707;
    private UVCCameraHandler mCameraHandlerRightTop = null;
    private CameraViewInterface mUVCCameraViewRightTop = null;
    private Surface mUVCCameraSurfaceRightTop = null;
    private int mCameraRightTopPid = 8708;
    private Stfaceattr stfaceattr;
    private FrameFaceView mFrameFaceView;
    private String mSrcFeatures;
    private long timeout = 30;
    private int compareScore = 65;
    private boolean isMatching = false;
    private int score = 0;
    private ImageView imgIdPhoto;
    private Bitmap bmLastBlackImage = null;
    private Bitmap bmLastColorImage = null;
    private final Object mSync = new Object();
    private boolean isExit = false;
    private final String saveImagePath = "/sdcard/YinAnFace";
    private final String nirImageFileName = saveImagePath + "/nir.jpg";
    private final String colorImageFileName = saveImagePath + "/color.jpg";
    private File dbgdir = null;
    private int failTimes;
    private int failCounter;
    private Timer timer = new Timer();

    private void startTimer() {
        if (timeout == 0) return;
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Logs.v("timer excute");
                //synchronized (mSync) {
//                    isFinsih = true;
//                }
                exitProcess(-1, "", "");

            }
        }, timeout * 1000);
    }


    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }

    public void getParams() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Logs.i("Screen Params:", "width:" + width + "  height:" + height);
        Logs.i("设备型号：", Build.MODEL.toUpperCase().toString());
    }

    //初始化摄像头
    private void initCamera() {
        //彩色
        mUVCCameraViewAll = (CameraViewInterface) findViewById(R.id.camera_view_all);
        mUVCCameraViewAll.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        mCameraHandlerAll = UVCCameraHandler.createHandler(this, mUVCCameraViewAll, USE_SURFACE_ENCODER ? 0 : 1,
                PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, BANDWIDTH_FACTORS[0]);
//        mCameraHandlerAll.addCallback(cameraCallbackAll);

//        mCameraHandlerAll = UVCCameraHandler.createHandler(this, mUVCCameraViewAll,
//                USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);
//        mUVCCameraViewAll.setRotation(0.0F);//修改摄像头预览方向
        //view.setRotationY(180);
        //近红外
        mUVCCameraViewRightTop = (CameraViewInterface) findViewById(R.id.camera_view_rightTop);
        mUVCCameraViewRightTop.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        mCameraHandlerRightTop = UVCCameraHandler.createHandler(this, mUVCCameraViewRightTop, USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, 1, BANDWIDTH_FACTORS[0]);
//        mCameraHandlerRightTop.addCallback(cameraCallbackRightTop);
//        mCameraHandlerRightTop = UVCCameraHandler.createHandler(this, mUVCCameraViewRightTop,
//                USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);
//        view.setRotation(0.0F);//修改摄像头预览方向

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        //----------------------------------------------------------------------
        mFrameFaceView = (FrameFaceView) findViewById(R.id.finderV);
        //人脸框不对，调整这里
        mFrameFaceView.onPreviewSize(620, 500);
        imgIdPhoto = (ImageView) findViewById(R.id.img1);

    }

    private void getFeature(final Bitmap bitmap1) {
//        if (pd != null) pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long start = System.currentTimeMillis();

                int nWidth = 640, nHeight = 480;
                if (bitmap1.getWidth() != 640)
                    nWidth = (bitmap1.getWidth() > bitmap1.getHeight() ? 640 : 480);
                if (bitmap1.getHeight() != 480)
                    nHeight = (bitmap1.getHeight() > bitmap1.getWidth() ? 640 : 480);
                final Bitmap bitmapA = Bitmap.createScaledBitmap(bitmap1, nWidth, nHeight, true);

                if (bitmap1.getHeight() == 126 && bitmap1.getWidth() == 102)
                    mSrcFeatures = FaceSDK.getFeature(bitmap1);
                else mSrcFeatures = FaceSDK.getFeature(bitmapA);

                Logs.i("Aming", "提特征时间：" + (System.currentTimeMillis() - start) + "ms");

//                mFrameFaceView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (pd != null) pd.dismiss();
//                    }
//                });
            }
        }).start();
    }

    private void matchBitmap(final Bitmap bitmap1) {
        if (mSrcFeatures == null || isMatching) {
            bitmap1.recycle();
            return;
        }
//        synchronized (mSync) {
        isMatching = true;
//        }

        try {
            score = FaceSDK.match(FaceSDK.getFeature(bitmap1), mSrcFeatures);
            if (score <= 0) {
//                synchronized (mSync) {
                isMatching = false;
//                }
            } else dealRes(score, bitmap1);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void dealRes(int score, Bitmap bitmap) {
//        Logs.i(TAG, "finish...1");
//
//        if(isFinsih)    return;
//        if (score >= compareScore) {
//            Logs.i(TAG, "finish...2");
//            bmLastBlackImage = finalBitmap;
//            synchronized (mSync) {
//                isFinsih = true;
//            }
//        } else {
//            Logs.i(TAG, "finish...3(" + failCounter + ")");
//            if(++failCounter >= failTimes) {
//                bmLastBlackImage = finalBitmap;
//                synchronized (mSync) {
//                    isFinsih = true;
//                }
//            } else isMatching = false;//继续下一次比对
//            Logs.i(TAG, "next...");
//        }
//        mFrameFaceView.setresult("相似度:"+ score);
        if (score >= compareScore) {
            Logs.i(TAG, "finish...");
            exitProcess(score, nirImageFileName, colorImageFileName);
        } else {
//            bitmap.recycle();
            if (++failCounter >= failTimes) {
                exitProcess(score, nirImageFileName, colorImageFileName);
            } else {
                Logs.i(TAG, "next...");
                isMatching = false;
            }
        }
    }

    void exitProcess(int back_info, String face_picture, String face_picture_) {
        try {
            stopTimer();

            Intent intent = new Intent();

            if (face_picture.length() > 0 && bmLastBlackImage != null) {
                saveBitmap(bmLastBlackImage, face_picture);
                bmLastBlackImage.recycle();
                intent.putExtra("face_picture", face_picture);
            } else intent.putExtra("face_picture", "");

            if (face_picture_.length() > 0) {
                isStillColorImage = true;
                try {
                    synchronized (mSync) {
                        //个别情况导致uvc摄像头没有启动，所以要设置一个超时时间。
                        mSync.wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //比较彩色照片一次
                if (bmLastColorImage != null) {
                    final int score_ = FaceSDK.match(FaceSDK.getFeature(bmLastColorImage), mSrcFeatures);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(DoubleUvcCamera.this, "比较彩色照片一次" + score_, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    intent.putExtra("back_info_", score_);
                    intent.putExtra("face_picture_", face_picture_);
                } else {
                    intent.putExtra("back_info_", 0);
                    intent.putExtra("face_picture_", "");
                }
            } else {
                intent.putExtra("back_info_", 0);
                intent.putExtra("face_picture_", "");
            }

            intent.putExtra("back_info", back_info);
            setResult(RESULT_OK, intent);

            queueEvent(new Runnable() {
                @Override
                public void run() {
                    releaseCamera();
                    finish();
                }
            }, 0);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (isExit) return;
            Logs.v("onAttach0:" + device.getVendorId() + ":" + device.getProductId());


            if (device.getProductId() == mCameraAllPid || device.getProductId() == 10034 || device.getProductId() == 0x3722 || device.getProductId() == 0x2203) {
                Logs.v("onAttach1:" + device);

                if (mCameraHandlerAll != null && !mCameraHandlerAll.isOpened()) {

                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mUSBMonitor.requestPermission(device);
                        }
                    }, 0);
                }
            } else if (device.getProductId() == mCameraRightTopPid || device.getProductId() == 10033 || device.getProductId() == 0x3721 || device.getProductId() == 0x2204) {
                Logs.v("onAttach2:" + device);
                if (mCameraHandlerRightTop != null && !mCameraHandlerRightTop.isOpened()) {
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
            if (isExit) return;

            if (device.getProductId() == mCameraAllPid || device.getProductId() == 10034 || device.getProductId() == 0x3722|| device.getProductId() == 0x2203) {
                if (mUVCCameraViewAll != null && !mCameraHandlerAll.isOpened()) {
                    if (DEBUG) Log.v(TAG, "onConnect1:" + device);
                    mCameraHandlerAll.addCallback(cameraCallbackAll);
                    mCameraHandlerAll.open(ctrlBlock);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int count = 0;
                            SurfaceTexture st = null;
                            while (st == null && count++ <= 100) {
                                try {
                                    Thread.sleep(100);
                                    st = mUVCCameraViewAll.getSurfaceTexture();
                                    if (DEBUG) Log.v(TAG, "onConnect5:" + count);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (st != null) {
                                mCameraHandlerAll.startPreview(new Surface(st));
                                if (DEBUG) Log.v(TAG, "onConnect4:");
                            } else if (DEBUG) Log.v(TAG, "onConnect3:");
                        }
                    }).start();

                }
            } else if (device.getProductId() == mCameraRightTopPid || device.getProductId() == 10033 || device.getProductId() == 0x3721|| device.getProductId() == 0x2204) {
                if (mUVCCameraViewRightTop != null && !mCameraHandlerRightTop.isOpened()) {
                    if (DEBUG) Log.v(TAG, "onConnect2:" + device);
                    mCameraHandlerRightTop.addCallback(cameraCallbackRightTop);
                    mCameraHandlerRightTop.open(ctrlBlock);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int count = 0;
                            SurfaceTexture st = null;
                            while (st == null && count++ <= 100) {
                                try {
                                    Thread.sleep(100);
                                    st = mUVCCameraViewRightTop.getSurfaceTexture();
                                    if (DEBUG) Log.v(TAG, "onConnect5:" + count);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (st != null) {
                                mCameraHandlerRightTop.startPreview(new Surface(st));
                                if (DEBUG) Log.v(TAG, "onConnect4:");
                            } else if (DEBUG) Log.v(TAG, "onConnect3:");
                        }
                    }).start();


                }
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            if (isExit) return;

            if ((mCameraHandlerAll != null) && !mCameraHandlerAll.isEqual(device) && mCameraHandlerAll.isOpened()) {
                Logs.v("onDisconnect1:" + device);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mCameraHandlerAll.close();
                        if (mUVCCameraSurfaceAll != null) {
                            mUVCCameraSurfaceAll.release();
                            mUVCCameraSurfaceAll = null;
                        }
                    }
                }, 0);
            } else if ((mCameraHandlerRightTop != null) && !mCameraHandlerRightTop.isEqual(device) && mCameraHandlerRightTop.isOpened()) {// && isRight
                Logs.v("onDisconnect2:" + device);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mCameraHandlerRightTop.close();
                        if (mUVCCameraSurfaceRightTop != null) {
                            mUVCCameraSurfaceRightTop.release();
                            mUVCCameraSurfaceRightTop = null;
                        }
                    }
                }, 0);
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Logs.v("onDettach:" + device);
        }

        @Override
        public void onCancel(final UsbDevice device) {
            Logs.v("onCancel:");
        }
    };

    //右上角比对及保存黑白图片
    private UVCCameraHandler.CameraCallback cameraCallbackRightTop = new UVCCameraHandler.CameraCallback() {
        @Override
        public void onOpen() {
            Logs.v("onOpen:");
        }

        @Override
        public void onClose() {
            Logs.v("onClose:");
        }

        @Override
        public void onStartPreview() {
            Logs.v("onStartPreview:");
        }

        @Override
        public void onStopPreview() {
            Logs.v("onStopPreview:");
        }

        @Override
        public void onStartRecording() {
            Logs.v("onStartRecording:");
        }

        @Override
        public void onStopRecording() {
            Logs.v("onStopRecording:");
        }

        @Override
        public void onError(final Exception e) {
            Logs.v("LonError:" + e.getMessage());
        }

        @Override
        public void onPreview(final ByteBuffer frame) {
//            Logs.v("onPreview1(1)....");
            //退出标识直接退出、同时只进行一次比对
            if (isExit || isMatching) return;

//            Logs.v("onPreview1(2)....");
            byte[] arrayOfByte;
            int i = frame.limit();
            if (i > 0) {
                Logs.v("onPreview1(3)....");
                arrayOfByte = new byte[i];
                frame.get(arrayOfByte);
                if (mSrcFeatures != null) {
                    Logs.v("onPreview1(4)....");
                    Bitmap[] faceBitmap = new Bitmap[1];
                    int[] locFace = null;
                    stfaceattr = FaceSDK.decodeBitmap(arrayOfByte, 640, 480, 0, faceBitmap);//-1000);

                    //保存最后一张黑白图片
                    if (faceBitmap[0] != null) {
                        if (bmLastBlackImage != null) {
                            bmLastBlackImage.recycle();
                        }
                        bmLastBlackImage = faceBitmap[0];
                    }

                    Logs.v("onPreview1(5)....");
                    int nStat = FaceSDK.mDecodeStatus;
                    if (nStat >= 0) {
                        locFace = stfaceattr.getLocFace();
                        final int[] finalLocFace1 = locFace;

                        if (faceBitmap[0] != null) {
                            final Bitmap finalFace = faceBitmap[0];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Logs.v("onPreview1(6)....");
                                    mFrameFaceView.setresult("相似度:" + score);
                                    mFrameFaceView.setLocFace(finalLocFace1);
                                }
                            }, 0);
                            matchBitmap(finalFace);
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Logs.v("onPreview1(7)....");
                                mFrameFaceView.setresult("相似度:" + score);
                                mFrameFaceView.setLocFace(null);
                            }
                        }, 0);
                    }
                    Logs.v("onPreview1(18)....");
                }
            }

        }
    };

    private boolean isStillColorImage = false;
    //全屏显示彩色图像及保存彩色图片
    private UVCCameraHandler.CameraCallback cameraCallbackAll = new UVCCameraHandler.CameraCallback() {
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
//            Logs.v("onPreview2(1)....");
            if (!isStillColorImage || isExit) return;
            Logs.v("onPreview2(2)....");
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
//                                Matrix m = new Matrix();
//
//                                if(FaceSDK.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
//                                    m.setRotate(90);
//                                else m.setRotate(270);

                                if (bmLastColorImage != null) bmLastColorImage.recycle();
                                bmLastColorImage = faceBitmap;
//                                bmLastColorImage = Bitmap.createBitmap(faceBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, m, true);
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
                    if (mCameraHandlerAll != null)
                        mCameraHandlerAll.removeCallback(cameraCallbackAll);
                }
            }
        }
    };

    private void saveBitmap(Bitmap orcBitmap, String picPath) {
        Logs.i(TAG, "保存图像：" + picPath);
        if (orcBitmap == null || orcBitmap.isRecycled()) return;
        dbgdir = new File(saveImagePath);
        if (!dbgdir.exists()) {
            dbgdir.mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(picPath);
            orcBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onCreate:");
        setContentView(R.layout.activity_uvccamera);
        isExit = false;
//        isFinsih = false;
        isMatching = false;
        initCamera();
        startTimer();
        if (getIntent() != null) {
//                timeout = getIntent().getIntExtra("time_out", 30);
//                compareScore = getIntent().getIntExtra("compare_score", 65);
            failCounter = 0;
//                failTimes = getIntent().getIntExtra("times", 3);
            mSrcFeatures = getIntent().getStringExtra(AddUserAct.ARG_FEATURES);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onResume:");
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onPause:");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onStart:");
        try {
            mUSBMonitor.register();
            if (mUVCCameraViewAll != null)
                mUVCCameraViewAll.onResume();
            if (mUVCCameraViewRightTop != null)
                mUVCCameraViewRightTop.onResume();
        } catch (final Exception exp) {
        }
    }

    @Override
    protected void onStop() {
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onStop:");
        super.onStop();
    }

    void releaseCamera() {
        if (isExit) return;
        isExit = true;
        try {

            if (mCameraHandlerAll != null) {
                Log.e(TAG, "mCameraHandlerAll.release");
//                mCameraHandlerAll.removeCallback(cameraCallbackAll);
                mCameraHandlerAll.release();
                mCameraHandlerAll = null;
                cameraCallbackAll = null;
            }
            if (mUVCCameraSurfaceAll != null) {
                mUVCCameraSurfaceAll.release();
                mUVCCameraSurfaceAll = null;
            }
//            if(mUVCCameraViewAll != null) {
//                Log.e(TAG,"mUVCCameraViewAll.onPause");
//                mUVCCameraViewAll.onPause();
//                mUVCCameraViewAll = null;
//            }

            if (mCameraHandlerRightTop != null) {
                Log.e(TAG, "mCameraHandlerRightTop.release");
                mCameraHandlerRightTop.removeCallback(cameraCallbackRightTop);
                cameraCallbackRightTop = null;
                mCameraHandlerRightTop.release();
                mCameraHandlerRightTop = null;
            }
            if (mUVCCameraSurfaceRightTop != null) {
                mUVCCameraSurfaceRightTop.release();
                mUVCCameraSurfaceRightTop = null;
            }
//            if(mUVCCameraViewRightTop != null) {
//                Log.e(TAG,"mUVCCameraViewRightTop.onPause");
//                mUVCCameraViewRightTop.onPause();
//                mUVCCameraViewRightTop = null;
//            }

            if (mUSBMonitor != null) {
                Log.e(TAG, "mUSBMonitor.destroy");
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        } catch (final Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.e(TAG, "DoublleUvcCamera onDestroy:");
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        exitProcess(-3, "", "");
        super.onBackPressed();
    }
}