package risks.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.YinanSoft.phoneface.FaceSDK;
import com.YinanSoft.phoneface.common.Logs;
import com.YinanSoft.phoneface.common.Stfaceattr;
import com.YinanSoft.phoneface.model.eyekey.CheckAction;
import com.YinanSoft.phoneface.model.result.Result;
import com.YinanSoft.phoneface.ui.camera.CameraFaceConfig;
import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;
import com.YinanSoft.phoneface.ui.view.FrameFaceView;

import java.util.HashMap;
import java.util.Map;

import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;


public class FaceTestActivity extends Activity implements CameraFaceCallback {

    private static final String TAG = FaceTestActivity.class.getSimpleName();

    public static final String ARG_FEATURES = FaceTestActivity.class.getSimpleName() + ".feature";
    public static final String ARG_SCORE = FaceTestActivity.class.getSimpleName() + ".score";
    public static final String ARG_BITMAP = FaceTestActivity.class.getSimpleName() + ".bitmap";

    private CameraSurfaceView mSurfaceView;

    private FrameFaceView mFrameFaceView;

    boolean isMatching = false;

    private static MyApplication mApp;

    private String mSrcFeatures;
    private long timeout = 10 * 1000;//默认10s
    private int cameId = 1;
    private int compareScore = 55;

    private Map<Integer, Bitmap> map;
    private Rect rect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_face_test);
        mApp = (MyApplication) getApplication();
//        mSrcFeatures = getIntent().getStringExtra(ARG_FEATURES);
        mSrcFeatures = mApp.faceFeatures;
        map = new HashMap<>();
        initConfig();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSurfaceView.startCapture();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        mSurfaceView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        A606AReader.PowerOffFlash606A();
        super.onDestroy();
    }

    private void initConfig() {
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurface);
        mFrameFaceView = (FrameFaceView) findViewById(R.id.finderV);
        CameraFaceConfig config = new CameraFaceConfig.Builder()
                .setCameraId(cameId)
                .setDistanceEyesMin(0)
                .setDistanceEyesMax(300)
                .setIsCheckLive(false)
                .setTime(true)
                .setTimeoutS(timeout)
                .build();
        mSurfaceView.setFaceConfig(config);
        mSurfaceView.setFaceCallback(this);
    }


    @Override
    public void onFaceBefore() {

    }

    @Override
    public void onFacing(int state) {

    }

    @Override
    public void onDecodeSuc(Result obj) {
        mSurfaceView.startPreviewDelay(0);
        Stfaceattr faceAttr = obj.getStfaceattr();
        int[] locFace = faceAttr.getLocFace();
        rect = new Rect(locFace[0], locFace[1], locFace[2], locFace[3]);
        Log.e(TAG, "人脸坐标： " + locFace[0] + " " + locFace[1] + " " + locFace[2] + " " + locFace[3]);
        mFrameFaceView.setLocFace(locFace);
    }

    @Override
    public void onDecodeError(Result obj) {
        mFrameFaceView.setLocFace(null);
        mSurfaceView.startPreviewDelay(0);
    }

    @Override
    public void onCheckingNoFace() {

    }

    @Override
    public void onCheckSuc(CheckAction action) {

    }

    @Override
    public void onFaceAfter() {

    }

    @Override
    public void onFaceTimeOut() {
        Intent intent = getIntent();
        intent.putExtra(ARG_SCORE, -1);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResult(Result result, Bitmap bitmap) {
        //动态对比
        if (isMatching) {
            bitmap.recycle();
            return;
        }
        isMatching = true;
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
        matchBitmap(scaleBitmap);
    }


    int score = 0;

    private void matchBitmap(final Bitmap bitmap) {
        if (mSrcFeatures == null) {
            isMatching = false;
            return;
        }

        final long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                score = FaceSDK.match(FaceSDK.getFeature(bitmap), mSrcFeatures);
                mSurfaceView.post(new Runnable() {
                    @Override
                    public void run() {
                        Logs.i(TAG, "提特征+比对：" + (System.currentTimeMillis() - start) + "ms"); // 新so 2958ms
                        dealRes(bitmap, score);
                    }
                });

            }
        }).start();
    }

    private void dealRes(Bitmap bitmap, int score) {
//        ToastUtil.showToast(FaceTestActivity.this, "比对分数：" + score);
        Logs.i(TAG, "比对分数：" + score);

        if (score >= compareScore) {

            Logs.i(TAG, "finish...");
            mApp.setFaceBitamp(bitmap);

            Intent intent = getIntent();
            intent.putExtra(ARG_SCORE, score);
            setResult(RESULT_OK, intent);
            finish();
        } else {
//            mFrameFaceView.setLocFace(null);
//            mSurfaceView.onPause();
//            mSurfaceView.onResume();
            //识别失败存储照片
            map.put(score, bitmap);
            Log.e(TAG, map.size() + "");
            //动态识别比对3次不成功退出
            if (map.size() == 3) {
                Log.e(TAG, "超时： " + map.size());
                compareFailure(map);
                return;
            }
            mSurfaceView.startCapture();
            isMatching = false;
        }
        Logs.i(TAG, "next...");
    }

    // 人脸比对失败
    private void compareFailure(Map<Integer, Bitmap> map) {
        int initValue = 0;
        // 遍历map集合取最高分数的键值
        for (Map.Entry<Integer, Bitmap> entry : map.entrySet()) {
            if (initValue < entry.getKey()) {
                initValue = entry.getKey();
                mApp.setFaceBitamp(entry.getValue());
            }
            System.out.println("key= " + entry.getKey());
        }
        // -1 超时，initValue 比对失败分数
        Intent intent = getIntent();
        intent.putExtra(ARG_SCORE, initValue);
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * 获取人脸切图
     *
     * @param bmp  原始图片
     * @param rect 人脸坐标
     * @return 人脸图像
     */
    private Bitmap cutFaceImage(Bitmap bmp, Rect rect) {
        if (bmp == null) {
            return null;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int mWidth = rect.width();
        int mHight = rect.height();
        int mw = mWidth / 4;
        int mh = mHight / 4;
        int x = rect.left - mw;
        if (x < 0) {
            x = 0;
        }

        int y = rect.top - mh;
        if (y < 0) {
            y = 0;
        }
        mWidth = mWidth * 3 / 2;
        if (mWidth + x > width) {
            mWidth = width - x;
        }
        mHight = mHight * 3 / 2;
        if (mHight + y > height) {
            mHight = height - y;
        }
        x = rect.left;
        y = rect.top;
        mWidth = rect.right;
        mHight = rect.bottom;
//        (left-width/2), (int)( top - height / 2), (int) (right + width/2), (int) (bottom + height/2))
        Log.e("createBitmap", "x:" + x + " y:" + y + " mWidth:" + mWidth + " mHight:" + mHight);
        if ((x - mWidth / 2) < 0) {
            x = 0;
        } else x = x - mWidth / 2;
        if ((y - mHight / 2) < 0) {
            y = 0;
        } else y = y - mHight / 2;
        if ((x + mWidth + mWidth) > width) {
            mWidth = width - x;
        } else {
            mWidth = mWidth + mWidth;
        }
        if ((y + mHight + mHight) > height) {
            mHight = height - y;
        } else {
            mHight = mHight + mHight;
        }
//        Bitmap tmp = Bitmap.createBitmap(bmp, x, y, mWidth+mWidth, mHight+mHight);
        Bitmap tmp = Bitmap.createBitmap(bmp, x, y, mWidth, mHight);
        return tmp;
    }


    @Override
    public void onTake() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
