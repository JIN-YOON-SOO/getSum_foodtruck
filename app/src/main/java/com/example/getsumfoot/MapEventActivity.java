package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.getsumfoot.data.EventMapData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class MapEventActivity extends AppCompatActivity {
    private Button button;
    private MapView mapView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<EventMapData> arrayList;
    private FragmentActivity fragmentActivity;
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers; //마커정보저장
    private NaverMap naverMap;
    private Double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);

        arrayList = new ArrayList<>();

        mapView = findViewById(R.id.event_map_view);

        button = findViewById(R.id.event_button_map);
        button.setOnClickListener(new Button.OnClickListener(){

           @Override
           public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventListActivity.class); //이벤트 목록 페이지로 넘어감
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("seoulEvent");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 데이터 리스트 추출
                    if (snapshot.hasChild("위도") & snapshot.hasChild("경도")) {
                        latitude = (Double) snapshot.child("위도").getValue();
                        longitude = (Double) snapshot.child("경도").getValue();
                        EventMapData eventMapData = new EventMapData(latitude, longitude);
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
    }

}
