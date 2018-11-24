package com.tec.zhang.guancha.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.recycler.Cards;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.NewsSingle;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainPage extends Fragment {
    //private boolean finished = false;
    //private ParseHTML parseHTML;

    private void setNewsSingles(List<ParseHTML.GuanChaSouceData> newsSingles) {
        this.newsSingles = newsSingles;
    }

    private List<ParseHTML.GuanChaSouceData> newsSingles = new ArrayList<>();

    public static MainPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {

        Bundle args = new Bundle();
        args.putParcelableArrayList("news",news);
        MainPage fragment = new MainPage();
        fragment.setArguments(args);
        fragment.setNewsSingles(news);
        return fragment;
    }

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
        Cards cards = new Cards(newsSingles);
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cards);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        });
        //finished = false;
        return view;
    }

}
