package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class AutoFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<AutoDataSource> data = new MutableLiveData<>();

    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        AutoDataSource dataSource = new AutoDataSource();
        data.postValue(dataSource);
        return dataSource;
    }
}
