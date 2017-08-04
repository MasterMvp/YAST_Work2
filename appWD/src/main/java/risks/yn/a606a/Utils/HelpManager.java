package risks.yn.a606a.Utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;


public class HelpManager {
    public HelpManager() {
    }

    public void finalize() {
    }

    public static boolean exist(String filepath) {
        if(filepath == null) {
            return false;
        } else {
            File file = new File(filepath);
            boolean exist = file.exists();
            file = null;
            return exist;
        }
    }

    public static boolean isDirectory(String filepath) {
        File file = new File(filepath);
        return file.isDirectory();
    }

    public static boolean makeSureDirExist(String dirpath) {
        boolean exist = true;
        File file = new File(dirpath);
        if(!file.exists()) {
            exist = file.mkdir();
        }

        return exist;
    }

    public static boolean makeSureFileExist(String filepath) {
        boolean exist = true;
        File file = new File(filepath);
        if(!file.exists()) {
            try {
                exist = file.createNewFile();
            } catch (IOException var4) {
                exist = false;
            }
        }

        return exist;
    }

    public static int makeSureFileExistEx(String filepath) {
        int status = -1;
        File file = new File(filepath);
        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    status = 0;
                }
            } catch (IOException var4) {
                status = -1;
            }
        } else {
            status = (int)file.length();
        }

        return status;
    }

    public static int getFileLength(String filepath) {
        File file = new File(filepath);
        return file.exists()?(int)file.length():-1;
    }

    public static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        return file.exists()?file.delete():true;
    }

    public static boolean copyFile(String src, String dst) {
        boolean result = false;

        try {
            File in = new File(src);
            File out = new File(dst);
            FileInputStream inFile = new FileInputStream(in);
            FileOutputStream outFile = new FileOutputStream(out);
            boolean i = false;
            byte[] buffer = new byte[1024];

            int i1;
            while((i1 = inFile.read(buffer)) != -1) {
                outFile.write(buffer, 0, i1);
            }

            buffer = (byte[])null;
            inFile.close();
            outFile.close();
            result = true;
        } catch (IOException var9) {
            ;
        }

        return result;
    }

    public static String newImageName() {
        String uuidStr = UUID.randomUUID().toString();
        return uuidStr.replaceAll("-", "") + ".jpg";
    }

    public static byte[] getBytesFromFile(String path) throws IOException {
        File file = new File(path);
        return getBytesFromFile(file);
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        if(length > 2147483647L) {
            is.close();
            throw new IOException("File is to large " + file.getName());
        } else {
            byte[] bytes = new byte[(int)length];

            byte[] var8;
            try {
                int e = 0;

                int numRead1;
                for(boolean numRead = false; e < bytes.length && (numRead1 = is.read(bytes, e, bytes.length - e)) >= 0; e += numRead1) {
                    ;
                }

                if(e < bytes.length) {
                    throw new IOException("Could not completely read file " + file.getName());
                }

                is.close();
                var8 = bytes;
            } catch (Exception var11) {
                return null;
            } finally {
                bytes = (byte[])null;
            }

            return var8;
        }
    }

    public static String getStrFromFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        if(length > 2147483647L) {
            is.close();
            throw new IOException("File is to large " + file.getName());
        } else {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"));
            String data = "";

            while((data = br.readLine()) != null) {
                sb.append(data);
                sb.append("\n");
            }

            String result = sb.toString();
            is.close();
            return result;
        }
    }

    public static void generateOtherImg(String imagePath) {
    }
}
