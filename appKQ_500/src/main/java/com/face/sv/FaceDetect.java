package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;
import com.face.sv.FaceDetectNative;

public class FaceDetect {
    private static final String TAG = "FaceDetect";
    private FaceDetectNative mDetectNative = new FaceDetectNative();
    private byte[] mutexFace = new byte[0];

    public FaceDetect() {
    }

    public void setDir(String libDir, String tempDir) {
        this.mDetectNative.setDir(libDir, tempDir);
    }

    public boolean initFaceDetectLib(int chlNum) {
        boolean ret = this.mDetectNative.InitFaceDetect(chlNum);
        return ret;
    }

    public void releaseFaceDetectLib() {
        this.mDetectNative.ReleaseFaceDetect();
    }

    public int[] getFacePositionFromGray(short chlID, byte[] gray, int width, int height) {
        this.log("getFacePositionFromGray(byte[] gray, int width, int height)");
        Object value = null;
        byte[] var6 = this.mutexFace;
        synchronized(this.mutexFace) {
            int[] value1 = this.mDetectNative.getFacePosition(chlID, gray, width, height);
            return value1;
        }
    }

    public int[] getFacePositionFromBitmap(short chlID, Bitmap bmp) {
        this.log("getFacePositionFromBitmap(Bitmap bmp)");
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] value = null;
        if(this.mDetectNative != null) {
            int[] pixels = new int[width * height];
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] gray = new byte[width * height];

            for(int i = 0; i < width * height; ++i) {
                int r = pixels[i] >> 16 & 255;
                int g = pixels[i] >> 8 & 255;
                int b = pixels[i] & 255;
                gray[i] = (byte)(306 * r + 601 * g + 117 * b >> 10);
            }

            byte[] var13 = this.mutexFace;
            synchronized(this.mutexFace) {
                value = this.mDetectNative.getFacePosition(chlID, gray, width, height);
            }
        }

        return value;
    }

    private void log(String msg) {
        Log.d("FaceDetect", msg);
    }
}
