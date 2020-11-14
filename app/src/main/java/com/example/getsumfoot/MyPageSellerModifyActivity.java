package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.getsumfoot.data.ImageData;
import com.example.getsumfoot.data.MenuData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class MyPageSellerModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyPageSellerModifyActivity";
    private EditText et_seller_name, et_seller_keyword, et_new_menu_name, et_new_menu_price, et_new_menu_desc;
    private Button btn_open_hour, btn_close_hour, btn_add_menu, btn_cancel, btn_modify, btn_add_img;
    private ImageButton btn_img_1, btn_img_2, btn_img_3;
    private LinearLayout ll_menu;

    private List<HashMap<String, Object>> menu_new_list;
    //List<HashMap<MenuData, Boolean>> menu_row_list; //(menudata, 수정여부)
    private List<String> menu_delete_list;
    private List<String> image_delete_list;

    private List<Uri> photo_new_list;
   // private Stack<View> available_img_view;

    class MenuModified{
        MenuData md;
        boolean isModified;
        MenuModified(MenuData md, boolean isModified){
            this.md = md;
            this.isModified = isModified;
        }
        void setModified(boolean isModified){
            this.isModified = isModified;
        }
    }
    private List<MenuModified> menu_row_list;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private static final int ACCESS_ALBUM = 1;
    private static final int MAX_PICTURES = 3;

    private String uid, newOpenTime, newCloseTime, oldName, oldKeyword, oldOpenTime, oldCloseTime;
    private int countPictures = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //button btn_open_hour click->timepicker
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_seller_modify);

        et_seller_name = findViewById(R.id.et_seller_name);
        et_seller_keyword = findViewById(R.id.et_seller_keyword);

        ll_menu = findViewById(R.id.ll_menu);
        et_new_menu_name = findViewById(R.id.et_new_menu_name);
        et_new_menu_price = findViewById(R.id.et_new_menu_price);
        et_new_menu_desc = findViewById(R.id.et_new_menu_desc);
        btn_add_menu = findViewById(R.id.btn_add_menu);
        btn_open_hour = findViewById(R.id.btn_open_hour);
        btn_close_hour = findViewById(R.id.btn_close_hour);

     //   available_img_view = new Stack<>();
        btn_add_img = findViewById(R.id.btn_add_img);
       // btn_img_1 = findViewById(R.id.btn_img_1);
       // btn_img_2 = findViewById(R.id.btn_img_2);
       // btn_img_3 = findViewById(R.id.btn_img_3);
       // available_img_view.add(btn_img_1);
       // available_img_view.add(btn_img_2);
       // available_img_view.add(btn_img_3);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_modify = findViewById(R.id.btn_modify);

        btn_open_hour.setOnClickListener(this);
        btn_close_hour.setOnClickListener(this);

        btn_add_img.setOnClickListener(this);
//        btn_img_1.setOnClickListener(this);
//        btn_img_2.setOnClickListener(this);
//        btn_img_3.setOnClickListener(this);

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
        menu_row_list = new ArrayList<>();
        menu_delete_list = new ArrayList<>();
        image_delete_list = new ArrayList<>();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //still need to handle null exception
                //https://stackoverflow.com/questions/48901270/how-to-read-firebase-data-using-uid-ref
                oldName = snapshot.child("name").getValue().toString();
                oldKeyword = snapshot.child("keyword").getValue().toString();
                oldOpenTime = snapshot.child("time_open").getValue().toString();
                oldCloseTime = snapshot.child("time_close").getValue().toString();

                et_seller_name.setText(oldName);
                et_seller_keyword.setText(oldKeyword);
                btn_open_hour.setText(oldOpenTime);
                btn_close_hour.setText(oldCloseTime);

                for(DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()){ //children마다 table row 생성
                    MenuData menuData = dataSnapshot.getValue(MenuData.class);
                    addMenuRow(menuData);
                }

                for(DataSnapshot dataSnapshot : snapshot.child("image").getChildren()){ //pic개수 3개면 추가 안보이게
                    //countPictures ++;
                    if(countPictures >= MAX_PICTURES) break; //3장까지 표시
                    //Uri imageUri = Uri.parse(dataSnapshot.getValue(ImageData.class).getImage_uri());
                    addPicture(dataSnapshot.getValue(ImageData.class));

                    //countPictures ++;
                    //Glide.with(this).load(imageUri).into(btn_img_1);
                    //String imageUri = dataSnapshot.getValue().toString();
                    //addPicture(imageUri);
//                    Glide.with(MyPageSellerModifyActivity.this).load(photoUrl).into(btn_img_1);
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
    private void addMenuRow(@NotNull final MenuData menuData){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View tr = inflater.inflate(R.layout.menu_table_row, null);

        final LinearLayout ll_menu = findViewById(R.id.ll_menu);

        final EditText et_menu_name = tr.findViewById(R.id.et_menu_name);
        final EditText et_menu_price = tr.findViewById(R.id.et_menu_price);
        EditText et_menu_desc = tr.findViewById(R.id.et_menu_desc);

        Button btn_delete_menu = tr.findViewById(R.id.btn_delete_menu);
       // assert menuData != null;
        et_menu_name.setText(menuData.getMenuName());
        et_menu_price.setText(menuData.getMenuPrice() +"원");
        et_menu_desc.setText(menuData.getMenuDescription());

        ll_menu.addView(tr);
//        HashMap<MenuData, Boolean> menu_row= new HashMap<>();
//        menu_row.put(menuData, false); //초기값: 수정되지 않음
        menu_row_list.add(new MenuModified(menuData, false)); //현재 표에 표시되는 메뉴들: 수정, 삭제될 수 있다.
//        et_menu_name.setId(View.generateViewId());
//        et_menu_price.setId(View.generateViewId());
        TextWatcher watcher= new TextWatcher() {
            String key;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if(key.equals(menuData.getMenuId())) return;
                key = menuData.getMenuId();
            }

            @Override
            public void afterTextChanged(Editable editable) {
               // menu_delete_list.add(key);
               // String id = databaseReference.child(uid).child("menu").push().getKey();
                String menu_name = et_menu_name.getText().toString();
                String menu_price = et_menu_price.getText().toString().replace("원","");
                String menu_desc = et_menu_desc.getText().toString();
                for(MenuModified menu_row : menu_row_list){
                    String menu_key = menu_row.md.getMenuId();
                    if(key.equals(menu_key)){
                        menu_row.md.setMenuName(menu_name);
                        menu_row.md.setMenuPrice(menu_price);
                        menu_row.md.setMenuDescription(menu_desc);
                        menu_row.setModified(true);
                        break;
                    }
                }
  //              String menu_description = menuData.getMenuDescription();
//                menuData.setMenuName(menu_name);
//                menuData.setMenuPrice(menu_price);
//                menuData.setMenuId(id);
//                String menu_name = et_menu_name.getText().toString();
//                String menu_price = et_menu_price.getText().toString();
//                //String menu_description = menuData.getMenuDescription();
//                menuData.setMenuName(menu_name);
//                menuData.setMenuPrice(menu_price);
//                menu_new_list.add(menuData.getMenuHash());
            }
        };

        //menu_new_list.add(menuData.getMenuHash());
        et_menu_name.addTextChangedListener(watcher);
        et_menu_price.addTextChangedListener(watcher);
        et_menu_desc.addTextChangedListener(watcher);

        btn_delete_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //delete row
                menu_delete_list.add(menuData.getMenuId());
                ll_menu.removeView(tr);
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
            }else if(view==btn_img_1||view==btn_img_2||view==btn_img_3){
                deletePicture(view);
            }else if(view==btn_add_menu){
                addMenu();
            }else if(view==btn_cancel){ //seller mypage로 이동
                Intent intent = new Intent(MyPageSellerModifyActivity.this, MyPageSellerActivity.class);
                startActivity(intent);
            }else if(view==btn_modify){ //hashmap해서 firebase에 보냄->nullexception이나 이런거 처리 필요
                submitModification();
            }
        }catch(Exception e){ //why..?
            Toast.makeText(MyPageSellerModifyActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
    private void addMenu(){ //still need to handle null value
//        Intent intent = new Intent(MyPageSellerModifyActivity.this, ModifyDetailActivity.class);
//        startActivityForResult(intent, SET_MENU_DESC);
////        if(menu_description==null){
////            Toast.makeText(MyPageSellerModifyActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
////            return;
////        }
        String menu_name = et_new_menu_name.getText().toString();
        String menu_price = et_new_menu_price.getText().toString();
        String menu_desc = et_new_menu_desc.getText().toString();

        String key = databaseReference.child(uid).child("menu").push().getKey();
        MenuData menuData = new MenuData(menu_name, menu_desc, menu_price, key);
        menu_new_list.add(menuData.getMenuHash());

        et_new_menu_name.setText("");
        et_new_menu_price.setText("");
        et_new_menu_desc.setText("");

        addMenuRow(menuData);
    }
    private void selectAlbum(){
        if(countPictures>=MAX_PICTURES){
            Toast.makeText(MyPageSellerModifyActivity.this, "3장까지 등록할 수 있습니다", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, ACCESS_ALBUM);
    }
    private void addPicture(ImageData imageData){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View img = inflater.inflate(R.layout.seller_image, null);
        LinearLayout ll_imgs = findViewById(R.id.ll_imgs);
        ImageButton btn_img = img.findViewById(R.id.btn_img);
        Uri imageUri = Uri.parse(imageData.getImage_uri());
        Glide.with(this).load(imageUri).into(btn_img);
        ll_imgs.addView(img);

        btn_img.setOnClickListener(view -> {
            //delete view, add to delete list
            image_delete_list.add(imageData.getImage_id());
            ll_imgs.removeView(img);
            countPictures--;
        });
        countPictures++;
    }
    private void deletePicture(View view){ //image view반납

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == ACCESS_ALBUM) {
                if(data.getData() != null){
                    try {
                        String imageUri = data.getData().toString();
                        String key = databaseReference.child(uid).child("image").push().getKey();
                        addPicture(new ImageData(imageUri, key));
                        //Glide.with(this).load(photoUri).into(btn_img_1);
                        //addPicture(photoUri.toString());
                        //사진 저장하는 코드
//                        SimpleDateFormat sdf= new SimpleDateFormat("yyyMMddhhmmss"); //2020111511224
//                        String fileName= sdf.format(new Date())+".png";
//                        StorageReference imgRef = storage.getReference("Seller_images/"+fileName);
//                        UploadTask uploadTask=imgRef.putFile(photoUri);
//                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                //이미지 업로드가 성공되었으므로 곧바로 firebase storage의 이미지 파일 다운로드 URL을 얻어오기
//                                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        //파라미터로 firebase의 저장소에 저장되어 있는 이미지에 대한 다운로드 주소(URL)을 문자열로 얻어오기
//                                        G.porfileUrl= uri.toString();
//                                        Toast.makeText(MyPageSellerModifyActivity.this, "프로필 저장 완료", Toast.LENGTH_SHORT).show();
//
//                                        //1. Firebase Database에 nickName, profileUrl을 저장
//                                        //firebase DB관리자 객체 소환
//                                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
//                                        //'profiles'라는 이름의 자식 노드 참조 객체 얻어오기
//                                        DatabaseReference profileRef= firebaseDatabase.getReference("profiles");
//
//                                        //닉네임을 key 식별자로 하고 프로필 이미지의 주소를 값으로 저장
//                                        profileRef.child(G.nickName).setValue(G.porfileUrl);
//
//                                        //2. 내 phone에 nickName, profileUrl을 저장
//                                        SharedPreferences preferences= getSharedPreferences("account",MODE_PRIVATE);
//                                        SharedPreferences.Editor editor=preferences.edit();
//
//                                        editor.putString("nickName",G.nickName);
//                                        editor.putString("profileUrl", G.porfileUrl);
//
//                                        editor.commit();

                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo_uri);
                        //btn_add_img.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void submitModification(){ //hashmap->childupdate로 수정!
        String newName = et_seller_name.getText().toString();
        String newKeyword = et_seller_keyword.getText().toString();

//        Log.d(TAG, "판매자 정보 수정");
//        final ProgressDialog mDialog = new ProgressDialog(MyPageSellerModifyActivity.this);
//        mDialog.setMessage("정보를 수정합니다");
//        mDialog.show();

        try{
            //name, times(if changed)
            if(!newName.equals(oldName)) databaseReference.child("Seller").child(uid).child("name").setValue(newName);
            //if(newKeyword!=null) databaseReference.child("Seller").child(uid).child("keyword").setValue(newKeyword);
            if(newOpenTime!=null) databaseReference.child("Seller").child(uid).child("time_open").setValue(newOpenTime);
            if(newCloseTime!=null) databaseReference.child("Seller").child(uid).child("time_close").setValue(newCloseTime);

            Map<String, Object> childUpdats = new HashMap<>();
            if(newKeyword!=null) {
                childUpdats.put("/Seller/"+uid+"/keyword",newKeyword);
                databaseReference.updateChildren(childUpdats);
            }
            //new menus(if changed)
            if(menu_new_list!=null){
                Map<String, Object> childUpdates = new HashMap<>();
                for(HashMap<String, Object> h:menu_new_list){
                    childUpdates.put("/Seller/"+uid+"/menu/"+h.get("menu_id"), h);
                }
                databaseReference.updateChildren(childUpdates);
            }

            //modified menus
            if(menu_row_list!=null){
                Map<String, Object> childUpdates = new HashMap<>();
                for(MenuModified mm : menu_row_list){
                    if(mm.isModified){
                        childUpdates.put("/Seller/"+uid+"/menu/"+mm.md.getMenuId(), mm.md.getMenuHash());
                       // databaseReference.child("Seller").child(uid).child("menu").up
//                        DatabaseReference ref = databaseReference.child("Seller").child(uid).child("menu").child(mm.md.getMenuId());
//                        ref.child("menu_name").setValue(mm.md.getMenuName());
//                        ref.child("menu_price").setValue(mm.md.getMenuPrice());
//                        ref.child("menu_description").setValue(mm.md.getMenuDescription());
                    }
                }
                databaseReference.updateChildren(childUpdates);
            }

            //delete menus
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