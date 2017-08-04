package attendance.yn.a606a.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.util.JSONUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;
import attendance.yn.a606a.utils.CertImgDisposeUtils;
import attendance.yn.a606a.utils.DateUtil;
import attendance.yn.a606a.utils.SoundPoolAudioClip;
import attendance.yn.a606a.bean.UserBean;
import cn.hdcloudwalk.httprequest.HDHttpMultipartPost;


public class HandlerCheckAct extends Activity {

    public static final String TAG = "HandlerCheckAct";
    public static final String ARG_FEATURES = FaceTestActivity.class.getSimpleName() + ".feature";
    private ImageView img;
    private static Bitmap bit = null;
    private EditText name, idCard;
    private TextView check_result;
    private String detailCase = "";
    private String sStatue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.handlercheck_act);
        initView();
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.check_img);
        name = (EditText) findViewById(R.id.check_name);
        name.setText("赵金彪");
        idCard = (EditText) findViewById(R.id.check_idcard);
        idCard.setText("232321199510252915");
        check_result = (TextView) findViewById(R.id.check_result);
    }

    public void onClick(View v) {
        nameStr = name.getText().toString().trim();
        idCardStr = idCard.getText().toString().trim();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtil.showToast(this, "请填写姓名！");
            return;
        }
        if (TextUtils.isEmpty(idCardStr)) {
            ToastUtil.showToast(this, "请填写身份证号码！");
            return;
        }
        sStatue = "";
//        searchInfo(idCardStr);
        searchInfo(idCardStr);
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivityForResult(intent, 1);

//        MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.look);//请目视摄像头
//        Intent intent = new Intent();
//        intent.setClass(HandlerCheckAct.this, SingleUvcCamera.class);
//        intent.putExtra(AddUserAct.ARG_FEATURES, "");
//        intent.putExtra("what_type", 0);
//        startActivityForResult(intent, 11);
    }

    public void onClick1(View v) {
        nameStr = name.getText().toString().trim();
        idCardStr = idCard.getText().toString().trim();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtil.showToast(this, "请填写姓名！");
            return;
        }
        if (TextUtils.isEmpty(idCardStr)) {
            ToastUtil.showToast(this, "请填写身份证号码！");
            return;
        }
        if (TextUtils.isEmpty(detailCase)) {
            ToastUtil.showToast(this, "请先查询失信信息！");
            return;
        }
        sStatue = "";
        MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.look);//请目视摄像头
        Intent intent = new Intent();
        intent.setClass(HandlerCheckAct.this, SingleUvcCamera.class);
        intent.putExtra(AddUserAct.ARG_FEATURES, "");
        intent.putExtra("what_type", 0);
        startActivityForResult(intent, 11);


    }


    public static void setOCRBitmapAcross(Bitmap bitmap) {
        HandlerCheckAct.bit = bitmap;
    }

    String nameStr = "";
    String idCardStr = "";
    private Bitmap blackBitmap;
    private Bitmap colorBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && bit != null) {
            nameStr = name.getText().toString().trim();
            idCardStr = idCard.getText().toString().trim();
            //旋转角度
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
//            matrix.postScale(-1, 1);
            Bitmap resizedBitmap = Bitmap.createBitmap(bit, 0, 0,
                    bit.getWidth(), bit.getHeight(), matrix, true);
            String s = CertImgDisposeUtils.bitmapToBase64(resizedBitmap);
            img.setImageBitmap(resizedBitmap);
//            comparePost(nameStr, idCardStr, s);

        } else if (requestCode == 11) {


//            int score = data.getIntExtra("back_info", 0);//
//            ToastUtil.showToast(HandlerCheckAct.this, "识别分数：" + score);
            nameStr = name.getText().toString().trim();
            idCardStr = idCard.getText().toString().trim();
            Log.e(TAG, "nameStr:" + nameStr + ",idCardStr:" + idCardStr);
            String blackPicture = data.getStringExtra("face_picture");
            String colorPicture = data.getStringExtra("face_picture_");
            blackBitmap = BitmapFactory.decodeFile(blackPicture);
            colorBitmap = BitmapFactory.decodeFile(colorPicture);


            if (colorBitmap != null) {
//                ToastUtil.showToast(HandlerCheckAct.this, "获取到了");
                String s = CertImgDisposeUtils.bitmapToBase64(colorBitmap);
                comparePost(nameStr, idCardStr, s);
                img.setImageBitmap(colorBitmap);
            } else if (blackBitmap != null) {
                String s = CertImgDisposeUtils.bitmapToBase64(blackBitmap);
                img.setImageBitmap(blackBitmap);
                comparePost(nameStr, idCardStr, s);
            } else {
                ToastUtil.showToast(this, "请先拍照！");
            }

//            if (colorBitmap != null) {
////                ToastUtil.showToast(HandlerCheckAct.this, "获取到了");
//                String s = CertImgDisposeUtils.bitmapToBase64(colorBitmap);
//                img.setImageBitmap(colorBitmap);
//
////                comparePost(nameStr, idCardStr, s);
//            } else if (blackBitmap != null) {
//                String s = CertImgDisposeUtils.bitmapToBase64(blackBitmap);
//                img.setImageBitmap(blackBitmap);
////                comparePost(nameStr, idCardStr, s);
//            }
        }

    }

    //联网核查
    private void searchInfo(String cardNum) {
        ToastUtil.showToast(this, "开始查询");
        final ProgressDialog dialog = ProgressDialog.show(this, "请求中", "请稍后...", true, false);
        final String uri = "http://182.254.138.33/query/queryById.do?id=" + cardNum;
//        final String uri = "http://182.254.138.33/query/queryById.do?id=" + "511124197112206628";
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
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
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
                    sStatue += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            check_result.setText(sStatue);
                            ToastUtil.showToast(HandlerCheckAct.this, "网络连接失败，请连接网络后再试。");
                        }
                    });
                    e.printStackTrace();
                } catch (Exception e) {
                    sStatue += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            check_result.setText(sStatue);
                            ToastUtil.showToast(HandlerCheckAct.this, "网络连接失败，请连接网络后再试。");
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


    /**
     * 认证核验接口
     *
     * @param nameStr   名字
     * @param icCardStr 身份证号
     * @param pic       现场采集照片的Base64
     */
    private void comparePost(String nameStr, String icCardStr, String pic) {
        // BASE64字符串
        HDHttpMultipartPost post = new HDHttpMultipartPost(HandlerCheckAct.this, icCardStr, nameStr, pic, "", handler,
                "verificationIdentity");
        post.execute();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 10) {
                try {
                    String is = (String) msg.obj;
                    Log.d("SHY", "is = " + is);
                    if (is.length() > 0) {
                        String ret = getSelectedNodeValue(is);
                        Log.e("結果", "三要素查询结果：" + ret);
                        if (ret.contains("系统判断为同一人")) {
//                            sStatue = "联网核查正常\n";
//                            searchInfo(idCard.getText().toString().trim());
                            insertUser();
                        } else if (ret.contains("系统判断为不同人")) {
                            sStatue = "添加失败，所持证件与本人不符，请重新操作\n";
                            ToastUtil.showToast(HandlerCheckAct.this, "添加失败，所持证件与本人不符，请重新操作。");
                        } else {
                            sStatue = "查询失败：" + ret + "\n";
                            ToastUtil.showToast(HandlerCheckAct.this, "查询失败：" + ret);
                        }
                        check_result.setText(sStatue);

//                        AlertDialog.Builder bd = new AlertDialog.Builder(
//                                new ContextThemeWrapper(HandlerCheckAct.this, android.R.style.Theme_Holo_Light_Dialog));
//                        bd.setTitle("查询结果");
//                        bd.setMessage(ret);
//                        bd.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                paramDialogInterface.dismiss();
//
//                            }
//                        });
//                        bd.setCancelable(true);
//                        bd.create().show();

                    } else {
                        Log.e("SHY", "人像比对失败2");
                        sStatue = "人像比对失败\n";
                        check_result.setText(sStatue);
                        Toast.makeText(HandlerCheckAct.this, "人像比对失败2", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("SHY", "人像比对失败3 [" + e.getMessage() + "]");
                    Toast.makeText(HandlerCheckAct.this, "人像比对失败3 [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
                }
            } else if (msg.what == 6) {
                sStatue += "录入成功\n";
                detailCase = "";
                colorBitmap = null;
                blackBitmap = null;
                check_result.setText(sStatue);
                Toast.makeText(HandlerCheckAct.this, "录入成功！", Toast.LENGTH_LONG).show();
                MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.optionsuccess);
            } else if (msg.what == 7) {
                String response = (String) msg.obj;
                Log.e(TAG, "返回信息1111：" + response);
                try {
                    JSONArray jsonArray = JSONUtils.getJSONArray(response, "dataList", null);
                    StringBuffer sb = new StringBuffer();
                    if (jsonArray != null) {
                        Log.e(TAG, "返回信息：" + jsonArray.toString());
                        String behavior = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj1 = jsonArray.getJSONObject(i);
                            behavior = JSONUtils.getString(obj1, "behavior", "");
                            String time = JSONUtils.getString(obj1, "time", "");
                            sb.append(time + ":" + behavior + "\n");
                        }
                        Log.e(TAG, "返回信息1：" + sb.toString());

                        if (!"".equals(sb.toString())) {
                            sStatue += "存在失信记录（" + sb + "）\n";
                            detailCase = sb.toString();
                        } else {
                            sStatue += "无失信记录\n";
                            detailCase = "无失信记录";
                        }
                        check_result.setText(sStatue);
//                        insertUser();
//                            sStatus += sb.toString() + "\n";
//                            check_result.setText(sStatus);
//                            ToastUtil.showToast(AddUserAct.this, "返回结果：" + sStatus);
//                        Intent intent = new Intent();
//                        intent.setClass(HandlerCheckAct.this, SingleUvcCamera2.class);
//                        startActivityForResult(intent, 11);

                    } else {
                        //
                        String errorMsg = JSONUtils.getString(response, "errorMsg", "");
                        ToastUtil.showToast(HandlerCheckAct.this, errorMsg);
                        detailCase = "";
                        sStatue += errorMsg + "\n";
                        check_result.setText(sStatue);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == 11) {
                Log.e("SHY", "返回11");
                Log.e("SHY", "人像比对失败4 [" + (String) msg.obj + "]");
                Toast.makeText(HandlerCheckAct.this, "请检查网络！", Toast.LENGTH_LONG).show();
            }

        }
    };

    private void insertUser() {
        final ProgressDialog dialog = ProgressDialog.show(this, "正在录入", "请稍后...", true, false);
        final UserBean userBean = new UserBean();
        userBean.setCardNum(idCardStr);
        userBean.setName(nameStr);
        if (detailCase.equals("")) {
            userBean.setPassword("无失信记录");//失信人员案件详情
        } else {
            userBean.setPassword(detailCase);//失信人员案件详情
        }
//        userBean.setPhotoBitmap(MyApplication.getFaceBitamp());
        userBean.setDepartment("北京研发部");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String blackFeature = "";
                String colorFeature = "";
                if (blackBitmap != null) {
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
                s = errormsg.getText();
            }

        } catch (Exception ex) {
            // ex.printStackTrace();
            s = ex.toString();
        }

        return s;
    }
}
