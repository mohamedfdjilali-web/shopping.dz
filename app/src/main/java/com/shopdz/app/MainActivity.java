package com.shopdz.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
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

    // حجم الفيديو بالنسبة للشاشة
    private int videoWidth = 500;
    private int videoHeight = 500;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==============================
        // شاشة بيضاء
        // ==============================

        getWindow().setBackgroundDrawableResource(
                android.R.color.white
        );

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        // ==============================
        // Root
        // ==============================

        FrameLayout root = new FrameLayout(this);

        root.setBackgroundColor(Color.WHITE);

        // ==============================
        // WebView
        // ==============================

        webView = new WebView(this);

        webView.setBackgroundColor(Color.WHITE);

        // مهم جدًا:
        // الموقع مخفي أثناء الـSplash
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

        webView.setWebChromeClient(
                new WebChromeClient()
        );

        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            WebResourceRequest request
                    ) {

                        return false;
                    }

                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ) {

                        pageLoaded = true;

                        checkSplash();
                    }
                }
        );

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);

        // ==============================
        // TextureView
        // ==============================

        splashView = new TextureView(this);

        splashView.setOpaque(false);

        /*
         * سنضع الفيديو في وسط الشاشة.
         * لن نجعله MATCH_PARENT مباشرة،
         * حتى لا يتمدد الفيديو المربع.
         */

        FrameLayout.LayoutParams splashParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        splashParams.gravity = Gravity.CENTER;

        root.addView(splashView, splashParams);

        // الفيديو فوق WebView
        splashView.bringToFront();

        setContentView(root);

        // ==============================
        // TextureView Listener
        // ==============================

        splashView.setSurfaceTextureListener(
                new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(
                            SurfaceTexture surfaceTexture,
                            int width,
                            int height
                    ) {

                        videoSurface =
                                new Surface(surfaceTexture);

                        startSplashVideo(
                                width,
                                height
                        );
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(
                            SurfaceTexture surfaceTexture,
                            int width,
                            int height
                    ) {

                        adjustVideoSize(width, height);
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(
                            SurfaceTexture surfaceTexture
                    ) {

                        if (videoSurface != null) {

                            videoSurface.release();

                            videoSurface = null;
                        }

                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(
                            SurfaceTexture surfaceTexture
                    ) {
                    }
                }
        );

        // ==============================
        // تحميل الموقع في الخلفية
        // ==============================

        webView.loadUrl(
                "https://shop-dz.gt.tc"
        );

        // ==============================
        // زر الرجوع
        // ==============================

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

    // =========================================================
    // تشغيل الفيديو
    // =========================================================

    private void startSplashVideo(
            int screenWidth,
            int screenHeight
    ) {

        try {

            mediaPlayer = MediaPlayer.create(
                    this,
                    R.raw.splash
            );

            if (mediaPlayer == null) {

                videoFinished = true;

                checkSplash();

                return;
            }

            // ==============================
            // الحصول على المقاس الحقيقي
            // ==============================

            videoWidth =
                    mediaPlayer.getVideoWidth();

            videoHeight =
                    mediaPlayer.getVideoHeight();

            if (videoWidth <= 0) {
                videoWidth = 500;
            }

            if (videoHeight <= 0) {
                videoHeight = 500;
            }

            // ==============================
            // Surface
            // ==============================

            mediaPlayer.setSurface(
                    videoSurface
            );

            mediaPlayer.setVolume(
                    0f,
                    0f
            );

            mediaPlayer.setLooping(false);

            // ==============================
            // عند جاهزية الفيديو
            // ==============================

            mediaPlayer.setOnPreparedListener(
                    mp -> {

                        adjustVideoSize(
                                splashView.getWidth(),
                                splashView.getHeight()
                        );

                        mp.start();
                    }
            );

            // ==============================
            // نهاية الفيديو
            // ==============================

            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        videoFinished = true;

                        checkSplash();
                    }
            );

            // ==============================
            // خطأ الفيديو
            // ==============================

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        videoFinished = true;

                        checkSplash();

                        return true;
                    }
            );

        } catch (Exception e) {

            videoFinished = true;

            checkSplash();
        }
    }

    // =========================================================
    // ضبط مقاس الفيديو بدون تشويه
    // =========================================================

    private void adjustVideoSize(
            int screenWidth,
            int screenHeight
    ) {

        if (screenWidth <= 0 ||
                screenHeight <= 0 ||
                videoWidth <= 0 ||
                videoHeight <= 0) {

            return;
        }

        /*
         * نسبة الفيديو الأصلية
         *
         * 500 / 500 = 1:1
         */

        float videoRatio =
                (float) videoWidth /
                (float) videoHeight;

        /*
         * نسبة الشاشة
         */

        float screenRatio =
                (float) screenWidth /
                (float) screenHeight;

        android.graphics.Matrix matrix =
                new android.graphics.Matrix();

        /*
         * نريد الفيديو بدون Stretch.
         *
         * الفيديو يبقى 1:1.
         */

        if (screenRatio > videoRatio) {

            // الشاشة أعرض من الفيديو

            float scale =
                    (float) screenHeight /
                    (float) screenHeight;

            matrix.setScale(
                    scale,
                    scale,
                    screenWidth / 2f,
                    screenHeight / 2f
            );

        } else {

            // الشاشة أطول من الفيديو

            float scale =
                    (float) screenWidth /
                    (float) screenWidth;

            matrix.setScale(
                    scale,
                    scale,
                    screenWidth / 2f,
                    screenHeight / 2f
            );
        }

        splashView.setTransform(matrix);
    }

    // =========================================================
    // فحص Splash
    // =========================================================

    private void checkSplash() {

        /*
         * لا نخفي الفيديو إلا بعد:
         *
         * الفيديو انتهى
         * +
         * الموقع انتهى من التحميل
         */

        if (videoFinished &&
                pageLoaded) {

            closeSplash();
        }
    }

    // =========================================================
    // إخفاء Splash
    // =========================================================

    private void closeSplash() {

        if (splashClosed) {
            return;
        }

        splashClosed = true;

        // الموقع يظهر كاملًا
        webView.setVisibility(
                View.VISIBLE
        );

        // إخفاء الفيديو
        if (splashView != null) {

            splashView.setVisibility(
                    View.GONE
            );
        }

        // تحرير MediaPlayer
        if (mediaPlayer != null) {

            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }

    // =========================================================
    // Destroy
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
