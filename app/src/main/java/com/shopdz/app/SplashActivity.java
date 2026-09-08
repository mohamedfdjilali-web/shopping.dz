package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // إزالة أي انتقالات أو خلفيات سوداء
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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

        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);
            mp.setLooping(false);

            // تشغيل الفيديو فورًا
            videoView.start();
        });

        videoView.setOnCompletionListener(mp -> {
            openMain();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            openMain();
            return true;
        });
    }

    private void openMain() {
        if (opened) return;

        opened = true;

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
