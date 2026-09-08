package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView video;
    private TextView debug;
    private final Handler handler = new Handler();

    private void log(String text) {
        runOnUiThread(() -> debug.setText(text));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        video = new VideoView(this);

        FrameLayout.LayoutParams videoParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        videoParams.gravity = Gravity.CENTER;

        root.addView(video, videoParams);

        debug = new TextView(this);
        debug.setTextColor(Color.BLACK);
        debug.setTextSize(18);
        debug.setGravity(Gravity.CENTER);
        debug.setText("1 - SplashActivity تعمل");

        FrameLayout.LayoutParams debugParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        debugParams.gravity = Gravity.BOTTOM;
        debugParams.bottomMargin = 100;

        root.addView(debug, debugParams);

        setContentView(root);

        log("1 - SplashActivity تعمل");

        Uri uri = Uri.parse(
                "android.resource://" +
                        getPackageName() +
                        "/" +
                        R.raw.splash
        );

        log("2 - تم العثور على R.raw.splash");

        video.setVideoURI(uri);

        log("3 - setVideoURI تم تنفيذه");

        video.setOnPreparedListener(mp -> {

            log("4 - VIDEO PREPARED");

            mp.setVolume(0f, 0f);
            mp.setLooping(false);

            video.start();

            log("5 - VIDEO START");

            handler.postDelayed(() -> {
                log("6 - الانتقال إلى الموقع");
                openMainActivity();
            }, 2500);
        });

        video.setOnCompletionListener(mp -> {
            log("VIDEO COMPLETED");
        });

        video.setOnErrorListener((mp, what, extra) -> {

            log("❌ VIDEO ERROR: " + what + " / " + extra);

            return true;
        });
    }

    private void openMainActivity() {

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

        if (video != null) {
            video.stopPlayback();
        }

        super.onDestroy();
    }
}
