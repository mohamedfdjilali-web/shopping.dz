package com.shopdz.app;

import android.content.Intent;
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

        // خلفية بيضاء قبل ظهور الفيديو
        getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        getWindow().setNavigationBarColor(android.graphics.Color.WHITE);

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

    splashVideo.setVisibility(View.VISIBLE);

    mp.setVolume(0f, 0f);

    splashVideo.start();
});

splashVideo.setOnErrorListener((mp, what, extra) -> {

    // إذا فشل الفيديو، افتح الموقع بدل البقاء في شاشة بيضاء
    openMainActivity();

    return true;
});

        // الانتقال إلى الموقع بعد 2.5 ثانية
        handler.postDelayed(() -> {

            openMainActivity();

        }, 2500);
    }

    private void openMainActivity() {

        if (opened) {
            return;
        }

        opened = true;

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        // انتقال بدون ظهور شاشة سوداء
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

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
