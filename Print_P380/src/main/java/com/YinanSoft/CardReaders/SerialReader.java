package com.YinanSoft.CardReaders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;

import com.YinanSoft.Utils.DataUtils;

public class SerialReader extends IDCardReader {
    private byte[] mBuffer = new byte[2320];
    private int mCurrentSize = 0;

    //发送信息
    private final String sCmdReadSAMID = "AAAAAA9669000312FFEE";
    private final String sCmdFindCard = "AAAAAA96690003200122";
    private final String sCmdSelectCard = "AAAAAA96690003200221";
    private final String sCmdReadCardBase = "AAAAAA96690003300132";
    private final String sCmdReadNewAddress = "AAAAAA96690003300330";
    private final String sCmdReadCardFinger = "AAAAAA96690003301023";
    private final String sReadCardId = "0200033229FFE403";
    private final String sReadCardIdNo = "02000230063603";
    //命令返回信息
    private final String sFIND_NO_CARD = "AAAAAA9669000400008084";
    private final String sFIND_SUCC = "AAAAAA9669000800009F0000000097";
    private final String sREAD_NO_CARD = "AAAAAA9669000400004145";
    private final String sSELECT_NO_CARD = "AAAAAA9669000400008185";
    private final String sSELECT_SUCC = "AAAAAA9669000C00009000000000000000009C";

    private static SerialReader self;

    public static SerialReader getInstance(Context context) {
        if (self == null) {
            self = new SerialReader(context);
        }
        return self;
    }

    public SerialReader(Context theContext) {
        super(theContext);
        CODE = 1;
        TAG = "A9LReader";
        PATH = "/dev/ttySAC3";
        BAUDRATE = 115200;
    }

    public SerialReader(Context theContext, String sComPort, int nBaud) {
        super(theContext);
        CODE = 1;
        TAG = "A9LReader";
        PATH = sComPort;
        BAUDRATE = nBaud;
    }

    public boolean InitReader(byte[] byLicBuf) {
        String[] sRet = new String[1];
        byte[] bt = null;
        if (isOpen) return true;
        try {
            if (!openSerialPort(new File(PATH), BAUDRATE, 0))
                return false;

            //解决个别时候读取模块号不正确问题
            for (int i = 0; i < 3; i++) {
                isOpen = true;
                bt = ReadSAMIDBuf(sRet);
                isOpen = false;
                if (bt != null) break;
            }

            bt = new byte[16];
            if (bt != null && byLicBuf != null) {
                sKey = stringFromJNI(bt, byLicBuf, CODE);
                if (sKey.length() < 16) {
                    isOpen = false;
                    return false;
                }
            } else {
                isOpen = false;
                return false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            isOpen = false;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            isOpen = false;
            return false;
        }
        isOpen = true;
        return true;
    }

    public void ReleaseReader() {
        if (isOpen) {
            closeSerialPort();
            mCurrentSize = 0;
            isOpen = false;
        }
    }

    public void PowerOnReader() {
        try {
            FileWriter localFileWriter = new FileWriter(new File("/proc/s706_power/s706_power_sfz"));
            localFileWriter.write("1");
            localFileWriter.close();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void PowerOffReader() {
        try {
            FileWriter localFileWriter = new FileWriter(new File("/proc/s706_power/s706_power_sfz"));
            localFileWriter.write("0");
            localFileWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public String ReadSAMID(String[] sRet) {
        String sSAMID = "";
        boolean isTryOpen = false;

        try {
            if (!isOpen) {
                if (!openSerialPort(new File(PATH), BAUDRATE, 0))
                    return sSAMID;
                isTryOpen = true;
                isOpen = true;
            }

            boolean bRet = SendAndRecv(sCmdReadSAMID, sRet, 300);
            if (!bRet) return sSAMID;
            if (mBuffer[0] == (byte) 0x00 && mBuffer[1] == (byte) 0x00 && mBuffer[2] == (byte) 0x90) {
                String temp1 = DataUtils.toHexString1(mBuffer[3]);
                String temp2 = DataUtils.toHexString1(mBuffer[5]);
                byte[] temp3 = new byte[4];
                System.arraycopy(mBuffer, 7, temp3, 0, temp3.length);
                reversal(temp3);
                byte[] temp4 = new byte[4];
                System.arraycopy(mBuffer, 11, temp4, 0, temp4.length);
                reversal(temp4);
                byte[] temp5 = new byte[4];
                System.arraycopy(mBuffer, 15, temp5, 0, temp5.length);
                reversal(temp5);
                StringBuffer sb = new StringBuffer();
                sb.append(temp1);
                sb.append(".");
                sb.append(temp2);
                sb.append("-");
                sb.append(byte2Int(temp3));
                sb.append("-");
                String str4 = Long.toString(byte2Int(temp4));
                for (int i = 0; i < 10 - str4.length(); i++) {
                    sb.append("0");
                }
                sb.append(str4);
                sb.append("-");
                String str5 = Long.toString(byte2Int(temp5));
                for (int i = 0; i < 10 - str5.length(); i++) {
                    sb.append("0");
                }
                sb.append(str5);
                sSAMID = sb.toString();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isTryOpen) {
                isOpen = false;
                closeSerialPort();
            }
        }
        return sSAMID;
    }

    public IDCardInfo ReadBaseCardInfo(String[] sRet) {
        if (!isOpen) return null;
//		if(this.FindCard(sRet) && this.SelectCard(sRet))
//		{
//			if(this.ReadCardBase(sRet))
//			{
//				return decodeInfo(mBuffer, false, true);
//			}
//		}
        this.FindCard(sRet);
        this.SelectCard(sRet);
        if (this.ReadCardBase(sRet)) {
            return decodeInfo(mBuffer, false, true);
        }
        return null;
    }

    public IDCardInfo ReadAllCardInfo(String[] sRet) {
//		if(!isOpen)	return null;
//
//		if(this.FindCard(sRet) && this.SelectCard(sRet))
//		{
//			String sNewAddress = "";
//			if(ReadCardNewAddress(sRet))
//			{
//				try
//				{
//					sNewAddress = new String(mBuffer, 3, 70, "UTF-16LE").trim();
//				}
//				catch (UnsupportedEncodingException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			if(this.ReadCardFinger(sRet))
//			{
//				IDCardInfo info = decodeInfo(mBuffer, true, true);
//				if(info != null)
//					info.setNewAddress(sNewAddress);
//				return info;
//			}
//		}
//		return null;
        IDCardInfo info = null;
        if (!isOpen) return null;
        this.FindCard(sRet);
        this.SelectCard(sRet);

        if (this.ReadCardFinger(sRet)) {
            info = decodeInfo(mBuffer, true, true);
            if (info == null) return null;
        }

        String sNewAddress = "";
        if (ReadCardNewAddress(sRet)) {
            try {
                sNewAddress = new String(mBuffer, 3, 70, "UTF-16LE").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (info != null)
            info.setNewAddress(sNewAddress);
        return info;
    }

    public String ReadCardID() {
        String[] sRet = new String[2];
        boolean bRet = SendAndRecvNEW(sReadCardId, sRet, 100);
        if (!bRet) return "";
        return sRet[0];
    }

    private boolean FindCard(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdFindCard, sRet, 100);
        if (!bRet) return false;
        if (sRet[0].equalsIgnoreCase(sFIND_SUCC)) return true;
        return false;
    }

    private boolean SelectCard(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdSelectCard, sRet, 100);
        if (!bRet) return false;
        if (sRet[0].equalsIgnoreCase(sSELECT_SUCC)) return true;
        return false;
    }

    private boolean ReadCardBase(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdReadCardBase, sRet, 2000);
        if (!bRet) return false;
        if (mBuffer[0] == (byte) 0x00 && mBuffer[1] == (byte) 0x00 && mBuffer[2] == (byte) 0x90)
            return true;
        return false;
    }

    private boolean ReadCardFinger(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdReadCardFinger, sRet, 3000);
        if (!bRet) return false;
        if (mBuffer[0] == (byte) 0x00 && mBuffer[1] == (byte) 0x00 && mBuffer[2] == (byte) 0x90)
            return true;
        return false;
    }

    private boolean ReadCardNewAddress(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdReadNewAddress, sRet, 1500);
        if (!bRet) return false;
        if (mBuffer[0] == (byte) 0x00 && mBuffer[1] == (byte) 0x00 && mBuffer[2] == (byte) 0x90)
            return true;
        return false;
    }


    public IDCardInfo decodeInfo(byte[] buffer, boolean haveFinger, boolean decodePhoto) {
        IDCardInfo people = new IDCardInfo();
        String temp = null;

        int nPtr = 0;
        short textSize = DataUtils.getShort(buffer[nPtr + 0], buffer[nPtr + 1]);
        short imageSize = DataUtils.getShort(buffer[nPtr + 2], buffer[nPtr + 3]);
        short fingerSize = 0;
        if (haveFinger) {
            fingerSize = DataUtils.getShort(buffer[nPtr + 4], buffer[nPtr + 5]);
            nPtr += 6;
        } else nPtr += 4;


        byte[] text = new byte[textSize];
        try {
            System.arraycopy(buffer, nPtr, text, 0, textSize);
        } catch (Exception e) {
            return null;
        }
        if (decodePhoto) {
            byte[] image = new byte[imageSize];
            try {
                System.arraycopy(buffer, nPtr + textSize, image, 0, imageSize);
            } catch (Exception e) {
                return null;
            }
            people.setPhoto(parsePhoto(image));
        }

        byte[] finger = null;
        if (fingerSize > 0) {
            finger = new byte[fingerSize];
            System.arraycopy(buffer, nPtr + textSize + imageSize, finger, 0, fingerSize);
            people.setFingerInfo(finger);
        }

        try {
            // 姓名
            temp = new String(text, 0, 30, "UTF-16LE").trim();
            people.setName(temp);

            // 性别
            temp = new String(text, 30, 2, "UTF-16LE");
            if (temp.equals("1"))
                temp = "男";
            else
                temp = "女";
            people.setGender(temp);

            // 民族
            temp = new String(text, 32, 4, "UTF-16LE");
            try {
                int code = Integer.parseInt(temp.toString());
                temp = parseNation(code);
            } catch (Exception e) {
                temp = "";
            }
            people.setNation(temp);

            // 出生
            temp = new String(text, 36, 16, "UTF-16LE").trim();
            people.setBirthday(temp);

            // 住址
            temp = new String(text, 52, 70, "UTF-16LE").trim();
            people.setAddress(temp);

            // 身份证号
            temp = new String(text, 122, 36, "UTF-16LE").trim();
            people.setCardNum(temp);

            // 签发机关
            temp = new String(text, 158, 30, "UTF-16LE").trim();
            people.setRegistInstitution(temp);

            // 有效起始日期
            temp = new String(text, 188, 16, "UTF-16LE").trim();
            people.setValidStartDate(temp);

            // 有效截止日期
            temp = new String(text, 204, 16, "UTF-16LE").trim();
            people.setValidEndDate(temp);

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        return people;
    }

    private byte[] ReadSAMIDBuf(String[] sRet) {
        boolean bRet = SendAndRecv(sCmdReadSAMID, sRet, 300);
        if (!bRet) return null;
        if (mBuffer[0] == (byte) 0x00 && mBuffer[1] == (byte) 0x00 && mBuffer[2] == (byte) 0x90) {
            byte[] tmp = new byte[16];
            System.arraycopy(mBuffer, 3, tmp, 0, tmp.length);
            return tmp;
        }
        return null;
    }

    private boolean SendAndRecv(String sSendCmd, String[] sRecvResp, int nWaitTime) {
        int nLen = 0;
        if (!isOpen) return false;
        sRecvResp[0] = "";
        Arrays.fill(mBuffer, (byte) 0);
        byte[] bTmp;
        try {
            write(DataUtils.hexStringToBytes(sSendCmd));
            int nRet = read(mBuffer, 7, nWaitTime);
            if (nRet != 0) {
                sRecvResp[0] = "read err1:" + String.valueOf(nRet);
                return false;
            }
            bTmp = new byte[7];
            System.arraycopy(mBuffer, 0, bTmp, 0, bTmp.length);
            sRecvResp[0] = DataUtils.bytesToHexString(bTmp);

            if (mBuffer[0] == (byte) 0xaa && mBuffer[1] == (byte) 0xaa && mBuffer[2] == (byte) 0xaa && mBuffer[3] == (byte) 0x96 && mBuffer[4] == (byte) 0x69) {
                nLen = DataUtils.getShort(mBuffer[5], mBuffer[6]);
                nRet = read(mBuffer, nLen, nWaitTime);
                if (nRet != 0) {
                    sRecvResp[0] = "read err2:" + String.valueOf(nRet);
                    return false;
                }

                bTmp = new byte[nLen];
                System.arraycopy(mBuffer, 0, bTmp, 0, bTmp.length);
                sRecvResp[0] += DataUtils.bytesToHexString(bTmp);
                return true;
            } else sRecvResp[0] = "read err3";
        } catch (IOException e) {
            e.printStackTrace();
            sRecvResp[0] = e.getMessage();
        }
        return false;
    }

    private boolean SendAndRecvNEW(String sSendCmd, String[] sRecvResp, int nWaitTime) {
        int nLen = 0;
        if (!isOpen) return false;
        sRecvResp[0] = "";
        Arrays.fill(mBuffer, (byte) 0);
        byte[] bTmp;
        try {
            byte[] writebyte = DataUtils.hexStringToBytes(sSendCmd);
            write(writebyte);
            int nRet = read(mBuffer, 5, nWaitTime);
            if (nRet != 0) {
                sRecvResp[0] = "read err1:" + String.valueOf(nRet);
                return false;
            }
            bTmp = new byte[5];
            System.arraycopy(mBuffer, 0, bTmp, 0, bTmp.length);
            sRecvResp[0] = DataUtils.bytesToHexString(bTmp);

            if (mBuffer[0] == (byte) 0x02 && mBuffer[3] == (byte) 0x00 && mBuffer[4] == (byte) 0x00) {
                nLen = DataUtils.getShort(mBuffer[1], mBuffer[2]);
                nRet = read(mBuffer, nLen, nWaitTime);
                if (nRet != 0) {
                    sRecvResp[0] = "read err2:" + String.valueOf(nRet);
                    return false;
                }

                bTmp = new byte[nLen - 4];
                System.arraycopy(mBuffer, 0, bTmp, 0, bTmp.length);
                sRecvResp[0] = DataUtils.bytesToHexString(bTmp);
                return true;
            } else sRecvResp[0] = "read err3";
        } catch (IOException e) {
            e.printStackTrace();
            sRecvResp[0] = e.getMessage();
        }
        return false;
    }


    private void reversal(byte[] data) {
        int length = data.length;
        for (int i = 0; i < length / 2; i++) {
            byte temp = data[i];
            data[i] = data[length - 1 - i];
            data[length - 1 - i] = temp;
        }
    }

    private long byte2Int(byte[] data) {
        int intValue = 0;
        for (int i = 0; i < data.length; i++) {
            intValue += (data[i] & 0xff) << (8 * (3 - i));
        }
        long temp = intValue;
        temp <<= 32;
        temp >>>= 32;
        return temp;
    }

}