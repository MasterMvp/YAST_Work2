package risks.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.YinanSoft.CardReaders.A606AReader;
import com.YinanSoft.CardReaders.A606LReader;
import com.YinanSoft.CardReaders.IDCardReader;
import com.rsk.api.ICard;
import com.techshino.fingerprint.FingerExt;

import risks.yn.a606a.R;
import risks.yn.a606a.Utils.ToastUtils;

public class Main2Activity extends Activity implements View.OnClickListener {
    public static final String TAG = "Main2Activity";
    private GridView grid;
    String[] title_res = {"上班打卡", "员工录入", "员工查询", "考勤查询"};
    int[] img_res = {R.drawable.kaoqin, R.drawable.luru, R.drawable.renyuan, R.drawable.jilu};
    Intent intent = null;
    private ImageView ll_sbdk, ll_yglr, ll_ygcx, ll_kqcx, ll_cxcx, main3_yh4;
    public static IDCardReader idReader = null;
    public static FingerExt fingerExt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        init();
//        grid = (GridView) findViewById(R.id.main2_grid);
//        Main2Adapter adapter = new Main2Adapter(title_res, img_res, this);
//        grid.setAdapter(adapter);
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        //打卡
//                        intent = new Intent(Main2Activity.this, MainActivity.class);
//                        startActivity(intent);
//                        break;
//                    case 1:
//                        //录入
//                        intent = new Intent(Main2Activity.this, AddUserAct.class);
//                        startActivity(intent);
//                        break;
//                    case 2:
//                        //员工查询
//                        intent = new Intent(Main2Activity.this, QueryAct.class);
//                        intent.putExtra("request", "1");
//                        startActivityForResult(intent, 1);
//                        break;
//                    case 3:
//                        //考勤记录查询
//                        intent = new Intent(Main2Activity.this, QueryAct.class);
//                        intent.putExtra("request", "2");
//                        startActivityForResult(intent, 2);
//                        break;
//                }
//            }
//        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (Build.MODEL.toUpperCase().equals("SK-S600")) {
//                    initSK600CardReader();
//                } else initCardReader();
//            }
//        }).start();
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
//        releaseFingerPrinter();
//        idReader = null;
//        fingerExt = null;
    }


    /**
     * 初始化
     */
    private void initCardReader() {
        if (idReader == null) {
            if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
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

    private void initSK600CardReader() {
        final int mResult = ICard.Open();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mResult == 0) {
//                    ToastUtils.showToast(Main2Activity.this, "打开设备成功");
                } else {
                    ToastUtils.showToast(Main2Activity.this, "打开设备失败");
                }
            }

        });
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

    private void init() {
        ll_sbdk = (ImageView) findViewById(R.id.ll_sbdk);
        ll_yglr = (ImageView) findViewById(R.id.ll_yglr);
        ll_ygcx = (ImageView) findViewById(R.id.ll_ygcx);
        ll_kqcx = (ImageView) findViewById(R.id.ll_kqcx);
        ll_cxcx = (ImageView) findViewById(R.id.ll_cxcx);
        main3_yh4 = (ImageView) findViewById(R.id.main3_yh4);
//        ll_sbdk.setOnClickListener(this);
//        ll_yglr.setOnClickListener(this);
//        ll_ygcx.setOnClickListener(this);
//        ll_kqcx.setOnClickListener(this);
        ll_cxcx.setOnClickListener(this);
        main3_yh4.setOnClickListener(this);
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
            case R.id.ll_cxcx:
                //诚信查询
                intent = new Intent(Main2Activity.this, AddRiskAct.class);
                startActivity(intent);
                break;
            case R.id.main3_yh4:
                intent = new Intent(Main2Activity.this, YHAct.class);
                startActivity(intent);
                break;
        }
    }
}
