package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class FinancialDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    FinancialDataSource(){
        super();
    }
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createFinancialNews("https://www.guancha.cn/economy?s=dhcaijing");
        newsList = parseHTML.getFinancialNews();
        ArrayList<ParseHTML.GuanChaSouceData> financialNews = new ArrayList<>();
        for (int i = 0; i < params.requestedLoadSize ; i ++) financialNews.add(newsList.get(i));
        callback.onResult(financialNews,startPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> financialNews = new ArrayList<>();
        int dataLength = newsList.size() < params.loadSize + params.startPosition ? newsList.size() : params.startPosition + params.loadSize;
        for (int i = params.startPosition ; i < dataLength ; i ++) financialNews.add(newsList.get(i));
        callback.onResult(financialNews);
    }
}
