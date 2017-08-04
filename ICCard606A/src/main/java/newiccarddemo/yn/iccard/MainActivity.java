package newiccarddemo.yn.iccard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A606AReader;
import com.YinanSoft.CardReaders.A606Reader;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.CardReaders.SerialReaderIC;
import com.YinanSoft.Utils.DataUtils;
import com.YinanSoft.Utils.ToastUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity {
    BufferedWriter bufWriter;
    private static final String ONED_GPIO_FD = "/sys/class/misc/mtgpio/pin";
    String path = "YinanSoft";
    String filename = "armidse.bin";
    String server_url = "http://www.mineki.cn/armidse.bin";

    private String sPort = "/dev/ttyMT3";
    //波特率
    private int iBaudRate = 115200;

    //读卡器对象
    private SerialReaderIC serialReader;
    private IDCardReader idReader;
    byte[] bySendData;
    byte[] pRecvDatas = new byte[200];
    int[] nRecvLen = new int[1];
    byte[] bt2 = new byte[96];
    int nRet = -1;
    //展示返回的数据
    private TextView tv;
    private RadioGroup group;
    private RadioButton radio1;
    private RadioButton radio2;
    private EditText et_apdu;
    //选择的类型
    private String type = "01";
    //身份证信息
    private IDCardInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        tv = (TextView) findViewById(R.id.tv111);
        group = (RadioGroup) findViewById(R.id.radiogroup);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        et_apdu = (EditText) findViewById(R.id.et_apdu);
        PowerOnICCard();
//        PowerOnReader();
        //连接设备
        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
            sPort = "/dev/ttyS1";
        } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
            sPort = "/dev/ttyMT3";
        } else {
            sPort = "/dev/ttyS1";
        }
        serialReader = new SerialReaderIC(this, sPort, iBaudRate);
        boolean b = serialReader.InitReader(bt2);
        //初始化读卡器
        initCardReader();
//        idReader = new A606AReader(MainActivity.this);
//        boolean b1 = idReader.InitReader(null);
        if (b) {
            ToastUtil.showToast(this, "初始化成功");
        } else {
            ToastUtil.showToast(this, "初始化失败");
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        type = "01";
                        break;
                    case R.id.radio2:
                        type = "10";
                        break;
                }
            }
        });


    }


    /**
     * 读取身份证
     *
     * @param v
     */
    public void onReadClick(View v) {
        info = idReader.ReadAllCardInfo(new String[1]);
//        String s = idReader.ReadSAMID(new String[1]);
//        ToastUtil.showToast(MainActivity.this,s);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("读取身份证所有信息");
        if (info != null) {
            String sInfo = "姓名：" + info.getName() + "\n"
                    + "证件号码：" + info.getCardNum() + "\n"
                    + "性别：" + info.getGender() + "\n"
                    + "民族：" + info.getNation() + "\n"
                    + "生日：" + info.getBirthday() + "\n"
                    + "签发机关：" + info.getRegistInstitution() + "\n"
                    + "有效期：" + info.getValidStartDate() + "-" + info.getValidEndDate() + "\n";
            if (info.getNewAddress() != null && info.getNewAddress().length() > 0)
                sInfo += "最新地址：" + info.getNewAddress() + "\n";

            if (info.getFingerInfo() != null) {
                sInfo += "指纹：证件中含有指纹信息\n";
                ToastUtil.showToast(this, "读取成功，证件中含有指纹信息");
            } else {
                ToastUtil.showToast(this, "读取成功");
            }
            sInfo += "头像：\n";
            builder.setMessage(sInfo);

            ImageView imgView3 = new ImageView(this);
            imgView3.setImageBitmap(info.getPhoto());

            imgView3.setMaxHeight(126);
            imgView3.setMaxWidth(102);
            builder.setView(imgView3);
            builder.show();
        } else {
            ToastUtil.showToast(this, "读卡失败，请重新放证");
        }
        serialReader.ReleaseReader();
    }

    //判断接触式卡状态
    public void onStateClick(View v) {
        String data;
        if (type.equals("01")) {
            data = "322101";
        } else {
            data = "322110";
        }
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        String s = DataUtils.bytesToHexString(pRecvDatas);
        s = s.substring(0, nRecvLen[0] * 2);
        tv.setText(s);
        getResult(s);
    }

    //接触式卡上电
    public void powerOnClick(View v) {
        String data;
        if (type.equals("01")) {
//            data = "02000532220000011103";
            data = "3222000001";
        } else {
//            data = "02000532220000100003";
            data = "3222000010";
        }
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        String s = DataUtils.bytesToHexString(pRecvDatas);
        s = s.substring(0, nRecvLen[0] * 2);
        tv.setText(s);
        getResult(s);
    }

    //接触式卡下电
    public void powerOffClick(View v) {
        String data;
        if (type.equals("01")) {
//            data = "0200033223011003";
            data = "322301";
        } else {
            data = "322310";
//            data = "0200033223100103";
        }
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        String s = DataUtils.bytesToHexString(pRecvDatas);
        s = s.substring(0, nRecvLen[0] * 2);
        tv.setText(s);
        getResult(s);

    }

    //应用层命令传输
    public void onSendClick(View v) {
        //apdu 输入框
        String et_text = et_apdu.getText().toString().trim();
//        String apdu;
        String data;
//        String LRC = null;
        //判断不可为 奇数
        if (et_text.length() % 2 != 0) {
            ToastUtil.showToast(MainActivity.this, "您的输入有误!请检查!");
            return;
        }
        if (type.equals("01")) {
            data = "322601" + et_text;

        } else {
            data = "322610" + et_text;
        }
        //转为Byte 数组
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        String s = DataUtils.bytesToHexString(pRecvDatas);
        s = s.substring(0, nRecvLen[0] * 2);
        tv.setText(s);
        getResult(s);
    }

    /**
     * 获取随机数
     *
     * @param v
     */
    public void onRandomClick(View v) {
        String data;
        if (type.equals("01")) {
//            data = "02000500840000081003";
            data = "3226010084000008";
        } else {
            data = "3226100084000008";
//            data = "02000500840000080103";
        }
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        String s = DataUtils.bytesToHexString(pRecvDatas);
        s = s.substring(0, nRecvLen[0] * 2);
        tv.setText(s);
        getResult(s);
    }


    public void getResult(String strings) {
        if (strings.startsWith("0000")) {
            ToastUtil.showToast(MainActivity.this, "操作成功");
        } else if (strings.startsWith("1002")) {
            ToastUtil.showToast(MainActivity.this, "接触式用户卡未插到位");
        } else if (strings.startsWith("2002")) {
            ToastUtil.showToast(MainActivity.this, "未发现PSAM卡");
        } else if (strings.startsWith("1005")) {
            ToastUtil.showToast(MainActivity.this, "接触式用户卡上电失败");
        } else if (strings.startsWith("2001")) {
            ToastUtil.showToast(MainActivity.this, "不支持的PSAM卡");
        } else if (strings.startsWith("2005")) {
            ToastUtil.showToast(MainActivity.this, "PSAM卡上电失败");
        } else if (strings.startsWith("1004")) {
            ToastUtil.showToast(MainActivity.this, "非接触式卡未上电");
        } else if (strings.startsWith("2004")) {
            ToastUtil.showToast(MainActivity.this, "PSAM卡未上电");
        } else if (strings.startsWith("1007")) {
            ToastUtil.showToast(MainActivity.this, "操作非接触式卡错误");
        } else if (strings.startsWith("2007")) {
            ToastUtil.showToast(MainActivity.this, "操作PSAM卡错误");
        }
    }

    /**
     * 初始化
     */
    private void initCardReader() {
        if (idReader == null) {
            if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
                idReader = new A606Reader(MainActivity.this);
            } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
                idReader = new A606AReader(MainActivity.this);
            } else {
                idReader = new A606Reader(MainActivity.this);
            }
            PowerOnICCard();
            idReader.PowerOnReader();
            idReader.InitReader(null);
        }

    }

    /**
     */
    public void PowerOnICCard() {
        if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
            //606AIC  卡  上电
            try {
                bufWriter = new BufferedWriter(new FileWriter(ONED_GPIO_FD));
                bufWriter.write("-wdout61 1");//模块上电
                bufWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Log", "Exception idCardcharge");
            }
        } else {
            //606IC  卡  上电
            com.sunxi.hw.util.GPIOOutputHigh("out1");
            com.sunxi.hw.util.GPIOOutputLow("out0");
        }
    }

    public void PowerOnReader1() {

        try {
            bufWriter = new BufferedWriter(new FileWriter(ONED_GPIO_FD));
            bufWriter.write("-wdout64 1");//模块上电

            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Log", "Exception idCardcharge");
        }
    }

    public void PowerOffICCard() {
        if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
            try {
                bufWriter = new BufferedWriter(new FileWriter(ONED_GPIO_FD));
                bufWriter.write("-wdout61 0");//模块上电
                bufWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Log", "Exception idCardcharge");
            }
        } else {
            com.sunxi.hw.util.GPIOOutputLow("out1");
        }
    }

    public void PowerOffReader1() {
        //606A
        try {
            FileWriter localFileWriter = new FileWriter(new File("/sys/class/misc/mtgpio/pin"));
            localFileWriter.write("-wdout64 0");
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

    @Override
    protected void onResume() {
        super.onResume();
        initCardReader();
    }

    @Override
    protected void onStop() {
        super.onStop();
        String data;
        //下电
        if (type.equals("01")) {
            data = "322301";
        } else {
            data = "322310";
        }
        bySendData = DataUtils.hexStringToByte(data);
        //发送指令,并获取返回数据
        nRet = serialReader.TransApduCommand(5, bySendData, bySendData.length, pRecvDatas, nRecvLen);
        PowerOffICCard();
        idReader.PowerOffReader();
        idReader = null;
    }



}
