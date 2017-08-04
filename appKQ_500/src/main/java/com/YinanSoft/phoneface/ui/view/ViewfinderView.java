/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.YinanSoft.phoneface.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.YinanSoft.phoneface.Constants;
import com.YinanSoft.phoneface.decode.IConstants;
import com.YinanSoft.phoneface.ui.camera.CameraManager;
import com.YinanSoft.phoneface.ui.fragment.BaseCameraHandler;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View implements IConstants {
    private static final String TAG = ViewfinderView.class.getSimpleName();
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
            128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int laserColor;
    private int scannerAlpha;
    //private Bitmap mFinishGlow;
    //private Bitmap mWhiteDot;
    //private Bitmap mFaceContour;
    private int faceRedraw;
    private RectF faceRect;
    private final boolean isPortrait;
    private String liveString;
    private Rect mDestination = new Rect();
    private float[][] POINTS;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs,
                          boolean isLandscape) {

        super(context, attrs);
        initFaceShape();
        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.isPortrait = isLandscape;
        maskColor = 1610612736;
        laserColor = -3407872;
        this.getResources();
//		try {
//			this.mFinishGlow = BitmapFactory.decodeStream(context.getAssets()
//					.open("ic_facial_dot_glow_correct.png"));
//			this.mWhiteDot = BitmapFactory.decodeStream(context.getAssets()
//					.open("ic_facial_dot.png"));

//			this.mFaceContour= BitmapFactory.decodeStream(context.getAssets().open("f1080x1920.png"));

//		} catch (IOException e) {
//			Log.i(VIEW_LOG_TAG, "请将若干资源文件放置在asset文件中");
//			e.printStackTrace();
//		}
        scannerAlpha = 0;
    }

    private void initFaceShape() {
        float[][] faceShape = new float[31][];
        faceShape[0] = new float[]{0.0f, (-1.0f)};
        faceShape[1] = new float[]{0.16f, (-0.98f)};
        faceShape[2] = new float[]{0.34f, (-0.92f)};
        faceShape[3] = new float[]{0.5f, (-0.82f)};
        faceShape[4] = new float[]{0.64f, (-0.7f)};
        faceShape[5] = new float[]{0.74f, (-0.54f)};
        faceShape[6] = new float[]{0.78f, (-0.36f)};
        faceShape[7] = new float[]{0.8f, (-0.18f)};
        faceShape[8] = new float[]{0.78f, 0.0f};
        faceShape[9] = new float[]{0.75f, 0.2f};
        faceShape[10] = new float[]{0.7f, 0.37f};
        faceShape[11] = new float[]{0.62f, 0.54f};
        faceShape[12] = new float[]{0.52f, 0.71f};
        faceShape[13] = new float[]{0.4f, 0.83f};
        faceShape[14] = new float[]{0.26f, 0.94f};
        faceShape[15] = new float[]{0.08f, 1.0f};
        faceShape[16] = new float[]{(-0.08f), 1.0f};
        faceShape[17] = new float[]{(-0.26f), 0.94f};
        faceShape[18] = new float[]{(-0.4f), 0.83f};
        faceShape[19] = new float[]{(-0.52f), 0.71f};
        faceShape[20] = new float[]{(-0.62f), 0.54f};
        faceShape[21] = new float[]{(-0.7f), 0.37f};
        faceShape[22] = new float[]{(-0.75f), 0.2f};
        faceShape[23] = new float[]{(-0.78f), 0.0f};
        faceShape[24] = new float[]{(-0.8f), (-0.18f)};
        faceShape[25] = new float[]{(-0.78f), (-0.36f)};
        faceShape[26] = new float[]{(-0.74f), (-0.54f)};
        faceShape[27] = new float[]{(-0.64f), (-0.7f)};
        faceShape[28] = new float[]{(-0.5f), (-0.82f)};
        faceShape[29] = new float[]{(-0.34f), (-0.92f)};
        faceShape[30] = new float[]{(-0.16f), (-0.98f)};
        POINTS = faceShape;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        RectF frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }
        canvas.getWidth();
        canvas.getHeight();
        paint.setColor(maskColor);
        paint.setAlpha(255);
        float mSize = 1.0F;
        float f = 1.0F * mSize;
        int i = (int) (getWidth() * (1.0F - f) / 2.0F);
        int j = (int) (getHeight() * (1.0F - f) / 2.0F);
        this.mDestination.set(i, j, (int) (i + f * getWidth()), (int) (j + f
                * getHeight()));
        if (faceRedraw == 2) {
            //canvas.drawBitmap( this.mFaceContour, 0, 0, this.paint);
            if (gitListenser != null) {
                Log.d(TAG, "onDraw...");
                gitListenser.onState(this.state);
            }
        }
        if (resultBitmap != null) {
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, mDestination, paint);
        } else {
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int p = 0;
            while (p < POINTS.length) {
                float cx, cy;
                if (isPortrait) {
                    cx = ((POINTS[p][0] * ((float) (this.getWidth()))) * 0.37f)
                            + ((float) ((this.getWidth() / 2)));
                    cy = ((POINTS[p][1] * ((float) (this.getWidth()))) * 0.37f)
                            + ((float) ((this.getHeight() / 2)));
                } else {
                    cx = ((POINTS[p][0] * ((float) (this.getHeight()))) * 0.37f)
                            + ((float) ((this.getWidth() / 2)));
                    cy = ((POINTS[p][1] * ((float) (this.getHeight()))) * 0.37f)
                            + ((float) ((this.getHeight() / 2.2)));

                }
                switch (faceRedraw) {
                    case 0:
                        //canvas.drawBitmap( this.mFaceContour, 0, 0, this.paint);
                        break;
                    case 1:
                        //canvas.drawBitmap( this.mFaceContour, 0, 0, this.paint);
                        if (gitListenser != null) {
                            gitListenser.onState(this.state);
                        }
                        break;

                }
                p++;
            }
            Paint paintF = new Paint();
            paintF.setColor(Color.GREEN);
            paintF.setStrokeWidth(10);
            paintF.setTextSize(60);
            paintF.setTextAlign(Align.CENTER);

            // 写字
            if ((!("".equals(liveString))) && (liveString != null)) {
//				canvas.drawText(liveString, canvas.getWidth() / 2,
//						canvas.getHeight() / 4, paintF);
            }


            if (Constants.isShowFrame) {
                if (faceRect != null) {
                    new RectF(faceRect.left, faceRect.top, faceRect.right,
                            faceRect.bottom);
                    canvas.drawRect(faceRect, paintF);
                }
                RectF previewFrame = cameraManager.getFramingRect();
                canvas.drawRect(previewFrame, paintF);
            }

            postInvalidateDelayed(ANIMATION_DELAY, (int) frame.left
                            - POINT_SIZE, (int) frame.top - POINT_SIZE,
                    (int) frame.right + POINT_SIZE, (int) frame.bottom
                            + POINT_SIZE);
        }
    }

    public GitImageListenser gitListenser;

    public interface GitImageListenser {
        void onState(int state);
    }

    public void setGitImageListener(GitImageListenser voiceListenser) {
        this.gitListenser = voiceListenser;
    }

    TipImageCallBack tipImageCallBack;

    public interface TipImageCallBack {
        void onImageTip(int imageid);
    }

    public void setOnImageTip(TipImageCallBack tipImageCallBack) {
        this.tipImageCallBack = tipImageCallBack;
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    private int state = -1;
    BaseCameraHandler baseCameraHandler;

    public void addPossibleResultPoint(int state, int faceRedraw, RectF faceRect,
                                       String liveString, BaseCameraHandler baseCameraHandler) {

        this.state = state;
        this.faceRedraw = faceRedraw;
        this.faceRect = faceRect;
        this.liveString = liveString;
        this.baseCameraHandler = baseCameraHandler;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "获取显示区域参数");
        int desiredWidth = 480;
        int desiredHeight = 640;
        /**
         * 每个MeasureSpec均包含两种数据，尺寸和设定类型，需要通过 MeasureSpec.getMode和getSize进行提取
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 参考值竖屏 800 1214
        // 参考值横屏 1280 734
        int width;
        int height;
        LayoutParams params;// 子布局的参数
        // 竖屏
        if (isPortrait) {
            // 测量宽度
            if (widthMode == MeasureSpec.EXACTLY) {
                // 精确值情况
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                // 范围值情况，哪个小取哪个？
                width = Math.min(desiredWidth, widthSize);
            } else {
                // 没设定就是默认的了，呵呵
                width = desiredWidth;
            }
            // 高度设定同上
            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                // height = Math.min(desiredHeight, heightSize);
                height = Math.min(desiredWidth, widthSize) * 4 / 3;
            } else {
                height = desiredHeight;
            }
            Log.i(TAG, "显示表明摄像头为竖屏");
            // 竖屏，宽度匹配，高度为宽度缩放
            // params = new LayoutParams((int) (width), (int) (height));

        } else {
            desiredWidth = 640;
            desiredHeight = 480;
            // 测量宽度
            if (widthMode == MeasureSpec.EXACTLY) {
                // 精确值情况
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                // 范围值情况，哪个小取哪个？
                // width = Math.min(desiredWidth, widthSize);
                width = Math.min(desiredHeight, heightSize) * 4 / 3;
            } else {
                // 没设定就是默认的了，呵呵
                width = desiredWidth;
            }

            // 高度设定同上
            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desiredHeight, heightSize);
            } else {
                height = desiredHeight;
            }
            // 横屏，高度匹配，宽度包裹
        }

        setMeasuredDimension(width, height);
        Log.e(TAG, "VFV设定宽度:" + widthSize + "  设定高度:" + heightSize);// 让我们来输出他们
        Log.e(TAG, "VFV实际宽度:" + width + "  实际高度:" + height);// 让我们来输出他们
    }

}
