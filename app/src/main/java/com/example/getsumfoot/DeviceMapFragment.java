package com.example.getsumfoot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsumfoot.data.SellerInfo;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
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

public class DeviceMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private View root;
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
//    private OnBackPressedDispatcher onBackPressedDispatcher;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int PAGE_UP = 8;
    private static final int PAGE_LEFT = 4;
    private static final int PAGE_RIGHT = 6;
    private static final int PAGE_DOWN = 2;

    private Marker lastMarker;
    private Marker[] markerItems;

    private View viewLayer;

    private SearchView searchView;
    private Button btnZoomOut;
    private Button btnZoomIn;
    private LocationButtonView btnHomeLocation;
    //맵뷰 상단 layout

    private ConstraintLayout clMarketInfo;
    private TextView tv_market_title;
    private TextView tv_market_time_value;
    private TextView tv_market_addr_value;
    private TextView tv_menu_category_value;
    private  TextView tv_is_open_value;
    //맵뷰 하단 layout

    private Animation translateUpAim;
    private Animation translateDownAim;
    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean initMapLoad = true;
    private boolean isInfoPageOpen = false;
    private boolean isHambergerOpen = false;

    private int pageValue;

    private SlidingPageAnimationListener animationListener;

    DatabaseReference sellerRef;
    FirebaseDatabase database;
    //firebase instance

    Seller_Image sellerImage[];
    SellerInfo sellerInfo[];
    Seller_Menu sellerMenu[];
    //database 저장객체

    public DeviceMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_device_map, container, false);

        //splash 화면
        startActivity(new Intent(getActivity(), SplashActivity.class));

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        locationSource = new FusedLocationSource(getActivity(), LOCATION_PERMISSION_REQUEST_CODE);

        clMarketInfo = root.findViewById(R.id.cl_market_info);
        btnHomeLocation = root.findViewById(R.id.btn_home_location);
        btnZoomIn = root.findViewById(R.id.btn_home_zoom_in);
        btnZoomOut = root.findViewById(R.id.btn_home_zoom_out);
        //layout fb

        btnHomeLocation.setOnClickListener(this);
        btnZoomOut.setOnClickListener(this);
        btnZoomIn.setOnClickListener(this);

        sellerImage = new Seller_Image[3];
        sellerInfo = new SellerInfo[3];
        sellerMenu = new Seller_Menu[3];

        //데이터 베이스 저장 객체

        //퍼미션 확인
        if (DeviceMapFragment.checkPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                && (DeviceMapFragment.checkPermissions(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION))
                && (DeviceMapFragment.checkPermissions(getActivity(), Manifest.permission.CAMERA))) {
            //권한 있음 - 원하는 메소드 사용
            //Toast.makeText(this, "권한 설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(getActivity(), "겟썸푸트 이용을 위한 권한을 설정해주세요.", Toast.LENGTH_LONG).show();
            DeviceMapActivity.requestExternalPermissions(getActivity());
        }
        return root;
    }

    //back button handling
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isInfoPageOpen) {
                    //TODO 마커정보 필요 getMarker에서 받아와야함(배열로 많이 받아올 수 있음)
                    //TODO 우선 임시로 marker 임의설정
                    lastMarker = new Marker();
                    lastMarker.setPosition(new LatLng(37.5670135, 126.9783740));
                    //임의설정 수정필요
                    map.getOnMapClickListener().onMapClick(new PointF(10, 10), lastMarker.getPosition());
                } else {
                    //super.onBackPressed();
                    getActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
    @Override
    public void onDestroyView () { super.onDestroyView(); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_location: {
                btnHomeLocation.setMap(map);
                break;
            }
            case R.id.btn_home_zoom_in:
                btnZoomClickEvent(btnZoomIn,true);
            case R.id.btn_home_zoom_out:
                btnZoomClickEvent(btnZoomOut,false);
        }
    }

    //줌 인/아웃 이벤트 처리 메서드
    private void btnZoomClickEvent(Button button, boolean zoom) {
        if (zoom) {
            map.moveCamera(CameraUpdate.zoomIn().animate(CameraAnimation.Easing, 1500));
        } else {
            map.moveCamera(CameraUpdate.zoomOut().animate(CameraAnimation.Fly, 1500));
        }
    }

    //맵 준비
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        mapLoad();  //지도로드
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
                translateDownAim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
                translateDownAim.setAnimationListener(animationListener);
                pageValue = PAGE_DOWN;
                clMarketInfo.startAnimation(translateDownAim);

                //TODO getmarker 메서드에서 위치정보를 받아온 후 설정할 수 있음 (음식마다 마커 커스텀하려면 firebase에 마커 구분 정보필요)
              /*  lastMarker.setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                lastMarker.setWidth(70);
                lastMarker.setHeight(70);*/
                lastMarker = null;
            }
        });
    }

    //위치정보를 파이어 베이스에서 받아 마커로 받아오는 메서드
    protected void getMarker() {
        //애니메이션 준비
        translateUpAim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_up);
        clMarketInfo = root.findViewById(R.id.cl_model_info);

        TextView tvModelNum = root.findViewById(R.id.tv_market_title);
        TextView tvBatteryValue = root.findViewById(R.id.tv_market_time_value);
        TextView tvTimeValue = root.findViewById(R.id.tv_time_value);


        //TODO  glide 라이브러리로 이미지 load
        //
        //

        database = FirebaseDatabase.getInstance();
        sellerRef = database.getReference("Seller");

        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue(Object.class);
                //TODO 데이터 객체에 가공하기
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("seller data read error", error.toString());
            }
        });
    }

    private static boolean checkPermissions(Activity activity, String permission) {
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
                Toast.makeText(getActivity(), "권한 설정이 모두 완료되었습니다.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "겟썸푸트 서비스 이용을 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
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

    //클릭 시 이쁘게 넘어가는 표현(최근실습문제?) -->햄버거버튼 관련한 애니매이션 필요없음
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN: {
                    isInfoPageOpen = false;
                    btnHomeLocation.setVisibility(View.GONE);
                    btnZoomIn.setVisibility(View.GONE);
                    btnZoomOut.setVisibility(View.GONE);
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
                    clMarketInfo.setVisibility(View.GONE);
                    break;
                }
                case PAGE_UP: {
                    clMarketInfo.setVisibility(View.VISIBLE);
                    btnHomeLocation.setVisibility(View.VISIBLE);
                    btnZoomIn.setVisibility(View.VISIBLE);
                    btnZoomOut.setVisibility(View.VISIBLE);
                    btnHomeLocation.setMap(map);
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