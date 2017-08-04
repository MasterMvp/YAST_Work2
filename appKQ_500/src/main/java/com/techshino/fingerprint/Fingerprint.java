package com.techshino.fingerprint;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Fingerprint {

  public final int VID = 0x735F; // Techshino FingerPrint USB Vendor ID
  public static int current_PID = 0x1303;
  //	public boolean root_version = true;
  public boolean root_version = false;
  public final static int CMD_ExtractFeatureAndImage = 0;
  public final static int CMD_ExtractTemplet = 1;
  public final static int CMD_ExtractFeatureMatch = 2;
  public final static int init_only = 3;
  public final static int CMD_OutofTime = 4;

  public static byte[] featureTemplet = new byte[513];
  public static byte[] featureTempletHex = new byte[513];
  public static byte[] featureTemplet0x30 = new byte[513];
  public static int timesToRun = 3;
  public static int imgWidth = 0;
  public static int imgHeight = 0;
  public static int imgSize = 0;
  public static int nResult = 0;

  public int[] ImageAttr = new int[3]; // 0:width 1:height
  public static byte[] featureBufferHex = new byte[513];
  public static byte[] snbuffer = new byte[64];
  public static byte[] Versionbuffer = new byte[64];
  public static byte[] featureBuffer0x30;
  public static byte[] featureBuffer ;
  public static byte[] imageBuffer = new byte[500 * 500 + 1024 + 54];
  public static byte[] imageBuffer1 = new byte[152 * 200 + 1024 + 54];
  public static byte[] TESOimageBuffer = new byte[500 * 500 + 1024 + 54];
  public static byte[] TESOimage;
  public static int cmd = CMD_ExtractFeatureAndImage;
  public int ExtractCount = 0;
  private static NotifyInterface mNCB = null;

  {

//		 root_version = getRootAhth();
    System.loadLibrary("fingerprint");
    Native_Init();

  }
  public synchronized void ExtractFeatureAndImage() {
    cmd = CMD_ExtractFeatureAndImage;
    Log.e("app", "ExtractFeatureAndImage");
    featureBuffer = new byte[300];
    featureBuffer0x30 = new byte[513];
//		nResult = FP_FeatureAndImageExtractAll(2, featureBufferHex, featureBuffer0x30, featureBuffer, imageBuffer,
//				ImageAttr);

    nResult = FP_FeatureAndTESOImageExtractAll(1, featureBufferHex,
        featureBuffer0x30, featureBuffer, imageBuffer,TESOimageBuffer,
        ImageAttr);
    TESOimage = new byte[40536];
//		 System.arraycopy(TESOimageBuffer, 0, TESOimage, 0, 40536);
//		 System.arraycopy(imageBuffer, 0, imageBuffer1, 0, 152*200+1024+54);
//		 System.arraycopy(featureBuffer0x30, 0, featureBuffer, 0, 300);
//		nResult = FP_FeatureAndREDImageExtractISO(2, featureBuffer, imageBuffer, ImageAttr);
//		nResult = FP_FeatureAndImageExtractISO(2, featureBuffer, imageBuffer, ImageAttr);
//		nResult = FP_FeatureAndImageExtractANSI(2, featureBuffer, imageBuffer, ImageAttr);
//		if (nResult >= 0) {
//			Log.e("ExtractFeatureAndImage", new String(featureBuffer));
//		}
    imgSize = ImageAttr[0] * ImageAttr[1] + 1024 + 54;
    if (mNCB != null) {
      mNCB.CallBackFun(cmd);
    }else{
      Log.e("app", "mNCB == null");
    }
    // FP_FeatureAndImageExtractCallback(2);
  }

  public synchronized void ExtractTemplet() {
    Log.e("app", "ExtractTemplet");
    cmd = CMD_ExtractTemplet;
    featureBuffer = new byte[300];
    featureBuffer0x30 = new byte[513];
    nResult = FP_FeatureAndTESOImageExtractAll(1, featureBufferHex,
        featureBuffer0x30, featureBuffer, imageBuffer,TESOimageBuffer,
        ImageAttr);
    TESOimage = new byte[40536];
    System.arraycopy(TESOimageBuffer, 0, TESOimage, 0, 40536);
//		 System.arraycopy(featureBuffer0x30, 0, featureBuffer, 0, 300);
//		nResult = FP_FeatureAndImageExtractAll(2, featureBufferHex, featureBuffer0x30, featureBuffer, imageBuffer,
//				ImageAttr);
//		nResult = FP_FeatureAndImageExtractISO(2, featureBuffer, imageBuffer, ImageAttr);
//		nResult = FP_FeatureAndImageExtractANSI(2, featureBuffer, imageBuffer, ImageAttr);
//		if (nResult >= 0) {
//			Log.e("ExtractFeatureAndImage", new String(featureBuffer));
//		}
    if (nResult<0) {
      ExtractCount++;
    }else{
      ExtractCount = 0;
    }
    if(ExtractCount>8){
      if (mNCB != null) {
        mNCB.CallBackFun(CMD_OutofTime);
      }
      ExtractCount = 0;
      return;
    }
    imgSize = ImageAttr[0] * ImageAttr[1] + 1024 + 54;
    if (mNCB != null) {
      mNCB.CallBackFun(cmd);
    }else{
      Log.e("app", "mNCB == null");
    }
    // FP_FeatureAndImageExtractCallback(2);
  }

  public synchronized void ExtractFeatureMatch() {
    Log.e("app", "ExtractFeatureMatch");
    cmd = CMD_ExtractFeatureMatch;
    featureBuffer = new byte[300];
    featureBuffer0x30 = new byte[513];
////		nResult = FP_FeatureAndImageExtractAll(10, featureBufferHex, featureBuffer0x30, featureBuffer, imageBuffer,
////				ImageAttr);
//
    nResult = FP_FeatureAndTESOImageExtractAll(1, featureBufferHex,
        featureBuffer0x30, featureBuffer, imageBuffer,TESOimageBuffer,
        ImageAttr);
    TESOimage = new byte[40572];
    System.arraycopy(TESOimageBuffer, 0, TESOimage, 0, 40572);

    Log.e("app", "ImageAttr[2] = "+ImageAttr[2]);
//		 System.arraycopy(featureBuffer0x30, 0, featureBuffer, 0, 300);
//		nResult = FP_FeatureAndImageExtractISO(2, featureBuffer, imageBuffer, ImageAttr);
//		nResult = FP_FeatureAndREDImageExtractISO(2, featureBuffer, imageBuffer, ImageAttr);
//		nResult = FP_FeatureAndImageExtractANSI(2, featureBuffer, imageBuffer, ImageAttr);
//		if (nResult >= 0) {
//			Log.e("ExtractFeatureAndImage", new String(featureBuffer));
//		}
    imgSize = ImageAttr[0] * ImageAttr[1] + 1024 + 54;
    if (mNCB != null) {
      mNCB.CallBackFun(cmd);
    }else{
      Log.e("app", "mNCB == null");
    }
    // FP_FeatureAndImageExtractCallback(10);
  }

  public synchronized void FP_Close() {
    imgSize = 0;
    ExtractCount = 0;
    LIVESCAN_Close();
  }

  public final void RegisterCallBack(NotifyInterface cb) {
    mNCB = cb;
  }

  public interface NotifyInterface {

    void CallBackFun(int cmd);
  }

  // 获取当前系统root状态
  public synchronized boolean getRootAhth() {
    Process process = null;
    DataOutputStream os = null;
    try {
      process = Runtime.getRuntime().exec("su");
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes("exit\n");
      os.flush();
      int exitValue = process.waitFor();
      if (exitValue == 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());
      return false;
    } finally {


      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*****************************************************************************
   * * algorithm API *
   *
   * output parameters memory must set to 0x00 before
   *
   *
   * *return #define TCY_SUCC >=0 /* success #define TCY_FAIL -1 /* 失败结果
   * #define TCY_ERRO -2 /* 校验错误 #define TCY_PARA -3 /* 参数错误 #define TCY_EMPT
   * -4 /* 空特征库 #define TCY_NOFP -5 /* no finger press #define TCY_NSAM -6 /*
   * 值不相关 #define TCY_NMAT -7 /* 值不匹配 #define TCY_NMEM -8 /* 内存不足 #define
   * TCY_FLSH -9 /* 有闪存错 #define TCY_NODV -10 /* 传感器错 #define TCY_TOLV -11 /*
   * 请抬起手 #define TCY_NSUP -12 /* 不支持令 #define TCY_TMOT -13 /* 操作超时 #define
   * TCY_BUSY -14 /* 我很忙啊 #define TCY_NLNK -15 /* 设备断开 #define TCY_LESS -16 /*
   * 特点过少 #define TCY_CNCL -17 /* 取消操作 #define TCY_FILE -18 /* 文件错误
   */
	/* Init native environment, must be done before using any algorithm API */
  public final native void Native_Init();

  public native int LIVESCAN_Init1(int pid);

  public native int LIVESCAN_Inithid(int pid);

  /*
   * Get fingerprint image and feature from device and will callback in
   * several count, native code default run in main thread, Unblock
   */
  public synchronized final native void FP_FeatureAndImageExtractCallback(int count);

  // public native void FP_FeatureAndImageExtractCallback(int count);

  public final native int FP_FeatureAndImageExtractAll(int count, byte[] FeatureHex, byte[] Feature0x30,
                                                       byte[] FeatureBase64, byte[] ImageData, int[] ImageAttr);
  public final native int FP_FeatureAndTESOImageExtractAll(int count, byte[] FeatureHex, byte[] Feature0x30,
                                                           byte[] FeatureBase64, byte[] ImageData,byte[] TESOImageData, int[] ImageAttr);

  public final native int FP_FeatureAndREDImageExtractISO(int count, byte[] FeatureBase64, byte[] ImageData,
                                                          int[] ImageAttr);
  public final native int FP_FeatureAndImageExtractISO(int count, byte[] FeatureBase64, byte[] ImageData,
                                                       int[] ImageAttr);

  public final native int FP_FeatureAndImageExtractANSI(int count, byte[] FeatureBase64, byte[] ImageData,
                                                        int[] ImageAttr);

  public void saveToSDCard(String filename, String content) throws Exception {
    File file = new File(Environment.getExternalStorageDirectory(), filename);
    FileOutputStream outStream = new FileOutputStream(file);
    outStream.write(content.getBytes());
    outStream.close();
  }

	/*
	 * Extract fingerprint feature from image, native code default run in main
	 * thread, block
	 */

  public native int FP_FeatureExtract(byte cScannerType, byte cFingerCode, byte[] FingerImgBuf, byte[] FeatureDdata);

  /*
   * Extract fingerprint feature templet from least three feature, native code
   * default run in main thread, block
   */
  public native int FP_FeatureTempletExtract(byte[] Feature0, byte[] Feature1, byte[] Feature2, byte[] templet);

  // Hex 格式的提取模板
  public native int FP_FeatureTempletExtractHex(byte[] Feature0, byte[] Feature1, byte[] Feature2, byte[] templet);

  // 0x30 格式的提取模板
  public native int FP_FeatureTempletExtract0x30(byte[] Feature0, byte[] Feature1, byte[] Feature2, byte[] templet);
  //ISO 19794 格式的提取模板
  public native int FP_FeatureTempletExtractISO(byte[] Feature0, byte[] Feature1, byte[] Feature2, byte[] templet);


  public native int FP_FeatureTempletExtractANSI(byte[] Feature0, byte[] Feature1, byte[] Feature2, byte[] templet);

  // base64 处理的特征值比对
  public native int FP_FeatureMatch(byte[] FeatureData1, byte[] FeatureTemplet, float[] fSimilarity);

  // Hex 处理的特征值比对
  public native int FP_FeatureMatchHex(byte[] FeatureData1, byte[] FeatureTemplet, float[] fSimilarity);

  // 0x30 处理的特征值比对
  public native int FP_FeatureMatch0x30(byte[] FeatureData1, byte[] FeatureTemplet, float[] fSimilarity);

  //feature TC to ISO 19794
  public native int FP_FeatureTCtoISO(byte[] FeatureDataTC, byte[] FeatureDataISO,int Wide,int High);

  //ISO 19794 格式的比对
  public native int FP_FeatureMatchISO(byte[] FeatureData1, byte[] FeatureTemplet, float[] fSimilarity);

  public native int FP_FeatureMatchANSI(byte[] FeatureData1, byte[] FeatureTemplet, float[] fSimilarity);

  public native int LIVESCAN_permission(Activity context);

  /*
   * 3.1 初始化采集器 函数原型： int LIVESCAN_Init()。 参数： fd 返回的USB操作符。 返回值： 调用成功返回1。
   * 否则返回错误代码，调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 本部分与公安部现有函数接口有异 初始化采集器，分配相应的资源，检查授权等。在所有接口函数中，通常首先调用此函数。
   * 只需要调用一次，允许重复调用，其结果与一次调用相同。
   */
  public native int LIVESCAN_Init(int fd); // 初始化啦

  // 蜂鸣器响一声
  public native int FP_Beep();

  /*
   * 3.2 释放采集器 函数原型： int LIVESCAN_Close()。 参数： 无。 返回值： 调用成功返回1。
   * 否则返回错误代码，调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。
   */
  public native int LIVESCAN_Close();

  /*
   * 3.3 获得采集器通道数量 函数原型： int LIVESCAN_GetChannelCount()。 参数： 无。 返回值：
   * 调用成功返回通道数量（＞0）。 否则返回错误代码，调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。
   * 说明： 获得采集器可以使用的通道数量及通道号。
   */
  public native int LIVESCAN_GetChannelCount();

  /*
   * 3.4 设置采集器当前的亮度 函数原型： int LIVESCAN_SetBright(int nChannel,int nBright)。
   * 参数： int nChannel 通道号。输入参数。 int nBright 亮度，范围为0～255，输入参数。 返回值： 调用成功返回1。
   * 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 设置采集器当前亮度
   */
  public native int LIVESCAN_SetBright(int nChannel, int nBright);

  /*
   * 3.5 设置采集器当前对比度 函数原型： int LIVESCAN_SetContrast(int nChannel,int
   * nContrast)。 参数： int nChannel 通道号。输入参数。 int nContrast 对比度, 范围0～255。输入参数。
   * 返回值： 调用成功返回1。 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 设置采集器当前对比度。
   */
  public native int LIVESCAN_SetContrast(int nChannel, int nContrast);

  /*
   * 3.6 获得采集器当前的亮度 函数原型： int LIVESCAN_GetBright(int nChannel,int *pnBright)。
   * 参数： int nChannel 通道号。输入参数。 int *pnBright 存放当前亮度的整形指针。输出参数。 返回值： 调用成功返回1。
   * 否则返回错误代码 ,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 获得采集器当前亮度。
   */
  public native int LIVESCAN_GetBright(int nChannel, int nBright);

  /*
   * 3.7 获得采集器当前对比度 函数原型： int LIVESCAN_GetContrast(int nChannel,int
   * *pnContrast)。 参数： int nChannel 通道号。输入参数。 int *pnContrast
   * 存放当前对比度的整型指针。输出参数。 返回值： 调用成功返回1。 否则返回错误代码,调用LIVESCAN_GetErrInfo
   * 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 获得采集器当前对比度。
   */
  public native int LIVESCAN_GetContrast(int nChannel, int pnContrast);

  /*
   * 3.8 获得采集器可采集图像的宽度、高度的最大值 函数原型： int __stdcall LIVESCAN_GetMaxImageSize（int
   * nChannel,int *pnWidth, int *pnHeight)。 参数： int nChannel 通道号。输入参数。 int
   * *pnWidth 存放图像宽度的整形指针。输出参数。 int *pnHeight 存放图像高度的整形指针。输出参数。 返回值： 调用成功返回1。
   * 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 获得采集器可采集图像的宽度、高度的最大值。
   */
  public native int LIVESCAN_GetMaxImageSize(int nChannel, int pnContrast);

  /*
   * 3.9 获得采集器当前图像的采集位置、宽度和高度 函数原型： int LIVESCAN_GetCaptWindow(int
   * nChannel,int *pnOriginX,Int *pnOriginY,int *pnWidth,int *pnHeight)。 参数：
   * int nChannel 通道号。输入参数。 int *pnOriginX 存放图像采集窗口的采集原点坐标X 值的整型指针。输出参数。 int
   * *pnOriginY 存放图像采集窗口的采集原点坐标Y 值的整型指针。输出参数。 int *pnWidth 存放采集图像宽度的整形指针。输出参数。
   * int *pnHeight 存放采集图像高度的整形指针。输出参数。 返回值： 调用成功返回1。
   * 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 获得采集器当前图像的采集位置、宽度和高度。当前图像宽度初始值为256，高度初始值为 360。
   */
  public native int LIVESCAN_GetCaptWindow(int nChannel, int pnOriginX, int pnOriginY, int pnWidth, int pnHeight);

  /*
   * 3.10 设置采集器当前图像的采集位置、宽度和高度 函数原型： int LIVESCAN_SetCaptWindow(int
   * nChannel,int nOriginX, int nOriginY,int nWidth, int nHeight)。 参数： int
   * nChannel 通道号。输入参数。 int nOriginX 图像采集窗口的采集原点坐标X 值。输入参数。 int nOriginY
   * 图像采集窗口的采集原点坐标Y 值。输入参数。 int nWidth 采集图像的宽度。对于居民身份证用单指指纹采集，应大于等于256。否则应
   * 返回参数错误代码。输入参数。 int nHeight 采集图像的高度。对于居民身份证用单指指纹采集，应大于等于360。否则应
   * 返回参数错误代码。输入参数。 返回值： 调用成功返回1。
   */
  public native int LIVESCAN_SetCaptWindow(int nChannel, int pnOriginX, int pnOriginY, int pnWidth, int pnHeight);

  /*
   * 3.11 调用采集器的属性设置对话框 函数原型： int LIVESCAN_Setup()。 参数： 无。 返回值： 调用成功返回1。
   * 否则返回错误代码，调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 此函数弹出一个模式对话框，用户可以设置除去对比度、亮度、采集窗口参数外的其它参数， 如 GAMMA 值等,使得设置适合采集器本身的特点。
   */
  public native int LIVESCAN_SetUp();

  /*
   * 3.12 准备采集一帧图像 函数原型： int LIVESCAN_BeginCapture(int nChannel)。 参数： int
   * nChannel 通道号。输入参数。 返回值： 调用成功返回1。 否则返回错误代码，调用LIVESCAN_GetErrInfo
   * 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 采集图像的一个前缀函数，使得采集器有机会进行另外一个采集之前的初始化工作。
   */
  public native int LIVESCAN_BeginCapture(int nChannel);

  /*
   * 3.13 采集一帧图像 函数原型： int LIVESCAN_GetFPRawData(int nChannel,unsigned char
   * *pRawData)。 参数： int nChannel 通道号。输入参数。 unsigned char *pRawData
   * 指向存放采集数据的内存块，调用者分配。 返回图像数据，大小应为：当前图像采集宽度×当前图像采集高度。输出参数。 返回值： 调用成功返回1。
   * 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 采集一帧图像。
   */
  public native int LIVESCAN_GetFPRawData(int nChannel, byte[] pRawData);

  /*
   * 3.14 采集一帧BMP格式图像 函数原型： int LIVESCAN_GetFPBmpData(int nChannel, unsigned
   * char *pBmpData)。 参数： int nChannel 通道号。输入参数。 unsigned char* pBmpData 指向存放8
   * 位灰度BMP 格式采集数据的内存块，调用者分配。 返回8 位灰度BMP 格式图像数据。大小应为：当前图像采集宽度×当前图像采集高度+1078。
   * 输出参数。 返回值： 调用成功返回1。 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4
   * 的要求。 说明： 采集一帧8 位灰度BMP 格式图像。
   */
  public native int LIVESCAN_GetFPBmpData(int nChannel, byte[] pBmpData);

  /*
   * 3.15 结束采集一帧图像 函数原型： int LIVESCAN_EndCapture(int nChannel)。 参数： int
   * nChannel 通道号。输入参数。 返回值： 调用成功返回1。 否则返回错误代码,调用LIVESCAN_GetErrInfo
   * 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 结束采集一帧图像或预览图像。
   */
  public native int LIVESCAN_EndCapture(int nChannel);

  /*
   * 3.16 采集器是否支持设置对话框 函数原型： int LIVESCAN_IsSupportSetup()。 参数： 无。 返回值：
   * 若采集接口支持LIVESCAN_Setup,则返回1，否则返回0。 否则返回错误代码,调用LIVESCAN_GetErrInfo
   * 函数获取错误信息。错误代码值符合A.4 的要求。 说明： 此函数用来确认是否支持设置对话框。
   */
  public native int LIVESCAN_IsSupportSetup();

  /*
   * 3.17 取得接口规范的版本 函数原型： int LIVESCAN_GetVersion()。 参数： 无。 返回值：
   * 获得接口规范的版本。当前版本为1.00,返回值为100。若以后扩展接口，则需要修改此 版本号。
   * 否则返回错误代码，调用LIVESCAN_GetErrorInfo 函数获取错误信息。错误代码值应符合 A.4 的要求。 说明： 获得接口版本号。
   */
  public native int LIVESCAN_GetVersion();

  /*
   * 3.18 获得接口规范的说明 函数原型： int LIVESCAN_GetDesc(char pszDesc[1024])。 参数： char
   * pszDesc[1024] 存放接口说明，其中pszDesc[0]用于存储采集器代码，缺省为FFH。 输出参数。 返回值： 调用成功返回1。
   * 否则返回错误代码，调用LIVESCAN_GetErrorInfo 函数获取错误信息。错误代码值应符合 A.4 的要求 说明：
   * 获得接口说明，不需要初始化就可以调用。pszDesc 以数值0 结尾的字符串，采用GB 13000 中规定的字符。
   */
  public native int LIVESCAN_GetDesc(byte[] pszDesc);

  /*
   * 3.19 获得采集接口错误信息 函数原型： int LIVESCAN_GetErrorInfo(int nErrorNo, char
   * pszErrorInfo[256])。 参数： char pszErrorInfo[256]
   * 用来存放错误信息的内存块，错误信息的长度不能超过256 个字节。输出参数。 int nErrorNo 错误代码(＜0)。输入参数。 返回值：
   * 若为合法的错误代码返回1，pszErrorInfo 中为错误信息。若 nErrorNo 为非法的错误 代码，则返回-6，同时设置
   * pszErrorInfo 为“非法错误号”错误。 说明： pszErrorInfo 采用 GB 13000 中规定的字符。
   */
  public native int LIVESCAN_GetErrorInfo(int nErrorNo, byte[] pszErrorInfo);

  /*
   * 3.20 设置存放采集数据的内存块为空 函数原型： int LIVESCAN_SetBufferEmpty(unsigned char
   * *pImageData, long imageLength）。 参数： unsigned char *pImageData
   * 指向存放采集数据的内存块。输入参数。 Long imageLength 存放采集数据的内存块长度。输入参数。 返回值： 调用成功返回1。
   * 否则返回错误代码,调用LIVESCAN_GetErrInfo 函数获取错误信息。错误代码值符合A.4 的要求。 说明：
   * 将存放采集数据的内存块中的每一个字节的值置为0x00。
   */
  public native int LIVESCAN_SetBufferEmpty(byte[] pImageData, long imageLength);

  // 获取设备的sn ，共64个字节
  public native int FP_GetSn(byte[] FingerprintSn);
  // 获取设备的Version ，共64个字节
  public native int FP_GetVersion(byte[] FingerprintVersion);

}
