package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.splashVideo);

        Uri videoUri = Uri.parse(
                "android.resource://" +
                getPackageName() +
                "/" +
                R.raw.splash
        );

        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0f, 0f);
                mp.setLooping(false);

                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                openWebsite();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                // لا نغلق التطبيق عند حدوث خطأ
                // نفتح الموقع مباشرة
                openWebsite();

                return true;
            }
        });
    }

    private void openWebsite() {

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        overridePendingTransition(0, 0);

        finish();
    }

    @Override
    protected void onDestroy() {

        if (videoView != null) {
            videoView.stopPlayback();
        }

        super.onDestroy();
    }
}
