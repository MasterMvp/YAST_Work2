package com.face.sv;

public class FaceDetectNative {
    private static FaceDetectNative mNative = null;

    static {
        System.loadLibrary("THFaceImage");
        System.loadLibrary("faceDetect");
    }

    public FaceDetectNative() {
    }

    public static FaceDetectNative getInstance() {
        if(mNative == null) {
            mNative = new FaceDetectNative();
        }

        return mNative;
    }

    public native void setDir(String var1, String var2);

    public native boolean InitFaceDetect(int var1);

    public native void ReleaseFaceDetect();

    public synchronized native int[] getFacePosition(short var1, byte[] var2, int var3, int var4);
}
