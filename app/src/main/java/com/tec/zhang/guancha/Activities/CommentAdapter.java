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

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER_VIEW = 1;
    private final int COMMENT_VIEW = 2;

    private List<CommentBean> commentList;
    private Activity context;
    private String csrfState,token;
    private OkHttpClient client = new OkHttpClient();

    public CommentAdapter(List<CommentBean> commentList,Activity context){
        this.commentList = commentList;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_header,parent,false);
            return new HeaderViewHolder(view);
        }
        else if (viewType == COMMENT_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card,parent,false);
            return new CommentViewHolder(view);
        }
        else throw new IllegalArgumentException("viewType参数错误！");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentBean comment = commentList.get(position);

        if (comment.getViewType() == HEADER_VIEW){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.hotHeader.setText(comment.getHeaderTitle());
        }else if (comment.getViewType() == COMMENT_VIEW){
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.comment.setText(formatComment(comment.getComment()));
            commentViewHolder.comment.setOnClickListener(v -> commentOthers(commentViewHolder.comment,comment.getCommentId()));//执行传入的监听器中的评论方法
            commentViewHolder.commentTime.setText(comment.getCommentTime());
            Picasso.get().load(comment.getUserHeaderImageUrl()).placeholder(R.drawable.ic_guancha).fit().into(commentViewHolder.userHeader);
            commentViewHolder.userName.setText(comment.getUserName());
            Log.d(TAG, "onBindViewHolder: " + comment.getUserName());
            if (comment.isUserPraised()) commentViewHolder.praisedIcon.setImageResource(R.drawable.ic_zan_ed);
            commentViewHolder.praisedIcon.setOnClickListener(v -> praise(commentViewHolder.praisedIcon,comment.getCommentId()));//执行传入的监听器中的点赞方法
            commentViewHolder.praisedNum.setText(comment.getPraisedNumber());
            if (comment.isDisliked()) commentViewHolder.dislikeIcon.setImageResource(R.drawable.ic_cai_red_ed);
            commentViewHolder.dislikedNum.setText(comment.getDislikedNumber());
            commentViewHolder.dislikeIcon.setOnClickListener(v -> trample(commentViewHolder.dislikeIcon,comment.getCommentId()));//执行传入的监听器中的反对方法
            commentViewHolder.share.setOnClickListener(v -> {});//分享的事件监听器
            commentViewHolder.jubao.setOnClickListener(v -> {});//举报的事件监听器
            if (!comment.isParentExists()) {
                commentViewHolder.parentView.setVisibility(View.GONE);
                return;
            }
            commentViewHolder.parentUserName.setText(comment.getParentUserName());
            commentViewHolder.parentCommentTime.setText(comment.getParentCommentTime());
            commentViewHolder.parentComment.setText(formatComment(comment.getParentComment()));
            commentViewHolder.parentComment.setOnClickListener(v -> commentOthers(commentViewHolder.parentComment,comment.getParentId()));
            if (comment.isParentUserPraised())commentViewHolder.parentPraiseIcon.setImageResource(R.drawable.ic_zan_ed);
            commentViewHolder.parentPraiseIcon.setOnClickListener(v -> praise(commentViewHolder.parentPraiseIcon,comment.getParentId()));
            commentViewHolder.parentPraisedNum.setText(comment.getParentPraisedNumber());
            if (comment.isParentDisliked()) commentViewHolder.parentDislikedIcon.setImageResource(R.drawable.ic_cai_red_ed);
            commentViewHolder.parentDislikedIcon.setOnClickListener(v -> trample(commentViewHolder.parentDislikedIcon,comment.getParentId()));
            commentViewHolder.parentDislikedNum.setText(comment.getParentDislikedNumber());
            commentViewHolder.parentShare.setOnClickListener(v -> {});//父评论的事件监听器
            commentViewHolder.parentJubao.setOnClickListener(v -> {});//父举报的事件监听器
        }else throw new IllegalArgumentException("参数错误!");
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position).getViewType();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userHeader;
        TextView userName;
        TextView commentTime;
        RelativeLayout parentView;
        TextView parentUserName;
        TextView parentCommentTime;
        TextView parentComment;
        ImageView parentPraiseIcon;
        TextView parentPraisedNum;
        TextView parentDislikedNum;
        ImageView parentDislikedIcon;
        ImageView parentShare;
        ImageView parentJubao;
        TextView comment;
        ImageView praisedIcon;
        TextView praisedNum;
        TextView dislikedNum;
        ImageView dislikeIcon;
        ImageView share;
        ImageView jubao;
        public CommentViewHolder(View view){
            super(view);
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

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView hotHeader;
        public HeaderViewHolder(View view){
            super(view);
            hotHeader = view.findViewById(R.id.comment_header_title);
        }
    }
    //点赞的回调函数
    private void praise(ImageView praiseIcon,int commentId){
        //初始化环境，主要是拿到token和csrf字符串
        initSessionProperty();
        //如果每户没登陆，则直接返回
        if (token == null) return;
        praiseIcon.setClickable(false);
        //创建接口字符串
        String praiseUrl =  "https://app.guancha.cn/comment/praise?access-token=" + token;
        Log.d(TAG, "praise: " + praiseUrl);
        //创建表单
        FormBody praiseForm = new FormBody.Builder()
                .add("comment_id",String.valueOf(commentId))
                .add("from","cms")
                .build();
        //创建Request
        Request praiseRequest = new Request.Builder()
                .url(praiseUrl)
                .header("Cookie",csrfState)
                .header("Content-Length",String.valueOf(praiseForm.contentLength()))
                .header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
                .post(praiseForm)
                .build();
        Log.d(TAG, "praise: csrf字符串为：" + csrfState);
        //向观网服务器提交请求，并且监听返回结果
        Call praiseCall = client.newCall(praiseRequest);
        praiseCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //提示用户操作失败
                context.runOnUiThread(() -> Toast.makeText(context,"点赞失败！",Toast.LENGTH_LONG).show());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到返回的字符串
                String praiseRespond = response.body().string();
                Log.d(TAG, "onResponse: 返回的字符串：" + praiseRespond);
                try{
                    //封装成json对象
                    JSONObject praiseJson = new JSONObject(praiseRespond);
                    //判断是否点赞成功
                    if (praiseJson.getString("msg").equals("成功")){
                        context.runOnUiThread(() -> {
                            //将图标换成已经点赞的图标
                            praiseIcon.setImageResource(R.drawable.ic_zan_ed);
                            //提示用户点赞成功
                            Toast.makeText(context,"点赞成功!",Toast.LENGTH_LONG).show();
                        });

                    }else {
                        //提示用户点赞失败
                        context.runOnUiThread(() -> Toast.makeText(context,"点赞失败！",Toast.LENGTH_LONG).show());
                    }
                    //更新csrf字符串
                    SessionProperty singleProperty = new SessionProperty();
                    String newCsrf = response.header("Set-Cookie","");
                    if (!newCsrf.equals("")){
                        csrfState = newCsrf;
                        singleProperty.setCsrfState(newCsrf);
                        singleProperty.updateAll();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    //反对的回调函数
    private void trample(ImageView trampleIcon,int commentId){
        //初始化环境，主要是拿到token和csrf字符串
        initSessionProperty();
        //如果每户没登陆，则直接返回
        if (token == null) return;
        trampleIcon.setClickable(false);
        //创建接口字符串
        String praiseUrl =  "https://app.guancha.cn/comment/tread?access-token=" + token;
        //创建表单
        FormBody trampleForm = new FormBody.Builder()
                .add("id",String.valueOf(commentId))
                .add("from","cms")
                .build();
        //创建Request
        Request praiseRequest = new Request.Builder()
                .url(praiseUrl)
                .header("Cookie",csrfState)
                .header("Content-Length",String.valueOf(trampleForm.contentLength()))
                .header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
                .post(trampleForm)
                .build();
        //向观网服务器提交请求，并且监听返回结果
        Call trampleCall = client.newCall(praiseRequest);
        trampleCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //提示用户操作失败
                context.runOnUiThread(() -> Toast.makeText(context,"反对失败！",Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到返回的字符串
                String praiseRespond = response.body().string();
                Log.d(TAG, "onResponse: 返回的字符串：" + praiseRespond);
                try{
                    //封装成json对象
                    JSONObject praiseJson = new JSONObject(praiseRespond);
                    //判断是否点赞成功
                    if (praiseJson.getString("msg").equals("成功")){
                        context.runOnUiThread(() -> {
                            //将图标换成已经点赞的图标
                            trampleIcon.setImageResource(R.drawable.ic_cai_red_ed);
                            //提示用户点赞成功
                            Toast.makeText(context,"反对成功!",Toast.LENGTH_LONG).show();
                        });

                    }else {
                        //提示用户点赞失败
                        context.runOnUiThread(() -> Toast.makeText(context,"反对失败！",Toast.LENGTH_LONG).show());
                    }
                    //更新csrf字符串
                    SessionProperty singleProperty = new SessionProperty();
                    String newCsrf = response.header("Set-Cookie","");
                    if (!newCsrf.equals("")){
                        csrfState = newCsrf;
                        singleProperty.setCsrfState(newCsrf);
                        singleProperty.updateAll();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    //回复评论的回调函数
    private void commentOthers(TextView praiseIcon,int commentId){
        if (clickListener != null){
            clickListener.onCommentClick(commentId);
        }
    }
    //初始化网络请求的参数
    private void initSessionProperty(){
        List<UserProperty> properties = LitePal.findAll(UserProperty.class);
        if (properties.size() == 0) {
            return;
        }
        //拿到本地的csrf数据
        List<SessionProperty> session = LitePal.findAll(SessionProperty.class);
        if (session.size() != 0) csrfState = session.get(0).getCsrfState();
        if (csrfState.contains("HttpOnly")){
            csrfState = csrfState.split(";")[0];
            Log.d(TAG, "initSessionProperty: 修改后的csrf字符串为：" + csrfState);
        }
        //下面拿到用户的token令牌，目测token与设备相关
        UserProperty userProperty = properties.get(0);
        token = userProperty.getUserToken();
    }

    public interface OnCommentClickListener{
        void onCommentClick(int commentId);
    }
    //评论被点击的监听器
    private OnCommentClickListener clickListener;

    //初始化上面的clickListener
    public void setOnCommentClickListener(OnCommentClickListener listener){
        this.clickListener = listener;
    }


    private Drawable htmlDrawable;
    /*
      将所要显示的评论进行格式化
      第一个要格式化的<br  / > ,替换成\n
      第二个是<strong></strong> ,将此标签内的内容替换为粗体字
      第三个要格式化的是官网的表情系统，包含在评论中的<img src = ""/>之类的
      */
    private Spanned formatComment(String comment){
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
            return Html.fromHtml(comment,Html.FROM_HTML_MODE_COMPACT,source -> {
                ImageView htmlView = new ImageView(context);
                Picasso.get().load(source).placeholder(R.drawable.guanwang).into(htmlView);
                htmlDrawable = htmlView.getDrawable();
                if (htmlDrawable == null) Log.d(TAG, "formatComment: 图片为空");
                return htmlDrawable;
            },null);
        }else return Html.fromHtml(comment);
    }
}
