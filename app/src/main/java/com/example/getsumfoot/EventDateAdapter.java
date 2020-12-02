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

public class EventDateAdapter extends RecyclerView.Adapter<EventDateAdapter.EventDateHolder> {
    private ArrayList<EventData> arrayList;
    private Context context;
    private EventDateAdapter.OnItemClickListener mListener = null;

    public EventDateAdapter(ArrayList<EventData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void setOnItemClickListener(EventDateAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    @NonNull
    @Override
    public EventDateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list,parent,false);
        EventDateAdapter.EventDateHolder holder = new EventDateAdapter.EventDateHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventDateHolder holder, int position) {
        holder.tv_notice_specific_event.setText(arrayList.get(position).getStart_date());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

     class EventDateHolder extends RecyclerView.ViewHolder {
        TextView tv_notice_specific_event;

         TextView textView;

         EventDateHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.event_name);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }

}
