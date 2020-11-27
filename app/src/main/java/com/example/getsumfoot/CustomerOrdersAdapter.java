package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.getsumfoot.data.OrderData;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.ViewHolder> {
    private Context context;
    private List<OrderData> list;

    public CustomerOrdersAdapter(ArrayList<OrderData> arrayList, Context context) {
        this.context = context;
        this.list = arrayList;
    }

    // ViewHolder 생성
    // row layout을 화면에 뿌려주고 holder에 연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_orders_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 각 위치에 문자열 세팅
        int itemposition = position;
        holder.tv_seller_name.setText(list.get(itemposition).getSeller_name());
        holder.tv_seller_address.setText(list.get(itemposition).getSeller_address());
        holder.tv_order_date.setText(list.get(itemposition).getDate());
        holder.tv_order_menu.setText(list.get(itemposition).getMenu_name()); //menu count 어디서 적용?
        holder.tv_order_sum.setText(list.get(itemposition).getMenu_sum()+"원");

    }

    // 몇개의 데이터를 리스트로 뿌려줘야하는지 반드시 정의해줘야한다
    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    // ViewHolder는 하나의 View를 보존하는 역할을 한다
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_seller_name, tv_seller_address, tv_order_menu, tv_order_date, tv_order_sum;

        public ViewHolder(View view){
            super(view);
            tv_seller_name = view.findViewById(R.id.tv_seller_name);
            tv_seller_address = view.findViewById(R.id.tv_seller_address);
            tv_order_menu = view.findViewById(R.id.tv_order_menu);
            tv_order_date = view.findViewById(R.id.tv_order_date);
            tv_order_sum = view.findViewById(R.id.tv_order_sum);
        }
    }
}
