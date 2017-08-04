package com.YinanSoft.CardReaders;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sdtapi {
    public Sdtusbapi usbapi;
    Common common = new Common();

    public Sdtapi(Context instance) throws Exception {
        this.usbapi = new Sdtusbapi(instance);
    }

    public int SDT_ResetSAM() {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 16;
        SendData[3] = -1;
        int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        return ret != 144?this.byte2int(SendData[4]):ret;
    }

    public int SDT_GetSAMStatus() {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 17;
        SendData[3] = -1;
        int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        return ret == 144?this.byte2int(SendData[4]):ret;
    }

    public int SDT_GetSAMID(byte[] pucSAMID) {
		byte[] SendData = new byte[this.common.MAX_RECVLEN];
		int[] puiRecvLen = new int[1];
		SendData[0] = 0;
		SendData[1] = 3;
		SendData[2] = 18;
		SendData[3] = -1;
		int ret = 0;
		
		if (Sdtusbapi.USBHID == 1) {
			ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, puiRecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, puiRecvLen);
		}
		if (ret != 144) {
			return ret;
		} else {
			if (puiRecvLen[0] - 5 > 0 && puiRecvLen[0] > 0
					&& SendData[4] == -112) {
				for (int i = 0; i < this.common.SAMID_LEN; ++i) {
					pucSAMID[i] = SendData[i + 5];
				}
			}

			return this.byte2int(SendData[4]);
		}
    }

    public int SDT_GetSAMIDToStr(char[] pucSAMID) {
        byte[] SendData = new byte[this.common.MAX_RECVLEN];
        int[] puiRecvLen = new int[1];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 18;
        SendData[3] = -1;
        int ret = 0;
		if (Sdtusbapi.USBHID == 1) {
			ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, puiRecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, puiRecvLen);
		}
        
        this.usbapi.writefile("in Stdapi.java ret=" + ret);
        if(ret == 144) {
            if(puiRecvLen[0] - 5 <= 0 || puiRecvLen[0] <= 0 || SendData[4] != -112) {
                this.usbapi.writefile("this.puiRecvLen=" + puiRecvLen[0]);
                this.usbapi.writefile("RecvData[4]=" + String.format("%x", new Object[]{Byte.valueOf(SendData[4])}));
                return this.byte2int(SendData[4]);
            }

            String ss = "";

            for(int i = 0; i < puiRecvLen[0] - 5; ++i) {
                SendData[i] = SendData[i + 5];
                ss = ss + String.format("%x", new Object[]{Byte.valueOf(SendData[i])}) + " ";
            }

            this.usbapi.writefile("ss=" + ss);
            this.SamIDIntTostr(SendData, pucSAMID);
        }

        return ret;
    }

    public int SDT_StartFindIDCard() {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 32;
        SendData[3] = 1;
        int ret =0;
        if (Sdtusbapi.USBHID == 1) {
        	ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, RecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
		}
     
        return ret == 144?this.byte2int(SendData[4]):ret;
    }

    public int SDT_SelectIDCard() {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 32;
        SendData[3] = 2;
        int ret =0;
        if (Sdtusbapi.USBHID == 1) {
        	ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, RecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
		}
      
        return ret == 144?this.byte2int(SendData[4]):ret;
    }

    public int SDT_ReadBaseMsg(byte[] pucCHMsg, int[] puiCHMsgLen, byte[] pucPHMsg, int[] puiPHMsgLen) {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[4 + this.common.MAX_RECVLEN];
        puiCHMsgLen[0] = puiPHMsgLen[0] = 0;
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 48;
        SendData[3] = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        sdf.format(new Date());
        int ret =0;
        if (Sdtusbapi.USBHID == 1) {
        	ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, RecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
		}
        
        sdf.format(new Date());
        if(ret != 144) {
            return ret;
        } else {
            if(RecvLen[0] - 5 > 0 && RecvLen[0] > 0 && SendData[4] == -112) {
                puiCHMsgLen[0] = SendData[5] * 256 + SendData[6];
                puiPHMsgLen[0] = SendData[7] * 256 + SendData[8];
                if(puiCHMsgLen[0] > 256) {
                    puiCHMsgLen[0] = 256;
                }

                if(puiPHMsgLen[0] > 1024) {
                    puiPHMsgLen[0] = 1024;
                }

                int i;
                for(i = 0; i < puiCHMsgLen[0]; ++i) {
                    pucCHMsg[i] = SendData[i + 9];
                }

                for(i = 0; i < puiPHMsgLen[0]; ++i) {
                    pucPHMsg[i] = SendData[i + 9 + puiCHMsgLen[0]];
                }
            }

            return this.byte2int(SendData[4]);
        }
    }

    public int SDT_ReadBaseFPMsg(byte[] pucCHMsg, int[] puiCHMsgLen, byte[] pucPHMsg, int[] puiPHMsgLen, byte[] pucFPMsg, int[] puiFPMsgLen) {
        int[] RecvLen = new int[1];
        byte[] SendData = new byte[4 + this.common.MAX_RECVLEN];
        puiCHMsgLen[0] = puiPHMsgLen[0] = puiFPMsgLen[0] = 0;
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 48;
        SendData[3] = 16;
        int ret =0;
        if (Sdtusbapi.USBHID == 1) {
        	ret = this.usbapi.usbHidsendrecv(SendData, 4, SendData, RecvLen);
		} else {
			ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
		}
        if(ret != 144) {
            return ret;
        } else {
            if(RecvLen[0] - 5 > 0 && RecvLen[0] > 0 && SendData[4] == -112) {
                puiCHMsgLen[0] = SendData[5] * 256 + SendData[6];
                puiPHMsgLen[0] = SendData[7] * 256 + SendData[8];
                puiFPMsgLen[0] = SendData[9] * 256 + SendData[10];
                if(puiCHMsgLen[0] > 256) {
                    puiCHMsgLen[0] = 256;
                }

                if(puiPHMsgLen[0] > 1024) {
                    puiPHMsgLen[0] = 1024;
                }

                if(puiFPMsgLen[0] > 1024) {
                    puiFPMsgLen[0] = 1024;
                }

                int i;
                for(i = 0; i < puiCHMsgLen[0]; ++i) {
                    pucCHMsg[i] = SendData[i + 11];
                }

                for(i = 0; i < puiPHMsgLen[0]; ++i) {
                    pucPHMsg[i] = SendData[i + 11 + puiCHMsgLen[0]];
                }

                for(i = 0; i < puiFPMsgLen[0]; ++i) {
                    pucFPMsg[i] = SendData[i + 11 + puiCHMsgLen[0] + puiPHMsgLen[0]];
                }
            }

            return this.byte2int(SendData[4]);
        }
    }

    void SamIDIntTostr(byte[] pucSAMID, char[] pcSAMID) {
        String temp = "";
        int iTemp = pucSAMID[0] + pucSAMID[1] * 256;
        temp = String.format("%02d", new Object[]{Integer.valueOf(iTemp)});
        iTemp = pucSAMID[2] + pucSAMID[3] * 256;
        temp = temp + String.format(".%02d", new Object[]{Integer.valueOf(iTemp)});

        int i;
        for(i = 0; i < 3; ++i) {
            long dwTemp = Long.valueOf(String.format("%02x", new Object[]{Byte.valueOf(pucSAMID[i * 4 + 4])}), 16).longValue() + Long.valueOf(String.format("%02x", new Object[]{Byte.valueOf(pucSAMID[i * 4 + 5])}), 16).longValue() * 256L + Long.valueOf(String.format("%02x", new Object[]{Byte.valueOf(pucSAMID[i * 4 + 6])}), 16).longValue() * 256L * 256L + Long.valueOf(String.format("%02x", new Object[]{Byte.valueOf(pucSAMID[i * 4 + 7])}), 16).longValue() * 256L * 256L * 256L;
            if(i == 1) {
                temp = temp + "-" + String.format("%010d", new Object[]{Long.valueOf(dwTemp)});
            } else {
                temp = temp + "-" + dwTemp;
            }
        }

        char[] tt = temp.toCharArray();

        for(i = 0; i < tt.length; ++i) {
            pcSAMID[i] = tt[i];
        }

    }

    int byte2int(byte b) {
        int i = Integer.valueOf(String.format("%x", new Object[]{Byte.valueOf(b)}), 16).intValue();
        return i;
    }
}