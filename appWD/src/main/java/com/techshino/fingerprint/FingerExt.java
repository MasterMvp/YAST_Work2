package com.techshino.fingerprint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.YinanSoft.phoneface.common.Logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import risks.yn.a606a.MyApplication;


public class FingerExt {

    private static final String TAG = FingerExt.class.getSimpleName();

    public static boolean Current_MonitorMode = false;

    public final static int CMD_ExtractFeatureAndImage = 0;
    public final static int CMD_ExtractTemplet = 1;
    public final static int CMD_ExtractFeatureMatch = 2;
    public final static int CMD_OutofTime = 4;

    public static byte[] featureBuffer0 = new byte[513];
    public static byte[] featureBuffer1 = new byte[513];
    public static byte[] featureBuffer2 = new byte[513];
    public static byte[] TempletBuffer = new byte[513];


    public boolean isExit = false;
    public boolean isReady = true; // for regist
    public boolean isRegisted = false;
    public boolean isVersionget = false;
    public boolean isFoundDevice = false;

    public Fingerprint fingerprint = new Fingerprint();
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private final NotifyCb ncb = new NotifyCb();

    byte[] mFingerByte = null;
    Bitmap mFingerImg2 = null;
    String mFeatures;

    private Context mContext = null;

    public FingerExt(Context c) {
        mContext = c;
    }


    static void GPIOOutputHigh(String gpio) {
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/devices/platform/leds-gpio/leds/" + gpio + "/brightness"));
            localFileWriter.write("1");
            localFileWriter.close();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    static void GPIOOutputLow(String gpio) {
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/devices/platform/leds-gpio/leds/" + gpio + "/brightness"));
            localFileWriter.write("0");
            localFileWriter.close();

        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    static public void PowerOnReader() {
        GPIOOutputHigh("out1");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static public void PowerOffReader() {
        GPIOOutputLow("out1");
    }

    public static int SetPacket(Context context, String str) {
        Intent testIntent = new Intent("com.android.USB.CANCEL_DIALOG_ACTION");
        testIntent.putExtra("AuthoryPackageName", str);
        context.sendBroadcast(testIntent);
        return 0;
    }


    public void validFinger(byte[] feature) {
        mFingerByte = feature;
        fingerprint.cmd = fingerprint.CMD_ExtractFeatureMatch;
        initUsbDevice();
    }


    public void initUsbFinger() {

        if (fingerprint.root_version) {
            Log.e("handleMessage", "fingerprint.root_version");
            // upgradeRootPermission(getPackageCodePath());
            // upgradeown(getPackageCodePath());
            tesoutil.upgradedirPermission("/dev/bus/usb");
            Log.e("handleMessage", "fingerprint.root_version out ");

        }
        fingerprint.cmd = fingerprint.init_only;
        initUsbDevice();
        fingerprint.RegisterCallBack(ncb);
    }

    public void UsbFingerClose() {
        fingerprint.FP_Close();
    }

    void initUsbDevice() {
        Log.i("techsino fingerprint", "initUsbDevice in ");

        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devlist = usbManager.getDeviceList();
        Iterator<UsbDevice> devs = devlist.values().iterator();

        // Log.i( null, "usb设备：" + ((UsbDevice) devs).getVendorId());
        Log.i(null, "usb设备：" + String.valueOf(devlist.size()));
        while (devs.hasNext() && !fingerprint.root_version) {
            UsbDevice d = devs.next();

            if (d.getVendorId() == fingerprint.VID) {
                usbDevice = d;
                isFoundDevice = true;
                if (!usbManager.hasPermission(usbDevice)) {// 没有访问权限。
                    PendingIntent pi = PendingIntent
                            .getBroadcast(mContext, 0, new Intent("com.techshino.usbpermission"), 0);
                    mContext.registerReceiver(new PermissionReceiver(), new IntentFilter("com.techshino.usbpermission"));
                    usbManager.requestPermission(usbDevice, pi); // 弹出对话框，申请权限。
                } else {

                    UsbDeviceConnection usbConnection = usbManager.openDevice(usbDevice);
                    if (usbConnection != null) {
                        final int fd = usbConnection.getFileDescriptor();
                        Log.i("techsino fingerprint", "2File Descriptor : " + fd);
                        if (fd > 0) {

//              new Thread(new Runnable() {
//
//                @Override
//                public void run() {
                            isExit = false;
                            if (fingerprint.LIVESCAN_Init(fd) == 1) {
                                MyApplication.initFingerSuccess = true;
                                myHanlder.sendEmptyMessage(100);
                            } else {
                                MyApplication.initFingerSuccess = false;
                                myHanlder.sendEmptyMessage(-100);
                            }

//                }
//              }).start();
                        }
                        return;
                    }
                }

            }
        }
        Log.i("techsino fingerprint", "devs.hasNext() =" + devs.hasNext());
        while (devs.hasNext() && fingerprint.root_version) {
            UsbDevice d = devs.next();
            Log.i("techsino fingerprint", "d.getVendorId() =" + d.getVendorId());
//      Toast.makeText(FingerExt.this, "d.getVendorId() =" + d.getVendorId(), Toast.LENGTH_SHORT).show();
            if (d.getVendorId() == fingerprint.VID) {
                isFoundDevice = true;
//        new Thread(new Runnable() {

//          @Override
//          public void run() {
                isExit = false;
                if (fingerprint.LIVESCAN_Init1(0) == 1) {
                    MyApplication.initFingerSuccess = true;
                    myHanlder.sendEmptyMessage(100);
                } else {
                    MyApplication.initFingerSuccess = false;
                    myHanlder.sendEmptyMessage(-100);
                }
//          }
//        }).start();
                return;
            }
        }
//    btnExtractFeatureAndImage.setEnabled(false);
//    btnRegisterTemplet.setEnabled(false);
//    btnCompareFeature.setEnabled(false);
        isFoundDevice = false;
        Log.i("techsino fingerprint", "未检测到指纹设备");
//    mFingerInfoTv.setText("未检测到指纹设备");
    }

    private class PermissionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mContext.unregisterReceiver(this);
            if (intent.getAction().equals("com.techshino.usbpermission") && !fingerprint.root_version) {
                if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//          mFingerInfoTv.setText("用户拒绝该程序访问Teso设备！");
                    Log.i("techsino fingerprint", "用户拒绝该程序访问Teso设备！");
                } else {
//					needrequestagain
//          mFingerInfoTv.setText("用户授权该程序访问Teso设备！");
                    // try {
                    // Thread.sleep(1000);
                    // } catch (InterruptedException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }
                    Log.i("techsino fingerprint", "用户授权该程序访问Teso设备！");
                    initUsbDevice();
                }
            }

        }
    }

    private final class NotifyCb implements Fingerprint.NotifyInterface {

        public void CallBackFun(int arg) {
            Logs.i(TAG, "CallBackFun:" + arg);
            myHanlder.sendEmptyMessage(arg);
        }
    }

    private Handler myHanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "current_PID is " +
                    String.valueOf(fingerprint.current_PID) + " what:" + msg.what);

            Log.i(TAG, "fingerprint.imgSize =  " +
                    fingerprint.imgSize);
            if (msg.what == 100) {
                success();
            } else if (msg.what == -100) {
                error();
            }

            setFingerBitmap();

            switchResult(msg.what);
        }
    };

    private void success() {
        if (fingerprint.cmd == fingerprint.CMD_ExtractFeatureAndImage) {
            Logs.i(TAG, "指纹监控...");
            Current_MonitorMode = true;
            isExit = false;
            isReady = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fingerprint.ExtractFeatureAndImage();
                }
            }).start();
        } else if (fingerprint.cmd == fingerprint.CMD_ExtractFeatureMatch) {
            Logs.i(TAG, "指纹比对...");
            isReady = true;
            isExit = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fingerprint.ExtractFeatureMatch();
                }
            }).start();
        }
        initSuccess();
    }

    private void error() {

//    mFingerInfoTv.setText("初始化结束");

    }

    private void switchResult(int what) {
        switch (fingerprint.nResult) {
            case -5: {
                switch (what) {
                    case CMD_OutofTime:
//            mFingerInfoTv.setText("采集指纹超时 ");
                        break;
                    case CMD_ExtractFeatureAndImage:
//            mFingerInfoTv.setText("请按指纹 !");
                        if (isExit != true)
                            new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    fingerprint.ExtractFeatureAndImage();
                                }
                            }).start();
                        break;
                    case CMD_ExtractTemplet:
//            mFingerInfoTv.setText("正在注册指纹");
                        isReady = true;
                        if (isExit == false) {
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    fingerprint.ExtractTemplet();
                                }
                            }).start();
                        }

                        break;
                    case CMD_ExtractFeatureMatch:
//            mFingerInfoTv.setText("未采集到指纹 ");
//            mFingerScoreTv.setText("比对");
                        break;
                }
            }
            break;
            default: {
                if (what == CMD_ExtractFeatureMatch) { // templet
                    int result = 0;
                    if (mFingerByte == null) {
//            ToastUtil.showToast(this, "身份证中无指纹信息！");
                        return;
                    }

                    if (fingerprint.nResult >= 0) {
                        fingerprint.FP_Beep();
                        System.arraycopy(fingerprint.featureBuffer, 0,
                                featureBuffer0, 0,
                                fingerprint.featureBuffer.length);
                        float[] score = new float[]{0};
                        result = fingerprint.FP_FeatureMatch(featureBuffer0,
                                mFingerByte, score);

                        if (result >= 0) {
                            if (score[0] > 0) {
//                mFingerInfoTv.setText("匹配,得分 " + String.valueOf(score[0]));
//                mFingerScoreTv.setText("匹配");
                            } else {
//                mFingerInfoTv.setText("不匹配 ，得分 " + String.valueOf(score[0]));
//                mFingerScoreTv.setText("不匹配");
                            }
                        } else {
//              mFingerInfoTv.setText("注册失败 ，错误码  " + String.valueOf(result));
//              mFingerScoreTv.setText("比对");
                        }
                    } else {
//            mFingerInfoTv.setText("未采集到指纹 ");
                    }
                    break;
                }
            }
        }

    }

    private void setFingerBitmap() {
        if (fingerprint.imgSize != 0) {
            mFingerImg2 = BitmapFactory.decodeByteArray(
                    fingerprint.imageBuffer, 0, fingerprint.imgSize);
            fingerprint.imgSize = 0;
        }
    }

    private void initSuccess() {

//    mFingerInfoTv.setText("指纹设备初始化成功！");
    }

    /**
     * 606A补光灯上电方案
     */

    static public void PowerOnFlash606A() {
        //606A
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/class/misc/mtgpio/pin"));
            localFileWriter.write("-wdout78 1");
            localFileWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }

    }

    static public void PowerOffFlash606A() {
        //606A
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/class/misc/mtgpio/pin"));
            localFileWriter.write("-wdout78 0");
            localFileWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }

    }


    /**
     * 606A指纹上电方案
     */

    static public void PowerOnFinger606A() {
        //606A
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/class/misc/mtgpio/pin"));
            localFileWriter.write("-wdout61 1");
            localFileWriter.close();
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }

    }


    static public void PowerOffFinger606A() {
        //606A
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/class/misc/mtgpio/pin"));
            localFileWriter.write("-wdout61 0");
            localFileWriter.close();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}
