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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AutoPage extends Fragment {
    private ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    public void setNewsList(ArrayList<ParseHTML.GuanChaSouceData> newsList) {
        this.newsList.clear();
        this.newsList.addAll(newsList);
    }
    Cards cards;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View autoView = inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = autoView.findViewById(R.id.recycler);
        cards = new Cards(newsList,getActivity());
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cards);
        return autoView;
    }

    public static AutoPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {
        Bundle args = new Bundle();
        AutoPage fragment = new AutoPage();
        fragment.setArguments(args);
        fragment.setNewsList(news);
        return fragment;
    }
    public void updateView(){
        cards.notifyDataSetChanged();
    }
}
