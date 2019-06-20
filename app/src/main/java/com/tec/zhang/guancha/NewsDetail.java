package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tec.zhang.guancha.Activities.ArticleBase;
import com.tec.zhang.guancha.Activities.CommentAdapter;
import com.tec.zhang.guancha.database.SessionProperty;
import com.tec.zhang.guancha.database.UserProperty;
import com.tec.zhang.guancha.recycler.CommentBean;
import com.tec.zhang.guancha.recycler.ParseHTML;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NewsDetail extends ArticleBase {
    private static final String TAG = "第三页";
    TextView detailText;
    private LinearLayout detailNoPic;
    ParseHTML.GuanChaSouceData newsType;

    private RecyclerView commentList;
    private CommentAdapter adapter;
    //声明codeId
    String codeId;
    //声明csrf变量，代表session状态
    String csrfState = "";
    //声明一个OKhttpClient
    OkHttpClient client;
    //声明令牌token，以便于复用
    String token;
    //Comment_id
    int currentcommentId;
    //评论框
    private EditText commentText;

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
        //初始化要分享图片的标题
        shareTitle = newsType.getTitle();
        //初始化要分享图片
        shareRawBitmap = CodeUtils.createImage(articleUrl,110,110,BitmapFactory.decodeResource(getResources(),R.drawable.guanwang));
        setTitle(newsType.getTitle());
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseDetail(articleUrl,newsType.getNewsType());
            }
        }).start();
        addCommentButtonListener(currentcommentId);
    }
    private void parseDetail(String articleUrl, ParseHTML.NEWS_TYPE type){
        try {
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
                codeId = "";
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
                            //将图片要显示的内容文章初始化
                            if (articleContent.length() < 125 && (! ele.text().startsWith("【"))){
                                articleContent = String.format("%s%s", articleContent, ele.text().trim());
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
                    loadComments(articleUrl);
                }
            }
            //下面进行一次带用户登录状态的请求，为的是得到csrf状态字符串
            client = new OkHttpClient();
            //先拿到用户数据
            List<UserProperty> properties = LitePal.findAll(UserProperty.class);
            if (properties.size() == 0) {
                return;
            }
            //拿到本地的csrf数据
            List<SessionProperty> session = LitePal.findAll(SessionProperty.class);
            if (session.size() != 0) csrfState = session.get(0).getCsrfState();
            //下面拿到用户的token令牌，目测token与设备相关
            UserProperty userProperty = properties.get(0);
            token = userProperty.getUserToken();
            //将网络请求的链接按照观网格式串联起来
            String comtentRequest = "https://app.guancha.cn/news/content?id=" + codeId + "&type=&access-token=" + token;
            Request contentGetRequest;
            //如果有之前的csrf数据，就加入请求头，如果没有就不加
            if (csrfState.equals("")) {
                contentGetRequest = new Request.Builder()
                        .url(comtentRequest)
                        .get()
                        .build();
            }else {
                contentGetRequest = new Request.Builder()
                        .url(comtentRequest)
                        .addHeader("Cookie",csrfState)
                        .get()
                        .build();
            }
            //下面正式进行网络请求
            Call call = client.newCall(contentGetRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //失败暂时不做处理
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //拿到文章的返回头
                    try{
                        String responseHeader = response.header("Set-Cookie","");
                        Log.d(TAG, "onResponse: 返回的csrf数据为" + responseHeader);
                        //存储或者升级csrf数据
                        SessionProperty singleSession = new SessionProperty();
                        singleSession.setCsrfState(responseHeader);
                        if (csrfState.equals("")){
                            csrfState = responseHeader;
                            singleSession.save();
                        }else {
                            csrfState = responseHeader;
                            singleSession.updateAll();
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadComments(String articleUrl){
        List<CommentBean> commentListComtent = new ArrayList<>(50);
        client = new OkHttpClient();
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
                    assert response.body().string() != null;
                    String respond = response.body().string();
                    JSONObject jo = new JSONObject(respond);
                    String hotComment = jo.getString("all_hot_count");
                    Log.d(TAG, "onResponse: 有" + hotComment + "条热评");
                    if (Integer.parseInt(hotComment) != 0){
                        CommentBean headerText = new CommentBean(HEADER_VIEW,0,"","","","",false,"",false,"",false);
                        headerText.setHeaderTitle("热门评论 " + hotComment + " 条");
                        commentListComtent.add(headerText);
                        JSONArray hotComments = jo.getJSONArray("hots");
                        for (int i = 0; i < hotComments.length(); i ++){
                            JSONObject hotCommentBean = hotComments.getJSONObject(i);
                            CommentBean singleBean = new CommentBean(COMMENT_VIEW,hotCommentBean.getInt("id"),hotCommentBean.getString("user_photo"),hotCommentBean.getString("user_nick"),hotCommentBean.getString("created_at"),hotCommentBean.getString("content"),hotCommentBean.getBoolean("has_praise"),hotCommentBean.getInt("praise_num") + "",hotCommentBean.getBoolean("has_tread"),hotCommentBean.getString("tread_num"), hotCommentBean.getInt("parent_id") != 0);
                            if (singleBean.isParentExists()){
                                JSONObject parentComment = hotCommentBean.getJSONArray("parent").getJSONObject(0);
                                singleBean.setParentId(hotCommentBean.getInt("parent_id"));
                                singleBean.setParentUserName(parentComment.getString("user_nick"));
                                singleBean.setParentCommentTime(parentComment.getString("created_at"));
                                singleBean.setParentComment(parentComment.getString("content"));
                                singleBean.setParentDisliked(parentComment.getBoolean("has_tread"));
                                singleBean.setParentDislikedNumber(parentComment.getString("tread_num"));
                                singleBean.setParentUserPraised(parentComment.getBoolean("has_praise"));
                                singleBean.setParentPraisedNumber(parentComment.getInt("praise_num") + "");
                            }
                            Log.d(TAG, "onResponse: 该条评论的id为：" + singleBean.getCommentId()+ "父评论的id为：" + singleBean.getParentId());
                            commentListComtent.add(singleBean);
                        }
                    }
                    if (Integer.parseInt(jo.getString("count")) != 0){
                        CommentBean headerText = new CommentBean(HEADER_VIEW,0,"","","","",false,"",false,"",false);
                        headerText.setHeaderTitle("所有评论 " + jo.getString("count") + " 条");
                        commentListComtent.add(headerText);
                        JSONArray normalComments = jo.getJSONArray("items");
                        for (int i = 0; i < normalComments.length(); i ++){
                            JSONObject normalComment = normalComments.getJSONObject(i);
                            Log.d(TAG, "onResponse: " + normalComment.getString("user_nick"));
                            CommentBean singleBean = new CommentBean(COMMENT_VIEW,normalComment.getInt("id"),normalComment.getString("user_photo"),normalComment.getString("user_nick"),normalComment.getString("created_at"),normalComment.getString("content"),normalComment.getBoolean("has_praise"),normalComment.getInt("praise_num") + "",normalComment.getBoolean("has_tread"),normalComment.getString("tread_num"), normalComment.getInt("parent_id") != 0);
                            if (singleBean.isParentExists()){
                                JSONObject parentComment = normalComment.getJSONArray("parent").getJSONObject(0);
                                singleBean.setParentId(normalComment.getInt("parent_id"));
                                singleBean.setParentUserName(parentComment.getString("user_nick"));
                                singleBean.setParentCommentTime(parentComment.getString("created_at"));
                                singleBean.setParentComment(parentComment.getString("content"));
                                singleBean.setParentDisliked(parentComment.getBoolean("has_tread"));
                                singleBean.setParentDislikedNumber(parentComment.getString("tread_num"));
                                singleBean.setParentUserPraised(parentComment.getBoolean("has_praise"));
                                singleBean.setParentPraisedNumber(parentComment.getInt("praise_num") + "");
                            }
                            Log.d(TAG, "onResponse: 该条评论的id为：" + singleBean.getCommentId()+ "父评论的id为：" + singleBean.getParentId());
                            commentListComtent.add(singleBean);
                        }
                    }
                    runOnUiThread(() -> {
                        commentList = findViewById(R.id.no_pic_recycler);
                        //添加一个Adapter的监听器，用于回复别人的评论
                        CommentAdapter.OnCommentClickListener clickListener = id -> {
                            //将当前的commentId设置为传入的id
                            currentcommentId = id;
                            //使输入框获得焦点
                            commentText.requestFocus();
                            //设置提示的文字
                            commentText.setHint("回复他的评论");
                            //弹出输入法
                            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(commentText,1);
                        };
                        //初始化一个评论列表的适配器
                        adapter = new CommentAdapter(commentListComtent,NewsDetail.this);
                        adapter.setOnCommentClickListener(clickListener);
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

    private void addCommentButtonListener(int commentId){
        //拿到评论框的实例
        commentText = findViewById(R.id.input_comment);
        //拿到发送按钮的实例
        ImageView sendComment = findViewById(R.id.send_comment);
        //设置评论框的输入事件监听器
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果此时输入后的评论不为空，则显示发送按钮
                if (!commentText.getText().toString().equals("")) sendComment.setVisibility(View.VISIBLE);
                else {
                    //为空的话，如果发送按钮没有隐藏，则隐藏发送按钮
                    if (sendComment.getVisibility() == View.VISIBLE) sendComment.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //为发送按钮设置事件监听器
        sendComment.setOnClickListener(v -> {
            //首先检测用户是否登录
            List<UserProperty> properties = LitePal.findAll(UserProperty.class);
            if (properties.size() == 0) {
                Toast.makeText(NewsDetail.this,"您尚未登录，请登录后再评论！",Toast.LENGTH_LONG).show();
                return;
            }
            //点击后将按钮设置为不可点击，否则将重复发送
            sendComment.setClickable(false);
            //此处启动一个动画，旋转图片
            ViewCompat.animate(sendComment)
                    .rotation(720)
                    .setDuration(5000)
                    .withLayer()
                    .setInterpolator(new LinearInterpolator())
                    .start();
            //然后进行一个网络请求，提交评论
            String commentUrl = "https://app.guancha.cn/comment/create?access-token=" + token;
            //构造一个FormBody，填入需要提交的表单
            FormBody form = new FormBody.Builder()
                    .add("access_device","3")
                    .add("parant_id",String.valueOf(commentId))
                    .add("code_id",codeId)
                    .add("type","1")
                    .add("content",commentText.getText().toString())
                    .add("from","cms")
                    .build();
            //构造网络请求
            Request commentRequest = new Request.Builder()
                    .url(commentUrl)
                    .header("Cookie",csrfState)
                    .post(form)
                    .build();
            Call call = client.newCall(commentRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //暂时不处理错误
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try{
                        //拿到返回的json字符串
                        JSONObject respond = new JSONObject(response.body().string());
                        Log.d(TAG, "onResponse: " + respond.toString());
                        //进行判断，如果评论成功，则发送一个吐司
                        if (respond.getString("msg").equals("成功")){
                            runOnUiThread(() -> {
                                Toast.makeText(NewsDetail.this,"评论成功！",Toast.LENGTH_LONG).show();
                                commentText.clearAnimation();
                                commentText.setText("");
                                sendComment.setClickable(true);
                            });

                        }
                        //然后存储csrf数据
                        csrfState = response.header("Set-Cookie","");
                        SessionProperty singleSession = new SessionProperty();
                        singleSession.setCsrfState(csrfState);
                        if (!csrfState.equals("")) singleSession.updateAll();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //判断是否是我们增加的分享按钮，其实此时ToolBar上面只有这一个按钮，所以这里的判断是多余的
        //但是不确定以后是否增加其他的按钮，所以先保留判断
        if (item.getItemId() == R.id.share_pic) {
            if (shareRawBitmap != null) {
                shareArticle(shareRawBitmap, shareTitle, articleContent);
            } else Log.d(TAG, "onOptionsItemSelected: 要分享的图片为空，请排查原因");
        }
        return super.onOptionsItemSelected(item);
    }
}
