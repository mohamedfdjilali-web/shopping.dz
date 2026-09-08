package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView splashVideo;
    private View whiteCover;

    private final Handler handler = new Handler();
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // خلفية بيضاء من أول لحظة
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_splash);

        splashVideo = findViewById(R.id.splashVideo);
        whiteCover = findViewById(R.id.whiteCover);

        Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.splash
        );

        splashVideo.setVideoURI(videoUri);

        // مهم: نضع OnInfoListener قبل تشغيل الفيديو
        splashVideo.setOnInfoListener((mp, what, extra) -> {

            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                // الفيديو بدأ فعليًا بالرسم على الشاشة
                whiteCover.setVisibility(View.GONE);

                return true;
            }

            return false;
        });

        splashVideo.setOnPreparedListener(mp -> {

            mp.setLooping(false);

            // كتم صوت الفيديو
            mp.setVolume(0f, 0f);

            // تشغيل الفيديو
            splashVideo.start();
        });

        splashVideo.setOnErrorListener((mp, what, extra) -> {

            openMainActivity();

            return true;
        });

        // Splash = 2.5 ثانية
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
