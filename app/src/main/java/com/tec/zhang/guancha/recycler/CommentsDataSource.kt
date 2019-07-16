package com.tec.zhang.guancha.recycler

import android.os.Build
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.tec.zhang.guancha.database.ArticleCodeId
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.litepal.LitePal
import java.io.IOException
import java.net.SocketTimeoutException

class CommentsDataSource : PageKeyedDataSource<Int,CommentBean>(){

    //当前文章的codeID
    var codeID = 0
    //当前文章的链接
    lateinit var articleUrl : String
    //总共评论的页数
    var commentPages : Int = 0
    //当前所要加载的评论的页面
    var pageIndex = 1
    private val client = OkHttpClient()
    private var hotCommentsNum = 0
    private var allCommentsNum = 0
    private var totalCommentsNum = 0

    private val HEADER_VIEW = 1
    private val COMMENT_VIEW = 2

    private val INITIAL_LOAD = 3
    private val RANGE_LOAD = 4

    val TAG = "dataSource里面"

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CommentBean>) {
        //从数据库中取出codeID
        val commentList = ArrayList<CommentBean>(40)
        val codeId = LitePal.findLast(ArticleCodeId :: class.java)
        codeId?: return
        codeID = codeId.codeId
        articleUrl = codeId.articleUrl
        //构造请求链接
        val requestLink = "https://user.guancha.cn/comment/cmt-list.json?codeId=$codeID&codeType=1&pageNo=$pageIndex&order=1&ff=www"
        pageIndex++//页数自增，为下次动态增加做准备
        requestComment(requestLink,INITIAL_LOAD,commentList,callback)
        Log.d(TAG,"此时LoadInitial方法已经返回")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CommentBean>) {
        Log.d(TAG,"loadAfter方法被调用")
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M && pageIndex >=3) return
        val commentContainer = ArrayList<CommentBean>(20)
        if (pageIndex> commentPages) return
        val nextPageUrl = "https://user.guancha.cn/comment/cmt-list.json?codeId=$codeID&codeType=1&pageNo=${params.key}&order=1&ff=www"
        pageIndex ++
        requestComment(nextPageUrl,RANGE_LOAD,commentContainer,callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CommentBean>) {

    }

    private fun requestComment(requestLink: String,loadType : Int,container : ArrayList<CommentBean>,callback: Any) {
        Log.d(TAG,"请求评论的方法被调用")
        //构造网络请求
        val request = Request.Builder().url(requestLink)
                .addHeader("Referer", articleUrl)
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("Origin", "https://www.guancha.cn")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .get()
                .build()
        //监听，并处理返回
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw SocketTimeoutException("连接超时！")
            }

            override fun onResponse(call: Call, response: Response) {
                val respond = response.body?.string()
                respond ?: return//返回的数据有问题就直接返回
                //json转换过程也许会出现异常情况
                try {
                    val responseInJson = JSONObject(respond)//封装成json对象
                    responseInJson.apply {
                        if (loadType == INITIAL_LOAD) {//提取全部的热评
                            val hotCount = getString("all_hot_count")
                            hotCommentsNum = hotCount.toInt()
                            if (hotCommentsNum != 0) {
                                parseComments(getJSONArray("hots"),
                                        "热门评论 $hotCount 条",
                                        loadType,
                                        container)
                            }
                        }
                        //提取全部评论，每次网络请求返回的评论的数目都是20条，当不满足20条的时候，肯定是后面没有任何评论了
                        allCommentsNum = getString("count").toInt()
                        totalCommentsNum = when(hotCommentsNum){
                            0 -> when(allCommentsNum){
                                0 -> 0
                                else -> allCommentsNum +1
                            }
                            else -> hotCommentsNum + allCommentsNum + 2
                        }
                        if (allCommentsNum != 0) {
                            commentPages = allCommentsNum / 20 + 1
                            parseComments(getJSONArray("items"),
                                    "所有评论 $allCommentsNum 条",
                                    loadType,
                                    container)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (loadType == INITIAL_LOAD){
                    (callback as PageKeyedDataSource.LoadInitialCallback<Int,CommentBean>).onResult(container,null,2)
                }else if(loadType == RANGE_LOAD){
                    (callback as PageKeyedDataSource.LoadCallback<Int,CommentBean>).onResult(container,pageIndex)
                }
            }
        })
    }

    /**
    * 提取评论
    * @param hotComments 所有要提取的评论所在的json数组对象
    * */
    private fun parseComments(hotComments : JSONArray, headerName : String, loadType: Int, container : ArrayList<CommentBean>){
        Log.d(TAG,"转换评论的方法被调用")
        if (loadType == INITIAL_LOAD) {
            val headerText = CommentBean(HEADER_VIEW,
                    0,
                    "",
                    "",
                    "",
                    "",
                    false,
                    "",
                    false,
                    "",false)
            headerText.headerTitle = headerName
            container.add(headerText)
        }
        for (i in 0 until hotComments.length()){
            val hotBean = hotComments.getJSONObject(i)
            val singleBean = hotBean.run {
                CommentBean(COMMENT_VIEW,
                        getInt("id"),
                        getString("user_photo"),
                        getString("user_nick"),
                        getString("created_at"),
                        getString("content"),
                        getBoolean("has_praise"),
                        getInt("praise_num").toString(),
                        getBoolean("has_tread"),
                        getInt("tread_num").toString(),
                        getInt("parent_id") != 0)
            }
            if (singleBean.isParentExists){
                val parentComments = hotBean.getJSONArray("parent").getJSONObject(0)
                with(parentComments){
                    singleBean.run {
                        parentId = hotBean.getInt("parent_id")
                        parentUserName = getString("user_nick")
                        parentCommentTime = getString("created_at")
                        parentComment = getString("content")
                        isParentDisliked = getBoolean("has_tread")
                        parentDislikedNumber = getString("tread_num")
                        isParentUserPraised = getBoolean("has_praise")
                        parentPraisedNumber = getInt("praise_num").toString()
                    }
                }
            }
            container.add(singleBean)
        }
    }
}