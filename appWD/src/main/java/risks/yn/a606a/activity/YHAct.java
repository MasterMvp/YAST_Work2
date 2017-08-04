package risks.yn.a606a.activity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.YinanSoft.Utils.ToastUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.hdcloudwalk.httprequest.HDHttpPhonePost;
import cn.hdcloudwalk.httprequest.HDHttpYH3Post;
import cn.hdcloudwalk.httprequest.HDHttpYH4Post;
import risks.yn.a606a.R;

public class YHAct extends AppCompatActivity implements View.OnClickListener {
    private TextView yh_tv;
    private Button yh_btn1, yh_btn2, yh_btn3;
    public static final String TAG = "YHAct";
    private Spinner yh_spinner;
    private EditText yh_name, yh_idcard, yh_kh, yh_phoneNum;
    private String[] strings = {"电信", "联通", "移动"};
    private static String dhyys = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_yh);
        initView();
    }

    private void initView() {
        yh_btn1 = (Button) findViewById(R.id.yh_btn1);
        yh_btn2 = (Button) findViewById(R.id.yh_btn2);
        yh_btn3 = (Button) findViewById(R.id.yh_btn3);
        yh_spinner = (Spinner) findViewById(R.id.yh_spinner);
        yh_tv = (TextView) findViewById(R.id.yh_tv);

        yh_name = (EditText) findViewById(R.id.yh_name);
        yh_name.setText("赵金彪");
        yh_idcard = (EditText) findViewById(R.id.yh_idcard);
        yh_idcard.setText("232321199510252915");
        yh_kh = (EditText) findViewById(R.id.yh_kh);
        yh_kh.setText("6217000450002254374");
        yh_phoneNum = (EditText) findViewById(R.id.yh_phoneNum);
        yh_phoneNum.setText("15210232964");

        yh_btn1.setOnClickListener(this);
        yh_btn2.setOnClickListener(this);
        yh_btn3.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
        yh_spinner.setSelection(0);
        yh_spinner.setAdapter(adapter);
        yh_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        dhyys = "1";
                        break;
                    case 1:
                        dhyys = "2";
                        break;
                    case 2:
                        dhyys = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        String name = yh_name.getText().toString().trim();
        String phoneNum = yh_phoneNum.getText().toString().trim();
        String cardNum = yh_kh.getText().toString().trim();
        String idCardNum = yh_idcard.getText().toString().trim();
        String mothedPhoneName = "checkMobilePhone";

//        String name = "郭莉莉";
//        String phoneNum = "13180792606";
//        String cardNum = "6222601210005596558";
//        String idCardNum = "350622198708290523";
//        String mothedPhoneName = "checkMobilePhone";

//        String name1 = "赵金彪";
//        String phoneNum1 = "15210232964";
//        String idCardNum1 = "232321199510252915";
//        String cardNum1 = "6217000450002254374";
//        String dhyys1 = "3";
        String mothedName = "checkBankCard";

        switch (v.getId()) {
            case R.id.yh_btn1://银行四要素
                HDHttpYH4Post request = new HDHttpYH4Post(this, idCardNum, name, cardNum, phoneNum, "", handler, mothedName);
                request.execute();
                break;
            case R.id.yh_btn2://银行三要素
                HDHttpYH3Post request3 = new HDHttpYH3Post(this, idCardNum, name, cardNum, "", handler, mothedName);
                request3.execute();
                break;
            case R.id.yh_btn3://手机号确认
                HDHttpPhonePost requestPhone = new HDHttpPhonePost(this, idCardNum, name, phoneNum, dhyys, "", handler, mothedPhoneName);
                requestPhone.execute();
                break;
        }


//        HDHttpYH3Post request = new HDHttpYH3Post(this, idCardNum1, name1, cardNum1, "", handler, mothedName);
//        HDHttpYH4Post request = new HDHttpYH4Post(this, idCardNum, name, cardNum, phoneNum, "", handler, mothedName);
        //手机号  其他数据可以查询
//        HDHttpPhonePost request = new HDHttpPhonePost(this, idCardNum1, name1, phoneNum1, dhyys1, "", handler, mothedName);
//        request.execute();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    String ret = (String) msg.obj;
                    Log.e(TAG, "Handler10:" + ret);
                    if (ret.length() > 0) {
                        String retMsg = getSelectedNodeValue(ret);
                        ToastUtil.showToast(YHAct.this, retMsg);
                        yh_tv.setText(retMsg);
                    }
                    break;
                case 11:
                    String ret1 = (String) msg.obj;
                    Log.e(TAG, "Handler11:" + ret1);
                    ToastUtil.showToast(YHAct.this, "请确认网络连接正常!");
                    yh_tv.setText("请确认网络连接正常!");
                    break;
                case  13:
                    String ret13 = (String) msg.obj;
                    Log.e(TAG, "Handler10:" + ret13);
                    if (ret13.length() > 0) {
                        String retMsg = getSelectedNodeValue(ret13);
                        ToastUtil.showToast(YHAct.this, retMsg);
                        yh_tv.setText(retMsg);
                    }
                    break;
                default:
                    yh_tv.setText((String) msg.obj);
                    Log.e(TAG,(String) msg.obj);
                    break;
            }
        }
    };

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


}
