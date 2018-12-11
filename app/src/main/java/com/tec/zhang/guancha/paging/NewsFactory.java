package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class NewsFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    /*private ArrayList<ParseHTML.GuanChaSouceData> news;

    public NewsFactory(ArrayList<ParseHTML.GuanChaSouceData> source){
        this.news = source;
        if (news == null) news = new ArrayList<>();
    }*/
    private MutableLiveData<MainPageNewsDataSource> sourceMutableLiveData = new MutableLiveData<>();
    @Override
    public DataSource<Integer,ParseHTML.GuanChaSouceData> create() {
        MainPageNewsDataSource source = new MainPageNewsDataSource();
        sourceMutableLiveData.postValue(source);
        return source;
    }
}
