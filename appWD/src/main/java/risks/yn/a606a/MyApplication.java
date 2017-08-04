package risks.yn.a606a;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.YinanSoft.CardReaders.A606AReader;
import com.YinanSoft.CardReaders.A606LReader;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.phoneface.FaceSDK;
import com.techshino.fingerprint.FingerExt;

import risks.yn.a606a.Utils.SoundPoolAudioClip;
import risks.yn.a606a.sqlite.DBManager;


/**
 * Created by Administrator on 2017/4/21.
 */
public class MyApplication extends Application {
    public static boolean initFaceDeleSuccess = false;
    public static boolean initFingerSuccess = false;
    public static String DBFilePath = "/sdcard/MobileManhunt.db";
    public static IDCardInfo idCardInfo;
    public static String faceFeatures = "";
    private static Bitmap faceBitamp = null;
    public static IDCardReader idReader = null;
    public static DBManager dbManager = null;
    public static FingerExt fingerExt = null;

    public static String PASSWORD = "yast123";

    public static SoundPoolAudioClip sp;

    public static int comcompareScore = 90;//采集彩色75分，近红外70分；比对彩色95分，近红外90分。


    public static boolean isExitVaildFinger = false;

    public static Bitmap getFaceBitamp() {
        return faceBitamp;
    }

    public static void setFaceBitamp(Bitmap faceBitamp) {
        MyApplication.faceBitamp = faceBitamp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化算法
        initFaceDeleSuccess = FaceSDK.init(getApplicationContext());
        //初始化数据库
        if (MyApplication.dbManager == null)
            dbManager = DBManager.getInstance(getApplicationContext());

        sp = new SoundPoolAudioClip(getApplicationContext());

//        initCardReader(getApplicationContext());

//        if (fingerExt == null)
//            fingerExt = new FingerExt(getApplicationContext());

    }


    /**
     * 初始化
     */
    public static void initCardReader(Context context) {
        if (idReader == null) {
            if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
                idReader = new A606LReader(context);
            } else if (Build.MODEL.toUpperCase().equals("JWZD-606A")) {
                idReader = new A606AReader(context);
            } else {
                idReader = new A606AReader(context);
            }
            idReader.PowerOnReader();
            idReader.InitReader(null);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sp.release();
        dbManager.Close();
//        releaseIdCardReader();
//        releaseFingerPrinter();
    }

//    public static void initFingerPrinter() {
//        //天成指纹
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//            fingerExt.PowerOnReader();
//        } else {
//            fingerExt.PowerOnFinger606A();
//        }
//        fingerExt.initUsbFinger();
//    }
//
//    public static void releaseFingerPrinter() {
//        isExitVaildFinger = true;
//        if (fingerExt != null) {
//            fingerExt.UsbFingerClose();
//        }
//        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
//            //606 and 800
//            fingerExt.PowerOffReader();
//        } else {
//            fingerExt.PowerOffFinger606A();//606A指纹下电
//        }
//    }
//
//    public static void releaseIdCardReader() {
//        idReader.PowerOffReader();
//        idReader.ReleaseReader();
//    }
}
