package com.example.getsumfoot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.getsumfoot.data.MenuDescription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuInfoActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mDataset = {"1","2"};

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuDercritions);
        mRecyclerView =  findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        queue = Volley.newRequestQueue(this);
        getMenuDescription();
    }

    public void getMenuDescription() {


        //TODO firebase instacne로 변경
        String url = "firebase";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray arrayaArticles = jsonObj.getJSONArray("seller");
                            List<MenuDescription> menu = new ArrayList<>();

                            for(int i = 0, j = arrayaArticles.length(); i < j; i++) {
                                JSONObject obj = arrayaArticles.getJSONObject(i);
                                //
                                Log.d("description", obj.toString());
                                //
                                MenuDescription menuData = new MenuDescription();
                                menuData.setTitle(obj.getString("title"));
                                menuData.setUrlToImage(obj.getString("urlToImage"));
                                menuData.setDescription(obj.getString("description"));

                                menu.add(menuData);

                            }
                            // specify an adapter (see also next example)
                            mAdapter = new MyAdapter(menu, MenuInfoActivity.this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Object obj = v.getTag();
                                    if(obj != null) {
                                        int position = (int)obj;
                                        Intent intent = new Intent(MenuInfoActivity.this, MenuDetailActivity.class);
                                        intent.putExtra("menuDescription",((MyAdapter)mAdapter).getData(position));
                                        startActivity(intent);
                                    }
                                }
                            });
                            mRecyclerView.setAdapter(mAdapter);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}
