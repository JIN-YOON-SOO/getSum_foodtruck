package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class MenuPopup extends AppCompatActivity implements View.OnClickListener {

    private SimpleDraweeView iv_image;

    private TextView tv_title;
    private TextView tv_content;
    private Button btn_order;
    private Button btn_return_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);

        iv_image = findViewById(R.id.iv_image);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_title);

        btn_order = findViewById(R.id.btn_order);
        btn_return_main = findViewById(R.id.btn_return_main);
        btn_order.setOnClickListener(this);
        btn_return_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_order : {

            }
            case R.id.btn_return_main : {
                finish();
            }
        }
    }
}