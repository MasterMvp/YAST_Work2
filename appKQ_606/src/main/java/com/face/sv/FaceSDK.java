package com.face.sv;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.YinanSoft.phoneface.Constants;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;

public class FaceSDK {

    private static final String TAG = FaceSDK.class.getSimpleName();
    static private FaceDetect mDetect = null;
    static private FaceFeature mFeature = null;
    private static Context sContext;


    //初始化算法库
    public static boolean init(Context context) {
        boolean isInit = false;
        sContext = context;
        if (mDetect == null)
            mDetect = new FaceDetect();
        if (mFeature == null)
            mFeature = new FaceFeature();

        File file = sContext.getCacheDir();
        String tempDir = file.getAbsolutePath();
        String libDir = tempDir.replace("cache", "lib");

        Log.e(TAG, "tempDir:" + tempDir + " libDir:" + libDir);

        if (mDetect != null) {
            mDetect.releaseFaceDetectLib();
            mDetect.setDir(libDir, tempDir);
            isInit = mDetect.initFaceDetectLib(1);
            Log.e(TAG, "initFaceDetectLib ret=" + isInit);
        }
        if (!isInit) return false;

        isInit = false;
        if (mFeature != null) {
            mFeature.releaseFaceFeatureLib();
            mFeature.setDir(libDir, tempDir);
            isInit = mFeature.initFaceFeatureLib(1);
            Log.e(TAG, "initFaceFeatureLib ret=" + isInit);
        }
        if (!isInit) return false;
        return isGranted();
    }

    //生成特征值
    public static String getFeature(Bitmap bitmap) {
        if (mDetect == null || mFeature == null)
            return null;

        faceBmp = bitmap;
        saveBitmap("getFeature");

        byte[] feature = null;
        int[] ret = mDetect.getFacePositionFromBitmap((short) 0, bitmap);

        if (ret != null && ret[0] > 0) {
            faceRect = new int[4];
            for(int i=0; i<4; i++)
                faceRect[i] = ret[i+1];
            feature = mFeature.getFaceFeatureFromBitmap((short) 0, bitmap, faceRect);
            if (feature != null && feature.length >= 2008)
                return Base64.encodeToString(feature, 0);
        }
        return null;
    }

    //比较特征值
    public static int match(String desF, String srcF) {
        if (desF == null || srcF == null)
            return 0;
        byte[] desFeature = Base64.decode(desF, 0);
        byte[] srcFeature = Base64.decode(srcF, 0);
        int sim = mFeature.compareFeatures(desFeature, srcFeature);
        return sim;
    }

    //算法库认证
    private static boolean isGranted() {
        return true;
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

    public static Bitmap faceBmp = null;

    public static int[] faceRect = null;

    public static int facing = 0;
    //将YUV数据解码成接口可识别数据，并检测人脸
    public static Stfaceattr decodeBitmap(byte[] data, int width, int height, int orientation) {
        Bitmap detBmp;
        long start = System.currentTimeMillis();
        hWdHi[0] = width;
        hWdHi[1] = height;


        YuvImage img = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        img.compressToJpeg(new Rect(0, 0, width, height), 100, output);
        Bitmap bmp = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size());

//        faceBmp = bmp;
//        saveBitmap("findface0_");

        //faceRgb24 = new byte[width * height * 3]; // 分配一个彩色RGB数组[nWd*nHi*3]
        //sAlgorithm.YuvToRgb(data, width, height, faceRgb24);
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
        Logs.i(TAG, "orientation:" + orientation);
        Logs.i(TAG, "facing:" + facing);
        try {
            long rotateStart = System.currentTimeMillis();
            Matrix m = new Matrix();
            if (!Constants.isDebug) {
                if (facing == 0) {//back
                    switch (orientation) {
                        case 0:
                            m.setRotate(0);//sAlgorithm.DoRotate(faceRgb24, hWdHi, 0);
                            break;
                        case 90:
                            m.setRotate(90);//sAlgorithm.DoRotate(faceRgb24, hWdHi, 1);
                            break;
                        case 180:
                            m.setRotate(180);//sAlgorithm.DoRotate(faceRgb24, hWdHi, 2);
                            break;
                        case 270:
                            m.setRotate(270);//sAlgorithm.DoRotate(faceRgb24, hWdHi, 3);
                            break;
                    }
                } else {//front
                    m.postScale(-1,1);
                    switch (orientation) {
                        case 0:
                            m.postRotate(0);//sAlgorithm.DoRotate(faceRgb24, hWdHi, 0);
                            break;
                        case 90:
                            m.postRotate(90);//sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                            break;
                        case 180:
                            m.postRotate(180);
                            break;
                        case 270:
                            m.postRotate(270);
                            break;
                    }
                }
            } else {
                switch (orientation) {
                    case 180:
                        m.postRotate(180);//sAlgorithm.DoRotate(faceRgb24, hWdHi, -1);
                        break;
                }
            }

            detBmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
//            faceBmp = detBmp;
//            saveBitmap("findface1_");

            long rotateStop = System.currentTimeMillis();
            Logs.d(TAG, "旋转图像花费时间 " + (rotateStop - rotateStart) + "MS");


            stfaceattr.setHeadPosi(1, 0, 0);


            long DiscoverXStart = System.currentTimeMillis();

            int[] ret = mDetect.getFacePositionFromBitmap((short) 0, detBmp);
            faceRect = new int[4];
            if (ret != null && ret[0] > 0) {
                mDecodeStatus = ret[0];
                faceBmp = detBmp;
                for(int i=0; i<4; i++) {
                    faceRect[i] = ret[i + 1];
                }
                stfaceattr.setLocFace(faceRect[0],faceRect[1],faceRect[2],faceRect[3]);
            } else {
                mDecodeStatus = -1;
                //faceBmp = null;
                for(int i=0; i<4; i++)
                    faceRect[i] = 0;
            }

            // 这个是画框的矩形
            long DiscoverXStop = System.currentTimeMillis();
            Logs.d(TAG, "检测接口: " + mDecodeStatus + "花费时间 +"
                    + (DiscoverXStop - DiscoverXStart) + " ms");
            long end1 = System.currentTimeMillis();
            long oneFrameTime = end1 - start;
            Logs.d(TAG, "检测单帧花费时间 " + oneFrameTime + " ms");


            return stfaceattr;
        } finally {

            try {
                bmp.recycle();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static int index = 0;

    private static void saveBitmap(String name) {
        Logs.i(TAG, "保存图像....................................  ");
        // sAlgorithm.DoRotate(faceRgb24, hWdHi, -1000);
//        final byte[] faceJpg = new byte[hWdHi[0] * hWdHi[1] * 3 + 1024];
//        sAlgorithm.RgbToJpg(faceRgb24, hWdHi[0], hWdHi[1], faceJpg, 0, 0);
//        final Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceJpg, 0, faceJpg.length);

//        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/faceJpg" + index + ".png");
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/faceJpg" + name + index + ".png");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            faceBmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        index++;
    }

    //把RGB转化成Bitmap数据
    public static Bitmap prepareBitmap(byte[] hRgb24, int[] hWdHi1) {
        //sAlgorithm.DoRotate(hRgb24, hWdHi1, -1000);
//        final byte[] faceJpg = new byte[hWdHi1[0] * hWdHi1[1] * 3 + 1024];
//        sAlgorithm.RgbToJpg(faceRgb24, hWdHi1[0], hWdHi1[1], faceJpg, 0, 0);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = hWdHi1[0] / 400;
//        Bitmap faceBitmap = BitmapFactory.decodeByteArray(
//                faceJpg, 0, faceJpg.length, options);
        if(mDecodeStatus >= 0)
            return faceBmp;
        return null;
    }
}
