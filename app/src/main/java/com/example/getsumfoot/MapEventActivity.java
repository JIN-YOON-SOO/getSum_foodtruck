package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.getsumfoot.data.EventMapData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class MapEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button button;
    private MapView mapView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<String> arrayList;
    private FragmentActivity fragmentActivity;
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers; //마커정보저장
    private NaverMap naverMap;
    private Double latitude, longitude;
    private String roadAddres;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        arrayList = new ArrayList<>();

        mapView = findViewById(R.id.event_map);
        mapView.getMapAsync(this::onMapReady);

        button = findViewById(R.id.event_button_map);
        button.setOnClickListener(new Button.OnClickListener(){

           @Override
           public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventListActivity.class); //이벤트 목록 페이지로 넘어감
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResult){
        if(locationSource.onRequestPermissionsResult(requestCode,permissions,grantResult)){
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); //나침반
        uiSettings.setScaleBarEnabled(true); // 축척바
        uiSettings.setZoomControlEnabled(true); //줌
        uiSettings.setLocationButtonEnabled(true); // 현위치

        databaseReference = FirebaseDatabase.getInstance().getReference("seoulEvent");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 데이터 리스트 추출
                    latitude = (Double) snapshot.child("위도").getValue();
                    longitude = (Double) snapshot.child("경도").getValue();
                    roadAddres = ((String) snapshot.child("소재지도로명주소").getValue());
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(latitude,longitude));
                    marker.setIcon(MarkerIcons.BLACK);
                    marker.setIconTintColor(Color.rgb(241, 196, 15));
                    marker.setCaptionText(roadAddres);
                    marker.setCaptionColor(Color.rgb(84, 84, 84));
                    marker.setMap(naverMap);
                    Log.d("Lat",latitude+","+longitude);
                }
            } // 파이어베이스 데이터베이스의 데이터 받아오는 곳

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapEventFragment", String.valueOf(databaseError.toException())); // 에러문 출력
            } //데이터 베이스 가져오다가 에러 발생시 에러처리
        });
    }

}
