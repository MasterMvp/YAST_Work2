package attendance.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.YinanSoft.CardReaders.IDCardInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import attendance.yn.a606a.R;
import attendance.yn.a606a.utils.CertImgDisposeUtils;
import attendance.yn.a606a.bean.UserBean;
import attendance.yn.a606a.sqlite.DBManager;

/**
 * Created by Administrator on 2017/5/3.
 */

public class PersonAct extends Activity {
    private ImageView img, left, right;
    private TextView result1, result2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.toUpperCase().contains("JWZD-500")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.query_person);

        img = (ImageView) findViewById(R.id.person_img);
        left = (ImageView) findViewById(R.id.person_left);
        right = (ImageView) findViewById(R.id.person_right);
        result1 = (TextView) findViewById(R.id.person_result1);
        result2 = (TextView) findViewById(R.id.person_result2);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        List<UserBean> userBeanList;
        DBManager dbManager = null;
        if (dbManager == null) {
            dbManager = DBManager.getInstance(PersonAct.this);
        }
        userBeanList = dbManager.selectUser();

        UserBean userBean = userBeanList.get(position);


        IDCardInfo info = new IDCardInfo();
        info.setName(userBean.getName());
        info.setCardNum(userBean.getCardNum());
        info.setGender(userBean.getGender());
        info.setNation(userBean.getNation());
        info.setBirthday(userBean.getBirthday());
        info.setAddress(userBean.getAddress());
        Bitmap bitmap = CertImgDisposeUtils.convertStringToIcon(userBean.getIdphoto());
        info.setPhoto(bitmap);

        Bitmap colorImg = CertImgDisposeUtils.convertStringToIcon(userBean.getPhoto());
        Bitmap blackImg = CertImgDisposeUtils.convertStringToIcon(userBean.getPhoto1());
        try {
            File file = new File("/storage/emulated/0/1.JPG".trim());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
//            colorImg.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            blackImg.compress(Bitmap.CompressFormat.JPEG, 100,fos);
            fos.flush();
        } catch (Exception e) {
            Log.e("Log", "保存图片出错" + e.getMessage());
            e.printStackTrace();
        }
        left.setImageBitmap(colorImg);
        right.setImageBitmap(blackImg);

        CertImgDisposeUtils certimg = new CertImgDisposeUtils(this);

        try {
            Bitmap bitmap1 = certimg.creatBitmap(info);
            img.setImageBitmap(bitmap1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String msg = userBean.getPassword();
        if (msg.equals("") || msg == null) {
            msg = "无失信记录";
        }
        result1.setText(msg);
        result2.setText("核验时间:" + userBean.getCreateTime());

    }


}
