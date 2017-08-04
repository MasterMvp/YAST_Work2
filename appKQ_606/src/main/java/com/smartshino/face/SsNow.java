/*    */ package com.smartshino.face;
/*    */ 
/*    */ import android.app.Activity;
import android.content.Context;
/*    */ import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;


import com.YinanSoft.phoneface.common.Logs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/*    */ //import com.techshino.utils.Logs.MyLog;
/*    */ 
/*    */ public class SsNow
/*    */ {
/*    */
/* 11 */   public static boolean isLoad = true;
/*    */ 
/*    */   static {
/* 14 */     if (!isLoad) {
/* 15 */       //System.loadLibrary("SsNow");
/* 16 */       //MyLog.debugLog("SsNow", "load library libFaceRecognize ");
/* 17 */       isLoad = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public native int SetCtx(Context paramContext);
/*    */ 
/*    */   public native String GenAct(int paramInt, byte[] paramArrayOfByte);
/*    */ 
/*    */   public native int Version(int[] paramArrayOfInt1, int[] paramArrayOfInt2, short[] paramArrayOfShort);
/*    */ 
/*    */   public native int Init(long[] paramArrayOfLong);
/*    */ 
/*    */   public native int Close(long paramLong);
/*    */ 
/*    */   public native int Fminut(String paramString, byte[] paramArrayOfByte, int paramInt, long paramLong);
/*    */ 
/*    */   public static native int Match(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, long paramLong);
/*    */ 
/*    */   public native int Minutia(byte[] paramArrayOfByte, Bitmap paramBitmap, int paramInt, long paramLong);
/*    */ 
/*    */   public native int AttrFbmp(String paramString, int[] paramArrayOfInt);
/*    */ 
/*    */   public native int LoadFbmp(String paramString, byte[] paramArrayOfByte, int paramInt);
/*    */ 
/*    */   public native int SaveFbmp(String paramString, byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt);
/*    */ 
/*    */   public native int FeatureX(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt, int paramInt, long paramLong);
/*    */ 
/*    */   public native int LodDat(String paramString);
/*    */ 
/*    */   public native int Discover(int[] paramArrayOfInt, Bitmap paramBitmap, int paramInt, long paramLong);
/*    */ 
/*    */   public native int YuvToRgb(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
/*    */ 
/*    */   public native int DoRotate(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt);
/*    */ 
/*    */   public native int DiscoverX(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong);
/*    */ 
/*    */   public native int Transform(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2);
/*    */ 
/*    */   public native int RgbToJpg(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4);

    /*******************
     * 后续封装算法调用
     ***********************/
    private final static String VERSION_CODE = "version_code";
    private final static String VERSION = "version";

    private static final String DAT_NAME = "FaceNew.dat";

    public static long ENV_SET;

    private static String mActivationResult;

    private long[] phEnvSet = new long[1];

    private String mActivationCode;

    private int nLoadResult = 0;

    Context context;
    byte[] hFea;
    byte[] hTpl;

    public byte[] gethTpl() {
        return hTpl;
    }

    private volatile static SsNow instance = null;
    private final String TAG = SsNow.class.getSimpleName();

    public static SsNow getInstance(Context context) {
        if (instance == null) {
            instance = new SsNow(context);
        }
        return instance;
    }

    private SsNow(Context context) {
        this.context = context;
//        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/\0";
        String path = context.getFilesDir().getPath() + "/";
        if(com.YinanSoft.phoneface.Constants.isDebugLib)
            GenAct(3, path.getBytes());
        else GenAct(3, path.getBytes());
    }

    public void init(String code) {
        if (com.YinanSoft.phoneface.Constants.isDebugLib) {
            //测试库
//            int[] arrayOfInt1 = new int[1];
//            int[] arrayOfInt2 = new int[1];
//            short[] arrayOfShort = new short[4];
//            int nRet = Version(arrayOfInt1, arrayOfInt2, arrayOfShort);
//            Log.e(this.TAG, "初始化测试算法 : nRet=" + nRet + ", nMbsz=" + arrayOfInt1[0] + ", nMath=" + arrayOfInt2[0] + ", v" + arrayOfShort[0] + "." + arrayOfShort[1] + "." + arrayOfShort[2] + "." + arrayOfShort[3]);
//            Log.e(this.TAG, "设置授权码 : " + GenAct(1, code.getBytes()));//)//"TechShino"));
//            String version = arrayOfShort[0] + "." + arrayOfShort[1] + "." + arrayOfShort[2] + "." + arrayOfShort[3];
//            loadAssetFile(version);
//            this.hFea = new byte[arrayOfInt1[0]];
//            this.hTpl = new byte[arrayOfInt1[0]];
//            mActivationResult = "ok";
            //正式库
            int nRet = -119;
            int[] nMbsz = new int[1];
            int[] nMath = new int[1];
            short[] nAvrsn = new short[4];

            nRet = Version(nMbsz, nMath, nAvrsn);//tt add
            com.YinanSoft.phoneface.common.Logs.d(TAG, "初始化算法 : nRet=" + nRet + ", nMbsz=" + nMbsz[0] + ", nMath=" + nMath[0] + ", v" + nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3]);
            if (com.YinanSoft.phoneface.util.StringUtils.length(code) != 16) return;
            mActivationResult = GenAct(1, code.getBytes());
            com.YinanSoft.phoneface.common.Logs.d(TAG, "设置授权码 : " + mActivationResult);//设置授权码

            if (!"ok".equals(mActivationResult)) {
                return;
            }

            String version = nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3];
            loadAssetFile(version);

            int featureSize = Init(phEnvSet);
            ENV_SET = phEnvSet[0];
            com.YinanSoft.phoneface.common.Logs.d(TAG, "初始化... 特征大小：" + featureSize + " 句柄：" + ENV_SET);
            hFea = new byte[featureSize]; /* 按最大长度分配两个特征内存 */
            hTpl = new byte[nMbsz[0]];
        } else {
            //正式库
            int nRet = -119;
            int[] nMbsz = new int[1];
            int[] nMath = new int[1];
            short[] nAvrsn = new short[4];

            nRet = Version(nMbsz, nMath, nAvrsn);//tt add
            com.YinanSoft.phoneface.common.Logs.d(TAG, "初始化算法 : nRet=" + nRet + ", nMbsz=" + nMbsz[0] + ", nMath=" + nMath[0] + ", v" + nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3]);
            if (com.YinanSoft.phoneface.util.StringUtils.length(code) != 16) return;
            mActivationResult = GenAct(1, code.getBytes());
            com.YinanSoft.phoneface.common.Logs.d(TAG, "设置授权码 : " + mActivationResult);//设置授权码

            if (!"ok".equals(mActivationResult)) {
                return;
            }

            String version = nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3];
            loadAssetFile(version);

            int featureSize = Init(phEnvSet);
            ENV_SET = phEnvSet[0];
            com.YinanSoft.phoneface.common.Logs.d(TAG, "初始化... 特征大小：" + featureSize + " 句柄：" + ENV_SET);
            hFea = new byte[featureSize]; /* 按最大长度分配两个特征内存 */
            hTpl = new byte[nMbsz[0]];
        }
    }


    public void loadAssetFile(String version) {
        String sdmdat = context.getFilesDir().getAbsolutePath() + "/" + DAT_NAME;
        File file = new File(sdmdat);
        int versionCode = getVersionCode();
        if (versionCode > getInt(VERSION_CODE) || !getString(VERSION).equals(version)) {
            if (file.exists()) {
                boolean bool = file.delete();
                com.YinanSoft.phoneface.common.Logs.d(TAG, "删除算法数据库:" + bool);// -18
            }
            saveString(VERSION, version);
            saveInt(VERSION_CODE, versionCode);
        }
        try {
            if (!file.exists()) {
                InputStream is = context.getResources().getAssets().open(DAT_NAME);

                FileOutputStream fos = new FileOutputStream(sdmdat);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            nLoadResult = LodDat(sdmdat);
            com.YinanSoft.phoneface.common.Logs.d(TAG, "加载算法数据库完成:" + nLoadResult);// -18
        } catch (Exception e) {
            com.YinanSoft.phoneface.common.Logs.d(TAG, "加载算法数据库失败，程序异常");
            e.printStackTrace();
        }
    }

    public int unloadAssetFile() {
        try {
            int loadresult = this.LodDat(null);
            Logs.v("卸载算法数据库完成:" + loadresult);
            return loadresult;
        } catch (Exception e) {
            Logs.v("卸载算法数据库失败，程序异常");
            e.printStackTrace();
        }return -4;
    }

    public void deinit() {
//        if(nLoadResult != 0)
//            unloadAssetFile();
        if(ENV_SET != 0) {
            Close(ENV_SET);
            Logs.v("deinit");
        }
        ENV_SET = 0;
    }

    public String getFeature(Bitmap bitmap) {
        int nRet = Minutia(hFea, bitmap, 0, ENV_SET);
        com.YinanSoft.phoneface.common.Logs.v("match4:"+nRet);
        if (nRet > 0 && hFea != null) {
            return Base64.encodeToString(hFea, 0);
        }
        return null;
    }

    //业务逻辑，虽说写到这里会比较乱，但是先这样写，然后重构

    //找脸
//
//    public com.YinanSoft.phoneface.common.OtcRect findSingleFace(Bitmap mCameraBitmap, int aWidth, int aHeight) {
//        // return this.native_opencv_findfaceinimage(aPixels, aWidth, aHeight);
//        int[] nPosit = new int[1];
//        com.YinanSoft.phoneface.common.OtcRect[] tcRct = Detect(nPosit, mCameraBitmap, 0, 0);
//        if (nPosit[0] >= 0) // 确认函数执行成功！
//        {
//            /*
//             * return new Rect(tcRct[0].nLft, tcRct[0].nTop, tcRct[0].nLft +
//			 * tcRct[0].uWid, tcRct[0].nTop + tcRct[0].uHei);
//			 */
//            return tcRct[0];
//        }
//        return null;
//    }

    //注册脸
    public boolean GetMinutia(Bitmap bitmap) {
        int nRet = Minutia(hFea, bitmap, 0, 0);
        if (nRet > 0 && hFea != null) {
            com.YinanSoft.phoneface.common.Logs.d(TAG, nRet + "  " + hFea.toString());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userid", "0001");
            editor.putString("FacehFea", Base64.encodeToString(hFea, 0));
            editor.commit();//提交修改
            //缓存了一个人脸
            return true;
        }
        return false;
    }

    public void setActivationCode(String activationCode) {
        mActivationCode = activationCode;
    }

    public static String getmActivationResult() {
        return mActivationResult;
    }

    //比对脸
    public boolean DoMatch(Bitmap bitmap) {
        int nRet = Minutia(hTpl, bitmap, 0, 0);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
        String prefString = prefs.getString("FacehFea", "");
        if (!"".equals(prefString)) {
            hFea = Base64.decode(prefString, 0);
            if (nRet > 0) {
                nRet = Match(hFea, hTpl, 0, ENV_SET);
                com.YinanSoft.phoneface.common.Logs.d(TAG, "分数为  " + nRet);
                return (nRet >= com.YinanSoft.phoneface.Constants.VERIFY_SCORE);
            }
        }
        return false;
    }

    public void saveString(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public void saveInt(String key, Integer value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public int getVersionCode()// 获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }



}


/* Location:           G:\安卓人脸单机版（比对）-timeout_20170701-SDK20170317_v1.2.4\FaceSDK_SingleVersion_Android_Release_V1.2.3_update_20170317\demo\FaceRecognizeDemo-time\libs\tesofacelib-time.jar
 * Qualified Name:     com.smartshino.face.SsNow
 * JD-Core Version:    0.5.4
 */