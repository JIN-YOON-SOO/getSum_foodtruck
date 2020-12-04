package com.example.getsumfoot;

import android.Manifest;
import android.content.Intent;
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
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Vector;

 class MapEventFragment extends Fragment {
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


     public MapEventFragment() {
         // Required empty public constructor
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
         // Inflate the layout for this fragment

         View root = inflater.inflate(R.layout.fragment_map_event, container, false);
         mapView = root.findViewById(R.id.event_map_view);
         mapView.onCreate(savedInstanceState);

         //지도 객체로 띄우는 방법
         //FragmentManager fm = fragmentActivity.getSupportFragmentManager();
         // MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.event_map_view);
         //if (mapFragment == null) {
         //   mapFragment = MapFragment.newInstance();
         //    fm.beginTransaction().add(R.id.event_map_view, mapFragment).commit();
         //}

         //mapFragment.getMapAsync(this);

         button = root.findViewById(R.id.event_button_map);
         button.setOnClickListener(new Button.OnClickListener() {

             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(fragmentActivity.getApplicationContext(), EventListActivity.class); //이벤트 목록 페이지로 넘어감
                 startActivity(intent);
             }
         });

         //UiSettings uiSettings = naverMap.getUiSettings();
         //uiSettings.setCompassEnabled(true); //나침반
         //uiSettings.setScaleBarEnabled(true); // 축척바
         //uiSettings.setZoomControlEnabled(true); //줌
         //uiSettings.setLocationButtonEnabled(true); // 현위치

         databaseReference = FirebaseDatabase.getInstance().getReference("seoulEvent");

         databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 데이터 리스트 추출
                     if(snapshot.hasChild("위도") & snapshot.hasChild("경도")){
                         latitude = (Double) snapshot.child("위도").getValue();
                         longitude = (Double) snapshot.child("경도").getValue();
                         EventMapData eventMapData = new EventMapData(latitude, longitude);
                         arrayList = new ArrayList<>();
                         arrayList.add(eventMapData);
                     }
                 }

             } // 파이어베이스 데이터베이스의 데이터 받아오는 곳

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 Log.e("MapEventFragment", String.valueOf(databaseError.toException())); // 에러문 출력
             } //데이터 베이스 가져오다가 에러 발생시 에러처리
         });

         for (int i = 0; i < arrayList.size(); i++) {
             Marker marker = new Marker();
             marker.setPosition(new LatLng(arrayList.get(i).get_latitude(), arrayList.get(i).get_longitude()));
             marker.setMap(naverMap);
         }

         return root;
     }
 }
