package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class VideoDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    private List<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPos = 0;
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.createVideoNews("https://www.guancha.cn/GuanWangKanPian?s=dhshipin");
        newsList = parseHTML.getVideoNews();
        List<ParseHTML.GuanChaSouceData> videoNews = new ArrayList<>();
        for (int i = params.requestedStartPosition; i < params.requestedLoadSize ; i ++) videoNews.add(newsList.get(i));

        callback.onResult(videoNews,startPos);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        List<ParseHTML.GuanChaSouceData> videoNews = new ArrayList<>();
        int dataLength = newsList.size() < params.loadSize + params.startPosition ? newsList.size() : params.loadSize + params.startPosition;
        for (int i = params.startPosition; i < dataLength ; i ++) videoNews.add(newsList.get(i));
        callback.onResult(videoNews);
    }
}