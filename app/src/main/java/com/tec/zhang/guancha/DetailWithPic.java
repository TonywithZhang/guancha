package com.tec.zhang.guancha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DetailWithPic extends AppCompatActivity {
    private ImageView newsPic;
    private TextView detailText;
    ParseHTML.GuanChaSouceData newsType;
    private static final String TAG = "新闻页面里 ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_with_pic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newsPic = findViewById(R.id.detail_image);
        detailText = findViewById(R.id.detail_text_pic);
        Intent intent = getIntent();
        byte[] pic = intent.getByteArrayExtra("pic");
        newsPic.setImageBitmap(BitmapFactory.decodeByteArray(pic,0,pic.length));
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        final String articleUrl = getIntent().getStringExtra("articleUrl");
        newsType = getIntent().getParcelableExtra("news");
        setTitle(newsType.getTitle());
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
            Elements articleSegments = document.select(".all-txt").select("p");
            if (articleSegments.size() == 0) articleSegments = document.select(".article-txt").select("p");
            for (Element ele : articleSegments){
                if (!ele.text().endsWith("截图")){
                    article = article + ele.text() + "\n\n";
                }
            }
            /*if (type == ParseHTML.NEWS_TYPE.FENGWEN){
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
            }*/
            Log.d(TAG, "parseDetail: "  + article);
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
