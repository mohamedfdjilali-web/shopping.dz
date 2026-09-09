package com.shopdz.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView splashVideo;
    private WebView webView;

    private final Handler handler = new Handler();

    private boolean pageLoaded = false;
    private boolean splashFinished = false;
    private boolean opened = false;

    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        setContentView(R.layout.activity_splash);

        splashVideo = findViewById(R.id.splashVideo);
        webView = findViewById(R.id.splashWebView);

        // إعداد WebView أثناء عرض الفيديو
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;

                tryOpenMain();
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        // ابدأ تحميل الموقع فورًا
        webView.loadUrl("https://shop-dz.gt.tc");

        // تشغيل فيديو Splash
        splashVideo.setVideoPath(
                "android.resource://" +
                getPackageName() +
                "/" +
                R.raw.splash
        );

        splashVideo.setOnPreparedListener(mp -> {

            mp.setVolume(0f, 0f);
            mp.setLooping(false);

            splashVideo.start();

            // Splash = 2.5 ثانية
            handler.postDelayed(() -> {

                splashFinished = true;

                tryOpenMain();

            }, 2500);
        });

        splashVideo.setOnErrorListener((mp, what, extra) -> {

            // إذا تعذر تشغيل الفيديو، لا نبقى عالقين
            splashFinished = true;

            tryOpenMain();

            return true;
        });
    }

    private void tryOpenMain() {

        /*
         * لا نفتح MainActivity إلا عندما:
         *
         * 1. انتهت 2.5 ثانية
         * 2. الموقع انتهى من التحميل
         */
        if (splashFinished && pageLoaded) {
            openMain();
        }
    }

    private void openMain() {

        if (opened) {
            return;
        }

        opened = true;

        handler.removeCallbacksAndMessages(null);

        /*
         * نعطي WebView الذي تم تحميله إلى MainActivity
         */
        MainActivity.webViewFromSplash = webView;

        webView = null;

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

        if (splashVideo != null) {
            splashVideo.stopPlayback();
        }

        if (webView != null) {
            webView.destroy();
        }

        super.onDestroy();
    }
}
