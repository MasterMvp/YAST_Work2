package utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Administrator on 2016/11/24.
 */

public class ToBackBitmap {

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context mContext, String fileName) {
        Bitmap image = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }


    /**
     * 保存方法
     */
    public static String saveBitmap(Bitmap bm) {
        Log.e("Log", "保存图片");
        File f = new File("/storage/emulated/0/", "zm.png");
        String absolutePath = f.getAbsolutePath();
        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("Log", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return absolutePath;
    }


    public static Bitmap myCanvas(Bitmap bitmap) {
        Canvas canvas;
        Paint mPaint;// 画笔
        Bitmap bmp = null;// 位图
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 生成色彩矩阵
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.22F, 0.30F, 0.5F, 0, 0,
                0.22F, 0.30F, 0.5F, 0, 0,
                0.22F, 0.30F, 0.5F, 0, 0,
                0, 0, 0, 1, 0
        });
        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

        // 获取位图
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        // 绘制位图
        canvas = new Canvas(bmp);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        return bmp;
    }

    /**
     * 将彩色图转换为黑白图
     *
     * @param bmp 要转换的图片
     * @return 黑白照片
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }

    //黑白
    public static Bitmap BlackWhite(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int a, r, g, b, r1, g1, b1;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);
            //黑白矩阵
            r1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);
            g1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);
            b1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);
            //最好的 数值
//            r1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);
//            g1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);
//            b1 = (int) (0.11 * r + 0.15 * g + 0.2 * b);

            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 > 255) {
                b1 = 255;
            }

            newPx[i] = Color.argb(a, r1, g1, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);
        return resultBitmap;
    }
}
