package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.getsumfoot.data.ReviewData;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private ArrayList<ReviewData> arrayList;
    private Context context;

    public ReviewAdapter(ArrayList<ReviewData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list,parent,false);
        ReviewHolder holder = new ReviewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPhoto())
                .into(holder.iv_image);
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_content.setText(arrayList.get(position).getContent());

    } //각 아이템 매칭

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title;
        TextView tv_content;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_image = itemView.findViewById(R.id.image);
            this.tv_title = itemView.findViewById(R.id.title);
            this.tv_content = itemView.findViewById(R.id.content);
        }
    }
}
