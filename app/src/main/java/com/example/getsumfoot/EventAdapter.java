package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getsumfoot.data.EventData;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder>{

    private ArrayList<EventData> arrayList;
    private Context context;

    public EventAdapter(ArrayList<EventData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
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
        String date = arrayList.get(position).getStart_date() + "~" + arrayList.get(position).getEnd_data();
        holder.tv_name.setText(arrayList.get(position).getFestival_name());
        holder.tv_notice_specific_event.setText(date);

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class EventHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        TextView tv_notice_specific_event;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.event_name);
            this.tv_notice_specific_event = itemView.findViewById(R.id.notice_specific_event);

        }
    }
}

