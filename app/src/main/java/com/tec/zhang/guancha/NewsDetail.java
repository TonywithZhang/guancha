package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tec.zhang.guancha.recycler.ParseHTML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsDetail extends AppCompatActivity {
    private static final String TAG = "第三页";
    TextView detailText;
    ParseHTML.GuanChaSouceData newsType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        final String articleUrl = getIntent().getStringExtra("articleUrl");
        Log.d(TAG, "onCreate: " + articleUrl);
        detailText = findViewById(R.id.news_detail_text);
        newsType = getIntent().getParcelableExtra("news");
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseDetail(articleUrl,newsType.getNewsType());
            }
        }).start();
    }
    private void parseDetail(String articleUrl, ParseHTML.NEWS_TYPE type){
        try {
            String article = "";
            Document document = Jsoup.connect(articleUrl).get();
            if (type == ParseHTML.NEWS_TYPE.FENGWEN){
                Elements articleSegments = document.select(".all-txt").select("p");
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        article = article + ele.text() + "\n\n";
                    }
                }
            }else {
                Elements articleSegments = document.select(".article-txt").select("p");
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        article = article + ele.text() + "\n\n";
                    }
                }
            }

            final String finalArticle = article;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    detailText.setText(finalArticle);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
