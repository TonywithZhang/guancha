package com.tec.zhang.guancha.recycler

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.util.concurrent.Executors

class CommentViewModel : ViewModel(){
    private val executor = Executors.newSingleThreadExecutor()
    private val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(false)
            .build()

    private val sourceDataFactory : DataSource.Factory<Int,CommentBean> = CommentFactory()

    val newsList : LiveData<PagedList<CommentBean>> = LivePagedListBuilder(sourceDataFactory,config)
            .setFetchExecutor(executor).build()

    fun invalidateDataSource(){
        val pagedList = newsList.value
        pagedList?.dataSource?.invalidate()
    }
}