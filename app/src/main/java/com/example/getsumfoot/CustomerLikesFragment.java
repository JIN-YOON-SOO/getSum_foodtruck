package com.example.getsumfoot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.getsumfoot.data.LikesData;
import com.example.getsumfoot.data.ReviewData;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

//todo firebase 연결해서 필요한 정보 뿌리기
public class CustomerLikesFragment extends Fragment {
    private static final String TAG="CustomerLikesFragment";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<LikesData> list;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference customerReference;
    private DatabaseReference sellerReference;
    private String current_user;
    private String name, address, time, menu, image, isOpen;

    public CustomerLikesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_customer_likes, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        current_user = BaseActivity.current_user;

        customerReference = database.getReference("Customer/"+current_user+"/likes");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_customer_likes);
        list = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // /customer/uid/likes
                if(snapshot.exists()){
                    list.clear(); //기존 배열리스트 초기화
                    for(DataSnapshot snap : snapshot.getChildren()){ // 데이터 리스트 추출
                        String seller_uid = snap.getValue().toString();

                        sellerReference = FirebaseDatabase.getInstance().getReference().child("Seller").child(seller_uid);

                        sellerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) { // /seller/uid -> like한 가게들 데이터 가져오기
                                if(snapshot.exists()){
                                    name = snapshot.child("name").getValue().toString();
                                    address = snapshot.child("address").getValue().toString();
                                    time = snapshot.child("time_open").getValue().toString() + " ~ "
                                            +snapshot.child("time_close").getValue().toString();
                                    isOpen = snapshot.child("is_open").getValue().toString();

                                    for(DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()){
                                        menu = "대표: "+dataSnapshot.child("menu_name").getValue().toString(); //하나만
                                        break;
                                    }

                                    for(DataSnapshot dataSnapshot : snapshot.child("image").getChildren()){
                                        image = dataSnapshot.child("image_uri").getValue().toString(); //하나만
                                        break;
                                    }
                                    list.add(new LikesData(name, address, menu, isOpen, time, image));
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, String.valueOf(error.toException()));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error.toException()));
            }
        });

        adapter = new CustomerLikesAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
