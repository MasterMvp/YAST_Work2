package com.YinanSoft.CardReaders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Sdtusbapi {
	private Context context;
    Common common = new Common();
    int debug = 0;
    UsbDeviceConnection mDeviceConnection;
    UsbEndpoint epOut;
    UsbEndpoint epIn;
    final String FILE_NAME = "/file.txt";
    RandomAccessFile raf;
    File targetFile;
   public static int  USBHID=0;

    public Sdtusbapi(Context instance) throws Exception {
    	this.context=instance;
        int ret = initUSB(instance);
        
        if(this.debug == 1) {
            this.writefile("inintUSB ret=" + ret);
        }

        if(ret != this.common.SUCCESS) {
            Exception e = new Exception();
            if(ret == this.common.ENOUSBRIGHT) {
                e.initCause(new Exception());
                this.writefile("error common.ENOUSBRIGHT");
            } else {
                e.initCause((Throwable)null);
                this.writefile("error null");
            }

            throw e;
        }
    }

    

	public int initUSB(Context instance) {
        this.openfile();
        UsbDevice mUsbDevice = null;
        UsbManager manager = (UsbManager)instance.getSystemService("usb");
        if(manager == null) {
            this.writefile("manager == null");
            return this.common.EUSBMANAGER;
        } else {
            if(this.debug == 1) {
                this.writefile("usb dev：" + manager.toString());
            }

            HashMap deviceList = manager.getDeviceList();
            if(this.debug == 1) {
                this.writefile("usb dev：" + String.valueOf(deviceList.size()));
            }

            Iterator deviceIterator = deviceList.values().iterator();
            ArrayList USBDeviceList = new ArrayList();

            while(deviceIterator.hasNext()) {
                UsbDevice device = (UsbDevice)deviceIterator.next();
                USBDeviceList.add(String.valueOf(device.getVendorId()));
                USBDeviceList.add(String.valueOf(device.getProductId()));
                Log.i("USBhid", "hidDECEVCE CREAT");
                if(device.getVendorId() == 1024 && device.getProductId() == 50010) {
                    mUsbDevice = device;
                    Sdtusbapi.USBHID=0;
                    if(this.debug == 1) {
                        this.writefile("zhangmeng:find device!");
                    }
                }
                else if(11147 == device.getVendorId() && 10017 == device.getProductId()){
                	 mUsbDevice = device;
                	 Sdtusbapi.USBHID=1;
                    Log.i("USBhid", "hidDECEVCE CREAT");
				}
            }

            int ret = this.findIntfAndEpt(manager, mUsbDevice);
            return ret;
        }
    }

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(128);//(bArray.length);
		String sTemp;
		for (int i = 0; i < 128; i++) { //bArray.length
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp);// .toUpperCase()
		}
		return sb.toString().toUpperCase();
	}
   public int usbsendrecv(byte[] pucSendData, int uiSendLen, byte[] RecvData, int[] puiRecvLen) {
        byte iFD = 0;
        Boolean bRet = null;
        byte ucCheck = 0;
        byte[] ucRealSendData = new byte[4096];
        byte[] pucBufRecv = new byte[4096];
        int[] iOffset = new int[1];
        if(4091 < uiSendLen) {
            return -1;
        } else {
            int iRet;
            if(-1 == iFD) {
                iRet = this.common.ENOOPEN;
                return iRet;
            } else {
                int iLen = (pucSendData[0] << 8) + pucSendData[1];
                ucRealSendData[0] = ucRealSendData[1] = ucRealSendData[2] = -86;
                ucRealSendData[3] = -106;
                ucRealSendData[4] = 105;

                for(int iIter = 0; iIter < iLen + 1; ++iIter) {
                    ucCheck ^= pucSendData[iIter];
                }

                int uiSizeSend;
                for(uiSizeSend = 0; uiSizeSend < iLen + 2; ++uiSizeSend) {
                    ucRealSendData[uiSizeSend + 5] = pucSendData[uiSizeSend];
                }

                ucRealSendData[iLen + 6] = ucCheck;
                uiSizeSend = iLen + 2 + 5;
                boolean uiSizeRecv = false;
                iRet = this.mDeviceConnection.bulkTransfer(this.epOut, ucRealSendData, uiSizeSend, 5000);
                Log.i("Send Data", bytesToHexString(ucRealSendData));
                this.writefile("before uiSizeRecv error iRet=" + iRet);
                int var18 = this.mDeviceConnection.bulkTransfer(this.epIn, pucBufRecv, pucBufRecv.length, 3000);
                Log.i("Receice Data", bytesToHexString(pucBufRecv));
                if(5 <= var18 && 4096 > var18) {
                	
                	//Log.i("Receice Data", bytesToHexString(pucBufRecv));
                    bRet = Boolean.valueOf(this.Usb_GetDataOffset(pucBufRecv, iOffset));
                    if(!bRet.booleanValue()) {
                        iRet = this.common.EDATAFORMAT;
                        this.writefile("iRet = EDATAFORMAT =" + bRet + "iOffset= " + iOffset);
                        return iRet;
                    } else {
                        iLen = (pucBufRecv[iOffset[0] + 4] << 8) + pucBufRecv[iOffset[0] + 5];
                        if(4089 < iLen) {
                            iRet = this.common.EDATALEN;
                            this.writefile("iRet = EDATALEN = " + iLen);
                            return iRet;
                        } else {
                            byte[] tempData = new byte[4096];

                            int i;
                            for(i = 0; i < pucBufRecv.length - iOffset[0] - 4; ++i) {
                                tempData[i] = pucBufRecv[i + iOffset[0] + 4];
                            }

                            bRet = Boolean.valueOf(Usb_CheckChkSum(iLen + 2, tempData));
                            if(!bRet.booleanValue()) {
                                iRet = this.common.EPCCRC;
                                this.writefile("iRet = EPCCRC");
                                return iRet;
                            } else {
                                for(i = 0; i < iLen + 1; ++i) {
                                    RecvData[i] = pucBufRecv[i + iOffset[0] + 4];
                                }

                                puiRecvLen[0] = iLen + 1;
                                this.writefile("stdapi.puiRecvLen =" + (iLen + 1));
                                return this.common.SUCCESS;
                            }
                        }
                    }
                } else {
                    iRet = this.common.EDATALEN;
                    this.writefile("uiSizeRecv error =" + var18);
                    return iRet;
                }
            }
        }
    }

	public int usbHidsendrecv(byte[] pucSendData, int uiSendLen,
			byte[] RecvData, int[] puiRecvLen) {
		byte iFD = 0;
		Boolean bRet = null;
		byte ucCheck = 0;
		byte[] ucRealSendData = new byte[4096];
		byte[] bufReceivehid = new byte[64];
		byte[] pucBufRecv = new byte[4096];
		int[] iOffset = new int[1];
		if (4091 < uiSendLen) {
			return -1;
		} else {
			int iRet;
			if (-1 == iFD) {
				iRet = this.common.ENOOPEN;
				return iRet;
			} else {
				int iLen = (pucSendData[0] << 8) + pucSendData[1];
				ucRealSendData[0] = ucRealSendData[1] = ucRealSendData[2] = -86;
				ucRealSendData[3] = -106;
				ucRealSendData[4] = 105;

				for (int iIter = 0; iIter < iLen + 1; ++iIter) {
					ucCheck ^= pucSendData[iIter];
				}

				int uiSizeSend;
				for (uiSizeSend = 0; uiSizeSend < iLen + 2; ++uiSizeSend) {
					ucRealSendData[uiSizeSend + 5] = pucSendData[uiSizeSend];
				}

				ucRealSendData[iLen + 6] = ucCheck;
				uiSizeSend = iLen + 2 + 5;
				boolean uiSizeRecv = false;
				iRet = this.mDeviceConnection.bulkTransfer(this.epOut,
						ucRealSendData, 64, 3000);
				try {
					if(pucSendData[3]==0x30)
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.i("Send Datalen", iRet+"");
				this.writefile("before uiSizeRecv error iRet=" + iRet);
				int var18 = this.mDeviceConnection.bulkTransfer(this.epIn,
						bufReceivehid, bufReceivehid.length, 1500);
				if (var18 == -1) {
					iRet = this.common.EDATALEN;
					Log.i("EDATALEN", "uiSizeRecv error =" + var18);
					return iRet;
				}
				try {
					System.arraycopy(bufReceivehid, 0, pucBufRecv, 0, 64);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("Exception", e.toString());
					iRet = this.common.EDATALEN;

					return iRet;
				}
				bRet = Boolean.valueOf(this.Usb_GetDataOffset(pucBufRecv,
						iOffset));
				if (!bRet.booleanValue()) {
					
					Log.i("Receice ", "common.EDATAFORMAT"+bytesToHexString(pucBufRecv));
					iRet = this.common.EDATAFORMAT;		
					while(this.mDeviceConnection.bulkTransfer(this.epIn,bufReceivehid, bufReceivehid.length, 800)!=-1)
					{
						Log.i("Receice ", "5555");
					}
					return iRet;
				}
				iLen = (pucBufRecv[1 + 4] << 8) + pucBufRecv[1 + 5] + 7;
				if (iLen > 64) {
					int reSeccout = iLen / 64;
					if (iLen % 64 > 0) {
						reSeccout = reSeccout + 1;
					}
					for (int i = 0; i < reSeccout - 1; i++) {
						this.mDeviceConnection.bulkTransfer(this.epIn,
								bufReceivehid, bufReceivehid.length, 800);
						try {
							System.arraycopy(bufReceivehid, 0, pucBufRecv,
									64 * (i + 1), 64);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.i("Exception", e.toString());
							iRet = this.common.EDATALEN;
						}
					}
					
				}

				if (var18 > 5) {

					Log.i("Receice Data", bytesToHexString(pucBufRecv));
					bRet = Boolean.valueOf(this.Usb_GetDataOffset(pucBufRecv,
							iOffset));
					if (!bRet.booleanValue()) {
						Log.i("Receice ", "common.EDATAFORMAT");
						iRet = this.common.EDATAFORMAT;
						this.writefile("iRet = EDATAFORMAT =" + bRet
								+ "iOffset= " + iOffset);
						return iRet;
					} else {
						iLen = (pucBufRecv[iOffset[0] + 4] << 8)
								+ pucBufRecv[iOffset[0] + 5];
						if (4089 < iLen) {
							Log.i("Receice ", "common.EDATALEN");
							iRet = this.common.EDATALEN;
							this.writefile("iRet = EDATALEN = " + iLen);
							return iRet;
						} else {
							byte[] tempData = new byte[4096];

							int i;
							for (i = 0; i < pucBufRecv.length - iOffset[0] - 4; ++i) {
								tempData[i] = pucBufRecv[i + iOffset[0] + 4];
							}

							bRet = Boolean.valueOf(Usb_CheckChkSum(iLen + 2,
									tempData));
							if (!bRet.booleanValue()) {
								Log.i("Receice ", "common.EPCCRC");
								iRet = this.common.EPCCRC;
								this.writefile("iRet = EPCCRC");
								return iRet;
							} else {
								for (i = 0; i < iLen + 1; ++i) {
									RecvData[i] = pucBufRecv[i + iOffset[0] + 4];
								}

								puiRecvLen[0] = iLen + 1;
								Log.i("Receice ", "common.SUCCESS");
								this.writefile("stdapi.puiRecvLen ="
										+ (iLen + 1));
								return this.common.SUCCESS;
							}
						}
					}
				} else {
					iRet = this.common.EDATALEN;
					this.writefile("uiSizeRecv error =" + var18);
					return iRet;
				}
			}
		}
	}

   public boolean Usb_GetDataOffset(byte[] dataBuffer, int[] iOffset) {
        iOffset[0] = 0;

        int iIter;
        for(iIter = 0; iIter < 7 && (dataBuffer[iIter + 0] != -86 || dataBuffer[iIter + 1] != -86 || dataBuffer[iIter + 2] != -106 || dataBuffer[iIter + 3] != 105); ++iIter) {
            ;
        }

        if(7 <= iIter) {
            return false;
        } else {
            iOffset[0] = iIter;
            return true;
        }
    }

    public static boolean Usb_CheckChkSum(int uiDataLen, byte[] pucRecvData) {
        byte ucCheck = 0;

        for(int iIter = 0; iIter < uiDataLen - 1; ++iIter) {
            ucCheck ^= pucRecvData[iIter];
        }

        return ucCheck == pucRecvData[uiDataLen - 1];
    }

    private void openfile() {
        if(this.debug == 1) {
            File sdCardDir = Environment.getExternalStorageDirectory();

            try {
                this.setTargetFile(new File(sdCardDir.getCanonicalPath() + "/file.txt"));
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            try {
                this.setFile(new RandomAccessFile(this.targetFile, "rw"));
            } catch (FileNotFoundException var3) {
                var3.printStackTrace();
            }

            this.writefile("in open file()");
        }

    }

    public void writefile(String context) {
        if(this.debug == 1 && Environment.getExternalStorageState().equals("mounted")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

            try {
                this.raf.seek(this.targetFile.length());
            } catch (IOException var5) {
                var5.printStackTrace();
            }

            try {
                this.raf.writeChars("\n" + sdf.format(new Date()) + " " + context);
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

    }

    private void closefile() {
        if(this.debug == 1 && this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    private int findIntfAndEpt(final UsbManager manager, final UsbDevice mUsbDevice) {
        UsbInterface mInterface = null;
        if(mUsbDevice == null) {
            this.writefile("zhangmeng:no device found");
            return this.common.EUSBDEVICENOFOUND;
        } else {
            byte connection = 0;
            if(connection < mUsbDevice.getInterfaceCount()) {
                UsbInterface intf = mUsbDevice.getInterface(connection);
                mInterface = intf;
            }

            if(mInterface != null) {
                UsbDeviceConnection connection1 = null;
                if(manager.hasPermission(mUsbDevice)) {
                    connection1 = manager.openDevice(mUsbDevice);
                    if(connection1 == null) {
                        return this.common.EUSBCONNECTION;
                    } else {
                        if(connection1.claimInterface(mInterface, true)) {
                            this.mDeviceConnection = connection1;
                            this.getEndpoint(this.mDeviceConnection, mInterface);
                        } else {
                            connection1.close();
                        }

                        return this.common.SUCCESS;
                    }
                } else {
                    this.writefile("zhangmeng:no rights");
                    (new Thread() {
                        public void run() {
                            Context var10000 =context;
                            Sdtusbapi.this.common.getClass();
                            PendingIntent pi = PendingIntent.getBroadcast(var10000, 0, new Intent("com.android.USB_PERMISSION"), 0);
                            manager.requestPermission(mUsbDevice, pi);
                        }
                    }).start();
                    return this.common.ENOUSBRIGHT;
                }
            } else {
                this.writefile("zhangmeng:no interface");
                return this.common.ENOUSBINTERFACE;
            }
        }
    }

    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if(intf.getEndpoint(1) != null) {
            this.epOut = intf.getEndpoint(1);
        }

        if(intf.getEndpoint(0) != null) {
            this.epIn = intf.getEndpoint(0);
        }

    }

    private void setFile(RandomAccessFile raf) {
        this.raf = raf;
    }

    private void setTargetFile(File f) {
        this.targetFile = f;
    }
}
