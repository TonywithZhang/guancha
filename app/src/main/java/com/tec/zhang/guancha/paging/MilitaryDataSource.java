package com.tec.zhang.guancha.paging;

import com.tec.zhang.guancha.recycler.ParseHTML;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class MilitaryDataSource extends PositionalDataSource<ParseHTML.GuanChaSouceData> {
    public MilitaryDataSource() {
        super();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ParseHTML.GuanChaSouceData> callback) {

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ParseHTML.GuanChaSouceData> callback) {

    }
}
