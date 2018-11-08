package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SurfPage extends AppCompatActivity {
    //private WebView webView;
    private static final String TAG = "所有新闻页面：";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_page);

        /*webView = (WebView) findViewById(R.id.web1);
        //webView.getSettings().setUserAgentString("app/XXX");
        webView.goBackOrForward(20);
        Log.d(TAG, "onCreate: 是否可以返回：" + webView.canGoBack());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("https://www.guancha.cn");
        Log.d(TAG, "onCreate: " + webView.getSettings().getUserAgentString());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.requestFocus();*/
    }
}
