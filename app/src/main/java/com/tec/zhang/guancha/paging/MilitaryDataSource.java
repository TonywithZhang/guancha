package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class MilitaryDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> newsList;

    public MilitaryDataSource() {
        super();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createMilitaryNews("https://www.guancha.cn/military-affairs?s=dhjunshi");
        newsList = parseHTML.getMilitaryNews();
        List<ParseHTML.GuanChaSouceData> militaryNews = new ArrayList<>();
        for (int i = startPos;i < params.requestedLoadSize; i ++) militaryNews.add(newsList.get(i));
        callback.onResult(militaryNews,startPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> news = new ArrayList<>();
        int dataLength = newsList.size() < params.startPosition + params.loadSize ? newsList.size() : params.startPosition + params.loadSize;
        for (int i = params.startPosition; i < dataLength; i ++) news.add(newsList.get(i));
        callback.onResult(news);
    }
}
