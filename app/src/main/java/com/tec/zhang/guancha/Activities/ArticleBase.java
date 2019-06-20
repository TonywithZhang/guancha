package com.tec.zhang.guancha.Activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.tec.zhang.guancha.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ArticleBase extends AppCompatActivity {

    private static final String TAG = "文章页面里面";

    //文章标题
    protected String shareTitle;
    //部分的正文
    protected String articleContent = "";
    //要分享的图片
    protected Bitmap shareRawBitmap;

    //分享的函数
    public void shareArticle(Bitmap sharePic,String articleTitle,String articleContent){
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

}
