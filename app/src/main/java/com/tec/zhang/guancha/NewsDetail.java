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
    private TextView detailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        final String articleUrl = getIntent().getStringExtra("articleUrl");
        //初始化评论列表
        commentList = findViewById(R.id.no_pic_recycler);
        detailFrame = findViewById(R.id.detail_no_pic_frame);
        detailText = findViewById(R.id.news_detail_text);
        newsType = getIntent().getParcelableExtra("news");
        Log.d(TAG, "onCreate: " + newsType.getTitle());
        //初始化要分享图片的标题
        shareTitle = newsType.getTitle();
        //初始化要分享图片
        shareRawBitmap = CodeUtils.createImage(articleUrl,110,110,BitmapFactory.decodeResource(getResources(),R.drawable.guanwang));
        setTitle(newsType.getTitle());
        new Thread(() -> parseDetail(articleUrl,newsType.getNewsType())).start();
        addCommentButtonListener(currentcommentId);
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
