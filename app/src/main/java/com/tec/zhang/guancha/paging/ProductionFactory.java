package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class ProductionFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<ProductionDataSource> liveData = new MutableLiveData<>();

    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        ProductionDataSource dataSource = new ProductionDataSource();
        liveData.postValue(dataSource);
        return dataSource;
    }
}
