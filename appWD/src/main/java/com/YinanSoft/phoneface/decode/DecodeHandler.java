/*
 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.YinanSoft.phoneface.decode;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.YinanSoft.phoneface.Constants;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.MessageManager;
import com.YinanSoft.phoneface.common.Stfaceattr;
import com.YinanSoft.phoneface.common.executor.AsyncTaskExecInterface;
import com.YinanSoft.phoneface.common.executor.AsyncTaskExecManager;
import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.camera.CameraFaceConfig;
import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;

import java.util.ArrayList;
import java.util.List;

public final class DecodeHandler extends Handler implements IConstants {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private static final int CHECK_INTERVAL_TIME = 300;
    private static final int TEMP_VALUE_LENGTH = 4;

    private final CameraSurfaceView mSurfaceView;
    //private final Algorithm algorithm;
    private final CameraFaceCallback mCameraFaceCallback;
    private final CameraFaceConfig mFaceConfig;
    // 超时30秒后返回,主要用于捕捉时超时
    private TimerAsyncTask timerAsyncTask; // 异步定时器
    private final AsyncTaskExecInterface taskExec; // 异步任务接口，根据系统类型调用不同的异步任务实现
    private boolean running = true;
    private int times = 0;
    private boolean liveCheckSuccess = false;
    private boolean normalPicSuccess = false;
    /**
     * 是否已经开始检测人脸
     */
    private boolean isStartChecking = false;
    /**
     * 是否检活成功
     */
    private boolean isVerfySuccess = false;

    private List<CheckAction> mCheckActionList = new ArrayList<CheckAction>();
    private CheckAction mCheckAction = CheckAction.NONE;
    private int[] tempValue;
    private Bitmap[] tempBitmap;
    //private int mDecodeStatus;
    public static int mCameraId;
    private int mOrientation;
    /**
     * 图片宽高
     */
    //int[] hWdHi = new int[2];
    /**
     * 图片解析Rgb
     */
    //byte[] faceRgb24 = null;
    Result faceResult = null;
    Handler handler = null;

    byte[] mCompared = null;
    boolean isFirstBitmap = true;
    private long oneFrameTime;

    DecodeHandler(CameraSurfaceView surfaceView,
                  CameraFaceCallback faceCallback) {
        this.mSurfaceView = surfaceView;
        //this.algorithm = algorithm;
        this.mFaceConfig = surfaceView.getFaceConfig();
        this.mCameraId = surfaceView.getCameraManager().getManualCameraId();
        this.mOrientation = surfaceView.getCameraManager().getOrientation();
        mCameraFaceCallback = faceCallback;
        taskExec = new AsyncTaskExecManager().build();
        if (mFaceConfig.isTime()) onTimer();// 开始计时
        tempValue = new int[TEMP_VALUE_LENGTH];
        tempBitmap = new Bitmap[3];
        mCheckActionList.addAll(mFaceConfig.getCheckActionList());
        handler = surfaceView.getHandler();
        // mCompared = new byte[algorithm.gethTpl().length];
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case DECODE:
                decodeArray((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case QUIT:
                running = false;
                timerCancel();
                Looper.myLooper().quit();
                break;
            case ONTIMER:
                onTimer();
                break;
        }
    }

    private void decodeArray(byte[] data, int width, int height) {
        if (data == null) {
            return;
        }
        Logs.i(TAG, "width:" + width + " height:" + height);

        faceResult = null;
        // 解析人脸获取解析数据

        //larry remove 20161115
        //Stfaceattr stfaceattr = decodeBitmap(data, width, height);
        int orientation = mSurfaceView.getCameraManager().getOrientation();

        Stfaceattr stfaceattr = FaceSDK.decodeBitmap(data, width, height, orientation);
        if (FaceSDK.mDecodeStatus >= 0 && isCheckFace(stfaceattr)) {
            if (mFaceConfig == null)
                return;
            isStartChecking = true;
            // 分发检活状态
            dispachState(stfaceattr);
            // 发送检测的图片
            sendBitmap();
        } else {
            Logs.i(TAG, "正在找脸！");
            Logs.e(TAG, "DECODE_FAILED...");
            setDecodeFaceError();
        }
    }

//  long normalDistanceTime = 0;
//  long distanceMinTime = 0;
//  long distanceMaxTime = 0;

    private void dispachState(Stfaceattr stfaceattr) {
        double distanceEyes = getEyeDistance(stfaceattr);
        Logs.i(TAG, "distanceEyes=" + distanceEyes + ",getDistanceEyesMax=" + mFaceConfig.getDistanceEyesMax() + ",getDistanceEyesMin=" + mFaceConfig.getDistanceEyesMin());
        String text = "";
        if (distanceEyes > mFaceConfig.getDistanceEyesMax()) {
//      normalDistanceTime = 0;
//      distanceMaxTime += oneFrameTime;
//      if (distanceMaxTime < 600)
//        return;

            text = "请向后远离一点";
            sleep(600);
        } else if (distanceEyes < mFaceConfig.getDistanceEyesMin()) {
//      normalDistanceTime = 0;
//      distanceMinTime += oneFrameTime;
//      if (distanceMinTime < 600)
//        return;

            text = "请向前靠近一点";
            sleep(600);
        } else {
//      distanceMaxTime = 0;
//      distanceMinTime = 0;
//      normalDistanceTime += oneFrameTime;
//      if (normalDistanceTime < 800) {
//        return;
//      }

            // 暂无信息，先不画框
            faceResult = new Result("FIND_FACE", null, stfaceattr);
            MessageManager.sendToTarget(handler, DECODE_SUCCEDED, faceResult);
            getFrontFace(stfaceattr);
        }
    }

    private double getEyeDistance(Stfaceattr stfaceattr) {
        int[][] eyes = stfaceattr.getLocEye();
        return Math.sqrt((eyes[1][0] - eyes[0][0]) * (eyes[1][0] - eyes[0][0])
                + (eyes[1][1] - eyes[0][1]) * (eyes[1][1] - eyes[0][1]));
    }

    private void sendBitmap() {
        if (faceResult != null) {
            if (!normalPicSuccess) {
                normalPicSuccess = false;
                Logs.e(TAG, "DECODE_FAILED(1)...");
                setDecodeFaceError();
                return;
            }
            normalPicSuccess = false;

            long getBitmapStart = System.currentTimeMillis();
            Logs.i(TAG, "发送照片...");

//      algorithm.DoRotate(faceRgb24, hWdHi, -1000);
//      final byte[] faceJpg = new byte[hWdHi[0] * hWdHi[1] * 3 + 1024];
//      algorithm.RgbToJpg(faceRgb24, hWdHi[0], hWdHi[1], faceJpg, 0, 0);
//      Bitmap faceBitmap = jpgToBitmap(faceJpg);
            //larry modify 20170322 解决比对没有分数问题
            Bitmap faceBitmap;
            if(com.YinanSoft.phoneface.Constants.DefaultAlgorithm == Constants.Algorithm.tesoface)
                faceBitmap = FaceSDK.prepareBitmap(com.techshino.tesoface.FaceSDK.faceRgb24, com.techshino.tesoface.FaceSDK.hWdHi);
            else if(com.YinanSoft.phoneface.Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2)
                faceBitmap = FaceSDK.prepareBitmap(com.smartshino.face.FaceSDK.faceRgb24, com.smartshino.face.FaceSDK.hWdHi);
            else
                faceBitmap = FaceSDK.prepareBitmap(com.face.sv.FaceSDK.faceRgb24, com.face.sv.FaceSDK.hWdHi);


            if (faceBitmap != null) {
                Logs.d(TAG, "获取到图片资源");
                Bundle bundle = new Bundle();
                bundle.putParcelable(DecodeThread.FACE_BITMAP,
                        faceBitmap);
                Message message = Message.obtain(handler, PHOTO_VERFY_SUCCESS);
                message.setData(bundle);
                message.sendToTarget();
                Logs.i(TAG, "setResult(2)...");
            } else {
                Logs.e(TAG, "DECODE_FAILED(3)...");
                setDecodeFaceError();
            }
            long getBitmapStop = System.currentTimeMillis();
            Logs.d(TAG, "转化Bitmap花费时间 " + (getBitmapStop - getBitmapStart)
                    + " ms");
        } else {
            Logs.e(TAG, "DECODE_FAILED(4)...");
            setDecodeFaceError();
        }
    }

//  private Bitmap jpgToBitmap(byte[] faceJpg) {
//    BitmapFactory.Options options = new BitmapFactory.Options();
//    options.inSampleSize = hWdHi[0] / 400;
//    Bitmap faceBitmap = BitmapFactory.decodeByteArray(
//        faceJpg, 0, faceJpg.length, options);
//    return faceBitmap;
//  }

    long frontFacePrepareTime = 0;

    /**
     * 获取正面照
     *
     * @param stfaceattr
     */
    private void getFrontFace(Stfaceattr stfaceattr) {
        frontFacePrepareTime += oneFrameTime;
//        if (frontFacePrepareTime < CHECK_INTERVAL_TIME)
//            return;

//        if (isCheckFace(stfaceattr)) {
        normalPicSuccess = true;
//        }
    }

    private boolean isCheckFace(Stfaceattr stfaceattr) {
        int mouthDegree = stfaceattr.getMouthDegree();
        int eyesDegree = stfaceattr.getEyeDegree();
        int turnedDegree = stfaceattr.getHeadPosi()[1];
        int nodDegree = stfaceattr.getHeadPosi()[2];
        Logs.d(TAG, "mouthDegree当前值======" + mouthDegree);
        Logs.d(TAG, "eyesDegree当前值======" + eyesDegree);
        Logs.d(TAG, "turnedDegree当前值======" + turnedDegree);
        Logs.d(TAG, "nodDegree当前值======" + nodDegree);

        if (mouthDegree < mFaceConfig.getFrontMouthDegree()
                && eyesDegree > mFaceConfig.getFrontEyeDegree()
                && turnedDegree <= mFaceConfig.getFrontTurnedMaxDegree()
                && turnedDegree >= mFaceConfig.getFrontTurnedMinDegree()
                && nodDegree >= mFaceConfig.getFrontNodMinDegree()
                && nodDegree <= mFaceConfig.getFrontNodMaxDegree()) {
            return true;
        }
        return false;
    }

    private void setDecodeFaceError() {
        if (handler != null) {
            MessageManager.sendToTarget(handler, DECODE_FAILED);
        }
    }

    /**
     * 启动沙漏
     */
    synchronized void onTimer() {
        Logs.i(TAG, "开始计时！");
        timerCancel();
        timerAsyncTask = new TimerAsyncTask();
        taskExec.execute(timerAsyncTask);
    }

    /**
     * 停止沙漏
     */
    private synchronized void timerCancel() {
        AsyncTask<?, ?, ?> task = timerAsyncTask;
        if (task != null) {
            Logs.e(TAG, "停止超时检测");
            task.cancel(true);
            timerAsyncTask = null;
        }
    }

    /**
     * @author James 简单沙漏，到了规定时间发送超时消息
     */
    private final class TimerAsyncTask extends
            AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            sleep(mFaceConfig.getTimeoutS() * 1000);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Logs.i(TAG, "已经等待" + mFaceConfig.getTimeoutS() + "秒捕捉超时");
            if (mSurfaceView.getHandler() != null) {
                Message message = Message.obtain(handler, CAMERA_TIME_OUT);
                message.sendToTarget();
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

}
