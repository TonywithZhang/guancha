package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class MilitaryFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<MilitaryDataSource>  liveData = new MutableLiveData<>();
    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        MilitaryDataSource source = new MilitaryDataSource();
        liveData.postValue(source);
        return source;
    }
}
