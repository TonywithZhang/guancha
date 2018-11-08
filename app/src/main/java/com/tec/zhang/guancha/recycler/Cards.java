package com.tec.zhang.guancha.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Cards extends RecyclerView.Adapter<Cards.MyViewHolder> {
    private List<NewsSingle> news;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewsSingle single = news.get(position);
        Picasso.get().load(single.getImageURL()).into(holder.newsPic);
        holder.newsTitle.setText(single.getNewsTitle());
        holder.reads.setText(single.getCommentNum());
        holder.beLong.setText(single.getBeLong());
    }

    public Cards(List<NewsSingle> news){
        news = news;
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
        return 0;
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
