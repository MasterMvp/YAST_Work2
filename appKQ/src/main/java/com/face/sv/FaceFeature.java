package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;
import com.face.sv.FaceFeatureNative;

public class FaceFeature {
    private static final String TAG = "FaceFeature";
    private FaceFeatureNative mFeatureNative = new FaceFeatureNative();
    private byte[] mutexFeature = new byte[0];
    private byte[] mutexCompare = new byte[0];

    public FaceFeature() {
    }

    public void setDir(String libDir, String tempDir) {
        this.mFeatureNative.setDir(libDir, tempDir);
    }

    public boolean initFaceFeatureLib(int chlNum) {
        boolean ret = this.mFeatureNative.InitFaceFeature(chlNum);
        return ret;
    }

    public void releaseFaceFeatureLib() {
        this.mFeatureNative.ReleaseFaceFeature();
    }

    public byte[] getFaceFeatureFromRGB(short chlID, byte[] rgb24, int[] rect, int width, int height) {
        Object feature = null;
        byte[] var7 = this.mutexFeature;
        synchronized(this.mutexFeature) {
            byte[] feature1 = this.mFeatureNative.getFaceFeatureByRect(chlID, rgb24, rect, width, height);
            return feature1;
        }
    }

    public byte[] getFaceFeatureFromBitmap(short chlID, Bitmap bmp, int[] rect) {
        this.log("getFaceFeatureFromBitmap(Bitmap bmp)");
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] feature = null;
        if(rect != null && 4 == rect.length && this.mFeatureNative != null) {
            int[] pixels = new int[width * height];
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] rgb24 = new byte[width * height * 3];

            for(int i = 0; i < width * height; ++i) {
                int r = pixels[i] >> 16 & 255;
                int g = pixels[i] >> 8 & 255;
                int b = pixels[i] & 255;
                rgb24[i * 3] = (byte)(b & 255);
                rgb24[i * 3 + 1] = (byte)(g & 255);
                rgb24[i * 3 + 2] = (byte)(r & 255);
            }

            byte[] var14 = this.mutexFeature;
            synchronized(this.mutexFeature) {
                feature = this.mFeatureNative.getFaceFeatureByRect(chlID, rgb24, rect, width, height);
            }
        }

        return feature;
    }

    public float compareFaces(short chlID1, Bitmap bmp1, int[] rect1, short chlID2, Bitmap bmp2, int[] rect2) {
        this.log("compareFaces(Bitmap bmp1, Bitmap bmp2)");
        float ret = -1.0F;
        Object feature1 = null;
        Object feature2 = null;
        byte[] feature11 = this.getFaceFeatureFromBitmap(chlID1, bmp1, rect1);
        if(feature11 != null && feature11.length > 1) {
            byte[] feature21 = this.getFaceFeatureFromBitmap(chlID2, bmp2, rect2);
            if(feature21 != null && feature21.length > 1) {
                byte[] var10 = this.mutexCompare;
                synchronized(this.mutexCompare) {
                    ret = (float)this.mFeatureNative.compareFeature(feature11, feature21);
                }
            } else {
                this.log("feature2 == null && feature2.length <= 1");
            }
        } else {
            this.log("feature1 == null && feature1.length <= 1");
        }

        return ret;
    }

    public int compareFeatures(byte[] feature1, byte[] feature2) {
        this.log("compareFeatures(final float[] feature1, final float[] feature2)");
        int ret = -1;
        if(this.mFeatureNative != null) {
            if(feature1 != null && feature1.length > 1) {
                if(feature2 != null && feature2.length > 1) {
                    byte[] var4 = this.mutexCompare;
                    synchronized(this.mutexCompare) {
                        ret = this.mFeatureNative.compareFeature(feature1, feature2);
                    }
                } else {
                    this.log("feature2 == null && feature2.length <= 1");
                }
            } else {
                this.log("feature1 == null && feature1.length <= 1");
            }
        }

        return ret;
    }

    private void log(String msg) {
        Log.d("FaceFeature", msg);
    }
}
