package risks.yn.a606a.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/19.
 * 数据写入word
 */

public class FileToWork {

    /**
     * 为了保证模板的可用，最好在现有的模板上复制后修改
     */
//    private void printer() {
//        try {
//            saveFile("demo.doc", MainActivity.this, R.raw.demo);//文件目录res/raw
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        //现场检查记录
//        String aafileurl = Environment.getExternalStorageDirectory() + "/inspection/demo.doc";
//        final String bbfileurl = Environment.getExternalStorageDirectory() + "/inspection/demo_printer.doc";
//        //获取模板文件
//        File demoFile = new File(aafileurl);
//        //创建生成的文件
//        File newFile = new File(bbfileurl);
//        if (newFile.exists()) {
//            newFile.delete();
//        }
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("$companyName$", a1.getText().toString().trim());
//        map.put("$companyAddress$", a2.getText().toString().trim());
//        map.put("$companyPic$", a3.getText().toString().trim());
//        map.put("$companyWork$", a4.getText().toString().trim());
//        map.put("$companyPhone$", a5.getText().toString().trim());
//        map.put("$CheckAddress$", a6.getText().toString().trim());
//
//        map.put("$userName$", a7.getText().toString().trim());
//        map.put("$userNum$", a8.getText().toString().trim());
//        map.put("$content$", a9.getText().toString().trim());
//        writeDoc(demoFile, newFile, map);
//
//    }

    /**
     * demoFile 模板文件
     * newFile 生成文件
     * map 要填充的数据
     */
    public static boolean writeDoc(File demoFile, File newFile, Map<String, String> map) {
        try {
            FileInputStream in = new FileInputStream(demoFile);
            HWPFDocument hdt = new HWPFDocument(in);
            // 读取word文本内容
            Range range = hdt.getRange();
            // 替换文本内容
            for (Map.Entry<String, String> entry : map.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            FileOutputStream out = new FileOutputStream(newFile, true);
            hdt.write(ostream);
            // 输出字节流
            out.write(ostream.toByteArray());
            out.close();
            ostream.close();
//            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将文件复制到SD卡，并返回该文件对应的对象
     *
     * @return
     * @throws IOException
     */
    public static void saveFile(String fileName, Context context, int rawid) throws IOException {
        // 首先判断该目录下的文件夹是否存在
        File dir = new File(Environment.getExternalStorageDirectory() + "/YAST/");
        if (!dir.exists()) {
            // 文件夹不存在 ， 则创建文件夹
            dir.mkdirs();
        }
        // 判断目标文件是否存在
        File file1 = new File(dir, fileName);
        if (!file1.exists()) {
            file1.createNewFile(); // 创建文件
        }
        InputStream input = context.getResources().openRawResource(rawid); // 获取资源文件raw
        try {
            FileOutputStream out = new FileOutputStream(file1); // 文件输出流、用于将文件写到SD卡中
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = (input.read(buffer))) != -1) { // 读取文件，-- 进到内存

                out.write(buffer, 0, len);
            }
            input.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
