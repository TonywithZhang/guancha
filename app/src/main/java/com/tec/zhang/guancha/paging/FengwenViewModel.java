package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class FengwenViewModel extends ViewModel {
    private Executor executor = Executors.newSingleThreadExecutor();

    private PagedList.Config newsConfig = new PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(false)
            .build();
    private DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> sourceDataFactory = new FengwenNewsFactory();
    private LiveData<PagedList<ParseHTML.GuanChaSouceData>> newsList = new LivePagedListBuilder<>(sourceDataFactory,newsConfig)
            .setFetchExecutor(executor)
            .build();
    public LiveData<PagedList<ParseHTML.GuanChaSouceData>> getNewsList() {
        return newsList;
    }
    public void invalidateDataSource(){
        PagedList<ParseHTML.GuanChaSouceData> pagedList = newsList.getValue();
        if (pagedList != null) pagedList.getDataSource().invalidate();
    }
}
