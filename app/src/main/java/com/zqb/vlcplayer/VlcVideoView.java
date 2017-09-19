package com.zqb.vlcplayer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Util;

import static android.content.res.Configuration.*;

public class VlcVideoView extends SurfaceView implements SurfaceHolder.Callback, IVideoPlayer {
    private final static String TAG = "SyVideoView";
    private String videoPath = null;
    private int mVideoHeight;
    private int mVideoWidth;
    private int mSarDen;
    private int mSarNum;
    private LibVLC mLibVLC = null;
    public videoStatue statue;

    public interface videoStatue {
        void playOver();
    }

    public VlcVideoView(Context context) {
        super(context);

    }

    public VlcVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().setFormat(PixelFormat.RGBX_8888);
        getHolder().addCallback(this);
        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);
        try {
            mLibVLC = Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
        }
    }

    public VlcVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setVideoPath(String path) {
        videoPath = "file://" + path;
    }

    public void playVideo() {
        if (mLibVLC != null) {

            if (videoPath != null) {
                mLibVLC.playMyMRL(videoPath);
            }

        }

    }

    public void resumeVideo() {
        if (mLibVLC != null) {
            mLibVLC.play();
        }
    }

    public void pauseVideo() {
        if (mLibVLC != null) {
            mLibVLC.pause();
        }
    }


    public void stopVideo() {
        if (mLibVLC != null) {
            mLibVLC.stop();
        }
    }

    public boolean isPlaying() {
        return mLibVLC.isPlaying();
    }

    public long getTime() {
        long time = -1;
        if (mLibVLC != null) {
            time = mLibVLC.getTime();
        }
        return time;
    }

    public void setTime(long time) {
        if (mLibVLC != null) {
            mLibVLC.setTime(time);
        }
    }

    public float getPosition() {
        float pos = -1;
        if (mLibVLC != null) {
            pos = mLibVLC.getPosition();
        }
        return pos;
    }

    public void setPosition(float pos) {
        if (mLibVLC != null) {
            mLibVLC.setPosition(pos);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mLibVLC.detachSurface();
        if (mLibVLC != null) {
            mLibVLC.stop();
        }
        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);
    }

    // Called when the surface is resized
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mLibVLC.attachSurface(holder.getSurface(), this);
    }


    Handler eventHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerPlaying:
                    Log.i(TAG, "MediaPlayerPlaying");
                    break;
                case EventHandler.MediaPlayerPaused:
                    Log.i(TAG, "MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.i(TAG, "MediaPlayerStopped");
                    break;
                case EventHandler.MediaPlayerEndReached:
                    Log.i(TAG, "MediaPlayerEndReached");
                    break;
                case EventHandler.MediaPlayerVout:
                    if (msg.getData().getInt("data") <= 0) {
                        Log.d(TAG, "play over");
                        if (statue != null) {
                            statue.playOver();
                        }
                    }
                    break;
                case 8888:
                    Log.e(TAG, "eventHandler");
                    changeSurfaceSize();
                    break;
                default:
                    Log.d(TAG, "Event not handled");
                    break;

            }
            super.handleMessage(msg);
        }

    };

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 8888:
                    changeSurfaceSize();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void changeSurfaceSize() {
        // get screen size
        int dw = getWidth();
        int dh = getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
        if (dw > dh && isPortrait || dw < dh && !isPortrait) {
            int d = dw;
            dw = dh;
            dh = d;
        }
        if (dw * dh == 0)
            return;
        // compute the aspect ratio
        double ar, vw;
        double density = (double) mSarNum / (double) mSarDen;
        getHolder().setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        setLayoutParams(lp);
        invalidate();
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width,
                               int visible_height, int sar_num, int sar_den) {
        Log.e(TAG, "setSurfaceSize!!");
        mVideoHeight = height;
        mVideoWidth = width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(8888);
        mHandler.sendMessage(msg);
    }


}
