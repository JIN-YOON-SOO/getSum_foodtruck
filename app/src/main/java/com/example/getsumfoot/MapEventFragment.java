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

 class MapEventFragment extends Fragment implements OnMapReadyCallback {
     private MapView mapView;
     private FirebaseDatabase database;
     private DatabaseReference databaseReference;
     private ArrayList<EventMapData> arrayList;
     private FragmentActivity fragmentActivity;
     private Vector<LatLng> markersPosition;
     private Vector<Marker> activeMarkers; //마커정보저장
     private NaverMap mNaverMap;
     private Button button;

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
         mapView.getMapAsync(this);

         //지도 객체로 띄우는 방법
         //FragmentManager fm = fragmentActivity.getSupportFragmentManager();
         // MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.event_map_view);
         //if (mapFragment == null) {
         //   mapFragment = MapFragment.newInstance();
         //    fm.beginTransaction().add(R.id.event_map_view, mapFragment).commit();
         //}

         //mapFragment.getMapAsync(this);

         button = root.findViewById(R.id.event_button_map);
         button.setOnClickListener(new Button.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(fragmentActivity.getApplicationContext(), EventListActivity.class); //이벤트 목록 페이지로 넘어감
                 startActivity(intent);
             }
         });
         return root;
     }

     @UiThread
     @Override
     public void onMapReady(@NonNull final NaverMap naverMap) {
         // 초기 위치 설정(숭실 대학교)
         LatLng initialPosition = new LatLng(37.495864723591716, 126.9577520481971);
         CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
         naverMap.moveCamera(cameraUpdate);

         UiSettings uiSettings = mNaverMap.getUiSettings();
         uiSettings.setCompassEnabled(true); //나침반
         uiSettings.setScaleBarEnabled(true); // 축척바
         uiSettings.setZoomControlEnabled(true); //줌
         uiSettings.setLocationButtonEnabled(true); // 현위치

         database = FirebaseDatabase.getInstance(); //파이어베이스 연동

         database.getReference("seoulEvent").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 arrayList.clear(); //기존 배열리스트 초기화
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 데이터 리스트 추출
                     EventMapData eventMapData = snapshot.getValue(EventMapData.class);
                     arrayList.add(eventMapData);
                 }
             } // 파이어베이스 데이터베이스의 데이터 받아오는 곳

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 Log.e("MapEventFragment", String.valueOf(databaseError.toException())); // 에러문 출력
             } //데이터 베이스 가져오다가 에러 발생시 에러처리
         });

         // 마커들 위치
         markersPosition = new Vector<LatLng>();
         for (int x = 0; x < 45; x++) {
             markersPosition.add(new LatLng(
                     arrayList.get(x).get_latitude(),
                     //Log.d("위도",toString(arrayList.get(x).get_latitude());
                     arrayList.get(x).get_longitude()
             ));

         }

         // 이동 호출 되는 이벤트
         naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
             @Override
             public void onCameraChange(int reason, boolean animated) {
                 freeActiveMarkers();
                 LatLng currentPosition = getCurrentPosition(naverMap);
                 for (LatLng markerPosition : markersPosition) {
                     Marker marker = new Marker();
                     marker.setPosition(markerPosition);
                     marker.setMap(naverMap);
                     activeMarkers.add(marker);
                 }
             }
         });
     }

     // 현재 보고있는 위치
     public LatLng getCurrentPosition(NaverMap naverMap) {
         CameraPosition cameraPosition = naverMap.getCameraPosition();
         LatLng currentPosition = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
         return currentPosition;
     }

     // 지도상에 표시되고있는 마커들 지도에서 삭제
     private void freeActiveMarkers() {
         if (activeMarkers == null) {
             activeMarkers = new Vector<Marker>();
         }
         for (Marker activeMarker : activeMarkers) {
             activeMarker.setMap(null);
         }
         activeMarkers = new Vector<Marker>();
     }

 }