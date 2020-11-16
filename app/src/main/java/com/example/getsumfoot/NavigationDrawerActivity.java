package com.example.getsumfoot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NavigationDrawerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAGE_UP = 8;
    private static final int PAGE_LEFT = 4;
    private static final int PAGE_RIGHT = 6;
    private static final int PAGE_DOWN = 2;

    //상수로 페이지 넘버 설정
    private static final int HOME = 0;
    private static final int MYPAGE_CUSTOMER = 1;
    private static final int MYPAGE_SELLER = 2;
    private static final int REVIEW = 3;
    private static final int EVENTLIST = 4;

    private int tag=0;

    private ImageView btn_hamburger;

    private View viewLayer;

    private ConstraintLayout cl_hamburger;
    private ConstraintLayout clToolbar;

    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean isHamburgerOpen = false;

    private int pageValue;

    private SlidingPageAnimationListener animationListener;

    //텍스트 클릭 후 이동
    private TextView tv_home; //device map
    private TextView tv_mypage; //isSeller? my page seller : my page user
    private TextView tv_specific; //isSeller? event list : review
    private ImageView img_specific; //isSeller? event list : review

    private boolean isSeller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);

        tv_home = (TextView)findViewById(R.id.tv_tab_list_1);
        tv_home.setOnClickListener(this);
        tv_mypage = (TextView)findViewById(R.id.tv_tab_list_2);
        tv_mypage.setOnClickListener(this);
        tv_specific = (TextView)findViewById(R.id.tv_tab_list_3);
        tv_specific.setOnClickListener(this);
        img_specific = (ImageView)findViewById(R.id.img_tab_list_3);

        //uid가 seller라면 메뉴 이미지, 텍스트 변경
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSeller = Objects.equals(snapshot.child("is_seller").getValue(), true);
                if(isSeller){
                    tv_specific.setText("행사 일정");
                    img_specific.setImageResource(R.drawable.map_seller);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NavigationDrawerActivity", String.valueOf(error.toException()));
            }
        };
        ref.child("Users").child(uid).addListenerForSingleValueEvent(eventListener);

        Intent intent = getIntent();

        tag = intent.getIntExtra("tag", 0);

        setContent(tag);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//        }

        setHamburger();
    }

    @Override
    public void onBackPressed() {
        if(isHamburgerOpen) {
            viewLayer.performClick();
            return;
        } else {
            super.onBackPressed();
        }
    }

    private void setHamburger() {
        cl_hamburger = findViewById(R.id.cl_hamburger);
        clToolbar = findViewById(R.id.cl_toolbar);
        btn_hamburger = findViewById(R.id.btn_hamburger);
        viewLayer = findViewById(R.id.view_layer);

        btn_hamburger.setOnClickListener(l -> {
            //애니메이션 준비
            translateRightAim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
            translateRightAim.setAnimationListener(animationListener);

            pageValue = PAGE_RIGHT;
            cl_hamburger.setAnimation(translateRightAim);
            cl_hamburger.setVisibility(View.VISIBLE);
            viewLayer.setVisibility(View.VISIBLE);

        });

        viewLayer.setOnClickListener((l -> {
            translateLeftAim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
            translateLeftAim.setAnimationListener(animationListener);

            pageValue = PAGE_LEFT;
            cl_hamburger.startAnimation(translateLeftAim);
            cl_hamburger.setVisibility(View.GONE);
            viewLayer.setVisibility(View.GONE);
        }));
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                }
                case PAGE_UP : {
                    break;
                }
                case PAGE_LEFT : {
                    cl_hamburger.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.GONE);
                    isHamburgerOpen = false;
                    break;
                }
                case PAGE_RIGHT : {
                    viewLayer.setVisibility(View.VISIBLE);
                    isHamburgerOpen = true;
                }
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                }
                case PAGE_UP : {
                    break;
                }
                case PAGE_LEFT : {
                    cl_hamburger.setVisibility(View.GONE);
                    //viewLayer.setVisibility(View.GONE);
                    break;
                }
                case PAGE_RIGHT : {
                    cl_hamburger.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    public void onClick(View v) {
        if(v == tv_home){
            if(tag != HOME) {
                tag = HOME;
                viewLayer.performClick();
                setContent(tag);
            }
        } else if(v == tv_mypage){
            if(tag != MYPAGE_SELLER&&isSeller) {
                tag = MYPAGE_SELLER;
                viewLayer.performClick();
                setContent(tag);
            }else if(tag!=MYPAGE_CUSTOMER&&!isSeller){
                tag = MYPAGE_CUSTOMER;
                viewLayer.performClick();
                setContent(tag);
            }
        } else if(v == tv_specific){
            if(tag != REVIEW&&!isSeller) { //customer인데 tag != review
                tag = REVIEW;
                viewLayer.performClick();
                setContent(tag);
            }
            else if(tag != EVENTLIST&&isSeller){
                tag = EVENTLIST;
                viewLayer.performClick();
                setContent(tag);
            }
        }
//        else if(v == tvToolbarTitle){
//            onBackPressed();
//            onBackPressed();
//        } else if(v == tvHambergerTitle){
//            onBackPressed();
//        }

    }

    public void setContent(int tag) {
        Intent intent = null;
        switch(tag){
            case HOME: //device map activity
                tv_home.setTextColor(0xFF4EBFDE);
                intent = new Intent(this, DeviceMapActivity.class);
                break;
            case MYPAGE_CUSTOMER: //my page customer activity
                tv_mypage.setTextColor(0xFF4EBFDE);
                intent = new Intent(this, MyPageCustomerActivity.class);
                break;
            case MYPAGE_SELLER: //my page seller activity
                tv_mypage.setTextColor(0xFF4EBFDE);
                intent = new Intent(this, MyPageSellerActivity.class);
                break;
            case REVIEW: //review activity
                tv_specific.setTextColor(0xFF4EBFDE);
                intent = new Intent(this, ReviewActivity.class);
                break;
            case EVENTLIST: //event list activity
                tv_specific.setTextColor(0xFF4EBFDE);
                intent = new Intent(this, EventListActivity.class);
                break;
        }
        if(isHamburgerOpen == true) {
            viewLayer.performClick();
            isHamburgerOpen = false;
        }
        startActivity(intent);
//        tv_home.setTextColor(0xFF000000);
//        tv_mypage.setTextColor(0xFF000000);
//        tv_specific.setTextColor(0xFF000000);

        //fragment 아직 없음
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        if(tag == HOME){ //devicemap
//            tv_home.setTextColor(0xFF4EBFDE);
//            PlanBackgroundFragment fr = new PlanBackgroundFragment();
//            transaction.replace(R.id.fl_content, fr);
//            transaction.commit();
//        } else if(tag == MYPAGE){
//            tv_mypage.setTextColor(0xFF4EBFDE);
//            LegalFragment fr = new LegalFragment();
//            transaction.replace(R.id.fl_content, fr);
//            transaction.commit();
//        } else if(tag == REVIEW){ //review activity
//            tv_specific.setTextColor(0xFF4EBFDE);
//            GuideFragment fr = new GuideFragment();
//            transaction.replace(R.id.fl_content, fr);
//            transaction.commit();
//        } else if(tag == EVENTLIST){ //eventlistactivity
//            tv_specific.setTextColor(0xFF4EBFDE);
//            blahFragment fr = new BlahFragment();
//            transaction.replace(R.id.fl_content, fr);
//            transaction.commit();
//        }
    }
}
