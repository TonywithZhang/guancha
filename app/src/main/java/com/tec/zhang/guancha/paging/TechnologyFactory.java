package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class TechnologyFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<TechnologyDataSource> liveData = new MutableLiveData<>();
    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        TechnologyDataSource dataSource = new TechnologyDataSource();
        liveData.postValue(dataSource);
        return dataSource;
    }
}
