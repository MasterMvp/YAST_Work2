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

package com.YinanSoft.phoneface.decode;

import android.os.Handler;
import android.os.Looper;

import com.YinanSoft.phoneface.ui.camera.live.CameraFaceCallback;
import com.YinanSoft.phoneface.ui.view.CameraSurfaceView;

import java.util.concurrent.CountDownLatch;

//import com.techshino.tesoface.Algorithm;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class DecodeThread extends Thread {

    public static final String FACE_BITMAP = "face_bitmap";
    public static final String FACE_BITMAP_0 = "face_bitmap_0";
    public static final String FACE_BITMAP_1 = "face_bitmap_1";
    public static final String FACE_BITMAP_2 = "face_bitmap_2";
    // public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final CameraSurfaceView fragment;
//    private final Algorithm algorithm;
    //private final Map<DecodeHintType,Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;
    private final CameraFaceCallback mFaceCallback;

    public DecodeThread(CameraSurfaceView fragment, CameraFaceCallback faceCallback) {
        this.fragment = fragment;
        //this.algorithm = algorithm;
        this.mFaceCallback = faceCallback;
        handlerInitLatch = new CountDownLatch(1);
    }

    public Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(fragment, mFaceCallback);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
