package com.shopdz.app;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener {

    private WebView webView;
    private TextureView videoView;
    private MediaPlayer mediaPlayer;

    private boolean videoFinished = false;
    private boolean pageFinished = false;

    // ==============================
    // حجم مربع الفيديو
    // ==============================
    // 0.70 = 70% من أقصر بُعد في الشاشة
    // غيّرها إلى:
    // 0.60 = أصغر
    // 0.80 = أكبر
    // 1.00 = أكبر حجم ممكن
    private static final float VIDEO_SIZE = 0.70f;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==========================================
        // الحاوية الرئيسية
        // ==========================================
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFFFFFFFF);

        setContentView(root);


        // ==========================================
        // WebView
        // ==========================================
        webView = new WebView(this);

        webView.setVisibility(View.INVISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);

        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(
                    WebView view,
                    String url) {

                super.onPageFinished(view, url);

                pageFinished = true;

                showWebsiteIfReady();
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);


        // ==========================================
        // مربع الفيديو
        // ==========================================
        videoView = new TextureView(this);

        videoView.setOpaque(false);
        videoView.setSurfaceTextureListener(this);

        // حساب حجم المربع
        int screenWidth = getResources()
                .getDisplayMetrics().widthPixels;

        int screenHeight = getResources()
                .getDisplayMetrics().heightPixels;

        int smallestSide = Math.min(screenWidth, screenHeight);

        int videoSize = (int) (smallestSide * VIDEO_SIZE);


        // ==========================================
        // وضع الفيديو في منتصف الشاشة
        // ==========================================
        FrameLayout.LayoutParams videoParams =
                new FrameLayout.LayoutParams(
                        videoSize,
                        videoSize
                );

        videoParams.gravity =
                android.view.Gravity.CENTER;

        root.addView(videoView, videoParams);


        // ==========================================
        // تحميل الموقع مباشرة في الخلفية
        // ==========================================
        webView.loadUrl("https://shop-dz.gt.tc");


        // ==========================================
        // زر الرجوع
        // ==========================================
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }
                }
        );
    }


    // ==================================================
    // TextureView أصبح جاهزًا
    // ==================================================

    @Override
    public void onSurfaceTextureAvailable(
            SurfaceTexture surfaceTexture,
            int width,
            int height) {

        Surface surface = new Surface(surfaceTexture);

        mediaPlayer = MediaPlayer.create(
                this,
                com.shopdz.app.R.raw.splash
        );

        if (mediaPlayer == null) {
            videoFinished = true;
            videoView.setVisibility(View.GONE);
            showWebsiteIfReady();
            return;
        }

        mediaPlayer.setSurface(surface);

        surface.release();

        mediaPlayer.setOnCompletionListener(mp -> {

            videoFinished = true;

            videoView.setVisibility(View.GONE);

            mp.release();
            mediaPlayer = null;

            showWebsiteIfReady();
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {

            videoFinished = true;

            videoView.setVisibility(View.GONE);

            mp.release();
            mediaPlayer = null;

            showWebsiteIfReady();

            return true;
        });

        mediaPlayer.start();
    }


    @Override
    public void onSurfaceTextureSizeChanged(
            SurfaceTexture surface,
            int width,
            int height) {
    }


    @Override
    public boolean onSurfaceTextureDestroyed(
            SurfaceTexture surface) {

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        return true;
    }


    @Override
    public void onSurfaceTextureUpdated(
            SurfaceTexture surface) {
    }


    // ==================================================
    // إظهار الموقع فقط بعد انتهاء الفيديو وتحميل الموقع
    // ==================================================

    private void showWebsiteIfReady() {

        if (videoFinished && pageFinished) {

            webView.post(() -> {

                webView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);

            });
        }
    }


    // ==================================================
    // تنظيف
    // ==================================================

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null) {

            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }

            mediaPlayer = null;
        }

        if (webView != null) {
            webView.destroy();
        }

        super.onDestroy();
    }
}
