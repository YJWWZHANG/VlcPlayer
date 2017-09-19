package org.videolan.libvlc;


import android.annotation.SuppressLint;
import android.content.Context;

public final class VlcPlayer {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private VlcPlayer() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(Context context) {
        VlcPlayer.context = context.getApplicationContext();
    }

    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

}
