package com.tec.zhang.guancha.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.recycler.Cards;
import com.tec.zhang.guancha.recycler.MyItemDecration;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TecnologyPage extends Fragment {
    private ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    public void setNewsList(ArrayList<ParseHTML.GuanChaSouceData> newsList) {
        this.newsList.clear();
        this.newsList.addAll(newsList);
    }
    Cards cards;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View tecnologyView = inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = tecnologyView.findViewById(R.id.recycler);
        cards = new Cards(newsList,getActivity());
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cards);
        return tecnologyView;
    }

    public static TecnologyPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {
        Bundle args = new Bundle();
        TecnologyPage fragment = new TecnologyPage();
        fragment.setArguments(args);
        fragment.setNewsList(news);
        return fragment;
    }
    public void updateView(){
        cards.notifyDataSetChanged();
    }
}