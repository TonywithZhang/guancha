package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.tabs.TabLayout;
import com.tec.zhang.guancha.recycler.Cards;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.NewsSingle;
import com.tec.zhang.guancha.recycler.ParseHTML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SurfPage extends AppCompatActivity {
    private static final String TAG = "主视图里面  ";
    //private WebView webView;
    private RecyclerView rececle;
    private ParseHTML guanchaWeb;
    private boolean parseFinishFlage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_page);
        //rececle = findViewById(R.id.recycler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager pager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tablayout);

        /*rececle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 滚动了屏幕");
            }
        });

        guanchaWeb = new ParseHTML();
        new Thread(new Runnable() {
            @Override
            public void run() {
                guanchaWeb.init();
                guanchaWeb.getHeadLine();
                guanchaWeb.createNormalNews();
                guanchaWeb.getImportantNews().addAll(guanchaWeb.getNormalNews());
                parseFinishFlage = true;
            }
        }).start();
        try {
            while (!parseFinishFlage) Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cards cardAdapter = new Cards(guanchaWeb.getImportantNews());
        Log.d(TAG, "onCreate: " + guanchaWeb.getImportantNews().size());
        Log.d(TAG, "onCreate: " + cardAdapter.news.size());
        //cardAdapter.news.addAll(guanchaWeb.getImportantNews());
        rececle.setLayoutManager(new LinearLayoutManager(SurfPage.this));
        rececle.addItemDecoration(new MyItemDecration(10));
        rececle.setAdapter(cardAdapter);*/
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
