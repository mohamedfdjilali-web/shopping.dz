package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView splashVideo;

    private final Handler handler = new Handler();

    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_splash);

        splashVideo = findViewById(R.id.splashVideo);

        Uri videoUri = Uri.parse(
                "android.resource://" +
                        getPackageName() +
                        "/" +
                        R.raw.splash
        );

        splashVideo.setVideoURI(videoUri);

        splashVideo.setOnPreparedListener(mp -> {

            mp.setLooping(false);

            // كتم الصوت
            mp.setVolume(0f, 0f);

            // تشغيل الفيديو مباشرة
            splashVideo.start();

            // 2.5 ثانية من بداية تشغيل الفيديو
            handler.postDelayed(() -> {
                openMainActivity();
            }, 2500);
        });

        splashVideo.setOnErrorListener((mp, what, extra) -> {

            openMainActivity();

            return true;
        });
    }

    private void openMainActivity() {

        if (opened) {
            return;
        }

        opened = true;

        handler.removeCallbacksAndMessages(null);

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        // انتقال مباشر
        overridePendingTransition(0, 0);

        finish();
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (splashVideo != null) {
            splashVideo.stopPlayback();
        }

        super.onDestroy();
    }
}
