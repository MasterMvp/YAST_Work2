package yn.iccard_ly;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yast.yadrly001.BtReaderClient;
import com.yast.yadrly001.IClientCallBack;
import com.yast.yadrly001.ToastUtil;

import org.w3c.dom.Text;

import java.util.Arrays;


public class MainActivity extends Activity {

    private BtReaderClient.People people;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BtReaderClient btReaderClient = new BtReaderClient(this);
    private String sMAC = "";
    //读卡器的连接状态
    private static boolean FLAG = false;
    private TextView tv;
    private RadioGroup raidoGroup;
    private RadioButton radio1;
    private RadioButton radio2;
    private EditText et_apdu;

    private static String cardType = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化蓝牙
        isBluetooth();
        tv = (TextView) findViewById(R.id.tv111);
        raidoGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        et_apdu = (EditText) findViewById(R.id.et_apdu);

        raidoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        cardType = "01";
                        break;
                    case R.id.radio2:
                        cardType = "10";
                        break;
                }
            }
        });


    }

    /**
     * 连接蓝牙设备
     *
     * @param v
     */
    public void onBluetoothClick(View v) {
        Intent serverIntent = new Intent(MainActivity.this, ListActivity.class);
        startActivityForResult(serverIntent, 1);
    }

    /**
     * 判断IC卡状态
     *
     * @param v
     */
    public void onStateClick(View v) {
        String[] strings = new String[2000];
        int[] nLens = new int[1];
        btReaderClient.TransApduData("3221" + cardType, strings, nLens, 3000);
        tv.setText(strings[0].substring(0, nLens[0] * 2) + "");
        getResult(strings);
    }

    /**
     * IC卡上电
     *
     * @param v
     */
    public void powerOnClick(View v) {
        String[] strings = new String[2000];
        int[] nLens = new int[1];
        btReaderClient.TransApduData("32220000" + cardType, strings, nLens, 3000);
        tv.setText(strings[0].substring(0, nLens[0] * 2) + "");
        getResult(strings);
    }

    /**
     * IC卡下电
     *
     * @param v
     */
    public void powerOffClick(View v) {
        String[] strings = new String[2000];
        int[] nLens = new int[1];
        btReaderClient.TransApduData("3223" + cardType, strings, nLens, 3000);
        tv.setText(strings[0].substring(0, nLens[0] * 2) + "");
        getResult(strings);
    }

    /**
     * 获取随机数
     *
     * @param v
     */
    public void onRandomClick(View v) {
        String[] strings = new String[2000];
        int[] nLens = new int[1];
        btReaderClient.TransApduData("3226" + cardType + "0084000008", strings, nLens, 3000);
        Log.d("Log", strings[0]);
        tv.setText(strings[0].substring(0, nLens[0] * 2) + "");
    }

    /**
     * IC卡发送IPDU
     *
     * @param v
     */
    public void onSendClick(View v) {
        String apdu = et_apdu.getText().toString().trim();
        if (apdu.length() > 0 && (apdu.length()) % 2 != 0) {
            ToastUtil.showToast(MainActivity.this, "APDU输入错误!");
            return;
        }
        String[] strings = new String[2000];
        int[] nLens = new int[1];
        btReaderClient.TransApduData("3226" + cardType + apdu, strings, nLens, 3000);
        tv.setText(strings[0].substring(0, nLens[0] * 2) + "");
        getResult(strings);
    }


    /**
     * 连接读卡器
     *
     * @param v
     */
    public void onConnectClick(View v) {
        if (sMAC.length() != 17) {
            ToastUtil.showToast(MainActivity.this, "请先连接蓝牙");
            return;
        }
        final ProgressDialog proDialog = new ProgressDialog(MainActivity.this);
        proDialog.setMessage("连接中...");
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setCancelable(false);
        proDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                int ref = btReaderClient.connectBt(2, sMAC, null);//
                btReaderClient.setCallBack(new IClientCallBack() {
                    @Override
                    public void onBtState(boolean b) {

                    }
                });
                if (ref == 0) {
                    FLAG = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            proDialog.dismiss();
                            ToastUtil.showToast(MainActivity.this, "连接成功");
                        }
                    });

                }
                if (ref != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            proDialog.dismiss();
                            ToastUtil.showToast(MainActivity.this, "连接失败");
                        }
                    });

                }
            }
        }.start();


    }

    /**
     * 断开读卡器
     *
     * @param v
     */
    public void onCloseClick(View v) {
        int ref = -1;
        if (FLAG) {
            ref = btReaderClient.closeIDCard();

            btReaderClient.setCallBack(new IClientCallBack() {
                @Override
                public void onBtState(boolean b) {

                }
            });


        }
        if (ref == 0) {
            FLAG = false;
            ToastUtil.showToast(MainActivity.this, "关闭连接");
        }

    }

    /**
     * 读卡
     *
     * @param v
     */
    public void onReadClick(View v) {
        getInfoTest();
    }


    public void getResult(String[] strings) {
        String result = strings[0];
        if (result.startsWith("0000")) {
            ToastUtil.showToast(MainActivity.this, "操作成功");
        } else if (result.startsWith("1002")) {
            ToastUtil.showToast(MainActivity.this, "接触式用户卡未插到位");
        } else if (result.startsWith("2002")) {
            ToastUtil.showToast(MainActivity.this, "未发现PSAM卡");
        } else if (result.startsWith("1005")) {
            ToastUtil.showToast(MainActivity.this, "接触式用户卡上电失败");
        } else if (result.startsWith("2001")) {
            ToastUtil.showToast(MainActivity.this, "不支持的PSAM卡");
        } else if (result.startsWith("2005")) {
            ToastUtil.showToast(MainActivity.this, "PSAM卡上电失败");
        } else if (result.startsWith("1004")) {
            ToastUtil.showToast(MainActivity.this, "非接触式卡未上电");
        } else if (result.startsWith("2004")) {
            ToastUtil.showToast(MainActivity.this, "PSAM卡未上电");
        } else if (result.startsWith("1007")) {
            ToastUtil.showToast(MainActivity.this, "操作非接触式卡错误");
        } else if (result.startsWith("2007")) {
            ToastUtil.showToast(MainActivity.this, "操作PSAM卡错误");
        }
    }


    private void getInfoTest() {
        if (sMAC.length() != 17) {
            ToastUtil.showToast(MainActivity.this, "请先连接蓝牙");
            return;
        }
        people = btReaderClient.readall();

        String sInfo = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        if (people != null) {
            sInfo += "\n姓名：" + people.getPeopleName();
            sInfo += "\n姓别：" + people.getPeopleSex();
            sInfo += "\n民族：" + people.getPeopleNation();
            sInfo += "\n出生日期：" + people.getPeopleBirthday();
            sInfo += "\n住址：" + people.getPeopleAddress();
            sInfo += "\n身份证号：" + people.getPeopleIDCode();
            sInfo += "\n签发机关：" + people.getDepartment();
            sInfo += "\n有效期限：" + people.getStartDate() + "-" + people.getEndDate();
            sInfo += "\n头像：\n";
            builder.setTitle("读取证件信息成功");
            builder.setMessage(sInfo);
            Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0, people.getPhoto().length);

            ImageView imgView3 = new ImageView(this);
            imgView3.setImageBitmap(photo);

            imgView3.setMaxHeight(300);
            imgView3.setMaxWidth(300);
            builder.setView(imgView3);
        } else {
            builder.setTitle("读取证件信息错误");
            builder.setMessage("读取证件信息错误");
        }
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 2:
                if (requestCode == 1) {
                    sMAC = data.getExtras().getString("address");
                } else if (resultCode == RESULT_CANCELED) {
                    ToastUtil.showToast(this, "蓝牙已禁用");
                }
                break;
            default:
                break;
        }
    }


    /**
     * 初始化蓝牙
     */
    public void isBluetooth() {

        //判断是否有蓝牙驱动
        if (btAdapter == null) {
            Toast.makeText(this, "没有找到蓝牙驱动", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //判断蓝牙是否打开,未打开    直接打开
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
        }
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            }
        }
    }

    //权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
