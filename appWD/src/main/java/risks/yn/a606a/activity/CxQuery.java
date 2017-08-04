package risks.yn.a606a.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.Police.Base64;
import com.YinanSoft.Utils.ToastUtil;
import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.rsk.api.ICard;
import com.rsk.api.ICardInfo;

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
import java.util.ArrayList;

import cn.hdcloudwalk.httprequest.HDHttpBadPost;
import cn.hdcloudwalk.httprequest.HDHttpZxPost;
import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;
import risks.yn.a606a.Utils.CertImgDisposeUtils;
import risks.yn.a606a.Utils.DateUtil;
import risks.yn.a606a.Utils.SoundPoolAudioClip;
import risks.yn.a606a.bean.RiskBean;
import risks.yn.a606a.riskbean.MultipointDebtList;
import risks.yn.a606a.riskbean.NetLoanBlackList;
import risks.yn.a606a.sqlite.DBManager;


public class CxQuery extends Activity implements View.OnClickListener {
    private static final String TAG = "CxQuery";
    private final String mothed = "checkReportDetail";
    private Button query;
    private EditText xm, idCard, phoneNum;
    private String mFeatures = "";
    private TextView message;
    private String badResponse = "";
    private StringBuffer sb = new StringBuffer();
    private IDCardInfo info = null;
    private int threadStat = 0;
    private String sStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cx);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(runableReadIDCard, 1000);
    }

    private void initView() {
        query = (Button) findViewById(R.id.cx_query);
        query.setOnClickListener(this);
        xm = (EditText) findViewById(R.id.cx_name);
        xm.setText("苗思奇");
        idCard = (EditText) findViewById(R.id.cx_idcard);
        idCard.setText("341225199607183826");
        phoneNum = (EditText) findViewById(R.id.cx_phoneNum);
        phoneNum.setText("18856052883");
        message = (TextView) findViewById(R.id.queryMsg);


    }


    @Override
    public void onClick(View v) {
//        Log.i(TAG, "查询被点击了!");
//        setMsg("");

//        Intent intent = new Intent(this, RiskMessageAct.class);
//        String phone = phoneNum.getText().toString().trim();
        String name = xm.getText().toString().trim();
        String idCards = idCard.getText().toString().trim();

//
        IDCardInfo idCardInfo = new IDCardInfo();
        idCardInfo.setName(name);
        idCardInfo.setCardNum(idCards);
        MyApplication.idCardInfo = idCardInfo;
//        intent.putExtra("phoneNum", phone);
//        startActivity(intent);

        //查询犯罪记录
        String badMothed = "checkBadness";
        HDHttpBadPost request = new HDHttpBadPost(CxQuery.this, "150105196007102542", "郑二莲", "", handler, badMothed);
        request.execute();

//        String uName = xm.getText().toString().trim();
//        String cardNum = idCard.getText().toString().trim();
//        String phoneNumber = phoneNum.getText().toString().trim();
//        HDHttpZxPost request = new HDHttpZxPost(this, cardNum, uName, phoneNumber, "", handler, mothed);
//        request.execute();
//        query.setClickable(false);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.look);//请目视摄像头
                    break;
                case 5://指纹识别失败或人脸识别失败，录入失败
                    threadStat = 0;
                    sStatus += "录入失败";
                    handler.removeCallbacks(runableReadIDCard);
                    handler.postDelayed(runableReadIDCard, 500);
                    break;
                case 10:
                    final String messageObj = (String) msg.obj;
//                    final String messageObj = "{'retcode':'10','retmsg':'调用成功','retdetail':{'message':'命中','result':'1','multipointDebtList':[{'contractDate':'2010年01月','arrearsAmount':'2035','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'5','repayState':'1','companyCode':'P2P393218'},{'contractDate':'2009年10月','arrearsAmount':'2250','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'4','repayState':'9','companyCode':'P2P393218'},{'contractDate':'2009年07月','arrearsAmount':'2365','borrowState':'6','borrowAmount':'6','borrowType':'1','loanPeriod':'3','repayState':'7','companyCode':'P2P393218'},{'contractDate':'2009年04月','arrearsAmount':'2476','borrowState':'2','borrowAmount':'7','borrowType':'1','loanPeriod':'2','repayState':'3','companyCode':'P2P393218'},{'contractDate':'2009年01月','arrearsAmount':'2589','borrowState':'1','borrowAmount':'8','borrowType':'1','loanPeriod':'1','repayState':'1','companyCode':'P2P393218'}],'riskLevel':'3','outOrderNo':'zx1496286575191','riskNum':3,'payBlackMap':{'blackBank':'光大银行中关村支行','blackDate':'2017年03月01日','blackArea':'北京','blackChannel':'联盟成员','blackContent':'个人钓鱼网站诈骗'},'netLoanBlackList':[{'borrowPeriod':'21','overdueDate':'2015年05月','borrowAmount':'8-10','overdueAmount':'3-6','borrowDate':'2015年02月','overdueLevel':'M3'},{'borrowPeriod':'23','overdueDate':'2015年09月','borrowAmount':'7-9','overdueAmount':'1-4','borrowDate':'2015年08月','overdueLevel':'M5'},{'borrowPeriod':'26','overdueDate':'2016年06月','borrowAmount':'5-8','overdueAmount':'10-16','borrowDate':'2016年05月','overdueLevel':'M6+'},{'borrowPeriod':'28','overdueDate':'2016年12月','borrowAmount':'2-7','overdueAmount':'3-10','borrowDate':'2016年11月','overdueLevel':'M6'}],'jnlNo':'20170601100823000683'}}";
                    Log.e(TAG, "返回的数据::" + messageObj);
                    setMsg(messageObj);//解析展示
//                    insertRisk(messageObj);//插入数据库
                    if (!TextUtils.isEmpty(message.getText().toString().trim())) {
                        message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CxQuery.this, RiskMessageAct.class);
                                intent.putExtra("message", messageObj);
                                intent.putExtra("badResponse", badResponse);
                                String phoneNum1 = phoneNum.getText().toString().trim();
                                intent.putExtra("phoneNum", phoneNum1);

                                startActivity(intent);
                            }
                        });
                    }
                    query.setClickable(true);
                    break;
                case 11:
                    String ret1 = (String) msg.obj;
                    Log.e(TAG, "Handler11:" + ret1);
                    ToastUtil.showToast(CxQuery.this, "请确认网络连接正常!");
                    message.setText("请确认网络连接正常!");
                    query.setClickable(true);
                    break;
                case 14:
                    badResponse = (String) msg.obj;
                    setMsg(badResponse);
                    String phoneNumber = phoneNum.getText().toString().trim();
                    String uName = xm.getText().toString().trim();
                    String cardNum = idCard.getText().toString().trim();
//                    HDHttpZxPost request = new HDHttpZxPost(CxQuery.this, cardNum, uName, phoneNumber, "", handler, mothed);
//                    request.execute();
//                    message.setText(badResponse);
                    Log.e(TAG, "个人不良信息返回:" + badResponse);
                    break;
                default:
                    message.setText((String) msg.obj);
                    break;
            }
        }


    };


    private Runnable runableReadIDCard = new Runnable() {
        @Override
        public void run() {
            if (threadStat == 0) {
                msgReader();
                handler.postDelayed(runableReadIDCard, 1000);
            }
        }
    };

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
            if (Main2Activity.idReader == null) return;
            info = Main2Activity.idReader.ReadAllCardInfo(new String[1]);
        }

        if (info == null) {
//            ToastUtil.showToast(AddUserAct.this, "未读取到身份证信息。");
            return;
        }
//        threadStat = 1;
//        bitmapFinger = null;
        sStatus = "";
        sStatus = "读卡成功\n";
        Log.e(TAG, sStatus);
        handler.sendEmptyMessage(1);
        MyApplication.sp.play(SoundPoolAudioClip.SoundIndex.di);
        try {
            Bitmap bit = new CertImgDisposeUtils(CxQuery.this).creatBitmap(info);
//            img.setImageBitmap(bit);
//            imgLeft.setImageBitmap(info.getPhoto());
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

//                threadStat = 1;//关闭读卡
//                new Thread(runableValidFinger).start();
                return;
            }
        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ToastUtil.showToast(AddUserAct.this, "指纹初始化失败。");
//                }
//            });
        }
//        人脸比对
        if (MyApplication.initFaceDeleSuccess) {
            gotoFaceDete1(MyApplication.idCardInfo);
            return;
        }

    }


    private void searchInfo(IDCardInfo idCardInfo) {
        if (idCardInfo == null) {
            ToastUtil.showToast(CxQuery.this, "请先读取身份证");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "请求中", "请稍后...", true, false);
        final String uri = "http://www.kdzxpt.com/query/queryById.do?id=" + idCardInfo.getCardNum();
//        final String uri = "http://www.kdzxpt.com/query/queryById.do?id=" + "511124197112206628";
        //开启线程来发起网络请求
        new Thread(new Runnable() {

            @Override
            public void run() {
                String sStatus = null;
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
//                            check_result.setText("");
                            ToastUtil.showToast(CxQuery.this, "网络连接失败，请连接网络后再试。");
                        }
                    });

                    e.printStackTrace();
                } catch (Exception e) {
                    sStatus += "网络连接失败\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            check_result.setText("");
                            ToastUtil.showToast(CxQuery.this, "网络连接失败，请连接网络后再试。");
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


    private void gotoFaceDete1(IDCardInfo idCardInfo) {
        if (idCardInfo.getPhoto() == null) {
            threadStat = 0;
            ToastUtil.showToast(CxQuery.this, "获取身份图片错误...");
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
                    ToastUtil.showToast(CxQuery.this, "特征值为空。");
                }
            });
//            threadStat = 0;
            return;
        }
        handler.sendEmptyMessage(0);//目视摄像头
        intent.setClass(CxQuery.this, SingleUvcCamera.class);
        intent.putExtra(AddUserAct.ARG_FEATURES, mFeatures);
        intent.putExtra("what_type", 1);
//        MyApplication.faceFeatures = "";
//        MyApplication.faceFeatures = mFeatures;
        startActivityForResult(intent, 11);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != CxQuery.this.RESULT_OK) return;
        switch (requestCode) {
            case 11:
                int score = data.getIntExtra("back_info", 0);//
//                ToastUtil.showToast(AddUserAct.this, "识别分数：" + score);

                String blackPicture = data.getStringExtra("face_picture");
                String colorPicture = data.getStringExtra("face_picture_");
                Bitmap blackBitmap = BitmapFactory.decodeFile(blackPicture);
                Bitmap colorBitmap = BitmapFactory.decodeFile(colorPicture);

                if (score == -1) {//人脸识别超时
//                    sStatus += "人脸识别超时\n";
//                    check_result.setText(sStatus);
                    handler.sendEmptyMessage(5);
                    //比对策略或 且 未打开指纹比对
                } else if (score < MyApplication.comcompareScore - 20) {
//                    sStatus += "人脸识别失败:" + score + "\n";
//                    check_result.setText(sStatus);
                    handler.sendEmptyMessage(5);
//                    imgLeft.setImageBitmap(blackBitmap);
//                    imgRight.setImageBitmap(colorBitmap);
                } else {
//                    sStatus += "人脸识别成功:" + score + "\n";
//                    check_result.setText(sStatus);
////                    insertUser();
//                    imgLeft.setImageBitmap(blackBitmap);
//                    imgRight.setImageBitmap(colorBitmap);

                    //联网核查
//                    searchInfo(MyApplication.idCardInfo);
                }

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //    private Runnable runableValidFinger = new Runnable() {
//        @Override
//        public void run() {
//            validFingerTC();
////            threadStat = 0;//打开读卡线程
//            handler.post(runableReadIDCard);
//        }
//    };
    public void setMsg(String respose) {
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
//            e.printStackTrace();
        }
    }

    private void insertRisk(String messageObj) {
        String name = xm.getText().toString().trim();
        String idCardNum = idCard.getText().toString().trim();
        String phoneNumber = phoneNum.getText().toString().trim();
        RiskBean riskBean = new RiskBean();
        riskBean.setPhonenum(phoneNumber);
        riskBean.setCardNum(idCardNum);
        riskBean.setName(name);
        riskBean.setMessage(messageObj);
        riskBean.setCreateTime(DateUtil.getNow(DateUtil.FORMAT_LONG));
        DBManager dbManager = DBManager.getInstance(CxQuery.this);
        dbManager.addRisk(riskBean);
    }
}
