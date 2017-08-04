package com.YinanSoft.phoneface;

import android.content.Context;
import android.graphics.Bitmap;

import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
public class FaceSDK {

    private static final String TAG = FaceSDK.class.getSimpleName();
    private static Context sContext;
    public static int mDecodeStatus;
    public static int facing;//该选项只在606a设备上使用，用于摄像头翻转时角度判断

    /**
     * 图片宽高
     */
    public static int[] hWdHi = new int[2];
    /**
     * 图片解析Rgb
     */
    public static byte[] faceRgb24 = null;

    //初始化算法库
    public static boolean init(Context context) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            return com.techshino.tesoface.FaceSDK.init(context);
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            return com.smartshino.face.FaceSDK.init(context);
        } else return com.face.sv.FaceSDK.init(context);
    }

    public static void Deinit() {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
             com.techshino.tesoface.FaceSDK.Deinit();
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
             com.smartshino.face.FaceSDK.Deinit();
        } //else  com.face.sv.FaceSDK.Deinit();
    }

    //生成特征值
    public static String getFeature(Bitmap bitmap) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            return com.techshino.tesoface.FaceSDK.getFeature(bitmap);
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            return com.smartshino.face.FaceSDK.getFeature(bitmap);
        } else return com.face.sv.FaceSDK.getFeature(bitmap);
    }

    //比较特征值
    public static int match(String desF, String srcF) {
        float nRet;
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            nRet = com.techshino.tesoface.FaceSDK.match(desF, srcF);
            if(nRet > 0)    nRet /= 1.27;
            Logs.v("FaceSDK match:" + nRet);
            return (int)nRet;
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            nRet = com.smartshino.face.FaceSDK.match(desF, srcF);
            if(nRet > 0)    nRet /= 1.27;
            Logs.v("FaceSDK match:" + nRet);
            return (int)nRet;
        } else return com.face.sv.FaceSDK.match(desF, srcF);
    }


    //将YUV数据解码成接口可识别数据，并检测人脸
    public static Stfaceattr decodeBitmap(byte[] data, int width, int height, int orientation) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            final Stfaceattr att =  com.techshino.tesoface.FaceSDK.decodeBitmap(data, width, height, orientation);
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[0];
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[1];
//            faceRgb24 = com.smartshino.face.FaceSDK.faceRgb24;
            mDecodeStatus = com.techshino.tesoface.FaceSDK.mDecodeStatus;
            return att;
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            final Stfaceattr att = com.smartshino.face.FaceSDK.decodeBitmap(data, width, height, orientation);
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[0];
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[1];
//            faceRgb24 = com.smartshino.face.FaceSDK.faceRgb24;
            mDecodeStatus = com.smartshino.face.FaceSDK.mDecodeStatus;
            return att;
        } else {
            final Stfaceattr att =   com.face.sv.FaceSDK.decodeBitmap(data, width, height, orientation);
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[0];
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[1];
//            faceRgb24 = com.smartshino.face.FaceSDK.faceRgb24;
            mDecodeStatus = com.face.sv.FaceSDK.mDecodeStatus;
            return att;
        }
    }

    public static Stfaceattr decodeBitmap(byte[] data, int width, int height, int orientation, Bitmap[] faceBmp) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            final Stfaceattr att =  com.techshino.tesoface.FaceSDK.decodeBitmap(data, width, height, orientation, faceBmp);
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[0];
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[1];
//            faceRgb24 = com.smartshino.face.FaceSDK.faceRgb24;
            mDecodeStatus = com.techshino.tesoface.FaceSDK.mDecodeStatus;
            return att;
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            final Stfaceattr att =  com.smartshino.face.FaceSDK.decodeBitmap(data, width, height, orientation, faceBmp);
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[0];
//            hWdHi[0] = com.smartshino.face.FaceSDK.hWdHi[1];
//            faceRgb24 = com.smartshino.face.FaceSDK.faceRgb24;
            mDecodeStatus = com.smartshino.face.FaceSDK.mDecodeStatus;
            return att;
        } else {
            final Stfaceattr att =   com.face.sv.FaceSDK.decodeBitmap(data, width, height, orientation);
            faceBmp[0] = com.face.sv.FaceSDK.faceBmp;
            mDecodeStatus = com.face.sv.FaceSDK.mDecodeStatus;
            return att;
        }
    }

    //把RGB转化成Bitmap数据
    public static Bitmap prepareBitmap(byte[] hRgb24, int[] hWdHi1) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface) {
            return com.techshino.tesoface.FaceSDK.prepareBitmap(hRgb24, hWdHi1);
        } else if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            Bitmap bRet = com.smartshino.face.FaceSDK.prepareBitmap(hRgb24, hWdHi1);
            return bRet;
        } else return com.face.sv.FaceSDK.prepareBitmap(hRgb24, hWdHi1);
    }

    public static Bitmap Yuv2Bmp(byte[] data, int width, int height) {
        if(Constants.DefaultAlgorithm == Constants.Algorithm.tesoface2) {
            Bitmap bRet = com.smartshino.face.FaceSDK.Yuv2Bmp(data, width, height);
            return bRet;
        } else return null;
    }
}
