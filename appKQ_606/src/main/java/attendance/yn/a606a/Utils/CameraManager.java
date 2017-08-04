package attendance.yn.a606a.Utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CameraManager implements Camera.AutoFocusCallback {
    public static final int mWidth = 1200;
    public static final int mHeight = 1600;
    private Camera mCamera;
    private Handler mHandler;
    private int takeType = 100;
    private Context context;
    private int cameraId;
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };
    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
        }
    };
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            try {
                String e = HelpManager.newImageName();
                Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                Message msg = new Message();
                msg.what = CameraManager.this.takeType;
                msg.obj = e;
                Bundle bundle = new Bundle();
                bundle.putByteArray("img_data", bos.toByteArray());
                msg.setData(bundle);
                CameraManager.this.mHandler.sendMessage(msg);
                bos.flush();
                bos.close();
                bm.recycle();
            } catch (NullPointerException var8) {
                CameraManager.this.mHandler.sendEmptyMessage(0);
            } catch (FileNotFoundException var9) {
                CameraManager.this.mHandler.sendEmptyMessage(0);
            } catch (IOException var10) {
                CameraManager.this.mHandler.sendEmptyMessage(0);
            }

        }
    };

    public CameraManager(Context context, Handler handler , int cameraId) {
        this.mHandler = handler;
        this.context = context;
        this.cameraId = cameraId;
    }

    public void openCamera(SurfaceHolder holder) throws IOException {

        if(this.mCamera == null) {
            try {
                this.mCamera = Camera.open(cameraId);
            }catch (RuntimeException e){
                //有些设备没有前置摄像头，此处调用后置摄像头,反之同样处理
                Log.e("RuntimeException","cameraId:"+cameraId);
                if(cameraId == 0) cameraId = 1;
                else cameraId = 0;
                this.mCamera = Camera.open(cameraId);
            }
            this.mCamera.setPreviewDisplay(holder);
            this.setPictureSize();
            setCameraDisplayOrientation((Activity) context,cameraId,mCamera);
            //Log.e("999", mCamera.getParameters().getSupportedWhiteBalance().toString());
            //mCamera.getParameters().setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
        }

    }

    public void startDisplay() {
        if(this.mCamera != null) {
            this.mCamera.startPreview();
        }

    }

    public void closeCamera() {
        if(this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }

    }

    public void autoFocusAndTakePic(int type) {
        if(this.mCamera != null) {
            this.takeType = type;
            this.mCamera.autoFocus(this);
        }

    }

    public void autoFouce() {
        if(this.mCamera != null) {
            this.mCamera.autoFocus(this.autoFocusCallback);
        }

    }

    public void takePicture() {
        if(this.mCamera != null) {
            try {
                Camera.Parameters parameters = this.mCamera.getParameters();
                parameters.setJpegQuality(100);
                this.mCamera.setParameters(parameters);
                this.mCamera.takePicture(this.shutterCallback, this.rawCallback, this.jpegCallback);
            } catch (RuntimeException var2) {
                ;
            }
        }

    }

    public void setPreviewSize(int width, int height) {
        if(this.mCamera != null) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            List previewSize = parameters.getSupportedPreviewSizes();
            Collections.sort(previewSize, new SizeComparator());
            if(previewSize != null) {
                int size = previewSize.size();
                int picIndex = 0;

                for(int i = 0; i < size; ++i) {
                    if(((Camera.Size)previewSize.get(i)).width == width) {
                        picIndex = i;
                        break;
                    }

                    if(((Camera.Size)previewSize.get(i)).width < width) {
                        picIndex = i - 1;
                        if(picIndex < 0) {
                            picIndex = 0;
                        }

                        int diffW1 = ((Camera.Size)previewSize.get(picIndex)).width - width;
                        int diffW2 = width - ((Camera.Size)previewSize.get(i)).width;
                        if(diffW1 > diffW2) {
                            picIndex = i;
                        }
                        break;
                    }
                }

                if(Build.MODEL.startsWith("MI-ONE")) {
                    if(Build.VERSION.INCREMENTAL.equals("2.10.12")) {
                        parameters.setPreviewSize(640, 480);
                    } else {
                        parameters.setPreviewSize(1280, 720);
                    }
                } else {
                    parameters.setPreviewSize(((Camera.Size)previewSize.get(picIndex)).width, ((Camera.Size)previewSize.get(picIndex)).height);
//                    parameters.setPreviewSize(1280, 720);
                }
            }

            this.mCamera.setParameters(parameters);
        }

    }

    private void setPictureSize() {
        Camera.Parameters parameters = this.mCamera.getParameters();
        parameters.setPictureFormat(256);
        List pictureSize = parameters.getSupportedPictureSizes();
        Collections.sort(pictureSize, new SizeComparator());
        if(pictureSize != null) {
            int size = pictureSize.size();
            int picIndex = 0;

            for(int i = 0; i < size; ++i) {
                if(((Camera.Size)pictureSize.get(i)).width == 1600) {
                    picIndex = i;
                    break;
                }

                if(((Camera.Size)pictureSize.get(i)).width < 1600) {
                    picIndex = i - 1;
                    if(picIndex < 0) {
                        picIndex = 0;
                    }

                    int diffW1 = ((Camera.Size)pictureSize.get(picIndex)).width - 1600;
                    int diffW2 = 1600 - ((Camera.Size)pictureSize.get(i)).width;
                    if(diffW1 > diffW2 && ((Camera.Size)pictureSize.get(i)).width > 1280) {
                        picIndex = i;
                    }
                    break;
                }
            }

            if(!Build.MODEL.startsWith("HTC 606w") && !Build.MODEL.startsWith("HTC 608t")) {
                parameters.setPictureSize(((Camera.Size)pictureSize.get(picIndex)).width, ((Camera.Size)pictureSize.get(picIndex)).height);
            } else {
                parameters.setPictureSize(1600, 1200);
            }
        }

        this.mCamera.setParameters(parameters);
    }

    public void setCameraFlashMode(String mode) {
        Camera.Parameters parameters = this.mCamera.getParameters();
        parameters.setFlashMode(mode);
        this.mCamera.setParameters(parameters);
    }

    public boolean isSupportFlash(String mode) {
        List modes = this.mCamera.getParameters().getSupportedFlashModes();
        return this.mCamera != null && modes != null && modes.contains(mode);
    }

    public boolean isSupportFlash(){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public boolean isSupportAutoFocus() {
        List list = this.getSupportedFocusModes();
        if(list == null) {
            return false;
        } else {
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                String string = (String)var3.next();
                if("auto".equals(string)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isSupportFocus(String mode) {
        List list = this.getSupportedFocusModes();
        if(list == null) {
            return false;
        } else {
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                String string = (String)var4.next();
                if(mode.equals(string)) {
                    return true;
                }
            }

            return false;
        }
    }

    private List<String> getSupportedFocusModes() {
        List list = null;
        if(this.mCamera != null) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            list = parameters.getSupportedFocusModes();
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                String string = (String)var4.next();
                Log.d("path", "------SupportedFocusModes----------->>" + string);
            }
        }

        return list;
    }

    public String getDefaultFlashMode() {
        return this.mCamera.getParameters().getSupportedFlashModes() != null?(String)this.mCamera.getParameters().getSupportedFlashModes().get(0):"off";
    }




    public void onAutoFocus(boolean success, Camera camera) {
        this.takePicture(success);
    }

    private void takePicture(boolean captureOnly) throws RuntimeException {
        if(this.mCamera != null) {
            try {
                Camera.Parameters parameters = this.mCamera.getParameters();
                parameters.setJpegQuality(100);
                this.mCamera.setParameters(parameters);
                this.mCamera.takePicture(this.shutterCallback, this.rawCallback, this.jpegCallback);
            } catch (RuntimeException var3) {
                ;
            }
        }

    }

    public class SizeComparator implements Comparator<Camera.Size> {
        public SizeComparator() {
        }

        public int compare(Camera.Size s1, Camera.Size s2) {
            return s2.width * s2.height - s1.width * s1.height;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}

