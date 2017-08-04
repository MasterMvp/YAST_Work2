package attendance.yn.a606a.finger.activity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import attendance.yn.a606a.R;

public class FPDisplay extends Activity
{
	public static final int FPDR_NONE = 0;
	public static final int FPDR_PASS = 1;
	public static Bitmap mImage = null;
	public static String msTitle = null;
	private ImageView mImgv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fpdisplay);
		Button passButton = (Button) findViewById(R.id.pass);

		mImgv = (ImageView) this.findViewById(R.id.img);

		if (mImage != null)
		{
			mImgv.setImageBitmap(mImage);
		}
		if (msTitle != null)
		{
			this.setTitle(msTitle);
		}

		passButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				setResult(FPDR_PASS);
				finish();
			}
		});
	}

}
