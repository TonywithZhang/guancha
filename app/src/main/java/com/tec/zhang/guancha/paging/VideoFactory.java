package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class VideoFactory extends DataSource.Factory<Integer,ParseHTML.GuanChaSouceData> {
    private MutableLiveData<VideoDataSource> liveData = new MutableLiveData<>();

    @Override
    public DataSource<Integer, ParseHTML.GuanChaSouceData> create() {
        VideoDataSource dataSource = new VideoDataSource();
        liveData.postValue(dataSource);
        return dataSource;
    }
}
