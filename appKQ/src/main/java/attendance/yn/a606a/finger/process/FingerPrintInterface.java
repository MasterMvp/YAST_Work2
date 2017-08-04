package attendance.yn.a606a.finger.process;

import com.android.fpcomm.FPHWInfoData;

import android.graphics.Bitmap;

/**
 * 指纹功能 接口
 * 
 * @author zteway-yf
 * 
 */
public interface FingerPrintInterface
{

	/**
	 * 初始化设置
	 * 参考设备类型说明
	 * 
	 * @param mSensorType
	 *            传入传感器类型
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTInitialize(int mSensorType);

	/**
	 * 打开设备
	 * 
	 * @param idKey
	 *            传入key码 （无用 暂时预留）
	 * @param enrollNum
	 *            传入指纹每次采集的次数 1或者5 （无用 暂时预留）
	 * @param encryptType
	 *            传入密钥类型 0 为单个绑定 1 为批量绑定（无用 暂时预留）
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTOpen(String idKey, int enrollNum, int encryptType);

	/**
	 * 关闭设备
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTClose();

	/**
	 * 取消操作
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTCancel();

	/**
	 * 检测设备是否连接
	 * 
	 * 
	 * @return
	 *         true = 设备连接
	 *         false=设备非连接
	 */
	public boolean PTIsConnect();

	/**
	 * 获取传感器类型
	 * 
	 * @param mSensorType
	 *            传出传感器类型
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常常
	 */
	public int PTGetSensorType(int[] mSensorType);

	/**
	 * 获取 指纹存储下标状态
	 * 
	 * @param bState
	 *            传出字节列表(根据字节数进行填充)
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTDoState(byte[] bState);
	
	/**
	 * 矫正传感器
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTCalibration();
	

	/**
	 * 录入指纹 (指纹id下标从FPHWInfoData.INITINDEX开始)
	 * @param mFPID
	 *            传出 FPid
	 * @param timeout
	 *            传入采集超时设置 -1=30s -2=永久等待 其他定义=ms
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other = 出现异常
	 */
	public int PTEnroll(int[] mFPID, long timeout);

	/**
	 * 指定Fid指纹进行手指比对 (指纹id下标从FPHWInfoData.INITINDEX开始)
	 * 
	 * @param mFPID
	 *            传入需要比对的指纹id 
	 * @param timeout
	 *            传入采集超时设置 -1=30s -2=永久等待 其他定义=ms
	 * @return
	 *         PT_STATUS_OK = 匹配
	 *         PT_STATUS_NOT_MATCH = 不匹配
	 *         PT_STATUS_SLOT_NOT_FOUND = 没有找到这枚指纹
	 *         PT_STATUS_NO_TEMPLATE = 没有模版
	 *         other=出现异常
	 * 
	 */
	public int PTVerify(int mFPID, long timeout);

	/**
	 * 1:n指纹库比对 (指纹id下标从FPHWInfoData.INITINDEX开始)
	 * 
	 * @param mFPID
	 *            传出相匹配的Fid
	 * @param timeout
	 *            入采集超时设置 -1=30s -2=永久等待 其他定义=ms
	 * @return
	 *         PT_STATUS_OK = 匹配
	 *         PT_STATUS_NOT_MATCH = 没有找到
	 *         PT_STATUS_NO_TEMPLATE = 没有模版
	 *         other=出现异常
	 */
	public int PTVerifyAll(int[] mFPID, long timeout);

	/**
	 * 外置两个模版直接比对
	 * 
	 * @param mFPtempletByte1
	 *            传入指纹模版1
	 * @param mFPtempletByte2
	 *            传入指纹模版2
	 * @return
	 *         PT_STATUS_OK = 匹配
	 *         PT_STATUS_NOT_MATCH = 不匹配
	 *         other=出现异常
	 */
	public int PTMatch(byte[] mFPtempletByte1, byte[] mFPtempletByte2);

	/**
	 * 删除指定id的指纹 (指纹id下标从FPHWInfoData.INITINDEX开始)
	 * 
	 * @param mFPID
	 *            传入需要删除的指纹id 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         PT_STATUS_ID_NOT_EXIST = 该指纹编号 不存在
	 *         other=出现异常
	 */
	public int PTDeleteFinger(int mFPID);

	/**
	 * 删除指纹库所有指纹
	 * 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTDeleteAllFingers();

	/**
	 * 检查 mFPID 模版是否有效
	 * 
	 * @param mFPID
	 *            传入需要检测的指纹id
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTCheckIDTemplate(int mFPID);

	/**
	 * 获取指纹总数
	 * 
	 * @param mFPCount
	 *            传出 指纹总数
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other = 出现异常
	 */
	public int PTListAllFingers(int[] mFPCount);

	/**
	 * 根据 获取的 指纹图像原始数据 转换为 Bitmap
	 * 
	 * 工具函数
	 * 
	 * @param aImageData
	 *            传入原始图像数据
	 * @param iWidth
	 *            传入原始图像宽
	 * 
	 * @return 正确= Bitmap图像数据 否则=出现错误
	 */
	public Bitmap PTBitmapFromRaw(byte[] mFPImageData, int mFPImageWidth);

	/**
	 * 根据 Bitmap 转换为 指纹图像原始数据
	 * 
	 * 工具函数
	 * 
	 * @param FPBitmap
	 *            传入指纹图像Bitmap数据
	 * 
	 * @return
	 *         正确=原始图像数据
	 *         否则=出现错误
	 */
	public byte[] PTRawFromBitmap(Bitmap mFPBitmap);

	/**
	 * 获取指纹图像原始数据 (可以使用 PTBitmapFromRaw()进行转换)
	 * 
	 * @param FPImageData
	 *            传出采集成功传回图像数据 否则为 null
	 * @param timeout
	 *            传入采集超时设置 -1=30s -2=永久等待 其他定义=ms
	 * @param quality
	 *            传入图像检测 1=检测 0=不检测 (默认检测--暂时预留)
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTGrab(byte[] mFPImageData, long timeout, long quality);

	/**
	 * 直接采集获取模版数据
	 * 
	 * @param mFPTemplate
	 *            传出获取模版数据
	 * @param timeout
	 *            传入采集超时设置 -1=30s -2=永久等待 其他定义=ms
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTCapture(byte[] mFPTemplate, long timeout);

	/**
	 * 获取相对应指纹的模版数据 (指纹id下标从FPHWInfoData.INITINDEX开始)
	 * 
	 * @param mFPTemplate
	 *            传出获取的模版数据
	 * @param mFPID
	 *            传入指纹id 
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTLoadFinger(byte[] mFPTemplate, int mFPID);

	/**
	 * 存储外置模版到指纹模块中
	 * 
	 * @param mFPTemplate
	 *            传入外置模版数据
	 * @param mFPID
	 *            传出模版存储的FPID
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTStoreFinger(byte[] mFPTemplate, int[] mFPID);

	/**
	 * 传入模版之后转换为转换模版数据
	 * ( Convert template from uk format to ANSI-378 format)
	 * 
	 * @deprecated 暂时没有实现
	 * 
	 * @param srcTemplate
	 *            传入原始模版
	 * 
	 * @param desTemplate
	 *            传出转换后的模版
	 * 
	 * @param desTemplateSize
	 *            传入转换模版大小
	 * @return
	 *         PT_STATUS_OK = 正常
	 *         other=出现异常
	 */
	public int PTConvertTemplate(byte[] srcTemplate, byte[] desTemplate, int desTemplateSize);

}
