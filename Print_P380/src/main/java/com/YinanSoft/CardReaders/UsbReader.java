package com.YinanSoft.CardReaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class UsbReader extends IDCardReader {
    private Common common;
    private Sdtapi sdta;
    private Context context;

    public UsbReader(Context theContext) {

        super(theContext);
        context = theContext;

    }

    public String ReadSAMID(String[] sRet) {
        String SamId = "";
        char[] puSAMID = new char[36];
        int ret = sdta.SDT_GetSAMIDToStr(puSAMID);

        if (ret == 0x90) {
            SamId = String.valueOf(puSAMID);
        } else {
            SamId = "����:" + String.format("0x%02x", ret);
        }

        return SamId;
    }


    public boolean InitReader(byte[] byLicBuf) {
        common = new Common();
        try {
            sdta = new Sdtapi(context);

        } catch (Exception e1) {// �����쳣��

            if (e1.getCause() == null) // USB�豸�쳣�������ӣ�Ӧ�ó��򼴽��رա�
            {

                return false;
            } else // USB�豸δ��Ȩ����Ҫȷ����Ȩ
            {
                Toast.makeText(context, "USB�豸δ��Ȩ����Ҫȷ����Ȩ", 1).show();
                return false;
            }

        }

//		IntentFilter filter = new IntentFilter();// ��ͼ������
//		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);// USB�豸�γ�
//		filter.addAction(common.ACTION_USB_PERMISSION);// �Զ����USB�豸������Ȩ
//		context.registerReceiver(mUsbReceiver, filter);
        return true;
    }


    public IDCardInfo ReadBaseCardInfo(String[] sRet) {

        IDCardInfo people = new IDCardInfo();
        int reffindcard = sdta.SDT_StartFindIDCard();// Ѱ�����֤
        Log.i("reffindcard", reffindcard + "");
        sdta.SDT_SelectIDCard();// ѡȡ���֤
        people = ReadBaseMsgToStr(people);
        if (people != null) {
            return people;
        } else {
            return null;
        }// end onclick()
    }

    public IDCardInfo ReadAllCardInfo(String[] sRet) {
        IDCardInfo people = new IDCardInfo();
        int reffindcard = sdta.SDT_StartFindIDCard();// Ѱ�����֤
        sdta.SDT_SelectIDCard();// ѡȡ���֤
        Log.i("reffindcard", reffindcard + "");
        people = ReadAllMsgToStr(people);
        if (people != null) {
            return people;
        } else {
            return null;
        }//
    }

    public IDCardInfo ReadAllMsgToStr(IDCardInfo people) {

        int ret;
        int[] puiCHMsgLen = new int[1];
        int[] puiPHMsgLen = new int[1];
        int[] puiFGMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];
        byte[] pucPHMsg = new byte[1024];
        byte[] puiFGMsg = new byte[1024];
        // sdtapi�б�׼�ӿڣ�����ֽڸ�ʽ����Ϣ��
        ret = sdta.SDT_ReadBaseFPMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen, puiFGMsg, puiFGMsgLen);
        Log.i("readcardref", ret + "");
        Log.i("Finger data", Sdtusbapi.bytesToHexString(puiFGMsg));
        if (ret == 0x90) {
            try {
                people = decodeInfo(pucCHMsg, false, false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return people;

    }

    // ��ȡ���֤�е�������Ϣ�����Ķ���ʽ�ģ�
    public IDCardInfo ReadBaseMsgToStr(IDCardInfo people) {

        int ret;
        int[] puiCHMsgLen = new int[1];
        int[] puiPHMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];
        byte[] pucPHMsg = new byte[1024];
        // sdtapi�б�׼�ӿڣ�����ֽڸ�ʽ����Ϣ��
        ret = sdta.SDT_ReadBaseMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);
        Log.i("readcardref", ret + "");
        if (ret == 0x90) {
            try {
                people = decodeInfo(pucCHMsg, false, false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return people;

    }

    /**
     * @param buffer
     * @param haveFinger
     * @param decodePhoto
     * @return
     */
    private IDCardInfo decodeInfo(byte[] buffer, boolean haveFinger, boolean decodePhoto) {
        IDCardInfo people = new IDCardInfo();
        String temp = null;

        int nPtr = 0;//3;
        short textSize = 256;
        short imageSize = 1024;
        //short textSize = DataUtils.getShort(buffer[nPtr + 0], buffer[nPtr + 1]);
        //short imageSize = DataUtils.getShort(buffer[nPtr + 2], buffer[nPtr + 3]);
        short fingerSize = 0;


        byte[] text = new byte[textSize];
        System.arraycopy(buffer, nPtr, text, 0, textSize);

//	  		if(decodePhoto)
//	  		{
//		  		byte[] image = new byte[imageSize];
//		  		System.arraycopy(buffer, nPtr + textSize, image, 0, imageSize);
//		  		people.setPhoto(parsePhoto(image));
//	  		}
//	  		
//	  		byte[] finger = null;
//	  		if(fingerSize > 0)
//	  		{
//	  			finger = new byte[fingerSize];
//	  			System.arraycopy(buffer, nPtr + textSize + imageSize, finger, 0, fingerSize);  	
//	  			people.setFingerInfo(finger);
//	  		}  		

        try {
            // ����
            temp = new String(text, 0, 30, "UTF-16LE").trim();

            people.setName(temp);

            // �Ա�
            temp = new String(text, 30, 2, "UTF-16LE");
            if (temp.equals("1"))
                temp = "��";
            else
                temp = "Ů";
            people.setGender(temp);

            // ����
            temp = new String(text, 32, 4, "UTF-16LE");
            try {
                int code = Integer.parseInt(temp.toString());
                temp = parseNation(code);
            } catch (Exception e) {
                temp = "";
            }
            people.setNation(temp);

            // ����
            temp = new String(text, 36, 16, "UTF-16LE").trim();
            people.setBirthday(temp);

            // סַ
            temp = new String(text, 52, 70, "UTF-16LE").trim();
            people.setAddress(temp);

            // ���֤��
            temp = new String(text, 122, 36, "UTF-16LE").trim();
            people.setCardNum(temp);

            // ǩ�����
            temp = new String(text, 158, 30, "UTF-16LE").trim();
            people.setRegistInstitution(temp);

            // ��Ч��ʼ����
            temp = new String(text, 188, 16, "UTF-16LE").trim();
            people.setValidStartDate(temp);

            // ��Ч��ֹ����
            temp = new String(text, 204, 16, "UTF-16LE").trim();
            people.setValidEndDate(temp);

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        return people;
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // USB�豸�γ��㲥
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                String deviceName = device.getDeviceName();
                if (device != null && device.equals(deviceName)) {
                    Log.i("USB����", "USB�豸�γ�");
                    // Message msg = new Message();
                    // msg.what=2;
                    // msg.obj = "USB�豸�γ���Ӧ�ó��򼴽��رա�";
                    // MyHandler.sendMessage(msg);

                }

            } else if (common.ACTION_USB_PERMISSION.equals(action)) {// USB�豸δ��Ȩ����SDTAPI�з����Ĺ㲥
                Log.i("USB����", "USB�豸��Ȩ��");
                // Message msg = new Message();
                // msg.what=3;
                // msg.obj = "USB�豸��Ȩ��";
                // MyHandler.sendMessage(msg);
            }

        }
    };


}
