package attendance.yn.a606a.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

import attendance.yn.a606a.R;

public class SoundPoolAudioClip {
    private SoundPool soundpool;
    private int musicID;
    private Map<Integer, Integer> mapSRC;

    public static enum SoundIndex {
        none, di, fingerdown, fingerup, validface, validok, validfail, look, fringerfailure,fringersuccess,optionsuccess,thanks
    }

    public SoundPoolAudioClip(Context ctx) {
        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
//			musicID = soundpool.load(ctx, resID, 1);
        mapSRC = new HashMap<Integer, Integer>();
        mapSRC.put(1, soundpool.load(ctx, R.raw.di, 0));
        mapSRC.put(2, soundpool.load(ctx, R.raw.fingerdown, 0));
        mapSRC.put(3, soundpool.load(ctx, R.raw.fingerup, 0));
        mapSRC.put(4, soundpool.load(ctx, R.raw.validface, 0));
        mapSRC.put(5, soundpool.load(ctx, R.raw.validok, 0));
        mapSRC.put(6, soundpool.load(ctx, R.raw.validfail, 0));
        mapSRC.put(7, soundpool.load(ctx, R.raw.look, 0));//请目视摄像头
        mapSRC.put(8, soundpool.load(ctx, R.raw.fringerfailure, 0));//指纹比对失败
        mapSRC.put(9, soundpool.load(ctx, R.raw.fringer_success, 0));//指纹采集成功
        mapSRC.put(10, soundpool.load(ctx, R.raw.option_success, 0));//录入成功
        mapSRC.put(11, soundpool.load(ctx, R.raw.thanks, 0));//谢谢
    }

    public synchronized void play(SoundPoolAudioClip.SoundIndex soundIndex) {
        int nResult;
        if (soundpool != null) {
            nResult = soundpool.play(soundIndex.ordinal(),// 播放的声音资源
                    1.0f,// 左声道，范围为0--1.0
                    1.0f,// 右声道，范围为0--1.0
                    0, // 优先级，0为最低优先级
                    0,// 循环次数,0为不循环
                    1);// 播放速率，1为正常速率

        }
    }

    public void release() {
        if (soundpool != null) {
            soundpool.release();
        }
    }
}
