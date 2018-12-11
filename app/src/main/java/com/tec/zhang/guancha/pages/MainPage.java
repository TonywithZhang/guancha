package com.tec.zhang.guancha.pages;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.paging.ItemAdapter;
import com.tec.zhang.guancha.paging.MainPageNewsViewModel;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainPage extends Fragment {
    //private boolean finished = false;
    //private ParseHTML parseHTML;
    //Cards cards;
    ItemAdapter adapter;
    MainPageNewsViewModel model;
/*
    public static boolean getModulesFinished = false;
    public void setNewsSingles(List<ParseHTML.GuanChaSouceData> newsSingles) {
        this.newsSingles.clear();
        this.newsSingles.addAll(newsSingles);
    }

    private List<ParseHTML.GuanChaSouceData> newsSingles = new ArrayList<>();
*/

    public static MainPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {

        Bundle args = new Bundle();
        args.putParcelableArrayList("news",news);
        MainPage fragment = new MainPage();
        fragment.setArguments(args);
        //fragment.setNewsSingles(news);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        /*parseHTML = new ParseHTML();
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseHTML.init();
                parseHTML.getHeadLine();
                parseHTML.createNormalNews();
                parseHTML.getImportantNews().addAll(parseHTML.getNormalNews());
                finished = true;
            }
        }).start();
        while (!finished) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        //cards = new Cards(newsSingles,getActivity());
        adapter = new ItemAdapter(getActivity());
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        ViewModelProvider provider = new ViewModelProvider(getViewModelStore(),new ViewModelProvider.AndroidViewModelFactory(Objects.requireNonNull(getActivity()).getApplication()));
        model = provider.get(MainPageNewsViewModel.class);
        model.getNewsList().observe(getActivity(), new Observer<PagedList<ParseHTML.GuanChaSouceData>>() {
            @Override
            public void onChanged(PagedList<ParseHTML.GuanChaSouceData> guanChaSouceData) {
                adapter.submitList(model.getNewsList().getValue());
            }
        });
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(! recyclerView.canScrollVertically(1)){

                }
            }
        });*/
        //finished = false;
        return view;
    }
    public void updateView(){
        //Log.d(TAG, "updateView: 更新视图方法被执行，新闻列表里面的新闻数量为" + newsSingles.size());
        adapter.notifyDataSetChanged();
    }
}
