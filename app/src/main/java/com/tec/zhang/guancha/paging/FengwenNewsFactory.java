package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class FengwenNewsFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {

    private MutableLiveData<FengwenDataSource> sourceMutableLiveData = new MutableLiveData<>();
    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        FengwenDataSource dataSource = new FengwenDataSource();
        sourceMutableLiveData.postValue(dataSource);
        return dataSource;
    }
}
