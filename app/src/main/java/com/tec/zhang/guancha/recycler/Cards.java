package com.tec.zhang.guancha.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class Cards extends RecyclerView.Adapter<Cards.MyViewHolder> {
    public List<ParseHTML.GuanChaSouceData> news;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParseHTML.GuanChaSouceData single = news.get(position);
        if (!single.getImageUrl().equals("")) {
            Picasso.get().load(single.getImageUrl()).placeholder(R.drawable.ic_guancha).fit().into(holder.newsPic);
        } else {
            holder.newsPic.setImageResource(R.drawable.ic_guancha);
        }
        holder.newsTitle.setText(single.getTitle());
        holder.reads.setText(String.valueOf(single.getCommentNum()));
        holder.beLong.setText(single.getBelongTo());
    }
    public Cards(List<ParseHTML.GuanChaSouceData> newsData){
        news = new ArrayList<>();
        news = newsData;
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
        ImageView newsPic;
        TextView newsTitle;
        TextView reads;
        TextView beLong;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            newsPic = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            reads = itemView.findViewById(R.id.comment_num);
            beLong = itemView.findViewById(R.id.belong_to);
        }
    }
}
