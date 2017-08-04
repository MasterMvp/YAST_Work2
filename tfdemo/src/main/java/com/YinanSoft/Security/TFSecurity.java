package com.YinanSoft.Security;

import android.util.Log;

public class TFSecurity {

    static {
        System.loadLibrary("skf");
        System.loadLibrary("TFSecurity");
    }

    // public native long start(String strPath);

    // SM1算法加密数据（加密密钥由参数输入）输入 不可少于16位
//	public native long SymEncryt(byte[] key, byte[] plaindata, byte[] endata,int[] length);

    // 设备认证
//	public native long DevAuth(byte[] szKey);

    // 单组数据加密（指定密钥索引号）
    public native long Encrypt(int index, byte[] Plaindata, byte[] Cipherdata, int[] length);

    // 单组数据解密（指定密钥索引号）
    public native long Decrypt(int index, byte[] Cipherdata, byte[] Plaindata, int[] length);

    // 单组数据计算MAC（指定索引密钥）
    public native long Mac(int index, byte[] MACdata, byte[] MAC, int[] MACLength);


}
