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

public class MilitaryPage extends Fragment {
    private ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    public void setNewsList(ArrayList<ParseHTML.GuanChaSouceData> newsList) {
        this.newsList = newsList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View militaryView = inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = militaryView.findViewById(R.id.recycler);
        Cards cards = new Cards(newsList);
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cards);
        return militaryView;
    }

    public static MilitaryPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {
        Bundle args = new Bundle();
        MilitaryPage fragment = new MilitaryPage();
        fragment.setArguments(args);
        fragment.setNewsList(news);
        return fragment;
    }
}
