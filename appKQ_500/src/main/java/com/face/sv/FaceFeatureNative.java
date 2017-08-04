package com.face.sv;

public class FaceFeatureNative {
    private static FaceFeatureNative mNative = null;

    static {
        System.loadLibrary("THFeature");
        System.loadLibrary("faceFeature");
    }

    public FaceFeatureNative() {
    }

    public static FaceFeatureNative getInstance() {
        if(mNative == null) {
            mNative = new FaceFeatureNative();
        }

        return mNative;
    }

    public native void setDir(String var1, String var2);

    public native boolean InitFaceFeature(int var1);

    public native void ReleaseFaceFeature();

    public native byte[] getFaceFeatureByRect(short var1, byte[] var2, int[] var3, int var4, int var5);

    public native int compareFeature(byte[] var1, byte[] var2);
}
