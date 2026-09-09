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
    private boolean splashTimeFinished = false;
    private boolean splashClosed = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // خلفية بيضاء من البداية
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        // إنشاء الحاوية
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        // =========================
        // WebView
        // =========================
        webView = new WebView(this);

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

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;
                checkSplash();
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(webView, webParams);

        // =========================
        // Video
        // =========================
        videoView = new VideoView(this);

        videoView.setBackgroundColor(Color.WHITE);

        FrameLayout.LayoutParams videoParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(videoView, videoParams);

        setContentView(root);

        // =========================
        // تحميل الموقع مباشرة
        // =========================
        webView.loadUrl("https://shop-dz.gt.tc");

        // =========================
        // تشغيل الفيديو
        // =========================
        String videoPath =
                "android.resource://" +
                getPackageName() +
                "/" +
                com.shopdz.app.R.raw.splash;

        videoView.setVideoPath(videoPath);

        videoView.setOnPreparedListener(mp -> {

            mp.setVolume(0f, 0f);
            mp.setLooping(false);

            videoView.start();

            // مدة الـSplash = 2.5 ثانية
            handler.postDelayed(() -> {

                splashTimeFinished = true;
                checkSplash();

            }, 2500);
        });

        // إذا حدث خطأ في الفيديو لا يحدث Crash
        videoView.setOnErrorListener((mp, what, extra) -> {

            splashTimeFinished = true;
            checkSplash();

            return true;
        });

        // حماية: إذا الموقع تأخر جدًا
        handler.postDelayed(() -> {

            if (!splashClosed) {
                splashTimeFinished = true;
                pageLoaded = true;
                closeSplash();
            }

        }, 10000);

        // زر الرجوع
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

    private void checkSplash() {

        if (splashTimeFinished && pageLoaded) {
            closeSplash();
        }
    }

    private void closeSplash() {

        if (splashClosed) {
            return;
        }

        splashClosed = true;

        handler.removeCallbacksAndMessages(null);

        // إيقاف الفيديو وإخفاؤه
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
        }

        // الموقع يظهر مباشرة
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
        }
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
