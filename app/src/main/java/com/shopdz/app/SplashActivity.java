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

        setContentView(R.layout.activity_splash);

        splashVideo = findViewById(R.id.splashVideo);

        Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.splash
        );

        splashVideo.setVideoURI(videoUri);

        splashVideo.setOnPreparedListener(mp -> {

            mp.setLooping(false);

            splashVideo.setVisibility(View.VISIBLE);

            splashVideo.start();
        });

        // الانتقال بعد 2.5 ثانية كحد أقصى
        handler.postDelayed(() -> openMainActivity(), 2500);

        splashVideo.setOnCompletionListener(mp -> {

            // إذا انتهى الفيديو قبل 2.5 ثانية
            // ننتظر حتى انتهاء المدة المحددة
        });
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
