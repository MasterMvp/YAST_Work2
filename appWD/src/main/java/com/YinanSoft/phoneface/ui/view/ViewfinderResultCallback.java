/*
 * Copyright (C) 2009 ZXing authors
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

import android.graphics.RectF;

import com.YinanSoft.phoneface.model.result.ResultPointCallback;
import com.YinanSoft.phoneface.ui.fragment.BaseCameraHandler;


public final class ViewfinderResultCallback implements ResultPointCallback {

  private final ViewfinderView viewfinderView;

  public ViewfinderResultCallback(ViewfinderView viewfinderView) {
    this.viewfinderView = viewfinderView;
  }

  @Override
  public void foundPossibleResultPoint(int state ,int faceRedraw,RectF faceRect,String liveString,BaseCameraHandler handler) {
    viewfinderView.addPossibleResultPoint(state,faceRedraw,faceRect,liveString,handler);
  }

}
