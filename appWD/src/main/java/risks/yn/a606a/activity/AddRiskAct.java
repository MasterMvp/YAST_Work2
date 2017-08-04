package risks.yn.a606a.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A606AReader;
import com.YinanSoft.CardReaders.A606LReader;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.Police.Base64;
import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.util.JSONUtils;
import com.rsk.api.ICard;
import com.rsk.api.ICardInfo;
import com.techshino.fingerprint.FingerExt;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cn.hdcloudwalk.httprequest.HDHttpBadPost;
import cn.hdcloudwalk.httprequest.HDHttpPhonePost;
import cn.hdcloudwalk.httprequest.HDHttpZxPost;
import cn.hdcloudwalk.httprequest.SSLConnection;
import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;
import risks.yn.a606a.Utils.CertImgDisposeUtils;
import risks.yn.a606a.Utils.DateUtil;
import risks.yn.a606a.Utils.SoundPoolAudioClip;
import risks.yn.a606a.Utils.ToastUtils;
import risks.yn.a606a.bean.RiskBean;
import risks.yn.a606a.bean.UserBean;
import risks.yn.a606a.dialog.CustomDialog;
import risks.yn.a606a.riskbean.MultipointDebtList;
import risks.yn.a606a.riskbean.NetLoanBlackList;


/**
 * Created by Administrator on 2017/4/17.
 */

public class AddRiskAct extends Activity {

    public static final String ARG_FEATURES = FaceTestActivity.class.getSimpleName() + ".feature";
    private final String mothed = "checkReportDetail";
    public static final String TAG = "AddRiskAct";
    //    private IDCardReader idReader;
    private ImageView img;
    private IDCardInfo info = null;
    //    private  Main2Activity.fingerExt  Main2Activity.fingerExt = null;
    private RiskBean userBean;
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
    private TextView mesg;
    private HDHttpPhonePost requestPhone = null;
    private StringBuffer sb = new StringBuffer();
    private String badResponse = "";

    private static String phoneNum = "";
    public static FingerExt fingerExt = null;
    public IDCardReader idReader = null;

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
//                    ToastUtil.showToast(AddRiskAct.this, "录入失败。");
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.validfail);
//                    handler.removeCallbacks(runableReadIDCard);
//                    handler.postDelayed(runableReadIDCard, 300);
                    break;
                case 6://录入成功
                    threadStat = 0;
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.optionsuccess);
//                    handler.removeCallbacks(runableReadIDCard);
//                    handler.postDelayed(runableReadIDCard, 300);
                    break;
                case 10:
                    threadStat = 0;
                    final String response = (String) msg.obj;
                    Log.e(TAG, "response:" + response);
                    setMsg(response, mesg);
                    if (idReader == null) {
                        ToastUtil.showToast(AddRiskAct.this, "idReader==null");
                    }
                    handler.removeCallbacks(runableReadIDCard);
                    handler.postDelayed(runableReadIDCard, 300);
                    if (!TextUtils.isEmpty(mesg.getText().toString().trim())) {
                        mesg.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AddRiskAct.this, RiskMessageAct.class);
                                if (TextUtils.isEmpty(response)) {
                                    return;
                                }
                                intent.putExtra("message", response);
                                intent.putExtra("phoneNum", phoneNum);
                                intent.putExtra("badResponse", badResponse);
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                case 11:
                    threadStat = 0;
                    String errorMsg = (String) msg.obj;
                    Log.e(TAG, "handler11:" + errorMsg);
                    mesg.setText("请检查网络连接!");
                    ToastUtil.showToast(AddRiskAct.this, "请检查网络连接!");
                    handler.removeCallbacks(runableReadIDCard);
                    handler.postDelayed(runableReadIDCard, 300);
                    break;
                case 13://手机号查询
                    String phoneResponse = (String) msg.obj;
                    if (phoneResponse.length() > 0) {
                        String retMsg = getSelectedNodeValue(phoneResponse);
//                        ToastUtil.showToast(AddRiskAct.this, retMsg);
                        if (retMsg.equals("验证通过")) {
                            //查询犯罪记录
                            String badMothed = "checkBadness";
                            HDHttpBadPost request = new HDHttpBadPost(AddRiskAct.this, MyApplication.idCardInfo.getCardNum(), MyApplication.idCardInfo.getName(), "", handler, badMothed);
                            request.execute();
                        } else {
                            mesg.setText(retMsg.toString());
                            threadStat = 0;
                            handler.removeCallbacks(runableReadIDCard);
                            handler.postDelayed(runableReadIDCard, 500);
                        }
                    }
                    break;
                case 14:
                    badResponse = (String) msg.obj;
                    setMsg(badResponse, mesg);
                    HDHttpZxPost request = new HDHttpZxPost(AddRiskAct.this, MyApplication.idCardInfo.getCardNum(), MyApplication.idCardInfo.getName(), phoneNum, "", handler, mothed);
                    request.execute();
//                    mesg.setText(badResponse);
                    Log.e(TAG, "个人不良信息返回:" + badResponse);
                    break;

            }

        }
    };
    private String lastIdCardNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add);
        img = (ImageView) findViewById(R.id.adduser_img);
        imgLeft = (ImageView) findViewById(R.id.img_left);
        imgRight = (ImageView) findViewById(R.id.img_right);
        check_result = (TextView) findViewById(R.id.result);
        mesg = (TextView) findViewById(R.id.mesg);
//        initCardReader();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume::::");
//        if ( Main2Activity.fingerExt == null)
//             Main2Activity.fingerExt = new  Main2Activity.fingerExt(AddUserAct.this);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initCardReader();
//                initFingerPrinter();
//            }
//        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCardReader();
                handler.removeCallbacks(runableReadIDCard);
                handler.postDelayed(runableReadIDCard, 300);
            }
        }).start();

//        if (fingerExt == null || MyApplication.initFingerSuccess == false) {
//            fingerExt = new FingerExt(getApplicationContext());
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    initFingerPrinter(); //电容指纹
//                }
//            }).start();
//        }

    }


    public void onClick(View v) {
//        ToastUtil.showToast(this, "被点击了");
        Intent intent = new Intent(this, CxQuery.class);
        startActivity(intent);
    }

    private void showPasswordrDialog() {
        final CustomDialog customDialog = new CustomDialog(AddRiskAct.this);
        customDialog.show();
        customDialog.setClicklistener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void upload(EditText et) {
                String password = et.getText().toString();
                if ("".equals(password)) {
                    ToastUtils.showToast(AddRiskAct.this, "请输入密码");
                    return;
                }
                if (!password.equals(MyApplication.PASSWORD)) {
                    ToastUtils.showToast(AddRiskAct.this, "密码输入错误");
                    return;
                }
                startActivity(new Intent(AddRiskAct.this, HandlerCheckAct.class));
                customDialog.dismiss();
            }
        });
    }


    private Runnable runableReadIDCard = new Runnable() {
        @Override
        public void run() {
            if (threadStat == 0) {
                msgReader();
                handler.postDelayed(runableReadIDCard, 300);
            }
        }
    };

    private void converToIDCardInfo(ICardInfo iCardInfo) {
        info = new IDCardInfo();
        info.setName(iCardInfo.name);
        info.setGender(iCardInfo.sex);
        info.setNation(iCardInfo.nation_str);
        info.setAddress(iCardInfo.address);
        info.setCardNum(iCardInfo.id_num);
        info.setBirthday(iCardInfo.birth_year + iCardInfo.birth_month + iCardInfo.birth_day);
        info.setPhoto(iCardInfo.getPhotoBitmap());
        info.setFingerInfo(iCardInfo.finger);
        info.setFingerBitmap(iCardInfo.getFingerBitmap());
        info.setRegistInstitution(iCardInfo.sign_office);
        info.setValidStartDate(iCardInfo.useful_s_date_year + iCardInfo.useful_s_date_month + iCardInfo.useful_s_date_day);
        info.setValidEndDate(iCardInfo.useful_s_date_year + iCardInfo.useful_e_date_month + iCardInfo.useful_e_date_day);
    }

    /**
     * 获取录入信息
     */
    public void msgReader() {
        if (Build.MODEL.toUpperCase().equals("SK-S600")) {
            ICardInfo iCardInfo = new ICardInfo();
            int nResult = ICard.ReadAll(iCardInfo);
//            Log.e(TAG, "lastIdCardNum:" + lastIdCardNum);
            if (nResult == 0) {
//                if (lastIdCardNum.equals(iCardInfo.id_num)) return;
//                lastIdCardNum = iCardInfo.id_num;
                converToIDCardInfo(iCardInfo);
            } else {
                return;
            }
        } else {
            if (idReader == null) return;
            info = idReader.ReadBaseCardInfo(new String[1]);
        }

        if (info == null) {
//            ToastUtil.showToast(AddRiskAct.this, "读取到身份证信息。");
            return;
        }
        threadStat = 1;
        bitmapFinger = null;
        sStatus = "";
        sStatus = "读卡成功\n";
        handler.sendEmptyMessage(1);
        MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.di);
        try {
            Bitmap bit = new CertImgDisposeUtils(AddRiskAct.this).creatBitmap(info);
            img.setImageBitmap(bit);
            imgLeft.setImageBitmap(info.getPhoto());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        info.setCardNum("511124197112206628");
        MyApplication.idCardInfo = info;

//        //联网核查
//        searchInfo(MyApplication.idCardInfo);

        //指纹比对
        if (MyApplication.initFingerSuccess) {
            if (MyApplication.idCardInfo.getFingerInfo() != null) {//info.getFingerInfo() != null
                threadStat = 1;//关闭读卡
                new Thread(runableValidFinger).start();
                return;
            }
        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ToastUtil.showToast(AddRiskAct.this, "指纹初始化失败。");
//                }
//            });
        }
        Log.e(TAG, "人脸初始化" + MyApplication.initFaceDeleSuccess);
//        人脸比对
        if (MyApplication.initFaceDeleSuccess) {
            Log.e(TAG, "人脸比对");
            gotoFaceDete1(MyApplication.idCardInfo);
            return;
        }
//        insertRisk();

    }

    private void searchInfo(IDCardInfo idCardInfo) {
        if (idCardInfo == null) {
            ToastUtil.showToast(AddRiskAct.this, "请先读取身份证");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "请求中", "请稍后...", true, false);
        final String uri = "http://www.kdzxpt.com/query/queryById.do?id=" + idCardInfo.getCardNum();
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
                            ToastUtil.showToast(AddRiskAct.this, "网络连接失败，请连接网络后再试。");
                        }
                    });

                    e.printStackTrace();
                } catch (Exception e) {
                    sStatus += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            check_result.setText(sStatus);
                            ToastUtil.showToast(AddRiskAct.this, "网络连接失败，请连接网络后再试。");
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


    private void insertRisk() {
        final ProgressDialog dialog = ProgressDialog.show(this, "正在录入", "请稍后...", true, false);
        userBean = new RiskBean();
        userBean.setName(MyApplication.idCardInfo.getName());
        userBean.setNation(MyApplication.idCardInfo.getNation());
        userBean.setBirthday(MyApplication.idCardInfo.getBirthday());
        userBean.setAddress(MyApplication.idCardInfo.getAddress());
        userBean.setCardNum(MyApplication.idCardInfo.getCardNum());
        userBean.setGender(MyApplication.idCardInfo.getGender());
        userBean.setIdphotoBitmap(MyApplication.idCardInfo.getPhoto());

        if ("".equals(MyApplication.idCardInfo.getpType())) {
            userBean.setMessage("无失信记录");//失信人员案件详情
        } else {
            userBean.setMessage(MyApplication.idCardInfo.getpType());//失信人员案件详情
        }
//        userBean.setPhotoBitmap(MyApplication.getFaceBitamp());
//        userBean.setDepartment("北京研发部");
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
//                        if (!TextUtils.isEmpty(finalColorFeature))
//                            userBean.setFeature(finalColorFeature);//color
//                        if (!TextUtils.isEmpty(finalBlackFeature))
//                            userBean.setFeature1(finalBlackFeature);//black
                        userBean.setCreateTime(DateUtil.getNow(DateUtil.FORMAT_LONG));
                        MyApplication.dbManager.addRisk(userBean);
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
            ToastUtil.showToast(AddRiskAct.this, "获取身份图片错误...");
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
                    ToastUtil.showToast(AddRiskAct.this, "特征值为空。");
                }
            });
            threadStat = 0;
            return;
        }
        handler.sendEmptyMessage(0);//目视摄像头
        intent.setClass(AddRiskAct.this, SingleUvcCamera.class);
        intent.putExtra(AddRiskAct.ARG_FEATURES, mFeatures);
        intent.putExtra("what_type", 1);
//        MyApplication.faceFeatures = "";
//        MyApplication.faceFeatures = mFeatures;
        startActivityForResult(intent, 11);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AddRiskAct.this.RESULT_OK) return;
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
                    final CustomDialog customDialog = new CustomDialog(this);
                    customDialog.show();
                    customDialog.setClicklistener(new CustomDialog.ClickListenerInterface() {
                        @Override
                        public void upload(EditText et) {
                            phoneNum = et.getText().toString().trim();
                            if ("".equals(phoneNum)) {
                                ToastUtils.showToast(AddRiskAct.this, "请输入手机号");
                                return;
                            }
                            customDialog.dismiss();
//                            handler.sendEmptyMessage(10);
//                            String badMothed = "checkBadness";
//                            HDHttpBadPost request = new HDHttpBadPost(AddRiskAct.this, "130634198110125034", "庞龙", "", handler, badMothed);
//                            request.execute();
//                            HDHttpZxPost request = new HDHttpZxPost(AddRiskAct.this, MyApplication.idCardInfo.getCardNum(), MyApplication.idCardInfo.getName(), phoneNum, "", handler, mothed);
//                            request.execute();

                            String mothedPhoneName = "checkMobilePhone";
                            requestPhone = new HDHttpPhonePost(AddRiskAct.this, MyApplication.idCardInfo.getCardNum(), MyApplication.idCardInfo.getName(), phoneNum, "0", "", handler, mothedPhoneName);
                            requestPhone.execute();

                        }
                    });
                    customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            threadStat = 0;
                            handler.removeCallbacks(runableReadIDCard);
                            handler.postDelayed(runableReadIDCard, 300);
                        }
                    });
                    //联网核查
//                    searchInfo(MyApplication.idCardInfo);
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
                    Log.e("app", "ImageAttr[2] = " + fingerExt.fingerprint.ImageAttr[2]);
                    fingerExt.fingerprint.imgSize = fingerExt.fingerprint.ImageAttr[0] * fingerExt.fingerprint.ImageAttr[1] + 1024 + 54;
                    bitmapFinger = BitmapFactory.decodeByteArray(fingerExt.fingerprint.imageBuffer, 0, fingerExt.fingerprint.imgSize);
                    fingerExt.fingerprint.FP_Beep();
                    System.arraycopy(fingerExt.fingerprint.featureBuffer, 0,
                            fingerExt.featureBuffer0, 0,
                            fingerExt.fingerprint.featureBuffer.length);

                    float[] score = new float[]{0};
                    byte[] f1 = new byte[512];
                    byte[] f2 = new byte[512];
                    System.arraycopy(MyApplication.idCardInfo.getFingerInfo(), 0, f1, 0, 512);
                    System.arraycopy(MyApplication.idCardInfo.getFingerInfo(), 512, f2, 0, 512);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    check_result.setText(sStatus);
                                }
                            });
                            handler.sendEmptyMessage(4);
                        } else {
                            sStatus += "指纹比对失败\n";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    check_result.setText(sStatus);
                                }
                            });
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


    /**
     * 初始化
     */
    private void initCardReader() {
        if (idReader == null) {
            if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
                idReader = new A606LReader(AddRiskAct.this);
            } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
                idReader = new A606AReader(AddRiskAct.this);
            } else {
                idReader = new A606AReader(AddRiskAct.this);
            }
        }
        if (idReader != null) {
            try {
                idReader.PowerOnReader();
                idReader.InitReader(null);
            } catch (Exception e) {

            }
        }

    }

    public void releaseCardReader(boolean poweroff) {
        if (idReader != null) {
            if (poweroff) idReader.PowerOffReader();
            idReader.ReleaseReader();
            idReader = null;
        }
    }

//    private void initFingerPrinter() {
//        //天成指纹
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//             fingerExt.PowerOnReader();
//        } else {
//             fingerExt.PowerOnFinger606A();
//        }
//         fingerExt.initUsbFinger();
//    }
//
//    public void releaseFingerPrinter() {
//        isExitVaildFinger = true;
//        if ( Main2Activity.fingerExt != null) {
//             fingerExt.UsbFingerClose();
//        }
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//            //606 and 800
//             fingerExt.PowerOffReader();
//        } else {
//             fingerExt.PowerOffFinger606A();//606A指纹下电
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        handler.removeCallbacks(runableReadIDCard);
        new Thread(new Runnable() {
            @Override
            public void run() {
                releaseCardReader(true);
            }
        }).start();
        //        releaseCardReader(true);
        //        releaseFingerPrinter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExitVaildFinger = true;
        handler.removeCallbacks(runableReadIDCard);
        handler.removeCallbacks(runableValidFinger);
//        releaseFingerPrinter();
//        releaseCardReader(false);
//        releaseFingerPrinter();
    }


    public void setMsg(String respose, TextView message) {
        Log.e(TAG, "返回数据:" + respose);

        MultipointDebtList multiData = null;
        NetLoanBlackList netLoanDate = null;
        ArrayList<MultipointDebtList> multList = new ArrayList<>();

//        String respose = "{\"retcode\":\"10\",\"retmsg\":\"调用成功\",\"retdetail\":{\"message\":\"查询成功\",\"result\":\"0\",\"caseTime\":\"2014-03-12,-,-\",\"isMatch\":\"true\",\"outOrderNo\":\"zx1501034148589\",\"jnlNo\":\"20170726004057000011\",\"badnessType\":\"前科、涉毒、吸毒\"}}";
//        String respose = "{'retcode':'10','retmsg':'调用成功','retdetail':{'message':'命中','result':'1','multipointDebtList':[{'contractDate':'2010年01月','arrearsAmount':'2035','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'5','repayState':'1','companyCode':'P2P393218'},{'contractDate':'2009年10月','arrearsAmount':'2250','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'4','repayState':'9','companyCode':'P2P393218'},{'contractDate':'2009年07月','arrearsAmount':'2365','borrowState':'6','borrowAmount':'6','borrowType':'1','loanPeriod':'3','repayState':'7','companyCode':'P2P393218'},{'contractDate':'2009年04月','arrearsAmount':'2476','borrowState':'2','borrowAmount':'7','borrowType':'1','loanPeriod':'2','repayState':'3','companyCode':'P2P393218'},{'contractDate':'2009年01月','arrearsAmount':'2589','borrowState':'1','borrowAmount':'8','borrowType':'1','loanPeriod':'1','repayState':'1','companyCode':'P2P393218'}],'riskLevel':'3','outOrderNo':'zx1496286575191','riskNum':3,'payBlackMap':{'blackBank':'光大银行中关村支行','blackDate':'2017年03月01日','blackArea':'北京','blackChannel':'联盟成员','blackContent':'个人钓鱼网站诈骗'},'netLoanBlackList':[{'borrowPeriod':'21','overdueDate':'2015年05月','borrowAmount':'8-10','overdueAmount':'3-6','borrowDate':'2015年02月','overdueLevel':'M3'},{'borrowPeriod':'23','overdueDate':'2015年09月','borrowAmount':'7-9','overdueAmount':'1-4','borrowDate':'2015年08月','overdueLevel':'M5'},{'borrowPeriod':'26','overdueDate':'2016年06月','borrowAmount':'5-8','overdueAmount':'10-16','borrowDate':'2016年05月','overdueLevel':'M6+'},{'borrowPeriod':'28','overdueDate':'2016年12月','borrowAmount':'2-7','overdueAmount':'3-10','borrowDate':'2016年11月','overdueLevel':'M6'}],'jnlNo':'20170601100823000683'}}";
//        respose = "{\"retcode\":\"10\",\"retmsg\":\"调用成功\",\"retdetail\":{\"message\":\"命中\",\"result\":\"1\",\"queryRecordMap\":{\"toTalCusNum\":\"1\",\"lastPrdGrpName\":\"中国电信\",\"toTalTransNum\":\"2\",\"lastTransTime\":\"2017年06月05日 13时17分16秒\"},\"riskLevel\":\"1\",\"outOrderNo\":\"zx1500372123352\",\"riskNum\":1,\"jnlNo\":\"20170718180159507237\"}}";
        try {
            JSONObject jsonObj = new JSONObject(respose);
            String retcode = jsonObj.optString("retcode");

            if (!"10".equals(retcode)) {
                message.setText(jsonObj.optString("retmsg"));
                return;
            }
            if (respose.contains("\"message\":\"查询无结果\"")) {
                sb.append("\n无犯罪记录\n");
                return;
            }
            jsonObj = jsonObj.optJSONObject("retdetail");
            String badnessType = jsonObj.optString("badnessType");
            if (!TextUtils.isEmpty(badnessType)) {
                sb.append("\n犯罪记录:" + badnessType + "\n");
            }

            String message1 = jsonObj.optString("message");
            if (message1.equals("未命中")) {
                message.setText("个人风险信息:未命中" + sb.toString() + "\n");
                return;
            }

            JSONArray courtLoseCreditList = jsonObj.optJSONArray("courtLoseCreditList");
            if (courtLoseCreditList != null && courtLoseCreditList.length() >= 1) {
                sb.append("有犯罪记录\n");
            }

            JSONObject queryRecordMap = jsonObj.optJSONObject("queryRecordMap");
            if (queryRecordMap != null) {
                String toTalTransNum = queryRecordMap.getString("toTalTransNum");
                String lastTransTime = queryRecordMap.getString("lastTransTime");
                String toTalCusNum = queryRecordMap.getString("toTalCusNum");
                String lastPrdGrpName = queryRecordMap.getString("lastPrdGrpName");
                sb.append("三个月内查询记录\n被查询次数:" + toTalTransNum + "最近一次被查询时间:" + lastTransTime + "被查询机构数:" + toTalCusNum + "最近一次被查询产品:" + lastPrdGrpName);
            }

            //多头负债详情信息
            JSONArray multipointDebtList = jsonObj.optJSONArray("multipointDebtList");
            if (multipointDebtList != null && multipointDebtList.length() >= 1) {
                for (int i = 0; i < multipointDebtList.length(); i++) {
                    JSONObject multObj = new JSONObject(multipointDebtList.getString(i));
                    multiData = new MultipointDebtList();
                    multiData.setContractDate(multObj.getString("contractDate"));
                    multiData.setArrearsAmount(multObj.getString("arrearsAmount"));
                    multiData.setBorrowState(multObj.getString("borrowState"));
                    multiData.setBorrowAmount(multObj.getString("borrowAmount"));
                    multiData.setBorrowType(multObj.getString("borrowType"));
                    multiData.setLoanPeriod(multObj.getString("loanPeriod"));
                    multiData.setRepayState(multObj.getString("repayState"));
                    multiData.setCompanyCode(multObj.getString("companyCode"));
                    multList.add(multiData);
                }
                sb.append("多头负债信息:" + multList.size() + "条\n");
            }

            //个人账户支付风险信息
            JSONObject paymap = jsonObj.optJSONObject("payBlackMap");
            if (paymap != null) {
                sb.append("个人账户支付风险信息\n银行:" + paymap.getString("blackBank") + "  日期:" + paymap.getString("blackDate") + "  地点:" + paymap.getString("blackArea") + "  路径:" + paymap.getString("blackChannel") + "  事件:" + paymap.getString("blackContent") + "\n");
            }

            JSONArray netLoanArr = jsonObj.optJSONArray("netLoanBlackList");
            ArrayList<NetLoanBlackList> netLoanBlackLists = new ArrayList<>();
            //多头负债信息
            if (netLoanArr != null && netLoanArr.length() >= 1) {
                for (int i = 0; i < netLoanArr.length(); i++) {
                    JSONObject netObj = new JSONObject(netLoanArr.getString(i));
                    netLoanDate = new NetLoanBlackList();
                    netLoanDate.setBorrowPeriod(netObj.getString("borrowPeriod"));
                    netLoanDate.setOverdueDate(netObj.getString("overdueDate"));
                    netLoanDate.setBorrowAmount(netObj.getString("borrowAmount"));
                    netLoanDate.setOverdueAmount(netObj.getString("overdueAmount"));
                    netLoanDate.setBorrowDate(netObj.getString("borrowDate"));
                    netLoanDate.setOverdueLevel(netObj.getString("overdueLevel"));
                    netLoanBlackLists.add(netLoanDate);
                }
                sb.append("逾期及违约:" + netLoanBlackLists.size() + "条");
            }

//            String mes = "多头负债信息:" + multList.size() + "条\n" + paymapMsg + "\n逾期及违约:" + netLoanBlackLists.size() + "条";
//            mesg.setText(mes);
            message.setText(sb.toString());
            Log.e(TAG, sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "getMsg解析出错了" + e.toString());
            e.printStackTrace();
        }
    }

    public String getSelectedNodeValue(String xmldata) {
        String s = "";
        try {
            Document document = DocumentHelper.parseText(xmldata); // 将字符串转为XML
            Element retmsg = (Element) document.selectSingleNode("/results/retmsg"); // 系统判断为不同人,比分为:100
            Element ret_fs = (Element) document.selectSingleNode("/results/ret_fs"); // 分数
            // 100
            Element errorcode = (Element) document.selectSingleNode("/results/errorcode"); // 错误码
            Element ret_gmsfhm = (Element) document.selectSingleNode("/results/ret_gmsfhm"); // 身份证号
            // 是否一致
            Element ret_xm = (Element) document.selectSingleNode("/results/ret_xm"); // 姓名是否一致
            Element ret_fx = (Element) document.selectSingleNode("/results/ret_fx"); // 系统判断为不同人
            Element errormsg = (Element) document.selectSingleNode("/results/errormsg"); // <errormsg>调用成功</errormsg>
            Element retcode = (Element) document.selectSingleNode("/results/retcode"); // <retcode>4</retcode>

            String judgeString = errormsg.getText();
            if (judgeString.equals("调用成功")) {
                s = retmsg.getText();
            } else {
                s = errormsg.getText() + "错误码:" + errorcode.getText();
            }

        } catch (Exception ex) {
            // ex.printStackTrace();
            s = ex.toString();
        }
        return s;
    }

    private void initFingerPrinter() {
        //天成指纹
        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
            fingerExt.PowerOnReader();
        } else {
            fingerExt.PowerOnFinger606A();
        }
        fingerExt.initUsbFinger();
    }

    public void releaseFingerPrinter() {
        if (fingerExt != null) {
            fingerExt.UsbFingerClose();
        }
        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
            //606 and 800
            fingerExt.PowerOffReader();
        } else {
            fingerExt.PowerOffFinger606A();//606A指纹下电
        }
    }

    public void ErrorCode(String retcocd) {

    }
}
