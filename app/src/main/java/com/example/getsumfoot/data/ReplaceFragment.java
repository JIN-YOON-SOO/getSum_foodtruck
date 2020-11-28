package com.example.getsumfoot.data;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.getsumfoot.R;

public class ReplaceFragment{ //activity->fragment전환 메소드 작성중

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment){
        //FragmentManager fragmentManager = new FragmentActivity().getSupportFragmentManager(); //host에 attach 되어야
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_mypage_seller_container, fragment); //화면마다 id 다름->전달받아야함
        fragmentTransaction.commit();
    }
}
