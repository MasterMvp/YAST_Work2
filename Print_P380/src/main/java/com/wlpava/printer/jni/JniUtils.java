//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wlpava.printer.jni;

public class JniUtils {
    static {
        System.loadLibrary("printer");
    }

    public JniUtils() {
    }

    public static native String print1();

    public static native String print2();

    public static native String pageSetup1();

    public static native String pageSetup2();

    public static native String drawBox();

    public static native String drawLine1();

    public static native String drawLine2();

    public static native String drawText1();

    public static native String drawText2();

    public static native String drawText3();

    public static native String drawText4();

    public static native String drawText5();

    public static native String drawText6();

    public static native String drawText7();

    public static native String drawText8();

    public static native String drawText9();

    public static native String drawText10();

    public static native String drawText11();

    public static native String drawText12();

    public static native String drawBarCode1();

    public static native String drawBarCode2();

    public static native String drawQrCode1();

    public static native String drawQrCode2();

    public static native String drawGraphic();

    public static native String printerPrefix();

    public static native byte[] printerStatus();

    public static native byte[] feed();

    public static native byte[] setPaperFeedLength();

    public static native byte[] checkID1();

    public static native byte[] checkID2();

    public static native byte[] sendIdCardCommand1();

    public static native byte[] sendIdCardCommand2();

    public static native byte[] sendIdCardCommand3();

    public static native byte[] sendIdCardCommand4();
}
