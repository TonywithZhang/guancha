package com.tec.zhang.guancha.pages;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.paging.ItemAdapter;
import com.tec.zhang.guancha.paging.ProductionViewModel;
import com.tec.zhang.guancha.recycler.Cards;
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

public class ProductionPage extends Fragment {
    private ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    public void setNewsList(ArrayList<ParseHTML.GuanChaSouceData> newsList) {
        this.newsList.clear();
        this.newsList.addAll(newsList);
    }
    //Cards cards;
    ItemAdapter adapter;
    ProductionViewModel viewModel;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View productionView = inflater.inflate(R.layout.partial_page,container,false);
        RecyclerView recyclerView = productionView.findViewById(R.id.recycler);
        //cards = new Cards(newsList,getActivity());
        adapter = new ItemAdapter(getActivity());
        recyclerView.addItemDecoration(new MyItemDecration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        ViewModelProvider provider = new ViewModelProvider(getViewModelStore(),new ViewModelProvider.AndroidViewModelFactory(Objects.requireNonNull(getActivity()).getApplication()));
        viewModel = provider.get(ProductionViewModel.class);
        viewModel.getLiveData().observe(getActivity(), new Observer<PagedList<ParseHTML.GuanChaSouceData>>() {
            @Override
            public void onChanged(PagedList<ParseHTML.GuanChaSouceData> guanChaSouceData) {
                adapter.submitList(viewModel.getLiveData().getValue());
            }
        });
        return productionView;
    }

    public static ProductionPage newInstance(ArrayList<ParseHTML.GuanChaSouceData> news) {
        Bundle args = new Bundle();
        ProductionPage fragment = new ProductionPage();
        fragment.setArguments(args);
        fragment.setNewsList(news);
        return fragment;
    }
   /* public void updateView(){
        cards.notifyDataSetChanged();
    }*/
}
