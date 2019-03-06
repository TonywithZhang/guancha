package com.tec.zhang.guancha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailWithPic extends AppCompatActivity {
    private ImageView newsPic;
    private TextView detailText;
    private LinearLayout detailFrame;
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
        detailFrame = findViewById(R.id.detail_frame);
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
                //List<String> pictureUrls = new ArrayList<>(10);
                //int imageNumber = 0;
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        if (!ele.text().equals("")){
                            TextView newsText = new TextView(DetailWithPic.this);
                            newsText.setText(ele.text());
                            runOnUiThread(() -> detailFrame.addView(newsText,layoutParams));
                        }
                        //article.append(ele.text()).append("\n");
                        Elements pictures = ele.select("img");
                        if (pictures.size() != 0){
                            Element pic = pictures.get(0);
                            //article.append("placeHolder").append(imageNumber).append("\n");
                            //imageNumber ++;
                            String imageUrl = pic.attr("abs:src");
                            //pictureUrls.add(imageUrl);
                            ImageView newsImage = new ImageView(DetailWithPic.this);
                            newsImage.setImageResource(R.drawable.ic_guancha);
                            runOnUiThread(() -> detailFrame.addView(newsImage,layoutParams));
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
                        TextView newsText = new TextView(DetailWithPic.this);
                        newsText.setText(ele.text());
                        runOnUiThread(() -> detailFrame.addView(newsText,layoutParams));
                    }
                }
                detailText.setVisibility(View.GONE);

                /*final SpannableString spannableString = new SpannableString(article);
                if (pictureUrls.size() != 0){
                    int index = 0;
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int windowWidth = metrics.widthPixels;
                    Log.d(TAG, "parseDetail: 显示屏的宽度是" + windowWidth);
                    for (String pic : pictureUrls){
                        URL pictureURL = new URL(pic);
                        Drawable articlePic = Drawable.createFromStream(pictureURL.openStream(),"xinwentupian.jpg");
                        float scale = ((float)articlePic.getIntrinsicHeight())/articlePic.getIntrinsicWidth();
                        Log.d(TAG, "parseDetail: 图片的纵横比例为：" + scale + "图片高度为:" + articlePic.getIntrinsicHeight() + "图片宽度为：" + articlePic.getIntrinsicWidth());
                        articlePic.setBounds(0,0,windowWidth, (int) (windowWidth * scale));
                        Log.d(TAG, "parseDetail: 图片显示出来的宽度是" + articlePic.getBounds().right);
                        ImageSpan newsPic = new ImageSpan(articlePic);
                        //Log.d(TAG, "parseDetail: " + pic);
                        int firstIndex = article.indexOf("placeHolder" + index);
                        index ++;
                        spannableString.setSpan(newsPic,firstIndex,firstIndex + "placeHolder1".length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        Log.d(TAG, "parseDetail: 添加了一张图片");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        detailText.setText(spannableString);
                    }
                });*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
