package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private ImageView iv_splash;
    private TextView tv_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        iv_splash = findViewById(R.id.iv_splash);
        tv_splash = findViewById(R.id.tv_splash);

        startLoading();
        //splashAnimation();
    }

    private void startLoading(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout ll_splash = findViewById(R.id.ll_splash);
                ll_splash.setVisibility(View.VISIBLE);
                Animation textAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.anim_splash_textview);
                Animation imageAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.anim_splash_imageview);

                tv_splash.startAnimation(textAnim);
                iv_splash.startAnimation(imageAnim);

                imageAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                textAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        overridePendingTransition(R.anim.move_up, R.anim.move_up);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });
    }

    @Override
    public void onBackPressed() {} // back button 처리 안되게
}