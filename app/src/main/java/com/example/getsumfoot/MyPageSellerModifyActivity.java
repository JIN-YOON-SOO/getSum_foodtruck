package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.getsumfoot.data.MenuData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPageSellerModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyPageSellerModifyActivity";
    EditText et_seller_name, et_new_menu_name, et_new_menu_price;
    Button btn_open_hour, btn_close_hour, btn_add_menu, btn_cancel, btn_modify;
    ImageButton btn_add_img;
    TableLayout tl_menu;

    List<HashMap<String, Object>> menu_new_list;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private static final int ACCESS_ALBUM = 1;
    private static final int SET_MENU_DESC = 2;

    private String uid, newOpenTime, newCloseTime, newName, menu_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //button btn_open_hour click->timepicker
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_seller_modify);

        et_seller_name = findViewById(R.id.et_seller_name);

        tl_menu = findViewById(R.id.tl_menu);
        et_new_menu_name = findViewById(R.id.et_new_menu_name);
        et_new_menu_price = findViewById(R.id.et_new_menu_price);
        btn_add_menu = findViewById(R.id.btn_add_menu);
        btn_open_hour = findViewById(R.id.btn_open_hour);
        btn_close_hour = findViewById(R.id.btn_close_hour);

        btn_add_img = findViewById(R.id.btn_add_img);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_modify = findViewById(R.id.btn_modify);

        btn_open_hour.setOnClickListener(this);
        btn_close_hour.setOnClickListener(this);
        btn_add_img.setOnClickListener(this);
        btn_add_menu.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_modify.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //currentUser's uid
       // String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        uid = "Fqm1PUy6hjXACFNOd02zjbnJP152";

        //database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance(); //storage에서 받아와야해

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //https://stackoverflow.com/questions/48901270/how-to-read-firebase-data-using-uid-ref
                String oldName = snapshot.child("name").getValue().toString();
                String oldOpenTime = snapshot.child("time_open").getValue().toString();
                String oldCloseTime = snapshot.child("time_close").getValue().toString();

                et_seller_name.setText(oldName);
                btn_open_hour.setText(oldOpenTime);
                btn_close_hour.setText(oldCloseTime);

                for(DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()){ //children마다 table row 생성
                    MenuData menuData = dataSnapshot.getValue(MenuData.class);
                    addMenuRow(menuData);
                }
                for(DataSnapshot dataSnapshot : snapshot.child("picture").getChildren()){ //pic개수 3개면 추가 안보이게

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error.toException()));
            }
        };
        databaseReference.child("Seller").child(uid).addListenerForSingleValueEvent(eventListener);
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                et_new_menu_name.setText(snapshot.getValue("name"));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        //초기- uid로 db내용 불러오기 datasnapshot.getvalue()
        //null이면 수정이 안된거
//        ValueEventListener mValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                    String key = postSnapshot.getKey();
//                    //info info_each = postSnapshot.getValue(info.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//        //사용
//        databaseReference.addValueEventListener(mValueEventListener);
//        //삭제
//        databaseReference.removeEventListener(mValueEventListener);

    }
    private void addMenuRow(final MenuData menuData){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View tr = inflater.inflate(R.layout.menu_table_row, null);

        TableLayout tl_menu = (TableLayout)findViewById(R.id.tl_menu);

        EditText et_menu_name = tr.findViewById(R.id.et_menu_name);
        EditText et_menu_price = tr.findViewById(R.id.et_menu_price);

        Button btn_add_desc = tr.findViewById(R.id.btn_add_desc);
        Button btn_del_menu = tr.findViewById(R.id.btn_del_menu);
       // assert menuData != null;
        et_menu_name.setText(menuData.getMenuName());
        et_menu_price.setText(menuData.getMenuPrice() +"원");

        tl_menu.addView(tr);
//        et_menu_name.setId(View.generateViewId());
//        et_menu_price.setId(View.generateViewId());
        btn_add_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageSellerModifyActivity.this, ModifyDetailActivity.class);
                intent.putExtra("oldDescription", menuData.getMenuDescription());
                startActivityForResult(intent, SET_MENU_DESC);
            }
        });

        btn_del_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //delete row

            }
        });
    }
    @Override
    public void onClick(View view) {
        try{
            if(view==btn_open_hour||view==btn_close_hour){
                setHour(view);
            }else if(view==btn_add_img){ //이미지 추가되면 거기 표시->얘가 문제다
                selectAlbum();
            }else if(view==btn_add_menu){
                addMenu();
            }else if(view==btn_cancel){ //seller mypage로 이동
                Intent intent = new Intent(MyPageSellerModifyActivity.this, MyPageSellerActivity.class);
                startActivity(intent);
            }else if(view==btn_modify){ //hashmap해서 firebase에 보냄->nullexception이나 이런거 처리 필요
                submitModification();
            }
        }catch(Exception e){
            Toast.makeText(MyPageSellerModifyActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();

            return;
        }
    }
    private void setHour(View view){ //https://stackoverflow.com/questions/20214547/show-timepicker-with-minutes-intervals-in-android/36980811

        int alarmHour = 0, alarmMinute = 0;
        final boolean isOpenHour = view == btn_open_hour;
        final Button btn = (Button)view;

        CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(MyPageSellerModifyActivity.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String am_pm = "오전";
                int hour = hourOfDay;
                if(hourOfDay>12&&hourOfDay<24){
                    am_pm ="오후";
                    hour = hourOfDay - 12;
                }
                String min = minute<10? "0"+minute : String.valueOf(minute);
                String timeStr = am_pm +" "+ hour+":"+min;
                if(isOpenHour){
                    newOpenTime = timeStr;
                }else {
                    newCloseTime = timeStr;
                }
                btn.setText(timeStr);
            }
        }, alarmHour, alarmMinute, false);

        customTimePickerDialog.show();
        Log.d(TAG, String.valueOf(alarmHour));
    }
    private void addMenu(){ //하나 할 때마다 해시맵 추가
        Intent intent = new Intent(MyPageSellerModifyActivity.this, ModifyDetailActivity.class);
        startActivityForResult(intent, SET_MENU_DESC);
//        if(menu_description==null){
//            Toast.makeText(MyPageSellerModifyActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
//            return;
//        }
        String menu_name = et_new_menu_name.getText().toString();
        String menu_price = et_new_menu_price.getText().toString();

        addMenuRow(new MenuData(menu_name, menu_description, Integer.valueOf(menu_price)));

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("menu_name", menu_name);
        hashMap.put("menu_description", menu_description);
        hashMap.put("menu_price", menu_price);

        menu_new_list.add(hashMap);

        et_new_menu_name.setText("");
        et_new_menu_price.setText("");
        menu_description = "";
    }
    private void selectAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, ACCESS_ALBUM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == ACCESS_ALBUM) {
                if(data.getData() != null){
                    try {
                        Uri photo_uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo_uri);
                        //btn_add_img.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(requestCode == SET_MENU_DESC){
                menu_description = data.getStringExtra("menu_description");
            }
        }
    }
    private void submitModification(){
        newName = et_seller_name.getText().toString();

//        Log.d(TAG, "판매자 정보 수정");
//        final ProgressDialog mDialog = new ProgressDialog(MyPageSellerModifyActivity.this);
//        mDialog.setMessage("정보를 수정합니다");
//        mDialog.show();

        try{
            Map<String, Object> updateValues = new HashMap<>();
            //name, times(if changed)
            if(newName!=null) updateValues.put("name", newName);
            if(newOpenTime!=null) updateValues.put("time_open",newOpenTime);
            if(newCloseTime!=null) updateValues.put("time_close",newCloseTime);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Seller/"+uid, updateValues);

            //new menus(if changed)
            if(menu_new_list!=null){
                for(HashMap<String, Object> h:menu_new_list){
                    String key = databaseReference.child(uid).child("menu").push().getKey();
                    childUpdates.put("/Seller/menu/"+key, h);
                }
            }

            databaseReference.updateChildren(childUpdates);

            Intent intent = new Intent(MyPageSellerModifyActivity.this, MyPageSellerActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MyPageSellerModifyActivity.this, "정보 수정에 성공했습니다", Toast.LENGTH_LONG).show();

        }catch (Exception e){

        }
    }
}