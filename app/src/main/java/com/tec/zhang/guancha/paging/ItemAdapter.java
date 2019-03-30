package com.tec.zhang.guancha.paging;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.zhang.guancha.DetailWithPic;
import com.tec.zhang.guancha.NewsDetail;
import com.tec.zhang.guancha.R;
import com.tec.zhang.guancha.recycler.ParseHTML;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends PagedListAdapter<ParseHTML.GuanChaSouceData,ItemAdapter.ItemViewHolder> {
    private Activity activity;
    //private List<ParseHTML.GuanChaSouceData> newsList;

    public ItemAdapter(Activity act) {
        super(DIFF_CALLBACK);
        this.activity = act;
        //this.newsList = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card,parent,false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final ParseHTML.GuanChaSouceData single = getItem(position);
        if (!single.getImageUrl().equals("")) {
            Picasso.get().load(single.getImageUrl()).placeholder(R.drawable.ic_guancha).into(holder.newsPic);
        } else {
            holder.newsPic.setImageResource(R.drawable.ic_guancha);
        }
        holder.newsTitle.setText(single.getTitle());
        holder.reads.setText(String.valueOf(single.getCommentNum()));
        holder.beLong.setText(single.getBelongTo());
        holder.card.setOnClickListener(v -> {
            if (single.getImageUrl().equals("")){
                Intent intent = new Intent(activity,NewsDetail.class);
                intent.putExtra("articleUrl",single.getArticleUrl());
                intent.putExtra("news",single);
                activity.startActivity(intent);
            }else {
                Intent intent = new Intent(activity,DetailWithPic.class);
                intent.putExtra("articleUrl",single.getArticleUrl());
                intent.setExtrasClassLoader(ParseHTML.class.getClassLoader());
                intent.putExtra("news",single);
                drawableToBitmap(holder.newsPic.getDrawable());
                intent.putExtra("pic",bitmap2Bytes(bitmap));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.newsPic,"animation_image");
                ActivityCompat.startActivity(activity,intent,optionsCompat.toBundle());
            }
        });
    }

    private static DiffUtil.ItemCallback<ParseHTML.GuanChaSouceData> DIFF_CALLBACK = new DiffUtil.ItemCallback<ParseHTML.GuanChaSouceData>(){
        @Override
        public boolean areItemsTheSame(@NonNull ParseHTML.GuanChaSouceData oldItem, @NonNull ParseHTML.GuanChaSouceData newItem) {
            return oldItem.getArticleUrl().equals(newItem.getArticleUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ParseHTML.GuanChaSouceData oldItem, @NonNull ParseHTML.GuanChaSouceData newItem) {
            return oldItem.equals(newItem);
        }
    };
    static class ItemViewHolder extends RecyclerView.ViewHolder{

        View card;
        ImageView newsPic;
        TextView newsTitle;
        TextView reads;
        TextView beLong;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            newsPic = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            reads = itemView.findViewById(R.id.comment_num);
            beLong = itemView.findViewById(R.id.belong_to);
        }
    }

    private Bitmap bitmap;
    private void drawableToBitmap(Drawable drawable){
        int height = drawable.getIntrinsicHeight();
        int width = drawable.getIntrinsicWidth();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(width,height,config);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
    }

    private byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }
}
