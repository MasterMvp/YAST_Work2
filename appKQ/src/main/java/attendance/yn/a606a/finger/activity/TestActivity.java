package attendance.yn.a606a.finger.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.fpcomm.FPHWInfoData;
import com.finger.FingerPrintManager;
import com.finger.OnSdkStatusListener;
import com.fingersdk.process.FingerPrintInterface;
import com.fingersdk.process.FpStatusCode;
import com.fingersdk.process.PTCommCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import attendance.yn.a606a.R;

public class TestActivity extends Activity implements OnSdkStatusListener
{
	// Debugging
	private static final String TAG = "MainActivity";
	private static final boolean D = true;

	public byte[] byteimage = null;
	public byte[] byteload = null;

	public int timeout = -1;// -1=30s -2 永久
	public int imgPixel = 508;// 图像精度

	private int enroll_num = 1;// 录入次数

	public int verify_num_id = 0;
	public int delete_num_id = 0;
	public int load_num_id = 0;

	public TextView tv1, tv2, tv_fingerdata, tv_base64fingerdata;
	public EditText edit_verify, edit_delete, edit_load;
	public Button btn_verify, btn_delete, btn_verifyall, btn_enroll,
			btn_deleteall;
	public Button btn_open, btn_total, btn_grab, btn_capture, btn_close,
			btn_guid;
	public Button btn_load, btn_store, btexternal_verify, btback;

	// 循环的标志
	private static boolean loop_grab_cancel = false;
	// 是否打开
	private static boolean isOpen = false;

	public Button btn_loop_grab, btn_loop_grab_cancel;
	public ImageView loop_grab_img;

	private static Handler messageHandler = null;
	private Context mContext = null;

	FingerPrintInterface FpIf = null;

	private long quality = 1;// 检测手指图像质量
	private long dwSleepMode = 1;// 睡眠模式-检测手指
	private byte callbackCalledTimes = 0;// 回调结束时间--默认

	private int encrypttype = 1; // 密钥类型 0 为单个绑定 1 为批量绑定

	/**
	 * 量产 cb6cb0-2021dbd-5476000-584a235-43ade4d
	 */
	private String id_key = "cb6cb0-2021dbd-5476000-584a235-43ade4d";
	// private String id_key = "c44692-502cb31-388f609-40e304b-552907d";

	private static int status = 0;
	
	/**
	 * 传出的指纹ID
	 */
	private int[] mFPID = new int[4];
	
	/**
	 * 传出的指纹总数
	 */
	private int[] mFPCount = new int[4];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.activity_test);
		mContext = getApplicationContext();

		Looper looper = Looper.getMainLooper();
		messageHandler = new MessageHandler(looper);

		FingerPrintManager mPrintManager = FingerPrintManager.getInstance(mContext);
		// 设置监听--进行状态回调
		mPrintManager.setOnSdkStatusListener(this);
		// 指纹操作接口
		FpIf = mPrintManager;

		// 初始化 选择设备类型 和消息的句柄
		FpIf.PTInitialize(FPHWInfoData.TCS1_508_SENSOR_TYPE);
		
		// DEVICE_IMG_TYPE_1(tcs)
		isOpen = false;

		findviewinit();

	}

	void findviewinit()
	{

		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv_fingerdata = (TextView) findViewById(R.id.tv_fingerdata);
		tv_base64fingerdata = (TextView) findViewById(R.id.tv_base64fingerdata);

		edit_verify = (EditText) findViewById(R.id.edit_verify);
		edit_delete = (EditText) findViewById(R.id.edit_delete);
		edit_load = (EditText) findViewById(R.id.edit_load);

		btn_open = (Button) findViewById(R.id.btn_open);
		btn_total = (Button) findViewById(R.id.btn_total);
		btn_grab = (Button) findViewById(R.id.btn_grab);
		btn_capture = (Button) findViewById(R.id.btn_capture);
		btn_deleteall = (Button) findViewById(R.id.btn_deleteall);
		btn_guid = (Button) findViewById(R.id.btn_guid);

		btn_verify = (Button) findViewById(R.id.btn_verify);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_verifyall = (Button) findViewById(R.id.btn_verifyall);
		btn_enroll = (Button) findViewById(R.id.btn_enroll);
		btn_close = (Button) findViewById(R.id.btn_close);

		btn_load = (Button) findViewById(R.id.btn_load);
		btn_store = (Button) findViewById(R.id.btn_store);

		btexternal_verify = (Button) findViewById(R.id.btexternal_verify);

		btback = (Button) findViewById(R.id.btback);

		btn_loop_grab = (Button) findViewById(R.id.btn_loop_grab);
		btn_loop_grab_cancel = (Button) findViewById(R.id.btn_loop_grab_cancel);
		loop_grab_img = (ImageView) findViewById(R.id.loop_grab_img);

		MyOnClickListener myOnClickListener = new MyOnClickListener();

		btn_open.setOnClickListener(myOnClickListener);
		btn_total.setOnClickListener(myOnClickListener);
		btn_grab.setOnClickListener(myOnClickListener);
		btn_capture.setOnClickListener(myOnClickListener);
		btn_deleteall.setOnClickListener(myOnClickListener);
		btn_guid.setOnClickListener(myOnClickListener);

		btn_verify.setOnClickListener(myOnClickListener);
		btn_delete.setOnClickListener(myOnClickListener);
		btn_verifyall.setOnClickListener(myOnClickListener);
		btn_enroll.setOnClickListener(myOnClickListener);
		btn_close.setOnClickListener(myOnClickListener);

		btn_load.setOnClickListener(myOnClickListener);
		btn_store.setOnClickListener(myOnClickListener);

		btexternal_verify.setOnClickListener(myOnClickListener);

		btback.setOnClickListener(myOnClickListener);

		btn_loop_grab.setOnClickListener(myOnClickListener);

		// 单独做一个监听
		btn_loop_grab_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// 取消循环
				FpIf.PTCancel();
				loop_grab_cancel = false;
				
			}
		});
	}

	/**
	 * 验证输入格式是否正确
	 * 
	 * @param edit_value0
	 *            值
	 * @return -1 错误 其他为正确 数值
	 */
	public int edit_matcher(EditText edit)
	{
		String msg = null;
		if (edit.getText() == null)
		{
			// 输入错误
			msg = "请先在编辑框填写所对应的参数";
			logprint(msg, 3);
			return 0;
		}

		String edit_value = edit.getText().toString().trim();

		logprint("输入参数为:" + edit_value, 1);

		// ^[0-9]*$ ^[\\d]{1,3}$ ^[0-9]{1,3}$
		Pattern pattern = Pattern.compile("^[0-9]{1,3}$");// 只能是数字

		Matcher matcher = pattern.matcher(edit_value);// pattern.matcher(edit_id);

		if (!matcher.matches())// 字符数字
		{
			// 输入错误
			msg = "请正确输入, 格式: 纯数字";

			logprint(msg, 3);

			return 0;
		}
		else
		{
			// 输入正确：
			return Integer.parseInt(edit_value);

		}
	}

	private void opendevice()
	{
		if (!isOpen)
		{
			Toast.makeText(mContext, "正在打开设备...", 100).show();
			Thread openThread = new Thread(openRunnable);
			openThread.start();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "设备已经打开!", Toast.LENGTH_SHORT).show();
		}

	}

	private Runnable openRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			//handle_message("正在打开设备...", 1);
			// handle_message("正在打开设备...", 101);

//			try
//			{
//				// 拉高
//				ShellExe.execCommand("echo 1 > /sys/finger_apk/fingerapk");
//				Thread.sleep(1 * 1500);
//			}
//			catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			handle_message("正在打开设备...", 1);
			status = FpIf.PTOpen(id_key, enroll_num, encrypttype);
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				handle_message("打开设备成功...", 1);
			}
			else
			{
				handle_message("打开设备失败...status:" + FpStatusCode.getMessage(status), 1);
			}
		}
	};

	private void closedevice()
	{
		tv1.setText("正在关闭设备...");
		Thread closeThread = new Thread(closeRunnable);
		closeThread.start();
	}

	private Runnable closeRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			//handle_message("正在关闭设备...", 1);
//			try
//			{
//				// 拉低
//				ShellExe.execCommand("echo 0 > /sys/finger_apk/fingerapk");
//				Thread.sleep(1 * 1500);
//
//			}
//			catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			status = FpIf.PTClose();
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				handle_message("指纹设备关闭成功", 1);
				
			}
			else
			{
				handle_message("指纹设备关闭失败:err" + FpStatusCode.getMessage(status), 1);
			}
			
			isOpen = false;

		}
	};

	// 按钮监听
	private class MyOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub

			// 防止动态采集还没有关闭
			if (loop_grab_cancel != false)
			{// 必须按下取消动态采集可以进行其他操作

				Toast.makeText(mContext, "请先取消动态采集....", Toast.LENGTH_SHORT).show();

				return;
			}

			// 在设置一次标志
			loop_grab_cancel = false;

			switch (v.getId())
			{
			case R.id.btn_open:

				opendevice();

				break;

			case R.id.btn_close:

				closedevice();

				break;
			case R.id.btn_loop_grab:

				Thread loog_grab_thread = new Thread(loop_grab);
				loog_grab_thread.start();

				break;

			case R.id.btback:

				Intent intent = new Intent();
				intent.setClass(mContext, TestActivity.class);
				startActivity(intent);

				break;

			
			case R.id.btn_deleteall:

				handle_message("开始执行deleteall...", 1);

				status = FpIf.PTDeleteAllFingers();

				if (status == FpStatusCode.PT_STATUS_OK)
				{
					handle_message("指纹库删除成功", 1);

					FpIf.PTListAllFingers(mFPCount);
					tv2.setText("指纹数为:" + mFPCount[0]);
				}
				else
				{
					tv1.setText("指纹库删除失败:err" + FpStatusCode.getMessage(status));
				}

				break;

			case R.id.btn_total:

				int status = FpIf.PTListAllFingers(mFPCount);
				if (status == FpStatusCode.PT_STATUS_OK)
				{

					tv1.setText("指纹总个数:" + mFPCount[0]);
				}
				else
				{
					tv1.setText("获取指纹失败:err" + FpStatusCode.getMessage(status));
				}

				break;

			case R.id.btn_grab:

				Thread thread_g = new Thread(grab);
				thread_g.start();

				break;

			case R.id.btn_capture:

				Thread thread_cap = new Thread(capture);
				thread_cap.start();

				break;

			case R.id.btn_verify:

				verify_num_id = edit_matcher(edit_verify);
				if (verify_num_id < 1)
				{
					logprint("错误提示：ID从整数  1开始", 2);
					break;
				}

				Thread thread_verify = new Thread(verify);
				thread_verify.start();

				break;

			case R.id.btn_delete:

				delete_num_id = edit_matcher(edit_delete);

				if (delete_num_id < FPHWInfoData.INITINDEX)
				{
					handle_message("错误提示：ID从整数  "+FPHWInfoData.INITINDEX+" 开始", 101);
					break;
				}

				handle_message("开始执行delete....", 1);
				status = FpIf.PTDeleteFinger(delete_num_id);
				if (status == FpStatusCode.PT_STATUS_OK)
				{
					handle_message("指纹id=" + delete_num_id + " 删除成功", 1);
					FpIf.PTListAllFingers(mFPCount);
					tv1.setText("指纹总数:" + mFPCount[0]);
				}
				else if (status == FpStatusCode.PT_STATUS_ID_NOT_EXIST)
				{
					handle_message("不存在 " + delete_num_id + " 这个指纹id", 1);
				}
				else
				{
					handle_message("指纹删除失败:err" + FpStatusCode.getMessage(status), 1);
				}
				break;

			case R.id.btn_verifyall:

				Thread thread_verifyall = new Thread(verifyall);
				thread_verifyall.start();

				break;

			case R.id.btn_enroll:

				Thread thread_enroll = new Thread(enroll);
				thread_enroll.start();

				break;

			case R.id.btn_load:

				load_num_id = edit_matcher(edit_load);

				if (load_num_id < FPHWInfoData.INITINDEX)
				{
					handle_message("错误提示：ID从整数  "+FPHWInfoData.INITINDEX+" 开始", 101);
					break;
				}
				String str_out = null;
				
				byteload = new byte[FPHWInfoData.TEMPLATESIZE];

				status = FpIf.PTLoadFinger(byteload, load_num_id);
				if (status == FpStatusCode.PT_STATUS_OK)
				{

					tv1.setText("指纹模版读取成功");
					// 把字节专为字符串
					str_out = byte2HexString(byteload);
					tv2.setText("" + str_out);
				}
				else
				{
					byteload = null;
					tv1.setText("指纹模版读取失败:err" + FpStatusCode.getMessage(status));
				}


				break;

			case R.id.btn_store:


				if (byteload == null)
				{
					handle_message("请先读取指纹模版", 101);
					break;
				}

				status = FpIf.PTStoreFinger(byteload,mFPID);
				if (status == FpStatusCode.PT_STATUS_OK)
				{

					tv1.setText("存储 指纹 fid=" + mFPID[0] + " 成功");

					FpIf.PTListAllFingers(mFPCount);
					tv2.setText("指纹总数:" + mFPCount[0]);
				}
				else
				{
					tv1.setText("指纹存储失败:err" + FpStatusCode.getMessage(status));
				}


				break;

			// 片外比对
			case R.id.btexternal_verify:

			
				break;

			default:
				break;
			}

		}

	}

	

	/*
	 * verifyall thread
	 */
	Runnable verifyall = new Runnable()
	{

		public void run()
		{
			String msg = null;
			System.out.println("Runable->" + Thread.currentThread().getId());
			msg = "开始执行verifyall:请采集指纹...";
			handle_message(msg, 1);

			status = FpIf.PTVerifyAll(mFPID,timeout);
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				msg = "比对结束，" + "指纹库所对应的 ID 是:" + mFPID[0];
				handle_message(msg, 1);
			}
			else if (status == FpStatusCode.PT_STATUS_NOT_MATCH)
			{
				msg = "比对结束：指纹库没有你的指纹";
				handle_message(msg, 1);
			}
			else
			{
				handle_message("指纹不匹配 错误 err:" + FpStatusCode.getMessage(status), 1);
			}

		}

	};

	/*
	 * verify thread
	 */
	Runnable verify = new Runnable()
	{

		public void run()
		{
			String str_out = null;
			System.out.println("Runable->" + Thread.currentThread().getId());
			str_out = "开始执行verify:请采集指纹...";
			handle_message(str_out, 1);

			status = FpIf.PTVerify(verify_num_id, timeout);
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				str_out = "比对结束，验证的指纹是配对的";
				handle_message(str_out, 1);
			}
			else if (status == FpStatusCode.PT_STATUS_NOT_MATCH)
			{
				str_out = "比对结束：验证的指纹不配对";

				handle_message(str_out, 1);
			}
			else if (status == FpStatusCode.PT_STATUS_ID_NOT_EXIST)
			{
				handle_message("不存在 " + verify_num_id + " 这个指纹id", 1);
			}
			else
			{
				handle_message("验证的指纹 失败:err" + FpStatusCode.getMessage(status), 1);
			}

		}

	};

	/*
	 * enroll thread
	 */
	Runnable enroll = new Runnable()
	{

		public void run()
		{

			String msg = null;

			System.out.println("Runable->" + Thread.currentThread().getId());
			Log.v("MyTag", "开始执行enroll:请采集指纹...");
			msg = "开始执行enroll:请采集指纹...";
			handle_message(msg, 1);

			status = FpIf.PTEnroll(mFPID,timeout);
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				msg = "采集结束，指纹存储成功，id=" + mFPID[0];
				handle_message(msg, 1);

				FpIf.PTListAllFingers(mFPCount);
				handle_message("总指纹个数:" + Integer.toString(mFPCount[0]) + "个", 2);
			}
			else
			{

				handle_message("指纹存储 失败 err:" + FpStatusCode.getMessage(status), 1);
			}

		}

	};

	// 图像显示跳转
	protected void ShowImage(Bitmap mBitmap)
	{
		FPDisplay.mImage = mBitmap;// 这个已经预先把数据赋值到FPDisplay参数中。。。
		// / saveMyBitmap("finger",mBitmap);//保存图片
		Intent aIntent = new Intent(mContext, FPDisplay.class);
		startActivityForResult(aIntent, 0);
	}

	private Runnable loop_grab = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			System.out.println("Runable->" + Thread.currentThread().getId());
			loop_grab_cancel = true;
			
			Log.v("MyTag", "开始执行grab imager:请采集指纹...");

			byte[] FPImageData = new byte[FPHWInfoData.TCS1_508_WIDTH*FPHWInfoData.TCS1_508_HEIGHT];
			quality = 0;//不检测
			while (loop_grab_cancel)
			{
				handle_message("开始执行grab imager:请采集指纹...", 1);

				status = FpIf.PTGrab(FPImageData,timeout, quality);// 图片的 采集
				if (status == FpStatusCode.PT_STATUS_OK)
				{
					handle_message("指纹图像采集完毕", 1);

					Message message = Message.obtain();
					message.obj = FpIf.PTBitmapFromRaw(FPImageData, FPHWInfoData.TCS1_508_WIDTH);
					// 通过Handler发布携带的消息
					message.what = 36;
					messageHandler.sendMessage(message);
				}
				else
				{
					handle_message("指纹图像采集 错误 err:" + FpStatusCode.getMessage(status), 1);

					break;
				}
			}

			loop_grab_cancel = false;

		}

	};

	/*
	 * grab thread
	 */

	Runnable grab = new Runnable()
	{

		public void run()
		{
			System.out.println("Runable->" + Thread.currentThread().getId());

			String msg = null;

			int status = -1;
			handle_message("正在休眠,等待采集中。。。", 1);
			// FpIf.PTSleep(dwSleepMode, callbackCalledTimes);
			//
			// status = statusCode.GetEndCode();
			// if (status != FpStatusCode.PT_STATUS_OK)
			// {
			// handle_message("指纹图像采集 错误 err:"+FpStatusCode.getMessage(status),1);
			// return;
			// }

			Log.v("MyTag", "开始执行grab imager:请采集指纹...");
			msg = "开始执行grab imager:请采集指纹...";
			handle_message(msg, 1);
			byte[] FPImageData = new byte[FPHWInfoData.TCS1_508_WIDTH*FPHWInfoData.TCS1_508_HEIGHT];
			
			status = FpIf.PTGrab(FPImageData,timeout, quality);// 图片的 采集
			if (status == FpStatusCode.PT_STATUS_OK)
			{
				handle_message("指纹图像采集完毕", 1);

				ShowImage(FpIf.PTBitmapFromRaw(FPImageData, FPHWInfoData.TCS1_508_WIDTH));
				
			}
			else
			{
				handle_message("指纹图像采集 错误 err:" + FpStatusCode.getMessage(status), 1);

			}

		}

	};

	/**
	 * 捕捉指纹模版
	 */
	Runnable capture = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			System.out.println("Runable->" + Thread.currentThread().getId());

			String msg = null;
			String str_out = null;

			Log.v("MyTag", "开始执行capture:请采集指纹...");
			msg = "开始执行capture:请采集指纹...";
			handle_message(msg, 1);
			byteload = new byte[FPHWInfoData.TEMPLATESIZE];
			
			status = FpIf.PTCapture(byteload,timeout);// 图片的 采集
			if (status != FpStatusCode.PT_STATUS_OK)
			{
				handle_message("指纹图像采集 错误 err:" + FpStatusCode.getMessage(status), 1);
				byteload = null;
			}
			else
			{
				Log.v("MyTag", "capture finger data success！");
				handle_message("指纹模版数据提取完成!", 1);

				// 把字节专为字符串
				str_out = byte2HexString(byteload);
				handle_message("原始数据：" + str_out, 2);

				// 使用base64编码对字节转码
				str_out = Base64.encodeToString(byteload, Base64.DEFAULT);

				handle_message(str_out, 3);

				// 对base64字符串解码
				byte b[] = Base64.decode(str_out, Base64.DEFAULT);
				str_out = byte2HexString(b);
				handle_message(str_out, 4);

				byteload = null;
			}

		}
	};

	/**
	 * 
	 * byte[]转换成字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2HexString(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		int length = b.length;
		for (int i = 0; i < length; i++)
		{
			String stmp = Integer.toHexString(b[i] & 0xff);
			if (stmp.length() == 1)
				sb.append("0" + stmp);
			else
				sb.append(stmp);
		}
		return sb.toString();
	}

	/**
	 * Log print
	 * 
	 * @param msg
	 * @param type
	 *            0=log 1=settext 2=maketext 3=settext,maketext
	 */
	void logprint(String msg, int type)
	{
		switch (type)
		{
		case 0:
			if (D)
				Log.d(TAG, msg);
			break;
		case 1:
			tv1.setText(msg);// 操作提示
			break;
		case 2:
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();// active
			break;
		case 3:
			tv1.setText(msg);// 操作提示
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();// active
			break;

		default:
			break;
		}

	}

	public void handle_message(String str, int num)
	{
		Message message = Message.obtain();
		message.obj = str;
		// 通过Handler发布携带的消息
		message.what = num;
		messageHandler.sendMessage(message);
	}

	// 子类化一个Handler

	public class MessageHandler extends Handler
	{

		public MessageHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
			case 1:// 操作提示--提示一
				tv1.setText("" + msg.obj.toString());
				break;
			case 2:// 指纹个数--提示二
				tv2.setText("" + msg.obj.toString());
				break;

			case 3:// base64指纹数据
				tv_base64fingerdata.setText("" + msg.obj.toString());
			case 4:// 二进制指纹数据
				tv_fingerdata.setText("" + msg.obj.toString());
				break;
			case 36:// loop_grab_imgview

				if (msg.obj == null)
				{
					return;
				}

				// bitmapData
				Drawable mDrawable = new BitmapDrawable((Bitmap) msg.obj);

				loop_grab_img.setBackgroundDrawable(mDrawable);
				break;
			case 101:
				Toast.makeText(getApplicationContext(), "" + msg.obj.toString(), 100).show();
				break;
			}
		}
	}
	
	public void onBackPressed()
	{
		//Toast.makeText(getApplicationContext(), "onBackPressed......", 100).show();
		closedevice();
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		Log.i(TAG, "onNewIntent");

		super.onNewIntent(intent);

	}// 这个作用是当已经存在，重新点亮以后直接在产生一个替代本身,

	public void onStart()
	{
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

	}

	// 开始蓝牙配置
	@Override
	public void onPause()
	{
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");

	}

	@Override
	public void onStop()
	{
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	public void onDestroy()
	{
		super.onDestroy();
		if (D) Log.e(TAG, "-- ON onDestroy --");
		System.exit(0);
	}

	/**
	 * 采集进度 回调
	 * 
	 * @param dwGuiState
	 *            指纹操作处理中的状态码
	 * @param byProgress
	 *            采集进度 (当使用gui采集的时候有用)
	 */
	@Override
	public void OnHALCallback(int dwGuiState, int byProgress)
	{
		// TODO Auto-generated method stub
		Log.d(TAG, "OnHALCallback-- dwGuiState="+dwGuiState+" byProgress:"+byProgress);
		// 可以查看底层采集信息
		handle_message(PTCommCallback.getCallbackmsg(dwGuiState), 2);
	}

}
