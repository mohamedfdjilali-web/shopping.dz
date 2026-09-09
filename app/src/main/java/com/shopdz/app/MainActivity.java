package com.shopdz.app;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
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

    // ==========================================
    // الحجم الأساسي للفيديو
    // ==========================================
    // 70% من أصغر بُعد في الشاشة
    private static final float VIDEO_SIZE = 0.70f;

    // ==========================================
    // التكبير الإضافي
    // ==========================================
    // 0.7 سم
    private static final float EXTRA_CM = 0.7f;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==========================================
        // Root
        // ==========================================

        FrameLayout root = new FrameLayout(this);

        root.setBackgroundColor(0xFFFFFFFF);

        setContentView(root);


        // ==========================================
        // WebView
        // ==========================================

        webView = new WebView(this);

        // نخفي الموقع أثناء الـSplash
        webView.setVisibility(View.INVISIBLE);

        webView.setBackgroundColor(0xFFFFFFFF);

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

                super.onPageFinished(view, url);

                pageFinished = true;

                showWebsiteIfReady();
            }
        });

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);


        // ==========================================
        // TextureView للفيديو
        // ==========================================

        videoView = new TextureView(this);

        videoView.setOpaque(false);

        videoView.setSurfaceTextureListener(this);


        // ==========================================
        // حساب حجم الفيديو
        // ==========================================

        DisplayMetrics metrics =
                getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        int smallestSide =
                Math.min(screenWidth, screenHeight);


        // الحجم الأساسي
        int baseSize =
                (int) (smallestSide * VIDEO_SIZE);


        // ==========================================
        // تحويل 0.7 سم إلى Pixels
        // ==========================================

        float densityDpi = metrics.densityDpi;

        int extraPixels =
                (int) (
                        (EXTRA_CM / 2.54f)
                                * densityDpi
                );


        // الحجم النهائي
        int videoSize =
                baseSize + extraPixels;


        // ==========================================
        // وضع الفيديو في المنتصف
        // ==========================================

        FrameLayout.LayoutParams videoParams =
                new FrameLayout.LayoutParams(
                        videoSize,
                        videoSize
                );

        videoParams.gravity = Gravity.CENTER;

        root.addView(videoView, videoParams);


        // الفيديو فوق الموقع
        videoView.bringToFront();


        // ==========================================
        // تحميل الموقع في الخلفية
        // ==========================================

        webView.loadUrl(
                "https://shop-dz.gt.tc"
        );


        // ==========================================
        // زر الرجوع
        // ==========================================

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (webView != null &&
                                webView.canGoBack()) {

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

        Surface surface =
                new Surface(surfaceTexture);

        try {

            mediaPlayer =
                    MediaPlayer.create(
                            this,
                            R.raw.splash
                    );

            if (mediaPlayer == null) {

                videoFinished = true;

                videoView.setVisibility(View.GONE);

                surface.release();

                showWebsiteIfReady();

                return;
            }


            // ربط الفيديو بالـTextureView
            mediaPlayer.setSurface(surface);

            surface.release();


            // بدون صوت
            mediaPlayer.setVolume(0f, 0f);

            // لا تكرار
            mediaPlayer.setLooping(false);


            // ==========================================
            // عند انتهاء الفيديو
            // ==========================================

            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        videoFinished = true;

                        videoView.setVisibility(
                                View.GONE
                        );

                        if (mediaPlayer != null) {

                            mediaPlayer.release();

                            mediaPlayer = null;
                        }

                        showWebsiteIfReady();
                    }
            );


            // ==========================================
            // إذا حدث خطأ
            // ==========================================

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        videoFinished = true;

                        videoView.setVisibility(
                                View.GONE
                        );

                        if (mediaPlayer != null) {

                            mediaPlayer.release();

                            mediaPlayer = null;
                        }

                        showWebsiteIfReady();

                        return true;
                    }
            );


            // ==========================================
            // تشغيل الفيديو
            // ==========================================

            mediaPlayer.start();


        } catch (Exception e) {

            videoFinished = true;

            videoView.setVisibility(View.GONE);

            if (mediaPlayer != null) {

                mediaPlayer.release();

                mediaPlayer = null;
            }

            surface.release();

            showWebsiteIfReady();
        }
    }


    // ==================================================
    // تغير حجم TextureView
    // ==================================================

    @Override
    public void onSurfaceTextureSizeChanged(
            SurfaceTexture surface,
            int width,
            int height) {
    }


    // ==================================================
    // تدمير Surface
    // ==================================================

    @Override
    public boolean onSurfaceTextureDestroyed(
            SurfaceTexture surface) {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

            } catch (Exception ignored) {
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }

        return true;
    }


    // ==================================================
    // تحديث Texture
    // ==================================================

    @Override
    public void onSurfaceTextureUpdated(
            SurfaceTexture surface) {
    }


    // ==================================================
    // إظهار الموقع
    // ==================================================

    private void showWebsiteIfReady() {

        if (videoFinished &&
                pageFinished) {

            webView.post(() -> {

                webView.setVisibility(
                        View.VISIBLE
                );

                videoView.setVisibility(
                        View.GONE
                );
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

            webView = null;
        }

        super.onDestroy();
    }
}
