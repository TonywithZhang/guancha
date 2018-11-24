package com.tec.zhang.guancha.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.recycler.Cards;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InternationalPage extends Fragment {
    private static final String TAG = "国际新闻里面";
    public void setNewsList(ArrayList<ParseHTML.GuanChaSouceData> newsList) {
        this.newsList = newsList;
    }

    private ArrayList<ParseHTML.GuanChaSouceData> newsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View internationalView= inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = internationalView.findViewById(R.id.recycler);
        Cards cards = new Cards(newsList);
        Log.d(TAG, "onCreateView: " + newsList.size());
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cards);
        return internationalView;
    }

    public static InternationalPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {
        Bundle args = new Bundle();
        InternationalPage fragment = new InternationalPage();
        fragment.setArguments(args);
        fragment.setNewsList(news);
        return fragment;
    }
}
