package attendance.yn.a606a.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A606AReader;
import com.YinanSoft.CardReaders.A606LReader;
import com.YinanSoft.CardReaders.IDCardReader;
import com.techshino.fingerprint.FingerExt;

import java.util.Timer;
import java.util.TimerTask;

import attendance.yn.a606a.MyApplication;
import attendance.yn.a606a.R;
import attendance.yn.a606a.dialog.CustomDialog;
import attendance.yn.a606a.utils.ToastUtils;

public class Main2Activity extends Activity implements View.OnClickListener {
    private String TAG = "Main2Activity";
    public static FingerExt fingerExt;
    public static IDCardReader idReader = null;
    private GridView grid;
    String[] title_res = {"上班打卡", "员工录入", "员工查询", "考勤查询"};
    int[] img_res = {R.drawable.kaoqin, R.drawable.luru, R.drawable.renyuan, R.drawable.jilu};
    Intent intent = null;
    private ImageView ll_sbdk, ll_yglr, ll_ygcx, ll_kqcx;
    private ImageView exit;
    private static boolean mBackKeyPressed = false;//记录是否有首次按键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_main3);
        init();

    }

    private int count = 0;

    private void init() {
        exit = (ImageView) findViewById(R.id.exit);
        ll_sbdk = (ImageView) findViewById(R.id.ll_sbdk);
        ll_yglr = (ImageView) findViewById(R.id.ll_yglr);
        ll_ygcx = (ImageView) findViewById(R.id.ll_ygcx);
        ll_kqcx = (ImageView) findViewById(R.id.ll_kqcx);
        ll_sbdk.setOnClickListener(this);
        ll_yglr.setOnClickListener(this);
        ll_ygcx.setOnClickListener(this);
        ll_kqcx.setOnClickListener(this);
        //退出到设置页面
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBackKeyPressed) {
                    mBackKeyPressed = true;
                    new Timer().schedule(new TimerTask() {//延时两秒，如果超出则擦错第一次按键记录
                        @Override
                        public void run() {
                            mBackKeyPressed = false;
                        }
                    }, 1000);
                } else {//退出程序
                    mBackKeyPressed = false;
                    final CustomDialog customDialog = new CustomDialog(Main2Activity.this);
                    customDialog.show();
                    customDialog.setClicklistener(new CustomDialog.ClickListenerInterface() {
                        @Override
                        public void upload(EditText et) {
                            String password = et.getText().toString();
                            if ("".equals(password)) {
                                ToastUtils.showToast(Main2Activity.this, "请输入密码");
                                return;
                            }
                            if (!password.equals(MyApplication.PASSWORD)) {
                                ToastUtils.showToast(Main2Activity.this, "密码输入错误");
                                return;
                            }
                            customDialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
//        Log.e("Main2Activity", "onStart");
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCardReader();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("Main2Activity", "onResume");
        //电容指纹
//        if (fingerExt == null || MyApplication.initFingerSuccess == false) {


        fingerExt = new FingerExt(getApplicationContext());
        new Thread(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                initFingerPrinter(); //电容指纹
                while (!MyApplication.initFingerSuccess) {
                    if (count++ >= 3) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!MyApplication.initFingerSuccess) {
                    initFingerPrinter(); //电容指纹
                }
            }
        }).start();

        
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sbdk:
                //打卡
                intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_yglr:
                //录入
                intent = new Intent(Main2Activity.this, AddUserAct.class);
                startActivity(intent);
                break;
            case R.id.ll_ygcx:
                //员工查询
                intent = new Intent(Main2Activity.this, QueryAct.class);
                intent.putExtra("request", "1");
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_kqcx:
                //考勤记录查询
                intent = new Intent(Main2Activity.this, QueryAct.class);
                intent.putExtra("request", "2");
                startActivityForResult(intent, 2);
                break;
        }
    }

    /**
     * 初始化
     */
    private void initCardReader() {
        if (idReader == null) {
            if (Build.MODEL.toUpperCase().equals("JWZD-606") || Build.MODEL.toUpperCase().equals("JWZD-500")) {
                idReader = new A606LReader(Main2Activity.this);
            } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
                idReader = new A606AReader(Main2Activity.this);
            } else {
                idReader = new A606AReader(Main2Activity.this);
            }
            idReader.PowerOnReader();
            idReader.InitReader(null);


        }

    }

    public void releaseCardReader(boolean poweroff) {
        if (idReader != null) {
            if (poweroff) idReader.PowerOffReader();
            idReader.ReleaseReader();
            com.sunxi.hw.util.GPIOOutputLow("out3");
            idReader = null;
        }

    }


    private void initFingerPrinter() {
        //天成指纹
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//            fingerExt.PowerOnFinger606();
//        } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
//            fingerExt.PowerOnFinger606A();
//        }
        //天成指纹
//        fingerExt.PowerOnReader();
        if (fingerExt != null)
            fingerExt.initUsbFinger();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Main2Activity", "onDestroy");
        releaseFingerPrinter();
        releaseCardReader(true);
    }

    public void releaseFingerPrinter() {
        if (fingerExt != null) {
            fingerExt.UsbFingerClose();
        }
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//            //606 and 800
//            fingerExt.PowerOffFinger606();
//        } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
//            fingerExt.PowerOffFinger606A();//606A指纹下电
//        }
//       FingerExt.PowerOffReader();
        fingerExt = null;
    }

    @Override
    public void onBackPressed() {
    }
}
