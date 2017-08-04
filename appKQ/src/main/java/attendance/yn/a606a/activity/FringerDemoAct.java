package attendance.yn.a606a.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.YinanSoft.Utils.ToastUtil;
import com.android.fpcomm.FPHWInfoData;
import com.finger.FingerPrintManager;
import com.finger.OnSdkStatusListener;
import com.fingersdk.process.FingerPrintInterface;
import com.fingersdk.process.FpStatusCode;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;

public class FringerDemoAct extends Activity {
    byte[] FPImageData = new byte[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fringer_demo);
    }


    public void onOpenFringerClick(View v) {
        opendevice(this);
    }

    public void onGetFringerClick(View v) {
        if (MyApplication.idCardInfo == null) {
            ToastUtil.showToast(this, "请先读取身份证!");
            return;
        }
        Thread thread = new Thread(grab);
        thread.start();
    }

    public void onSaveFringerClick(View v) {
        int state = FpIf.PTStoreFinger(MyApplication.idCardInfo.getFingerInfo(), mFPID);
        if (state == FpStatusCode.PT_STATUS_OK) {
            ToastUtil.showToast(this, "存入成功!");
        }
    }

    public void onVerifyAllFringerClick(View v) {
        Thread thread_verifyall = new Thread(verifyall);
        thread_verifyall.start();
    }


    private static final String TAG = "FringerDemoAct";
    // 是否打开
    private static boolean isOpen = false;
    private static int status = 0;

    private static FingerPrintManager mPrintManager = null;
    public static FingerPrintInterface FpIf = null;

    public void opendevice(Context context) {
        if (!isOpen) {
            mPrintManager = FingerPrintManager.getInstance(context);
            // 设置监听--进行状态回调
            mPrintManager.setOnSdkStatusListener(new OnSdkStatusListener() {
                @Override
                public void OnHALCallback(int i, int i1) {
                    Log.e(TAG, "OnHALCallback-- dwGuiState=" + i + " byProgress:" + i1);
                }
            });
            // 指纹操作接口
            FpIf = mPrintManager;

            // 初始化 选择设备类型 和消息的句柄
            FpIf.PTInitialize(FPHWInfoData.TCS1_508_SENSOR_TYPE);
            Toast.makeText(context, "正在打开设备...", Toast.LENGTH_SHORT).show();
            Thread openThread = new Thread(openRunnable);
            openThread.start();
        } else {
            Toast.makeText(context, "设备已经打开!", Toast.LENGTH_SHORT).show();
        }

    }

    private Runnable openRunnable = new Runnable() {
        String id_key = "cb6cb0-2021dbd-5476000-584a235-43ade4d";

        @Override
        public void run() {
            Log.e(TAG, "正在打开设备...");
            status = FpIf.PTOpen(id_key, 1, 1);
            if (status == FpStatusCode.PT_STATUS_OK) {
                handler.sendEmptyMessage(1);
                Log.e(TAG, "打开设备成功...");
            } else {
                handler.sendEmptyMessage(2);
                Log.e(TAG, "打开设备失败...");
            }
        }
    };


    Runnable grab = new Runnable() {

        public void run() {
            String msg = null;
            int status = -1;
            Log.e(TAG, "等待采集中...");
            Log.e(TAG, "开始执行grab imager:请采集指纹...");
            msg = "开始执行grab imager:请采集指纹...";
            byte[] fingerInfo = MyApplication.idCardInfo.getFingerInfo();
//            FpIf.PTGrab(FPImageData, -1, 1);
//            int aaaa = FpIf.PTMatch(fingerInfo, FPImageData);
//            if (aaaa == FpStatusCode.PT_STATUS_OK) {
//                handler.sendEmptyMessage(5);
//            } else {
//                handler.sendEmptyMessage(6);
//            }

            int i = 0;
            while (i < 15) {
//                status = FpIf.PTGrab(FPImageData, -1, 1);  //BUG  提示 通道损坏
                status = FpIf.PTCapture(FPImageData, -1);// 图片的 采集
//                try {
//                    Log.e(TAG, "模板数据" + new String(fingerInfo, "utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                if (status == FpStatusCode.PT_STATUS_OK) {
                    int aaaa = FpIf.PTMatch(fingerInfo, FPImageData);  //比对15次时  通过
                    if (aaaa == FpStatusCode.PT_STATUS_OK) {
                        Log.e(TAG, "比对成功...i=" + i);
                        handler.sendEmptyMessage(5);
                        return;
                    } else {

                    }
//                    handler.sendEmptyMessage(3);
                    Log.e(TAG, "指纹图像采集完毕...");
                    i++;
                } else {
                    handler.sendEmptyMessage(4);
                    Log.e(TAG, "指纹图像采集 错误 err:" + FpStatusCode.getMessage(status));
                    i += 15;
                }
//                if (i >= 15) {
//                    handler.sendEmptyMessage(6);
//                }
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(FringerDemoAct.this, "设备打开成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(FringerDemoAct.this, "设备打开失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(FringerDemoAct.this, "指纹获取成功", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(FringerDemoAct.this, "指纹获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(FringerDemoAct.this, "比对成功", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(FringerDemoAct.this, "比对超时/失败", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    String result = (String) msg.obj;
                    Toast.makeText(FringerDemoAct.this, "N比对结果:" + result, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 传出的指纹ID
     */
    private int[] mFPID = new int[4];
    public int timeout = -1;// -1=30s -2 永久
    /*
     * verifyall thread
	 */
    Runnable verifyall = new Runnable() {

        public void run() {
            String msg = null;
            System.out.println("Runable->" + Thread.currentThread().getId());
            msg = "开始执行verifyall:请采集指纹...";
            status = FpIf.PTVerifyAll(mFPID, timeout);
            Log.e(TAG, "N对比:" + status);
            if (status == FpStatusCode.PT_STATUS_OK) {
                msg = "比对结束，" + "指纹库所对应的 ID 是:" + mFPID[0];


            } else if (status == FpStatusCode.PT_STATUS_NOT_MATCH) {
                msg = "比对结束：指纹库没有你的指纹";
            } else {
            }
            Message mesg = handler.obtainMessage();
            mesg.obj = msg;
            handler.sendMessage(mesg);
            handler.sendEmptyMessage(7);
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FpIf.PTClose();
//            }
//        }).start();
    }
}
