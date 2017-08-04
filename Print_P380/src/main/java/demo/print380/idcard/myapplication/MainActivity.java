package demo.print380.idcard.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.CardReaders.SerialReader;
import com.YinanSoft.Utils.DataUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

import beans.UserBean;
import printpp.printpp_yt.PrintPP_CPCL;
import utils.CertImgDisposeUtils;
import utils.ToBackBitmap;
import utils.ToastUtil;
import utils.Util;

public class MainActivity extends AppCompatActivity {
    //预览图片
    private ImageView act_photo;
    //蓝牙信息
    private TextView mes;
    //打印机信息
    private TextView print;
    //打印文字
    private Button printText;
    //打印照片
    private Button printPhoto;
    //刷新按钮
    private Button refresh;
    //输出操作的对象
    PrintPP_CPCL iPrinter;
    //驱动地址
    private String deviceAddress;
    //驱动名称
    private String deviceName;
    //身份证信息对象
    private IDCardInfo idCardInfo;
    //    private SoundPool soundPool;
    private MediaPlayer mp;


    private final static int DataType_Text = 0;
    private final static int DataType_Photo = 1;
    private final static int DataType_IDCard = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        iPrinter = new PrintPP_CPCL();
        mp = new MediaPlayer();
        //初始化视图
        initView();
        //初始化蓝牙
        initBuletooth();
    }


    /**
     * 初始化连接
     */
    private void initBuletooth() {
        final ProgressDialog proDialog = new ProgressDialog(MainActivity.this);
        proDialog.setMessage("刷新中...");
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setCancelable(false);
        proDialog.show();
        refresh.setEnabled(false);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否有蓝牙驱动
        if (btAdapter == null) {
            Toast.makeText(this, "没有找到蓝牙驱动", Toast.LENGTH_SHORT).show();
            refresh.setEnabled(true);
            //关闭进度条
            proDialog.dismiss();
            finish();
            return;
        }
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
            if (btAdapter.getState() == 11 || btAdapter.getState() == 12) {
            } else {
                mes.setText("未连接");
                //关闭进度条
                proDialog.dismiss();
                refresh.setEnabled(true);
                return;
            }
        }
        //获取蓝牙驱动,放入集合
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        //遍历所有蓝牙驱动
        for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
            final BluetoothDevice device = iterator.next();
            //对比蓝牙驱动地址
            if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING) {
                if (device != null) {
                    mes.setText(" [" + device.getAddress() + "]");
                }
                //断开打印机
                if (!iPrinter.isConnected()) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            deviceAddress = device.getAddress();
                            deviceName = device.getName();
                            //连接打印机
                            iPrinter.connect(deviceName, deviceAddress);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //关闭进度条
                                    proDialog.dismiss();
                                    //刷新按钮设置允许被点击
                                    refresh.setEnabled(true);
                                    if (iPrinter.isConnected()) {
                                        //连接成功
                                        Toast.makeText(MainActivity.this, "连接设备成功!", Toast.LENGTH_SHORT).show();
                                        print.setText("已连接");
                                    } else {
                                        //连接失败
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                        dialog.setMessage("连接失败,是否手动连接?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                                //用于刷新状态
                                                initBuletooth();
                                            }
                                        }).show();
                                        print.setText("未连接");
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "连接设备成功!", Toast.LENGTH_SHORT).show();
                    //关闭进度条
                    proDialog.dismiss();
                    //刷新按钮设置允许被点击
                    refresh.setEnabled(true);
                }

            }
        }
    }


    /**
     * 初始化视图
     */
    private void initView() {
        //初始化控件
        refresh = (Button) findViewById(R.id.act_refresh);
        act_photo = (ImageView) findViewById(R.id.act_photo);
        mes = (TextView) findViewById(R.id.act_msg);
        printText = (Button) findViewById(R.id.act_printText);
        print = (TextView) findViewById(R.id.print);
    }

    /**
     * 设备状态 刷新
     *
     * @param v
     */
    public void onRefreshClick(View v) {
        initBuletooth();
    }

    /**
     * 打印文字 点击事件
     *
     * @param v
     */
    public void onTextClick(View v) {
        StartPrint(DataType_Text);
    }


    /**
     * 开始 打印
     *
     * @param type 打印类型
     *             DataType_Text 打印文字
     *             DataType_IDCard  读取身份证
     * @return
     */
    int StartPrint(final int type) {
        //判断连接状态
        int ret = -1;
        if (!iPrinter.isConnected()) {
            Toast.makeText(this, "与设备失去连接...", Toast.LENGTH_SHORT).show();
            return -1;
        }
        //子线程去打印
        new Thread(new Runnable() {

            @Override
            public void run() {
                switch (type) {
                    case DataType_Text:
                        sendPrintKD();
//                        sendPrintText();
                        iPrinter.print(1, 0);
                        iPrinter.feed();
//                        iPrinter.setPaperFeedLength(2);
                        break;
                    case DataType_IDCard:
                        try {
                            getIdCardMsg();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (idCardInfo == null) {
                            return;
                        }
                        break;
                    default:
                        break;
                }

            }
        }).start();

        ret = 0;
        return ret;
    }

    /**
     * 读取身份证信息
     *
     * @param v
     */
    public void onReadIDCardClick(View v) {
        //禁止被点击
        v.setEnabled(false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //允许被点击
        v.setEnabled(true);
        //开始异步打印
        StartPrint(DataType_IDCard);
//        sendPrintIDCard();
    }

    public static int flag = 0;

    public void getIdCardMsg() throws IOException {
        byte[] bytes = null;
        bytes = iPrinter.readIdcard2();
//        if (bytes != null && bytes.length > 0) {
//            bytes = iPrinter.readIdcard2();
//        }
        if (flag == 0) {
            flag = 1;
            getIdCardMsg();
            return;
        }
        if (bytes != null) {
            Log.e("Log", "有数据拉,长度:" + bytes.length);
            //十六进制转字符串
            String s = bytesToHexString(bytes);
            Log.e("Log", "数据" + s);
            if (s.length() < 1) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getIdCardMsg();
                Log.e("Log", "重新执行getIdCardMsg()");
                return;
            }
//            截取字符串
            if (s.length() > 0) {
                String substring = s.substring(s.length() - 4, s.length());
                Log.e("Log", "截取最后四位" + substring);

                if (substring.equals("8185") || substring.equals("4145") || substring.equals("009C") || substring.equals("6556")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mp.reset();
                            mp = MediaPlayer.create(MainActivity.this, R.raw.failure);
                            mp.start();
                            flag = 0;
                            idCardInfo = null;
                            ToastUtil.showToast(MainActivity.this, "读取失败,请重试!");
                            act_photo.setVisibility(View.INVISIBLE);
                        }
                    });
                    return;
                }
                //截取 有用的 身份证信息
                s = s.substring(s.indexOf("AAAAAA96690508000090") + 20, s.length());
                //String 转为16进制 数组
                byte[] bytes1 = hexStringToByte(s);
                //解析 数组对象
                SerialReader serial = new SerialReader(MainActivity.this);
//                serial.InitReader(null);
                //获取 身份证信息
                if (bytes1.length < 1) {
                    return;
                }
                idCardInfo = null;
                idCardInfo = serial.decodeInfo(bytes1, false, true);
                if (idCardInfo == null) {
                    return;
                }

                Log.e("Log", "姓名" + idCardInfo.getName());

                CertImgDisposeUtils certImg = new CertImgDisposeUtils(MainActivity.this);

                //使用 获取到的身份证信息 得到Bitmap对象
                Bitmap photo = idCardInfo.getPhoto();
                bytes = null;
                bytes1 = null;
                s = "";
                if (photo != null) {
                    //合成身份证正面
                    final Bitmap bitmap = certImg.creatBitmap(idCardInfo);
                    idCardInfo = null;
                    //展示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act_photo.setVisibility(View.VISIBLE);
                            mp.reset();
                            mp = MediaPlayer.create(MainActivity.this, R.raw.success);
                            mp.start();
                            flag = 0;
                            ToastUtil.showToast(MainActivity.this, "读取成功!");
                            act_photo.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(MainActivity.this, "读取失败,请检查设备!");
                }
            });
            Log.e("Log", "没有数据");
        }
    }

    /**
     * String 转byte
     *
     * @param hex
     * @return
     */
    private byte[] hexStringToByte(String hex) {
        if (hex == null || hex.equals("")) {
            return null;
        }
        hex = hex.toUpperCase();
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 十六进制转String
     *
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);// .toUpperCase()
        }
        return sb.toString().toUpperCase();
    }


    /**
     * 打印面单
     */
    public void sendPrintKD() {
        iPrinter.pageSetup(568, 2000);
        if (idCardInfo == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(MainActivity.this, "请先读取身份证");
                }
            });
            return;
        }
        /**
         * 准备  最后的身份证号码
         */
        String cardNum = idCardInfo.getCardNum();
        String newCardNum = idCardInfo.getCardNum();
        cardNum = cardNum.substring(6, 14);
        Log.i("Log", "截取后的字符串:" + cardNum);
        newCardNum = newCardNum.replace(cardNum, "********");


        //第一联
        iPrinter.drawBox(2, 2 + 4 + 4, 1 + 30, 566, 256 + 128 + 168 + 128 + 64 - 90); //第一联边框
        iPrinter.drawLine(2, 2 + 4 + 4, 170, 566, 170, false);//第一联横线1
        iPrinter.drawLine(2, 2 + 4 + 4, 294, 566, 294, false);//第一联横线2
        iPrinter.drawLine(2, 2 + 4 + 4, 458, 566 - 32, 458, false);//第一联横线3
        iPrinter.drawLine(2, 40 + 4 + 4, 294, 40 + 4 + 4, 680 + 4 + 64 - 90, false);//第一联竖线1，从左到右
        iPrinter.drawLine(2, 2 + 408 + 4 + 4, 552 - 28 - 90 + 29, 2 + 408 + 4 + 4, 680 + 64 - 90, false);//第一联竖线2，从左到右
        iPrinter.drawLine(2, 566 - 32, 384 - 90, 566 - 32, 680 + 64 - 90, false);//第一联竖线3，从左到右
        //二维码信息
        iPrinter.drawQrCode(2 + 160, 16 + 30, "www.yto.net.cn", 0, 2, 5);
        iPrinter.drawText(2 + 320, 16 + 20 + 8, "代收货款", 3, 0, 1, false, false);
        //金额
        iPrinter.drawText(2 + 320, 48 + 8 + 30 + 8, "金额：", 3, 0, 0, false, false);
        //具体金额
        iPrinter.drawText(2 + 8 + 400, 48 + 8 + 30 + 8, "0.0元", 3, 0, 1, false, false);
        //条码
        iPrinter.drawBarCode(2 + 160, 240 + 16 + 32 - 50 - 50, "858691130534", 1, 0, 3, 80);
        //条码字符
        iPrinter.drawText(2 + 96 + 76 + 32, 265, "858691130534", 3, 0, 0, false, false);
        //收件人
        iPrinter.drawText(2 + 4 + 4 + 4, 384 + 28 - 90, 32, 120, "收件人", 3, 0, 1, false, false);
        //收件人姓名＋电话，最终实施时请用变量替换
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 264 + 128 - 90, 480, 32, "程远远" + " " + "18721088532" + "  " + "", 3, 0, 1, false, false);
        //收件地址 ，最终实施时请用变量替换
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 372 + 40 + 22 - 90, 448, 120, "北京北京市朝阳区 北京曹威风威风威风 为氛围分为氛围阳曲", 3, 0, 1, false, false);
        //寄件人
        iPrinter.drawText(2 + 8 + 4 + 4, 552 + 22 + 32 - 70, 32, 96, "寄件人", 2, 0, 0, false, false);
        //寄件人姓名＋电话，
        if (idCardInfo != null) {
            iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 552 + 8 - 90, 480, 24, idCardInfo.getName(), 2, 0, 0, false, false);
        }
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 552 + 40 - 90, 344, 112, "     13512345678", 2, 0, 0, false, false);
        //寄件人地址
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 552 + 72 - 90, 344, 112, idCardInfo.getAddress(), 2, 0, 0, false, false);
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 552 + 72 + 32 + 16 - 90 + 32, 344, 112, newCardNum, 2, 0, 0, false, false);
        //签收人
        iPrinter.drawText(2 + 424, 552 + 8 - 90 + 32, "签收人：", 2, 0, 0, false, false);
        //日期
        iPrinter.drawText(2 + 424, 680 - 90 - 26 + 32, "日期：", 2, 0, 0, false, false);
        //派件联
        iPrinter.drawText(566 - 32 + 3, 384 - 90 + 128, 32, 96, "派件联", 2, 0, 0, false, false);


//        第二联
        iPrinter.drawBox(2, 2 + 4 + 4, 680 + 16 + 32 + 64 - 70, 566, 680 + 16 + 288 + 32 + 64 - 90);//第二联边框
//        iPrinter.drawLine(2, 2 + 4 + 4, 696 + 32 + 32+64, 566, 696 + 32 + 32+64, false);//第二联横线1，从左到右
        iPrinter.drawLine(2, 2 + 4 + 4, 696 + 160 + 32 + 64 - 100, 566 - 32, 696 + 160 + 32 + 64 - 100, false);//第二联横线2，从左到右
        iPrinter.drawLine(2, 2 + 32 + 4 + 4, 696 + 160 + 96 + 32 + 64 - 100, 566 - 32, 696 + 160 + 96 + 32 + 64 - 100, false);//第二联横线3，从左到右
        iPrinter.drawLine(2, 2 + 32 + 4 + 4, 696 + 64 + 32 - 70, 2 + 32 + 4 + 4, 680 + 16 + 288 + 32 + 64 - 100, false);//第二联竖线1，从左到右
        iPrinter.drawLine(2, 248 + 42 + 4 + 4, 680 + 16 + 288 + 32 + 64 - 32 - 100, 248 + 42 + 4 + 4, 680 + 16 + 288 + 32 + 64 - 100, false);//第二联竖线2，从左到右
        iPrinter.drawLine(2, 566 - 32, 696 + 64 + 32 - 70, 566 - 32, 680 + 16 + 288 + 32 + 64 - 100, false);//第二联竖线3，从左到右
        //运单号+运单号
        iPrinter.drawText(2 + 8 + 4 + 4, 696 + 3 + 32 + 32 - 70, "运单号：" + "858691130534" + "  " + "订单号：" + "DD00000014486", 2, 0, 0, false, false);
        //收件人
        iPrinter.drawText(2 + 8 + 4 + 4, 696 + 32 + 16 + 64 + 32 - 80, 32, 96 + 32, "收件人", 2, 0, 0, false, false);
        //收件人姓名＋电话，最终实施时请用变量替换
        iPrinter.drawText(2 + 8 + 32 + 8 + 4 + 4, 608 + 128 + 64 - 80, 480, 24 + 32, "程远远" + " " + "18721088532" + "  " + "", 2, 0, 0, false, false);
        //收件地址 ，最终实施时请用变量替换
        iPrinter.drawText(2 + 8 + 32 + 8 + 4 + 4, 696 + 40 + 2 + 32 + 64 - 90 + 32, 424, 80, "北京北京市朝阳区 北京曹威风威风威风 为氛围分为氛围阳曲", 2, 0, 0, false, false);
        iPrinter.drawText(2 + 8 + 4 + 4, 696 + 160 + 3 + 32 + 64 - 90, 32, 120, "内容品名", 2, 0, 0, false, false);
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 696 + 160 + 8 + 32 + 64 - 80, 432, 136, "0", 2, 0, 0, false, false);
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 696 + 160 + 96 + 4 + 32 + 64 - 100, "数量：" + "1", 2, 0, 0, false, false);
        iPrinter.drawText(2 + 410, 696 + 160 + 96 + 4 + 32 + 64 - 100, "重量：" + "0" + "kg", 2, 0, 0, false, false);
        iPrinter.drawText(566 - 32 + 3, 696 + 32 + 80 + 32 + 64 - 80 - 10, 32, 96, "收件联", 2, 0, 0, false, false);

        //第三联
        iPrinter.drawBox(2, 2 + 4 + 4, 1000, 566, 1000 + 432 - 25);//第三联边框
        iPrinter.drawLine(2, 2 + 4 + 4, 1096 - 20, 566, 1096 - 20, false);//第三联横线1，从左到右
        iPrinter.drawLine(2, 2 + 4 + 4, 1096 + 104 - 8 - 20, 566 - 32, 1096 + 104 - 8 - 20, false);//第三联横线2，从左到右
        iPrinter.drawLine(2, 2 + 4 + 4, 1096 + 104 + 104 - 8 - 20 + 16, 566 - 32, 1096 + 104 + 104 - 8 - 20 + 16, false);//第三联横线3，从左到右
        iPrinter.drawLine(2, 2 + 32 + 4 + 4, 1096 + 104 + 104 + 96 + 4 - 4 - 2 - 8 - 4 - 20, 566 - 32, 1096 + 104 + 104 + 96 + 4 - 4 - 2 - 8 - 4 - 20, false);//第三联横线4，从左到右
        iPrinter.drawLine(2, 2 + 32 + 4 + 4 - 4, 1096 - 20, 2 + 32 + 4 + 4 - 4, 1432 - 4 - 16 - 20, false);//第三联竖线1，从左到右
        iPrinter.drawLine(2, 248 + 42 + 4 + 4, 1096 + 104 + 104 + 96 - 8 - 20, 248 + 42 + 4 + 4, 1432 - 4 - 16 - 20, false);//第三联竖线2，从左到右
        iPrinter.drawLine(2, 566 - 32, 1096 - 20, 566 - 32, 1432 - 4 - 16 - 20, false);//第三联竖线3，从左到右
        //
        iPrinter.drawBarCode(2 + 250 + 4, 1000 + 8, "858691130534", 1, 0, 3, 56);
        //条码数据
        iPrinter.drawText(2 + 8 + 50, 1008 + 4, "858691130534", 3, 0, 0, false, false);
        //收件人
        iPrinter.drawText(2 + 8 + 4, 1096 + 5 - 20, 32, 96, "收件人", 2, 0, 0, false, false);
        //收件人姓名＋电话，最终实施时请用变量替换
        iPrinter.drawText(2 + 8 + 32 + 8 + 4 + 4, 1096 + 8 - 20, 480, 24, "程远远" + " " + "18721088532" + "  " + "", 2, 0, 0, false, false);
        //收件地址 ，最终实施时请用变量替换
        iPrinter.drawText(2 + 8 + 32 + 8 + 4 + 4, 1096 + 10, 456, 64 + 160, "北京北京市朝阳区 北京曹威风威风威风 为氛围分为氛围阳曲", 2, 0, 0, false, false);

        //寄件人
        iPrinter.drawText(2 + 8 + 4, 1096 + 104 + 5 - 20, 32, 96, "寄件人", 2, 0, 0, false, false);
        //寄件人姓名＋电话，
        if (idCardInfo != null) {
            iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 1096 + 104 + 8 - 20, 480, 24, idCardInfo.getName(), 2, 0, 0, false, false);
        }
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4 + 200, 1096 + 104 + 8 - 20, 480, 24, "     13512345678", 2, 0, 0, false, false);
        //寄件人地址
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 1096 + 104 + 8 + 24 + 8 - 20, 456, 72, idCardInfo.getAddress(), 2, 0, 0, false, false);

        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 1096 + 104 + 8 + 24 + 8 + 64 - 32 - 20 + 16, 456, 72, newCardNum, 2, 0, 0, false, false);
        //内容品名
        iPrinter.drawText(2 + 8 + 4, 1096 + 104 + 104 + 1 - 20 + 16, 32, 120, "内容", 2, 0, 0, false, false);
        //订单号
        //	iPrinter.drawText(2+4+32+8,1348,"订单号："+mOrderVO.getOrderNo(),2,0, 0,false,false);
        //内容品名具体
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 1096 + 104 + 104 + 8 - 20 + 16, 432, 156, "0", 2, 0, 0, false, false);
        //数量
        iPrinter.drawText(2 + 4 + 32 + 8 + 4 + 4, 1432 - 32 + 4 - 4 - 8 - 4 - 20, "数量：" + "1", 2, 0, 0, false, false);
        //重量
        iPrinter.drawText(2 + 400, 1432 - 32 + 4 - 4 - 8 - 4 - 20, "重量：" + "0" + "kg", 2, 0, 0, false, false);
        //寄件联
        iPrinter.drawText(566 - 32 + 3, 1096 + 104 + 16 - 20, 32, 96, "寄件联", 2, 0, 0, false, false);

    }

    /**
     * 使用广播 接收  蓝牙是否断开,
     */
    private static final String DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED";

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DISCONNECTED);
        registerReceiver(dynamicReceiver, dynamic_filter);
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DISCONNECTED)) {
                iPrinter.disconnect();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dynamicReceiver);
    }
}
