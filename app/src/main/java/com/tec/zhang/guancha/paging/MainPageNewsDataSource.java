package com.tec.zhang.guancha.paging;

import android.util.Log;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainPageNewsDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {

    private ArrayList<ParseHTML.GuanChaSouceData> dataSource = new ArrayList<>();
    private ParseHTML parseHTML;
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPosition = 0;
        fetchNews();
        ArrayList<ParseHTML.GuanChaSouceData> news = additionDataList(startPosition,params.requestedLoadSize);
        callback.onResult(news,0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        ArrayList<ParseHTML.GuanChaSouceData> news = additionDataList(params.startPosition,params.loadSize);
        callback.onResult(news);
    }

    private ArrayList<ParseHTML.GuanChaSouceData> additionDataList(int position,int loadSize){
        ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>();
        ParseHTML.GuanChaSouceData data;
        Log.d(TAG, "additionDataList: 首页中有" + dataSource.size() + "条新闻");
        int dataLength = position + loadSize > dataSource.size() ? dataSource.size() : position + loadSize;
        for (int i = position ; i < position + loadSize ; i ++){
            data = dataSource.get(i);
            newsList.add(data);
        }
        return newsList;
    }
    private void fetchNews(){
        parseHTML = new ParseHTML();
        parseHTML.init();
        parseHTML.getHeadLine();
        parseHTML.createNormalNews();
        dataSource.addAll(parseHTML.getImportantNews());
        dataSource.addAll(parseHTML.getNormalNews());
    }
}
