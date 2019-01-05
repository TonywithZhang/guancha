package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class InternationalDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> dataSource = new ArrayList<>();

    InternationalDataSource() {
        super();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPosition = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createInternationalNews("https://www.guancha.cn/internation?s=dhguoji");
        dataSource = parseHTML.getInternationalNews();
        List<ParseHTML.GuanChaSouceData> internationalNews = new ArrayList<>();
        for (int i = 0 ; i < params.requestedLoadSize ; i ++){
            internationalNews.add(dataSource.get(i));
        }
        callback.onResult(internationalNews,startPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> news = new ArrayList<>();
        int dataLength = dataSource.size() < params.startPosition + params.loadSize ? dataSource.size() : params.startPosition + params.loadSize;
        for (int i = params.startPosition ; i < dataLength ; i ++){
            news.add(dataSource.get(i));
        }
        callback.onResult(news);
    }
}
