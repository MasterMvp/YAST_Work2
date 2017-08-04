package com.YinanSoft.phoneface.ui.view;

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
public class FrameFaceView extends View implements CameraPreview {

    private static final String TAG = FrameFaceView.class.getSimpleName();

    private int previewWidth = 640;
    private int prevHeight = 480;

    private Paint mPaint;

    private int[] mLocFace;

    private int mWidth = 0;

    private int mHeight = 0;

    private float mDx;

    private float mDy;

    private Bitmap mBitmap;

    private int mBitmapWidth;

    private int mBitmapHeight;
    private String result = "";

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

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mTempWidth = getMeasuredWidth();
        int mTempHeight = getMeasuredHeight();

        float radio = (float) mTempWidth / mTempHeight;

        if (radio < (float) previewWidth / prevHeight) {
            mHeight = getMeasuredHeight();
            mWidth = mHeight * previewWidth / prevHeight;
            mDx = (float) (mWidth - mTempWidth) / 2;
        } else {
            mWidth = getMeasuredWidth();
            mHeight = mWidth * prevHeight / previewWidth;

            mDy = (float) (mHeight - mTempHeight) / 2;
        }

        Logs.i(TAG, "width:" + mWidth + " height:" + mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int nTextSize = 30;
        float left = 0, top = 0, right = 0, bottom = 0;

        if (mLocFace == null) return;

        if (result.equals("clear")) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setTextSize(nTextSize);
            p.setColor(Color.RED);// 设置红色
            canvas.drawText("", 10, 50, p);// 画文本
        } else if (result != "") {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setTextSize(nTextSize);
            String[] tmp = result.split(":");
            if (tmp != null && Integer.parseInt(tmp[1]) < 90) {
                p.setColor(Color.RED);// 设置红色
            } else {
                p.setColor(Color.GREEN);// 设置红色
            }
            canvas.drawText(result, 10, 50, p);// 画文本
        }

//    if (mLocFace == null) return;
//    float right  = getX(mLocFace[0]) - mDx;
//    float top    = getY(mLocFace[1]) - mDy;
//    float left   = right - (float) mLocFace[2] * mWidth  / previewWidth;
//    float bottom = top   + (float) mLocFace[3] * mHeight / prevHeight;

        left = getX(mLocFace[0]) - mDx;
        top = getY(mLocFace[1]) - mDy;
        right = left + (float) mLocFace[2] * mWidth / previewWidth;
        bottom = top + (float) mLocFace[3] * mHeight / prevHeight;
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            left = getX(mLocFace[0]) - mDx;// + 65;
            top = getY(mLocFace[1]) - mDy;// + 50;
            right = left + (float) mLocFace[2] * mWidth / previewWidth;// + 20;
            bottom = top + (float) mLocFace[3] * mHeight / prevHeight;
        }

//        float width = right - left;
//        float height = bottom - top;

//        Logs.i(TAG, "真实x:" + mLocFace[0]);
//        Logs.i(TAG, "真实width:" + mLocFace[2]);
//        Logs.i(TAG, "mDx:" + mDx);
//        Logs.i(TAG, "mDY:" + mDy);
//        Logs.i(TAG, "left:" + left);
//        Logs.i(TAG, "right:" + right);
//        Logs.i(TAG, "top:" + top);
//        Logs.i(TAG, "bottom:" + bottom);
//    canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) (left-width), (int)( top - height / 2), (int) (right), (int) (bottom + height/2)), mPaint);
//    canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) (left-width/2), (int)( top - height / 2), (int) (right + width/2), (int) (bottom + height/2)), mPaint);
        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmapWidth, mBitmapHeight), new Rect((int) left, (int) top, (int) right, (int) bottom), mPaint);
    }

    private float getY(int y) {
        return (float) y * mHeight / prevHeight;
    }

    private float getX(int x) {
//    return mWidth - (float) x * mWidth / previewWidth;
        return (float) x * mWidth / previewWidth;
    }

    public int[] getLocFace() {
        return mLocFace;
    }

    public void setLocFace(int[] locFace) {
        mLocFace = locFace;
        postInvalidate();
    }

    public void setresult(String resultM) {
        result = resultM;
        //postInvalidate();
    }

    @Override
    public void onPreviewSize(int width, int height) {

        previewWidth = width;
        prevHeight = height;

        Logs.v("previewWidth=" + previewWidth + ", prevHeight=" + prevHeight);
        requestLayout();
    }
}
