package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DeviceMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

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

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int PAGE_UP = 8;
    private static final int PAGE_LEFT = 4;
    private static final int PAGE_RIGHT = 6;
    private static final int PAGE_DOWN = 2;

    private Marker lastMarker;
    private Marker[] markerItems;

    private Button btnHomeLend;
    private Button btnInfoLend;
    private Button btnHomeZoomIn;
    private Button btnHomeZoomOut;
    private Button btnInfoZoomIn;
    private Button btnInfoZoomOut;

    private ImageView btnHamberger; //보배 햄버거바 객체

    private View viewLayer;

    private ConstraintLayout clModelInfo;

    private Animation translateUpAim;
    private Animation translateDownAim;
    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean initMapLoad = true;
    private boolean isInfoPageOpen = false;
    private boolean isHambergerOpen = false;

    private LocationButtonView btnHomeLocation;
    private LocationButtonView btnInfoLocation;

    private int pageValue;
    private String modelName;

    private SlidingPageAnimationListener animationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_map);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

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
    /////////////////////////////////////////////////////////////////////////////////////////
    //TODO

    @Override
    public void onBackPressed() {
        if (isHambergerOpen) {
            viewLayer.performClick();
            return;
        } else if (isInfoPageOpen) {
            //TODO 마커정보 필요 getMarker에서 받아와야함(배열로 많이 받아올 수 있음)
            //TODO 우선 임시로 marker 임의설정
            lastMarker = new Marker();
            lastMarker.setPosition(new LatLng(37.5670135, 126.9783740));
            //임의설정 수정필요
            map.getOnMapClickListener().onMapClick(new PointF(10, 10), lastMarker.getPosition());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                Toast.makeText(this, "겟썸푸트 서비스 이용을 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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




    @Override
    public void onClick(View view) {

    }

    //맵 준비
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        mapLoad();  //지도로드
        //setHamberger();   //햄버거 바 설정
        getMarker();    //마커표시
        makeCircle();   //어플 사용가능 영역 설정
    }

    //  맵 얻어오기
    private void mapLoad() {
        map.setLocationSource(locationSource);

        map.addOnLocationChangeListener(location -> {
            if (initMapLoad) {
                map.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(location.getLatitude(), location.getLongitude()), 14)
                        .animate(CameraAnimation.Linear, 3000));
                map.setLocationTrackingMode(LocationTrackingMode.Follow);
                initMapLoad = false;
            }
        });

        map.addOnOptionChangeListener(() -> {
            locationSource.setCompassEnabled(true);
        });
        map.setLocationTrackingMode(LocationTrackingMode.Follow);

        map.setOnMapClickListener((point, coord) -> {
            //애니메이션
            if (isInfoPageOpen) {
                //애니메이션 준비
                translateDownAim = AnimationUtils.loadAnimation(this, R.anim.translate_down);
                translateDownAim.setAnimationListener(animationListener);
                pageValue = PAGE_DOWN;
                clModelInfo.startAnimation(translateDownAim);

                //TODO getmarker 메서드에서 위치정보를 받아온 후 설정할 수 있음 (음식마다 마커 커스텀하려면 firebase에 마커 구분 정보필요)
              /*  lastMarker.setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                lastMarker.setWidth(70);
                lastMarker.setHeight(70);*/
                lastMarker = null;
            }
        });
    }
        //줌 인/아웃 이벤트 처리 메서드
        private void btnZoomClickEvent(Button button, boolean zoom) {
            if (zoom) {
                map.moveCamera(CameraUpdate.zoomIn().animate(CameraAnimation.Easing, 1500));
            } else {
                map.moveCamera(CameraUpdate.zoomOut().animate(CameraAnimation.Fly, 1500));
            }
        }

        //위치정보를 파이어 베이스에서 받아 마커로 받아오는 메서드
        protected void getMarker() {
            //애니메이션 준비
            translateUpAim = AnimationUtils.loadAnimation(this, R.anim.translate_up);
            clModelInfo = findViewById(R.id.cl_model_info);

            TextView tvModelNum = findViewById(R.id.tv_model_num);
            TextView tvBatteryValue = findViewById(R.id.tv_battery_value);
            TextView tvTimeValue = findViewById(R.id.tv_time_value);

           /* Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GetsumfootService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();*/
                //이미지 로드 통신용으로 사용

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference sellerRef = database.getReference("Seller");

            sellerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("seller data read error", error.toString());
                }
            });


        }

        //이용가능한 영역 원으로 표시
    protected void makeCircle() {
        CircleOverlay circleOverlay = new CircleOverlay();
        circleOverlay.setCenter(new LatLng(37.4963111, 126.9574596));
        circleOverlay.setRadius(2000);
        circleOverlay.setColor(Color.parseColor("#196ED3EF"));
        circleOverlay.setOutlineColor(Color.parseColor("#FF4EBFDE"));
        circleOverlay.setOutlineWidth(3);
        circleOverlay.setMap(map);
    }

    //클릭 시 이쁘게 넘어가는 표현(최근실습문제?)
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN: {
                    isInfoPageOpen = false;
                    btnInfoLocation.setVisibility(View.GONE);
                    btnInfoZoomIn.setVisibility(View.GONE);
                    btnInfoZoomOut.setVisibility(View.GONE);
                    break;
                }
                case PAGE_UP: {
                    isInfoPageOpen = true;
                    break;
                }
                case PAGE_LEFT: {
                   // clHamberger.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.GONE);
                    //clToolbar.setVisibility(View.VISIBLE);
                    isHambergerOpen = false;
                    break;
                }
                case PAGE_RIGHT: {
                    //clToolbar.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.VISIBLE);
                    isHambergerOpen = true;
                }
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN: {
                    clModelInfo.setVisibility(View.GONE);
                    break;
                }
                case PAGE_UP: {
                    clModelInfo.setVisibility(View.VISIBLE);
                    btnInfoLocation.setVisibility(View.VISIBLE);
                    btnInfoZoomIn.setVisibility(View.VISIBLE);
                    btnInfoZoomOut.setVisibility(View.VISIBLE);
                    btnInfoLocation.setMap(map);
                    break;
                }
                case PAGE_LEFT: {
                    //clHamberger.setVisibility(View.GONE);
                    break;
                }
                case PAGE_RIGHT: {
                    //clHamberger.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


}