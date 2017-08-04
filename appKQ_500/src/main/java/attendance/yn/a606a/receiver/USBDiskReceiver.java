package attendance.yn.a606a.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.YinanSoft.Utils.ToastUtil;

/**
 * Created by Administrator on 2017/5/8.
 */

public class USBDiskReceiver extends BroadcastReceiver {
    public static String UsbFile = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)) {
            Log.d("usb", "U盘de 地址" + path);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                UsbFile = "";
                Log.d("usb", "U盘拔出");
                ToastUtil.showToast(context, "U盘拔出");
            }
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                Log.d("usb", "U盘插入");
                ToastUtil.showToast(context, "U盘插入");
                UsbFile = path;
            }
        }


    }
}
