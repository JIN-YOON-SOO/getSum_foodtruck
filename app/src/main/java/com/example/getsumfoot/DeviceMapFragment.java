package com.example.getsumfoot;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsumfoot.data.SellerInfo;
import com.example.getsumfoot.data.Seller_Image;
import com.example.getsumfoot.data.Seller_Menu;
import com.google.firebase.auth.FirebaseAuth;
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
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.util.HashMap;
import java.util.Map;

public class DeviceMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private View root;
    //????????? ?????????
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

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

   // private Button btnZoomOut;
   // private Button btnZoomIn;
    private LocationButtonView btnHomeLocation;
    //?????? ?????? layout

    private ConstraintLayout clMarketInfo;
    private TextView tv_market_title;
    private TextView tv_market_time_value;
    private TextView tv_market_addr_value;
    private TextView tv_menu_category_value;
    private  TextView tv_is_open_value;
    private ImageButton btn_like;
    private Button btn_order;
    //?????? ?????? layout

    private Animation translateUpAim;
    private Animation translateDownAim;
    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean initMapLoad = true;
    private boolean isInfoPageOpen = false;
    private boolean isHambergerOpen = false;

    private int pageValue;
    static int sel_marker =-1;   //  0 ice cream    1.boong_a_ppang      2.pizza

    private SlidingPageAnimationListener animationListener;

    FirebaseDatabase database;
    DatabaseReference sellerRef[];  //??????
    DatabaseReference ref;  //????????????
    //firebase instance
    SellerInfo sellerInfo[];
    //database ????????????
    Seller_Menu seller_menu[];

    int like_1 = -1;
    int like_2 = -1;
    int like_3 = -1;


    public DeviceMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_device_map, container, false);

        //splash ??????
        startActivity(new Intent(getActivity(), SplashActivity.class));
        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        locationSource = new FusedLocationSource(getActivity(), LOCATION_PERMISSION_REQUEST_CODE);

        clMarketInfo = root.findViewById(R.id.cl_market_info);
        btnHomeLocation = root.findViewById(R.id.btn_home_location);
        btnHomeLocation.setOnClickListener(this);

        tv_market_title = root.findViewById(R.id.tv_market_title); //????????????
        tv_market_time_value = root.findViewById(R.id.tv_market_time_value);
        tv_market_addr_value = root.findViewById(R.id.tv_market_addr_value);
        tv_menu_category_value = root.findViewById(R.id.tv_menu_category_value);
        tv_is_open_value = root.findViewById(R.id.tv_is_open_value);

        btn_order = root.findViewById(R.id.btn_order);  //???????????? ??????
        btn_like = root.findViewById(R.id.btn_like);    //???????????? ??????

        btn_order.setOnClickListener(this);
        btn_like.setOnClickListener(this);

        sellerRef = new DatabaseReference[3];   //ref ???????????????
        seller_menu = new Seller_Menu[3];
        //????????? ????????? ?????? ??????
        return root;
    }

    //back button handling
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markerItems = new Marker[3];
        for(int i=0; i<3; i++)
            markerItems[i] = new Marker();

        sellerInfo = new SellerInfo[3]; //ref ?????? ??????
        for(int i=0; i<3; i++){
            sellerInfo[i] = new SellerInfo();
            //seller_menu[i] = new Seller_Menu();
        }

        //DB ?????? ?????? ?????? ??????
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isInfoPageOpen) {
                    lastMarker = new Marker();
                    lastMarker.setPosition(new LatLng(37.5670135, 126.9783740));
                    //???????????? ????????????
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



    //TODO SearchView ?????? ????????? ?????? -----> ?????? ??????????????? ????????? ??????????????? ?????????????????? ??????....
    //TODO ??????????????? ????????????????????? ?????????????????????.... + res/menu/search???  ????????? ??????
    //
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.menu_main_search);
        SearchView sv = (SearchView) menuItem.getActionView();
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (sv != null) {
            sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener(){

                @Override
                public boolean onQueryTextSubmit(String s) {
                    if(s==sellerInfo[0].getKeyword()) {
                        markerItems[0].performClick();
                    }
                    else if(s==sellerInfo[1].getKeyword()){
                        markerItems[1].performClick();
                    }
                    else if(s==sellerInfo[2].getKeyword()){
                        markerItems[2].performClick();
                    }
                    else
                        Toast.makeText(getContext(), "?????? ????????? ???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();

                    //serachView??? ????????? ???????????? keyword??? ????????? ????????? ??? ????????? select marker??? ??????
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.e("??","???");
                    return false;
                }
            };
            sv.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curld = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        switch (curld){
            case R.id.menu_main_search:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
    //TODO SERACHVIEW ITEM

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_location: {
                btnHomeLocation.setMap(map);
                break;
            }
          /*  case R.id.btn_home_zoom_in:
                btnZoomClickEvent(btnZoomIn,true);
                break;
            case R.id.btn_home_zoom_out:
                btnZoomClickEvent(btnZoomOut,false);
                break;*/
            case R.id.btn_order : {
                    Intent intent = new Intent(getActivity(),MenuPopup.class);
                    switch (sel_marker){
                        case 0 :
                            intent.putExtra("sellerInfo",sellerInfo[0]);
                            intent.putExtra("menuInfo",seller_menu[0]);
                            break;
                        case 1 :
                            intent.putExtra("sellerInfo",sellerInfo[1]);
                            intent.putExtra("menuInfo",seller_menu[1]);
                            break;
                        case 2 :
                            intent.putExtra("sellerInfo",sellerInfo[2]);
                            intent.putExtra("menuInfo",seller_menu[2]);
                    }
                    startActivity(intent);
            }
            case R.id.btn_like : {

                String like_uids[];
                like_uids = new String[2];
                DatabaseReference ref_uid1 = FirebaseDatabase.getInstance().getReference("Customer").child(BaseActivity.current_user).child("likes").child("seller1");
                DatabaseReference ref_uid2 = FirebaseDatabase.getInstance().getReference("Customer").child(BaseActivity.current_user).child("likes").child("seller2");

                like_uids[0] = ref_uid1.toString();
                like_uids[1] = ref_uid2.toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer").child(BaseActivity.current_user).child("likes");
                String likes_id = String.valueOf(System.currentTimeMillis());
                Map<String, Object> updates = new HashMap<>();

                switch (sel_marker){
                    case 0 :
                        String compare = "DxIVq5n2nGdebKShVNI7ndGX5PP2";
                        for(int i=0; i<2; i++)
                            if(compare.equals(like_uids[i])==true && like_1 ==1)
                            {
                                like_1 = -1;
                                btn_like.setImageResource(R.drawable.btn_unlike);
                            }
                            else{
                                like_2 = 1;
                                btn_like.setImageResource(R.drawable.btn_like);
                            }
                        updates.put(likes_id,compare);
                        ref.updateChildren(updates);
                        break;
                    case 1 :
                        compare = "SayMp3MfplTazcNnXf5ung4Fs0J3";
                        for(int i=0; i<2; i++)
                            if(compare.equals(like_uids[i])==true && like_1 ==1)
                            {
                                like_2 = -1;
                                btn_like.setImageResource(R.drawable.btn_unlike);
                            }
                            else{
                                like_2 = 1;
                                btn_like.setImageResource(R.drawable.btn_like);
                            }
                        updates.put(likes_id, compare);
                        ref.updateChildren(updates);
                        break;
                    case 2 :
                        compare = "xbd8Dlm2WNXkAGegT8FuhzMOSX53";
                        for(int i=0; i<2; i++)
                            if(compare.equals(like_uids[i])==true && like_1 ==1)
                            {
                                like_3 = -1;
                                btn_like.setImageResource(R.drawable.btn_unlike);
                            }
                            else if(like_2==-1){
                                like_3 = 1;
                                btn_like.setImageResource(R.drawable.btn_like);
                            }
                            else{
                                like_3 = -1;
                                btn_like.setImageResource(R.drawable.btn_unlike);
                            }
                        updates.put(likes_id, compare);
                        ref.updateChildren(updates);
                        break;
                }
            }
        }
    }

    //??? ???/?????? ????????? ?????? ?????????
    private void btnZoomClickEvent(Button button, boolean zoom) {
        if (zoom) {
            map.moveCamera(CameraUpdate.zoomIn().animate(CameraAnimation.Easing, 1500));
        } else {
            map.moveCamera(CameraUpdate.zoomOut().animate(CameraAnimation.Fly, 1500));
        }
    }

    //??? ??????
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        mapLoad();  //????????????
        getMarker();    //????????????
        makeCircle();   //?????? ???????????? ?????? ??????
        clickMarker();
    }
    private void clickMarker(){
        markerItems[0].setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                markerItems[0].setWidth(170);
                markerItems[0].setHeight(170);
                lastMarker = markerItems[0];
                tv_market_title.setText(sellerInfo[0].getName());
                tv_market_time_value.setText(sellerInfo[0].getTime_open() + "-" + sellerInfo[0].getTime_close());
                tv_market_addr_value.setText(sellerInfo[0].getAddress());
                tv_menu_category_value.setText(sellerInfo[0].getKeyword());
                tv_is_open_value.setText(sellerInfo[0].getCheckOpen());

                //??????????????? ??????
                pageValue = PAGE_UP;
                translateUpAim.setAnimationListener(animationListener);
                clMarketInfo.setVisibility(View.VISIBLE);
                clMarketInfo.startAnimation(translateUpAim);

                sel_marker =0;
                return true;
            }
        });

        markerItems[1].setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                markerItems[1].setWidth(170);
                markerItems[1].setHeight(170);
                lastMarker = markerItems[1];
                tv_market_title.setText(sellerInfo[1].getName());
                tv_market_time_value.setText(sellerInfo[1].getTime_open() + "-" + sellerInfo[1].getTime_close());
                tv_market_addr_value.setText(sellerInfo[1].getAddress());
                tv_menu_category_value.setText(sellerInfo[1].getKeyword());
                tv_is_open_value.setText(sellerInfo[1].getCheckOpen());

                //??????????????? ??????
                pageValue = PAGE_UP;
                translateUpAim.setAnimationListener(animationListener);
                clMarketInfo.setVisibility(View.VISIBLE);
                clMarketInfo.startAnimation(translateUpAim);

                sel_marker =1;
                return true;
            }
        });

        markerItems[2].setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                markerItems[2].setWidth(170);
                markerItems[2].setHeight(170);
                lastMarker = markerItems[2];
                tv_market_title.setText(sellerInfo[2].getName());
                tv_market_time_value.setText(sellerInfo[2].getTime_open() + "-" + sellerInfo[2].getTime_close());
                tv_market_addr_value.setText(sellerInfo[2].getAddress());
                tv_menu_category_value.setText(sellerInfo[2].getKeyword());
                tv_is_open_value.setText(sellerInfo[2].getCheckOpen());

                //??????????????? ??????
                pageValue = PAGE_UP;
                translateUpAim.setAnimationListener(animationListener);
                clMarketInfo.setVisibility(View.VISIBLE);
                clMarketInfo.startAnimation(translateUpAim);

                sel_marker =2;
                return true;
            }
        });
    }

    //  ??? ????????????
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
            //???????????????
            if (isInfoPageOpen) {
                //??????????????? ??????
                translateDownAim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
                translateDownAim.setAnimationListener(animationListener);
                pageValue = PAGE_DOWN;
                clMarketInfo.startAnimation(translateDownAim);

                //TODO getmarker ??????????????? ??????????????? ????????? ??? ????????? ??? ?????? (???????????? ?????? ?????????????????? firebase??? ?????? ?????? ????????????)
                switch (sel_marker)
                {
                    case 0 : lastMarker.setIcon(OverlayImage.fromResource(R.drawable.marker_icecream));
                    break;
                    case 1 : lastMarker.setIcon(OverlayImage.fromResource(R.drawable.marker_boong_a_bbang));
                    break;
                    case 2 :  lastMarker.setIcon(OverlayImage.fromResource(R.drawable.marker_pizza));
                    break;
                }
                lastMarker.setWidth(70);
                lastMarker.setHeight(70);
                lastMarker = null;
            }
        });
    }

    //??????????????? ????????? ??????????????? ?????? ????????? ???????????? ?????????
    protected void getMarker() {
        //??????????????? ??????
        translateUpAim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_up);
        clMarketInfo = root.findViewById(R.id.cl_market_info);

        database = FirebaseDatabase.getInstance();
        sellerRef[0] = database.getReference("Seller"+"/"+"DxIVq5n2nGdebKShVNI7ndGX5PP2");  //???????????????
        sellerRef[1] = database.getReference("Seller"+"/"+"SayMp3MfplTazcNnXf5ung4Fs0J3");  //?????????
        sellerRef[2] = database.getReference("Seller"+"/"+"xbd8Dlm2WNXkAGegT8FuhzMOSX53");  //??????

        sellerRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    sellerInfo[0] = new SellerInfo();
                    // sellerInfo[0] = snapshot.getValue(SellerInfo.class);
                    sellerInfo[0].setName(snapshot.child("name").getValue().toString());
                    sellerInfo[0].setKeyword(snapshot.child("keyword").getValue().toString());
                    sellerInfo[0].setTime_open(snapshot.child("time_open").getValue().toString());
                    sellerInfo[0].setTime_close(snapshot.child("time_close").getValue().toString());
                    sellerInfo[0].setLat((double) snapshot.child("Lat").getValue());
                    sellerInfo[0].setLng((double) snapshot.child("Lng").getValue());
                    sellerInfo[0].setAddress(snapshot.child("address").getValue().toString());
                    sellerInfo[0].setIs_open(Boolean.parseBoolean(snapshot.child("is_open").getValue().toString()));
                    markerItems[0].setPosition(new LatLng(sellerInfo[0].getLat(), sellerInfo[0].getLng()));
                    markerItems[0].setWidth(140);
                    markerItems[0].setHeight(140);
                    markerItems[0].setIcon(OverlayImage.fromResource(R.drawable.marker_icecream));

                    for (DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()) {
                        seller_menu[0] = dataSnapshot.getValue(Seller_Menu.class);
                        sellerInfo[0].setSellerMenu(seller_menu[0]);
                    }
                    if (sellerInfo[0].isIs_open() == true)
                        markerItems[0].setMap(map);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("seller data read error", error.toString());
            }
        });
        sellerRef[1].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //sellerInfo[1] = snapshot.getValue(SellerInfo.class);
                    sellerInfo[1].setName(snapshot.child("name").getValue().toString());
                    sellerInfo[1].setKeyword(snapshot.child("keyword").getValue().toString());
                    sellerInfo[1].setTime_open(snapshot.child("time_open").getValue().toString());
                    sellerInfo[1].setTime_close(snapshot.child("time_close").getValue().toString());
                    sellerInfo[1].setLat((double) snapshot.child("Lat").getValue());
                    sellerInfo[1].setLng((double) snapshot.child("Lng").getValue());
                    sellerInfo[1].setAddress(snapshot.child("address").getValue().toString());
                    sellerInfo[1].setIs_open(Boolean.parseBoolean(snapshot.child("is_open").getValue().toString()));

                    markerItems[1].setPosition(new LatLng(sellerInfo[1].getLat(), sellerInfo[1].getLng()));
                    markerItems[1].setWidth(140);
                    markerItems[1].setHeight(140);
                    markerItems[1].setIcon(OverlayImage.fromResource(R.drawable.marker_boong_a_bbang));

                    for (DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()) {
                        seller_menu[1] = dataSnapshot.getValue(Seller_Menu.class);
                        sellerInfo[1].setSellerMenu(seller_menu[1]);
                    }

                    if (sellerInfo[1].isIs_open() == true)
                        markerItems[1].setMap(map);
                    //TODO ????????? ????????? ????????????
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("seller data read error", error.toString());
            }
        });
        sellerRef[2].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //sellerInfo[2] = snapshot.getValue(SellerInfo.class);
                    sellerInfo[2].setName(snapshot.child("name").getValue().toString());
                    sellerInfo[2].setKeyword(snapshot.child("keyword").getValue().toString());
                    sellerInfo[2].setTime_open(snapshot.child("time_open").getValue().toString());
                    sellerInfo[2].setTime_close(snapshot.child("time_close").getValue().toString());
                    sellerInfo[2].setLat((double) snapshot.child("Lat").getValue());
                    sellerInfo[2].setLng((double) snapshot.child("Lng").getValue());
                    sellerInfo[2].setAddress(snapshot.child("address").getValue().toString());
                    sellerInfo[2].setIs_open(Boolean.parseBoolean(snapshot.child("is_open").getValue().toString()));
                    markerItems[2].setPosition(new LatLng(sellerInfo[2].getLat(), sellerInfo[2].getLng()));
                    markerItems[2].setWidth(140);
                    markerItems[2].setHeight(140);
                    markerItems[2].setIcon(OverlayImage.fromResource(R.drawable.marker_pizza));

                    for (DataSnapshot dataSnapshot : snapshot.child("menu").getChildren()) {
                        seller_menu[2] = dataSnapshot.getValue(Seller_Menu.class);
                        sellerInfo[2].setSellerMenu(seller_menu[2]);
                    }
                    if (sellerInfo[2].isIs_open() == true)
                        markerItems[2].setMap(map);
                    //TODO ????????? ????????? ????????????
                }
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

    //??????????????? ?????? ????????? ??????
    protected void makeCircle() {
        CircleOverlay circleOverlay = new CircleOverlay();
        circleOverlay.setCenter(new LatLng(37.4963111, 126.9574596));
        circleOverlay.setRadius(2000);
        circleOverlay.setColor(Color.parseColor("#196ED3EF"));
        circleOverlay.setOutlineColor(Color.parseColor("#FF4EBFDE"));
        circleOverlay.setOutlineWidth(3);
        circleOverlay.setMap(map);
    }

    //?????? ??? ????????? ???????????? ??????(???????????????????) -->??????????????? ????????? ??????????????? ????????????
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN: {
                    isInfoPageOpen = false;
                    btnHomeLocation.setVisibility(View.GONE);
              /*      btnZoomIn.setVisibility(View.GONE);
                    btnZoomOut.setVisibility(View.GONE);*/
                    break;
                }
                case PAGE_UP: {
                    isInfoPageOpen = true;
                    break;
                }
                case PAGE_LEFT: {
                    viewLayer.setVisibility(View.GONE);
                    isHambergerOpen = false;
                    break;
                }
                case PAGE_RIGHT: {
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
               /*     btnZoomIn.setVisibility(View.VISIBLE);
                    btnZoomOut.setVisibility(View.VISIBLE);*/
                    btnHomeLocation.setMap(map);
                    break;
                }
                case PAGE_LEFT: {
                    break;
                }
                case PAGE_RIGHT: {
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}