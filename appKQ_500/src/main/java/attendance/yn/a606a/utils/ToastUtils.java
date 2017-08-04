package attendance.yn.a606a.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/29.
 */
public class ToastUtils {
        public static void showToast(Activity context,String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        public static void showToast(Activity context,int resId) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
