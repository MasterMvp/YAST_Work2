package com.techshino.fingerprint;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class tesoutil {
  public static boolean upgradedirPermission(String pkgCodePath) {
    Process process = null;
    DataOutputStream os = null;
    try {
      Log.d("initUsbDevice", "techshino mUsbReceiver1 pkgCodePath = " + pkgCodePath);
      String cmd = "chmod -R 0777 " + pkgCodePath;
      process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.writeBytes("exit\n");
      os.flush();
      process.waitFor();
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
    return true;
  }

  public static boolean upgradeRootPermission(String pkgCodePath) {
    Process process = null;
    DataOutputStream os = null;
    try {
      Log.d("initUsbDevice", "techshino mUsbReceiver1 pkgCodePath = " + pkgCodePath);
      String cmd = "chmod 0777 " + pkgCodePath;
      process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.writeBytes("exit\n");
      os.flush();
      process.waitFor();
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
    return true;
  }

  public static boolean upgradeown(String pkgCodePath) {
    Process process = null;
    DataOutputStream os = null;
    try {
      Log.d("initUsbDevice", "techshino mUsbReceiver1 pkgCodePath = " + pkgCodePath);
      String cmd = "chown system:system " + pkgCodePath;
      process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.writeBytes("exit\n");
      os.flush();
      process.waitFor();
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
    return true;
  }

  public static boolean upgradegroup(String pkgCodePath) {
    Process process = null;
    DataOutputStream os = null;
    try {
      Log.d("initUsbDevice", "techshino mUsbReceiver1 pkgCodePath = " + pkgCodePath);
      String cmd = "chgrp system " + pkgCodePath;
      process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.writeBytes("exit\n");
      os.flush();
      process.waitFor();
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
    return true;
  }

  public static void saveBmp(byte[] bitmap) {
    if (bitmap == null)
      return;

    try {
      // �洢�ļ���
      int i = 0;
      String filename;
      File file;
      while (true) {
        filename = "/sdcard/teso/";
        file = new File(filename);
        if (!file.exists())
          file.mkdir();
        filename = "/sdcard/teso/tesofinger" + i + ".bmp";
        file = new File(filename);

        if (file.exists()) {
          i++;
          continue;

        } else {

          break;
        }

      }

      FileOutputStream fileos = new FileOutputStream(filename);

      fileos.write(bitmap);
      fileos.flush();
      fileos.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void savebitmap(byte[] bitmap) {

    try {
      // �洢�ļ���
      int i = 0;
      String filename;
      File file;
      while (true) {
        filename = "/sdcard/teso/";
        file = new File(filename);
        if (!file.exists())
          file.mkdir();
        filename = "/sdcard/teso/teso" + i + ".bmp";
        file = new File(filename);

        if (file.exists()) {
          i++;
          continue;

        } else {

          break;
        }

      }

      FileOutputStream fileos = new FileOutputStream(filename);

      fileos.write(bitmap);
      fileos.flush();
      fileos.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void saveBitmapToPNG(Bitmap bitmap, String bitName) throws IOException {
    File file;
    String filename = "/sdcard/teso/";
    file = new File(filename);
    if (!file.exists())
      file.mkdir();
    filename = "/sdcard/teso/" + bitName;
    file = new File(filename);

    if (file.exists()) {
      file.delete();
    }
    FileOutputStream out;
    try {
      out = new FileOutputStream(file);
      if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
        out.flush();
        out.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Bitmap getRedTransparentBitmap(Bitmap bmFinger) {
    Bitmap sourceImg = bmFinger.copy(Config.ARGB_8888, true);
    int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

    sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

        .getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ

    //number = number * 255 / 100;

    for (int i = 0; i < argb.length; i++) {
      if (((argb[i] & 0x000000FF) > 0x00000096) || (argb[i] & 0x0000FF00) > 0x00009600) {
        argb[i] = (0 << 24) | (argb[i] & 0x00FF0000);
      } else {
        //argb[i] = (0xff << 24) | (argb[i] & 0x00FFFFFF);//ԭͼ��
        argb[i] = (0xff << 24) | (argb[i] & 0x00FF0000);//�����Ϊ�˴���
      }

    }

    sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

        .getHeight(), Config.ARGB_8888);

    return sourceImg;
  }


}
