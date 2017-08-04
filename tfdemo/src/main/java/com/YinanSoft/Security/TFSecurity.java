package com.YinanSoft.Security;

import android.util.Log;

public class TFSecurity {

    static {
        System.loadLibrary("skf");
        System.loadLibrary("TFSecurity");
    }

    // public native long start(String strPath);

    // SM1�㷨�������ݣ�������Կ�ɲ������룩���� ��������16λ
//	public native long SymEncryt(byte[] key, byte[] plaindata, byte[] endata,int[] length);

    // �豸��֤
//	public native long DevAuth(byte[] szKey);

    // �������ݼ��ܣ�ָ����Կ�����ţ�
    public native long Encrypt(int index, byte[] Plaindata, byte[] Cipherdata, int[] length);

    // �������ݽ��ܣ�ָ����Կ�����ţ�
    public native long Decrypt(int index, byte[] Cipherdata, byte[] Plaindata, int[] length);

    // �������ݼ���MAC��ָ��������Կ��
    public native long Mac(int index, byte[] MACdata, byte[] MAC, int[] MACLength);


}
