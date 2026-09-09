package com.shopdz.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private TextureView splashView;

    private MediaPlayer mediaPlayer;
    private Surface videoSurface;

    private boolean pageLoaded = false;
    private boolean videoFinished = false;
    private boolean splashClosed = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(
                android.R.color.white
        );

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        // =====================================================
        // ROOT
        // =====================================================

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        // =====================================================
        // WEBVIEW
        // =====================================================

        webView = new WebView(this);

        webView.setBackgroundColor(Color.WHITE);

        // نخفي الموقع أثناء الـSplash
        webView.setVisibility(View.INVISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                return false;
            }

            @Override
            public void onPageFinished(
                    WebView view,
                    String url) {

                pageLoaded = true;

                tryShowWebsite();
            }
        });

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);

        // =====================================================
        // SPLASH TEXTUREVIEW
        // =====================================================

        splashView = new TextureView(this);

        splashView.setOpaque(false);

        FrameLayout.LayoutParams splashParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(splashView, splashParams);

        setContentView(root);

        // =====================================================
        // TEXTURE LISTENER
        // =====================================================

        splashView.setSurfaceTextureListener(
                new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(
                            SurfaceTexture surfaceTexture,
                            int width,
                            int height) {

                        videoSurface = new Surface(surfaceTexture);

                        startSplashVideo();
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

                        if (videoSurface != null) {
                            videoSurface.release();
                            videoSurface = null;
                        }

                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(
                            SurfaceTexture surfaceTexture) {
                    }
                }
        );

        // =====================================================
        // LOAD WEBSITE
        // =====================================================

        webView.loadUrl("https://shop-dz.gt.tc");

        // =====================================================
        // BACK BUTTON
        // =====================================================

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (webView != null && webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }
                }
        );
    }

    // =========================================================
    // START VIDEO
    // =========================================================

    private void startSplashVideo() {

        try {

            mediaPlayer = MediaPlayer.create(
                    this,
                    R.raw.splash
            );

            if (mediaPlayer == null) {
                videoFinished = true;
                tryShowWebsite();
                return;
            }

            mediaPlayer.setSurface(videoSurface);

            mediaPlayer.setVolume(0f, 0f);

            mediaPlayer.setLooping(false);

            mediaPlayer.setOnPreparedListener(mp -> {

                mp.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {

                videoFinished = true;

                tryShowWebsite();
            });

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        videoFinished = true;

                        tryShowWebsite();

                        return true;
                    }
            );

        } catch (Exception e) {

            videoFinished = true;

            tryShowWebsite();
        }
    }

    // =========================================================
    // SHOW WEBSITE
    // =========================================================

    private void tryShowWebsite() {

        if (splashClosed) {
            return;
        }

        /*
         * لا نخفي الفيديو إلا عندما:
         *
         * الفيديو انتهى
         * +
         * الصفحة انتهت من التحميل
         */

        if (videoFinished && pageLoaded) {

            splashClosed = true;

            // إظهار الموقع كاملًا
            webView.setVisibility(View.VISIBLE);

            // إخفاء الفيديو
            splashView.setVisibility(View.GONE);

            if (mediaPlayer != null) {

                try {
                    mediaPlayer.stop();
                } catch (Exception ignored) {
                }

                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null) {

            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (videoSurface != null) {
            videoSurface.release();
            videoSurface = null;
        }

        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
