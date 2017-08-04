package com.YinanSoft.Police;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

//import com.google.common.base.Ascii;

/** 
 * 3DES���ܹ����� 
 *  
 * @author liufeng  
 * @date 2012-10-11 
 */  
public class secret {  
    // ��Կ  
    private final static String secretKey = "12ab34cdabcd1234abcd123456efabcd12ab34cdabcd1234";  
    // ����  
    private final static String iv = "01234567";  
    // �ӽ���ͳһʹ�õı��뷽ʽ  
    private final static String encoding ="utf-8";//"utf-8";//"UTF-16LE";//"utf-8";  
  
    /** 
     * 3DES���� 
     *  
     * @param plainText ��ͨ�ı� 
     * @return 
     * @throws Exception  
     */  
    public static String encode(String plainText,byte[] key) throws Exception {  
        Key deskey = null;  
        DESedeKeySpec spec = new DESedeKeySpec(key);//(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);  
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));  
        return Base64.encode(encryptData);  
    }  
    /** 
     * 3DES���� 
     *  
     * @param plainText ��ͨ�ı� 
     * @return 
     * @throws Exception  
     */  
    public static String encodenew(String plainText) throws Exception {  
        Key deskey = null;  
        DESedeKeySpec spec = new DESedeKeySpec(hexStringTobyte(secretKey));//(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);  
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));  
        return Base64.encode(encryptData);  
    }  
  

    public static String decode(byte[]input,byte[] key,byte[]kiv) {  
        Key deskey = null;  
        
        DESedeKeySpec spec = null;
		try {
			spec = new DESedeKeySpec(key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//secretKey.getBytes());  
       
        SecretKeyFactory keyfactory = null;
		try {
			keyfactory = SecretKeyFactory.getInstance("desede");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        try {
			deskey = keyfactory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("desede/CBC/ZerosPadding");//ZerosPadding(NoPadding"desede/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//byte[] kiv=iv.getBytes();
        IvParameterSpec ips = new IvParameterSpec(kiv);//iv.getBytes());  
        try {
			try {
				cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}  
        
        
        byte[] decryptData = null;
		try {
			decryptData = cipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Base64.decode(encryptText));  
        Log.i("hexstring", bytesToHexString(decryptData));
		
        try {
			return new String(decryptData, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;  
		}
		
    }  
    public static String decodenew(String data) {  
        Key deskey = null;  
        
        DESedeKeySpec spec = null;
        byte[] key=null;
		try {
			key=hexStringTobyte(secretKey);
			spec = new DESedeKeySpec(key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//secretKey.getBytes());  
       
        SecretKeyFactory keyfactory = null;
		try {
			keyfactory = SecretKeyFactory.getInstance("desede");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        try {
			deskey = keyfactory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");//ZerosPadding(NoPadding"desede/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		byte[] kiv=iv.getBytes();
        IvParameterSpec ips = new IvParameterSpec(kiv);//iv.getBytes());  
        try {
			try {
				cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}  
        
        byte[] inputbyte = null;
        byte[] decryptData = null;
		try {
			   
			try {
				inputbyte = data.getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			decryptData = cipher.doFinal(inputbyte);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Base64.decode(encryptText));  
        Log.i("hexstring", bytesToHexString(decryptData));
		
        try {
			return new String(decryptData, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;  
		}
		
    }  
    private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
    public static byte[] hexStringTobyte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		String temp = "";
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
			temp += result[i] + ",";
		}
		// uiHandler.obtainMessage(206, hex + "=read=" + new String(result))
		// .sendToTarget();
		return result;
	}
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp);// .toUpperCase()
		}
		return sb.toString().toUpperCase();
	}
    /*
         public static String decode(byte[]input,byte[] key) {  
        Key deskey = null;  
        
        DESedeKeySpec spec = null;
		try {
			spec = new DESedeKeySpec(key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//secretKey.getBytes());  
       
        SecretKeyFactory keyfactory = null;
		try {
			keyfactory = SecretKeyFactory.getInstance("desede");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        try {
			deskey = keyfactory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("desede/ECB/NoPadding");//(NoPadding"desede/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        try {
			cipher.init(Cipher.DECRYPT_MODE, deskey);//, ips);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}  
        
        
        byte[] decryptData = null;
		try {
			decryptData = cipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Base64.decode(encryptText));  
        
        try {
			return new String(decryptData, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;  
		}
		
    }
     */
    
}  