package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.getsumfoot.data.SellerInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

public class MenuPopup extends AppCompatActivity implements View.OnClickListener, DeviceMapFragment.OnApplySelectedListener {

    private TextView tv_title;
    private RelativeLayout rl_menu1;
    private RelativeLayout rl_menu2;
    private RelativeLayout rl_menu3;

    private TextView tv_menu1;
    private TextView tv_menu2;
    private TextView tv_menu3;

    private TextView tv_menu1_num;
    private TextView tv_menu2_num;
    private TextView tv_menu3_num;


    private Button btn_order;
    private Button btn_return_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);

        tv_title = findViewById(R.id.tv_title);

        rl_menu1 =findViewById(R.id.rl_menu1);
        rl_menu2 = findViewById(R.id.rl_menu2);
        rl_menu3 = findViewById(R.id.rl_menu3);

        tv_menu1 = findViewById(R.id.tv_menu1);
        tv_menu2 = findViewById(R.id.tv_menu2);
        tv_menu3 = findViewById(R.id.tv_menu3);

        tv_menu1_num = findViewById(R.id.tv_menu1_num);
        tv_menu2_num = findViewById(R.id.tv_menu2_num);
        tv_menu3_num = findViewById(R.id.tv_menu3_num);

        btn_order = findViewById(R.id.btn_order);
        btn_return_main = findViewById(R.id.btn_return_main);
        btn_order.setOnClickListener(this);
        btn_return_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_order : {
                //TODO firebase push ALARM service
            }
            case R.id.btn_return_main : {
                finish();
            }
        }
    }

    @Override
    public void onCatagoryApplySelected(Object object) {
        //TODO Fragment -> Activity 데이터 수신 후 처리 하여 poup java에서 처리
        SellerInfo sellerInfo = (SellerInfo)object;

    }
}