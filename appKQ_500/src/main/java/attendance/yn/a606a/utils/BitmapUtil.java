package attendance.yn.a606a.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/4/18.
 */

public class BitmapUtil {

    /**
     * 把Bitmap转Byte
     *
     * @param bm Bitmap数据
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte数组转Bitmap
     *
     * @param bytes byte数组
     * @return
     */
    public static Bitmap Bytes2Bitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
}
