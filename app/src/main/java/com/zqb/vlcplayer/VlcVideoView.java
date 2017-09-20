package com.zqb.vlcplayer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Util;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class VlcVideoView extends SurfaceView implements SurfaceHolder.Callback, IVideoPlayer {
    private String mVideoPath;
    private int mVideoHeight;
    private int mVideoWidth;
    private int mSarDen;
    private int mSarNum;
    private LibVLC mLibVLC;
    private OnCompletionListener mOnCompletionListener;

    Handler mEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerPlaying:
                    break;
                case EventHandler.MediaPlayerPaused:
                    break;
                case EventHandler.MediaPlayerStopped:
                    break;
                case EventHandler.MediaPlayerEndReached:
                    break;
                case EventHandler.MediaPlayerVout:
                    if (msg.getData().getInt("data") <= 0) {
                        if (mOnCompletionListener != null) {
                            mOnCompletionListener.onCompletion();
                        }
                    }
                    break;
                case 8888:
                    changeSurfaceSize();
                    break;
                default:
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

    public VlcVideoView(Context context) {
        this(context, null);
    }

    public VlcVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VlcVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().setFormat(PixelFormat.RGBX_8888);
        getHolder().addCallback(this);
        EventHandler em = EventHandler.getInstance();
        em.addHandler(mEventHandler);
        try {
            mLibVLC = Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
        }
    }

    public void setPath(String path) {
        mVideoPath = "file://" + path;
    }

    public void start() {
        if (mLibVLC != null) {
            if (mVideoPath != null) {
                mLibVLC.playMyMRL(mVideoPath);
            }
        }
    }

    public void resume() {
        if (mLibVLC != null) {
            mLibVLC.play();
        }
    }

    public void pause() {
        if (mLibVLC != null) {
            mLibVLC.pause();
        }
    }

    public void stop() {
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

    public long getLength() {
        long len = -1;
        if (mLibVLC != null) {
            len = mLibVLC.getLength();
        }
        return len;
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
        em.removeHandler(mEventHandler);
    }

    // Called when the surface is resized
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mLibVLC.attachSurface(holder.getSurface(), this);
    }

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
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        mVideoHeight = height;
        mVideoWidth = width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(8888);
        mHandler.sendMessage(msg);
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    public interface OnCompletionListener {
        void onCompletion();
    }

}
