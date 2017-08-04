/*
 * Copyright 2007 ZXing authors
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

package com.YinanSoft.phoneface.model.result;

import android.graphics.RectF;

import com.YinanSoft.phoneface.common.Stfaceattr;

/**
 * <p>
 * Encapsulates the result of decoding a barcode within an image.
 * </p>
 * 
 * @author Sean Owen
 */
public final class Result {

	private final String text;
	private final RectF faceRet;
	private final long timestamp;
	private final Stfaceattr mStfaceattr;

	public Result(String text, RectF faceRet) {
		this(text, faceRet,null);
	}

	public Result(String text, RectF faceRet,Stfaceattr stfaceattr) {
		this(text,faceRet,stfaceattr, System.currentTimeMillis());
	}

	public RectF getFaceRet() {
		return faceRet;
	}

	public Result(String text, RectF faceRet,Stfaceattr stfaceattr,long timestamp) {
		this.text = text;
		this.faceRet = faceRet;
		this.timestamp = timestamp;
		this.mStfaceattr = stfaceattr;
	}

	/**
	 * @return raw text encoded by the barcode
	 */
	public String getText() {
		return text;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return text;
	}

	public Stfaceattr getStfaceattr() {
		return mStfaceattr;
	}
}
