package com.whzxw.uface.ether.utils;

import android.media.AudioAttributes;
import android.media.SoundPool;

import com.uniubi.uface.ether.R;
import com.whzxw.uface.ether.EtherApp;

/**
 * 语音助手
 */
public class Voiceutils {

    private static int sound;
    private static int soundReco;
    private static SoundPool mSoundPoll;

    public static final void init() {
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build() ;
        mSoundPoll = new SoundPool.Builder()
                .setMaxStreams(100)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        sound = mSoundPoll.load(EtherApp.context, R.raw.sound, 1);
        soundReco = mSoundPoll.load(EtherApp.context, R.raw.sound_reco, 1);
    }

    public static final void playRecoOver() {
        if (mSoundPoll != null)
            mSoundPoll.play(sound, 1,1,1,0,1);
    }

    public static final void playPlayStartReco() {
        if (mSoundPoll != null)
            mSoundPoll.play(soundReco, 1,1,1,0,1);
    }
}
