package com.YinanSoft.phoneface.decode;

/**
 * Created by mito on 9/15/13.
 */
public interface IConstants {

	int DECODE = 0;
	
	int QUIT = 1;
	
	int ONTIMER=9;
	
	int DECODE_COMPLETE = 2;
	/** 解析状态 */
	int DECODE_STATE = 11;
	/** 解码成功 */
	int DECODE_SUCCEDED = 2;
	/** 解码失败 */
	int DECODE_FAILED = 3;
	int SUCESS = 4;

	int RESTART_PREVIEW = 5;
	int AUTO_FOCUS = 6;
	int DECODE_RESULT = 7;
	/** 检活成功 */
	int PHOTO_VERFY_SUCCESS = 8;
	/** 检测过程中丢帧 **/
	int CHECKING_NO_FACE = 21;
	/** 启动检测 */
	int START_DECODE_THREAD = 22;
	int CAMERA_TIME_OUT = 13;
	int NET_TIME_OUT = 15;

}
