package attendance.yn.a606a.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.Police.Base64;
import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.util.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;
import attendance.yn.a606a.dialog.CustomDialog;
import attendance.yn.a606a.utils.CertImgDisposeUtils;
import attendance.yn.a606a.utils.DateUtil;
import attendance.yn.a606a.utils.SoundPoolAudioClip;
import attendance.yn.a606a.bean.UserBean;
import attendance.yn.a606a.utils.ToastUtils;

/**
 * Created by Administrator on 2017/4/17.
 */

public class AddUserAct extends Activity {

    public static final String ARG_FEATURES = FaceTestActivity.class.getSimpleName() + ".feature";
    private Vibrator vibrator;
    public static final String TAG = "AddUserAct";

    private ImageView img;
    private IDCardInfo info = null;
    private UserBean userBean;
    private boolean isExitVaildFinger = false;
    private String sStatus = "";
    private int threadStat = 0;
    private String mFeatures = "";
    private ImageView imgLeft;
    private ImageView imgRight;
    private TextView check_result;
    private Bitmap bitmapFinger = null;
    private Bitmap blackBitmap;
    private Bitmap colorBitmap;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.look);//请目视摄像头
                    break;
                case 1://更新状态
                    check_result.setText(sStatus);
                    imgRight.setImageBitmap(bitmapFinger);
                    break;
                case 2:
                    break;
                case 3:
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.fingerdown);
                    isExitVaildFinger = false;
                    break;
                case 4://指纹成功，走人脸
                    isExitVaildFinger = true;
                    //        人脸比对
                    if (MyApplication.initFaceDeleSuccess) {
                        gotoFaceDete1(MyApplication.idCardInfo);
                        return;
                    }
//                    sp.play(SoundPoolAudioClip.SoundIndex.optionsuccess);
//                    insertUser(true);
                    break;
                case 5://指纹识别失败或人脸识别失败，录入失败
                    threadStat = 0;
                    isExitVaildFinger = true;
                    sStatus += "录入失败";
                    check_result.setText(sStatus);
                    ToastUtil.showToast(AddUserAct.this, "录入失败。");
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.validfail);
                    break;
                case 6://录入成功
                    threadStat = 0;
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.optionsuccess);
                    break;
                case 7:
                    String response = (String) msg.obj;

                    try {
                        JSONArray jsonArray = JSONUtils.getJSONArray(response, "dataList", null);
                        Log.e(TAG, "返回信息：" + jsonArray.toString());
                        StringBuffer sb = new StringBuffer();
                        if (jsonArray != null) {
                            String behavior = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj1 = jsonArray.getJSONObject(i);
                                behavior = JSONUtils.getString(obj1, "behavior", "");
                                String time = JSONUtils.getString(obj1, "time", "");
                                sb.append(time + ":" + behavior + "\n");
                            }
                            Log.e(TAG, "返回信息1：" + sb.toString());
                            if (!"[]".equals(jsonArray.toString())) {
                                MyApplication.idCardInfo.setpType(sb.toString());
                                sStatus += "联网核查异常\n";
                            } else {
                                sStatus += "联网核查正常\n";
                            }
                            check_result.setText(sStatus);
//                            ToastUtil.showToast(AddUserAct.this, "返回结果：" + sStatus);

//                            if (MyApplication.initFaceDeleSuccess) {
//                                gotoFaceDete1(MyApplication.idCardInfo);
//                                return;
//                            }
                            insertUser();
                        } else {
                            String errorMsg = JSONUtils.getString(response, "errorMsg", "");
                            ToastUtil.showToast(AddUserAct.this, errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        setContentView(R.layout.act_add);
        img = (ImageView) findViewById(R.id.adduser_img);
        imgLeft = (ImageView) findViewById(R.id.img_left);
        imgRight = (ImageView) findViewById(R.id.img_right);
        check_result = (TextView) findViewById(R.id.result);
//      initCardReader();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (Main2Activity.fingerExt == null)
//                    Main2Activity.fingerExt = new FingerExt(AddUserAct.this);
//                initCardReader();
////                initFingerPrinter();
//            }
//        }).start();

        handler.postDelayed(runableReadIDCard, 1000);
    }

    public void onClick(View v) {
//        try {
//            Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.faceimage);
//            FaceSDK.init(this);
//            String feature = FaceSDK.getFeature(bit);
//            Log.e(TAG, "特征值:" + feature);
//        } catch (Exception e) {
//            Log.e(TAG, "出异常了:" + e.toString());
//            e.printStackTrace();
//        }


//        Intent intent = new Intent(this, TestActivity.class);
//        startActivity(intent);
        //中天一维指纹调试
//        Intent intent = new Intent(this, FringerDemoAct.class);
//        startActivity(intent);
        //正常逻辑
        showPasswordrDialog();

        //物理ID
//        String[] str = new String[1];
//        Main2Activity.idReader.SendAndRecvNEW("0200033229FFE403", str, 500);
//        ToastUtil.showToast(this, "物理ID" + str[0]);
//        Log.e(TAG, "物理ID" + str[0]);

//        Intent intent = new Intent(this, HandlerCheckAct.class);
//        startActivity(intent);
//        TextView tv = (TextView) findViewById(R.id.tv);
//        List<UserBean> list = dbManager.selectUser();
//        StringBuffer sb = new StringBuffer();
//        for (UserBean user : list) {
//            sb.append("_id=" + user.getId() + ",name=" + user.getName() + ",cardNum=" + user.getCardNum() + "\n");
//        }
//        tv.setText(sb.toString());

    }

    private Runnable runableReadIDCard = new Runnable() {
        @Override
        public void run() {
            if (threadStat == 0) {
                msgReader();
                handler.postDelayed(runableReadIDCard, 100);
            }
        }
    };

    /**
     * 获取录入信息
     */
    public void msgReader() {
        com.sunxi.hw.util.GPIOOutputHigh("out3");
//        ToastUtil.showToast(AddUserAct.this, "请放置身份证");
        if (Main2Activity.idReader == null) {
            com.sunxi.hw.util.GPIOOutputLow("out3");
            return;
        }
        info = Main2Activity.idReader.ReadAllCardInfo(new String[1]);
        if (info == null) {
            com.sunxi.hw.util.GPIOOutputLow("out3");
//            ToastUtil.showToast(AddUserAct.this, "未读取到身份证信息。");
            return;
        }
        vibrator.vibrate(500);//蜂鸣器
        bitmapFinger = null;
        sStatus = "";
        sStatus = "读卡成功\n";
        handler.sendEmptyMessage(1);
        MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.di);
        try {
            Bitmap bit = new CertImgDisposeUtils(AddUserAct.this).creatBitmap(info);
            img.setImageBitmap(bit);
            imgLeft.setImageBitmap(info.getPhoto());
        } catch (IOException e) {
            com.sunxi.hw.util.GPIOOutputLow("out3");
            e.printStackTrace();
        }
//        info.setCardNum("511124197112206628");
        MyApplication.idCardInfo = info;

//        //联网核查
//        searchInfo(MyApplication.idCardInfo);

        //指纹比对
        if (info.getFingerInfo() != null && MyApplication.initFingerSuccess) {
            threadStat = 1;//关闭读卡
            com.sunxi.hw.util.GPIOOutputLow("out3");
            new Thread(runableValidFinger).start();
            return;
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(AddUserAct.this, "指纹初始化失败。");
                }
            });

        }
        com.sunxi.hw.util.GPIOOutputLow("out3");
//        人脸比对
        if (MyApplication.initFaceDeleSuccess) {
            gotoFaceDete1(MyApplication.idCardInfo);
            com.sunxi.hw.util.GPIOOutputLow("out3");
            return;
        }
        insertUser();

    }

    private void searchInfo(IDCardInfo idCardInfo) {
        if (idCardInfo == null) {
            ToastUtil.showToast(AddUserAct.this, "请先读取身份证");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "请求中", "请稍后...", true, false);
        final String uri = "http://182.254.138.33/query/queryById.do?id=" + idCardInfo.getCardNum();
//        final String uri = "http://www.kdzxpt.com/query/queryById.do?id=" + "511124197112206628";
        //开启线程来发起网络请求
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(uri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = 7;
                    //将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    handler.sendMessage(message);

                } catch (MalformedURLException e) {
                    sStatus += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            check_result.setText(sStatus);
                            ToastUtil.showToast(AddUserAct.this, "网络连接失败，请连接网络后再试。");
                        }
                    });
                    e.printStackTrace();
                } catch (Exception e) {
                    sStatus += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            check_result.setText(sStatus);
                            ToastUtil.showToast(AddUserAct.this, "网络连接失败，请连接网络后再试。");
                        }
                    });
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }

            }

        }).start();

    }


    private void insertUser() {
        final ProgressDialog dialog = ProgressDialog.show(this, "正在录入", "请稍后...", true, false);
        userBean = new UserBean();
        userBean.setName(MyApplication.idCardInfo.getName());
        userBean.setNation(MyApplication.idCardInfo.getNation());
        userBean.setBirthday(MyApplication.idCardInfo.getBirthday());
        userBean.setAddress(MyApplication.idCardInfo.getAddress());
        userBean.setCardNum(MyApplication.idCardInfo.getCardNum());
        userBean.setGender(MyApplication.idCardInfo.getGender());
        userBean.setIdphotoBitmap(MyApplication.idCardInfo.getPhoto());
        if (MyApplication.idCardInfo.getpType().equals("")) {
            userBean.setPassword("无失信记录");//失信人员案件详情
        } else {
            userBean.setPassword(MyApplication.idCardInfo.getpType());//失信人员案件详情
        }
//        userBean.setPhotoBitmap(MyApplication.getFaceBitamp());
        userBean.setDepartment("北京研发部");
        if (MyApplication.idCardInfo.getFingerInfo() != null)
            userBean.setFinger1(Base64.encode(MyApplication.idCardInfo.getFingerInfo()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String blackFeature = "";
                String colorFeature = "";
                if (blackBitmap != null) {
//                    Bitmap target = Bitmap.createBitmap(480, 640, blackBitmap.getConfig());
//                    Canvas canvas = new Canvas(target);
//                    canvas.drawBitmap(blackBitmap, null, new Rect(0, 0, target.getWidth(), target.getHeight()), null);
                    userBean.setPhotoBitmapBlack(blackBitmap);
                    blackFeature = FaceSDK.getFeature(blackBitmap);//近红外照片特征值
                }
                if (colorBitmap != null) {
                    userBean.setPhotoBitmap(colorBitmap);
                    colorFeature = FaceSDK.getFeature(colorBitmap);//彩色照片特征值
                }
                final String finalBlackFeature = blackFeature;
                final String finalColorFeature = colorFeature;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(finalColorFeature))
                            userBean.setFeature(finalColorFeature);//color
                        if (!TextUtils.isEmpty(finalBlackFeature))
                            userBean.setFeature1(finalBlackFeature);//black
                        userBean.setCreateTime(DateUtil.getNow(DateUtil.FORMAT_LONG));
                        MyApplication.dbManager.addUser(userBean);
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        handler.sendEmptyMessage(6);
                    }
                });
            }
        }).start();
    }

    private void gotoFaceDete1(IDCardInfo idCardInfo) {
        if (idCardInfo.getPhoto() == null) {
            threadStat = 0;
            ToastUtil.showToast(AddUserAct.this, "获取身份图片错误...");
            return;
        }
        getFeature(idCardInfo.getPhoto());
    }


    private void getFeature(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long start = System.currentTimeMillis();
                mFeatures = FaceSDK.getFeature(bitmap);
                Logs.i("Aming", "提特征时间：" + (System.currentTimeMillis() - start) + "ms");
                startTest();
            }
        }).start();
    }

    private void startTest() {
        Intent intent = new Intent();
        if (TextUtils.isEmpty(mFeatures)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(AddUserAct.this, "特征值为空。");
                }
            });
            threadStat = 0;
            return;
        }
        handler.sendEmptyMessage(0);//目视摄像头
        intent.setClass(AddUserAct.this, SingleUvcCamera.class);
        intent.putExtra(AddUserAct.ARG_FEATURES, mFeatures);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            intent.putExtra("what_type", 2);
        } else {
            intent.putExtra("what_type", 1);
        }

//        MyApplication.faceFeatures = "";
//        MyApplication.faceFeatures = mFeatures;
        startActivityForResult(intent, 11);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AddUserAct.this.RESULT_OK) return;
        switch (requestCode) {
            case 11:
                int score = data.getIntExtra("back_info", 0);//
//                ToastUtil.showToast(AddUserAct.this, "识别分数：" + score);

                String blackPicture = data.getStringExtra("face_picture");
                String colorPicture = data.getStringExtra("face_picture_");
                blackBitmap = BitmapFactory.decodeFile(blackPicture);
                colorBitmap = BitmapFactory.decodeFile(colorPicture);

                if (score == -1) {//人脸识别超时
                    sStatus += "人脸识别超时\n";
                    check_result.setText(sStatus);
                    handler.sendEmptyMessage(5);
                    //比对策略或 且 未打开指纹比对
                } else if (score < MyApplication.comcompareScore - 20) {
                    sStatus += "人脸识别失败:" + score + "\n";
                    check_result.setText(sStatus);
                    handler.sendEmptyMessage(5);
                    imgLeft.setImageBitmap(blackBitmap);
                    imgRight.setImageBitmap(colorBitmap);
                } else {
                    sStatus += "人脸识别成功:" + score + "\n";
                    check_result.setText(sStatus);
//                    insertUser();
                    imgLeft.setImageBitmap(blackBitmap);
                    imgRight.setImageBitmap(colorBitmap);
                    //联网核查
                    searchInfo(MyApplication.idCardInfo);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Runnable runableValidFinger = new Runnable() {
        @Override
        public void run() {
            validFingerTC();
//            threadStat = 0;//打开读卡线程
            handler.post(runableReadIDCard);
        }
    };

    private int validFingerTC() {
        isExitVaildFinger = false;
        try {
            int results = 0;
            int time = 10000;
            long start = System.currentTimeMillis();
            handler.sendEmptyMessage(3);//请按指纹
            while (!isExitVaildFinger) {
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
                    Log.e("app", "ImageAttr[2] = " + Main2Activity.fingerExt.fingerprint.ImageAttr[2]);
                    Main2Activity.fingerExt.fingerprint.imgSize = Main2Activity.fingerExt.fingerprint.ImageAttr[0] * Main2Activity.fingerExt.fingerprint.ImageAttr[1] + 1024 + 54;
                    bitmapFinger = BitmapFactory.decodeByteArray(Main2Activity.fingerExt.fingerprint.imageBuffer, 0, Main2Activity.fingerExt.fingerprint.imgSize);
                    Main2Activity.fingerExt.fingerprint.FP_Beep();
                    System.arraycopy(Main2Activity.fingerExt.fingerprint.featureBuffer, 0,
                            Main2Activity.fingerExt.featureBuffer0, 0,
                            Main2Activity.fingerExt.fingerprint.featureBuffer.length);

                    float[] score = new float[]{0};
                    byte[] f1 = new byte[512];
                    byte[] f2 = new byte[512];
                    System.arraycopy(MyApplication.idCardInfo.getFingerInfo(), 0, f1, 0, 512);
                    System.arraycopy(MyApplication.idCardInfo.getFingerInfo(), 512, f2, 0, 512);
                    //特征值比对
                    int result = Main2Activity.fingerExt.fingerprint.FP_FeatureMatch(Main2Activity.fingerExt.featureBuffer0,
                            f1, score);
                    if (result >= 0 && score[0] <= 0)
                        result = Main2Activity.fingerExt.fingerprint.FP_FeatureMatch(Main2Activity.fingerExt.featureBuffer0,
                                f2, score);
                    //比对结果处理
                    if (result >= 0) {
                        if (score[0] > 0) {
                            sStatus += "指纹比对成功\n";
                            handler.sendEmptyMessage(4);
                        } else {
                            sStatus += "指纹比对失败\n";
                            handler.sendEmptyMessage(5);
                        }
                        break;
                    } else {
                        continue;
                    }
                } else if (System.currentTimeMillis() - start >= time) {
                    //超时
                    sStatus += "指纹比对超时\n";
                    handler.sendEmptyMessage(5);
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


    private void showPasswordrDialog() {
        final CustomDialog customDialog = new CustomDialog(AddUserAct.this);
        customDialog.show();
        customDialog.setClicklistener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void upload(EditText et) {
                String password = et.getText().toString();
                if ("".equals(password)) {
                    ToastUtils.showToast(AddUserAct.this, "请输入密码");
                    return;
                }
                if (!password.equals(MyApplication.PASSWORD)) {
                    ToastUtils.showToast(AddUserAct.this, "密码输入错误");
                    return;
                }
                startActivity(new Intent(AddUserAct.this, HandlerCheckAct.class));
                customDialog.dismiss();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runableReadIDCard);
//        releaseCardReader(true);
        //        releaseFingerPrinter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExitVaildFinger = true;
        handler.removeCallbacks(runableReadIDCard);
        handler.removeCallbacks(runableValidFinger);
//        releaseCardReader(true);
//        releaseFingerPrinter();
    }
}
