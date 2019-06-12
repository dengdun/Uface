package com.uniubi.uface.ether.utils;

import android.content.Context;

import com.uniubi.uface.ether.EtherApp;

/**
 * 音量管理
 */
public class AudioUtils {

    /**
     * 设置音量最大
     */
    public static final void setMaxVolum() {

        // 设置音量最大 音量最大
        android.media.AudioManager audioManager = (android.media.AudioManager) EtherApp.context.getSystemService(Context.AUDIO_SERVICE);
        // 获取最大的音量
        int streamMaxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_SYSTEM);
        audioManager.setStreamVolume(android.media.AudioManager.STREAM_SYSTEM, streamMaxVolume, android.media.AudioManager.FLAG_PLAY_SOUND);
    }
}
