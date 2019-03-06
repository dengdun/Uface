package com.uniubi.uface.etherdemo.bean;

/**
 * 用来传递屏幕保护消息的
 */
public  class ScreenSaverMessageEvent {
    public ScreenSaverMessageEvent(boolean isScreenSaver) {
        this.isScreenSaver = isScreenSaver;
    }

    /**
     * 是否开启屏保
     */
    public boolean isScreenSaver;
}
