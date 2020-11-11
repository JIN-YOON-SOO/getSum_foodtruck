package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
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
    List<String> menu_delete_list;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private static final int ACCESS_ALBUM = 1;
    private static final int SET_MENU_DESC = 2;
    private static int row_index = 0;

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
        menu_new_list = new ArrayList<>();
        menu_delete_list = new ArrayList<>();

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
        row_index++;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View tr = inflater.inflate(R.layout.menu_table_row, null);

        final TableLayout tl_menu = (TableLayout)findViewById(R.id.tl_menu);

        final EditText et_menu_name = tr.findViewById(R.id.et_menu_name);
        final EditText et_menu_price = tr.findViewById(R.id.et_menu_price);

        Button btn_add_desc = tr.findViewById(R.id.btn_add_desc);
        Button btn_del_menu = tr.findViewById(R.id.btn_del_menu);
       // assert menuData != null;
        et_menu_name.setText(menuData.getMenuName());
        et_menu_price.setText(menuData.getMenuPrice() +"원");

        tl_menu.addView(tr);
//        et_menu_name.setId(View.generateViewId());
//        et_menu_price.setId(View.generateViewId());
        TextWatcher watcher= new TextWatcher() {
            String key, preText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { //id찾기
                if(charSequence.equals(preText)) return;
                key = menuData.getMenuId();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                menu_delete_list.add(key);
                String id = databaseReference.child(uid).child("menu").push().getKey();
                String menu_name = et_menu_name.getText().toString();
                String menu_price = et_menu_price.getText().toString().replace("원","");
  //              String menu_description = menuData.getMenuDescription();
                menuData.setMenuName(menu_name);
                menuData.setMenuPrice(menu_price);
                menuData.setMenuId(id);
//                String menu_name = et_menu_name.getText().toString();
//                String menu_price = et_menu_price.getText().toString();
//                //String menu_description = menuData.getMenuDescription();
//                menuData.setMenuName(menu_name);
//                menuData.setMenuPrice(menu_price);
//                menu_new_list.add(menuData.getMenuHash());
            }
        };

        menu_new_list.add(menuData.getMenuHash());
        et_menu_name.addTextChangedListener(watcher);
        et_menu_price.addTextChangedListener(watcher);

        btn_add_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageSellerModifyActivity.this, ModifyDetailActivity.class);
                intent.putExtra("oldDescription", menuData.getMenuDescription());
                startActivityForResult(intent, SET_MENU_DESC);
                Log.d("abc",menuData.getMenuName()+" "+ menuData.getMenuPrice());
            }
        });

        btn_del_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //delete row
                menu_delete_list.add(menuData.getMenuId());
                tl_menu.removeView(tr);
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

        String key = databaseReference.child(uid).child("menu").push().getKey();
        MenuData menuData = new MenuData(menu_name, menu_description, menu_price, key);
        menu_new_list.add(menuData.getMenuHash());

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
            //name, times(if changed)
            if(newName!=null) databaseReference.child("Seller").child(uid).child("name").setValue(newName);
            if(newOpenTime!=null) databaseReference.child("Seller").child(uid).child("time_open").setValue(newOpenTime);
            if(newCloseTime!=null) databaseReference.child("Seller").child(uid).child("time_close").setValue(newCloseTime);

            //new menus(if changed)
            if(menu_new_list!=null){
                Map<String, Object> childUpdates = new HashMap<>();
                for(HashMap<String, Object> h:menu_new_list){
                    childUpdates.put("/Seller/"+uid+"/menu/"+h.get("menu_id"), h);
                }
                databaseReference.updateChildren(childUpdates);
            }

            if(menu_delete_list!=null){
                for(String s:menu_delete_list){
                    databaseReference.child("Seller").child(uid).child("menu").child(s).removeValue();
                }
            }


//            Intent intent = new Intent(MyPageSellerModifyActivity.this, MyPageSellerActivity.class);
//            startActivity(intent);
//            finish();
            Toast.makeText(MyPageSellerModifyActivity.this, "정보 수정에 성공했습니다", Toast.LENGTH_LONG).show();

        }catch (Exception e){

        }
    }
}