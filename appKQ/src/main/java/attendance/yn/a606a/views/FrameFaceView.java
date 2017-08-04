package attendance.yn.a606a.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.YinanSoft.phoneface.common.Logs;

import attendance.yn.a606a.R;

/**
 * Created by wangzhi on 2016/5/23.
 */
public class FrameFaceView extends View {

    private static final String TAG = FrameFaceView.class.getSimpleName();

    public static int WIDTH = 480;
    public static int HEIGHT = 640;

    private Paint mPaint;

    private int[] mLocFace;

    private int mWidth = 0;

    private int mHeight = 0;

    private float mDx;

    private float mDy;

    private Bitmap mBitmap;

    private int mBitmapWidth;

    private int mBitmapHeight;

    private Context context;

    public FrameFaceView(Context context) {
        super(context);
        init(context);
    }

    public FrameFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FrameFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.frame)).getBitmap();
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mTempWidth = getMeasuredWidth();
        int mTempHeight = getMeasuredHeight();

        float radio = (float) mTempWidth / mTempHeight;

        //mTempWidth:800 mTempHeight:408 radio:1.9607843 radio:1.3333334

        Logs.i("FrameFaceView1", "mTempWidth:" + mTempWidth + " mTempHeight:" + mTempHeight + " radio:" + radio + " radio:" + ((float) WIDTH / HEIGHT));

        if (radio < (float) WIDTH / HEIGHT) {
            mHeight = getMeasuredHeight();
            mWidth = mHeight * WIDTH / HEIGHT;

            mDx = (float) (mWidth - mTempWidth) / 2;
        } else {
            mWidth = getMeasuredWidth();
            mHeight = mWidth * HEIGHT / WIDTH;

            mDy = (float) (mHeight - mTempHeight) / 2;
        }

        Logs.i(TAG, "width:" + mWidth + " height:" + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//    String str = "请目视摄像头";
//    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//    p.setTextSize(50);
//    p.setColor(Color.RED);// 设置红色
//    canvas.drawText(str, (HotelApp.screenWidth - p.measureText(str)) / 2, 50, p);// 画文本

        if (mLocFace == null)
            return;
//    float right = getX(mLocFace[0]) - mDx;
//    float top = getY(mLocFace[1]) - mDy;
//    float left = right - (float) mLocFace[2] * mWidth / WIDTH;
//    float bottom = top + (float) mLocFace[3] * mHeight / HEIGHT;

        float left = 0, top = 0, right = 0, bottom = 0;

        if (Build.MODEL.toUpperCase().contains("JWZD-606")) {
            if (1 == 1) {
                left = getX(mLocFace[0]) - mDx - 190;
                top = getY(mLocFace[1]) - mDy;
                right = 50 + left + (float) mLocFace[2] * mWidth / WIDTH;
                bottom = 50 + top + (float) mLocFace[3] * mHeight / HEIGHT;
            } else {
                left = 460 - getX(mLocFace[0]) - mDx;
                top = getY(mLocFace[1]) - mDy;
                right = 50 + left + (float) mLocFace[2] * mWidth / WIDTH;
                bottom = 50 + top + (float) mLocFace[3] * mHeight / HEIGHT;
            }

        } else if (Build.MODEL.toUpperCase().contains("JWZD-606A")) {

            /**
             * 606A带后置补光灯的版本
             */
            if (1 == 1) {//前置
                left = getX(mLocFace[0]) - mDx - 140;
                top = getY(mLocFace[1]) - mDy;
                right = 50 + left + (float) mLocFace[2] * mWidth / WIDTH;
                bottom = 50 + top + (float) mLocFace[3] * mHeight / HEIGHT;
            } else {
                left = 460 - getX(mLocFace[0]) - mDx;
                top = getY(mLocFace[1]) - mDy;
                right = 50 + left + (float) mLocFace[2] * mWidth / WIDTH;
                bottom = 50 + top + (float) mLocFace[3] * mHeight / HEIGHT;
            }

        } else {//P3
            if (1 == 1) {
                right = getX(mLocFace[0]) - mDx;
                top = getY(mLocFace[1]) - mDy;
                left = right - (float) mLocFace[2] * mWidth / WIDTH;
                bottom = top + (float) mLocFace[3] * mHeight / HEIGHT;
            } else {
                right = getX(mLocFace[0]) - mDx + 100;
                top = getY(mLocFace[1]) - mDy;
                left = 50 + right - (float) mLocFace[2] * mWidth / WIDTH;
                bottom = top + (float) mLocFace[3] * mHeight / HEIGHT;
            }

//            right = getX(mLocFace[0]) - mDx - 200;
//            top = getY(mLocFace[1]) - mDy - 200;
//            left = right - (float) mLocFace[2] * mWidth / WIDTH;
//            bottom = (float) mLocFace[3] * mHeight / HEIGHT;
        }


        Logs.i(TAG, "mLocFace[0]:" + mLocFace[0]);//（280，168）
        Logs.i(TAG, "mLocFace[1]" + mLocFace[1]);
        Logs.i(TAG, "mLocFace[2]:" + mLocFace[2]);//144
        Logs.i(TAG, "mLocFace[3]:" + mLocFace[3]);//144
        Logs.i(TAG, "getX(mLocFace[0]): " + getX(mLocFace[0]));
        Logs.i(TAG, "getY(mLocFace[1]): " + getY(mLocFace[1]));
        Logs.i(TAG, "mDx:" + mDx);//0
        Logs.i(TAG, "mDY:" + mDy);//72
        Logs.i(TAG, "left:" + left);
        Logs.i(TAG, "right:" + right);
        Logs.i(TAG, "top:" + top);
        Logs.i(TAG, "bottom:" + bottom);

        //第一个Rect 代表要绘制的bitmap 区域，第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方

//        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) mLocFace[0], (int) mLocFace[1], (int) mLocFace[2] *4, (int) mLocFace[3] + mBitmapHeight ), mPaint);
//        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) mLocFace[0], (int) mLocFace[1], (int) mLocFace[2], (int) mLocFace[3]), mPaint);
        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) left, (int) top, (int) right, (int) bottom), mPaint);

    }


    private float getY(int y) {
        return (float) y * mHeight / HEIGHT;
    }

    private float getX(int x) {
        return mWidth - (float) x * mWidth / WIDTH;
    }

    public int[] getLocFace() {
        return mLocFace;
    }

    public void setLocFace(int[] locFace) {
        mLocFace = locFace;
        postInvalidate();
    }
}
