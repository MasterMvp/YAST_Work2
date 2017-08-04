package com.sunxi.hw;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class util {

    private static final String TAG = "sunxi_util";
    static {
        System.loadLibrary("sunxi_hw");
        Log.i(TAG, "================= lib version=" + version());
    }

    /**
     * get lib's version
     */
    public final static native String version();

    // stdio interface
    public final static int O_RDONLY = 0x00000001;
    public final static int O_WRONLY = 0x00000002;
    public final static int O_RDWR = 0x00000003;
    public final static int O_CREAT = 0x00000100;
    public final static int O_EXCL = 0x00000200;
    public final static int O_NOCTTY = 0x00000400;
    public final static int O_TRUNC = 0x00001000;
    public final static int O_APPEND = 0x00002000;
    public final static int O_NONBLOCK = 0x00004000;
    public final static int O_SYNC = 0x00010000;

    public final static int SEEK_SET = 0;
    public final static int SEEK_CUR = 1;
    public final static int SEEK_END = 2;

    /**
     * open a file with flag of (O_RDWR | O_CREAT) and mode of 0666.
     */
    public final static int open(String file) {
        return open(file, O_RDWR | O_CREAT, 0666);
    }

    /**
     * open a file.
     */
    public final static native int open(String file, int flag, int mode);

    /**
     * close a file descriptor.
     */
    public final static native boolean close(int fd);

    /**
     * write a string to a file descriptor.
     */
    public final static native int write(int fd, byte[] buf);

    /**
     * read a file descriptor.
     */
    public final static native String read(int fd, int size);

    /**
     * set the current position of a file descriptor.
     */
    public final static native long lseek(int fd, long pos, int whence);

    /**
     * force write of file with file descriptor to disk.
     */
    public final static native boolean fsync(int fd);

    /**
     * execute the command (a string) in a subshell.
     */
    public final static native int system(String cmd);

    // GPIO Control interface
    /**
     * GPIO Control
     */
    public final static int GPIO_LOW = 0;
    public final static int GPIO_HIGH = 1;

    public final static native int GPIOInput(String io_name);

    public final static int GPIOOutputHigh(String io_name) {
        return GPIOOutput(io_name, GPIO_HIGH, 0);
    }

    public final static int GPIOOutputLow(String io_name) {
        return GPIOOutput(io_name, GPIO_LOW, 0);
    }

    public final static native int GPIOOutput(String io_name, int on, int OnMs);

    // LED Control interface
    /**
     * LED Control
     */
    public final static int LED_OFF = 0;
    public final static int LED_ON = 1;

    public final static int setLEDOn(String io_name) {
        return setLED(io_name, LED_ON, 0, 0);
    }

    public final static int setLEDOff(String io_name) {
        return setLED(io_name, LED_OFF, 0, 0);
    }

    public final static native int setLED(String io_name, int on, int type,
                                          int OnMs);

    // I2C Control interface
    /**
     * I2C Control
     */
    public final static native int openI2CDevice(int channel, int dev_addr);

    public final static native void closeI2CDevice(int fd);

    public final static native int writeDataToI2CDevice(int fd, byte[] buf,
                                                        int size);

    public final static int writeDataToI2CDevice(int fd, byte[] buf) {
        int size = buf.length;
        return writeDataToI2CDevice(fd, buf, size);
    }

    public final native String readDataFromI2CDevice(int fd, int size);

    /*
     * It will send two msgs. One is write msg, other read msg.
     */
    public final native String sendI2CMsg(int fd, int dev_addr,
                                          byte[] write_buf, int write_size, int read_size);

    // Uart Control interface
    /**
     * Uart Control
     */
    private FileDescriptor mFd;
    private FileInputStream mSerialInputStream;
    private FileOutputStream mSerialOutputStream;

    public int SerialPort(File device, int baudrate, int flags)
            throws SecurityException, IOException {

		/* Check access permission */
        if (false && (!device.canRead() || !device.canWrite()) ) {
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

        mFd = openSerialPort(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native can't open " + device.getAbsolutePath());
            return -1;
            // throw new IOException();
        }
        mSerialInputStream = new FileInputStream(mFd);
        mSerialOutputStream = new FileOutputStream(mFd);

        return 0;
    }

    // Getters and setters
    public InputStream getSerialInputStream() {
        return mSerialInputStream;
    }

    public OutputStream getSerialOutputStream() {
        return mSerialOutputStream;
    }

    private native static FileDescriptor openSerialPort(String path,
                                                        int baudrate, int flags);

    public native void closeSerialPort();

}
