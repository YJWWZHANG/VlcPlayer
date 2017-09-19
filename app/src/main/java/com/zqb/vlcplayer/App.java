package com.zqb.vlcplayer;


import android.app.Application;

import com.blankj.utilcode.util.Utils;

import org.videolan.libvlc.VlcPlayer;

public class App extends Application {

    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Utils.init(this);
        VlcPlayer.init(this);
    }
    public static App getInstance() {
        return mApp;
    }

}
