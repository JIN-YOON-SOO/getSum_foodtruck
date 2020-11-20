package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewpagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    public ViewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        arrayList.add(new CustomerOrdersFragment());
        arrayList.add(new CustomerOrdersFragment());
        name.add("즐겨찾기");
        name.add("주문내역");
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return name.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
