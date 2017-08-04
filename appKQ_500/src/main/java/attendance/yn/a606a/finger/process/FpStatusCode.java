package attendance.yn.a606a.finger.process;



import android.util.Log;


public class FpStatusCode
{
	
	//jni底层回调-暂时 不在使用
	public int StatusCode;
	
	/*********蓝牙控制部分********/
	public static final int BT_DEVICE_NOT_SUPPORT= -100;//设备不支持蓝牙
	public static final int BT_CONECT_FAILED = -101;//蓝牙连接失败
	public static final int BT_CONECT_INPUT_FAILED = -102;//套接字 建立输入失败
	public static final int BT_CONECT_OUTPUT_FAILED = -103;//套接字 建立输出失败
	public static final int BT_MATCH_CONNECT = -104;//配对连接
	public static final int BT_MATCH_KEY = -105;//配对
	
	
	/******************操作状态部分*****************/
	
	public static final int PT_MSG_OPEN_BACKCALL = 200;//打开
	public static final int PT_MSG_CODE_BACKCALL = 201;//状态码消息
	
	public static final int PT_MSG_PROCESS_START = 202;//开始
	public static final int PT_MSG_PROCESS_END = 203;//结束
	public static final int PT_MSG_IMG_TRANSPORT = 204;//图像传输
	
	public static final int PT_MSG_HAL_BACKCALL = 205;//底层回调信息
	public static final int PT_MSG_GRAB_BACKCALL = 206;//图像
	public static final int PT_MSG_OTHER = 206;//其他信息
	
	/*****************后台服务部分**************************/
	
	public static final int UNIBAP_VERIFY_FAIL = 20000063;//后台比对失败
	
	

	/**************设备通信控制部分************************/
	
	/**
	 * @name Public TFM API error codes
	 * Those are valid values of @ref PT_STATUS return type.
	 */
	/** 操作 成功  */
	public static final int PT_STATUS_OK  										 = 0;

	/** 操作 失败 */
	public static final int PT_STATUS_FAILED  									 = -1;
	
	/** 授权成功 */
	public static final int PT_PERMISSION_OK 									 = 3;
	/** 授权失败 */
	public static final int PT_PERMISSION_FAILED 								 = -3;
	
	/** 网络联络失败 */
	public static final int PT_NETWORK_CONNECT_ERR 								 = -675;
	/** 密钥错误 */
	public static final int PT_STATUS_KEY_ERROR 								 = -915;
	/** 加密类型 错误 */
	public static final int PT_STATUS_ENCRYPTTYPE 								 = -919;
	/** 未知错误 */
	public static final int PT_STATUS_GENERAL_ERROR 							 = -1001;             
	/** 未初始化 */
	public static final int PT_STATUS_API_NOT_INIT 								 = -1002;
	/** 已经初始化 */
	public static final int PT_STATUS_API_ALREADY_INITIALIZED 					 = -1003;

	/** 无效参数 */
	public static final int PT_STATUS_INVALID_PARAMETER 						 = -1004;
	/** 无效句柄 */
	public static final int PT_STATUS_INVALID_HANDLE 							 = -1005;        


	/** 没有足够的内存来处理特定的操作 */
	public static final int PT_STATUS_NOT_ENOUGH_MEMORY 						 = -1006;
	/** 通过数据太大 */
	public static final int PT_STATUS_DATA_TOO_LARGE 							 = -1008;              
	/** 没有足够的常驻内存来存储数据 */
	public static final int PT_STATUS_NOT_ENOUGH_PERMANENT_MEMORY 				 = -1009;

	/** 配置YW0192失败 */
	public static final int  PT_STATUS_CONFIG_YW0192_SENSOR_FAILED               = -1010;
	/** 配置NB传感器失败 */
	public static final int  PT_STATUS_CONFIG_NB_SENSOR_FAILED             		 = -1011;


	/**  未知的命令 */
	public static final int  PT_STATUS_UNKNOWN_COMMAND 							 = -1031;

	/**  命令执行失败 */
	public static final int  PT_STATUS_COMMAND_FAILED 	             			 = -1032;

	/** 函数失败 */
	public static final int PT_STATUS_FUNCTION_FAILED 							 = -1033;

	/** 命令帧的格式错误  */
	public static final int  PT_STATUS_MALFORMED_COMMAND_FRAME  				 = -1034;
	/**  给定的参数无效 */
	public static final int  PT_STATUS_INVALID_PURPOSE 							 = -1035;

	/** 结构形式无效 */
	public static final int PT_STATUS_INVALID_INPUT_BIR_FORM 					 = -1036;

	/**  通信错误 */
	public static final int  PT_STATUS_COMM_ERROR 								 = -1037;


	/** 设备未打开 */
	public static final int  PT_STATUS_UN_OPENED							 	 = -1038;
	
	/** 已经打开 */
	public static final int  PT_STATUS_ALREADY_OPENED 							 = -1039;

	/**  会话结束 */
	public static final int  PT_STATUS_SESSION_TERMINATED 						 = -1040;
	/**  超时 */
	public static final int  PT_STATUS_TIMEOUT 									 = -1041;
	/** 无效的超时时间 */
	public static final int  PT_STATUS_INVALID_TIMEOUT              			 = -1042;
	/** 该指纹编号 不存在 */
	public static final int  PT_STATUS_ID_NOT_EXIST             				 = -1043;
	/** 无效的指纹编号 */
	public static final int  PT_STATUS_INVALID_ID_NUM             				 = -1044;
	/** 指纹编号已经存在 */
	public static final int  PT_STATUS_FP_ID_ALREADY_EXIST             			 = -1045;


	/** 指纹 不匹配  -- 当指纹库为空比对也返回 此提示*/
	public static final int  PT_STATUS_NOT_MATCH             					 = -1046;

	/** 无效的ID范围 */
	public static final int  PT_STATUS_INVALID_ID_RANGE            				 = -1047;

	/** 指纹库已满，无可用指纹ID */
	public static final int  PT_STATUS_FP_DATABASE_IS_FULL             			 = -1048;

	/** 缓冲区中无有效图像 */
	public static final int  PT_STATUS_INVALID_IMAGE_BUFFER             		 = -1049;


	/** 无有效指纹图像 */
	public static final int  PT_STATUS_INVALID_FP_IMAGE             			 = -1050;


	/**  没有模版 */
	public static final int  PT_STATUS_NO_TEMPLATE 								 = -1051;
	/** 缓冲区中无有效特征值 */
	public static final int  PT_STATUS_INVALID_EIGENVALUES_BUFFER           	 = -1052;
	/**  模版合并失败 */
	public static final int  PT_STATUS_CONSOLIDATION_FAILED 					 = -1053;

	

	/** 指纹库中有同样的特征值(相对于已经存在) */
	public static final int  PT_STATUS_FP_EIGENVALUES_ALREADY_EXIST              = -1054;
	/** 无效的特征值数量 */
	public static final int  PT_STATUS_INVALID_EIGENVALUES_NUM             		 = -1055;

	/** 无效的特征值编号  */
	public static final int  PT_STATUS_INVALID_EIGENVALUES_ID_NUM             	 = -1056;
	/** 无效的特征值 */
	public static final int  PT_STATUS_INVALID_EIGENVALUES             			 = -1057;
	/** 特征值损坏 */
	public static final int  PT_STATUS_EIGENVALUES_DAMAGE            			 = -1058;

	  
	/** 读FLASH失败 */
	public static final int  PT_STATUS_READ_FLASH_FAILED             			 = -1059;

	/** 写FLASH失败 */
	public static final int  PT_STATUS_WRITE_FLASH_FAILED             			 = -1060;

	/**  从FLASH中读取算法的版本失败 */
	public static final int  PT_STATUS_READ_FLASH_ALGORITHM_VERSION_FAILED       = -1061;

	/**  从FLASH中读取序列号失败		 */
	public static final int  PT_STATUS_READ_FLASH_SERIAL_NUM_FAILED              = -1062;    

	/**  向FLASH中写入序列号失败		*/
	public static final int  PT_STATUS_WRITE_FLASH_SERIAL_NUM_FAILED              = -1064;    

	/**  传感器 校准失败 */
	public static final int  PT_STATUS_CALIBRATION_FAILED 						 = -1065;
	/**  没有传感器 */
	public static final int  PT_STATUS_NO_SENSOR 								 = -1066;
	/**  没有找到设备 */
	public static final int  PT_STATUS_DEVICE_NOT_FOUND 						 = -1067;
	/**  传感器未校准 */
	public static final int  PT_STATUS_SENSOR_NOT_CALIBRATED 					 = -1068;

	/**  主机不支持此速度 */
	public static final int  PT_STATUS_UNSUPPORTED_SPEED 						 = -1069;

	/**  无效的序列号 */
	public static final int  PT_STATUS_INVALID_SERIAL_NUM            			 = -1070;
	/**  无效的波特率 */
	public static final int  PT_STATUS_INVALID_BAUD             				 = -1071;
	/**  无效的安全级别 */
	public static final int  PT_STATUS_INVALID_SECURITY_LEVEL            		 = -1072;
	/**  无效的自动学习模式 */
	public static final int  PT_STATUS_INVALID_AUTO_LEARNING             		 = -1073;
	/**  无效的重复检测 */
	public static final int  PT_STATUS_INVALID_DUPLICATE_DETECTION             	 = -1074;

	/**  没有找到数据 */
	public static final int  PT_STATUS_NO_DATA 									 = -1075;

	/** 该ID所对的空间模版为空 */
	public static final int  PT_STATUS_FP_ID_TEMPLATE_EMPTY           			 = -1076;


	/** 读取指纹有效表失败 */
	public static final int  PT_STATUS_READ_FP_VALID_TABLE_FAILED          		 = -1077;


	/** 读取指纹库的特征值失败 */
	public static final int  PT_STATUS_READ_FP_DATABASE_EIGENVALUES_FAILED 		= -1078;

	/** 热敏传感器没有参数要配置 */
	public static final int  PT_STATUS_NB_SENSOR_NO_CONFIG						= -1079;

	/** 操作取消 */
	public static final int  PT_STATUS_CANCEL_TASK								= -1080;
	
	/** 写入指纹库失败 */
	public static final int  PT_STATUS_WRITE_FP_DATABASE_FAILED             	= -1081;

	/** 设备通信类型 错误*/
	public static final int  PT_STATUS_FP_DEVICE_COMM_TYPE_ERROR			    = -1082;
	
	/** 没有找到设备*/
	public static final int  PT_STATUS_FP_DEVICE_NOT_FOUND			    		= -1083;
	
	/** 访问设备被拒绝（没有足够的权限） */
	public static final int  PT_STATUS_DEV_ERROR_ACCESS 						= -1084;
	
	
	/** 单次图像采集超过最大次数 --重新开始即可 */
	public static final int  PT_STATUS_GRAB_ERROR_MORE_THAN_MAX_NUM				= -1085;
	
	/**  生成模版失败 */
	public static final int  PT_STATUS_GENERATION_TEMPLATE_FAILED 				= -1086;
	
	/**  指纹图像质量太差 */
	public static final int  PT_STATUS_FP_LOW_QUALITY 							= -1087;
	
	/**  授权过期 */
	public static final int  PT_STATUS_LICENSE_OVER 							= -1088;
	
	
	
	/*********************usb********************error******************/


	/** usb 输入/输出错误 */
	public static final int  PT_STATUS_USB_ERROR_IO								= -2080;

	/** usb 无效的参数 */
	public static final int  PT_STATUS_USB_ERROR_INVALID_PARAM 					= -2081;

	/** USB 访问被拒绝（没有足够的权限） */
	public static final int  PT_STATUS_USB_ERROR_ACCESS 						= -2082;

	/** USB 没有这样的设备（它可能已被断开） */
	public static final int  PT_STATUS_USB_ERROR_NO_DEVICE 						= -2083;

	/** USB 未找到实体 */
	public static final int  PT_STATUS_USB_ERROR_NOT_FOUND 						= -2084;

	/** USB 资源忙 */
	public static final int  PT_STATUS_USB_ERROR_BUSY 							= -2085;

	/** usb 操作超时 */
	public static final int  PT_STATUS_USB_ERROR_TIMEOUT 						= -2086;

	/** USB 溢出 */
	public static final int  PT_STATUS_USB_ERROR_OVERFLOW 						= -2087;

	/** usb 通道 错误 */
	public static final int  PT_STATUS_USB_ERROR_PIPE 							= -2088;

	/** USB 系统调用中断（可能由于信号原因）*/
	public static final int  PT_STATUS_USB_ERROR_INTERRUPTED 					= -2089;

	/**USB 内存不足*/
	public static final int  PT_STATUS_USB_ERROR_NO_MEM 						= -2090;

	/** USB 操作不支持或者未实现在这个平台上 */
	public static final int  PT_STATUS_USB_ERROR_NOT_SUPPORTED 					= -2091;

	/*  when adding new error codes here. */

	/** USB其他错误  */
	public static final int  PT_STATUS_USB_ERROR_OTHER 							= -2199;

	public void setCode(int StatusCode)
	{
		this.StatusCode = StatusCode;
	}

	public int getCode()
	{
		return this.StatusCode;
	}

	public static String getMessage(int StatusCode)
	{
		Log.d("getMessagecode", ""+StatusCode);
		
	//	String result = (new StringBuilder("fp process info(")).append(StatusCode).append("): ").toString();
		String result = "";
		switch (StatusCode)
		{
		case PT_STATUS_OK: 
			result = (new StringBuilder(String.valueOf(result))).append("操作 成功").toString();
			break;
		
		case PT_STATUS_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("操作 失败").toString();
			break;
			
		case PT_PERMISSION_OK: 
			result = (new StringBuilder(String.valueOf(result))).append("授权成功").toString();
			break;
			
		case PT_PERMISSION_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("授权失败").toString();
			break;
			
		case PT_NETWORK_CONNECT_ERR: 
			result = (new StringBuilder(String.valueOf(result))).append("网络联络失败  ").toString();
			break;
			
		case PT_STATUS_KEY_ERROR: 
			result = (new StringBuilder(String.valueOf(result))).append("密钥错误 ").toString();
			break;
			
		case PT_STATUS_ENCRYPTTYPE: 
			result = (new StringBuilder(String.valueOf(result))).append("加密类型 错误 ").toString();
			break;
			
		case PT_STATUS_GENERAL_ERROR: 
			result = (new StringBuilder(String.valueOf(result))).append("未知错误").toString();
			break;
			
		case PT_STATUS_API_NOT_INIT: 
			result = (new StringBuilder(String.valueOf(result))).append("未初始化").toString();
			break;
			
		case PT_STATUS_API_ALREADY_INITIALIZED:
			result = (new StringBuilder(String.valueOf(result))).append("已经初始化").toString();
			break;

		case PT_STATUS_INVALID_PARAMETER:
			result = (new StringBuilder(String.valueOf(result))).append("无效参数").toString();
			break;
			
		case PT_STATUS_INVALID_HANDLE:
			result = (new StringBuilder(String.valueOf(result))).append("无效句柄").toString();
			break;	
			
			
		case PT_STATUS_NOT_ENOUGH_MEMORY: 
			result = (new StringBuilder(String.valueOf(result))).append("无没有足够的内存来处理特定的操作  ").toString();
			break;
			
		case PT_STATUS_DATA_TOO_LARGE: 
			result = (new StringBuilder(String.valueOf(result))).append(" 通过数据太大").toString();
			break;

		case PT_STATUS_NOT_ENOUGH_PERMANENT_MEMORY: 
			result = (new StringBuilder(String.valueOf(result))).append("没有足够的常驻内存来存储数据.").toString();
			break;

		case PT_STATUS_CONFIG_YW0192_SENSOR_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("配置YW0192失败").toString();
			break;

		case PT_STATUS_CONFIG_NB_SENSOR_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("配置NB传感器失败").toString();
			break;

		case PT_STATUS_UNKNOWN_COMMAND: 
			result = (new StringBuilder(String.valueOf(result))).append("未知的命令").toString();
			break;

		case PT_STATUS_COMMAND_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("命令执行失败").toString();
			break;

		case PT_STATUS_FUNCTION_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("函数失败").toString();
			break;

		case PT_STATUS_MALFORMED_COMMAND_FRAME: 
			result = (new StringBuilder(String.valueOf(result))).append("给定的参数无效").toString();
			break;

		case PT_STATUS_INVALID_PURPOSE: 
			result = (new StringBuilder(String.valueOf(result))).append("给定的参数无效").toString();
			break;

		case PT_STATUS_INVALID_INPUT_BIR_FORM: 
			result = (new StringBuilder(String.valueOf(result))).append("结构形式无效").toString();
			break;

		case PT_STATUS_ALREADY_OPENED: 
			result = (new StringBuilder(String.valueOf(result))).append("已经打开").toString();
			break;

		case PT_STATUS_UN_OPENED: 
			result = (new StringBuilder(String.valueOf(result))).append("设备未打开").toString();
			break;

		case PT_STATUS_COMM_ERROR: 
			result = (new StringBuilder(String.valueOf(result))).append("通信错误 ").toString();
			break;

		case PT_STATUS_SESSION_TERMINATED: 
			result = (new StringBuilder(String.valueOf(result))).append("会话结束").toString();
			break;

		case PT_STATUS_TIMEOUT: 
			result = (new StringBuilder(String.valueOf(result))).append("超时").toString();
			break;

		case PT_STATUS_INVALID_TIMEOUT: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的超时时间").toString();
			break;

		case PT_STATUS_ID_NOT_EXIST: 
			result = (new StringBuilder(String.valueOf(result))).append("该指纹编号不在").toString();
			break;

		case PT_STATUS_INVALID_ID_NUM: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的指纹编号").toString();
			break;

		case PT_STATUS_FP_ID_ALREADY_EXIST: 
			result = (new StringBuilder(String.valueOf(result))).append("指纹已经存在").toString();
			break;

		case PT_STATUS_NOT_MATCH: 
			result = (new StringBuilder(String.valueOf(result))).append("指纹 不匹配").toString();
			break;

		case PT_STATUS_INVALID_ID_RANGE: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的ID范围").toString();
			break;

		case PT_STATUS_FP_DATABASE_IS_FULL: 
			result = (new StringBuilder(String.valueOf(result))).append("指纹库已满，无可用指纹ID").toString();
			break;

		case PT_STATUS_INVALID_IMAGE_BUFFER: 
			result = (new StringBuilder(String.valueOf(result))).append("缓冲区中无有效图像").toString();
			break;

		case PT_STATUS_INVALID_FP_IMAGE: 
			result = (new StringBuilder(String.valueOf(result))).append("无有效指纹图像").toString();
			break;

		case PT_STATUS_NO_TEMPLATE: 
			result = (new StringBuilder(String.valueOf(result))).append("没有模版").toString();
			break;

		case PT_STATUS_INVALID_EIGENVALUES_BUFFER: 
			result = (new StringBuilder(String.valueOf(result))).append("缓冲区中无有效特征值").toString();
			break;

		case PT_STATUS_CONSOLIDATION_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append(" 模版合并失败 ").toString();
			break;

		case PT_STATUS_WRITE_FP_DATABASE_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("写入指纹库失败").toString();
			break;
			
		case PT_STATUS_FP_DEVICE_COMM_TYPE_ERROR:
			result = (new StringBuilder(String.valueOf(result))).append("设备通信类型 错误").toString();
			break;

		case PT_STATUS_FP_EIGENVALUES_ALREADY_EXIST: //指纹库中有同样的特征值(相对于已经存在)
			result = (new StringBuilder(String.valueOf(result))).append("指纹已经存在").toString();
			break;

		case PT_STATUS_INVALID_EIGENVALUES_NUM: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的特征值数量 ").toString();
			break;

		case PT_STATUS_INVALID_EIGENVALUES_ID_NUM: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的特征值编号").toString();
			break;

		case PT_STATUS_INVALID_EIGENVALUES: 
			result = (new StringBuilder(String.valueOf(result))).append(" 无效的特征值").toString();
			break;

		case PT_STATUS_EIGENVALUES_DAMAGE: 
			result = (new StringBuilder(String.valueOf(result))).append("特征值损坏").toString();
			break;

		case PT_STATUS_READ_FLASH_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("读FLASH失败").toString();
			break;

		case PT_STATUS_WRITE_FLASH_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("写FLASH失败").toString();
			break;

		case PT_STATUS_READ_FLASH_ALGORITHM_VERSION_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append(" 从FLASH中读取算法的版本失败").toString();
			break;

		case PT_STATUS_READ_FLASH_SERIAL_NUM_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("从FLASH中读取序列号失败").toString();
			break;

		case PT_STATUS_WRITE_FLASH_SERIAL_NUM_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("向FLASH中写入序列号失败").toString();
			break;

		case PT_STATUS_CALIBRATION_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("传感器 校准失败").toString();
			break;

		case PT_STATUS_NO_SENSOR: 
			result = (new StringBuilder(String.valueOf(result))).append("没有传感器").toString();
			break;

		case PT_STATUS_DEVICE_NOT_FOUND: 
			result = (new StringBuilder(String.valueOf(result))).append("没有找到设备").toString();
			break;

		case PT_STATUS_SENSOR_NOT_CALIBRATED: 
			result = (new StringBuilder(String.valueOf(result))).append("传感器未校准").toString();
			break;

		case PT_STATUS_UNSUPPORTED_SPEED: 
			result = (new StringBuilder(String.valueOf(result))).append("主机不支持此速度 ").toString();
			break;

		case PT_STATUS_INVALID_SERIAL_NUM: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的序列号").toString();
			break;

		case PT_STATUS_INVALID_BAUD: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的波特率 ").toString();
			break;

		case PT_STATUS_INVALID_SECURITY_LEVEL: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的安全级别 ").toString();
			break;

		case PT_STATUS_INVALID_AUTO_LEARNING: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的自动学习模式").toString();
			break;

		case PT_STATUS_INVALID_DUPLICATE_DETECTION: 
			result = (new StringBuilder(String.valueOf(result))).append("无效的重复检测").toString();
			break;

		case PT_STATUS_NO_DATA: 
			result = (new StringBuilder(String.valueOf(result))).append("没有找到数据").toString();
			break;

		case PT_STATUS_FP_ID_TEMPLATE_EMPTY: 
			result = (new StringBuilder(String.valueOf(result))).append(" 该ID所对的空间模版为空 ").toString();
			break;

		case PT_STATUS_READ_FP_VALID_TABLE_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("读取指纹有效表失败").toString();
			break;

		case PT_STATUS_READ_FP_DATABASE_EIGENVALUES_FAILED: 
			result = (new StringBuilder(String.valueOf(result))).append("读取指纹库的特征值失败 ").toString();
			break;

		case PT_STATUS_NB_SENSOR_NO_CONFIG: 
			result = (new StringBuilder(String.valueOf(result))).append("热敏传感器没有参数要配置").toString();
			break;

		case PT_STATUS_CANCEL_TASK: 
			result = (new StringBuilder(String.valueOf(result))).append("操作取消").toString();
			break;
		case PT_STATUS_FP_DEVICE_NOT_FOUND: 
			result = (new StringBuilder(String.valueOf(result))).append("没有发现设备").toString();
			break;
		case PT_STATUS_DEV_ERROR_ACCESS:
			result = (new StringBuilder(String.valueOf(result))).append("访问设备被拒绝（没有足够的权限）").toString();
		break;
		
		case PT_STATUS_GRAB_ERROR_MORE_THAN_MAX_NUM:
			result = (new StringBuilder(String.valueOf(result))).append("单次图像采集超过最大次数").toString();
			break;
		case PT_STATUS_GENERATION_TEMPLATE_FAILED:
			result = (new StringBuilder(String.valueOf(result))).append("生成模版失败").toString();
			break;
			
		case PT_STATUS_FP_LOW_QUALITY:
			result = (new StringBuilder(String.valueOf(result))).append("指纹图像质量太差").toString();
			break;	
		case PT_STATUS_LICENSE_OVER:
			result = (new StringBuilder(String.valueOf(result))).append("授权过期").toString();
			break;	
		
				
			
			
			
			/************************usb*****************error*************/
		case PT_STATUS_USB_ERROR_IO: 
			result = (new StringBuilder(String.valueOf(result))).append("usb 输入/输出错误").toString();
			break;

		case PT_STATUS_USB_ERROR_INVALID_PARAM: 
			result = (new StringBuilder(String.valueOf(result))).append("usb 无效的参数").toString();
			break;

		case PT_STATUS_USB_ERROR_ACCESS: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 访问被拒绝（没有足够的权限）").toString();
			break;

		case PT_STATUS_USB_ERROR_NO_DEVICE: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 没有这样的设备（它可能已被断开）").toString();
			break;

		case PT_STATUS_USB_ERROR_NOT_FOUND: 
			result = (new StringBuilder(String.valueOf(result))).append(" USB 未找到实体").toString();
			break;

		case PT_STATUS_USB_ERROR_BUSY: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 资源忙").toString();
			break;

		case PT_STATUS_USB_ERROR_TIMEOUT: 
			result = (new StringBuilder(String.valueOf(result))).append("usb 操作超时 ").toString();
			break;

		case PT_STATUS_USB_ERROR_OVERFLOW: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 溢出 ").toString();
			break;

		case PT_STATUS_USB_ERROR_PIPE: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 通道 错误 ").toString();
			break;

		case PT_STATUS_USB_ERROR_INTERRUPTED: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 系统调用中断（可能由于信号原因）").toString();
			break;

		case PT_STATUS_USB_ERROR_NO_MEM: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 内存不足").toString();
			break;

		case PT_STATUS_USB_ERROR_NOT_SUPPORTED: 
			result = (new StringBuilder(String.valueOf(result))).append("USB 操作不支持或者未实现在这个平台上 ").toString();
			break;

		case PT_STATUS_USB_ERROR_OTHER: 
			result = (new StringBuilder(String.valueOf(result))).append("USB其他错误").toString();
			break;
			
		default:
			result = (new StringBuilder(String.valueOf(result))).append("unknown").toString();
			break;
		}
		return result;
	}
	
}
