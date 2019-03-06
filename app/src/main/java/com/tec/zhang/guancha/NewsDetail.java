package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tec.zhang.guancha.recycler.ParseHTML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsDetail extends AppCompatActivity {
    private static final String TAG = "第三页";
    TextView detailText;
    private LinearLayout detailNoPic;
    ParseHTML.GuanChaSouceData newsType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        final String articleUrl = getIntent().getStringExtra("articleUrl");
        Log.d(TAG, "onCreate: " + articleUrl);
        detailNoPic = findViewById(R.id.detail_no_pic_frame);
        detailText = findViewById(R.id.news_detail_text);
        newsType = getIntent().getParcelableExtra("news");
        Log.d(TAG, "onCreate: " + newsType.getTitle());
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
            StringBuilder article = new StringBuilder();
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
                Elements eles = document.select(".last");
                String fullUrl = null;
                StringBuilder builder = new StringBuilder();
                if(eles.size() != 0) {
                    for(Element ele : eles) {
                        fullUrl = ele.attr("onclick");
                        if (! fullUrl.equals("")) {
                            fullUrl = fullUrl.substring(fullUrl.indexOf("=") + 2,fullUrl.length() -2);
                            builder.append(articleUrl.substring(0,articleUrl.indexOf("/",10))).append(fullUrl);
                            document = Jsoup.connect(builder.toString()).headers(header).get();
                            Log.d(TAG, "parseDetail: " + builder.toString());
                        }
                    }
                }
                Elements articleSegments = document.select(".all-txt").select("p");
                if (articleSegments.size() == 0){
                    //Log.d(TAG, "parseDetail: 新闻格式不是all text");
                    articleSegments = document.select(".article-txt").select("p");
                }
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        if (!ele.text().equals("")){
                            TextView newsText = new TextView(NewsDetail.this);
                            newsText.setText(ele.text());
                            runOnUiThread(() -> detailNoPic.addView(newsText,layoutParams));
                        }
                        //article.append(ele.text()).append("\n");
                        Elements pictures = ele.select("img");
                        if (pictures.size() != 0){
                            Element pic = pictures.get(0);
                            //article.append("placeHolder").append(imageNumber).append("\n");
                            //imageNumber ++;
                            String imageUrl = pic.attr("abs:src");
                            //pictureUrls.add(imageUrl);
                            ImageView newsImage = new ImageView(NewsDetail.this);
                            newsImage.setImageResource(R.drawable.ic_guancha);
                            runOnUiThread(() -> detailNoPic.addView(newsImage,layoutParams));
                            new Thread(() -> {
                                try {
                                    Drawable newsDetailPic = Drawable.createFromStream(new URL(imageUrl).openStream(),"xinwentupian.jpg");
                                    runOnUiThread(() -> newsImage.setImageDrawable(newsDetailPic));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).start();

                        }
                    }else{
                        TextView newsText = new TextView(NewsDetail.this);
                        newsText.setText(ele.text());
                        runOnUiThread(() -> detailNoPic.addView(newsText,layoutParams));
                    }
                }
                detailText.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
