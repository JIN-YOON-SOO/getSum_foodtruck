package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.getsumfoot.data.SellerInfo;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPageSellerModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyPageSellerModifyActivity";
    private EditText et_seller_name, et_seller_keyword, et_new_menu_name, et_new_menu_price, et_new_menu_desc;
    private Button btn_open_hour, btn_close_hour, btn_add_menu, btn_cancel, btn_modify, btn_add_img;

    private List<HashMap<String, Object>> menu_new_list;
    private List<Seller_Image> image_new_list;
    private List<String> menu_delete_list;
    private List<String> image_delete_list;
    Map<String, Object> childUpdates;

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

    private SellerInfo sellerInfo;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int ACCESS_ALBUM = 1;
    private static final int MAX_PICTURES = 3;

    private String current_user;

    private String newOpenTime, newCloseTime, oldName, oldKeyword, oldOpenTime, oldCloseTime;
    private int countPictures = 0;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_seller_modify);

        et_seller_name = findViewById(R.id.et_seller_name);
        et_seller_keyword = findViewById(R.id.et_seller_keyword);

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

        //currentUser's uid
       // String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Seller").child(current_user); //firebase
        storage = FirebaseStorage.getInstance(); //storage?????? ???????????????

        sellerInfo = new SellerInfo();
        menu_new_list = new ArrayList<>();
        image_new_list = new ArrayList<>();
        menu_row_list = new ArrayList<>();
        menu_delete_list = new ArrayList<>();
        image_delete_list = new ArrayList<>();
        childUpdates = new HashMap<>();

        //mypage seller?????? intent??? ??? ???????????? ??????
        Intent intent = getIntent();

        sellerInfo = (SellerInfo) intent.getSerializableExtra("sellerInfo");

        setComp();
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
            if(countPictures >= MAX_PICTURES) break; //3????????? ??????
            addPicture(image);
        }
    }

    private void addMenuRow(@NotNull final Seller_Menu sellerMenu){ //view??? ???????????? menu data??? text??? ??????
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View tr = inflater.inflate(R.layout.menu_table_row, null);

        final LinearLayout ll_menu = findViewById(R.id.ll_menu);

        final EditText et_menu_name = tr.findViewById(R.id.et_menu_name);
        final EditText et_menu_price = tr.findViewById(R.id.et_menu_price);
        EditText et_menu_desc = tr.findViewById(R.id.et_menu_desc);

        Button btn_delete_menu = tr.findViewById(R.id.btn_delete_menu);
        et_menu_name.setText(sellerMenu.getMenuName());
        et_menu_price.setText(sellerMenu.getMenuPrice() +"???");
        et_menu_desc.setText(sellerMenu.getMenuDescription());

        ll_menu.addView(tr);
        menu_row_list.add(new MenuModified(sellerMenu, false)); //?????? ?????? ???????????? ????????? list: ??????, ????????? ??? ??????.
        TextWatcher watcher= new TextWatcher() {
            String key;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                key = sellerMenu.getMenuId();
            }

            @Override
            public void afterTextChanged(Editable editable) { //???????????? ??????????????? ???
                String menu_name = et_menu_name.getText().toString();
                String menu_price = et_menu_price.getText().toString().replace("???","");
                String menu_desc = et_menu_desc.getText().toString();
                for(MenuModified menu_row : menu_row_list){
                    String menu_key = menu_row.menu.getMenuId();
                    if(key.equals(menu_key)){ //????????? ????????? set
                        menu_row.menu.setMenuName(menu_name);
                        menu_row.menu.setMenuPrice(menu_price);
                        menu_row.menu.setMenuDescription(menu_desc);
                        menu_row.setModified(true); //?????????????????? ??????
                        break;
                    }
                }
            }
        };

        et_menu_name.addTextChangedListener(watcher);
        et_menu_price.addTextChangedListener(watcher);
        et_menu_desc.addTextChangedListener(watcher);

        btn_delete_menu.setOnClickListener(view -> {  //delete??? ???????????????
            menu_delete_list.add(sellerMenu.getMenuId()); //????????? ????????? list
            ll_menu.removeView(tr); //???????????? ??????
        });
    }
    @Override
    public void onClick(View view) {
        try{
            if(view==btn_open_hour||view==btn_close_hour){ //open, close ?????? ??????
                setHour(view);
            }else if(view==btn_add_img){ //????????? ??????
                selectAlbum();
            }else if(view==btn_add_menu){ //?????? ??????
                addMenu();
            }else if(view==btn_cancel){ //seller mypage??? ??????
                onBackPressed();
            }else if(view==btn_modify){ //db update
                submitModification();
            }
        }catch(Exception e){
            Toast.makeText(MyPageSellerModifyActivity.this, "?????? ??????????????????", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }
    }

    private void setHour(View view){ //https://stackoverflow.com/questions/20214547/show-timepicker-with-minutes-intervals-in-android/36980811
        int alarmHour = 0, alarmMinute = 0;
        final boolean isOpenHour = view == btn_open_hour;
        final Button btn = (Button)view;
        //30??? ??????
        CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(MyPageSellerModifyActivity.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String am_pm = "??????";
                int hour = hourOfDay;
                if(hourOfDay>12&&hourOfDay<24){
                    am_pm ="??????";
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

    private void addMenu(){ //?????? ??????
        String menu_name = et_new_menu_name.getText().toString();
        String menu_price = et_new_menu_price.getText().toString();
        String menu_desc = et_new_menu_desc.getText().toString();

        String key = databaseReference.child("menu").push().getKey(); //menu key
        Seller_Menu sellerMenu = new Seller_Menu(menu_name, menu_desc, menu_price, key);
        menu_new_list.add(sellerMenu.getMenuHash()); //????????? menu list

        et_new_menu_name.setText("");
        et_new_menu_price.setText("");
        et_new_menu_desc.setText("");

        addMenuRow(sellerMenu);
    }

    private void addPicture(Seller_Image sellerImage){ //?????? ??? ??????

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View img = inflater.inflate(R.layout.seller_image, null);
        LinearLayout ll_imgs = findViewById(R.id.ll_imgs);
        ImageButton btn_img = img.findViewById(R.id.btn_img);

        Uri imageUri = Uri.parse(sellerImage.getImageUri());
        Glide.with(this).load(imageUri).into(btn_img);

        ll_imgs.addView(img);

        btn_img.setOnClickListener(view -> { //delete view, add to delete list
            ll_imgs.removeView(img);
            countPictures--;
            for(Seller_Image h:image_new_list){ //??????????????? ?????? ??? ????????????
                if(h.getImageId().equals(sellerImage.getImageId())) {
                    image_new_list.remove(h);
                    return;
                }
            }
            image_delete_list.add(sellerImage.getImageId());
        });
        countPictures++;
    }

    private void selectAlbum(){ //image ????????? ??? ????????? ??????
        if(countPictures>=MAX_PICTURES){
            Toast.makeText(MyPageSellerModifyActivity.this, "3????????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, ACCESS_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == ACCESS_ALBUM) {
                if(data.getData() != null){ //image ????????? ????????????
                    try {
                        Uri imageUri = data.getData();
                        Seller_Image sellerImage = new Seller_Image(imageUri.toString(), current_user);
                        image_new_list.add(sellerImage); //????????? ???????????? list
                        addPicture(sellerImage); //view ???????????? ????????? ????????????
                        }
                    catch (Exception e) {
                        Log.e(TAG+": onactivityresult", e.toString());
                    }
                }
            }
        }
    }


    private void storeImage(Seller_Image h, int i, int elements){ //strage??? ????????? update ->??? ?????? ??? ?????? ?????? ??????
        storageReference =  storage.getReferenceFromUrl("gs://getsumfoot.appspot.com").child("Seller_images/"+h.getImageId());
            UploadTask uploadTask = storageReference.putFile(Uri.parse(h.getImageUri()));
            Log.e("Storage",h.getImageUri());
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Log.e("Storage 505",e.toString());
                        }
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String strUri = downloadUri.toString();
                        h.setImageUri(strUri);
                        childUpdates.put("/image/"+h.getImageId(), h.getImageHash());
                        Log.e("Storage", "i="+i+", elements="+elements+", uri="+strUri);
                        //if(i==elements) updateDB(); //??????????????????
                        updateDB();
                    } else {
                        Toast.makeText(MyPageSellerModifyActivity.this, "????????? ??????!", Toast.LENGTH_LONG).show();
                        Log.e("Storage 524", task.getException().toString());
                    }
                }
            });
    }
    private void updateDB(){
        if(childUpdates.isEmpty()) { //???????????? ?????? ?????? ?????? ??????
            mDialog.dismiss();
            Intent intent = new Intent(MyPageSellerModifyActivity.this, BaseActivity.class);
            intent.putExtra("is_seller", "true");
            intent.putExtra("current_user", firebaseAuth.getCurrentUser());
            startActivity(intent);
            return;
        }
        databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    Toast.makeText(MyPageSellerModifyActivity.this, "?????? ????????? ??????????????????", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MyPageSellerModifyActivity.this, BaseActivity.class);
                    intent.putExtra("is_seller", "true");
                    intent.putExtra("current_user", firebaseAuth.getCurrentUser());
                    startActivity(intent);
                }else{
                    Log.e("Storage", task.getException().toString());
                }
            }
        });
    }
    private void submitModification(){ //hashmap->childupdate??? ??????!

        String newName = et_seller_name.getText().toString();
        String newKeyword = et_seller_keyword.getText().toString();

//        Log.d(TAG, "????????? ?????? ??????");
        mDialog = new ProgressDialog(MyPageSellerModifyActivity.this);
        mDialog.setMessage("????????? ???????????????");
        mDialog.show();

        try{
            //update name, keyword, open/close time- if changed
            if(!newName.equals(oldName)) childUpdates.put("/name", newName);
            if(!newKeyword.equals(oldKeyword)) childUpdates.put("/keyword",newKeyword);
            if(newOpenTime!=null&&!newOpenTime.equals(oldOpenTime)) childUpdates.put("/time_open", newOpenTime);
            if(newCloseTime!=null&&!newCloseTime.equals(oldCloseTime)) childUpdates.put("/time_close",newCloseTime);

            //new menus- if exist
            if(menu_new_list!=null && menu_new_list.size()!=0){
                for(HashMap<String, Object> h:menu_new_list){
                    childUpdates.put("/menu/"+h.get("menu_id"), h);
                }
            }

            //modify menus- if needed
            if(menu_row_list!=null && menu_row_list.size()!=0){
                for(MenuModified mm : menu_row_list){
                    if(mm.isModified){
                        childUpdates.put("/menu/"+mm.menu.getMenuId(), mm.menu.getMenuHash());
                    }
                }
            }

            //new images- if exist
            if(image_new_list != null && image_new_list.size() != 0){
                int i=0;

                for(Seller_Image h:image_new_list){
                    i++;
                    storeImage(h, i, image_new_list.size());
                }
            }

            //delete menus
            if(menu_delete_list!=null&&menu_delete_list.size()!=0){
                for(String s:menu_delete_list){
                    databaseReference.child("menu").child(s).removeValue();
                }
            }

            //delete images
            if(image_delete_list!=null&&image_delete_list.size()!=0){
                for(String s:image_delete_list){ //db??? storage?????? ??????
                    databaseReference.child("image").child(s).removeValue();
                    storage.getReferenceFromUrl("gs://getsumfoot.appspot.com").child("Seller_images/"+s).delete();
                }
            }

            if(image_new_list.size()==0) updateDB(); //storage??? ???????????? ?????? update: ?????? update ??????

        }catch (Exception e){
            Log.e("Storage 626", String.valueOf(e));
        }

    }
}