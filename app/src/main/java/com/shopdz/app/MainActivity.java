package com.shopdz.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static WebView webViewFromSplash;

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*
         * إذا كان WebView قد تم تحميله مسبقًا في SplashActivity
         * نستخدمه مباشرة بدل إعادة تحميل الموقع.
         */
        if (webViewFromSplash != null) {

            webView = webViewFromSplash;

            webViewFromSplash = null;

            setContentView(webView);

        } else {

            webView = new WebView(this);

            setContentView(webView);

            setupWebView();

            webView.loadUrl("https://shop-dz.gt.tc");
        }

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

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {

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

                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    protected void onDestroy() {

        /*
         * لا ندمر WebView إذا كان قد تم نقله من Splash
         * وتم وضعه في MainActivity.
         */
        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
