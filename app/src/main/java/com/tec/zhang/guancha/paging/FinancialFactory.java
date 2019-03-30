package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class FinancialFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<FinancialDataSource> liveData = new MutableLiveData<>();

    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        FinancialDataSource dataSource = new FinancialDataSource();
        liveData.postValue(dataSource);
        return dataSource;
    }
}
