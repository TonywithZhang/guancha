package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.pages.TecnologyPage;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class TechnologyDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    TechnologyDataSource(){
        super();
    }
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int starPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createTecnologyNews("https://www.guancha.cn/gongye·keji?s=dhgongye·keji");
        newsList = parseHTML.getTecnologyNews();
        List<ParseHTML.GuanChaSouceData> technologyNews = new ArrayList<>();
        for (int i = 0 ; i < params.requestedLoadSize ; i ++) technologyNews.add(newsList.get(i));
        callback.onResult(technologyNews,starPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> technologyNews = new ArrayList<>();
        int dataLength = newsList.size() < params.startPosition + params.loadSize ? newsList.size() : params.loadSize + params.startPosition;
        for (int i = params.startPosition ; i < dataLength ; i ++) technologyNews.add(newsList.get(i));

        callback.onResult(technologyNews);
    }
}
