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
import com.tec.zhang.guancha.Activities.ArticleBase;
import com.tec.zhang.guancha.Activities.CommentAdapter;
import com.tec.zhang.guancha.database.SessionProperty;
import com.tec.zhang.guancha.database.UserProperty;
import com.tec.zhang.guancha.recycler.CommentBean;
import com.tec.zhang.guancha.recycler.ParseHTML;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DetailWithPic extends ArticleBase {
    //声明页面顶部的图片
    private ImageView newsPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_with_pic);
        //拿到toolBar的实例
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //将actionBar设置为toolbar
        setSupportActionBar(toolbar);
        //拿到顶部图片的实例
        newsPic = findViewById(R.id.detail_image);
        //拿到新闻内容的实例
        detailText = findViewById(R.id.detail_text_pic);
        //拿到评论内容容器的实例
        detailFrame = findViewById(R.id.detail_frame);
        //拿到评论列表的实例
        commentList = findViewById(R.id.comment_list);
        //拿到从上一个activity传过来的内容，主要是图片，文章链接等等
        Intent intent = getIntent();
        //拿到图片
        byte[] pic = intent.getByteArrayExtra("pic");
        //将图片显示出来
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
        //拿到文章链接
        final String articleUrl = getIntent().getStringExtra("articleUrl");
        Log.d(TAG, "onCreate: 文章地址为：" + articleUrl);
        //拿到文章类型，即军事，汽车，产经，财经等等分类
        newsType = getIntent().getParcelableExtra("news");
        //初始化要分享图片的标题
        shareTitle = newsType.getTitle();
        //初始化要分享图片
        shareRawBitmap = CodeUtils.createImage(articleUrl,110,110,BitmapFactory.decodeResource(getResources(),R.drawable.guanwang));
        //下面的内容为使用spannablestring将标题格式化，设置为黑色等等
        SpannableString ss = new SpannableString(newsType.getTitle());
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);
        ss.setSpan(fcs,0,ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(ss);
        //setTitleColor(0x000000);
        //进行文章内容的爬取，和解析还有评论内容的爬取
        new Thread(() -> parseDetail(articleUrl,newsType.getNewsType())).start();
        //添加评论面板的事件监听
        addCommentButtonListener(currentcommentId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //判断是否是我们增加的分享按钮，其实此时ToolBar上面只有这一个按钮，所以这里的判断是多余的
        //但是不确定以后是否增加其他的按钮，所以先保留判断
        if (item.getItemId() == R.id.share_pic){
            if (shareRawBitmap != null){
                shareArticle(shareRawBitmap,shareTitle,articleContent);
            }else Log.d(TAG, "onOptionsItemSelected: 要分享的图片为空，请排查原因");
        }
        return super.onOptionsItemSelected(item);
    }

}
