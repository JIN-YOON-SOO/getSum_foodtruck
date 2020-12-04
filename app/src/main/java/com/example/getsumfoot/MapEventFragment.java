package com.example.getsumfoot;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;

import com.example.getsumfoot.data.EventData;
import com.example.getsumfoot.data.EventMapData;
import com.example.getsumfoot.data.ReviewData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Vector;

 class MapEventFragment extends Fragment implements OnMapReadyCallback{
     private MapView mapView;
     private FirebaseDatabase database;
     private DatabaseReference databaseReference;
     private ArrayList<EventMapData> arrayList;
     private FragmentActivity fragmentActivity;
     private Vector<LatLng> markersPosition;
     private Vector<Marker> activeMarkers; //마커정보저장
     private NaverMap naverMap;
     private Button button;
     private Double latitude, longitude;
     private String roadAddres;
     private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
     private FusedLocationSource locationSource;


     public MapEventFragment() {
         // Required empty public constructor
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
         // Inflate the layout for this fragment

         View root = inflater.inflate(R.layout.fragment_map_event, container, false);
         mapView = root.findViewById(R.id.event_map_view);
         mapView.getMapAsync(this::onMapReady);

         locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

         button = root.findViewById(R.id.event_button_map);
         button.setOnClickListener(new Button.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(fragmentActivity.getApplicationContext(), EventListActivity.class); //이벤트 목록 페이지로 넘어감
                 startActivity(intent);
             }
         });

         //지도 객체로 띄우는 방법
         //FragmentManager fm = fragmentActivity.getSupportFragmentManager();
         // MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.event_map_view);
         //if (mapFragment == null) {
         //   mapFragment = MapFragment.newInstance();
         //    fm.beginTransaction().add(R.id.event_map_view, mapFragment).commit();
         //}
         //mapFragment.getMapAsync(this);

         return root;
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
