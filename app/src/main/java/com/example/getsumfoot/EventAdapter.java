package com.example.getsumfoot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getsumfoot.R;
import com.example.getsumfoot.data.EventData;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private ArrayList<EventData> arrayList;
    private Context context;

    public EventAdapter(ArrayList<EventData> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list,parent,false);
        EventHolder holder = new EventHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        holder.tv_festival_name.setText(arrayList.get(position).getFestival_name());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }


    public class EventHolder extends RecyclerView.ViewHolder {
        TextView tv_festival_name;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_festival_name = itemView.findViewById(R.id.festival_name);
        }
    }
}
