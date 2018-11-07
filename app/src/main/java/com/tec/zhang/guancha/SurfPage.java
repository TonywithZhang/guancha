package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SurfPage extends AppCompatActivity {
    private WebView webView;
    private static final String TAG = "网页界面：";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_page);

        webView = (WebView) findViewById(R.id.web1);
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
        webView.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "onKeyDown: 按下了返回键");
            if (webView.canGoBack()) webView.goBack();
            else {
                Log.d(TAG, "onKeyDown: 浏览器不允许返回");
                System.exit(0);
            }
        }else Log.d(TAG, "onKeyDown: 按下的不是返回键");
        return super.onKeyDown(keyCode, event);
    }
}
