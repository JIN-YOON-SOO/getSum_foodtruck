package com.example.getsumfoot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.getsumfoot.data.GPSTracker;
import com.example.getsumfoot.data.SellerInfo;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MyPageSellerFragment extends Fragment {
    private static final String TAG = "MyPageSellerFragment";
    private Button btn_open;
    private ImageView iv_img_1, iv_img_2, iv_img_3;
    private TextView tv_user_name, tv_seller_address, tv_seller_name, tv_seller_hours, tv_seller_menu, tv_seller_keyword;
    private LinearLayout ll_info;
    private FirebaseAuth firebaseAuth;
    //private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private GPSTracker gpsTracker;
    private FirebaseStorage storage;
    //private String uid;
    private String current_user;
    private static final int MAX_PICTURES = 3;
    private View[] imageViewList;

    private boolean isOpen;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private SellerInfo sellerInfo;

    public MyPageSellerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_page_seller, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Seller").child(current_user);
        storage = FirebaseStorage.getInstance();

        btn_open = root.findViewById(R.id.btn_open);
        ll_info = root.findViewById(R.id.ll_info);
        iv_img_1 = root.findViewById(R.id.iv_img_1);
        iv_img_2 = root.findViewById(R.id.iv_img_2);
        iv_img_3 = root.findViewById(R.id.iv_img_3);
        tv_user_name = root.findViewById(R.id.tv_user_name);
        tv_seller_keyword = root.findViewById(R.id.tv_seller_keyword);
        tv_seller_address = root.findViewById(R.id.tv_seller_address);
        tv_seller_name = root.findViewById(R.id.tv_seller_name);
        tv_seller_hours = root.findViewById(R.id.tv_seller_hours);
        tv_seller_menu = root.findViewById(R.id.tv_seller_menu);

        imageViewList = new View[]{iv_img_1, iv_img_2, iv_img_3};
        //????????? ?????? ?????? class
        sellerInfo = new SellerInfo();

        btn_open.setOnClickListener(v -> setOpenOrClose());
        ll_info.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPageSellerModifyActivity.class);
            intent.putExtra("sellerInfo", sellerInfo);
            startActivity(intent);

        });
        tv_seller_address.setOnClickListener(v -> setAddress());

        ValueEventListener eventListener = new ValueEventListener() { //db?????? data ????????????

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sellerInfo.setName(snapshot.child("name").getValue().toString());
                    sellerInfo.setKeyword(snapshot.child("keyword").getValue().toString());
                    sellerInfo.setTime_open(snapshot.child("time_open").getValue().toString());
                    sellerInfo.setTime_close(snapshot.child("time_close").getValue().toString());
                    sellerInfo.setLat((double)snapshot.child("Lat").getValue());
                    sellerInfo.setLng((double)snapshot.child("Lng").getValue());
                    sellerInfo.setAddress(snapshot.child("address").getValue().toString());

                    sellerInfo.setIs_open(Boolean.parseBoolean(snapshot.child("is_open").getValue().toString()));

                    for(DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()){
                        Seller_Menu sellerMenu = dataSnapshot.getValue(Seller_Menu.class);
                        sellerInfo.setSellerMenu(sellerMenu);
                    }

                    int countPictures = 1;
                    for(DataSnapshot dataSnapshot : snapshot.child("image").getChildren()){
                        Log.e("count: ", String.valueOf(countPictures));
                        if(countPictures > MAX_PICTURES) break; //3????????? ??????
                        Seller_Image sellerImage = dataSnapshot.getValue(Seller_Image.class);
                        sellerInfo.setSellerImage(sellerImage);
                        countPictures++;
                    }
                    setComp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error.toException()));
            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        gpsTracker = new GPSTracker(getActivity());

        //TODO firebase push ALARM service
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        //?????? ??? ?????? ?????? ???
                        if (task.isSuccessful() == false) {
                            Log.e("?????? id?????? ??????", "send token error", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();
                        Log.e("FCM LOG","fcm ?????? ?????????"+token);
                    }
                });

        return root;
    }

    @SuppressLint("ResourceAsColor")
    public void setComp(){
        String time = sellerInfo.getTime_open()+" ~ "+sellerInfo.getTime_close();
        String name = sellerInfo.getName();

        tv_user_name.setText(name + "??? ???????????????");
        tv_seller_name.setText(name);
        tv_seller_keyword.setText(sellerInfo.getKeyword());
        tv_seller_hours.setText(time);
        setAddress();

        isOpen = sellerInfo.isIs_open();
        if (isOpen) {
            btn_open.setBackgroundResource(R.color.sub_colorGray);
            btn_open.setText("????????????");
        }
        btn_open.setVisibility(View.VISIBLE);

        int i = 0;
        for(Seller_Image img : sellerInfo.getSellerImage()){
            Uri imageUri = Uri.parse(img.getImageUri());
            try{
                Glide.with(this).load(imageUri).into((ImageView) imageViewList[i]);
                imageViewList[i].setVisibility(View.VISIBLE);
            }catch (Exception e){
                break;
            }
            i++;
        }

        i = 0;
        StringBuilder menus = new StringBuilder();
        for(Seller_Menu menu : sellerInfo.getSellerMenu()){
            if(i>2){
                menus.append(" ...");
                break;
            }
            menus.append(menu.getMenuName()).append(" (").append(menu.getMenuPrice()).append("???) ");
            i++;
        }
        tv_seller_menu.setText(menus);
    }

    public void setAddress(){
        double longitude = gpsTracker.getLongitude();
        double latitude = gpsTracker.getLatitude();
        String address = gpsTracker.getAddress();

        if(latitude==0||longitude==0){
            Toast.makeText(getActivity(), "????????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
            tv_seller_address.setText(sellerInfo.getAddress());
            return;
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Lng",longitude);
        childUpdates.put("/Lat",latitude);
        childUpdates.put("/address", address);

        databaseReference.updateChildren(childUpdates);
        tv_seller_address.setText("????????????: "+address);
        sellerInfo.setAddress(address);
    }

    @SuppressLint("ResourceAsColor")
    public void setOpenOrClose(){ //?????? ???????????? => ?????????? ????????????: ????????????
        if(isOpen){
            databaseReference.child("is_open").setValue(false);
            btn_open.setBackgroundResource(R.color.colorPrimary);
            btn_open.setText("????????????");
        }else{
            databaseReference.child("is_open").setValue(true);
            btn_open.setBackgroundResource(R.color.sub_colorGray);
            btn_open.setText("????????????");
            setAddress();
        }
        isOpen = !isOpen;
        sellerInfo.setIs_open(isOpen);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????
            boolean check_result = true;

            // ?????? ???????????? ????????????????
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (!check_result) { //????????? ????????? ??? ??????
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkRunTimePermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return;
        } else { //?????? ???????????? ?????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {//???????????? ???????????? ??????
                Toast.makeText(getActivity(), "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("??????", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) { //???????????? GPS ?????? ???????????? ??????
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}