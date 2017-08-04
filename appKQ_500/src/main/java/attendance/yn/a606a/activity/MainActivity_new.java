//package attendance.yn.a606a.activity;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.hongda.fingerclass;
//import com.techshino.fingerprint.FingerExt;
//import com.uvc.fingerprint.FingerPreview;
//import com.uvc.fingerprint.utils;
//import com.yinan.hotel.HotelApp;
//import com.yinan.hotel.R;
//import com.yinan.hotel.common.DateUtil;
//import com.yinan.hotel.common.base.SharePreferencesUtils;
//import com.yinan.hotel.model.CustomDialog;
//import com.yinan.hotel.model.DaoSession;
//import com.yinan.hotel.model.DbHelper;
//import com.yinan.hotel.model.HttpApi;
//import com.yinan.hotel.model.Record;
//import com.yinan.hotel.model.RecordDao;
//import com.yinan.hotel.ui.fragment.InChinaFragment;
//import com.yinan.hotel.ui.fragment.OutChinaFragment;
//
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Created by CHZ on 2016/9/29.
// */
//public class MainActivity_new extends AppCompatActivity {
//    private static final String TAG = MainActivity_new.class.getSimpleName();
//
//    @BindView(R.id.txlgmc)
//    TextView txlgmc;
//    @BindView(R.id.txljzt)
//    TextView txljzt;
//
//    public static FingerExt fingerExt;
//    @BindView(R.id.imgruzhu)
//    Button imgruzhu;
//    @BindView(R.id.imgtufang)
//    Button imgtufang;
//    @BindView(R.id.imgchaxun)
//    Button imgchaxun;
//    @BindView(R.id.imgshezhi)
//    Button imgshezhi;
//
//    private static String UPLOAD_STATUS_UNUPLOAD = "2";
//    private static String UPLOAD_STATUS_UPLOAD = "1";
//    @BindView(R.id.tvrz)
//    TextView tvrz;
//    @BindView(R.id.tvcs)
//    TextView tvcs;
//    private DaoSession session;
//    private HttpApi httpApi;
//    private Boolean isfalseupload = false;
//    private boolean isExitValidFinger;
//    public static int time = 10000;
//    public static   String company="hongda";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_new);
//        ButterKnife.bind(this);
//        session = DbHelper.getInstance(getApplicationContext());
//        txlgmc.setText("旅馆名称：" + HotelApp.enter_name);
//        httpApi = new HttpApi(this);
//
//
//        initReader();
//        getWindows();
//        timer.schedule(task, 100000, 100000);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        Log.e("keyCode", keyCode + "");
//        if (keyCode == 131) {
//            startActivity(new Intent(this, InChinaFragment.class));
//        } else if (keyCode == 132) {
//            startActivity(new Intent(this, OutChinaFragment.class));
//        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity_new.this);
//            builder.setMessage("您确定要退出登录界面吗？");
//            builder.setTitle("提示");
//            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    LoginActivity.reader.PowerOffReader();
//                    finish();
//                }
//            });
//            builder.setNegativeButton("取消",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            return;
//                        }
//                    });
//
//            builder.create().show();
//            return true;
//        }
////        Log.i("Keycode","ddd");
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        MainActivity_new.time = 0;
//
//        Log.i("Resume", "Resume start");
//        inputmodel(1);
//        inspect(1);
//
//
//        if (SharePreferencesUtils.getBoolean(MainActivity_new.this, HotelApp.isWhatFinger, true)) {//指纹选项
//            //电容指纹
//            if (fingerExt == null || HotelApp.initFingerSuccess == false) {
//                releaseFinger();//光电
//                fingerExt = new FingerExt(getApplicationContext());
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        initFingerPrinter(); //电容指纹
//
//                    }
//                }).start();
//            }
//        } else {
//            if (fingercamera == null || MainActivity_new.isInitFinger == false) {
//                releaseFingerPrinter(true);
//                //鸿达光电指纹
//
//                if(company=="hongda")
//                {
//
//                    if(MainActivity_new.isInitFinger==false) {
//                        int ref = fingerclass.InitFinger(this);
//                        if (ref == 1) {
//                            isInitFinger = true;
//                            HotelApp.initFingerSuccess = true;
//                        } else {
//                            isInitFinger = false;
//                        }
//                    }
//                }
//                else {
//                    fingercamera = new FingerPreview(MainActivity_new.this);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (SharePreferencesUtils.getInt(getApplicationContext(), HotelApp.FINGERDETE) == 1) {//指纹开关
////                            if (SharePreferencesUtils.getBoolean(MainActivity_new.this, HotelApp.isWhatFinger, true)) {//指纹选项
////                                initFingerPrinter(); //电容指纹
////                            } else {
//                                initFinger();//光电指纹
////                            }
//                            }
//                        }
//                    }).start();
//                }
//            }
//        }
//
//
//    }
//
//    public static FingerPreview fingercamera;
//    public static boolean isInitFinger = false;
//
//    /**
//     * 初始化天成光电指纹
//     */
//    private void initFinger() {
//        isExitValidFinger = false;
//        if (!checkPermission()) {
//            Log.e(TAG, "设备没root权限,无法使用光电指纹");
//            isInitFinger = false;
//            return;
//        }
//        if (fingercamera.checkusbdevice()) {
//            isInitFinger = true;
//        } else {
//            Log.e(TAG, "未发现TCO310指纹设备");
//            isInitFinger = false;
//        }
//        int ret = fingercamera.Init();
//        if (ret >= 0) {
//            isInitFinger = true;
////            SharePreferencesUtils.putInt(MainActivity_new.this, HotelApp.GDFingerId, fingercamera.fingerJNI.getTCODevice());
////            Log.e(TAG, "UVC设备节点： " + SharePreferencesUtils.getInt(MainActivity_new.this, HotelApp.GDFingerId, fingercamera.fingerJNI.getTCODevice()));
//        } else {
//            Log.e(TAG, "未发现UVC设备");
//            isInitFinger = false;
//        }
//
//    }
//
//    //光电指纹
//    private boolean checkPermission() {
//        if (utils.hasRootPermission()) {
//            utils.upgradeRootPermission("/dev/video0");
//            utils.upgradeRootPermission("/dev/video1");
//            utils.upgradeRootPermission("/dev/video2");
//            utils.upgradeRootPermission("/dev/video3");
//            utils.upgradeRootPermission("/dev/video4");
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isNetworkConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//            if (mNetworkInfo != null) {
//                return mNetworkInfo.isAvailable();
//            }
//        }
//        return false;
//    }
//
//    private void inspect(int code) {
//        showincouts();
//        showuploadcounts();
//        if (!isNetworkConnected(this)) {
//            txljzt.setTextColor(Color.parseColor("#FF840C00"));
//            txljzt.setText("当前网络状态：未连接");
//            return;
//        } else {
//            txljzt.setText("当前网络状态：已连接");
//            if(code==1) {
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        uploadc();
//                    }
//                });
//                t.start();
//            }
//
//        }
//    }
//
//    private void showincouts()
//    {
//        String countin=session.getRecordDao().queryBuilder().where(RecordDao.Properties.In_time.like("%" + DateUtil.formateDate("yyyy-MM-dd", new Date()) + "%"), RecordDao.Properties.House_number.notEq("")).count()+"";
//        while (countin.length() < 3) {
//            countin = "0" + countin;
//        }
//        countin = " " + countin;
//        tvrz.setText(countin);
//    }
//    private void showuploadcounts()
//    {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                long countincs=session.getRecordDao().queryBuilder().where(RecordDao.Properties.In_status.eq(UPLOAD_STATUS_UNUPLOAD)).count();
//                long countoutcs=session.getRecordDao().queryBuilder().where(RecordDao.Properties.Out_status.eq(UPLOAD_STATUS_UNUPLOAD)).count();
//                long countscall=session.getRecordDao().queryBuilder().where(RecordDao.Properties.In_status.eq(UPLOAD_STATUS_UNUPLOAD), RecordDao.Properties.Out_status.eq(UPLOAD_STATUS_UNUPLOAD)).count();
//                String countsc=countincs+countoutcs-countscall+"";
//                while (countsc.length() < 3) {
//                    countsc = "0" + countsc;
//                }
//                countsc = " " + countsc;
//                tvcs.setText(countsc);
//            }
//        });
//
//    }
//    private void uploadc() {
//        if (isfalseupload == true) {
//            return;
//        }
//        isfalseupload = true;
//        List<Record> list = session.
//                getRecordDao().queryBuilder().
//                where(RecordDao.Properties.In_status.eq(UPLOAD_STATUS_UNUPLOAD))
//                .list();
//        if (list.size() > 0) {
//
////            XMLUtil.DATE_TYPE_FILENAME="1";
//            updataRecord(list);
//        } else {
//            showuploadcounts();
//            master.sendEmptyMessage(1);
//        }
//    }
//
//    public static void inputmodel(int i) {
//        try {
//            FileWriter localFileWriter = new FileWriter(new File("/dev/tm1650op"));
//            localFileWriter.write(i + "");
//            localFileWriter.close();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void uploadcout() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<Record> listout = session.
//                        getRecordDao().queryBuilder().
//                        where(RecordDao.Properties.Out_status.eq(UPLOAD_STATUS_UNUPLOAD))
//                        .list();
//                if (listout.size() > 0) {
//                    for (int i = 0; i < listout.size(); i++) {
////            XMLUtil.DATE_TYPE_FILENAME="2";
//                        while (falseerr) {
//                            try {
//                                Thread.sleep(2000);
//                                falseerr=false;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        updataRecordout(listout.get(i));
//                        while (recordover) {
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                        while (falseerr) {
//                            try {
//                                Thread.sleep(2000);
//                                falseerr=false;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    showuploadcounts();
//                    master.sendEmptyMessage(2);
//
//                } else {
//                    master.sendEmptyMessage(2);
//                }
//            }
//        });
//        t.start();
//
//    }
//    private final Timer timer = new Timer();
//
//
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            Message message = new Message();
//            message.what = 3;
//            master.sendMessage(message);
//        }
//    };
//    Handler master = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    uploadcout();
//                    break;
//                case 2:
//                    // uploadf();
//                    isfalseupload = false;
//                    inspect(2);
//                 //   master.sendEmptyMessageAtTime(3, 60000);
//                    break;
//                case 3:
//                    //  uploadfout();
//                    inspect(1);
//                   // timer.sendEmptyMessageAtTime(1, 60000);
//                    break;
//                case 4:
//                    // toast("更新完成");
//                    break;
//            }
//            return false;
//        }
//    });
//    private int listsize = 0;
//    private int uploadsize = 0;
//
//    private void save(Record record) {
//        record.setPerson_base64_head("");
//        session.update(record);
//    }
//    private Boolean falseerr=false;
//    private Boolean recordover = false;
//
//    private void updataRecordout(final Record record) {
//
//
////            List<Record> list = listrecord;
////            listsize=list.size();
////            uploadsize=0;//Record record : list
////            int i=0;
////            while (i<listsize)  {
////                     if(recordover==true)
////                     {
////                        return;
////                     }
////                final Record record=list.get(i);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txljzt.setText("正在上传：" + record.getPerson_name() + "退房信息");
//            }
//        });
//        // i=i+1;
//        recordover = true;
//        httpApi.upload(record, new HttpApi.OnUploadListener() {
//            @Override
//            public void success(Record record) {
//                uploadsize = uploadsize + 1;
//                recordover = false;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(2);
////                        }
//                record.setOut_status(UPLOAD_STATUS_UPLOAD);
//                save(record);
//
//            }
//
//            @Override
//            public void fail(Record record, String errorMsg) {
//                final String mess=errorMsg;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        txljzt.setText("错误：" + mess);
//                        falseerr=true;
//                        // HotelApp.toastCommom.ToastShow(MainActivity_new.this, (ViewGroup) findViewById(R.id.toast_layout_root), "错误：" + mess);
//                    }
//                });
//
//                uploadsize = uploadsize + 1;
//                recordover = false;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(2);
////                        }
//            }
//
////                    @Override
////                    public void success(Object t) {
////                        // Record record = (Record) t;
////                        uploadsize=uploadsize+1;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(2);
////                        }
////                        recordnew.setOut_status(UPLOAD_STATUS_UPLOAD);
////                        save(recordnew);
////                        toast("手动上传国内住宿人员退房信息成功");
////
////
////                    }
////
////                    @Override
////                    public void fail(int failCode, Object t) {
////                        uploadsize=uploadsize+1;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(2);
////                        }
////                        toast("手动上传国内住宿人员退房信息失败");
////
////
////                    }
//        });
//        //}
//
//
//    }
//
//
//    private void updataRecord(List<Record> listrecord) {
//
//
//        List<Record> list = listrecord;
//        listsize = list.size();
//        uploadsize = 0;
//        int i = 0;
//        while (i < listsize) {
//            while (recordover) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            while (falseerr) {
//                try {
//                    Thread.sleep(2000);
//                    falseerr=false;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                if (i >= listsize) {
//
//                    return;
//                }
//
//                final Record record = list.get(i);
//
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        txljzt.setText("正在上传：" + record.getPerson_name() + "入住信息");
//                       // HotelApp.toastCommom.ToastShow(MainActivity_new.this, (ViewGroup) findViewById(R.id.toast_layout_root),"正在上传：" + record.getPerson_name() + "入住信息");
//                    }
//                });
//                i = i + 1;
//                recordover = true;
////                record.setCert_type("身份证");
////                record.setPerson_base64_head(Base64.encodeToString(record.getPerson_head(), Base64.DEFAULT));
//                httpApi.upload(record, new HttpApi.OnUploadListener() {
//                    @Override
//                    public void success(Record record) {
//
//                        uploadsize = uploadsize + 1;
//                        recordover = false;
//                        if (uploadsize == listsize) {
//                            master.sendEmptyMessage(1);
//                        }
//                        record.setIn_status(UPLOAD_STATUS_UPLOAD);
//                        save(record);
////                        toast("手动上传国内住宿人员信息成功");
//                    }
//
//                    @Override
//                    public void fail(Record record, String errorMsg) {
//                        final String mess=errorMsg;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                txljzt.setText("错误：" + mess);
//                                falseerr=true;
//                               // HotelApp.toastCommom.ToastShow(MainActivity_new.this, (ViewGroup) findViewById(R.id.toast_layout_root), "错误：" + mess);
//
//                            }
//                        });
//
//                        uploadsize = uploadsize + 1;
//                        recordover = false;
//                        if (uploadsize == listsize) {
//                            master.sendEmptyMessage(1);
//                        }
////                        toast("手动上传国内住宿人员信息失败");
//                    }
//
//
////                    @Override
////                    public void success(Object t) {
////                        Record record = (Record) t;
////                        uploadsize=uploadsize+1;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(1);
////                        }
////                        record.setIn_status(UPLOAD_STATUS_UPLOAD);
////                        save(record);
////                        toast("手动上传国内住宿人员信息成功");
////
////                    }
////
////                    @Override
////                    public void fail(int failCode, Object t) {
////                        uploadsize=uploadsize+1;
////                        if(uploadsize==listsize) {
////                            master.sendEmptyMessage(1);
////                        }
////                        toast("手动上传国内住宿人员信息失败");
////
////                    }
//                });
//
//            } catch (Exception ex) {
//                i = listsize;
//                Log.e(TAG, ex.toString());
//            }
//        }
//
//
//    }
//
//    public Context getAppContext() {
//        return getApplicationContext();
//    }
//
//    private void initReader() {
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //初始化指纹
//
//                //初始化读卡
//
//
//                LoginActivity.reader.PowerOnReader();
//                Log.e(TAG, "PowerOnReader");
//
//                if (!LoginActivity.reader.InitReader(initAuthFile())) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getAppContext(), "上电失败", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//            }
//        }).start();
//    }
//
//    private byte[] initAuthFile() {
//        byte[] buffer = null;
//        try {
//
//            InputStream is = this.getResources().openRawResource(R.raw.armidse);
//            DataInputStream inputReader = new DataInputStream(is);
//            buffer = new byte[is.available()];
//            inputReader.readFully(buffer);
//            inputReader.close();
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return buffer;
//    }
//
//    private void getWindows() {
//        WindowManager windowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
//        int width = windowManager.getDefaultDisplay().getWidth();
//        int height = windowManager.getDefaultDisplay().getHeight();
//        Log.d("windows", width + "+" + height);
//    }
//
//    @OnClick({R.id.imgruzhu, R.id.imgtufang, R.id.imgchaxun, R.id.imgshezhi, R.id.tvrz, R.id.tvcs})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.imgruzhu:
//                if (SharePreferencesUtils.getBoolean(MainActivity_new.this, HotelApp.isWhatFinger, true)) {//指纹选项
//                    //电容指纹
//                    if (fingerExt == null || HotelApp.initFingerSuccess == false) {
//                        releaseFinger();//光电
//                        fingerExt = new FingerExt(getApplicationContext());
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                initFingerPrinter(); //电容指纹
//
//                            }
//                        }).start();
//                    }
//                } else {
//                    if(company=="hongda")
//                    {
//
//                        if(MainActivity_new.isInitFinger==false|| HotelApp.initFingerSuccess == false) {
//                            int ref = fingerclass.InitFinger(this);
//                            if (ref == 1) {
//                                isInitFinger = true;
//                                HotelApp.initFingerSuccess = true;
//                            } else {
//                                isInitFinger = false;
//                            }
//                        }
//                    }
//                    else {
//                        fingercamera = new FingerPreview(MainActivity_new.this);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (SharePreferencesUtils.getInt(getApplicationContext(), HotelApp.FINGERDETE) == 1) {//指纹开关
////                            if (SharePreferencesUtils.getBoolean(MainActivity_new.this, HotelApp.isWhatFinger, true)) {//指纹选项
////                                initFingerPrinter(); //电容指纹
////                            } else {
//                                    initFinger();//光电指纹
////                            }
//                                }
//                            }
//                        }).start();
//                    }
//                }
//                startActivity(new Intent(this, InChinaFragment.class));
//                break;
//            case R.id.imgtufang:
//                startActivity(new Intent(this, OutChinaFragment.class));
//                break;
//            case R.id.imgchaxun:
//                startActivity(new Intent(this, QueryActivity.class));
//                break;
//            case R.id.imgshezhi:
//                startActivity(new Intent(this, SettingActivity.class));
//                break;
//            case R.id.tvrz:
//                HotelApp.listnum = 4;
//                startActivity(new Intent(this, ListActivity.class));
//                break;
//            case R.id.tvcs:
//                HotelApp.listnum = 5;
//                startActivity(new Intent(this, ListActivity.class));
//                break;
//        }
//    }
//
//    private void initFingerPrinter() {
//        if (SharePreferencesUtils.getInt(getApplicationContext(), HotelApp.FINGERDETE) == 1) {
//            //天成指纹
//            FingerExt.PowerOnReader();
//            if (fingerExt != null)
//                fingerExt.initUsbFinger();
//
//        }
//    }
//
//    public void releaseFingerPrinter(boolean poweroff) {
//        if (SharePreferencesUtils.getInt(getApplicationContext(), HotelApp.FINGERDETE) == 1) {
//            if (fingerExt != null)
//                fingerExt.UsbFingerClose();
//            if (poweroff) FingerExt.PowerOffReader();
//        }
//    }
//
//    /**
//     * 释放天成光电指纹
//     */
//    public void releaseFinger() {
//        if (fingercamera != null) {
//            isExitValidFinger = true;
//            fingercamera.fingerJNI.FingerClose();
//            Log.e(TAG, "释放光电指纹");
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        if (SharePreferencesUtils.getInt(getApplicationContext(), HotelApp.FINGERDETE) == 1) {
//            if (SharePreferencesUtils.getBoolean(MainActivity_new.this, HotelApp.isWhatFinger, true)) {
//                releaseFingerPrinter(true);
//            } else {
//                if(company=="hongda")
//                {
//
//                    fingerclass.closeFinger();
//                    isInitFinger = false;
//                }
//                else {
//                    releaseFinger();//光电
//                }
//            }
//        }
//        fingercamera = null;
//        fingerExt = null;
//
//        super.onDestroy();
//    }
//
//
//
//    //    @OnClick({R.id.imgruzhu, R.id.imgtufang, R.id.imgchaxun, R.id.imgshezhi})
////    public void onClick(View view) {
////        switch (view.getId()) {
//////            case R.id.imgruzhu:
//////                imgruzhu.setImageResource(R.mipmap.ruzhudown);
//////                startActivity(new Intent(this, InChinaFragment.class));
//////                imgruzhu.setImageResource(R.mipmap.ruzhuup);
//////                break;
////            case R.id.imgtufang:
////                imgtufang.setImageResource(R.mipmap.tuifangdown);
////                startActivity(new Intent(this, InChinaFragment.class));
////                imgtufang.setImageResource(R.mipmap.tuifangup);
////                break;
////            case R.id.imgchaxun:
////                imgchaxun.setImageResource(R.mipmap.chaxundown);
////                imgchaxun.setImageResource(R.mipmap.chaxunup);
////                break;
////            case R.id.imgshezhi:
////                imgshezhi.setImageResource(R.mipmap.shezhidown);
////                imgchaxun.setImageResource(R.mipmap.shezhiup);
////                break;
////        }
////    }
//
//
//}
//
