package ly006.yn.read;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yast.yadrly001.BtReaderClient;
import com.yast.yadrly001.IClientCallBack;
import com.yast.yadrly001.ToastUtil;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private static BtReaderClient.People people;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BtReaderClient btReaderClient = new BtReaderClient(this);
    private String sMAC = "";
    //读卡器的连接状态
    private static boolean FLAG = false;

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        //初始化蓝牙
        isBluetooth();


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


    public void onSamidClick(View v) {
        TextView tv = (TextView) findViewById(R.id.tvMsg);
        String readsamid = btReaderClient.readsamid();
        tv.setText("模块号：" + readsamid);
        Log.e("Log", "模块号" + readsamid);
    }

    /**
     * 断开读卡器
     *
     * @param v
     */
    public void onCloseClick(View v) {
//        String readsamid = btReaderClient.readsamid();
//        Log.e("Log","模块号"+readsamid);
        boolean b = false;
        int ref = -1;
        if (FLAG) {
//            ref = btReaderClient.closeIDCard();
            b = btReaderClient.disconnectBt();
            btReaderClient.setCallBack(new IClientCallBack() {
                @Override
                public void onBtState(boolean b) {
                }
            });
        }
        if (b) {
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

    private void getInfoTest() {
        if (sMAC.length() != 17) {
            ToastUtil.showToast(MainActivity.this, "请先连接蓝牙");
            return;
        }
//        people = btReaderClient.readall();
        people = btReaderClient.read();
//        String sInfo = "";
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//        if (people != null) {
//            sInfo += "\n姓名：" + people.getPeopleName();
//            sInfo += "\n姓别：" + people.getPeopleSex();
//            sInfo += "\n民族：" + people.getPeopleNation();
//            sInfo += "\n出生日期：" + people.getPeopleBirthday();
//            sInfo += "\n住址：" + people.getPeopleAddress();
//            sInfo += "\n身份证号：" + people.getPeopleIDCode();
//            sInfo += "\n签发机关：" + people.getDepartment();
//            sInfo += "\n有效期限：" + people.getStartDate() + "-" + people.getEndDate();
//            if (people.getIsfalsefinger()) {
//                sInfo += "\n该身份证有指纹信息\n";
//            }
//            sInfo += "\n头像：\n";
//            builder.setTitle("读取证件信息成功");
//            builder.setMessage(sInfo);
//            if (people.getPhoto() != null) {
//                Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0, people.getPhoto().length);
//
//                ImageView imgView3 = new ImageView(this);
//                imgView3.setImageBitmap(photo);
//
//                imgView3.setMaxHeight(300);
//                imgView3.setMaxWidth(300);
//                builder.setView(imgView3);
////                ToastUtil.showToast(MainActivity.this, "读取失败");
//            }
//
//        } else {
//            builder.setTitle("读取证件信息错误");
//            builder.setMessage("读取证件信息错误");
//        }
//        builder.show();


        CertImgDisposeUtils certImg = new CertImgDisposeUtils(MainActivity.this);
        IDCardInfo cardInfo = new IDCardInfo();
        if (people != null) {
            cardInfo.setAddress(people.getPeopleAddress());
            cardInfo.setBirthday(people.getPeopleBirthday());
            cardInfo.setCardNum(people.getPeopleIDCode());
            cardInfo.setGender(people.getPeopleSex());
            cardInfo.setNation(people.getPeopleNation());
            cardInfo.setName(people.getPeopleName());
            cardInfo.setNewAddress(people.getPeopleAddress());
            if (people.getPhoto() == null) {
                ToastUtil.showToast(MainActivity.this, "读取失败");
            }
            cardInfo.setPhoto(Bytes2Bimap(people.getPhoto()));
            cardInfo.setValidStartDate(people.getStartDate());
            cardInfo.setValidEndDate(people.getEndDate());
            cardInfo.setId(1);
            try {
                Bitmap bitmap = certImg.creatBitmap(cardInfo);
                img.setImageBitmap(bitmap);
                ToastUtil.showToast(MainActivity.this, "读取成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            img.setImageResource(R.drawable.zm);
            ToastUtil.showToast(MainActivity.this, "读取失败");
        }

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


    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
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
