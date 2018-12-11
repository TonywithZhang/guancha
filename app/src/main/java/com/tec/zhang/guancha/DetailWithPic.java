package com.tec.zhang.guancha;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

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
        Log.d(TAG, "onCreate: 文章地址为：" + articleUrl);
        newsType = getIntent().getParcelableExtra("news");

        SpannableString ss = new SpannableString(newsType.getTitle());
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);
        ss.setSpan(fcs,0,ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(ss);
        //setTitleColor(0x000000);

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

            Map<String, String> header = new HashMap<>();

            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            header.put("Accept-Language", "zh-cn,zh;q=0.5");
            header.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
            header.put("Connection", "keep-alive");
            if (!articleUrl.equals("")){
                Document document = Jsoup.connect(articleUrl).headers(header).get();
            /*File dir = Environment.getExternalStorageDirectory();
            File storageDir = new File(dir.toString() + "/guanchazhe");
            if (!storageDir.exists()) storageDir.mkdir();
            File log = new File(storageDir.toString()+ "/" + System.currentTimeMillis() + ".html");
            if (!log.exists()) log.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(log,"rw");
            raf.write(document.toString().getBytes());
            raf.close();*/
                Elements articleSegments = document.select(".all-txt").select("p");
                if (articleSegments.size() == 0){
                    //Log.d(TAG, "parseDetail: 新闻格式不是all text");
                    articleSegments = document.select(".article-txt").select("p");
                }
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
                //Log.d(TAG, "parseDetail: "  + article);
                final String finalArticle = article;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        detailText.setText(finalArticle);
                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
