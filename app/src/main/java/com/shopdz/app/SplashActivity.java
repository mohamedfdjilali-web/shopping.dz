package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.TextureView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private boolean opened = false;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // أبيض من أول لحظة
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_splash);

        textureView = findViewById(R.id.splashTexture);

        textureView.setSurfaceTextureListener(
                new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(
                            android.graphics.SurfaceTexture surface,
                            int width,
                            int height) {

                        startVideo(surface);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(
                            android.graphics.SurfaceTexture surface,
                            int width,
                            int height) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(
                            android.graphics.SurfaceTexture surface) {

                        if (mediaPlayer != null) {
                            mediaPlayer.setSurface(null);
                        }

                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(
                            android.graphics.SurfaceTexture surface) {
                    }
                }
        );
    }

    private void startVideo(android.graphics.SurfaceTexture surface) {

        try {

            mediaPlayer = MediaPlayer.create(
                    this,
                    R.raw.splash
            );

            if (mediaPlayer == null) {
                openMain();
                return;
            }

            android.view.Surface videoSurface =
                    new android.view.Surface(surface);

            mediaPlayer.setSurface(videoSurface);

            mediaPlayer.setVolume(0f, 0f);

            mediaPlayer.setLooping(false);

            mediaPlayer.setOnPreparedListener(mp -> {

                mp.start();

                // نعرض السلاش لمدة 2.5 ثانية فقط
                handler.postDelayed(() -> {
                    openMain();
                }, 2500);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                openMain();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                openMain();
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {

            openMain();
        }
    }

    private void openMain() {

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

        overridePendingTransition(0, 0);

        finish();
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {

            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }
}
