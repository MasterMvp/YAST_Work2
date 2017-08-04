package com.smartshino.face;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.YinanSoft.phoneface.Constants;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
import com.YinanSoft.phoneface.decode.DecodeHandler;
import com.YinanSoft.phoneface.util.FileUtils;
import com.YinanSoft.phoneface.util.JSONUtils;
import com.YinanSoft.phoneface.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhi on 2016/3/31.
 */
public class FaceSDK {

    private static final String TAG = FaceSDK.class.getSimpleName();
    private static final String LICENSE_DIR = Environment.getExternalStorageDirectory() + "/techshinoLicense";
    private static final String LICENSE_FILE_NAME = "license";
    private static final String LICENSE_FILE_PATH = LICENSE_DIR + File.separator + LICENSE_FILE_NAME;

    private static SsNow sAlgorithm;

    private static Context sContext;

    private static boolean isInitSuccess = false;

    public static int[] faceRect = null;

    public static boolean init(Context context) {
        sContext = context;
        String currDate = new SimpleDateFormat("yyyyMM").format(new Date());
        if (currDate.equals("201704") || currDate.equals("201705") || currDate.equals("201706")) {
            Constants.isDebugLib = true;
        } else Constants.isDebugLib = false;

        if (Constants.isDebugLib)
            System.loadLibrary("SsNowTime");//测试库2016-03-xx
        else
            System.loadLibrary("SsNow");  //正式库

        sAlgorithm = SsNow.getInstance(context);

        isInitSuccess = isGranted();
        return isInitSuccess;
    }

    public static void Deinit() {
        if (isInitSuccess) {
            sAlgorithm.deinit();
            isInitSuccess = false;
        }
    }

    public static String getFeature(Bitmap bitmap) {
        if (sAlgorithm == null) {
            Logs.v("match3:");
            return null;
        }
        return sAlgorithm.getFeature(bitmap);
    }

    private static boolean isGranted() {

        // SharedPreferences查找授权码
        if (isGrantedBySharedPreferences())
            return true;

        // 本地文件查找授权码
        if (isGrantedByDisk())
            return true;

        // 从assets中搜索
        if (isGrantedByAssets())
            return true;

        if (Constants.isDebugLib) {
            return true;
        }

        return false;
    }

    private static boolean isGrantedByAssets() {
        Logs.w(TAG, "从assets中搜索授权码...");
        String[] licenses = getLicensesFromAssets();
        if (licenses == null || licenses.length == 0)
            return false;
        for (String code : licenses) {
            sAlgorithm.init(code);
            if (isOk()) {
                saveString(LICENSE_FILE_NAME, code);
                return true;
            }
        }
        return false;
    }

    private static boolean isGrantedByDisk() {
        Logs.w(TAG, "本地文件查找授权码...");
        File file = new File(LICENSE_DIR);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
        String[] licensesByDisk = getLicensesFromDisk();
        if (licensesByDisk == null || licensesByDisk.length == 0)
            return false;
        for (String code : licensesByDisk) {
            sAlgorithm.init(code);
            if (isOk()) {
                saveString(LICENSE_FILE_NAME, code);
                return true;
            }
        }
        return false;
    }

    private static boolean isGrantedBySharedPreferences() {
        Logs.w(TAG, "SharedPreferences查找授权码...");
        String license = getString(LICENSE_FILE_NAME);
        if (!StringUtils.isBlank(license)) {
            sAlgorithm.init(license);
            if (isOk()) return true;
        }
        return false;
    }

    private static boolean isOk() {
        return "ok".equals(sAlgorithm.getmActivationResult());
    }

    /**
     * 获取授权码（从文件中）
     *
     * @return
     */
    private static String getLicenseCode() {
        String code = getMapFromFile().get(getLocalMacAddressFromWifiInfo(sContext));
        return code;
    }

    private static String[] getLicensesFromDisk() {
        StringBuilder fileContent = FileUtils.readFile(LICENSE_FILE_PATH, "UTF-8");
        return fileContent == null ? null : fileContent.toString().split(",");
    }

    private static String[] getLicensesFromAssets() {
        BufferedReader reader;
        StringBuilder fileContent = new StringBuilder("");
        try {
            InputStream is = sContext.getResources().getAssets().open(LICENSE_FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            return null;
        }
        return fileContent.toString().split(",");
    }

    /**
     * 从文件中读取授权列表
     *
     * @return
     */
    private static Map<String, String> getMapFromFile() {
        Map<String, String> map = new HashMap<>();
        BufferedReader reader;
        StringBuilder fileContent = new StringBuilder("");
        try {
            InputStream is = sContext.getResources().getAssets().open(LICENSE_FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            return map;
        }

        map = JSONUtils.parseKeyAndValueToMap(fileContent.toString());
        return map;
    }

    public static void saveString(String key, String value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }


    private String getUniqueIdByMd5(Context context) {
        String combineStr = getDeviceId(context) + getAndroidId(context) + getLocalMacAddressFromWifiInfo(context);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(combineStr.getBytes(), 0, combineStr.length());

        // get md5 bytes
        byte md5Data[] = m.digest();
        // create a hex string
        String uniqueID = new String();

        for (int i = 0; i < md5Data.length; i++) {
            int b = (0xFF & md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                uniqueID += "0";
            // add number to string
            uniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        uniqueID = uniqueID.toUpperCase();
        return uniqueID;
    }

    /**
     * The IMEI: 仅仅只对Android手机有效:
     *
     * @param context
     * @return
     */
    private String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei == null ? "" : szImei;
    }

    private String getDevice() {
        return Build.DEVICE;
    }

    /**
     * The Android ID
     *
     * @return
     */
    private String getAndroidId(Context context) {
        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return m_szAndroidID == null ? "" : m_szAndroidID;
    }

    /**
     * The WLAN MAC Address string
     *
     * @param context
     * @return
     */
    private static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC == null ? "" : m_szWLANMAC;
    }

    public static SsNow getAlgorithm() {
        return sAlgorithm;
    }

    public static void setDebug(boolean isDebug) {
        Logs.setsIsLogEnabled(isDebug);
    }

    public interface FaceInitListener {
        void onResult(boolean result);
    }

    /**
     * 图片宽高
     */
    public static int[] hWdHi = new int[2];
    /**
     * 图片解析Rgb
     */
    public static byte[] faceRgb24 = null;

    //找脸算法返回值，小于等于0失败
    public static int mDecodeStatus;

    public static Stfaceattr decodeBitmap(byte[] data, int width, int height, int orientation) {


        long start = System.currentTimeMillis();
        hWdHi[0] = width;
        hWdHi[1] = height;

        faceRgb24 = new byte[width * height * 3]; // 分配一个彩色RGB数组[nWd*nHi*3]
        sAlgorithm.YuvToRgb(data, width, height, faceRgb24);
        long end = System.currentTimeMillis();
        long timeYuvToRgb = end - start;
        Logs.d(TAG, "native YuvToRgb花费时间 " + timeYuvToRgb);

        // 初始化加载点
        Stfaceattr stfaceattr = new Stfaceattr();
        int[] hFattr = stfaceattr.gethFattr();
        stfaceattr.setSize(115 * 4);
        stfaceattr.setOcclusion(1);// 遮挡物
        // 初始化图像信息

        //orientation = mSurfaceView.getCameraManager().getOrientation();
        Logs.i(TAG, "id:" + DecodeHandler.mCameraId + "orientation:" + orientation);
        long rotateStart = System.currentTimeMillis();
        if (!Constants.isDebug) {
            if (DecodeHandler.mCameraId == 0) {
                switch (orientation) {
                    case 90:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 1);
                        break;
                    case 0:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 0);
                        break;
                    case 270:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 3);
//                        sAlgorithm.DoRotate(faceRgb24, hWdHi, -1000);
                        break;
                    case 180:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 2);
                        break;
                    default:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
                        break;
                }
            } else {
                switch (orientation) {
                    case 90:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                        break;
                    case 0:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, -1000);//0);
                        break;
                    case 270:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 3);
                        break;
                    case 180:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 2);
                        break;
                    default:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
                }
            }
        } else {
            switch (orientation) {
                case 180:
                    sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                    break;
                default:
                    sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
            }
        }
        long rotateStop = System.currentTimeMillis();
        Logs.d(TAG, "旋转图像花费时间 " + (rotateStop - rotateStart) + "MS");
        stfaceattr.setHeadPosi(1, 0, 0);


        long DiscoverXStart = System.currentTimeMillis();

        FaceSDK.mDecodeStatus = sAlgorithm.DiscoverX(hFattr, faceRgb24, hWdHi[0],
                hWdHi[1], 0, SsNow.ENV_SET);// 换到正常的RGB空间后，就可以检测DiscoverX了。

        hFattr = null;
        // 这个是画框的矩形
        long DiscoverXStop = System.currentTimeMillis();
        Logs.d(TAG, "检测接口: " + FaceSDK.mDecodeStatus + "花费时间 +"
                + (DiscoverXStop - DiscoverXStart) + " ms");
        long end1 = System.currentTimeMillis();
        long oneFrameTime = end1 - start;
        Logs.d(TAG, "检测单帧花费时间 " + oneFrameTime + " ms");

//        saveBitmap();
        return stfaceattr;
    }

    public static synchronized Stfaceattr decodeBitmap(byte[] data, int width, int height, int orientation, Bitmap[] faceBmp) {


        long start = System.currentTimeMillis();
        hWdHi[0] = width;
        hWdHi[1] = height;

        byte[] faceRgb24 = new byte[width * height * 3]; // 分配一个彩色RGB数组[nWd*nHi*3]
        sAlgorithm.YuvToRgb(data, width, height, faceRgb24);
        long end = System.currentTimeMillis();
        long timeYuvToRgb = end - start;
//        Logs.d(TAG, "native YuvToRgb花费时间 " + timeYuvToRgb);

        // 初始化加载点
        Stfaceattr stfaceattr = new Stfaceattr();
        int[] hFattr = stfaceattr.gethFattr();
        stfaceattr.setSize(115 * 4);
        stfaceattr.setOcclusion(1);// 遮挡物
        // 初始化图像信息

        //orientation = mSurfaceView.getCameraManager().getOrientation();
//        Logs.i(TAG, "mCameraId = " + DecodeHandler.mCameraId + "orientation:" + orientation);
        long rotateStart = System.currentTimeMillis();
        if (!Constants.isDebug) {
            if (DecodeHandler.mCameraId == 0) {
                switch (orientation) {
                    case 90:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 1);
                        break;
                    case 0:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 0);
                        break;
                    case 270:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 3);
                        break;
                    case 180:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 2);
                        break;
                    default:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
                        break;
                }
            } else {
                switch (orientation) {
                    case 90:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                        break;
                    case 0:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 0);
                        break;
                    case 270:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 3);
                        break;
                    case 180:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, 2);
                        break;
                    default:
                        sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
                }
            }
        } else {
            switch (orientation) {
                case 180:
                    sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                    break;
                default:
                    sAlgorithm.DoRotate(faceRgb24, hWdHi, orientation);
            }
        }
        long rotateStop = System.currentTimeMillis();
//        Logs.d(TAG, "旋转图像花费时间 " + (rotateStop - rotateStart) + "MS");
        stfaceattr.setHeadPosi(1, 0, 0);


        long DiscoverXStart = System.currentTimeMillis();

        FaceSDK.mDecodeStatus = sAlgorithm.DiscoverX(hFattr, faceRgb24, hWdHi[0],
                hWdHi[1], 0, SsNow.ENV_SET);// 换到正常的RGB空间后，就可以检测DiscoverX了。

        //if(com.YinanSoft.phoneface.FaceSDK.mDecodeStatus > 0)
        faceBmp[0] = prepareBitmap(faceRgb24, hWdHi);

        hFattr = null;
        // 这个是画框的矩形
//        long DiscoverXStop = System.currentTimeMillis();
//        Logs.d(TAG, "检测接口: " + mDecodeStatus + "花费时间 +" + (DiscoverXStop - DiscoverXStart) + " ms");
//        long end1 = System.currentTimeMillis();
//        long oneFrameTime = end1 - start;
//        Logs.d(TAG, "检测单帧花费时间 " + oneFrameTime + " ms");
        //saveBitmap();
        return stfaceattr;
    }

    static int index = 0;

    private static void saveBitmap() {
        Logs.i(TAG, "saveBitmap保存图像....................................  ");
        // sAlgorithm.DoRotate(faceRgb24, hWdHi, -1000);
        final byte[] faceJpg = new byte[hWdHi[0] * hWdHi[1] * 3 + 1024];
        sAlgorithm.RgbToJpg(faceRgb24, hWdHi[0], hWdHi[1], faceJpg, 0, 0);
        final Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceJpg, 0,
                faceJpg.length);

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/faceJpg" + index + ".png");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        index++;
    }

    public static Bitmap prepareBitmap(byte[] hRgb24, int[] hWdHi1) {
        //sAlgorithm.DoRotate(hRgb24, hWdHi1, -1000);
        final byte[] faceJpg = new byte[hWdHi1[0] * hWdHi1[1] * 3 + 1024];
        sAlgorithm.RgbToJpg(hRgb24, hWdHi1[0], hWdHi1[1], faceJpg, 0, 0);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = hWdHi1[0] / 400;
//        Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceJpg, 0, faceJpg.length, options);
//        return faceBitmap;
        return BitmapFactory.decodeByteArray(faceJpg, 0, faceJpg.length, options);
    }

    public static Bitmap Yuv2Bmp(byte[] data, int width, int height) {
        byte[] faceRgb24 = new byte[width * height * 3]; // 分配一个彩色RGB数组[nWd*nHi*3]
        sAlgorithm.YuvToRgb(data, width, height, faceRgb24);
        final byte[] faceJpg = new byte[width * height * 3 + 1024];
        sAlgorithm.RgbToJpg(faceRgb24, width, height, faceJpg, 0, 0);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = width / 400;
        return BitmapFactory.decodeByteArray(faceJpg, 0, faceJpg.length, options);
    }

    public static int match(String desF, String srcF) {
        if (desF == null || srcF == null) {
//            Logs.v("FaceSDK match2:");
            return 0;
        }
//        Logs.v("FaceSDK match5:" + SsNow.ENV_SET);
        byte[] desFeature = Base64.decode(desF, 0);
        byte[] srcFeature = Base64.decode(srcF, 0);

        return SsNow.Match(srcFeature, desFeature, 0, SsNow.ENV_SET);
    }
}
