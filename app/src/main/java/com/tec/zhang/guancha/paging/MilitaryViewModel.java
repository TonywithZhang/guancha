package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class MilitaryViewModel extends ViewModel {
    private Executor executor = Executors.newSingleThreadExecutor();

    private PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(20)
                                        .setEnablePlaceholders(false)
                                        .setPageSize(20)
                                        .setPrefetchDistance(4)
                                        .build();

    private DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> factory = new MilitaryFactory();

    private LiveData<PagedList<ParseHTML.GuanChaSouceData>> liveData = new LivePagedListBuilder<>(factory,config).setFetchExecutor(executor).build();

    public LiveData<PagedList<ParseHTML.GuanChaSouceData>> getLiveData() {
        return liveData;
    }
}
