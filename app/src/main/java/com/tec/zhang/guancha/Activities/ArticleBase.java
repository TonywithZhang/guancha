package com.tec.zhang.guancha.Activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tec.zhang.guancha.DetailWithPic;
import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.database.*;
import com.tec.zhang.guancha.recycler.*;

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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ArticleBase extends AppCompatActivity {

    //声明文章的正式内容
    protected TextView detailText;
    //声明文章正式内容的容器
    protected LinearLayout detailFrame;
    //声明评论内容的列表
    protected RecyclerView commentList;
    //声明评论内容的适配器
    protected CommentAdapter adapter;
    protected CommentViewModel commentViewModel;
    //声明评论内容类型
    protected ParseHTML.GuanChaSouceData newsType;
    //声明codeId
    protected String codeId = "";
    //声明csrf变量，代表session状态
    protected String csrfState = "";
    //声明一个OKhttpClient
    protected OkHttpClient client;
    //声明令牌token，以便于复用
    protected String token;
    //Comment_id
    protected int currentcommentId;
    //评论框
    protected EditText commentText;

    //这一种是评论头部内容，即热评和所有评论
    private final int HEADER_VIEW = 1;
    //所有评论
    private final int COMMENT_VIEW = 2;
    private static final String TAG = "新闻页面里 ";
    //文章标题
    protected String shareTitle;
    //部分的正文
    protected String articleContent = "";
    //要分享的图片
    protected Bitmap shareRawBitmap;

    //分享的函数
    protected void shareArticle(Bitmap sharePic,String articleTitle,String articleContent){
        //Log.d(TAG, "shareArticle: 标题是" + articleTitle + "文章内容为：" + articleContent);
        //初始化窗口管理器
        //WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //创建一个屏幕变换的对象
        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //讲屏幕的所有参数传入到屏幕变换的对象中
        //windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        //新建一张位图，用于分享
        Bitmap shareBitmap = Bitmap.createBitmap(410,180,Bitmap.Config.ARGB_8888);
        //新建画布，参数为上面刚刚创建的位图，使用画布在位图上面创建要分享的内容
        Canvas shareCanvas = new Canvas(shareBitmap);
        //将要分享的位图缩放到理想的尺寸
        Bitmap sharingPic = Bitmap.createScaledBitmap(sharePic,110,110,false);
        //新建一个画笔，用于将要分享的内容画到画布上面
        Paint sharePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //先绘制白色底色
        shareCanvas.drawColor(Color.rgb(0xff,0xff,0xff));
        //首先将图片绘制到画布上面的合适位置
        shareCanvas.drawBitmap(sharingPic,(float) (shareBitmap.getWidth() - 108) , (float) (shareBitmap.getHeight() * 0.75 - 68) ,sharePaint);
        //将要分享的文章标题绘制到要分享的图片上面，首先应该设置分享文字的字体和颜色
        sharePaint.setTextSize(18f);
        //然后将文字绘制到画布，有5dp的Margin
        int titleLength = articleTitle.length();
        shareCanvas.drawText(titleLength >22 ? articleTitle.substring(0,22) : articleTitle,5,23,sharePaint);
        //写一部分文章到画布上面，先将画笔字体调小
        //sharePaint.setColor(0xdcdcdc);
        sharePaint.setTextSize(12f);
        //创建一个循环，将文字绘制到画布上面
        int contentLength = articleContent.length();
        //判断内容的行数
        int rowNum = contentLength / 25 > 6 ? 6 : contentLength / 25;
        for (int i = 0 ; i <= rowNum ; i ++){
            int clipEnd = (i + 1) * 25 >= contentLength ? contentLength: (i + 1) * 25;
            shareCanvas.drawText(articleContent.substring(i * 25,clipEnd),5,50 + i * 20,sharePaint);
        }
        //下面的代码将文件保存到本地
        Uri shareLink = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),shareBitmap,null,null));
        String shareUrl = Environment.getExternalStorageDirectory() + "/guancha/";
        File guanchaUrl = new File(shareUrl);
        if (!guanchaUrl.exists()) guanchaUrl.mkdirs();
        String picUrl = shareUrl + String.format("%s.jpg",System.currentTimeMillis());
        picUrl = String.format(picUrl, System.currentTimeMillis());
        Log.d(TAG, "shareArticle: " + picUrl);
        File shareFileUrl = new File(picUrl);
        try {
            shareFileUrl.createNewFile();
            shareBitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(shareFileUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //然后调用Android系统原生的分享接口，分享图片
        Intent intent = new Intent(Intent.ACTION_SEND);//action_send为分享的枚举类型
        //设置所分享内容的类型
        intent.setType("image/*");
        //将要分享的图片的uri传入intent
        intent.putExtra(Intent.EXTRA_STREAM,shareLink);
        Intent shareIntent = Intent.createChooser(intent,"分享文章到：");
        //启动分享的页面
        startActivity(shareIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //将包含分享按钮的菜单映射到页面头部的Toolbar
        getMenuInflater().inflate(R.menu.article_panel,menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void loadComments(String articleUrl){
        //添加一个Adapter的监听器，用于回复别人的评论
        CommentAdapter.OnCommentClickListener clickListener = id -> {
            //将当前的commentId设置为传入的id
            currentcommentId = id;
            //使输入框获得焦点
            commentText.requestFocus();
            //设置提示的文字
            commentText.setHint("回复他的评论");
            //弹出输入法
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(commentText,0);
        };
        //初始化一个评论列表的适配器
        adapter = new CommentAdapter(ArticleBase.this);
        adapter.setOnCommentClickListener(clickListener);
        commentList.setLayoutManager(new LinearLayoutManager(ArticleBase.this));
        commentList.setAdapter(adapter);//链接RecyclerView和适配器
        ViewModelProvider provider = new ViewModelProvider(getViewModelStore(),new ViewModelProvider.AndroidViewModelFactory(Objects.requireNonNull(getApplication())));
        commentViewModel = provider.get(CommentViewModel.class);
        commentViewModel.getNewsList().observe(this, commentBeans -> adapter.submitList(commentViewModel.getNewsList().getValue()));
    }

    /**
     * 转换所要显示的新闻页面，过程包括根据url下载页面，并且用爬虫找到我们需要的信息，并且提取出来
     * 显示在页面上面
     * @param articleUrl 要解析的文章的url
     * @param type 文章的类型，包括是否是风闻类新闻
     * */
    protected void parseDetail(String articleUrl, ParseHTML.NEWS_TYPE type){
        try {
            Map<String, String> header = new HashMap<>();//新建一个字典，用于存储请求头的各种信息
            //设置代理，其实这一步是多余的
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            //接受的类型，这一句也是多余的
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            //接受的语言类型，也是多余的
            header.put("Accept-Language", "zh-cn,zh;q=0.5");
            //设置字符集，这一步很重要
            header.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
            //设置连接状态，这一步也是多余的
            header.put("Connection", "keep-alive");
            //判断，如果文章的链接不为空，则开始爬虫解析
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
                String codeScript;//这个字符串表示codeId所在的一块代码区域
                //通过爬虫，爬取到codeID所在的区域
                Elements scripts = document.select("head").get(0).select("script");
                //然后在循环中，提取出真正的codeID
                for (Element ele : scripts){
                    if (ele.toString().contains("ID")){
                        codeScript = ele.toString();
                        int codeIndex = codeScript.indexOf("ID");
                        codeId = codeScript.substring(codeIndex + 4,codeIndex + 10);
                    }
                }
                //这一步是提取真正的文章url，很多从前一个Activity冲进来的链接并非要看的文章的真正链接
                //需要在下载的HTML网页里面再次提取
                Elements eles = document.select(".last");
                String fullUrl = null;//我感觉自己要词穷了，不知道该取什么名字了
                //因为真正的链接是分散的，所以要拼凑起来
                StringBuilder builder = new StringBuilder();
                if(eles.size() != 0) {
                    //通过搜索下载的html网页，然后对比真正的网页链接，可以知道，真正的链接的组成部分
                    //保存在onclick函数里面的，而onclick函数很多时候不止一个，而且不是每个onclick都有
                    //我们需要的信息，所以要在循环里面，将我们要提取的内容提取出来
                    for(Element ele : eles) {
                        fullUrl = ele.attr("onclick");
                        if (! fullUrl.equals("")) {
                            fullUrl = fullUrl.substring(fullUrl.indexOf("=") + 2,fullUrl.length() -2);
                            builder.append(articleUrl.substring(0,articleUrl.indexOf("/",10))).append(fullUrl);
                            document = Jsoup.connect(builder.toString()).headers(header).get();
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
                //如果codeId不为空就存到数据库
                if (codeId != null && (!codeId.equals(""))){
                    ArticleCodeId articleCodeId = new ArticleCodeId(Integer.parseInt(codeId),articleUrl);
                    articleCodeId.save();
                }
                //List<String> pictureUrls = new ArrayList<>(10);
                //int imageNumber = 0;
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                for (Element ele : articleSegments){
                    if (!ele.text().endsWith("截图")){
                        if (!ele.text().equals("")){
                            TextView newsText = new TextView(ArticleBase.this);
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
                            ImageView newsImage = new ImageView(ArticleBase.this);
                            newsImage.setImageResource(R.drawable.ic_guancha);
                            newsImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            newsImage.setAdjustViewBounds(true);
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
                        TextView newsText = new TextView(ArticleBase.this);
                        newsText.setTextSize(20f);
                        newsText.setTextColor(Color.rgb(0,0,0));
                        String segmentContent = "    " +ele.text();
                        if (ele.text().startsWith("　")) {
                            newsText.setText(ele.text());
                        } else {
                            newsText.setText(segmentContent);
                        }
                        runOnUiThread(() -> detailFrame.addView(newsText,layoutParams));
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
                    runOnUiThread(() -> loadComments(articleUrl));
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
            if (csrfState.contains("HttpOnly")){
                csrfState = csrfState.split(";")[0];
            }
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

    protected void addCommentButtonListener(int commentId){
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
                Toast.makeText(ArticleBase.this,"您尚未登录，请登录后再评论！",Toast.LENGTH_LONG).show();
                return;
            }
            //点击后将按钮设置为不可点击，否则将重复发送
            sendComment.setClickable(false);
            //此处启动一个动画，旋转图片
            ViewCompat.animate(sendComment)
                    .rotation(720)
                    .setDuration(2000)
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
            //重置comment_id
            currentcommentId = 0;
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
                                Toast.makeText(ArticleBase.this,"评论成功！",Toast.LENGTH_LONG).show();
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

}
