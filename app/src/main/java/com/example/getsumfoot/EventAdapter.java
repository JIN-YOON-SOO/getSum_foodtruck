package com.example.getsumfoot;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getsumfoot.data.EventData;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder>{

    private ArrayList<EventData> arrayList;
    private Context context;
    public String webUri;
    private Button button;

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
        holder.tv_name.setText(arrayList.get(position).getFestival_name());
        holder.tv_date.setText(arrayList.get(position).getStart_date() +" - " + arrayList.get(position).getEnd_data());
        holder.tv_address.setText(arrayList.get(position).getAddress());
        holder.tv_homepage.setText(arrayList.get(position).getHomepage());
        holder.tv_telephone.setText(arrayList.get(position).getTelephone());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class EventHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        TextView tv_date;
        TextView tv_address;
        TextView tv_homepage;
        TextView tv_telephone;
        Button link_homepage;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.event_name);
            this.tv_date = itemView.findViewById(R.id.event_date);
            this.tv_address = itemView.findViewById(R.id.event_address);
            this.tv_homepage = itemView.findViewById(R.id.event_homepage);
            this.tv_telephone = itemView.findViewById(R.id.event_telephone);
        }

    }
}

