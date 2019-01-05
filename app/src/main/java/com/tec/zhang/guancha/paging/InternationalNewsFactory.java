package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class InternationalNewsFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<InternationalDataSource> liveData = new MutableLiveData<>();
    @Override
    public DataSource<Integer,ParseHTML.GuanChaSouceData> create() {
        InternationalDataSource dataSource = new InternationalDataSource();
        liveData.postValue(dataSource);
        return dataSource;
    }
}
