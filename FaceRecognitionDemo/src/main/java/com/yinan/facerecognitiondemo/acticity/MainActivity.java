package com.yinan.facerecognitiondemo.acticity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A606LReader;
import com.YinanSoft.CardReaders.CertImgDisposeUtils;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.Utils.FileUnits;
import com.YinanSoft.Utils.HttpDownloader;
import com.YinanSoft.Utils.ToastUtil;
import com.yinan.facerecognitiondemo.Global;
import com.yinan.facerecognitiondemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    @Bind(R.id.tvSAMID)
    TextView tvSAMID;
    @Bind(R.id.front_image)
    ImageView front_image;
    @Bind(R.id.img_photo)
    ImageView img_photo;
    @Bind(R.id.img_pic)
    ImageView img_pic;
    @Bind(R.id.result)
    TextView check_result;
//    @Bind(R.id.camera_r)
//    RadioGroup camera_r;

    boolean isDebug = false;

    byte[] bt2 = new byte[96];
    private IDCardReader idReader = null;
    private IDCardInfo idCardInfo;
    private Bitmap bitmap;
    private Bitmap bitmapF;
    private String sStatus = "";

    String path = "YinanSoft";
    String filename = "armidse.bin";
    String server_url = "http://www.mineki.cn/armidse.bin";
    //static public boolean initFingerSuccess = false;//指纹是否初始化成功
    static public boolean initReaderSuccess = false;//是否初始化成功
    private boolean isExitVaildFinger = false;
    private int comPareScore = 65;
    private int comPareScore_ = 75;

    private int cameraID = 0;
    private boolean isFirstTime = true;
    private com.techshino.fingerprint.FingerExt fingerExt = null;

    //测试图片放到这个路径下
    private final String dbgpath = "/sdcard/YinAnFace";
    private final String imgPath_ = "/sdcard/YinAnFace/face.jpg";//

    private SoundPool sp;
    private Map<Integer, Integer> mapSRC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.MODEL.toUpperCase().contains("JWZD-500") || Build.MODEL.toUpperCase().contains("JWZD-800")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        ButterKnife.bind(this);
        //初始化授权信息
        initVar();
        initSoundPool();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //初始化读卡和指纹
        initCardReaderAndFingerPrinter();
    }

    @Override
    protected void onStop() {
        isExitVaildFinger = true;
        releaseCardReader(true);
        releaseFingerPrinter(true);
        super.onStop();
    }

    @OnClick(R.id.btn_readSAMID)
    public void readSAMID() {
        if (idReader == null) return;
        String[] sRet = new String[1];
        tvSAMID.setText("模块号:" + idReader.ReadSAMID(sRet).trim());
        isDebug = !isDebug;
    }

    @OnClick(R.id.btn_readIDCard)
    public void readIDCardd() {
        if (idReader == null) return;
        readIDCard();
    }

    @OnClick(R.id.btn_readFinger)
    public void btn_readFinger() {
        validFingerInfo();
    }

    private boolean isClicked = false;

    private int clicks = 1;

    String sFirstRecogTime;

    int nDebugType = 1;


    private void saveBitmap(Bitmap orcBitmap, String pPath) {
        if (orcBitmap == null) return;
        Log.i(TAG, "保存图像....................................  ");
        File file = new File(dbgpath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
//            dbgdir.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(pPath);
            orcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //读卡
    private void readIDCard() {
        sStatus = "";
        if (!initReaderSuccess) {
            ToastUtil.showToast(MainActivity.this, "读卡器初始化失败");
            return;
        }
        idCardInfo = null;
        img_pic.setImageBitmap(null);
        img_photo.setImageBitmap(null);
        handler.sendEmptyMessage(6);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.MAX_VALUE;
                long start = System.currentTimeMillis();

                try {
                    while (idCardInfo == null) {

                        idCardInfo = idReader.ReadAllCardInfo(new String[1]);

                        if (idCardInfo != null) {
                            handler.sendEmptyMessage(4);
                            isFirstTime = true;
                            clicks = 0;
                            sStatus += "读卡成功\n";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    img_pic.setImageBitmap(null);
                                    img_photo.setImageBitmap(idCardInfo.getPhoto());
                                    try {
                                        if (bitmap != null && !bitmap.isRecycled()) {
                                            bitmap.recycle();
                                            bitmap = null;
                                        }
                                        bitmap = new CertImgDisposeUtils(MainActivity.this).creatBitmap(idCardInfo);
                                        if (bitmap != null) {
                                            front_image.setImageBitmap(bitmap);
                                        }
                                        saveBitmap(idCardInfo.getPhoto(), imgPath_);
                                        check_result.setText(sStatus);
                                        check_result.setTextSize(16f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else if (System.currentTimeMillis() - start >= time) {//超时
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sStatus += "请将身份证放在感应区域";
                                    check_result.setText(sStatus);
                                    check_result.setTextSize(16f);
                                    //ToastUtil.showToast(MainActivity.this, sStatus);
                                }
                            });
                            break;
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }


    //指纹比对
    private void validFingerInfo() {

        //指纹比对
        if (Global.initFingerSuccess) {
            if (idCardInfo == null) {
                ToastUtil.showToast(MainActivity.this, "请先读卡");
                return;
            }
            if (idCardInfo.getFingerInfo() != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.MODEL.toUpperCase().equals("P3")) {
                        } else {
                            validFingerTC();
                        }
                    }
                }).start();
            } else {
                ToastUtil.showToast(MainActivity.this, "该身份证没有指纹信息");
            }
        } else {
            ToastUtil.showToast(MainActivity.this, "指纹初始化失败");
        }
    }

    private int validFingerTC() {
        bitmapF = null;
        isExitVaildFinger = false;
        try {
            int results = 0;
            int time = 10000;
            long start = System.currentTimeMillis();
            sStatus += "请按指纹\n";
            handler.sendEmptyMessage(0);
            while (!isExitVaildFinger) {
                //采集指纹图像和特征值
                fingerExt.fingerprint.featureBuffer = new byte[300];
                fingerExt.fingerprint.featureBuffer0x30 = new byte[513];
                fingerExt.fingerprint.nResult = fingerExt.fingerprint.FP_FeatureAndTESOImageExtractAll(1,
                        fingerExt.fingerprint.featureBufferHex,
                        fingerExt.fingerprint.featureBuffer0x30,
                        fingerExt.fingerprint.featureBuffer,
                        fingerExt.fingerprint.imageBuffer,
                        fingerExt.fingerprint.TESOimageBuffer,
                        fingerExt.fingerprint.ImageAttr);
                if (fingerExt.fingerprint.nResult >= 0) {
                    handler.sendEmptyMessage(3);
                    Log.e("app", "ImageAttr[2] = " + fingerExt.fingerprint.ImageAttr[2]);
                    fingerExt.fingerprint.imgSize = fingerExt.fingerprint.ImageAttr[0] * fingerExt.fingerprint.ImageAttr[1] + 1024 + 54;
                    bitmapF = BitmapFactory.decodeByteArray(fingerExt.fingerprint.imageBuffer, 0, fingerExt.fingerprint.imgSize);


                    fingerExt.fingerprint.FP_Beep();
                    System.arraycopy(fingerExt.fingerprint.featureBuffer, 0,
                            fingerExt.featureBuffer0, 0,
                            fingerExt.fingerprint.featureBuffer.length);
                    float[] score = new float[]{0};
                    byte[] f1 = new byte[512];
                    byte[] f2 = new byte[512];
                    System.arraycopy(idCardInfo.getFingerInfo(), 0, f1, 0, 512);
                    System.arraycopy(idCardInfo.getFingerInfo(), 512, f2, 0, 512);
                    //特征值比对
                    int result = fingerExt.fingerprint.FP_FeatureMatch(fingerExt.featureBuffer0,
                            f1, score);
                    if (result >= 0 && score[0] <= 0)
                        result = fingerExt.fingerprint.FP_FeatureMatch(fingerExt.featureBuffer0,
                                f2, score);
                    //比对结果处理
                    if (result >= 0) {
                        if (score[0] > 0) {
                            sStatus += "指纹比对成功\n";
                            handler.sendEmptyMessage(1);
                        } else {
                            sStatus += "指纹比对失败\n";
                            handler.sendEmptyMessage(2);
                        }
                        break;
                    } else {
                        continue;
                    }
                } else if (System.currentTimeMillis() - start >= time) {
                    //超时
                    sStatus += "指纹比对超时\n";
                    handler.sendEmptyMessage(2);
                    break;
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "指纹返回：" + e.getMessage());
        }
        return 0;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://请捺印指纹提示
                    playSound(Global.SoundIndex.fingerdown, 0);
                    break;
                case 1://显示比对结果成功
                    playSound(Global.SoundIndex.validok, 0);
                    img_pic.setImageBitmap(bitmapF);
                    check_result.setText(sStatus);
                    isExitVaildFinger = true;//停止指纹线程
                    break;
                case 2://显示比对结果失败
                    playSound(Global.SoundIndex.validfail, 0);
                    img_pic.setImageBitmap(bitmapF);
                    check_result.setText(sStatus);
                    isExitVaildFinger = true;//停止指纹线程
                    break;
                case 3://请抬起手指
                    playSound(Global.SoundIndex.fingerup, 0);
                    break;
                case 4://读证成功
                    playSound(Global.SoundIndex.di, 0);
                    break;
                case 6://读卡

                    playSound(Global.SoundIndex.readidcard, 0);
                    break;
                default:
                    break;
            }
        }
    };


    private void initCardReaderAndFingerPrinter() {
        //final long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCardReader();
                initFingerPrinter();
            }
        }).start();
    }

    private void initCardReader() {
        if (idReader == null) {
            idReader = new A606LReader(MainActivity.this);
        }
        idReader.PowerOnReader();
        initReaderSuccess = idReader.InitReader(null);

    }


    public void releaseCardReader(boolean poweroff) {
        if (idReader != null) {
            if (poweroff) idReader.PowerOffReader();
            idReader.ReleaseReader();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ToastUtil.showToast(MainActivity.this, "releaseCardReader");
//                }
//            });
        }
    }

    private void initFingerPrinter() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtil.showToast(MainActivity.this, "initFingerPrinter");
//            }
//        });

        if (Build.MODEL.toUpperCase().equals("P3")) {
        } else if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
            com.techshino.fingerprint.FingerExt.PowerOnFinger606();
            if (fingerExt == null)
                fingerExt = new com.techshino.fingerprint.FingerExt(MainActivity.this);
            fingerExt.initUsbFinger();
        } else if (Build.MODEL.toUpperCase().equals("JWZD-606A") || Build.MODEL.equals("wisky8783_tb_l1")) {
            com.techshino.fingerprint.FingerExt.PowerOnFinger606A();
            if (fingerExt == null)
                fingerExt = new com.techshino.fingerprint.FingerExt(MainActivity.this);
            fingerExt.initUsbFinger();
        }
    }

    public void releaseFingerPrinter(boolean poweroff) {
        isExitVaildFinger = true;
        if (Build.MODEL.toUpperCase().equals("JWZD-606")) { //606 and 800
            if (fingerExt != null) {
                fingerExt.UsbFingerClose();
            }
            if (poweroff) com.techshino.fingerprint.FingerExt.PowerOffFinger606();
        } else if (Build.MODEL.toUpperCase().equals("P3")) {
        } else if (Build.MODEL.toUpperCase().equals("JWZD-606A") || Build.MODEL.equals("wisky8783_tb_l1")) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ToastUtil.showToast(MainActivity.this, "releaseFingerPrinter");
//                }
//            });
            if (fingerExt != null) {
                fingerExt.UsbFingerClose();
            }
            if (poweroff) com.techshino.fingerprint.FingerExt.PowerOffFinger606A();//606A指纹下电
        }
    }

    private void initVar() {
        //初始化授权信息
        initLicFile();

        FileUnits units = new FileUnits();
        //直接读卡文件
        bt2 = units.readSDFile(path, filename);
        if (bt2 == null) {
            Toast.makeText(MainActivity.this, "授权文件读取失败，请保证联网更新授权，或者重新安装本应用", Toast.LENGTH_SHORT).show();
        }

    }

    //加载本地授权码
    private void initLicFile() {
        SharedPreferences settings = getSharedPreferences("com.YinanSoft.www",
                MODE_PRIVATE);
        String arm = settings.getString("armidse", "-1");
        if (arm.equals("-1")) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("armidse", "1");
            editor.commit();
            try {
                InputStream is = this.getResources().openRawResource(
                        R.raw.armidse);
                FileUnits unit = new FileUnits();
                unit.writeToSDfromInput(path, filename, is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //联网更新授权
    private void updateBin() {
        if (HttpDownloader.isConnect(this)) {
            ToastUtil.showToast(MainActivity.this, "开始更新授权文件");
            Runnable down = new Runnable() {
                @Override
                public void run() {
                    int rs = HttpDownloader.downFile(server_url, path, filename);
                    if (rs == 1) {
                        ToastUtil.showToast(MainActivity.this, "授权文件更新成功");
                    } else {
                        ToastUtil.showToast(MainActivity.this, "授权文件更新失败，请检查下网络");
                    }
                }
            };
            handler.post(down);
        } else {
            ToastUtil.showToast(MainActivity.this, "该功能必须联网使用");
        }
    }

    private void initSoundPool() {
        //初始化声音池
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mapSRC = new HashMap<Integer, Integer>();
        mapSRC.put(1, sp.load(this, R.raw.di, 0));
        mapSRC.put(2, sp.load(this, R.raw.readidcard, 0));
        mapSRC.put(3, sp.load(this, R.raw.fingerdown, 0));

        mapSRC.put(4, sp.load(this, R.raw.fingerup, 0));
        mapSRC.put(5, sp.load(this, R.raw.validface, 0));
        mapSRC.put(6, sp.load(this, R.raw.validok, 0));
        mapSRC.put(7, sp.load(this, R.raw.validfail, 0));
        mapSRC.put(8, sp.load(this, R.raw.blacklist, 0));
    }

    private void playSound(Global.SoundIndex index, int number) {
        sp.play(mapSRC.get(index.ordinal()), 1.0f, 1.0f, 0, number, 1.0f);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);

//        releaseCardReader(true);// 释放读卡
//        releaseFingerPrinter(true);//释放指纹
        super.onDestroy();
    }
}
