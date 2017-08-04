package attendance.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.YinanSoft.CardReaders.IDCardInfo;

import java.io.IOException;
import java.util.List;

import attendance.yn.a606a.R;
import attendance.yn.a606a.Utils.CertImgDisposeUtils;
import attendance.yn.a606a.bean.UserBean;
import attendance.yn.a606a.sqlite.DBManager;

/**
 * Created by Administrator on 2017/5/3.
 */

public class PersonAct extends Activity {
    private ImageView img, left, right;
    private TextView result1, result2, info1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_person);
        img = (ImageView) findViewById(R.id.person_img);
        left = (ImageView) findViewById(R.id.person_left);
        right = (ImageView) findViewById(R.id.person_right);
        result1 = (TextView) findViewById(R.id.person_result1);
        result2 = (TextView) findViewById(R.id.person_result2);
        info1 = (TextView) findViewById(R.id.info);

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
        Log.e("Person:", "" + msg);
        if ("无失信记录".equals(msg)) {
            info1.setTextColor(Color.GREEN);
            info1.setText(msg);
            result1.setVisibility(View.INVISIBLE);
        } else {
            info1.setTextColor(Color.RED);
            info1.setText("有失信记录");
            result1.setVisibility(View.VISIBLE);
        }
        result1.setText("失信详情:（" + msg + "）");
        result2.setText("核验时间:" + userBean.getCreateTime());
    }
}
