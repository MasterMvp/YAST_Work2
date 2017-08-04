//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package printpp.printpp_yt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BluetoothPort {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket mmBtSocket;
    private byte[] _cmd = new byte[1];
    public boolean isOpen = false;
    private String btDeviceString;
    private OutputStream mmOutStream = null;
    private InputStream mmInStream = null;

    public BluetoothPort() {
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    public boolean getBluetoothStateON(int timeout) {
        for(int loop = timeout / 50; loop > 0; --loop) {
            int r = this.btAdapter.getState();
            if(r == 12) {
                return true;
            }

            try {
                Thread.sleep(50L);
            } catch (InterruptedException var5) {
                ;
            }
        }

        return false;
    }

    public boolean open(String strBtAddr, int timeout) {
        this.isOpen = false;
        if(strBtAddr == null) {
            return false;
        } else {
            this.btAdapter = BluetoothAdapter.getDefaultAdapter();
            this.btDeviceString = strBtAddr;
            if(timeout < 1000) {
                timeout = 1000;
            }

            if(timeout > 6000) {
                timeout = 6000;
            }

            long start_time = SystemClock.elapsedRealtime();

            while(12 != this.btAdapter.getState()) {
                if(SystemClock.elapsedRealtime() - start_time > (long)timeout) {
                    Log.e("PP", "adapter state on timeout");
                    return false;
                }

                try {
                    Thread.sleep(200L);
                } catch (InterruptedException var13) {
                    var13.printStackTrace();
                }
            }

            BluetoothSocket TmpSock = null;

            try {
                BluetoothDevice e = this.btAdapter.getRemoteDevice(this.btDeviceString);
                TmpSock = e.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (Exception var12) {
                TmpSock = null;
                Log.e("PP", "createRfcommSocketToServiceRecord exception");
                this.isOpen = false;
                return false;
            }

            this.mmBtSocket = TmpSock;
            start_time = SystemClock.elapsedRealtime();

            while(true) {
                try {
                    this.mmBtSocket.connect();
                    break;
                } catch (Exception var14) {
                    var14.printStackTrace();
                    Log.e("PP", "connect exception");
                    if(SystemClock.elapsedRealtime() - start_time > (long)timeout) {
                        try {
                            this.mmBtSocket.close();
                        } catch (IOException var8) {
                            var8.printStackTrace();
                        }

                        this.isOpen = false;
                        Log.e("PP", "connet timeout");
                        return false;
                    }

                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }
                }
            }

            try {
                this.mmOutStream = this.mmBtSocket.getOutputStream();
            } catch (IOException var10) {
                var10.printStackTrace();
            }

            try {
                this.mmInStream = this.mmBtSocket.getInputStream();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            this.isOpen = true;
            Log.e("PP", "connect ok");
            return true;
        }
    }

    public boolean close() {
        if(this.mmBtSocket == null) {
            this.isOpen = false;
            Log.e("PP", "mmBtSocket null");
            return false;
        } else {
            if(this.isOpen) {
                try {
                    if(this.mmOutStream != null) {
                        this.mmOutStream.close();
                        this.mmOutStream = null;
                    }

                    if(this.mmInStream != null) {
                        this.mmInStream.close();
                        this.mmOutStream = null;
                    }

                    this.mmBtSocket.close();
                } catch (Exception var2) {
                    this.isOpen = false;
                    Log.e("PP", "close exception");
                    return false;
                }
            }

            this.isOpen = false;
            this.mmBtSocket = null;
            return true;
        }
    }

    public boolean flushReadBuffer() {
        byte[] buffer = new byte[64];
        if(!this.isOpen) {
            return false;
        } else {
            while(true) {
                boolean r = false;

                try {
                    int r1 = this.mmInStream.available();
                    if(r1 == 0) {
                        return true;
                    }

                    if(r1 > 0) {
                        if(r1 > 64) {
                            r1 = 64;
                        }

                        this.mmInStream.read(buffer, 0, r1);
                    }
                } catch (IOException var5) {
                }

                try {
                    Thread.sleep(10L);
                } catch (InterruptedException var4) {
                    ;
                }
            }
        }
    }

    public boolean write(byte[] buffer, int offset, int length) {
        if(!this.isOpen) {
            return false;
        } else if(this.mmBtSocket == null) {
            Log.e("PP", "mmBtSocket null");
            return false;
        } else if(this.mmOutStream == null) {
            Log.e("PP", "mmOutStream null");
            return false;
        } else {
            try {
                this.mmOutStream.write(buffer, offset, length);
                return true;
            } catch (Exception var5) {
                return false;
            }
        }
    }

    public boolean writeNULL() {
        this._cmd[0] = 0;
        return this.write(this._cmd, 0, 1);
    }

    public boolean write(char[] data, int offset, int length, int size) {
        int i;
        if(size == 1) {
            for(i = offset; i < offset + length; ++i) {
                this._cmd[0] = (byte)data[i];
                this.write(this._cmd, 0, 1);
            }
        } else {
            for(i = offset; i < offset + length; ++i) {
                this._cmd[0] = (byte)data[i];
                this._cmd[1] = (byte)(data[i] >> 8);
                this.write(this._cmd, 0, 2);
            }
        }

        return true;
    }

    public boolean write(String text) {
        byte[] data = (byte[])null;

        try {
            data = text.getBytes("GBK");
        } catch (UnsupportedEncodingException var4) {
            Log.e("PP", "Sting getBytes(\'GBK\') failed");
            return false;
        }

        return !this.write(data, 0, data.length)?false:this.writeNULL();
    }

    public boolean WriteM30(byte[] buffer) {
        byte[] Cmd = new byte[4];
        boolean Result = true;
        int Sum = 0;

        for(int Rep = 0; Rep < 72; ++Rep) {
            Sum += (short)buffer[Rep];
        }

        Cmd[0] = 31;
        Cmd[1] = -103;
        Cmd[2] = (byte)(Sum & 255);
        Cmd[3] = (byte)((Sum & '\uff00') >> 8);
        Result = this.write(Cmd, 0, 4);
        if(!Result) {
            return false;
        } else {
            Result = this.write(buffer, 0, 72);
            if(!Result) {
                return false;
            } else {
                byte[] var6 = new byte[3];
                return this.read(var6, 3, 500)?(var6[0] == 31 && var6[1] == 153 && var6[2] == 0?true:(var6[0] == 31 && var6[1] == 153 && var6[2] == 1?false:false)):false;
            }
        }
    }

    public boolean read(byte[] buffer, int offset, int length, int timeout_read) {
        if(!this.isOpen) {
            return false;
        } else {
            if(timeout_read < 200) {
                timeout_read = 200;
            }

            if(timeout_read > 5000) {
                timeout_read = 5000;
            }

            try {
                long ex = SystemClock.elapsedRealtime();
                long cur_time = 0L;
                int need_read = length;
                boolean cur_readed = false;

                while(true) {
                    if(this.mmInStream.available() > 0) {
                        int cur_readed1 = this.mmInStream.read(buffer, offset, need_read);
                        offset += cur_readed1;
                        need_read -= cur_readed1;
                    }

                    if(need_read == 0) {
                        return true;
                    }

                    cur_time = SystemClock.elapsedRealtime();
                    if(cur_time - ex > (long)timeout_read) {
                        Log.e("PP", "read timeout");
                        return false;
                    }

                    Thread.sleep(20L);
                }
            } catch (Exception var11) {
                Log.e("PP", "read exception");
                this.close();
                return false;
            }
        }
    }

    public boolean read(byte[] buffer, int length, int timeout_read) {
        return length > buffer.length?false:this.read(buffer, 0, length, timeout_read);
    }

    public int readLength() {
        try {
            return this.mmInStream.available();
        } catch (IOException var2) {
            Log.e("PP", "read exception");
            this.close();
            return 0;
        }
    }
}
