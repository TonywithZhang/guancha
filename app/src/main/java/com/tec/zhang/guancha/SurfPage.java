package com.tec.zhang.guancha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.tabs.TabLayout;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurfPage extends AppCompatActivity {
    private static final String TAG = "主视图里面  ";
    private final String[] MODULES = {
            "首页","风闻","国际","军事","财经","产经","科技","汽车","智库前沿","视频"
    };
    private List<String> fragmentNames;
    private List<Fragment> fragments;
    //private WebView webView;
    //private RecyclerView rececle;
    private ParseHTML guanchaWeb;
    private boolean parseFinishFlage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_page);
        //rececle = findViewById(R.id.recycler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager pager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        //tabLayout.setTabTextColors(0xffff00,0xff0000);
        guanchaWeb = new ParseHTML();
        new Thread(new Runnable() {
            @Override
            public void run() {
                guanchaWeb.init();
                guanchaWeb.getHeadLine();
                guanchaWeb.createNormalNews();
                guanchaWeb.getImportantNews().addAll(guanchaWeb.getNormalNews());
                guanchaWeb.createModuleUrls();
                guanchaWeb.parseFengwen();
                guanchaWeb.createInternationalNews();
                guanchaWeb.createMilitaryNews();
                guanchaWeb.createFinancialNews();
                guanchaWeb.createProductionNews();
                guanchaWeb.createTecnologyNews();
                guanchaWeb.createAutoNews();
                guanchaWeb.createLeadAheadNews();
                guanchaWeb.createVideoNews();
                parseFinishFlage = true;
            }
        }).start();
        try {
            while (!parseFinishFlage) Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fragmentNames = new ArrayList<>(16);
        for (String MODULE : MODULES) {
            tabLayout.addTab(tabLayout.newTab().setText(MODULE));
            //Log.d(TAG, "onCreate: "  + MODULE);
            fragmentNames.add(MODULE);
        }
        fragments = new ArrayList<>(16);
        fragments.add(MainPage.newInstance(guanchaWeb.getImportantNews()));
        fragments.add(FengWenPage.newInstance(guanchaWeb.getFengwenList()));
        fragments.add(InternationalPage.newInstance(guanchaWeb.getInternationalNews()));
        fragments.add(MilitaryPage.newInstance(guanchaWeb.getMilitaryNews()));
        fragments.add(FinancialPage.newInstance(guanchaWeb.getFinancialNews()));
        fragments.add(ProductionPage.newInstance(guanchaWeb.getProductionNews()));
        fragments.add(TecnologyPage.newInstance(guanchaWeb.getTecnologyNews()));
        fragments.add(AutoPage.newInstance(guanchaWeb.getAutoNews()));
        fragments.add(LeadingAheadPage.newInstance(guanchaWeb.getLeadAheadNews()));
        fragments.add(VideoPage.newInstance(guanchaWeb.getVideoNews()));

        /*for (String s : fragmentNames){
            Log.d(TAG, "onCreate: " + s);
        }*/
        FragmentIndicator fragmentAdapter = new FragmentIndicator(getSupportFragmentManager(),fragments,fragmentNames);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);
        /*for (int i = 0; i < fragmentNames.size() ; i ++){
            Log.d(TAG, "onCreate: " + Objects.requireNonNull(tabLayout.getTabAt(i)).getText());
        }*/

    }
}
