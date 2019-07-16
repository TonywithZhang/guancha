package com.tec.zhang.guancha.recycler

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

class CommentFactory : DataSource.Factory<Int,CommentBean>(){

    private val sourceMutableLiveData = MutableLiveData<CommentDataSource>()

    override fun create(): DataSource<Int, CommentBean> {
        val dataSource = CommentDataSource()
        sourceMutableLiveData.postValue(dataSource)
        return dataSource
    }

}