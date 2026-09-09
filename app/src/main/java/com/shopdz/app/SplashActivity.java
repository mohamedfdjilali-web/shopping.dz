package com.shopdz.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private Surface surface;

    private final Handler handler = new Handler();

    private boolean videoStarted = false;
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // منع أي لون أسود
        getWindow().setBackgroundDrawableResource(
                android.R.color.white
        );

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(R.layout.activity_splash);

        textureView = findViewById(R.id.splashTexture);

        textureView.setSurfaceTextureListener(
                new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(
                            SurfaceTexture surfaceTexture,
                            int width,
                            int height) {

                        surface = new Surface(surfaceTexture);

                        prepareVideo();
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(
                            SurfaceTexture surfaceTexture,
                            int width,
                            int height) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(
                            SurfaceTexture surfaceTexture) {

                        if (surface != null) {
                            surface.release();
                            surface = null;
                        }

                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(
                            SurfaceTexture surfaceTexture) {
                    }
                }
        );
    }

    private void prepareVideo() {

        try {

            mediaPlayer = MediaPlayer.create(
                    this,
                    R.raw.splash
            );

            if (mediaPlayer == null) {
                openMain();
                return;
            }

            mediaPlayer.setSurface(surface);

            mediaPlayer.setVolume(0f, 0f);

            mediaPlayer.setLooping(false);

            mediaPlayer.setOnPreparedListener(mp -> {

                videoStarted = true;

                mp.start();

                /*
                 * ننتظر 2.5 ثانية فقط.
                 * بعدها نفتح الموقع.
                 */
                handler.postDelayed(() -> {
                    openMain();
                }, 2500);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                openMain();
            });

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        openMain();

                        return true;
                    }
            );

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
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (surface != null) {
            surface.release();
            surface = null;
        }

        super.onDestroy();
    }
}
