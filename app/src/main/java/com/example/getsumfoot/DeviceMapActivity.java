package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.util.FusedLocationSource;


//TODO 종훈 브랜치
public class DeviceMapActivity extends AppCompatActivity {

    //퍼미션 리스트
    private static String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };
    private static final int request_code = 0;
    private FusedLocationSource locationSource;
    private MapView mapView;
    private NaverMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_map);

        //퍼미션 확인
        if (DeviceMapActivity.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && (DeviceMapActivity.checkPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                && (DeviceMapActivity.checkPermissions(this, Manifest.permission.CAMERA))) {
            //권한 있음 - 원하는 메소드 사용
            //Toast.makeText(this, "권한 설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "겟썸푸트 이용을 위한 권한을 설정해주세요.", Toast.LENGTH_LONG).show();
            DeviceMapActivity.requestExternalPermissions(this);
        }

    }

    private static boolean checkPermissions(DeviceMapActivity activity, String permission) {
        int permissionResult = ActivityCompat.checkSelfPermission(activity, permission);
        if (permissionResult == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) { //if(requestCode == BreathTestingActivity.request_code)
            if (DeviceMapActivity.verifyPermission(grantResults)) {
                //요청한 권한 얻음, 원하는 메소드 사용
                Toast.makeText(this, "권한 설정이 모두 완료되었습니다.", Toast.LENGTH_LONG).show();
                if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    if (!locationSource.isActivated()) {
                        map.setLocationTrackingMode(LocationTrackingMode.None);
                    }
                    map.setLocationSource(locationSource);
                    map.setLocationTrackingMode(LocationTrackingMode.Follow);
                    return;
                }
            } else {
                //showRequestAgainDialog();
                Toast.makeText(this, "불고타 서비스 이용을 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRequestAgainDialog() {
    }

    public static void requestExternalPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, permission_list, request_code);
    }

    public static boolean verifyPermission(int[] grantresults) { //하나라도 허용 안되어있으면 flase리턴
        if (grantresults.length < 1) {
            return false;
        }
        for (int result : grantresults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}