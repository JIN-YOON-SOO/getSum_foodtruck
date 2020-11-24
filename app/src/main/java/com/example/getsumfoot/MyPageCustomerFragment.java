package com.example.getsumfoot;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class MyPageCustomerFragment extends Fragment {
    ViewPager viewPager;
    ConstraintLayout cl_my_page_customer;
    TabLayout tabLayout;
    boolean isHamburgerOpen;

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