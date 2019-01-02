package com.tec.zhang.guancha.paging;

import android.util.Log;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FengwenDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {

    private ArrayList<ParseHTML.GuanChaSouceData> dataSource = new ArrayList<>();

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {
        final int startPosition = 0;
        fetchNews();
        callback.onResult(dataSource,0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {
        Log.d(TAG, "loadRange: 风闻加载更多新闻被调用");
        callback.onResult(additionDataList(params.startPosition,params.loadSize));
    }
    private int index = 2;
    private ArrayList<ParseHTML.GuanChaSouceData> additionDataList(int position,int loadSize){
        String requestUrl = "https://user.guancha.cn/main/index?page=" + index + "&order=2";
        //Log.d(TAG, "additionDataList: " + requestUrl);
        ArrayList<ParseHTML.GuanChaSouceData> newsList = new ArrayList<>(ParseHTML.nextFengwenPage(requestUrl));
        index ++;
        return newsList;
    }

    private void fetchNews(){
        ParseHTML parseHTML = new ParseHTML();
        parseHTML.parseFengwen("http://user.guancha.cn/?s=dhfengwen");
        dataSource.addAll(parseHTML.getFengwenList());
    }

}
