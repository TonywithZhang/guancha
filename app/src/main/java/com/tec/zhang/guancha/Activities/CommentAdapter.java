package com.tec.zhang.guancha.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.database.SessionProperty;
import com.tec.zhang.guancha.database.UserProperty;
import com.tec.zhang.guancha.recycler.CommentBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import org.litepal.LitePal;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CommentAdapter extends PagedListAdapter<CommentBean,RecyclerView.ViewHolder> {
    //下面两个都是flag，在评论和头部信息之间进行切换
    private final int HEADER_VIEW = 1;//评论框头部的内容，包括热门评论，和所有评论
    private final int COMMENT_VIEW = 2;//评论本体

    //private List<CommentBean> commentList;//评论的列表
    private Activity context;//评论的上下文环境，就是这个适配器所要应用到的Activity
    private String csrfState, token;//防网络攻击字符串，应用于网络请求中
    private OkHttpClient client = new OkHttpClient();//网络请求的客户端对象

    /*
     * 构造方法，将上下文环境和评论列表以构造器参数形式传入
     * */
    public CommentAdapter(Activity context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    /*
     * 映射具体评论界面的过程，产生两种view ，根据实际情况进行切换
     * */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;//新建一个view
        //从数据里判断要返回view的类型
        if (viewType == HEADER_VIEW) {
            //如果要头部的视图类型，则调用映射器，将头部视图布局蛇叔出来
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_header, parent, false);
            //创建新的头部视图并且返回
            return new HeaderViewHolder(view);
        } else if (viewType == COMMENT_VIEW) {
            //如果要求返回的评论本体，则映射评论视图的布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
            //创建评论视图，并且返回
            return new CommentViewHolder(view);
        }
        //因为不会存在其他的视图，所以走到else分支的肯定是数据出现错误，这里就直接抛出异常
        else throw new IllegalArgumentException("viewType参数错误！");
    }

    /*
     * 在此方法内，将视图和后面的模型进行绑定
     * */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentBean comment = getItem(position);//从列表中拿出当前位置的数据
        //进行显示内容的判断
        if (comment.getViewType() == HEADER_VIEW) {
            //如果要返回头部内容，则强转为头部类型
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            //设置头部的信息
            headerViewHolder.hotHeader.setText(comment.getHeaderTitle());
        } else if (comment.getViewType() == COMMENT_VIEW) {
            //如果是评论的类型，则就强转为评论
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            //设置评论的文本内容
            commentViewHolder.comment.setText(formatComment(comment.getComment()));
            //设置评论的点击事件监听
            commentViewHolder.comment.setOnClickListener(v -> commentOthers(commentViewHolder.comment, comment.getCommentId()));//执行传入的监听器中的评论方法
            //显示评论时间
            commentViewHolder.commentTime.setText(comment.getCommentTime());
            //加载用户的头像
            Picasso.get().load(comment.getUserHeaderImageUrl()).placeholder(R.drawable.ic_guancha).fit().into(commentViewHolder.userHeader);
            //显示用户名
            commentViewHolder.userName.setText(comment.getUserName());
            Log.d(TAG, "onBindViewHolder: 方法被调用，并且此评论的用户名为：" + comment.getUserName());
            //判断，如果用户点击了这个评论的点赞按钮，则将按钮设置为已经点赞的样式
            if (comment.isUserPraised())
                commentViewHolder.praisedIcon.setImageResource(R.drawable.ic_zan_ed);
            //设置点赞按钮的点击事件监听
            commentViewHolder.praisedIcon.setOnClickListener(v -> praise(commentViewHolder.praisedIcon, comment.getCommentId()));//执行传入的监听器中的点赞方法
            //显示此条评论的点赞数量
            commentViewHolder.praisedNum.setText(comment.getPraisedNumber());
            //进行判断，如果当前用户点击了这条评论的反对，则将这一条评论的反对设置为已经反对的样式
            if (comment.isDisliked())
                commentViewHolder.dislikeIcon.setImageResource(R.drawable.ic_cai_red_ed);
            //显示当前评论反对的数量
            commentViewHolder.dislikedNum.setText(comment.getDislikedNumber());
            //设置当前反对按钮的点击事件监听
            commentViewHolder.dislikeIcon.setOnClickListener(v -> trample(commentViewHolder.dislikeIcon, comment.getCommentId()));//执行传入的监听器中的反对方法
            //设置分享按钮的事件监听，当前为空实现
            commentViewHolder.share.setOnClickListener(v -> {
            });//分享的事件监听器
            //设置举报按钮的事件监听，当前为空
            commentViewHolder.jubao.setOnClickListener(v -> {
            });//举报的事件监听器
            //判断，看此评论是否是回复别人评论的评论
            if (!comment.isParentExists()) {
                //如果不是，则直接隐藏上一级评论的界面并且返回，跳出此方法
                commentViewHolder.parentView.setVisibility(View.GONE);
                return;
            }
            //下面都是当上一级评论存在的情况下，要做的事情
            //显示上一级评论的用户名
            commentViewHolder.parentUserName.setText(comment.getParentUserName());
            //显示上一级评论的评论时间
            commentViewHolder.parentCommentTime.setText(comment.getParentCommentTime());
            //显示上一级评论的评论内容
            commentViewHolder.parentComment.setText(formatComment(comment.getParentComment()));
            //设置上一级评论内容的事件监听，主要是弹出输入法，用于恢复该评论
            commentViewHolder.parentComment.setOnClickListener(v -> commentOthers(commentViewHolder.parentComment, comment.getParentId()));
            //判断当前用户是否点击了上一级评论的点赞按钮并根据结果进行处理
            if (comment.isParentUserPraised())
                commentViewHolder.parentPraiseIcon.setImageResource(R.drawable.ic_zan_ed);
            //设置上一级评论的点赞按钮的点击事件监听
            commentViewHolder.parentPraiseIcon.setOnClickListener(v -> praise(commentViewHolder.parentPraiseIcon, comment.getParentId()));
            //显示点赞的数量
            commentViewHolder.parentPraisedNum.setText(comment.getParentPraisedNumber());
            //判断当前用户是否点击了上一级评论的反对按钮，并进行相应的处理
            if (comment.isParentDisliked())
                commentViewHolder.parentDislikedIcon.setImageResource(R.drawable.ic_cai_red_ed);
            //设置上一级评论的反对按钮的点击事件监听
            commentViewHolder.parentDislikedIcon.setOnClickListener(v -> trample(commentViewHolder.parentDislikedIcon, comment.getParentId()));
            //显示上一级评论的反对数量
            commentViewHolder.parentDislikedNum.setText(comment.getParentDislikedNumber());
            //设置上一级评论的分享和举报的点击事件监听，当前都为空
            commentViewHolder.parentShare.setOnClickListener(v -> {
            });//父评论的事件监听器
            commentViewHolder.parentJubao.setOnClickListener(v -> {
            });//父举报的事件监听器
        } else throw new IllegalArgumentException("参数错误!");//如果到了当前的分支，直接排除异常，提示解析错误
    }

    /*
     * 评论本体的模型，用于和布局相结合，显示评论本体
     * */
    class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userHeader;//用户的头像
        TextView userName;// 用户名
        TextView commentTime;//评论时间
        RelativeLayout parentView;//上一级评论的布局容器
        TextView parentUserName;//上一级评论的用户名
        TextView parentCommentTime;// 上一级评论的时间
        TextView parentComment;// 上一级评论内容
        ImageView parentPraiseIcon;//上一级评论的点赞图标
        TextView parentPraisedNum;//上一级评论的点赞数量
        TextView parentDislikedNum;//上一级评论的反对数量
        ImageView parentDislikedIcon;//上级评论的反对图标
        ImageView parentShare;//上一级评论的分享图标
        ImageView parentJubao;//上一级评论的举报图标
        TextView comment;//评论的内容
        ImageView praisedIcon;//点赞的图标
        TextView praisedNum;//点赞的数量
        TextView dislikedNum;//反对的数量
        ImageView dislikeIcon;//反对的图标
        ImageView share;//分享图标
        ImageView jubao;//举报图标

        /**
         * 必须实现的构造函数，利用传进来的布局，将布局和model进行结合
         *
         * @param view 传入的布局，当前传入的布局为评论的布局
         */
        public CommentViewHolder(View view) {
            super(view);
            //下面都是将model和view上面的部分进行一一绑定，具体省略...........
            userHeader = view.findViewById(R.id.user_image);
            userName = view.findViewById(R.id.user_name);
            commentTime = view.findViewById(R.id.comment_time);
            parentView = view.findViewById(R.id.comment_parent_card);
            parentUserName = view.findViewById(R.id.parent_user_name);
            parentCommentTime = view.findViewById(R.id.parent_comment_time);
            parentComment = view.findViewById(R.id.parent_comment);
            parentPraiseIcon = view.findViewById(R.id.parent_praise_icon);
            parentPraisedNum = view.findViewById(R.id.parent_praise_number);
            parentDislikedNum = view.findViewById(R.id.parent_cai_number);
            parentDislikedIcon = view.findViewById(R.id.parent_cai_icon);
            parentShare = view.findViewById(R.id.parent_share_icon);
            parentJubao = view.findViewById(R.id.parent_jubao_icon);
            comment = view.findViewById(R.id.comment);
            praisedIcon = view.findViewById(R.id.praise_icon);
            praisedNum = view.findViewById(R.id.praise_number);
            dislikedNum = view.findViewById(R.id.cai_number);
            dislikeIcon = view.findViewById(R.id.cai_icon);
            share = view.findViewById(R.id.share_icon);
            jubao = view.findViewById(R.id.jubao_icon);
        }
    }

    /*
     * 这个内部类是头部信息的模型，用于和头部信息进行绑定
     * */
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView hotHeader;// 名字是hotheader，其实并不是一直都是热评，还可以显示“全部评论”

        /**
         * 必须实现的构造函数，和头部信息进行绑定，显示头部信息
         *
         * @param view 传进来的头部信息的布局
         */
        public HeaderViewHolder(View view) {
            super(view);
            //头部信息其实很简单了，只需要显示一个文本
            hotHeader = view.findViewById(R.id.comment_header_title);
        }
    }

    /**
     * 点赞的回调函数
     *
     * @param praiseIcon 需要响应的点赞图标
     * @param commentId  评论所在文章的唯一标识码
     */
    private void praise(ImageView praiseIcon, int commentId) {
        //初始化环境，主要是拿到token和csrf字符串
        initSessionProperty();
        //如果每户没登陆，则直接返回
        if (token == null) return;
        praiseIcon.setClickable(false);
        //创建接口字符串
        String praiseUrl = "https://app.guancha.cn/comment/praise?access-token=" + token;
        Log.d(TAG, "praise: " + praiseUrl);
        //创建表单
        FormBody praiseForm = new FormBody.Builder()
                .add("comment_id", String.valueOf(commentId))
                .add("from", "cms")
                .build();
        //创建Request
        Request praiseRequest = new Request.Builder()
                .url(praiseUrl)
                .header("Cookie", csrfState)
                .header("Content-Length", String.valueOf(praiseForm.contentLength()))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .post(praiseForm)
                .build();
        Log.d(TAG, "praise: csrf字符串为：" + csrfState);
        //向观网服务器提交请求，并且监听返回结果
        Call praiseCall = client.newCall(praiseRequest);
        praiseCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //提示用户操作失败
                context.runOnUiThread(() -> Toast.makeText(context, "点赞失败！", Toast.LENGTH_LONG).show());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到返回的字符串
                String praiseRespond = response.body().string();
                Log.d(TAG, "onResponse: 返回的字符串：" + praiseRespond);
                try {
                    //封装成json对象
                    JSONObject praiseJson = new JSONObject(praiseRespond);
                    //判断是否点赞成功
                    if (praiseJson.getString("msg").equals("成功")) {
                        context.runOnUiThread(() -> {
                            //将图标换成已经点赞的图标
                            praiseIcon.setImageResource(R.drawable.ic_zan_ed);
                            //提示用户点赞成功
                            Toast.makeText(context, "点赞成功!", Toast.LENGTH_LONG).show();
                        });

                    } else {
                        //提示用户点赞失败
                        context.runOnUiThread(() -> Toast.makeText(context, "点赞失败！", Toast.LENGTH_LONG).show());
                    }
                    //更新csrf字符串
                    SessionProperty singleProperty = new SessionProperty();
                    String newCsrf = response.header("Set-Cookie", "");
                    if (!newCsrf.equals("")) {
                        csrfState = newCsrf;
                        singleProperty.setCsrfState(newCsrf);
                        singleProperty.updateAll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 反对的回调函数
     *
     * @param trampleIcon 需要响应的反对图标
     * @param commentId   评论所在文章的唯一标识码
     */
    private void trample(ImageView trampleIcon, int commentId) {
        //初始化环境，主要是拿到token和csrf字符串
        initSessionProperty();
        //如果每户没登陆，则直接返回
        if (token == null) return;
        trampleIcon.setClickable(false);
        //创建接口字符串
        String praiseUrl = "https://app.guancha.cn/comment/tread?access-token=" + token;
        //创建表单
        FormBody trampleForm = new FormBody.Builder()
                .add("id", String.valueOf(commentId))
                .add("from", "cms")
                .build();
        //创建Request
        Request praiseRequest = new Request.Builder()
                .url(praiseUrl)
                .header("Cookie", csrfState)
                .header("Content-Length", String.valueOf(trampleForm.contentLength()))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .post(trampleForm)
                .build();
        //向观网服务器提交请求，并且监听返回结果
        Call trampleCall = client.newCall(praiseRequest);
        trampleCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //提示用户操作失败
                context.runOnUiThread(() -> Toast.makeText(context, "反对失败！", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到返回的字符串
                String praiseRespond = response.body().string();
                Log.d(TAG, "onResponse: 返回的字符串：" + praiseRespond);
                try {
                    //封装成json对象
                    JSONObject praiseJson = new JSONObject(praiseRespond);
                    //判断是否点赞成功
                    if (praiseJson.getString("msg").equals("成功")) {
                        context.runOnUiThread(() -> {
                            //将图标换成已经点赞的图标
                            trampleIcon.setImageResource(R.drawable.ic_cai_red_ed);
                            //提示用户点赞成功
                            Toast.makeText(context, "反对成功!", Toast.LENGTH_LONG).show();
                        });

                    } else {
                        //提示用户点赞失败
                        context.runOnUiThread(() -> Toast.makeText(context, "反对失败！", Toast.LENGTH_LONG).show());
                    }
                    //更新csrf字符串
                    SessionProperty singleProperty = new SessionProperty();
                    String newCsrf = response.header("Set-Cookie", "");
                    if (!newCsrf.equals("")) {
                        csrfState = newCsrf;
                        singleProperty.setCsrfState(newCsrf);
                        singleProperty.updateAll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //回复评论的回调函数
    private void commentOthers(TextView praiseIcon, int commentId) {
        if (clickListener != null) {
            clickListener.onCommentClick(commentId);
        }
    }

    //初始化网络请求的参数
    private void initSessionProperty() {
        List<UserProperty> properties = LitePal.findAll(UserProperty.class);
        if (properties.size() == 0) {
            return;
        }
        //拿到本地的csrf数据
        List<SessionProperty> session = LitePal.findAll(SessionProperty.class);
        if (session.size() != 0) csrfState = session.get(0).getCsrfState();
        if (csrfState.contains("HttpOnly")) {
            csrfState = csrfState.split(";")[0];
            Log.d(TAG, "initSessionProperty: 修改后的csrf字符串为：" + csrfState);
        }
        //下面拿到用户的token令牌，目测token与设备相关
        UserProperty userProperty = properties.get(0);
        token = userProperty.getUserToken();
    }

    public interface OnCommentClickListener {
        void onCommentClick(int commentId);
    }

    //评论被点击的监听器
    private OnCommentClickListener clickListener;

    //初始化上面的clickListener
    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.clickListener = listener;
    }

    //创建
    private Drawable htmlDrawable;

    /*
      将所要显示的评论进行格式化
      第一个要格式化的<br  / > ,替换成\n
      第二个是<strong></strong> ,将此标签内的内容替换为粗体字
      第三个要格式化的是官网的表情系统，包含在评论中的<img src = ""/>之类的
      */
    private Spanned formatComment(String comment) {
        /*//首先把所有的<br  / >替换为换行符，这一部最简单
        comment = comment.replace("<br  / >","\n");
        //创建一个空的SpannableStringBuilder，用于生成我们需要的格式化字符串
        SpannableString commentFormatter = new SpannableString(comment);
        //创建一个正则表达式对象，表示对<strong> 标签的匹配
        Pattern strongPattern = Pattern.compile("^(<strong>)(.+)$(</strong>)");
        //得到匹配的对象
        Matcher strongMatcher = strongPattern.matcher(comment);
        //创建一个flag，表征字符串中是否有需要格式化的内容
        boolean hasFormatable = false;
        //在循环中，将所有的strong标签内的文字加粗
        while (strongMatcher.find()){
            //将此时匹配到的strong标签中的文字改成粗体字
            commentFormatter.setSpan(new StyleSpan(Typeface.BOLD),strongMatcher.start() + 8,strongMatcher.end() - 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //再创建表情的正则表达式
        Pattern facialExpression = Pattern.compile("^(<img src).+$(/>)");
        Matcher facialMatcher = facialExpression.matcher(commentFormatter);
        //在循环中，创建表情富文本
        while (facialMatcher.find()){
            //首先将匹配到的
        }
        //上面的步骤仅仅是把所有该加粗的字加粗，还要把其中的标签去掉
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(comment, Html.FROM_HTML_MODE_COMPACT, source -> {
                ImageView htmlView = new ImageView(context);
                Picasso.get().load(source).placeholder(R.drawable.guanwang).into(htmlView);
                htmlDrawable = htmlView.getDrawable();
                if (htmlDrawable == null) Log.d(TAG, "formatComment: 图片为空");
                return htmlDrawable;
            }, null);
        } else return Html.fromHtml(comment);
    }

    //用于确定是否重复
    private static DiffUtil.ItemCallback<CommentBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<CommentBean>() {
        @Override
        public boolean areItemsTheSame(@NonNull CommentBean oldItem, @NonNull CommentBean newItem) {
            return oldItem.getCommentId() == newItem.getCommentId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CommentBean oldItem, @NonNull CommentBean newItem) {
            return oldItem.getCommentId() == newItem.getCommentId();
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
}