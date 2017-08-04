package com.YinanSoft.phoneface;

public class Constants {
    /**
     * 以下为程序应用常量信息
     */
    public static String SERVER_LOCATION = "http://192.168.86.194:8081/testpf/jaxrs/testpf/";
    public static String SERVER_IP_PORT = "jnyytech.vicp.cc:8899";
    //	public static String SERVER_LOCATION = "http://127.0.0.1:8080/phoneface/jaxrs/PSFR/";
    public static String ACTIVE_SERVER = "127.0.0.1";
    public static int ACTIVE_SERVER_PORT = 31313;
    /**
     * 常量信息
     */
    public static long TIME_OUT_DELAY_MS = 40 * 1000L; // 人脸捕捉时超时30秒后返回
    public static long NET_TIME_OUT_DELAY_MS = 30 * 1000L; // 人脸捕捉时超时30秒后返回
    public static int HTTP_CONNECTION_TIME_OUT_MS = 5 * 1000; // 网络链接超时5秒
    public static int HTTP_REQUEST_TIME_OUT_MS = 10 * 1000; // 通讯超时10秒
    public static Boolean isShowFrame = false;
    public static Boolean isDebug = false;
    public static Boolean isDebugLib = false;
    public static enum Algorithm {tesoface, facesv, tesoface2};
    public static Algorithm DefaultAlgorithm = Algorithm.tesoface2;
    /**
     * 重要信息
     */
    public static final long VERIFY_SCORE = 85;
    /**
     * 上一个版本的常量
     */

    public static final int RESULT_REG_FAIL = 0; // 注册失败
    public static final int RESULT_REG_FINISH = 1; // 注册成功
    public static final int RESULT_VAILDATE_SUCCESS = 2; // 验证成功
    public static final int RESULT_VAILDATE_FAIL = 3; // 验证失败
    public static final int RESULT_TIMEOUT = 4; // 验证失败
    public static final int RESULT_NET_ERROR = 5; // 网络错误
    public static final int RESULT_IS_PHOTO = 6; // 网络错误

}
