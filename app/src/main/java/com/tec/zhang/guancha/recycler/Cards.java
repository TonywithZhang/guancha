package com.tec.zhang.guancha.recycler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.animation.content.Content;
import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.NewsDetail;
import com.tec.zhang.guancha.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class Cards extends RecyclerView.Adapter<Cards.MyViewHolder> {
    public List<ParseHTML.GuanChaSouceData> news;
    private Context content;
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ParseHTML.GuanChaSouceData single = news.get(position);
        if (!single.getImageUrl().equals("")) {
            Picasso.get().load(single.getImageUrl()).placeholder(R.drawable.ic_guancha).fit().into(holder.newsPic);
        } else {
            holder.newsPic.setImageResource(R.drawable.ic_guancha);
        }
        holder.newsTitle.setText(single.getTitle());
        holder.reads.setText(String.valueOf(single.getCommentNum()));
        holder.beLong.setText(single.getBelongTo());
        holder.card.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(content,NewsDetail.class);
                intent.putExtra("articleUrl",single.getArticleUrl());
                intent.putExtra("news",single);
                content.startActivity(intent);
            }
        });
    }
    public Cards(List<ParseHTML.GuanChaSouceData> newsData, Activity activity){
        news = new ArrayList<>();
        news = newsData;
        content = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View card;
        ImageView newsPic;
        TextView newsTitle;
        TextView reads;
        TextView beLong;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            newsPic = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            reads = itemView.findViewById(R.id.comment_num);
            beLong = itemView.findViewById(R.id.belong_to);
        }
    }
}
