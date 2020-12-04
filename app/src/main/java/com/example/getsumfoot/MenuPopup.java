package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.getsumfoot.data.MenuDescription;
import com.example.getsumfoot.data.OrderInfo;
import com.example.getsumfoot.data.SellerInfo;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MenuPopup extends AppCompatActivity implements View.OnClickListener{

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

    private String customer_uid, seller_name, seller_address;
    private  ArrayList<Seller_Menu> menuDescription_list;
    private MenuDescription menuDescription[];

    private SellerInfo sellerInfo;

    private OrderInfo orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);

        orderInfo = new OrderInfo();
        orderInfo.menu_price = new String[3];
        orderInfo.menu_num = new int[3];
        orderInfo.tot_price =0;
        orderInfo.menu_count =0;

        orderInfo.menu_num[0] = 0;
        orderInfo.menu_num[1] = 0;
        orderInfo.menu_num[2] = 0;

        menuDescription_list = new ArrayList<>(3);
        menuDescription = new MenuDescription[3];

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

        Intent intent = getIntent();
        sellerInfo = (SellerInfo)intent.getExtras().getSerializable("sellerInfo");
        menuDescription_list = sellerInfo.getSellerMenu();

        int num=0;
        Iterator it = menuDescription_list.iterator();
        while (it.hasNext()) {
            if(num>=3)
                break;
            menuDescription[num] = (MenuDescription) it.next();
            orderInfo.menu_price[num] = menuDescription[num].getPrice();
            num++;
        }

            //tv_menu1_description = menuDescription[0].getDescription();
            //tv_menu2_description = menuDescription[1].getDescription();
            //tv_menu3_description = menuDescription[2].getDescription();

            tv_title.setText(sellerInfo.getName());
            tv_menu1.setText(menuDescription[0].getTitle());
            tv_menu2.setText(menuDescription[1].getTitle());
            tv_menu3.setText(menuDescription[2].getTitle());

        seller_name = sellerInfo.getName();
        seller_address = sellerInfo.getAddress();
        //require next activity
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu1_plus :{
                orderInfo.menu_num[0] +=1;
                orderInfo.menu_count+=1;
                tv_menu1_num.setText(orderInfo.menu_num[0]);
                break;
            }
            case R.id.menu1_minus :{
                if(orderInfo.menu_num[0]>0) {
                    orderInfo.menu_num[0] -= 1;
                    orderInfo.menu_count-=1;
                }
                tv_menu1_num.setText(orderInfo.menu_num[0]);
                break;
            }
            case R.id.menu2_plus :{
                orderInfo.menu_num[1] +=1;
                orderInfo.menu_count+=1;
                tv_menu1_num.setText(orderInfo.menu_num[1]);
                break;
            }
            case R.id.menu2_minus :{
                if(orderInfo.menu_num[1]>0) {
                    orderInfo.menu_num[1] -= 1;
                    orderInfo.menu_count-=1;
                }
                tv_menu1_num.setText(orderInfo.menu_num[1]);
                break;
            }
            case R.id.menu3_plus :{
                orderInfo.menu_num[2] +=1;
                orderInfo.menu_count+=1;
                tv_menu1_num.setText(orderInfo.menu_num[2]);
                break;
            }
            case R.id.menu3_minus :{
                if(orderInfo.menu_num[2]>0){
                    orderInfo.menu_num[2] -=1;
                    orderInfo.menu_count-=1;
                }
                tv_menu1_num.setText(orderInfo.menu_num[2]);
                break;
            }
            case R.id.btn_order : {
                int check = 0;

                for(int i=0; i<3; i++) {
                    orderInfo.tot_price += Integer.parseInt(orderInfo.menu_price[i]) * orderInfo.menu_num[i];

                    if(orderInfo.menu_num[i]>0){
                        switch (i) {
                            case 0 :
                                orderInfo.setMenu_name(menuDescription[0].getTitle());
                                check++;
                                break;
                            case 1 :
                                orderInfo.setMenu_name(menuDescription[1].getTitle());
                                check++;
                                break;
                            case 2 :
                                orderInfo.setMenu_name(menuDescription[2].getTitle());
                                check++;
                                break;
                        }
                    }

                    if(check ==0)
                        orderInfo.setMenu_name("주문한 메뉴없음");
                    else if(check ==2)
                        orderInfo.setMenu_name(orderInfo.getMenu_name()+"외 1");
                                else if(check==3)
                        orderInfo.setMenu_name(orderInfo.getMenu_name()+"외 2");
                                else
                                    ;
                }
                //총 주문 금액

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer").child(customer_uid).child("orders");
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DATE);

                Map<String, Object> childupdates = new HashMap<>();

                childupdates.put("menu_sum",orderInfo.tot_price);   //tot price
                childupdates.put("menu_name",orderInfo.getMenu_name()); //name +외(부가)
                childupdates.put("menu_count",orderInfo.getMenu_count());   //tot select menu num

                childupdates.put("date", year+"."+month+"."+day); //주문일자 set
                childupdates.put("seller_name", seller_name); //seller name
                childupdates.put("seller_address", seller_address); //seller address

                String order_id = customer_uid + "_" + System.currentTimeMillis(); //주문의 id 생성
                reference.child(order_id).updateChildren(childupdates);


                //TODO firebase push ALARM service
            }
            case R.id.btn_return_main : {
                finish();
            }
        }
    }
}