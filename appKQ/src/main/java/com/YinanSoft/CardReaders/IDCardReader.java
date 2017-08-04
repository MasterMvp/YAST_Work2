package com.YinanSoft.CardReaders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IDCardReader {
    protected int CODE = 0;
    protected String TAG = "IDCardReader";
    private Context mContext = null;
    protected boolean isOpen;
    protected String sKey = "";

    //串口操作相关定义
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    protected String PATH = "/dev/ttySAC3";
    protected int BAUDRATE = 115200;    //a320

    private native static FileDescriptor open(String path, int baudrate, int flags);

    private native void close();

    protected native String stringFromJNI(byte[] samIDBuffer, byte[] LicFileBuffer, int readerCode);

    protected native int WltToBmp(String wltFileName, String bmpFileName, String sKey);

    protected native int WltToBmpBuffer(byte[] wltBuffer, byte[] bmpBuffer, String sKey);

    static {
//		System.loadLibrary("wlt2bmp");
        System.loadLibrary("IDCardReader");
    }

    public IDCardReader(Context theContext) {
        isOpen = false;
        mContext = theContext;
    }

    public void PowerOnReader() {

    }

    public void PowerOffReader() {
    }

    public String getReaderPowerStatus() {
        return "";
    }

    protected native void ReaderPower(int paramInt);

    public String ReadSAMID(String[] sRet) {
        return "";
    }

    public boolean FindCardpc(String[] sRet) {
        return false;
    }//new

    public boolean SelectCardpc(String[] sRet) {
        return false;
    }//new

    public boolean ReadCardBasepc(String[] sRet) {
        return false;
    }//new

    public boolean InitReader(byte[] byLicBuf) {
        return false;
    }

    public void ReleaseReader() {
    }

    public IDCardInfo ReadBaseCardInfo(String[] sRet) {
        return null;
    }

    public IDCardInfo ReadAllCardInfo(String[] sRet) {
        return null;
    }

    public void SendAndRecvtext(String sSendCmd, String[] sRecvResp, int nWaitTime) {
    }

    public void SendAndRecvtextonlyw() {
    }

    public void SendAndRecvtextonlyr() {
    }

    //-----------------------------------------------------------------------------------------------
    protected String parseNation(int code) {
        String nation;
        switch (code) {
            case 1:
                nation = "汉";
                break;
            case 2:
                nation = "蒙古";
                break;
            case 3:
                nation = "回";
                break;
            case 4:
                nation = "藏";
                break;
            case 5:
                nation = "维吾尔";
                break;
            case 6:
                nation = "苗";
                break;
            case 7:
                nation = "彝";
                break;
            case 8:
                nation = "壮";
                break;
            case 9:
                nation = "布依";
                break;
            case 10:
                nation = "朝鲜";
                break;
            case 11:
                nation = "满";
                break;
            case 12:
                nation = "侗";
                break;
            case 13:
                nation = "瑶";
                break;
            case 14:
                nation = "白";
                break;
            case 15:
                nation = "土家";
                break;
            case 16:
                nation = "哈尼";
                break;
            case 17:
                nation = "哈萨克";
                break;
            case 18:
                nation = "傣";
                break;
            case 19:
                nation = "黎";
                break;
            case 20:
                nation = "傈僳";
                break;
            case 21:
                nation = "佤";
                break;
            case 22:
                nation = "畲";
                break;
            case 23:
                nation = "高山";
                break;
            case 24:
                nation = "拉祜";
                break;
            case 25:
                nation = "水";
                break;
            case 26:
                nation = "东乡";
                break;
            case 27:
                nation = "纳西";
                break;
            case 28:
                nation = "景颇";
                break;
            case 29:
                nation = "柯尔克孜";
                break;
            case 30:
                nation = "土";
                break;
            case 31:
                nation = "达斡尔";
                break;
            case 32:
                nation = "仫佬";
                break;
            case 33:
                nation = "羌";
                break;
            case 34:
                nation = "布朗";
                break;
            case 35:
                nation = "撒拉";
                break;
            case 36:
                nation = "毛南";
                break;
            case 37:
                nation = "仡佬";
                break;
            case 38:
                nation = "锡伯";
                break;
            case 39:
                nation = "阿昌";
                break;
            case 40:
                nation = "普米";
                break;
            case 41:
                nation = "塔吉克";
                break;
            case 42:
                nation = "怒";
                break;
            case 43:
                nation = "乌孜别克";
                break;
            case 44:
                nation = "俄罗斯";
                break;
            case 45:
                nation = "鄂温克";
                break;
            case 46:
                nation = "德昂";
                break;
            case 47:
                nation = "保安";
                break;
            case 48:
                nation = "裕固";
                break;
            case 49:
                nation = "京";
                break;
            case 50:
                nation = "塔塔尔";
                break;
            case 51:
                nation = "独龙";
                break;
            case 52:
                nation = "鄂伦春";
                break;
            case 53:
                nation = "赫哲";
                break;
            case 54:
                nation = "门巴";
                break;
            case 55:
                nation = "珞巴";
                break;
            case 56:
                nation = "基诺";
                break;
            case 97:
                nation = "其他";
                break;
            case 98:
                nation = "外国血统中国籍人士";
                break;
            default:
                nation = "";
        }

        return nation;
    }

    protected Bitmap parsePhoto(byte[] wltData) {
        byte[] bmpData = new byte[38864];
        WltToBmpBuffer(wltData, bmpData, sKey);
        return BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
    }

    //------------------------------------------------------------------------------------------------------------
    // SerialPort
    //------------------------------------------------------------------------------------------------------------
    protected void closeSerialPort() {
        try {
            mFileInputStream.close();
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    protected boolean openSerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
/*
        // Check access permission
		if (!device.canRead() || !device.canWrite()) {
			try {
				//* Missing read/write permission, trying to chmod the file
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
*/
        if (false && (!device.canRead() || !device.canWrite())) {
            try {
                 /* Missing read/write permission, trying to chmod the file */
                Process su;
                //su = Runtime.getRuntime().exec("/system/bin/su");
                su = Runtime.getRuntime().exec("/system/xbin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }


        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        return true;
    }

    // Getters and setters
    private InputStream getInputStream() {
        return mFileInputStream;
    }

    private OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    protected int read(byte recvBuf[], int recvLen, long waitTime) throws IOException {
        long lBeginTime = System.currentTimeMillis();//更新当前秒计数
        long lCurrentTime = 0;
        int nRet = 0;
        int nReadedSize = 0;


        while (true) {

            if (mFileInputStream.available() > 0) {
                nRet = mFileInputStream.read(recvBuf, nReadedSize, (recvLen - nReadedSize));
                if (nRet > 0) {
                    nReadedSize += nRet;
                    if (recvLen == nReadedSize) return 0;
                }
            }

            try {
                //Thread.sleep(10);
                lCurrentTime = System.currentTimeMillis();
                if ((lCurrentTime - lBeginTime) > waitTime) {
                    return 2;
                }
            } catch (Exception e)//InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected int read(byte recvBuf[], long waitTime) throws IOException {
        long lBeginTime = System.currentTimeMillis();//更新当前秒计数
        long lCurrentTime = 0;
        int nRet = 0;
        int nReadedSize = 0;


        while (true) {

            if (mFileInputStream.available() > 0) {
                nRet = mFileInputStream.read(recvBuf, nReadedSize, 1);
                if (nRet > 0) {
                    nReadedSize += nRet;
                }
            }

            try {
                Thread.sleep(10);
                lCurrentTime = System.currentTimeMillis();
                if ((lCurrentTime - lBeginTime) > waitTime) {
                    return nReadedSize;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    protected void write(byte[] data) throws IOException {
        while (mFileInputStream.available() > 0) mFileInputStream.read();
        mFileOutputStream.write(data);
    }

    //-----------------------------------------------------------------------------------------------
    private void displayError(String sMsg) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(mContext);
        localBuilder.setTitle("Error");
        localBuilder.setMessage(sMsg);
        localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            }
        });
        localBuilder.show();
    }
}
