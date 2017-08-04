package yn.finger.ly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 身份证图像处理工具类
 *
 * @author Administrator
 */
public class CertImgDisposeUtils {

	private Context mContext = null;

	// private MD5 md5;

	public CertImgDisposeUtils() {

	}

	public CertImgDisposeUtils(Context mCtx) {
		this.mContext = mCtx;
	}

	// /** 获取证件图片 **/
	// public void initGoalView(ImageView frontView, ImageView backView) {
	// this.viewFront = frontView;
	// this.viewback = backView;
	// }

	/**
	 * TODO 正面合成
	 *
	 * @param //decodeInfo 需要合成的数据
	 * @throws IOException 错误
	 */
	@SuppressLint("NewApi")
	public Bitmap creatBitmap(IDCardInfo idCardInfo)
			throws IOException {
		Bitmap bitmap = null;
		Paint mPaint = new Paint();

		InputStream inputStream=mContext.getResources().getAssets().open("zm.png");
		Bitmap bmpF = BitmapFactory.decodeStream(inputStream);
		int FcvWidth = bmpF.getWidth();
		int FcvHeight = bmpF.getHeight();
		Bitmap newbmpF = Bitmap.createBitmap(FcvWidth, FcvHeight,
				Config.ARGB_8888);
		float FontMultf = (float) (FcvHeight * FcvHeight + FcvWidth * FcvWidth)
				/ (float) (329 * 329 + 210 * 210);
		FontMultf = (float) Math.sqrt(FontMultf);

		float xMultF = 1;
		float yMultF = 1;
		Canvas Fcv = new Canvas(newbmpF);
		Fcv.drawBitmap(bmpF, 0, 0, mPaint);
		int bsC = Color.argb(0, 71, 95, 155);
		Paint Fp = new Paint(Paint.ANTI_ALIAS_FLAG);
		Fp.setTextAlign(Paint.Align.LEFT);
		Fp.setColor(bsC);
		Fp.setAntiAlias(true);
		Fp.setDither(true);
		Fp.setAlpha(255);
		Fp.setStyle(Paint.Style.FILL);
		Fp.setTextSize(13 * FontMultf);
		Rect src=new Rect();
		//		Fcv.drawText("姓  名", 23 * xMultF, 39 * yMultF, Fp);
//		Fcv.drawText("性  别", 23 * xMultF, 66 * yMultF, Fp);
//		Fcv.drawText("民  族", 98 * xMultF, 66 * yMultF, Fp);
//		Fcv.drawText("出  生", 23 * xMultF, 92 * yMultF, Fp);
//		Fcv.drawText("年", 98 * xMultF, 92 * yMultF, Fp);
//		Fcv.drawText("月", 131 * xMultF, 92 * yMultF, Fp);
//		Fcv.drawText("日", 165 * xMultF, 92 * yMultF, Fp);
//		Fcv.drawText("住  址", 23 * xMultF, 115 * yMultF, Fp);
//		Fcv.drawText("公民身份号码", 23 * xMultF, 184 * yMultF, Fp);

		//Fcv.drawBitmap(resizeImage(idCardInfo.getPhoto(),128), 220 * xMultF, 28 * yMultF, Fp);
		Paint imagePaint = new Paint();
		imagePaint.setFilterBitmap(true);
		// 去锯齿
		imagePaint.setAntiAlias(true);
		// 防抖动
		imagePaint.setDither(true);
//		        Bitmap bitmp = BitmapFactory
//						.decodeByteArray(per.getPhoto(), 0, per.Photo.length);
		Bitmap bitmp =resizeImage(idCardInfo.getPhoto(),125).copy(Config.ARGB_8888, true);//照片的高
		//	Bitmap bitmp =idCardInfo.getPhoto().copy(Config.ARGB_8888, true);

		int w=bitmp.getWidth();
		int h=bitmp.getHeight();
		try
		{
			for (int i = 0; i<h; i++)
			{
				for(int j=0;j<w;j++)
				{
					//Log.e("xxx","i="+i+"---j="+j+"\nbmpF.w="+ bmpF.getWidth() + "--bmpF.h="+bmpF.getHeight());
					int i1 = bitmp.getPixel(j, i);
					int i2 = Color.red(i1);
					int i3 = Color.green(i1);
					int i4 = Color.blue(i1);
					if ((i2 <= 245) || (i3 <= 245) || (i4 <= 245))
						continue;

					if(Build.MODEL.toUpperCase().equals("U8000S")){
						bitmp.setPixel(j, i,bmpF.getPixel(j + 255, i+ 23));
					} else bitmp.setPixel(j, i,bmpF.getPixel(j + 255, i+ 23));

				}
			}
			// saveMyBitmap(bitmp, "/sdcard/touxiang555.jpg");
			// imagePaint.setXfermode(new PixelXorXfermode(Color.argb(0,254, 254, 254)));//));//bitmp.getPixel(1, 1)过滤白色背景

			//
			if(Build.MODEL.toUpperCase().equals("U8000S")) {
				Fcv.drawBitmap(bitmp, 285 * xMultF, 30 * yMultF, imagePaint);
			} else Fcv.drawBitmap(bitmp,285 * xMultF, 25 * yMultF,imagePaint);







		}

		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		Fp.setColor(Color.BLACK);
		Fp.setTextSize(14 * FontMultf);
		Fcv.drawText(idCardInfo.getName(), 79 * xMultF, 50 * yMultF, Fp);
		Fp.setTextSize(13 * FontMultf);
		Fcv.drawText(idCardInfo.getGender(), 84 * xMultF, 84 * yMultF, Fp);
		Fcv.drawText(idCardInfo.getNation(), 190 * xMultF, 87 * yMultF, Fp);
		Fcv.drawText(idCardInfo.getBirthday().substring(0, 4), 83 * xMultF, 122 * yMultF,
				Fp);
		if (idCardInfo.getBirthday().substring(4, 5).endsWith("0")) {
			Fcv.drawText(idCardInfo.getBirthday().substring(5, 6), 165 * xMultF,
					122 * yMultF, Fp);
		} else {
			Fcv.drawText(idCardInfo.getBirthday().substring(4, 6), 165 * xMultF,
					122 * yMultF, Fp);
		}
		if (idCardInfo.getBirthday().substring(6, 7).endsWith("0")) {
			Fcv.drawText(idCardInfo.getBirthday().substring(7, 8), 212 * xMultF,
					122 * yMultF, Fp);
		} else {
			Fcv.drawText(idCardInfo.getBirthday().substring(6, 8), 212 * xMultF,
					122 * yMultF, Fp);
		}

		String straddr = idCardInfo.getAddress();
		if (straddr.length() > 22) {
			Fcv.drawText(straddr.substring(0, 11), 80 * xMultF, 160 * yMultF,
					Fp);
			Fcv.drawText(straddr.substring(11, 22), 80 * xMultF, 181 * yMultF,
					Fp);
			Fcv.drawText(straddr.substring(22, straddr.length()), 80 * xMultF,203 * yMultF,
					Fp);
		} else {
			if (straddr.length() > 11) {
				Fcv.drawText(straddr.substring(0, 11), 80 * xMultF,163 * yMultF,
						Fp);
				Fcv.drawText(straddr.substring(11, straddr.length()),80 * xMultF, 181 * yMultF,
						Fp);
			} else {
				Fcv.drawText(straddr.substring(0, straddr.length()),80 * xMultF, 160 * yMultF,
						Fp);
			}
		}

		Fp.setTextSize(14 * FontMultf);
		Fp.setTypeface(Typeface.DEFAULT_BOLD);
		Fp.setStrokeWidth(3f);
		Fp.setTextScaleX(1.2f);
		Fcv.drawText(idCardInfo.getCardNum(), 139 * xMultF, 230 * yMultF, Fp);

		Fcv.save(Canvas.ALL_SAVE_FLAG);
		Fcv.restore();
		Fcv.save(Canvas.ALL_SAVE_FLAG);
		Fcv.restore();

		// 合成背面照片

//		Bitmap bmpB = BitmapFactory.decodeResource(mContext.getResources(),
//				R.mipmap.cert_back);
//		Bitmap newbmpB = Bitmap.createBitmap(bmpB.getWidth(), bmpB.getHeight(),
//				Config.ARGB_8888);
//		int BcvWidth = bmpF.getWidth();
//		int BcvHeight = bmpF.getHeight();
//
//		float FontMultB = (float) (BcvWidth * BcvWidth + BcvHeight * BcvHeight)
//				/ (float) (329 * 329 + 210 * 210);
//		FontMultB = (float) Math.sqrt(FontMultB);
//		float xMultB = (float) BcvWidth / 329f;
//		float yMultB = (float) BcvHeight / 210f;
//
//		Canvas Bcv = new Canvas(newbmpB);
//		Bcv.drawBitmap(bmpB, 0, 0, mPaint);
//
//		Fp.reset();
//		Fp.setTextAlign(Paint.Align.LEFT);
//		Fp.setColor(Color.BLACK);
//		Fp.setAntiAlias(true);
//		Fp.setDither(true);
//		Fp.setAlpha(255);
//		Fp.setStyle(Paint.Style.FILL);
//		Fp.setTextSize(13 * FontMultB);
//		Fp.setTypeface(Typeface.DEFAULT_BOLD);
//
//		Bcv.drawText("签发机关", 68 * xMultB, 169 * yMultB, Fp);
//		Bcv.drawText("有效期限", 68 * xMultB, 193 * yMultB, Fp);
//
//		Fp.setTypeface(Typeface.DEFAULT);
//		Bcv.drawText(idCardInfo.getRegistInstitution(), 130 * xMultB, 169 * yMultB, Fp);
//		if (idCardInfo.getValidEndDate().contains("长期") || idCardInfo.getValidEndDate().contains("长") || idCardInfo.getValidEndDate().contains("永久") || idCardInfo.getValidEndDate().contains("永") || idCardInfo.getValidEndDate().contains("长期有效") || idCardInfo.getValidEndDate().contains("效")) {
//			Bcv.drawText(
//					idCardInfo.getValidStartDate().substring(0, 4) + "."
//							+ idCardInfo.getValidStartDate().substring(4, 6) + "."
//							+ idCardInfo.getValidStartDate().substring(6, 8) + "-"
//							+ idCardInfo.getValidEndDate(), 130 * xMultB, 193 * yMultB, Fp);
//			Log.e("aa", idCardInfo.getValidStartDate().substring(0, 4) + "."
//					+ idCardInfo.getValidStartDate().substring(4, 6) + "."
//					+ idCardInfo.getValidStartDate().substring(6, 8) + "-"
//					+ idCardInfo.getValidEndDate());
//		} else {
//			Bcv.drawText(
//					idCardInfo.getValidStartDate().substring(0, 4) + "."
//							+ idCardInfo.getValidStartDate().substring(4, 6) + "."
//							+ idCardInfo.getValidStartDate().substring(6, 8) + "-"
//							+ idCardInfo.getValidEndDate().substring(0, 4) + "."
//							+ idCardInfo.getValidEndDate().substring(4, 6) + "."
//							+ idCardInfo.getValidEndDate().substring(6, 8), 130 * xMultB,
//					193 * yMultB, Fp);
//		}
//
//		Bcv.save(Canvas.ALL_SAVE_FLAG);
//		Bcv.restore();



		return newbmpF;
	}

	/**
	 * TODO 合成图片
	 *
	 * @param frontBitmap 证件正面位图
	 * @param backBitmap  证件背面位图
	 * @return 合成后的位图
	 */
	private Bitmap compositeImages(Bitmap frontBitmap, Bitmap backBitmap) {
		Bitmap bmp = null;
		Bitmap desBitmap = getDescribeBitmap(frontBitmap.getWidth());
		// 创建一个空的Bitmap
		bmp = Bitmap.createBitmap(
				frontBitmap.getWidth(),
				frontBitmap.getHeight() + backBitmap.getHeight()
						+ desBitmap.getHeight(), frontBitmap.getConfig());
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bmp);
		// 绘制第一张图片
		canvas.drawBitmap(frontBitmap, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
		canvas.drawBitmap(backBitmap, 0, frontBitmap.getHeight(), paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
		canvas.drawBitmap(desBitmap, 0,
				frontBitmap.getHeight() + backBitmap.getHeight(), paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
		return bmp;
	}

	/**
	 * 创建证件信息下方的描述图片
	 *
	 * @param BitmapWidth
	 * @param //clientManager
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private Bitmap getDescribeBitmap(int BitmapWidth) {
		Bitmap describeBitmap = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy年MM月dd日   HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());// 获取当前时间 String
			String ShowTime = "创建时间：" + dateFormat.format(date);
			int bitmapWidth = BitmapWidth;
			int bitmapHeight = 100;
			// 新建一个新的输出图片
			describeBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(describeBitmap);
			// 新建一个矩形
			RectF outerRect = new RectF(0, 0, bitmapWidth, bitmapHeight);
			// 产生一个红色的圆角矩形 或者任何有色颜色，不能是透明！
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(outerRect, 0, 0, paint);
			paint.setTextSize(20.0f);
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(ShowTime, 10, 30, paint);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
//			canvas.drawText(
//					"操作人员："
//							+ new SharedPreferencesUtil(mContext).getUserInfo()
//									.get(SharedPreferencesUtil.AccountName),
//					10, 55, paint);// 绘制上去字，开始未知x,y采用那只笔绘制
//			canvas.save(Canvas.ALL_SAVE_FLAG);
//			canvas.restore();
//			canvas.drawText(
//					"人员姓名："
//							+ new SharedPreferencesUtil(mContext).getUserInfo()
//									.get(SharedPreferencesUtil.AccountId), 10,
//					80, paint);// 绘制上去字，开始未知x,y采用那只笔绘制
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return describeBitmap;
	}

	public static String bitmaptoString(Bitmap bitmap) {


		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 40, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;


	}

	public static Bitmap convertStringToIcon(String st) {
		// OutputStream out;
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(st, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}



	/**
	 * 按比例缩放图片
	 *
	 * @param bitmap
	 * @param w  需要的宽度
	 * @return
	 */
	public Bitmap resizeImage(Bitmap bitmap, int w) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;

		float scaleWidth = ((float) newWidth) / width;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);

		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	private void saveCroppedImage(Bitmap bmp) {
		//File file = new File("/storage/1.JPG");
//		if (!file.exists())
//			file.mkdir();

		File file = new File("/sdcard/1.JPG".trim());
		//	String fileName = file.getName();
//		String mName = fileName.substring(0, fileName.lastIndexOf("."));
//		String sName = fileName.substring(fileName.lastIndexOf("."));

		//	String newFilePath = "/storage/emulated/0/myFolder" + "/" + mName + "_cropped" + sName;
		//	file = new File();
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}