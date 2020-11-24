package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
//tab으로 주문목록, 즐겨찾기 보여짐
public class MyPageCustomerActivity extends AppCompatActivity {
    ViewPager viewPager;
    ConstraintLayout cl_my_page_customer;
    TabLayout tabLayout;
    boolean isHamburgerOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_customer);

//        cl_my_page_customer = findViewById(R.id.cl_my_page_customer);
//        HamburgerBar hamburger = new HamburgerBar(this);
//        cl_my_page_customer.addView(hamburger.getHamburger());

//        viewPager = findViewById(R.id.viewpager);
//        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);

        viewPager=findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewpagerAdapter(getSupportFragmentManager(),0));

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
    }


}