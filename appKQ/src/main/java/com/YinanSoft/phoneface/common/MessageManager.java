package com.YinanSoft.phoneface.common;

import android.os.Handler;
import android.os.Message;

/**
 * Created by wangzhi on 2015/12/4.
 */
public class MessageManager {

    public static void sendToTarget(Handler handler, int what) {
        Message msg = Message.obtain(handler, what);
        msg.sendToTarget();
    }

    public static void sendToTarget(Handler handler, int what, Object obj) {
        Message msg = Message.obtain(handler, what, obj);
        msg.sendToTarget();
    }
}
