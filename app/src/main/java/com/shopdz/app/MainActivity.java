package com.shopdz.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private VideoView videoView;

    private final Handler handler = new Handler();

    private boolean pageLoaded = false;
    private boolean splashFinished = false;
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

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        // =========================
        // WEBVIEW
        // =========================

        webView = new WebView(this);

        // مهم جدًا:
        // الموقع يكون مخفيًا أثناء التحميل
        webView.setVisibility(View.INVISIBLE);

        webView.setBackgroundColor(Color.WHITE);

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

                checkSplash();
            }
        });

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);

        // =========================
        // VIDEO
        // =========================

        videoView = new VideoView(this);

        videoView.setBackgroundColor(Color.WHITE);

        FrameLayout.LayoutParams videoParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(videoView, videoParams);

        // الفيديو فوق الموقع
        videoView.bringToFront();

        setContentView(root);

        // =========================
        // ابدأ تحميل الموقع فورًا
        // =========================

        webView.loadUrl("https://shop-dz.gt.tc");

        // =========================
        // تشغيل الفيديو
        // =========================

        videoView.setVideoPath(
                "android.resource://" +
                getPackageName() +
                "/" +
                R.raw.splash
        );

        videoView.setOnPreparedListener(mp -> {

            mp.setVolume(0f, 0f);
            mp.setLooping(false);

            videoView.start();

            // 2.5 ثانية
            handler.postDelayed(() -> {

                splashFinished = true;

                checkSplash();

            }, 2500);
        });

        videoView.setOnErrorListener((mp, what, extra) -> {

            splashFinished = true;

            checkSplash();

            return true;
        });

        // =========================
        // زر الرجوع
        // =========================

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

    private void checkSplash() {

        /*
         * لا نكشف الموقع إلا عندما:
         *
         * 1 - ينتهي الفيديو 2.5 ثانية
         * 2 - تنتهي الصفحة من التحميل
         */

        if (splashFinished && pageLoaded) {

            /*
             * ننتظر 300ms إضافية حتى ينتهي WebView
             * من الرسم النهائي للصفحة.
             */
            handler.postDelayed(() -> {

                closeSplash();

            }, 300);
        }
    }

    private void closeSplash() {

        if (splashClosed) {
            return;
        }

        splashClosed = true;

        handler.removeCallbacksAndMessages(null);

        // أولًا أظهر الموقع
        webView.setVisibility(View.VISIBLE);

        // ثم أخفِ الفيديو
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (videoView != null) {
            videoView.stopPlayback();
        }

        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
