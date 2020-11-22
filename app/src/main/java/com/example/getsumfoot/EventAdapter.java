package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getsumfoot.data.EventData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private ArrayList<Map<String,Object>> items;

    public EventAdapter(ArrayList<Map<String,Object>> arrayList){
        this.items = arrayList;
    }


    @NonNull
    @Override
    public EventAdapter.EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list,parent,false);
        EventHolder holder = new EventHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventHolder holder, int position) {
        Map<String,Object>item=items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class EventHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvDate;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.event_name);
            tvDate = itemView.findViewById(R.id.event_date);
        }

        public void setItem(Map<String, Object> item) {
            tvName.setText(item.get("축제명").toString());
            tvDate.setText(item.get("축제시작일자").toString() + " ~ " + item.get("축제시작일자").toString());
        }
    }
}
