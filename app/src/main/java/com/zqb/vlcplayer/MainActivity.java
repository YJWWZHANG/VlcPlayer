package com.zqb.vlcplayer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vlc_video_view)
    VlcVideoView mVlcVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mVlcVideoView.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/IISFREE_VIDEO/F.mp4");
        mVlcVideoView.playVideo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
