package jni;

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

import beans.IDCardInfo;

public class IDCardReader {

	static {
		System.loadLibrary("IDCardReader");
	}
	protected int CODE = 0;
	protected String TAG = "IDCardReader";
	private Context mContext = null;
	protected boolean isOpen;
	protected String sKey = "";

	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	protected String PATH = "/dev/ttySAC3";
	protected int BAUDRATE = 115200; // a320

	private native static FileDescriptor open(String path, int baudrate, int flags);

	private native void close();

	protected native String stringFromJNI(byte[] samIDBuffer, byte[] LicFileBuffer, int readerCode);

	protected native int WltToBmp(String wltFileName, String bmpFileName, String sKey);

	protected native int WltToBmpBuffer(byte[] wltBuffer, byte[] bmpBuffer, String sKey);



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

	public String ReadCardID() {
		return "";
	}

	public String ReadSAMID(String[] sRet) {
		return "";
	}

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

	// -----------------------------------------------------------------------------------------------
	protected String parseNation(int code) {
		String nation;
		switch (code) {
		case 1:
			nation = "��";
			break;
		case 2:
			nation = "�ɹ�";
			break;
		case 3:
			nation = "��";
			break;
		case 4:
			nation = "��";
			break;
		case 5:
			nation = "ά���";
			break;
		case 6:
			nation = "��";
			break;
		case 7:
			nation = "��";
			break;
		case 8:
			nation = "׳";
			break;
		case 9:
			nation = "����";
			break;
		case 10:
			nation = "����";
			break;
		case 11:
			nation = "��";
			break;
		case 12:
			nation = "��";
			break;
		case 13:
			nation = "��";
			break;
		case 14:
			nation = "��";
			break;
		case 15:
			nation = "����";
			break;
		case 16:
			nation = "����";
			break;
		case 17:
			nation = "������";
			break;
		case 18:
			nation = "��";
			break;
		case 19:
			nation = "��";
			break;
		case 20:
			nation = "����";
			break;
		case 21:
			nation = "��";
			break;
		case 22:
			nation = "�";
			break;
		case 23:
			nation = "��ɽ";
			break;
		case 24:
			nation = "����";
			break;
		case 25:
			nation = "ˮ";
			break;
		case 26:
			nation = "����";
			break;
		case 27:
			nation = "����";
			break;
		case 28:
			nation = "����";
			break;
		case 29:
			nation = "�¶�����";
			break;
		case 30:
			nation = "��";
			break;
		case 31:
			nation = "���Ӷ�";
			break;
		case 32:
			nation = "����";
			break;
		case 33:
			nation = "Ǽ";
			break;
		case 34:
			nation = "����";
			break;
		case 35:
			nation = "����";
			break;
		case 36:
			nation = "ë��";
			break;
		case 37:
			nation = "����";
			break;
		case 38:
			nation = "����";
			break;
		case 39:
			nation = "����";
			break;
		case 40:
			nation = "����";
			break;
		case 41:
			nation = "������";
			break;
		case 42:
			nation = "ŭ";
			break;
		case 43:
			nation = "���α��";
			break;
		case 44:
			nation = "����˹";
			break;
		case 45:
			nation = "���¿�";
			break;
		case 46:
			nation = "�°�";
			break;
		case 47:
			nation = "����";
			break;
		case 48:
			nation = "ԣ��";
			break;
		case 49:
			nation = "��";
			break;
		case 50:
			nation = "������";
			break;
		case 51:
			nation = "����";
			break;
		case 52:
			nation = "���״�";
			break;
		case 53:
			nation = "����";
			break;
		case 54:
			nation = "�Ű�";
			break;
		case 55:
			nation = "���";
			break;
		case 56:
			nation = "��ŵ";
			break;
		case 97:
			nation = "����";
			break;
		case 98:
			nation = "���Ѫͳ�й�����ʿ";
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

	// ------------------------------------------------------------------------------------------------------------
	// SerialPort
	// ------------------------------------------------------------------------------------------------------------
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
		 * // Check access permission if (!device.canRead() ||
		 * !device.canWrite()) { try { //* Missing read/write permission, trying
		 * to chmod the file Process su; su =
		 * Runtime.getRuntime().exec("/system/bin/su"); String cmd =
		 * "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
		 * su.getOutputStream().write(cmd.getBytes()); if ((su.waitFor() != 0)
		 * || !device.canRead() || !device.canWrite()) { throw new
		 * SecurityException(); } } catch (Exception e) { e.printStackTrace();
		 * throw new SecurityException(); } }
		 */
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
		long lBeginTime = System.currentTimeMillis();// ���µ�ǰ�����
		long lCurrentTime = 0;
		int nRet = 0;
		int nReadedSize = 0;

		while (true) {

			if (mFileInputStream.available() > 0) {
				nRet = mFileInputStream.read(recvBuf, nReadedSize, (recvLen - nReadedSize));
				if (nRet > 0) {
					nReadedSize += nRet;
					if (recvLen == nReadedSize)
						return 0;
				}
			}

			try {
				Thread.sleep(10);
				lCurrentTime = System.currentTimeMillis();
				if ((lCurrentTime - lBeginTime) > waitTime) {
					return 2;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected int read(byte recvBuf[], long waitTime) throws IOException {
		long lBeginTime = System.currentTimeMillis();// ���µ�ǰ�����
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
		while (mFileInputStream.available() > 0)
			mFileInputStream.read();
		mFileOutputStream.write(data);
	}

	// -----------------------------------------------------------------------------------------------
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
