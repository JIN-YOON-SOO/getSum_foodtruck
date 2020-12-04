package com.example.getsumfoot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.getsumfoot.data.MenuDescription;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MenuDetailActivity extends Activity implements View.OnClickListener {

    private MenuDescription menuDescription;
    private TextView TextView_title, TextView_content;
    private SimpleDraweeView imageView;
    private Button buttonReturnMain, buttonOrder;

    private String customer_uid, seller_name, seller_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);
        setComp();
        getMenuDetail();
        setMenuDescription();
    }

    //기본 컴포넌트 셋팅
    public void setComp() {
        TextView_title = findViewById(R.id.tv_title);
       // TextView_content = findViewById(R.id.tv_content);
        //imageView = findViewById(R.id.iv_image);

        //돌아가기
        buttonReturnMain = findViewById(R.id.btn_return_main);
        buttonReturnMain.setOnClickListener(this);
        //주문하기
        buttonOrder = findViewById(R.id.btn_order);
        buttonOrder.setOnClickListener(this);

    }

    //이전 액티비티에서 받아오는 인텐트
    public void getMenuDetail() {
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bld = intent.getExtras();

            customer_uid = bld.getString("customer_uid"); //고객 uid setting
            //주문목록 업데이트 위한한 seller info
            seller_address = bld.getString("seller_address");
            seller_name = bld.getString("seller_name");

            Object obj = bld.get("menuDescription");
            if(obj != null && obj instanceof MenuDescription) {
                this.menuDescription = (MenuDescription) obj;
            }
        }
    }

    //이전 액티비티에서 받아오는 인텐트에서 정보를 확인
    public void setMenuDescription() {
        if(this.menuDescription != null) {
            String title = this.menuDescription.getTitle();
            if(title != null) {
                TextView_title.setText(title);
            }
            String description = this.menuDescription.getDescription();
            if(description != null) {
                TextView_content.setText(description);
            }
            Uri uri = Uri.parse(menuDescription.getUrlToImage());
            if(uri !=null){
                imageView.setImageURI(uri);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonReturnMain) {
            Intent intent = new Intent(this, MenuInfoActivity.class);
            startActivity(intent);
        }else if(v == buttonOrder){ //고객의 주문목록 db에 저장
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer").child(customer_uid).child("orders");
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DATE);

            Map<String, Object> childupdates = new HashMap<>();

            //todo menu_name(주문하는 메뉴 중 하나- 총 개수 2개 이상일 경우 -> menu_name+"외"),
            // menu_count(총 개수), menu_sum(주문금액 합계) put 해주세요
            childupdates.put("date", year+"."+month+"."+day); //주문일자 set
            childupdates.put("seller_name", seller_name); //seller name
            childupdates.put("seller_address", seller_address); //seller address

            String order_id = customer_uid + "_" + System.currentTimeMillis(); //주문의 id 생성
            reference.child(order_id).updateChildren(childupdates);

            //todo fcm - 판매자에게 주문목록 알림 전달
        }
    }
}
