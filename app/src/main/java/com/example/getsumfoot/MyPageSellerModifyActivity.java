package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.getsumfoot.data.CustomTimePickerDialog;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyPageSellerModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyPageSellerModifyActivity";
    private EditText et_seller_name, et_seller_keyword, et_new_menu_name, et_new_menu_price, et_new_menu_desc;
    private Button btn_open_hour, btn_close_hour, btn_add_menu, btn_cancel, btn_modify, btn_add_img;
    private LinearLayout ll_menu;

    private List<HashMap<String, Object>> menu_new_list;
    private List<HashMap<String, Object>> image_new_list;
    private List<String> menu_delete_list;
    private List<String> image_delete_list;

    class MenuModified{
        Seller_Menu menu;
        boolean isModified;
        MenuModified(Seller_Menu menu, boolean isModified){
            this.menu = menu;
            this.isModified = isModified;
        }
        void setModified(boolean isModified){
            this.isModified = isModified;
        }
    }
    private List<MenuModified> menu_row_list;

    //sellerinfo 고치면 그걸로 바꾸기
    static class SellerInfo implements Serializable { //img, menu가 list
        private double Lat;
        private double Lng;
        private boolean is_open;
        private String name;
        private String keyword;
        private String address;
        private String time_close;
        private String time_open;

        private ArrayList<Seller_Image> sellerImage = new ArrayList<>(); //이미지 max 3개
        private ArrayList<Seller_Menu> sellerMenu= new ArrayList<>();

        public SellerInfo(){
        }

        public void setLat(double lat) {
            Lat = lat;
        }

        public void setLng(double lng) {
            Lng = lng;
        }

        public double getLat() {
            return Lat;
        }

        public double getLng() {
            return Lng;
        }

        public boolean isIs_open() {
            return is_open;
        }

        public void setIs_open(boolean is_open) {
            this.is_open = is_open;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getTime_close() {
            return time_close;
        }

        public void setTime_close(String time_close) {
            this.time_close = time_close;
        }

        public String getTime_open() {
            return time_open;
        }

        public void setTime_open(String time_open) {
            this.time_open = time_open;
        }

        public ArrayList<Seller_Image> getSellerImage() {
            return sellerImage;
        }

        public void setSellerImage(Seller_Image sellerImage) {
            this.sellerImage.add(sellerImage);
        }

        public ArrayList<Seller_Menu> getSellerMenu() {
            return sellerMenu;
        }

        public void setSellerMenu(Seller_Menu sellerMenu) {
            this.sellerMenu.add(sellerMenu);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }
    private MyPageSellerFragment.SellerInfo sellerInfo;

    //private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private static final int ACCESS_ALBUM = 1;
    private static final int MAX_PICTURES = 3;

    private final String current_user = BaseActivity.current_user;

    private String newOpenTime, newCloseTime, oldName, oldKeyword, oldOpenTime, oldCloseTime;
    private int countPictures = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        btn_add_img = findViewById(R.id.btn_add_img);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_modify = findViewById(R.id.btn_modify);

        btn_open_hour.setOnClickListener(this);
        btn_close_hour.setOnClickListener(this);

        btn_add_img.setOnClickListener(this);

        btn_add_menu.setOnClickListener(this);

        btn_cancel.setOnClickListener(this);
        btn_modify.setOnClickListener(this);

        //firebaseAuth = FirebaseAuth.getInstance();

        //currentUser's uid
       // String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //uid = "Fqm1PUy6hjXACFNOd02zjbnJP152";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Seller").child(current_user); //firebase
        storage = FirebaseStorage.getInstance(); //storage에서 받아와야해

        sellerInfo = new MyPageSellerFragment.SellerInfo();
        menu_new_list = new ArrayList<>();
        image_new_list = new ArrayList<>();
        menu_row_list = new ArrayList<>();
        menu_delete_list = new ArrayList<>();
        image_delete_list = new ArrayList<>();

        //mypage seller에서 intent로 값 받아오게 설정
        Intent intent = getIntent();

        sellerInfo = (MyPageSellerFragment.SellerInfo) intent.getSerializableExtra("sellerInfo");

        setComp();


//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) { //https://stackoverflow.com/questions/48901270/how-to-read-firebase-data-using-uid-ref
//                oldName = snapshot.child("name").getValue().toString();
//                oldKeyword = snapshot.child("keyword").getValue().toString();
//                oldOpenTime = snapshot.child("time_open").getValue().toString();
//                oldCloseTime = snapshot.child("time_close").getValue().toString();
//
//                et_seller_name.setText(oldName);
//                et_seller_keyword.setText(oldKeyword);
//                btn_open_hour.setText(oldOpenTime);
//                btn_close_hour.setText(oldCloseTime);
//
//                for(DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()){ //children마다 table row 생성
//                    Seller_Menu sellerMenu = dataSnapshot.getValue(Seller_Menu.class);
//                    addMenuRow(sellerMenu);
//                }
//
//                for(DataSnapshot dataSnapshot : snapshot.child("image").getChildren()){
//                    if(countPictures >= MAX_PICTURES) break; //3장까지 표시
//                    //Uri imageUri = Uri.parse(dataSnapshot.getValue(ImageData.class).getImage_uri()); //직접 표시하는 코드
//                    addPicture(dataSnapshot.getValue(ImageData.class));
//                    //Glide.with(this).load(imageUri).into(btn_img_1);
////                    Glide.with(MyPageSellerModifyActivity.this).load(photoUrl).into(btn_img_1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, String.valueOf(error.toException()));
//            }
//        };
//        databaseReference.addListenerForSingleValueEvent(eventListener);
    }
    private void setComp(){
        List<Seller_Menu> menus = sellerInfo.getSellerMenu();
        List<Seller_Image> images = sellerInfo.getSellerImage();
        oldName = sellerInfo.getName();
        oldKeyword = sellerInfo.getKeyword();
        oldOpenTime = sellerInfo.getTime_open();
        oldCloseTime = sellerInfo.getTime_close();

        et_seller_name.setText(oldName);
        et_seller_keyword.setText(oldKeyword);
        btn_open_hour.setText(oldOpenTime);
        btn_close_hour.setText(oldCloseTime);

        for(Seller_Menu menu : menus){
            addMenuRow(menu);
        }
        for(Seller_Image image : images){
            if(countPictures >= MAX_PICTURES) break; //3장까지 표시
            addPicture(image);
        }
    }

    private void addMenuRow(@NotNull final Seller_Menu sellerMenu){ //view를 추가하고 menu data의 text를 채움
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View tr = inflater.inflate(R.layout.menu_table_row, null);

        final LinearLayout ll_menu = findViewById(R.id.ll_menu);

        final EditText et_menu_name = tr.findViewById(R.id.et_menu_name);
        final EditText et_menu_price = tr.findViewById(R.id.et_menu_price);
        EditText et_menu_desc = tr.findViewById(R.id.et_menu_desc);

        Button btn_delete_menu = tr.findViewById(R.id.btn_delete_menu);
        et_menu_name.setText(sellerMenu.getMenuName());
        et_menu_price.setText(sellerMenu.getMenuPrice() +"원");
        et_menu_desc.setText(sellerMenu.getMenuDescription());

        ll_menu.addView(tr);
        menu_row_list.add(new MenuModified(sellerMenu, false)); //현재 표에 표시되는 메뉴들 list: 수정, 삭제될 수 있다.
        TextWatcher watcher= new TextWatcher() {
            String key;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                key = sellerMenu.getMenuId();
            }

            @Override
            public void afterTextChanged(Editable editable) { //텍스트가 수정되었을 때
                String menu_name = et_menu_name.getText().toString();
                String menu_price = et_menu_price.getText().toString().replace("원","");
                String menu_desc = et_menu_desc.getText().toString();
                for(MenuModified menu_row : menu_row_list){
                    String menu_key = menu_row.menu.getMenuId();
                    if(key.equals(menu_key)){ //새로운 값으로 set
                        menu_row.menu.setMenuName(menu_name);
                        menu_row.menu.setMenuPrice(menu_price);
                        menu_row.menu.setMenuDescription(menu_desc);
                        menu_row.setModified(true); //수정대상임을 표시
                        break;
                    }
                }
            }
        };

        et_menu_name.addTextChangedListener(watcher);
        et_menu_price.addTextChangedListener(watcher);
        et_menu_desc.addTextChangedListener(watcher);

        btn_delete_menu.setOnClickListener(view -> {  //delete를 클릭했다면
            menu_delete_list.add(sellerMenu.getMenuId()); //삭제할 메뉴들 list
            ll_menu.removeView(tr); //화면에서 삭제
        });
    }
    @Override
    public void onClick(View view) {
        try{
            if(view==btn_open_hour||view==btn_close_hour){ //open, close 시간 설정
                setHour(view);
            }else if(view==btn_add_img){ //이미지 추가
                selectAlbum();
            }else if(view==btn_add_menu){ //메뉴 추가
                addMenu();
            }else if(view==btn_cancel){ //seller mypage로 이동
                onBackPressed();
            }else if(view==btn_modify){ //db update
                submitModification();
            }
        }catch(Exception e){
            Toast.makeText(MyPageSellerModifyActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
    }

    private void setHour(View view){ //https://stackoverflow.com/questions/20214547/show-timepicker-with-minutes-intervals-in-android/36980811
        int alarmHour = 0, alarmMinute = 0;
        final boolean isOpenHour = view == btn_open_hour;
        final Button btn = (Button)view;
        //30분 단위
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

    private void addMenu(){ //메뉴 추가
        String menu_name = et_new_menu_name.getText().toString();
        String menu_price = et_new_menu_price.getText().toString();
        String menu_desc = et_new_menu_desc.getText().toString();

        String key = databaseReference.child("menu").push().getKey(); //menu key
        Seller_Menu sellerMenu = new Seller_Menu(menu_name, menu_desc, menu_price, key);
        menu_new_list.add(sellerMenu.getMenuHash()); //추가될 menu list

        et_new_menu_name.setText("");
        et_new_menu_price.setText("");
        et_new_menu_desc.setText("");

        addMenuRow(sellerMenu);
    }

    private void addPicture(Seller_Image sellerImage){ //화면 뷰 추가

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View img = inflater.inflate(R.layout.seller_image, null);
        LinearLayout ll_imgs = findViewById(R.id.ll_imgs);
        ImageButton btn_img = img.findViewById(R.id.btn_img);

        Uri imageUri = Uri.parse(sellerImage.getImageUri());
        Glide.with(this).load(imageUri).into(btn_img);

        ll_imgs.addView(img);

        btn_img.setOnClickListener(view -> { //delete view, add to delete list
            image_delete_list.add(sellerImage.getImageId());
            ll_imgs.removeView(img);
            countPictures--;
        });
        countPictures++;
    }

    private void selectAlbum(){ //image 추가할 때 갤러리 접근
        if(countPictures>=MAX_PICTURES){
            Toast.makeText(MyPageSellerModifyActivity.this, "3장까지 등록할 수 있습니다", Toast.LENGTH_LONG).show();
            return;
        }
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
                if(data.getData() != null){ //image 제대로 들어오면
                    try {
                        Uri imageUri = data.getData();
                        Seller_Image sellerImage = new Seller_Image(imageUri.toString(), current_user);
                        image_new_list.add(sellerImage.getImageHash()); //추가될 이미지의 list
                        addPicture(sellerImage); //view 추가해서 이미지 보이도록
                        }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void storeImage(HashMap h){ //strage에 이미지 update
        Uri file = Uri.fromFile(new File((String) h.get("image_uri")));
        StorageReference ref =  storage.getReference().child("Seller_images/"+h.get("image_id"));
        UploadTask uploadTask = ref.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        h.put("image_uri", uri);
                    }
                });
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


//        UploadTask uploadTask = ref.putFile(Uri.parse(Objects.requireNonNull(h.get("image_uri")).toString())); //갤러리에서 받아온 uri로 이미지에 접근
//
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//
//                // Continue with the task to get the download URL
//                return ref.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    h.put("image_uri", downloadUri);
//                } else {
//                    Log.e(TAG, "image saving failure");
//                }
//            }
//        });
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                 storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                     @Override
//                     public void onSuccess(Uri uri) {
//
//                     }
//                 });
//            }
//        });
//        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//           h.put("image_uri", uri); //해당 이미지의 uri를 갤러리->storage의 경로로 바꾼다
//        }));
    }

    private void submitModification(){ //hashmap->childupdate로 수정!
        String newName = et_seller_name.getText().toString();
        String newKeyword = et_seller_keyword.getText().toString();

        Log.d(TAG, "판매자 정보 수정");
        final ProgressDialog mDialog = new ProgressDialog(MyPageSellerModifyActivity.this);
        mDialog.setMessage("정보를 수정합니다");
        mDialog.show();

        try{
            Map<String, Object> childUpdates = new HashMap<>();

            //update name, keyword, open/close time- if changed
            if(!newName.equals(oldName)) childUpdates.put("/name", newName);
            if(!newKeyword.equals(oldKeyword)) childUpdates.put("/keyword",newKeyword);
            if(newOpenTime!=null&&!newOpenTime.equals(oldOpenTime)) childUpdates.put("/time_open", newOpenTime);
            if(newCloseTime!=null&&!newCloseTime.equals(oldCloseTime)) childUpdates.put("/time_close",newCloseTime);

            //new menus- if exist
            if(menu_new_list!=null){
                for(HashMap<String, Object> h:menu_new_list){
                    childUpdates.put("/menu/"+h.get("menu_id"), h);
                }
            }

            //modify menus- if needed
            if(menu_row_list!=null){
                for(MenuModified mm : menu_row_list){
                    if(mm.isModified){
                        childUpdates.put("/menu/"+mm.menu.getMenuId(), mm.menu.getMenuHash());
                    }
                }
            }

            //new images- if exist
            if(image_new_list!=null){
                for(HashMap<String, Object> h:image_new_list){
                    storeImage(h);
                    childUpdates.put("/image/"+h.get("image_id"), h);
                }
            }

            databaseReference.updateChildren(childUpdates);

            //delete menus
            if(menu_delete_list!=null){
                for(String s:menu_delete_list){
                    databaseReference.child("menu").child(s).removeValue();
                }
            }

            //delete images
            if(image_delete_list!=null){
                for(String s:image_delete_list){ //db와 storage에서 삭제
                    databaseReference.child("image").child(s).removeValue();
                    storage.getReference().child("Seller_images/"+s).delete();
                }
            }

//            Intent intent = new Intent(MyPageSellerModifyActivity.this, BaseActivity.class);
//            startActivity(intent);
//            finish();
           replaceFragment(new MyPageSellerFragment());
            finish();
            Toast.makeText(MyPageSellerModifyActivity.this, "정보 수정에 성공했습니다", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
    //activity --> fragment 전환 함수
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_mypage_seller_container, fragment); //전환하는 fragment의 container(frame layout의 id)
        fragmentTransaction.commit();
    }
}