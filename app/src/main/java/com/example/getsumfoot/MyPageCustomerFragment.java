package com.example.getsumfoot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyPageCustomerFragment extends Fragment {
    private static final String TAG="MyPageCustomerFragment";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tv_username;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String current_user;


    public MyPageCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_page_customer, container, false);
        tv_username = root.findViewById(R.id.tv_username);

//        uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = BaseActivity.current_user;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer").child(current_user);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String username = snapshot.child("name").getValue().toString();
                    tv_username.setText(username+"님 안녕하세요");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error.toException()));
            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        tabLayout = root.findViewById(R.id.tab_layout);
        viewPager=root.findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewpagerAdapter(getActivity().getSupportFragmentManager(),0));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });

        return root;
    }
}