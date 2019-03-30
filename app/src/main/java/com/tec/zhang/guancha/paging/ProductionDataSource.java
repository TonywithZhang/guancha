package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class ProductionDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    ProductionDataSource(){
        super();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createProductionNews("https://www.guancha.cn/chanjing?s=dhchanjing");
        newsList = parseHTML.getProductionNews();
        List<ParseHTML.GuanChaSouceData> productionNews = new ArrayList<>();
        for (int i = 0 ; i < params.requestedLoadSize ; i ++) productionNews.add(newsList.get(i));
        callback.onResult(productionNews,startPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> productionNews = new ArrayList<>();
        int dataLength = newsList.size() < params.loadSize + params.startPosition ? newsList.size() : params.loadSize + params.startPosition;
        for (int i = params.startPosition ; i < dataLength ; i ++) productionNews.add(newsList.get(i));
        callback.onResult(productionNews);
    }
}
