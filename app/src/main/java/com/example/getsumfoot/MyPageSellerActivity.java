package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class MyPageSellerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyPageSellerActivity";
    private Button btn_open;
    private ImageView iv_btn_setting, iv_img_1, iv_img_2, iv_img_3;
    private TextView tv_user_name, tv_seller_address, tv_seller_name, tv_seller_hours, tv_seller_menu;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private LocationManager locationManager;
    private GPSTracker gpsTracker;
    //private FirebaseStorage storage;
    private String uid;
    private boolean isOpen;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_seller);

        firebaseAuth = FirebaseAuth.getInstance();
//        uid = firebaseAuth.getCurrentUser().getUid();
        uid = "Fqm1PUy6hjXACFNOd02zjbnJP152";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Seller").child(uid);
        //storage = FirebaseStorage.getInstance();

        btn_open = findViewById(R.id.btn_open);
        iv_btn_setting = findViewById(R.id.iv_btn_setting);
        iv_img_1 = findViewById(R.id.iv_img_1);
        iv_img_2 = findViewById(R.id.iv_img_2);
        iv_img_3 = findViewById(R.id.iv_img_3);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_seller_address = findViewById(R.id.tv_seller_address);
        tv_seller_name = findViewById(R.id.tv_seller_name);
        tv_seller_hours = findViewById(R.id.tv_seller_hours);
        tv_seller_menu = findViewById(R.id.tv_seller_menu);

        btn_open.setOnClickListener(this);
        iv_btn_setting.setOnClickListener(this);
        tv_seller_address.setOnClickListener(this);

        ValueEventListener eventListener = new ValueEventListener() { //db에서 data 받아오기
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isOpen = (boolean) snapshot.child("is_open").getValue();
                if (isOpen) {
                    //databaseReference.child("is_open").setValue(true);
                    btn_open.setBackgroundColor(R.color.sub_colorGray);
                    btn_open.setText("영업종료");
                }
                btn_open.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error.toException()));
            }
        };
        databaseReference.addValueEventListener(eventListener);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        gpsTracker = new GPSTracker(this);
        setAddress();
    }

    @Override
    public void onClick(View v) {
        if(v == btn_open){
            setOpenOrClose();
        }else if(v == iv_btn_setting){
            startActivity(new Intent(this, MyPageSellerModifyActivity.class));
        }else if(v == tv_seller_address){
            setAddress();
        }
    }

    public void setAddress(){
        double longitude = gpsTracker.getLongitude();
        double latitude = gpsTracker.getLatitude();
        String address = gpsTracker.getAddress();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Lng",longitude);
        childUpdates.put("/Lat",latitude);
        childUpdates.put("/address", address);

        databaseReference.updateChildren(childUpdates);
        tv_seller_address.setText(address);
    }

    @SuppressLint("ResourceAsColor")
    public void setOpenOrClose(){ //버튼 눌렀을때 => 영업중? 영업종료: 영업시작
        if(isOpen){
            databaseReference.child("is_open").setValue(false);
            btn_open.setBackgroundColor(R.color.colorPrimary);
            btn_open.setText("영업시작");
        }else{
            databaseReference.child("is_open").setValue(true);
            btn_open.setBackgroundColor(R.color.sub_colorGray);
            btn_open.setText("영업종료");
            setAddress();
        }
        isOpen = !isOpen;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는가?
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (!check_result) { //위치를 가져올 수 없다
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkRunTimePermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return;
        } else { //위치 퍼미션이 없다면
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {//사용자가 거부했던 경우
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) { //사용자가 GPS 활성 시켰는지 검사
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
