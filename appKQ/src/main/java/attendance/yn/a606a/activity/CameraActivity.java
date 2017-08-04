package attendance.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.YinanSoft.Utils.ToastUtil;

import java.util.Timer;

import attendance.yn.a606a.R;
import attendance.yn.a606a.utils.CameraManager;
import attendance.yn.a606a.utils.ToastUtils;


public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    public static Bitmap bit;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView bt_a;
    private CameraManager ocrCamera;
    private byte[] A_data = null;
    private final int A_TYPE = 100;
    private Timer timer;
    private ImageView flashLight;//闪光灯
    private boolean isOpenFlashLight = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_camera);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮

        ocrCamera = new CameraManager(CameraActivity.this, mHandler, 1);
        initViews();
//        setTimeOut();
    }



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == A_TYPE) {
                ocrCamera.closeCamera();
                byte[] img_data = msg.getData().getByteArray("img_data");
                Bitmap bitmapOrg = BitmapFactory.decodeByteArray(img_data, 0, img_data.length);
                Bitmap bitmap = Bitmap.createScaledBitmap(bitmapOrg, 640, 480, true);
                HandlerCheckAct.setOCRBitmapAcross(null);
                HandlerCheckAct.setOCRBitmapAcross(bitmap); //拍照bitmap

                Intent intent = new Intent();
//                intent.putExtra("A_data", "1");
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(CameraActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
            }
//			ocrCamera.startDisplay();
        }

    };

    private void initViews() {
        flashLight = (ImageView) findViewById(R.id.flash_light);
        bt_a = (ImageView) findViewById(R.id.bt_a);
        bt_a.setOnClickListener(listener);
        flashLight.setOnClickListener(listener);
        surfaceView = (SurfaceView) findViewById(R.id.preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(CameraActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 606 自带补光灯，屏蔽掉所有闪光灯
//        if(cameraId == 1) flashLight.setVisibility(View.GONE);//所有前置摄像头都不带闪光灯
        if (Build.MODEL.toUpperCase().equals("JWZD-606")) {
            flashLight.setVisibility(View.GONE);
        }// 606自带补光灯，闪光灯都需屏蔽
        if (Build.MODEL.toUpperCase().equals("R310")) {
            flashLight.setVisibility(View.VISIBLE);
        }// 300机器只有后置摄像头

    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_a:
                    ocrCamera.autoFouce();//自动对焦
                    ocrCamera.takePicture();// .autoFocusAndTakePic(A_TYPE);
                    break;
                case R.id.flash_light:
                    if (!ocrCamera.isSupportFlash()) {
                        ToastUtils.showToast(CameraActivity.this, "相机不支持闪光灯...");
                        break;
                    }
                    if (!isOpenFlashLight) {
                        ToastUtil.showToast(CameraActivity.this, "打开闪关灯");
                        ocrCamera.setCameraFlashMode(Parameters.FLASH_MODE_TORCH);
                        flashLight.setImageResource(R.mipmap.light_off);
                        isOpenFlashLight = true;
                    } else {
                        ToastUtil.showToast(CameraActivity.this, "关闭闪关灯");
                        ocrCamera.setCameraFlashMode(Parameters.FLASH_MODE_OFF);
                        flashLight.setImageResource(R.mipmap.light_on);
                        isOpenFlashLight = false;
                    }
                    break;
            }
        }

    };

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            ocrCamera.openCamera(holder);
            if (ocrCamera.isSupportFlash(Parameters.FLASH_MODE_AUTO)) {
//                ocrCamera.setCameraFlashMode(Parameters.FLASH_MODE_AUTO);
            } else if (ocrCamera.isSupportFlash(Parameters.FLASH_MODE_OFF)) {
                ocrCamera.setCameraFlashMode(Parameters.FLASH_MODE_OFF);
            }

        } catch (Exception e) {
            Toast.makeText(CameraActivity.this, "相机未正常启动", Toast.LENGTH_SHORT).show();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width > height) {
            ocrCamera.setPreviewSize(width, height);
        } else {
            ocrCamera.setPreviewSize(height, width);
        }
        ocrCamera.startDisplay();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        ocrCamera.closeCamera();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        A_data = null;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ocrCamera.closeCamera();
        ocrCamera = null;
        super.onDestroy();
    }

}