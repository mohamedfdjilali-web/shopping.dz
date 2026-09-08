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

    private boolean started = false;
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // كل شيء أبيض قبل تشغيل الفيديو
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_splash);

        splashVideo = findViewById(R.id.splashVideo);
        whiteCover = findViewById(R.id.whiteCover);

        Uri videoUri = Uri.parse(
                "android.resource://" +
                        getPackageName() +
                        "/" +
                        R.raw.splash
        );

        splashVideo.setVideoURI(videoUri);

        splashVideo.setOnPreparedListener(mp -> {

            mp.setLooping(false);

            // بدون صوت
            mp.setVolume(0f, 0f);

            // يبدأ الفيديو
            splashVideo.start();

            started = true;

            /*
             * ننتظر حتى يبدأ Android فعليًا في
             * رسم أول إطار للفيديو.
             */
            mp.setOnInfoListener((player, what, extra) -> {

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                    // إخفاء الغطاء الأبيض
                    whiteCover.animate()
                            .alpha(0f)
                            .setDuration(100)
                            .withEndAction(() ->
                                    whiteCover.setVisibility(View.GONE)
                            )
                            .start();

                    return true;
                }

                return false;
            });
        });

        splashVideo.setOnErrorListener((mp, what, extra) -> {

            // في حالة حدوث خطأ فقط
            openMainActivity();

            return true;
        });

        /*
         * مدة Splash = 2.5 ثانية
         */
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
