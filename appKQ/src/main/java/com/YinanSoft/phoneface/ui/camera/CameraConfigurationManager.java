/*
 
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

package com.YinanSoft.phoneface.ui.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.YinanSoft.phoneface.Constants;
import com.YinanSoft.phoneface.ui.Preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to
 * configure the camera hardware.
 */
final class CameraConfigurationManager {

  private static final String TAG = "CameraConfiguration";

  // This is bigger than the size of a small screen, which is still supported. The routine
  // below will still select the default (presumably 320x240) size for these. This prevents
  // accidental selection of very low resolution on some devices.
  private static final int MIN_PREVIEW_PIXELS = 640 * 480; // 最小摄像头尺寸
  private static final int MAX_PREVIEW_PIXELS = 640 * 480; // 最大摄像头尺寸

  private final Context context;
  private final View view;
  private Point screenResolution;
  private Point cameraResolution;
  private int rotation = 90;
  private int mPreviewWidth;
  private int mPreviewHeight;

  public int getRotation() {
    return rotation;
  }

  CameraConfigurationManager(Context context, View view) {
    this.context = context;
    this.view = view;
  }

  public void setPreviewSize(int previewWidth, int previewHeight) {
    this.mPreviewWidth = previewWidth;
    this.mPreviewHeight = previewHeight;
  }

  /**
   * Reads, one time, values from the camera that are needed by the app.
   * 从设备一次读取摄像头所需要的数据
   */
  void initFromCameraParameters(Camera camera) {
    Camera.Parameters parameters = camera.getParameters();
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    if (!Constants.isDebug) {
      /**
       * 获取当前屏幕旋转角度
       *
       * @return 90表示是竖屏; 0表示是左横屏;  270表示是右横屏; 180表示是反向竖屏
       */
      switch (manager.getDefaultDisplay().getRotation()) {
        case Surface.ROTATION_0:
          rotation = 90;
          break;
        case Surface.ROTATION_90:
          rotation = 0;
          break;
        case Surface.ROTATION_180:
          rotation = 270;
          break;
        case Surface.ROTATION_270:
          rotation = 180;
          break;
        default:
          rotation = 90;
          break;
      }
    } else {
      switch (manager.getDefaultDisplay().getRotation()) {
        case Surface.ROTATION_0:
          rotation = 180;
          break;
        case Surface.ROTATION_90:
          rotation = 180;
          break;
        case Surface.ROTATION_180:
          rotation = 180;
          break;
        case Surface.ROTATION_270:
          rotation = 180;
          break;
      }
    }


    Display display = manager.getDefaultDisplay();

    int width = display.getWidth();
    int height = display.getHeight();

    // We're landscape-only, and have apparently seen issues with display thinking it's portrait
    // when waking from sleep. If it's not landscape, assume it's mistaken and reverse them:
    
/*   if (width < height) {
      Log.i(TAG, "显示表明摄像头为竖屏，修改为横屏");
      int temp = width;
      width = height;
      height = temp;
    }*/


    screenResolution = new Point(width, height);
    Log.i(TAG, "屏幕分辩率: " + screenResolution);
    //不同摄像头要修改
    //larry modify 20170407 改回查找方式
    cameraResolution = new Point(640, 480);// findBestPreviewSizeValue(parameters, screenResolution);//    cameraResolution = new Point(480, 640);
    Log.i(TAG, "摄像头分辨率: " + cameraResolution);
  }

  void setDesiredCameraParameters(Camera camera, boolean safeMode) {
    Camera.Parameters parameters = camera.getParameters();

    if (parameters == null) {
      Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
      return;
    }

    Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

    if (safeMode) {
      Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    initializeTorch(parameters, prefs, safeMode);

    String focusMode = null;
    if (prefs.getBoolean(Preferences.KEY_AUTO_FOCUS, true)) {
      if (safeMode || prefs.getBoolean(Preferences.KEY_DISABLE_CONTINUOUS_FOCUS, false)) {
        focusMode = findSettableValue(parameters.getSupportedFocusModes(),
            Camera.Parameters.FOCUS_MODE_AUTO);
      } else {
        focusMode = findSettableValue(parameters.getSupportedFocusModes(),
            "continuous-picture", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE in 4.0+
            "continuous-video",   // Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO in 4.0+
            Camera.Parameters.FOCUS_MODE_AUTO);
      }
    }
    // Maybe selected auto-focus but not available, so fall through here:
    if (!safeMode && focusMode == null) {
      focusMode = findSettableValue(parameters.getSupportedFocusModes(),
          Camera.Parameters.FOCUS_MODE_MACRO,
          "edof"); // Camera.Parameters.FOCUS_MODE_EDOF in 2.2+
    }
    if (focusMode != null) {
      parameters.setFocusMode(focusMode);
    }

    if (prefs.getBoolean(Preferences.KEY_INVERT_SCAN, false)) {
      String colorMode = findSettableValue(parameters.getSupportedColorEffects(),
          Camera.Parameters.EFFECT_NEGATIVE);
      if (colorMode != null) {
        parameters.setColorEffect(colorMode);
      }
    }

    parameters.setPreviewSize(mPreviewWidth == 0 ? cameraResolution.x : mPreviewWidth, mPreviewHeight == 0 ? cameraResolution.y : mPreviewHeight);
//    parameters.setPreviewSize(640,480);
//    parameters.setPictureSize(640,480);
    /*if(Constants.isDebug){
        setOrientation(camera,rotation+90, parameters);//模拟器测试
    }else{
    }*/
//        setOrientation(camera, rotation, parameters);//正常情况
    camera.setParameters(parameters);
  }

  private void setOrientation(Camera camera, int orientation, Camera.Parameters parameters) {
    // 90表示是竖屏; 180表示是左横屏; 0表示是右横屏; 180表示是反向竖屏

    switch (orientation) {
      case 90:
        if (Build.VERSION.SDK_INT == 7) {
          parameters.set("orientation", "portrait");
          parameters.setRotation(orientation);
        } else {
          camera.setDisplayOrientation(orientation);
        }
        return;
      case 180:
        if (Build.VERSION.SDK_INT == 7) {
          parameters.set("orientation", "landscape");
          parameters.setRotation(orientation);
        } else {
          camera.setDisplayOrientation(orientation);
        }
        return;
      case 0:
        if (Build.VERSION.SDK_INT == 7) {
          parameters.set("orientation", "landscape");
          parameters.setRotation(orientation);
        } else {
          camera.setDisplayOrientation(orientation);
        }
        return;
      case 270:
        if (Build.VERSION.SDK_INT == 7) {
          parameters.set("orientation", "portrait");
          parameters.setRotation(orientation);
        } else {
          camera.setDisplayOrientation(orientation);
        }
        return;
    }
  }

  // if (view.getWidth() < view.getHeight()){


  Point getCameraResolution() {
    return cameraResolution;
  }

  Point getViewResolution() {
    return new Point(view.getWidth(), view.getHeight());
  }

  Point getScreenResolution() {
    return screenResolution;
  }

  boolean getTorchState(Camera camera) {
    if (camera != null) {
      Camera.Parameters parameters = camera.getParameters();
      if (parameters != null) {
        String flashMode = camera.getParameters().getFlashMode();
        return flashMode != null &&
            (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) ||
                Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
      }
    }
    return false;
  }

  void setTorch(Camera camera, boolean newSetting) {
    Camera.Parameters parameters = camera.getParameters();
    doSetTorch(parameters, newSetting, false);
    camera.setParameters(parameters);
  }

  private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
    boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
    doSetTorch(parameters, currentSetting, safeMode);
  }

  private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
    String flashMode;
    if (newSetting) {
      flashMode = findSettableValue(parameters.getSupportedFlashModes(),
          Camera.Parameters.FLASH_MODE_TORCH,
          Camera.Parameters.FLASH_MODE_ON);
    } else {
      flashMode = findSettableValue(parameters.getSupportedFlashModes(),
          Camera.Parameters.FLASH_MODE_OFF);
    }
    if (flashMode != null) {
      parameters.setFlashMode(flashMode);
    }

    /*
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (!prefs.getBoolean(Preferences.KEY_DISABLE_EXPOSURE, false)) {
      if (!safeMode) {
        ExposureInterface exposure = new ExposureManager().build();
        exposure.setExposure(parameters, newSetting);
      }
    }
     */
  }

  private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {

    List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
    if (rawSupportedSizes == null) {
      Log.w(TAG, "没有返回预览尺寸，采用默认值代替");
      Camera.Size defaultSize = parameters.getPreviewSize();
      return new Point(defaultSize.width, defaultSize.height);
    }

    //从大到小对返回尺寸排序
    List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
    Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
      @Override
      public int compare(Camera.Size a, Camera.Size b) {
        int aPixels = a.height * a.width;
        int bPixels = b.height * b.width;
        if (bPixels < aPixels) {
          return -1;
        }
        if (bPixels > aPixels) {
          return 1;
        }
        return 0;
      }
    });

    if (Log.isLoggable(TAG, Log.INFO)) {
      StringBuilder previewSizesString = new StringBuilder();
      for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
        previewSizesString.append(supportedPreviewSize.width).append('x')
            .append(supportedPreviewSize.height).append(' ');
      }
      Log.i(TAG, "支持的摄像头尺寸" + previewSizesString);
    }

    Point bestSize = null;
    float screenAspectRatio = (float) screenResolution.x / (float) screenResolution.y;
    // 1.779166667 屏幕尺寸比例

    float diff = Float.POSITIVE_INFINITY;//无限大
    /**
     * 遍历支持的尺寸，寻找到与显示尺寸相差最小的数值
     */

    for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
      int realWidth = supportedPreviewSize.width;
      int realHeight = supportedPreviewSize.height;
      int pixels = realWidth * realHeight; //像素数量
      // 409920
      // MIN = 150400
      // MAX = 1024000
      if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
        continue; //不考虑小于最小或者大于最大分辨率的情况
      }
      boolean isCandidatePortrait = realWidth < realHeight; //判断是否横竖屏？？
      int maybeFlippedWidth = realHeight; //isCandidatePortrait ? realHeight : realWidth;
      int maybeFlippedHeight = realWidth; //isCandidatePortrait ? realWidth : realHeight;

      //如果摄像头参数与实际屏幕参数等于一致的情况
      if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
        Point exactPoint = new Point(realWidth, realHeight);
        Log.i(TAG, "找到精确的显示尺寸: " + exactPoint);
        return exactPoint;
      }
      float aspectRatio = (float) maybeFlippedWidth / (float) maybeFlippedHeight; //显示比率

      float newDiff = Math.abs(aspectRatio - screenAspectRatio);
      if (newDiff < diff) {

        bestSize = new Point(realWidth, realHeight);
        Log.i(TAG, "暂时设定为为摄像头初始的实际尺寸" + bestSize);
        diff = newDiff;
      }
    }

    if (bestSize == null) {
      Camera.Size defaultSize = parameters.getPreviewSize();

      bestSize = new Point(defaultSize.width, defaultSize.height);
      Log.i(TAG, "没找到合适的尺寸，使用默认尺寸: " + bestSize);
    }
    Log.i(TAG, "找到合适的尺寸，尺寸为: " + bestSize);
    return bestSize;
  }

  private static String findSettableValue(Collection<String> supportedValues,
                                          String... desiredValues) {
    Log.i(TAG, "Supported values: " + supportedValues);
    String result = null;
    if (supportedValues != null) {
      for (String desiredValue : desiredValues) {
        if (supportedValues.contains(desiredValue)) {
          result = desiredValue;
          break;
        }
      }
    }
    Log.i(TAG, "Settable value: " + result);
    return result;
  }

}
