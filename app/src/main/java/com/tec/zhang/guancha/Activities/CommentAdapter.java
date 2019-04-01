package com.tec.zhang.guancha.Activities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.recycler.CommentBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER_VIEW = 1;
    private final int COMMENT_VIEW = 2;

    private List<CommentBean> commentList;

    public CommentAdapter(List<CommentBean> commentList){
        this.commentList = commentList;
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
            commentViewHolder.comment.setText(comment.getComment());
            commentViewHolder.commentTime.setText(comment.getCommentTime());
            Picasso.get().load(comment.getUserHeaderImageUrl()).placeholder(R.drawable.ic_guancha).fit().into(commentViewHolder.userHeader);
            commentViewHolder.userName.setText(comment.getUserName());
            Log.d(TAG, "onBindViewHolder: " + comment.getUserName());
            if (comment.isUserPraised()) commentViewHolder.praisedIcon.setImageResource(R.drawable.ic_zan_ed);
            commentViewHolder.praisedNum.setText(comment.getPraisedNumber());
            if (comment.isDisliked()) commentViewHolder.dislikeIcon.setImageResource(R.drawable.ic_cai_red_ed);
            commentViewHolder.dislikedNum.setText(comment.getDislikedNumber());
            commentViewHolder.share.setOnClickListener(v -> {});//分享的事件监听器
            commentViewHolder.jubao.setOnClickListener(v -> {});//举报的事件监听器
            if (!comment.isParentExists()) {
                commentViewHolder.parentView.setVisibility(View.GONE);
                return;
            }
            commentViewHolder.parentUserName.setText(comment.getParentUserName());
            commentViewHolder.parentCommentTime.setText(comment.getParentCommentTime());
            commentViewHolder.parentComment.setText(comment.getParentComment());
            if (comment.isParentUserPraised())commentViewHolder.parentPraiseIcon.setImageResource(R.drawable.ic_zan_ed);
            commentViewHolder.parentPraisedNum.setText(comment.getParentPraisedNumber());
            if (comment.isParentDisliked()) commentViewHolder.parentDislikedIcon.setImageResource(R.drawable.ic_cai_red_ed);
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
}
