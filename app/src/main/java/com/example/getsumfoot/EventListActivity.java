package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.getsumfoot.data.EventData;
import com.example.getsumfoot.data.ReviewData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EventListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EventData> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.event_recycle_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        database.getReference("EventData").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 데이터 리스트 추출
                    EventData eventData = snapshot.getValue(EventData.class); //만들어뒀던 ReviewData 객체에 데이터를 담는다
                    arrayList.add(eventData); // 담은 데이터들을 배열리스트에 넣고 리사이틀러뷰로 보낼준비

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity",String.valueOf(databaseError));
            }
        });

        adapter = new EventAdapter(arrayList,this);
        EventDateAdapter adapters = new EventDateAdapter();

        adapters.setOnItemClickListener(new EventDateAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(View v, int position) {
                recyclerView.setAdapter(adapters);
            }
        });

        recyclerView.setAdapter(adapter);

    }
}