package yn.com.tfdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.Security.TFSecurity;

import java.io.File;

import yn.com.tfdemo.R;

public class MainActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private Button btn4, btn5, btn6;
    private TextView tv1, tv2, tv3, tv4, tv5;
    private EditText inputData;
    TFSecurity tSKF = new TFSecurity();
    private static byte[] Cipherdata = null;
    private int index = 0;
    /* 加密后的数据 密文 */
    String cipherData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();



        Cipherdata = new byte[100];

    }

    private void initView() {
        btn4 = (Button) findViewById(R.id.button4);
        btn5 = (Button) findViewById(R.id.button5);
        btn6 = (Button) findViewById(R.id.button6);

        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);

        inputData = (EditText) findViewById(R.id.inputData);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                // 加密
                int Cipherdatalength[] = new int[1];
                byte[] bytes = inputData.getText().toString().trim().getBytes();
                if (bytes.length < 1) {
                    Toast.makeText(this, "请输入加密数据!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv1.setText("加密明文:" + new String(bytes));
                tSKF.Encrypt(0, bytes, Cipherdata, Cipherdatalength);
                // 转换加密后的数据
                String bytesToHexString = bytesToHexString(Cipherdata);
                Log.e(TAG, "数据长度:" + Cipherdatalength[0]);
                // 截取加密会的数据
                bytesToHexString = bytesToHexString.substring(0, Cipherdatalength[0]);
                tv2.setText("加密密文:" + bytesToHexString);
                cipherData = bytesToHexString;
                Log.e(TAG, "加密结果:" + bytesToHexString);
                index++;
                break;
            case R.id.button5:
                // 解密
                if (TextUtils.isEmpty(cipherData)) {
                    Toast.makeText(this, "请先加密数据!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int newPlaindatalength[] = new int[1];
                byte[] newPlaindata = new byte[100];
                tv3.setText("解密密文:" + cipherData);
                byte[] cipherDataBytes = cipherData.getBytes();
                tSKF.Decrypt(0, cipherDataBytes, newPlaindata, newPlaindatalength);
                Log.e(TAG, "数据长度:" + newPlaindatalength[0]);
                tv4.setText("解密明文:" + new String(newPlaindata));
                Log.e(TAG, "解密密结果:" + bytesToHexString(newPlaindata).substring(0, newPlaindatalength[0] * 2));
                break;
            case R.id.button6:
                // 计算MAC
                // 数据长度 16 倍数
                String data = inputData.getText().toString().trim();
                if (TextUtils.isEmpty(data) || (data.length()) % 16 != 0) {
                    Toast.makeText(this, "输入MAC计算数据不正确!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] MACLength = new int[1];
                byte[] MAC = new byte[32];
                tSKF.Mac(0, data.getBytes(), MAC, MACLength);
                tv5.setText("MAC结果:" + new String(MAC));
                Log.e(TAG, "数据长度:" + MACLength[0] + "数据" + new String(MAC));
                break;

            default:
                break;
        }

    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
