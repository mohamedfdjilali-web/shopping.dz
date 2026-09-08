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

    private boolean videoStarted = false;
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // أبيض من أول لحظة
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

            // تشغيل الفيديو
            splashVideo.start();

            videoStarted = true;

            /*
             * مدة الـSplash تبدأ من لحظة تشغيل الفيديو
             * وليس من لحظة فتح Activity.
             */
            handler.postDelayed(() -> {

                openMainActivity();

            }, 2500);
        });

        /*
         * عندما يرسم Android أول إطار حقيقي
         * نخفي الغطاء الأبيض.
         */
        splashVideo.setOnInfoListener(
                (mp, what, extra) -> {

                    if (what ==
                            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                        whiteCover.animate()
                                .alpha(0f)
                                .setDuration(80)
                                .withEndAction(() ->
                                        whiteCover.setVisibility(
                                                View.GONE
                                        )
                                )
                                .start();

                        return true;
                    }

                    return false;
                }
        );

        splashVideo.setOnErrorListener(
                (mp, what, extra) -> {

                    openMainActivity();

                    return true;
                }
        );
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

        /*
         * انتقال مباشر بدون Fade.
         */
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
