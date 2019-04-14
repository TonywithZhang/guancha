package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.database.UserProperty;
import com.tec.zhang.guancha.pages.AutoPage;
import com.tec.zhang.guancha.pages.FengWenPage;
import com.tec.zhang.guancha.pages.FinancialPage;
import com.tec.zhang.guancha.pages.FragmentIndicator;
import com.tec.zhang.guancha.pages.InternationalPage;
import com.tec.zhang.guancha.pages.LeadingAheadPage;
import com.tec.zhang.guancha.pages.MainPage;
import com.tec.zhang.guancha.pages.MilitaryPage;
import com.tec.zhang.guancha.pages.ProductionPage;
import com.tec.zhang.guancha.pages.TecnologyPage;
import com.tec.zhang.guancha.pages.VideoPage;
import com.tec.zhang.guancha.recycler.Cards;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.NewsSingle;
import com.tec.zhang.guancha.recycler.ParseHTML;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurfPage extends BaseActivity {
    private static final String TAG = "主视图里面  ";
    //观网各个板块的名字
    private final String[] MODULES = {
            "首页","风闻","国际","军事","财经","产经","科技","汽车","视频"
    };

    //private ParseHTML guanchaWeb;
    //是否转化完毕的标志位。已经弃用
    //private static boolean parseFinishFlage = false;
    private MainPage mainPage;//首页
    private FengWenPage fengWenPage;//风闻页
    private InternationalPage internationalPage;//国际页面
    private MilitaryPage militaryPage;//军事页面
    private FinancialPage financialPage;//财经页面
    private ProductionPage productionPage;//产经页面
    private TecnologyPage tecnologyPage;//科技页面
    private AutoPage autoPage;//汽车页面
    //private LeadingAheadPage leadingAheadPage;
    private VideoPage videoPage;//视频页面
    private DrawerLayout drawer;
    private NavigationView navigation;
    CircleImageView userHeader;
    //Bitmap userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main_page);
        //rececle = findViewById(R.id.recycler);
        //设置用户界面的显示
        setupUserWindow();
        //拿到toolbar实例
        Toolbar toolbar = findViewById(R.id.toolbar);
        //将界面上的actionbar设置为得到的toolBar
        setSupportActionBar(toolbar);
        //拿到pagerview实例
        ViewPager pager = findViewById(R.id.view_pager);
        //拿到tablayout实例
        TabLayout tabLayout = findViewById(R.id.tablayout);
        //tabLayout.setTabTextColors(0xffff00,0xff0000);
        //请求本地磁盘存储权限
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},0x1);
        /*guanchaWeb = new ParseHTML();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //guanchaWeb.init();
                //guanchaWeb.getHeadLine();
                //guanchaWeb.createNormalNews();
                //guanchaWeb.getImportantNews().addAll(guanchaWeb.getNormalNews());
                //guanchaWeb.createModuleUrls();
                //guanchaWeb.parseFengwen();
                //guanchaWeb.createInternationalNews();
                //guanchaWeb.createMilitaryNews();
                //guanchaWeb.createFinancialNews();
                //guanchaWeb.createProductionNews();
                //guanchaWeb.createTecnologyNews();
                //guanchaWeb.createAutoNews();
                //guanchaWeb.createLeadAheadNews();
                //guanchaWeb.createVideoNews();
                parseFinishFlage = true;
            }
        }).start();*/
        //将所有页面进行初始化
        mainPage = new MainPage();
        fengWenPage = new FengWenPage();
        internationalPage = new InternationalPage();
        militaryPage = new MilitaryPage();
        financialPage = new FinancialPage();
        productionPage = new ProductionPage();
        tecnologyPage = new TecnologyPage();
        autoPage = new AutoPage();
        //leadingAheadPage = new LeadingAheadPage();
        videoPage = new VideoPage();
        /*try {
            while (!parseFinishFlage) Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //创建一个列表，用于存储这些页面对应的标签
        List<String> fragmentNames = new ArrayList<>(16);
        for (String MODULE : MODULES) {
            tabLayout.addTab(tabLayout.newTab().setText(MODULE));
            fragmentNames.add(MODULE);
        }
        //创建列表，存储页面
        List<Fragment> fragments = new ArrayList<>(16);
        fragments.add(mainPage);
        fragments.add(fengWenPage);
        fragments.add(internationalPage);
        fragments.add(militaryPage);
        fragments.add(financialPage);
        fragments.add(productionPage);
        fragments.add(tecnologyPage);
        fragments.add(autoPage);
        //fragments.add(leadingAheadPage);
        fragments.add(videoPage);
        //实例化一个适配器，将pagerlayout和tabview连接起来
        FragmentIndicator fragmentAdapter = new FragmentIndicator(getSupportFragmentManager(), fragments, fragmentNames);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);
        //new Thread(displayItems).start();
    }
    /*Runnable displayItems = new Runnable() {
        @Override
        public void run() {
            while (!parseFinishFlage) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //mainPage.setNewsSingles(guanchaWeb.getImportantNews());
            //fengWenPage.setNewsList(guanchaWeb.getFengwenList());
            //internationalPage.setNewsList(guanchaWeb.getInternationalNews());
            //militaryPage.setNewsList(guanchaWeb.getMilitaryNews());
            //financialPage.setNewsList(guanchaWeb.getFinancialNews());
            //productionPage.setNewsList(guanchaWeb.getProductionNews());
            //tecnologyPage.setNewsList(guanchaWeb.getTecnologyNews());
            //autoPage.setNewsList(guanchaWeb.getAutoNews());
            //leadingAheadPage.setNewsList(guanchaWeb.getLeadAheadNews());
            //videoPage.setNewsList(guanchaWeb.getVideoNews());
            *//*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainPage.updateView();
                    fengWenPage.updateView();
                    internationalPage.updateView();
                    militaryPage.updateView();
                    financialPage.updateView();
                    productionPage.updateView();
                    tecnologyPage.updateView();
                    autoPage.updateView();
                    leadingAheadPage.updateView();
                    videoPage.updateView();
                }
            });*//*
        }
    };
*/
    /*
    * 显示屏幕右上角的菜单
    * 但是因为需求问题，暂时只是显示菜单，但是并未进行相关事件监听
    * **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_with_pic,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //设置用户界面的方法
    public void setupUserWindow(){
        //拿到drawerlayout 和navigation layout的实例
        drawer = findViewById(R.id.drawer);
        navigation = drawer.findViewById(R.id.navigation);
        //此方法将Item上面自身的图标颜色显示出来
        navigation.setItemIconTintList(null);
        //拿到header view的实例
        View headerView = navigation.getHeaderView(0);
        //声明一个UserProperty变量
        UserProperty userProperty;
        //声明一个列表，用于存储查询的返回值
        List<UserProperty> userList;
        //进行判断
        //如果列表不为空，则取出数据，进行用户界面的初始过
        if ((userList = LitePal.findAll(UserProperty.class)).size() != 0){
            //去除用户数据，用户数据将设计为永远只有一个
            userProperty = userList.get(0);
            //将登录的界面隐藏
            headerView.findViewById(R.id.pre_login_layout).setVisibility(View.GONE);
            //拿到用户头像的实例
            userHeader = headerView.findViewById(R.id.user_header);
            /*new Thread(() -> {
                try {
                    userImage = BitmapFactory.decodeStream(new URL(userProperty.getImageUrl()).openStream());
                    runOnUiThread(() -> userHeader.setImageBitmap(userImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();*/
            //将用户头像显示出来
            Picasso.get().load(userProperty.getImageUrl()).placeholder(R.drawable.ic_guancha).into(userHeader);
            //拿到用户名的实例
            TextView userName = headerView.findViewById(R.id.user_name);
            //显示用户名
            userName.setText(userProperty.getUserNickName());
            //拿到用户描述的实例
            TextView userDesc = headerView.findViewById(R.id.personal_signature);
            //将用户描述的内容显示出来
            userDesc.setText(userProperty.getUserDescription());
            //拿到用户邮箱的实例
            TextView userMail = headerView.findViewById(R.id.mail_address);
            //将用户邮箱显示出来
            userMail.setText(userProperty.getMobileNum());
        }else {
            //如果列表为空，则表明用户未登录，显示登录界面
            //下面这一句将用户信息的界面隐藏
            headerView.findViewById(R.id.user_info_card).setVisibility(View.GONE);
            //设置用户登录界面的点击事件监听器
            headerView.findViewById(R.id.pre_login_layout).setOnClickListener(v -> {
                //创建一个popupWindow，用作用户登陆页面的控件
                PopupWindow loginWindow = new PopupWindow(this);
                //将设计好的用户登陆界面映射出来
                View loginLayout = LayoutInflater.from(this).inflate(R.layout.login_layout,null);
                //拿到用户名输入的editText引用
                EditText userNameInput = loginLayout.findViewById(R.id.et_login_username);
                //拿到用户密码输入的editText引用
                EditText passwordInput = loginLayout.findViewById(R.id.et_login_pwd);
                //拿到登录按键的引用
                Button loginEx = loginLayout.findViewById(R.id.bt_login_submit);
                //拿到注册按键的引用，目前先搁置此按键的事件监听
                Button registerEx = loginLayout.findViewById(R.id.bt_login_register);
                //拿到记住密码的checkBox还有忘记密码的TextView，同样目前搁置这两个控件的事件监听
                CheckBox rememberPassword = loginLayout.findViewById(R.id.cb_remember_login);
                TextView forgetPassword = loginLayout.findViewById(R.id.tv_login_forget_pwd);
                /**
                 * 给登录键设置事件监听
                 * */
                loginEx.setOnClickListener(view -> {
                    //判断用户名或者密码是否为空
                    if (userNameInput.getText().toString().equals("") || passwordInput.getText().toString().equals("")){
                        //如果为空，则弹出snackbar提示用户
                        Snackbar tip = Snackbar.make(loginLayout,"用户名或者密码不能为空！",Snackbar.LENGTH_LONG);
                        //设置一个action，如果点击提示，则snackbar消失
                        tip.setAction("好哒",action -> {
                            tip.dismiss();
                        });
                        //显示snackbar
                        tip.show();
                        //如果用户名密码任意一个为空，则直接返回，剩下代码不执行
                        return;
                    }
                    //拿到用户输入的用户名字符串
                    String userName = userNameInput.getText().toString();
                    //拿到用户输入的密码字符串
                    String password = passwordInput.getText().toString();
                    //将界面上的圆形进度条显示出来
                    ProgressBar loginProgress = loginLayout.findViewById(R.id.login_progress);
                    loginProgress.setVisibility(View.VISIBLE);
                    //创建OKhttp客户端，开始进行网络请求，进行登录操作
                    OkHttpClient client = new OkHttpClient();
                    //构建requestBody，将用户名密码等信息以键值对形式填入表单
                    FormBody requestBody = new FormBody.Builder()
                            .add("username",userName)
                            .add("password",password)
                            .add("from","3")
                            .add("phone_code","86")
                            .build();
                    //构建请求
                    Request request = new Request.Builder()
                            .url("https://app.guancha.cn/user/login")
                            .post(requestBody)
                            .build();
                    //构建call实例
                    Call loginCall = client.newCall(request);
                    //异步请求网络，得到返回信息
                    loginCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //登录不成功，提示用户账号密码错误
                            runOnUiThread(() -> loginProgress.setVisibility(View.GONE));
                            Snackbar.make(loginLayout,"发生未知错误！！！",Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //拿到返回的json字符串
                            String respondJson = response.body().string();
                            Log.d(TAG, "onResponse: 返回的数据为：" + respondJson);
                            try{
                                //将字符串封装进json对象中
                                JSONObject loginRespond = new JSONObject(respondJson);
                                //msg信息代表是否登录成功，如果不成功，则提示用户
                                if (!loginRespond.getString("msg").equals("成功")){
                                    //取消进度条
                                    runOnUiThread(() -> loginProgress.setVisibility(View.GONE));
                                    //提示用户账号密码错误
                                    Toast.makeText(SurfPage.this,"用户名或者密码错误！",Toast.LENGTH_LONG).show();
                                    //直接返回
                                    return;
                                }
                                //登录成功的话就将弹出框取消
                                //然后更新header视图
                                //将用户信息展示出来
                                //如果成功，则提取用户信息
                                UserProperty returnedProperty = new UserProperty();
                                //拿到返回字符串中的子json，里面含有用户的主要信息
                                JSONObject userData = loginRespond.getJSONObject("data");
                                //token目测为与设备有关的信息，与用户不是绑定的
                                returnedProperty.setUserToken(userData.getString("token"));
                                //拿到userid
                                returnedProperty.setUserId(userData.getInt("uid") + "");
                                //拿到子子json，里面主要是与用户紧相关的信息
                                JSONObject userSegment = userData.getJSONObject("user");
                                //设置用户的头像路径
                                returnedProperty.setImageUrl(userSegment.getString("avatar"));
                                //设置用户评论数
                                returnedProperty.setCommentCount(userSegment.getInt("comment_count"));
                                //拿到手机号
                                returnedProperty.setMobileNum(userSegment.getString("mobile"));
                                //拿到电话区号
                                returnedProperty.setPhoneCode(userSegment.getInt("phone_code"));
                                //拿到用户描述
                                returnedProperty.setUserDescription(userSegment.getString("user_description"));
                                //拿到等级logo
                                returnedProperty.setUserLevelLogo(userSegment.getString("user_level_logo"));
                                //拿到用户昵称
                                returnedProperty.setUserNickName(userSegment.getString("user_nick"));
                                //拿到messagecount
                                returnedProperty.setMessageCount(userSegment.getInt("msg_count"));
                                //保存用户账号信息
                                returnedProperty.save();
                                runOnUiThread(() -> {
                                    //隐藏登录窗口
                                    loginWindow.dismiss();
                                    //通知用户登陆成功
                                    Toast.makeText(SurfPage.this,"登陆成功！",Toast.LENGTH_LONG).show();
                                    headerView.findViewById(R.id.pre_login_layout).setVisibility(View.GONE);
                                    //取消用户信息界面的隐藏
                                    headerView.findViewById(R.id.user_info_card).setVisibility(View.VISIBLE);
                                    //将用户头像展示出来
                                    userHeader  = headerView.findViewById(R.id.user_header);
                                    Picasso.get().load(returnedProperty.getImageUrl()).into(userHeader);
                                    Log.d(TAG, "onResponse: 用户的头像地址为：" + returnedProperty.getImageUrl());
                                    //展示用户昵称
                                    TextView userName = headerView.findViewById(R.id.user_name);
                                    userName.setText(returnedProperty.getUserNickName());
                                    //展示用户描述
                                    TextView userDesc = headerView.findViewById(R.id.personal_signature);
                                    userDesc.setText(returnedProperty.getUserDescription());
                                    //展示用户手机号码
                                    TextView userMail = headerView.findViewById(R.id.mail_address);
                                    userMail.setText(returnedProperty.getMobileNum());
                                });
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                });
                //将popupwindow的内容设置为映射出来的loginlayout
                loginWindow.setContentView(loginLayout);
                //设置loginwindow的底色
                loginWindow.setBackgroundDrawable(new ColorDrawable(0xb0808080));
                //将loginwindow设置为可聚焦的
                loginWindow.setFocusable(true);
                //设置login window以外是可以触摸的，触摸触发login window的退出
                loginWindow.setOutsideTouchable(true);
                //设置login window的宽度，宽度为屏幕宽度
                loginWindow.setWidth(getWindow().getDecorView().getWidth());
                //设置login window的高度，高度为屏幕高度
                loginWindow.setHeight(getWindow().getDecorView().getHeight());
                //设置login window的显示方式
                loginWindow.showAsDropDown(loginLayout);
                //设置login window的显示位置，这个方法过后，login window会立即显示
                loginWindow.showAtLocation(loginLayout, Gravity.CENTER,0,0);
            });
        }
    }
}
