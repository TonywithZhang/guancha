package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class AutoDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    List<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createAutoNews("https://www.guancha.cn/qiche?s=dhqiche");
        newsList = parseHTML.getAutoNews();
        List<ParseHTML.GuanChaSouceData>  autoNews = new ArrayList<>();
        for (int i = params.requestedStartPosition; i < params.requestedLoadSize ; i ++) autoNews.add(newsList.get(i));
        callback.onResult(autoNews,startPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> autoNews = new ArrayList<>();
        int dataLength = newsList.size() < params.loadSize + params.startPosition ? newsList.size() : params.loadSize + params.startPosition;
        for (int i = params.startPosition ; i < dataLength ; i ++) autoNews.add(newsList.get(i));
        callback.onResult(autoNews);
    }
}
