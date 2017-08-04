package com.techshino.tesoface;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

/* TESO检测定位用的矩形结构体大小：每个12字节 */


public class Algorithm {

    private final static String VERSION_CODE = "version_code";
    private final static String VERSION = "version";

    private static final String DAT_NAME = "Face.dat";

    public static long ENV_SET;

    private static String mActivationResult;

    private long[] phEnvSet = new long[1];

    private int nLoadResult = 0;

    private String mActivationCode;

    static									/* 加载人脸算法库 */ {
//        System.loadLibrary("FaceRecognize");  //正式库
//        System.loadLibrary("TesoFace");       //测试库2016-07-06

    }

    /**
     * 设置上下文，用于获取IMEI或MAC地址等硬件信息
     */
    public native int SetCtx(Context context); /* JAVA类的上下文 */

    /**
     * 生成返回码，处理激活码，存成许可文件
     */

//  public static native String GenAct(int nMode, /* 0=返回，1=激活 */
//                              //String szCtx /* 输入激活码字串 */
//                              byte[] code
//  );

    //测试库
    public static native String GenAct(int nMode, /* 0=返回，1=激活 */
                                       String szCtx /* 输入激活码字串 */
    );

    /**
     * 正式库
     * 生成返回码，处理激活码，存成许可文件
     */

    public static native String GenAct(int nMode, /* 0=返回，1=激活 */
                                       //String szCtx /* 输入激活码字串 */
                                       byte[] code
    );


    /**
     * 求取最大特征字节长度，取版本描述信息(4段式)
     */
    public native int Version(int[] pdwMbsz, /* 最大特征长[1] */
                              int[] pdwMath, /* 算法标识符[1] */
                              short[] pwAvrsn /* 算法库版本[4] */
    );

    /**
     * 初始化Minut和Match的算法环境，若不需要可空置
     */
    public native int Init(long[] phEnvSet); /* 环境句柄址[1] */

    /**
     * 结束Minut和Match的算法环境，若不需要可空置
     */
    public native int Close(long hEnvSet); /* 环境的句柄 */

    /**
     * 从给定的BMP图像文件提取特征值，最长dwSzit字节
     */
    public native int Fminut(String szFile, /* 图全文件名 */
                             byte[] hBinMut, /* 特征值区域 */
                             int nOptCfg, /* 附加配置值 */
                             long hEnvSet /* 环境的句柄 */
    );

    /**
     * 比对给定的两个特征值，返回相似度，[0, 127]分数
     */
    public static native int Match(byte[] hFeaBin, /* 特征数据值 */
                                   byte[] hTplBin, /* 模板数据值 */
                                   int nOptCfg, /* 附加配置值 */
                                   long hEnvSet /* 环境的句柄 */
    );

    /**
     * 从指定的图像数据中提取出特征值，返回实际使用长度
     */
    public native int Minutia(byte[] hBinMut, /* 特征值区域 */
                              Bitmap srcBmp, /* 输入位图像 */
                              int nOptCfg, /* 附加配置值 */
                              long hEnvSet /* 环境的句柄 */
    );

    /**
     * 从指定的图像数据中提取出适合提取特征的目标矩形
     */
    public native com.YinanSoft.phoneface.common.OtcRect[] Detect(int[] hPosit, /* 结果值[1] */
                                   Bitmap srcBmp, /* 输入位图像 */
                                   int nOptCfg, /* 附加配置值 0 */
                                   long hEnvSet /* 环境的句柄 0 */
    );

    /**
     * 取BMP文件的宽、高、色彩(0=彩，1=灰)、DPI等图像属性
     */
    public native int AttrFbmp(String szFile, /* 图全文件名 */
                               int[] hAttr /* [4]：宽-高-色彩-DPI */
    );

    /**
     * 读取BMP文件的图像数据到给定的足够大内存(预先分配好)
     */
    public native int LoadFbmp(String szFile, /* 图全文件名 */
                               byte[] hRgb24, /* []：加载的R-G-B图像 */
                               int bToGray /* 0=原样，1=变成灰度 */
    );

    /**
     * 按宽-高-色彩(0=彩，1=灰)-DPI，保存图像数据到BMP文件
     */
    public native int SaveFbmp(String szFile, /* 图全文件名 */
                               byte[] hRgb24, /* []：保存的R-G-B图像 */
                               int[] hAttr, /* [4]：宽-高-色彩-DPI */
                               int bToGray /* 0=原样，1=变成灰度 */
    );

    /**
     * 按宽-高-色彩(0=彩，1=灰)-DPI，从图像数据提取特征值
     */
    public native int FeatureX(byte[] hBinMut, /* []：特征值区域 */
                               byte[] hRgb24, /* []：输入的R-G-B图像 */
                               int[] hAttr, /* [4]：宽-高-色彩-DPI */
                               int nOptCfg /* 附加配置值，请给0 */
    );

    /**
     * 输入DAT全路径文件名，加载到内存，并初始化
     */
    public native int LodDat(String szDat /* 数据文件，给""释放内存 */
    );

    /**
     * 由给定图像分析出49点轮廓及其它信息，可用于状态判定
     */
    public native int Discover(int[] hFattr, /* 属性整形数组 */
                               Bitmap srcBmp, /* 输入位图像 */
                               int nOptCfg, /* 附加配置值 */
                               long hEnvSet /* 环境的句柄 */
    );

    /**
     * 转换YUV420SP图像，到R-G-B图像(由外部分配提供内存空间)
     */
    public native int YuvToRgb(byte[] h420sp, /* []：输入YUV420SP图像 */
                               int nWd, int nHi, /* 图像的宽度和高度 */
                               byte[] hRgb24 /* []：输出转换后的RGB图像 */
    );

    /**
     * 旋转90度(顺1逆-1)，-1000左右-1001上下，宽高会变，更新R-G-B图像区
     */
    public native int DoRotate(byte[] hRgb24, /* []：待旋转的R-G-B图像 */
                               int[] hWdHi, /* [2]：图像的宽度和高度 */
                               int nRot /* 旋转90度，顺1逆-1，0=不转 */
    );

    /**
     * 由给定图像分析出49点轮廓及其它信息，可用于状态判定
     */
    public native int DiscoverX(int[] hFattr, /* STFACEATTR按int数组 */
                                byte[] hRgb24, /* []：待检测的R-G-B图像 */
                                int nWd, int nHi, /* 图像的宽度和高度 */
                                int nOptCfg, /* 附加配置值 */
                                long hEnvSet /* 环境的句柄 */
    );

    /**
     * 按旋转变换XY坐标及宽高，旋转90度(顺1逆-1)，-1000左右-1001上下
     */
    public native int Transform(int[] hXY, /* [2]：X、Y，待变换XY */
                                int nWd, int nHi, /* 所在图像的宽度和高度 */
                                int nRot, /* 旋转90度，顺1逆-1，0=不转 */
                                int[] hWdHi /* 返回旋转后的宽度和高度 */
    );

    /**
     * 将R-G-B格式数据，转换成文件的内存映像，返回实际使用字节数量
     */
    public native int RgbToJpg(byte[] hRgb24, /* []：源始的R-G-B图像 */
                               int nWd, int nHi, /* 图像的宽度和高度 */
                               byte[] hFimage, /* []：文件内存映像，须足大 */
                               int nFmt, int nQty /* 0=JPG/1=BMP，质量0默认85 */
    );
    /*-------------------------------------------------*/


    /**
     * 从指定BMP文件读取特征
     */
    public int getMinutFromBmp(int nMode, String szDir, String szNam, byte[] byMut, int nId) {
        //TextView ptv = (TextView)findViewById(nId);
        //if(ptv == null) return (-14); /* 没有找到输出框 */
        int nRet = -18;
        String sFile = szDir + szNam;

        if (nMode == 0) /* BMP文件 */ {
            nRet = Fminut(sFile, byMut, 0, 0);
        } else if (nMode == 1) /* 测试加载到[]，再取特征，再保存到BMP文件 */ {
            int[] hAtr = {0, 0, 0, 0};
            nRet = AttrFbmp(sFile, hAtr);

            if (nRet > 0) /* 返回所需RGB大小 */ {
                byte[] hRgb24 = new byte[nRet];
                nRet = LoadFbmp(sFile, hRgb24, 0);

                if (nRet > 0) /* 加载BMP到[]里，实际所用大小 */ {
                    nRet = FeatureX(byMut, hRgb24, hAtr, 0);

                    if (nRet > 0) /* []取特征，实际所用大小 */ {
                        nRet = SaveFbmp(sFile + "_new.BMP", hRgb24, hAtr, 1);
                    }
                }
            }
        }
        //ptv.setText(szNam + "=" + nRet); /* 显示返回状态值 */
        return (nRet);
    }


    /*******************
     * 后续封装算法调用
     ***********************/
    Context context;
    byte[] hFea;
    byte[] hTpl;

    public byte[] gethTpl() {
        return hTpl;
    }

    private volatile static Algorithm instance = null;
    private final String TAG = Algorithm.class.getSimpleName();

    public static Algorithm getInstance(Context context) {
        if (instance == null) {
            instance = new Algorithm(context);
        }
        return instance;
    }

    private Algorithm(Context context) {
        this.context = context;
//        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/\0";
        String path = context.getFilesDir().getPath() + "/";
        if(com.YinanSoft.phoneface.Constants.isDebugLib)
            GenAct(3, path);
        else GenAct(3, path.getBytes());
    }

    public void init(String code) {
        if (com.YinanSoft.phoneface.Constants.isDebugLib) {
            //测试库
            int[] arrayOfInt1 = new int[1];
            int[] arrayOfInt2 = new int[1];
            short[] arrayOfShort = new short[4];
            int nRet = Version(arrayOfInt1, arrayOfInt2, arrayOfShort);
            Log.e(this.TAG, "初始化测试算法 : nRet=" + nRet + ", nMbsz=" + arrayOfInt1[0] + ", nMath=" + arrayOfInt2[0] + ", v" + arrayOfShort[0] + "." + arrayOfShort[1] + "." + arrayOfShort[2] + "." + arrayOfShort[3]);
            Log.e(this.TAG, "设置授权码 : " + GenAct(1, "TechShino"));
            String version = arrayOfShort[0] + "." + arrayOfShort[1] + "." + arrayOfShort[2] + "." + arrayOfShort[3];
            loadAssetFile(version);
            this.hFea = new byte[arrayOfInt1[0]];
            this.hTpl = new byte[arrayOfInt1[0]];
            mActivationResult = "ok";
        } else {
            //正式库
            int nRet = -119;
            int[] nMbsz = new int[1];
            int[] nMath = new int[1];
            short[] nAvrsn = new short[4];

            nRet = Version(nMbsz, nMath, nAvrsn);//tt add
            Logs.d(TAG, "初始化算法 : nRet=" + nRet + ", nMbsz=" + nMbsz[0] + ", nMath=" + nMath[0] + ", v" + nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3]);
            if (com.YinanSoft.phoneface.util.StringUtils.length(code) != 16) return;
            mActivationResult = GenAct(1, code.getBytes());
            Logs.d(TAG, "设置授权码 : " + mActivationResult);//设置授权码

            if (!"ok".equals(mActivationResult)) {
                return;
            }

            String version = nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3];
            loadAssetFile(version);

            int featureSize = Init(phEnvSet);
            ENV_SET = phEnvSet[0];
            Logs.d(TAG, "初始化... 特征大小：" + featureSize + " 句柄：" + ENV_SET);
            hFea = new byte[featureSize]; /* 按最大长度分配两个特征内存 */
            hTpl = new byte[nMbsz[0]];
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
        if(ENV_SET != 0)
            Close(ENV_SET);
        ENV_SET = 0;
    }


    public void loadAssetFile(String version) {
        String sdmdat = context.getFilesDir().getAbsolutePath() + "/" + DAT_NAME;
        File file = new File(sdmdat);
        int versionCode = getVersionCode();
        if (versionCode > getInt(VERSION_CODE) || !getString(VERSION).equals(version)) {
            if (file.exists()) {
                boolean bool = file.delete();
                Logs.d(TAG, "删除算法数据库:" + bool);// -18
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
            Logs.d(TAG, "加载算法数据库完成:" + nLoadResult);// -18
        } catch (Exception e) {
            Logs.d(TAG, "加载算法数据库失败，程序异常");
            e.printStackTrace();
        }
    }

    //业务逻辑，虽说写到这里会比较乱，但是先这样写，然后重构

    //找脸

    public com.YinanSoft.phoneface.common.OtcRect findSingleFace(Bitmap mCameraBitmap, int aWidth, int aHeight) {
        // return this.native_opencv_findfaceinimage(aPixels, aWidth, aHeight);
        int[] nPosit = new int[1];
        com.YinanSoft.phoneface.common.OtcRect[] tcRct = Detect(nPosit, mCameraBitmap, 0, 0);
        if (nPosit[0] >= 0) // 确认函数执行成功！
        {
            /*
             * return new Rect(tcRct[0].nLft, tcRct[0].nTop, tcRct[0].nLft +
			 * tcRct[0].uWid, tcRct[0].nTop + tcRct[0].uHei);
			 */
            return tcRct[0];
        }
        return null;
    }

    //注册脸
    public boolean GetMinutia(Bitmap bitmap) {
        int nRet = Minutia(hFea, bitmap, 0, 0);
        if (nRet > 0 && hFea != null) {
            Logs.d(TAG, nRet + "  " + hFea.toString());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
            Editor editor = prefs.edit();
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

    public String getFeature(Bitmap bitmap) {
        int nRet = Minutia(hFea, bitmap, 0, ENV_SET);
        if (nRet > 0 && hFea != null) {
            return Base64.encodeToString(hFea, 0);
        }
        return null;
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
                Logs.d(TAG, "分数为  " + nRet);
                return (nRet >= com.YinanSoft.phoneface.Constants.VERIFY_SCORE);
            }
        }
        return false;
    }

    public void saveString(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
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
        Editor editor = sharedPreferences.edit();
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

 	/*-------------------------------------------------*/

/*    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm);
    	GenAct(1, "TechShino");  设置授权码 
        int nRet = -119;
    	int[] nMbsz = new int[1];
    	TextView ptv = (TextView)findViewById(R.id.tvVersion);
        if(ptv != null)  版本号，及特征最大长度 
        {
        	int[] nMath = new int[1];
        	short[] nAvrsn = new short[4];
        	
        	nRet = Version(nMbsz, nMath, nAvrsn);
        	ptv.setText("nRet=" + nRet + ", nMbsz=" + nMbsz[0] + ", nMath=" + nMath[0] +
        		", v" + nAvrsn[0] + "." + nAvrsn[1] + "." + nAvrsn[2] + "." + nAvrsn[3]);
        }
        
        byte[] hVerBin = new byte[nMbsz[0]];  按最大长度分配两个特征内存 
        byte[] hRegBin = new byte[nMbsz[0]];
        String szPath = Environment.getExternalStorageDirectory().getPath() + "/";
        String szVfile = "WHX.bmp", szRfile = "SH.bmp";
        final int doMode = 1;  0=BMP文件，1=BMP->[]->BMP 
        
        getMinutFromBmp(doMode, szPath, szVfile, hVerBin, R.id.tvFminut0);
        
        getMinutFromBmp(doMode, szPath, szRfile, hRegBin, R.id.tvFminut1);
        
        ptv = (TextView)findViewById(R.id.tvMatch);
        if(ptv != null)  比对两枚特征  
        {
    		nRet = Match(hVerBin, hRegBin, 0, 0);
    		ptv.setText("Match=" + nRet);
        }
    }*/


};
