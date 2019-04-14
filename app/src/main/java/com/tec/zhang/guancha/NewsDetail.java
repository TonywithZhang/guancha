package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tec.zhang.guancha.Activities.CommentAdapter;
import com.tec.zhang.guancha.recycler.CommentBean;
import com.tec.zhang.guancha.recycler.ParseHTML;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    private RecyclerView commentList;
    private CommentAdapter adapter;

    private final int HEADER_VIEW = 1;
    private final int COMMENT_VIEW = 2;
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
                //提取codeId
                String codeId = "";
                String codeScript;
                Elements scripts = document.select("head").get(0).select("script");
                for (Element ele : scripts){
                    if (ele.toString().contains("ID")){
                        codeScript = ele.toString();
                        int codeIndex = codeScript.indexOf("ID");
                        codeId = codeScript.substring(codeIndex + 4,codeIndex + 10);
                    }
                }
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
                String[] segTagNames = {".all-txt",".article-txt",".article-txt-content"};
                Elements articleSegments = new Elements();
                for (String tagName : segTagNames){
                    articleSegments = document.select(tagName).select("p");
                    if (articleSegments.size() !=0) break;
                }
                if (articleSegments.size() == 0 ){
                    Elements fenWenSeg = document.select("script");
                    String realDocUrl;
                    for (Element element : fenWenSeg) {
                        String script = element.toString();
                        if (script.contains("href")){
                            realDocUrl = element.toString().substring(script.indexOf("href") + 6,script.lastIndexOf("\"")) ;
                            Document fengwenDoc = Jsoup.connect(realDocUrl).get();
                            articleSegments = fengwenDoc.select(segTagNames[2]).select("p");
                            Log.d(TAG, "parseDetail: realUrl = " + realDocUrl);
                            if (realDocUrl.contains("id=")) codeId = articleUrl.substring(articleUrl.indexOf("id=") + 3);
                            break;
                        }
                    }

                }
                Log.d(TAG, "parseDetail: 文章的里面的段落数量为：" + articleSegments.size());
                //List<String> pictureUrls = new ArrayList<>(10);
                //int imageNumber = 0;
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        if (!ele.text().equals("")){
                            TextView newsText = new TextView(NewsDetail.this);
                            newsText.setTextColor(Color.rgb(0,0,0));
                            newsText.setTextSize(20f);
                            String segmentContent = "        " +ele.text();
                            if (ele.text().startsWith("　")) {
                                newsText.setText(ele.text());
                            } else {
                                newsText.setText(segmentContent);
                            }
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
                            newsImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            newsImage.setAdjustViewBounds(true);
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
                        newsText.setTextSize(20f);
                        newsText.setTextColor(Color.rgb(0,0,0));
                        String segmentContent = "    " +ele.text();
                        if (ele.text().startsWith("　")) {
                            newsText.setText(ele.text());
                        } else {
                            newsText.setText(segmentContent);
                        }
                        runOnUiThread(() -> detailNoPic.addView(newsText,layoutParams));
                    }
                }
                runOnUiThread(() -> detailText.setVisibility(View.GONE));
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
                if (!codeId.equals("")){
                    loadComments(articleUrl,codeId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadComments(String articleUrl,String codeId){
        List<CommentBean> commentListComtent = new ArrayList<>(50);
        OkHttpClient client = new OkHttpClient();
        String requestUrl = "https://user.guancha.cn/comment/cmt-list.json?codeId=" + codeId + "&codeType=1&pageNo=1&order=1&ff=www";
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("Referer",articleUrl)
                .addHeader("Accept","application/json, text/javascript, */*; q=0.01")
                .addHeader("Origin","https://www.guancha.cn")
                .addHeader("Accept-Language","zh-CN,zh;q=0.9")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(NewsDetail.this,"请求网络过程中发生错误！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                try{
                    String respond = response.body().string();
                    JSONObject jo = new JSONObject(respond);
                    String hotComment = jo.getString("all_hot_count");
                    Log.d(TAG, "onResponse: 有" + hotComment + "条热评");
                    if (Integer.parseInt(hotComment) != 0){
                        CommentBean headerText = new CommentBean(HEADER_VIEW,"","","","",false,"",false,"",false);
                        headerText.setHeaderTitle("热门评论 " + hotComment + " 条");
                        commentListComtent.add(headerText);
                        JSONArray hotComments = jo.getJSONArray("hots");
                        for (int i = 0; i < hotComments.length(); i ++){
                            JSONObject hotCommentBean = hotComments.getJSONObject(i);
                            CommentBean singleBean = new CommentBean(COMMENT_VIEW,hotCommentBean.getString("user_photo"),hotCommentBean.getString("user_nick"),hotCommentBean.getString("created_at"),hotCommentBean.getString("content"),hotCommentBean.getBoolean("has_praise"),hotCommentBean.getInt("praise_num") + "",hotCommentBean.getBoolean("has_tread"),hotCommentBean.getString("tread_num"), hotCommentBean.getInt("parent_id") != 0);
                            if (singleBean.isParentExists()){
                                JSONObject parentComment = hotCommentBean.getJSONArray("parent").getJSONObject(0);
                                singleBean.setParentUserName(parentComment.getString("user_nick"));
                                singleBean.setParentCommentTime(parentComment.getString("created_at"));
                                singleBean.setParentComment(parentComment.getString("content"));
                                singleBean.setParentDisliked(parentComment.getBoolean("has_tread"));
                                singleBean.setParentDislikedNumber(parentComment.getString("tread_num"));
                                singleBean.setParentUserPraised(parentComment.getBoolean("has_praise"));
                                singleBean.setParentPraisedNumber(parentComment.getInt("praise_num") + "");
                            }
                            commentListComtent.add(singleBean);
                        }
                    }
                    if (Integer.parseInt(jo.getString("count")) != 0){
                        CommentBean headerText = new CommentBean(HEADER_VIEW,"","","","",false,"",false,"",false);
                        headerText.setHeaderTitle("所有评论 " + jo.getString("count") + " 条");
                        commentListComtent.add(headerText);
                        JSONArray normalComments = jo.getJSONArray("items");
                        for (int i = 0; i < normalComments.length(); i ++){
                            JSONObject normalComment = normalComments.getJSONObject(i);
                            Log.d(TAG, "onResponse: " + normalComment.getString("user_nick"));
                            CommentBean singleBean = new CommentBean(COMMENT_VIEW,normalComment.getString("user_photo"),normalComment.getString("user_nick"),normalComment.getString("created_at"),normalComment.getString("content"),normalComment.getBoolean("has_praise"),normalComment.getInt("praise_num") + "",normalComment.getBoolean("has_tread"),normalComment.getString("tread_num"), normalComment.getInt("parent_id") != 0);
                            if (singleBean.isParentExists()){
                                JSONObject parentComment = normalComment.getJSONArray("parent").getJSONObject(0);
                                singleBean.setParentUserName(parentComment.getString("user_nick"));
                                singleBean.setParentCommentTime(parentComment.getString("created_at"));
                                singleBean.setParentComment(parentComment.getString("content"));
                                singleBean.setParentDisliked(parentComment.getBoolean("has_tread"));
                                singleBean.setParentDislikedNumber(parentComment.getString("tread_num"));
                                singleBean.setParentUserPraised(parentComment.getBoolean("has_praise"));
                                singleBean.setParentPraisedNumber(parentComment.getInt("praise_num") + "");
                            }
                            commentListComtent.add(singleBean);
                        }
                    }
                    runOnUiThread(() -> {
                        commentList = findViewById(R.id.no_pic_recycler);
                        adapter = new CommentAdapter(commentListComtent);
                        commentList.setLayoutManager(new LinearLayoutManager(NewsDetail.this));
                        commentList.setAdapter(adapter);
                        Log.d(TAG, "onResponse: 评论列条的数量为：" + commentListComtent.size());
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
