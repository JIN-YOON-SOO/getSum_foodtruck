package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.getsumfoot.data.LikesData;

import java.util.ArrayList;
import java.util.List;

public class CustomerLikesAdapter extends RecyclerView.Adapter<CustomerLikesAdapter.ViewHolder> {
    private Context context;
    private List<LikesData> list;

    public CustomerLikesAdapter(ArrayList<LikesData> arrayList, Context context) {
        this.context = context;
        this.list = arrayList;
    }

    // ViewHolder 생성
    // row layout을 화면에 뿌려주고 holder에 연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_likes_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 각 위치에 문자열 세팅
        int itemposition = position;
        holder.tv_like_name.setText(list.get(itemposition).getName());
        holder.tv_like_address.setText(list.get(itemposition).getAddress());
        holder.tv_like_time.setText(list.get(itemposition).getTime());
        holder.tv_like_menu.setText(list.get(itemposition).getMenu());
        Glide.with(holder.itemView)
                .load(list.get(position).getImage())
                .into(holder.iv_like_image);
    }

    // 몇개의 데이터를 리스트로 뿌려줘야하는지 반드시 정의해줘야한다
    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    // ViewHolder는 하나의 View를 보존하는 역할을 한다
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_like_name, tv_like_address, tv_like_menu, tv_like_time;
        public ImageView iv_like_image;

        public ViewHolder(View view){
            super(view);
            tv_like_name = view.findViewById(R.id.tv_like_name);
            tv_like_address = view.findViewById(R.id.tv_like_address);
            tv_like_menu = view.findViewById(R.id.tv_like_menu);
            tv_like_time = view.findViewById(R.id.tv_like_time);
            iv_like_image = view.findViewById(R.id.iv_like_image);
        }
    }
}
