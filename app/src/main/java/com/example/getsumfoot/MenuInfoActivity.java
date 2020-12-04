package com.example.getsumfoot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/*todo 판매자 정보 전부 다 메인화면 로딩시에 받아온다고 하지 않았나?->이 화면에서 db접근 안하고 거기 접근해야 할듯
   이미지는 대표 이미지 하나만 받아옴
    주문 갯수 체크, 금액 수량 계산, 즐겨찾기 버튼, 주문하기 버튼 등 레이아웃 더 구현 필요 */
public class MenuInfoActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mDataset = {"1","2"};

    private Button btn_like; //고객 즐겨찾기 set하는 button
    private String customer_uid; // 현재 접속해서 접근중인 고객 uid
    private String seller_uid; //현재 눌려진 판매자의 uid


    private DatabaseReference reference;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_descriptions);
        mRecyclerView =  findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        queue = Volley.newRequestQueue(this);

        //현재 유저의 db로 접근하기 위한 객체둘
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        customer_uid = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Customer").child(customer_uid); // 현재 유저의 db

        getMenuDescription();
    }

    //고객 즐겨찾기에 저장. btn_like와 연결해 호출
    public void setLike(){
        reference.child("likes").child("seller_uid").setValue(seller_uid);
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
                            mAdapter = new MenuAdapter(menu, MenuInfoActivity.this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Object obj = v.getTag();
                                    if(obj != null) {
                                        int position = (int)obj;
                                        Intent intent = new Intent(MenuInfoActivity.this, MenuDetailActivity.class);
                                        //todo 주문내역 제대로 전달해야 함- 금액, 수량
                                        intent.putExtra("menuDescription",((MenuAdapter)mAdapter).MenuAdapter(position));
                                        intent.putExtra("customer_uid", customer_uid); // 현재 소비자의 uid
                                        //todo seller_name, seller_address를 intent로 넘겨주세요
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
