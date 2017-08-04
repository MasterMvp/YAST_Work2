package attendance.yn.a606a.finger.process;

/**
 * 指纹操作底层回调信息
 * @author Administrator
 *
 */
public class PTCommCallback 
{

	/** 
	 * GUI Callback messages
	 */
	 
	public static final int PT_GUIMSG_GOOD_IMAGE    				= 0;    ///<  图像采集成功
	public static final int PT_GUIMSG_BAD_QUALITY    				= 1;    ///<  质量不过关（用户没有按手指和质量太差都为一类） 
	public static final int PT_GUIMSG_IMAGE_FAILED    				= 2;    ///<  采集失败
	public static final int PT_GUIMSG_PUT_FINGER    				= 3;    ///<  请放手指
	public static final int PT_GUIMSG_PUT_FINGER1    				= 4;    ///<  第一次采集
	public static final int PT_GUIMSG_PUT_FINGER2    				= 5;    ///<  第二次采集
	public static final int PT_GUIMSG_PUT_FINGER3    				= 6;    ///<  第三次采集
	public static final int PT_GUIMSG_REMOVE_FINGER    				= 7;    ///<  抬起手指

	public static final int PT_GUIMSG_PROCESSING_IMAGE_START    	= 8;    ///<  图像处理开始

	public static final int PT_GUIMSG_CONSOLIDATION_FAIL   			= 9;    ///<  合并特征值失败
	public static final int PT_GUIMSG_CONSOLIDATION_START   		= 10;   ///<  合并特征值
	public static final int PT_GUIMSG_CONSOLIDATION_END     		= 11;   ///<  合并特征值结束

	public static final int PT_GUIMSG_IMAGE_PROCESSED_END    		= 12;   ///<  图像处理完毕
	public static final int PT_GUIMSG_IMAGE_PROCESSED_FAILED   		= 13;   ///<  图像处理失败

	public static final int PT_GUIMSG_TMPELATE_SAVE_START			= 14;	///<  特征值存储开始
	public static final int PT_GUIMSG_TMPELATE_SAVE_END				= 15;	///<  特征值存储结束
	public static final int PT_GUIMSG_TMPELATE_SAVE_FAILED			= 16;	///<  特征值存储失败
	public static final int PT_GUIMSG_IMAGE_UPLOAD_START			= 17;	///<  上传图像开始
	public static final int PT_GUIMSG_IMAGE_DOWNLOAD_START			= 18;	///<  下载图像开始
	public static final int PT_GUIMSG_GRAB_PROGRESS    			    = 19;   ///<  图像采集进度
	
	public static final String GOOD_IMAGE  = "图像采集成功" ;
	//（用户没有按手指和质量太差都为一类） 
	public static final String BAD_QUALITY= "质量不过关" ;
	public static final String IMAGE_FAILED = "采集失败" ;
	public static final String PUT_FINGER= "请放手指" ;
	public static final String PUT_FINGER1 = "第一次采集" ;
	public static final String PUT_FINGER2 = "第二次采集" ;
	public static final String PUT_FINGER3  = "第三次采集" ;
	public static final String REMOVE_FINGER = "抬起手指" ;

	public static final String PROCESSING_IMAGE_START = "图像处理开始" ;

	public static final String CONSOLIDATION_FAIL  = "合并特征值失败" ;
	public static final String CONSOLIDATION_START = "合并特征值" ;
	public static final String CONSOLIDATION_END  = "合并特征值结束" ;

	public static final String IMAGE_PROCESSED_END = "图像处理完毕" ;
	public static final String IMAGE_PROCESSED_FAILED = "图像处理失败" ;

	public static final String TMPELATE_SAVE_START = "特征值存储开始" ;
	public static final String TMPELATE_SAVE_END = "特征值存储结束" ;
	public static final String TMPELATE_SAVE_FAILED = "特征值存储失败" ;
	
	public static final String IMAGE_UPLOAD_START	= "上传图像开始" ;
	public static final String IMAGE_DOWNLOAD_START =	"下载图像开始" ;

	public static final String GRAB_PROGRESS = "图像采集进度";
	
	// callbacks Correspondence information

	public PTCommCallback()
	{
	
		
	}


	public static String getCallbackmsg(int code)
	{
		
		String result = null;
		
		switch (code)
		{
		
		case  PT_GUIMSG_GOOD_IMAGE :  
				result = GOOD_IMAGE;
			break;
			
			case  PT_GUIMSG_BAD_QUALITY :  
				result = BAD_QUALITY;
			break;
			
			case  PT_GUIMSG_IMAGE_FAILED :  
				result = IMAGE_FAILED;
			break;
			
			case  PT_GUIMSG_PUT_FINGER  :  
				result = PUT_FINGER;
			break;
			
			case  PT_GUIMSG_PUT_FINGER1  :  
				result = PUT_FINGER1;
			break;
			
			case  PT_GUIMSG_PUT_FINGER2  : 
				result = PUT_FINGER2;
			break;
			
			case  PT_GUIMSG_PUT_FINGER3  : 
				result = PUT_FINGER3;
			break;
			
			case  PT_GUIMSG_REMOVE_FINGER  : 
				result = REMOVE_FINGER;
			break;
			
			case  PT_GUIMSG_PROCESSING_IMAGE_START  : 
				result = PROCESSING_IMAGE_START;
			break;
			
			case  PT_GUIMSG_CONSOLIDATION_FAIL  : 
				result = CONSOLIDATION_FAIL;
			break;
			
			case  PT_GUIMSG_CONSOLIDATION_START  : 
				result = CONSOLIDATION_START;
			break;
			
			case  PT_GUIMSG_CONSOLIDATION_END  : 
				result = CONSOLIDATION_END;
			break;
			
			case  PT_GUIMSG_IMAGE_PROCESSED_END  :  
				result = IMAGE_PROCESSED_END ;
			break;
			
			case  PT_GUIMSG_IMAGE_PROCESSED_FAILED  : 
				result = IMAGE_PROCESSED_FAILED;
			break;
			
			case  PT_GUIMSG_TMPELATE_SAVE_START  :  
				result = TMPELATE_SAVE_START ;
			break;
			
			case  PT_GUIMSG_TMPELATE_SAVE_END  : 
				result = TMPELATE_SAVE_END;
			break;

			case  PT_GUIMSG_TMPELATE_SAVE_FAILED  :  
				result = TMPELATE_SAVE_FAILED ;
			break;
			
			case  PT_GUIMSG_IMAGE_UPLOAD_START  :  
				result = IMAGE_UPLOAD_START ;
			break;
			
			case  PT_GUIMSG_IMAGE_DOWNLOAD_START	  :  
				result = IMAGE_DOWNLOAD_START ;
			break;
			
			case PT_GUIMSG_GRAB_PROGRESS:
				result = GRAB_PROGRESS;
				break;
		default:
		    	
		        result = String.format("");

			break;
		}
		
		
		return result;
	}

		  
}
