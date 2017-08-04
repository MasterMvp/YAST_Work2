package com.YinanSoft.FingerPrinterReaders;

import android.content.Context;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AAReader
{
	protected String TAG = "AAReader";
	private Context mContext = null;
	protected boolean isOpen;

	//串口操作相关定义
	private FileDescriptor mFd = null;
	private FileInputStream mFileInputStream = null;
	private FileOutputStream mFileOutputStream = null;
	protected String PATH = "/dev/ttyS1";
	protected int BAUDRATE = 115200;//115200;	//a320

	static {
		System.loadLibrary("FingerPrinterReader");
	}
	private native static FileDescriptor AAModuleOpen(String path, int baudrate, int flags);
	private native void AAModuleClose();
	private native static int AAModuleVerify(long id,int imgae);
	private native static int AAModuleSetTemplate2ndID(long id, byte[]data, int size, int cflag);
	private native static int AAModuleClear();
	private native static int AAModuleSetSecurity(int level);
	private native static int AAModuleSetDeviceBaud(int baud);

	private static AAReader self;
	public static AAReader getInstance(Context context){
		if(self == null){
			self = new AAReader(context);
		}
		return self;
	}



	public AAReader(Context theContext)
	{
		isOpen = false;
		mContext = theContext;
	}

	static public void GPIOOutputHigh(String gpio) {
		try
		{
			FileWriter localFileWriter = new FileWriter(new File("/sys/devices/platform/leds-gpio/leds/"+gpio+"/brightness"));
			localFileWriter.write("1");
			localFileWriter.close();
		}
		catch (IOException localIOException)
		{
			localIOException.printStackTrace();
		}
	}

	static public void GPIOOutputLow(String gpio) {
		try
		{
			FileWriter localFileWriter = new FileWriter(new File("/sys/devices/platform/leds-gpio/leds/"+gpio+"/brightness"));
			localFileWriter.write("0");
			localFileWriter.close();

		}
		catch (IOException localIOException)
		{
			localIOException.printStackTrace();
		}
	}

	public void PowerOnReader() {
		GPIOOutputHigh("out1");
		GPIOOutputLow("out0");
	}

	public void PowerOffReader(){
		GPIOOutputLow("out1");
	}

	public void InitFingerReader(String path,int baudrate,int flags)
	{
		try {
			PATH=path;
			openSerialPort(new File(path), baudrate, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int ModuleSetSecurity(int lev)
	{
		return AAModuleSetSecurity(lev);
	}
	public int ModuleSetDeviceBaud(int baud)
	{
		if(AAModuleSetDeviceBaud(baud)==0)
		{

			AAModuleClose();
			try {
				BAUDRATE=baud;
				openSerialPort(new File(PATH), baud, 0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
		return 1;

	}
	public int ModuleClear()
	{
//    	byte[] data={0x01,0x02};
//		try {
//			write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return AAModuleClear();


	}
	public int ModuleSetTemplate_2ndID(long id, byte[]data, int size, int cflag)
	{

		return AAModuleSetTemplate2ndID( id, data,  size,  cflag);

	}
	public int ModuleVerify(long id,int image)
	{
		return AAModuleVerify(id,image);
	}

	public String getReaderPowerStatus() {return "";}

	public boolean InitReader(byte[] byLicBuf){return false;}
	public void ReleaseReader(){closeSerialPort();}
	//------------------------------------------------------------------------------------------------------------
	// SerialPort
	//------------------------------------------------------------------------------------------------------------
	protected void closeSerialPort() {
		try {
			if(mFileInputStream!=null)
				mFileInputStream.close();
			if(mFileOutputStream!=null)
				mFileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AAModuleClose();
		mFd = null;
	}

	protected boolean openSerialPort(File device, int baudrate, int flags) throws SecurityException, IOException
	{
		try {
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
			//closeSerialPort();
			mFd = AAModuleOpen(device.getAbsolutePath(), baudrate, flags);
			if (mFd == null) {
				//Log.e(TAG, "native open returns null");
				//throw new IOException();
				return false;
			}


			mFileInputStream = new FileInputStream(mFd);
			mFileOutputStream = new FileOutputStream(mFd);

		} catch(Exception exp){return false;}

		return true;
	}

	// Getters and setters
	private InputStream getInputStream()
	{
		return mFileInputStream;
	}

	private OutputStream getOutputStream()
	{
		return mFileOutputStream;
	}

	protected int read(byte recvBuf[], int recvLen, long waitTime) throws IOException
	{
		long lBeginTime = System.currentTimeMillis();//更新当前秒计数
		long lCurrentTime = 0;
		int nRet = 0;
		int nReadedSize = 0;


		while(true)
		{

			if (mFileInputStream.available() > 0)
			{
				nRet = mFileInputStream.read(recvBuf, nReadedSize, (recvLen - nReadedSize));
				if (nRet > 0)
				{
					nReadedSize += nRet;
					//mCurrentSize += nReadedSize;
					if(recvLen == nReadedSize)	return 0;
				}
			}

			try
			{
				Thread.sleep(10);
				lCurrentTime = System.currentTimeMillis();
				if((lCurrentTime - lBeginTime) > waitTime)
				{
					return 2;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected int read(byte recvBuf[], long waitTime) throws IOException
	{
		long lBeginTime = System.currentTimeMillis();//更新当前秒计数
		long lCurrentTime = 0;
		int nRet = 0;
		int nReadedSize = 0;


		while(true)
		{

			if (mFileInputStream.available() > 0)
			{
				nRet = mFileInputStream.read(recvBuf, nReadedSize, 1);
				if (nRet > 0)
				{
					nReadedSize += nRet;
					//mCurrentSize += nReadedSize;
					//if(recvLen == nReadedSize)	return 0;
				}
			}

			try
			{
				Thread.sleep(10);
				lCurrentTime = System.currentTimeMillis();
				if((lCurrentTime - lBeginTime) > waitTime)
				{
					return nReadedSize;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected void write(byte[] data) throws IOException
	{
		//mCurrentSize = 0;
		while (mFileInputStream.available() > 0)	mFileInputStream.read();
		mFileOutputStream.write(data);
	}
}
