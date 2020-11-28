package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.getsumfoot.data.EventData;
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
    private TextView textView;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.event_recycle_view);

        try{
            InputStream inputStream = getAssets().open("EventList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            Map<String,Object> eventListResult = gson.fromJson(jsonObject.get("이벤트").toString(),new TypeToken<Map<String,Object>>(){}.getType());
            ArrayList<Map<String,Object>> jsonList = (ArrayList) eventListResult.get("seoulEvent");
            adapter = new EventAdapter(jsonList);
            recyclerView.setAdapter(adapter);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}