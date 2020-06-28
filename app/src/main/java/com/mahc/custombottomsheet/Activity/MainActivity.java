package com.mahc.custombottomsheet.Activity;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.libraries.places.compat.ui.SupportPlaceAutocompleteFragment;
import com.mahc.custombottomsheet.Adapter.RouteAdapter;
import com.mahc.custombottomsheet.R;
import com.mahc.custombottomsheet.Class.RouteList1;
import com.mahc.custombottomsheet.Interface.TaskLoadedCallback;
import com.mahc.custombottomsheetbehavior.BottomSheetBehaviorGoogleMapsLike;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayout;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayoutBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    //출발지
    //Fragment start_location;
    //도착지
   // AutoCompleteTextView end_location;
    //imagebutton
    ImageButton walk;
    ImageButton drive;
    ImageButton transit;
    ImageButton bycle;

    //imagebutton 선택한 내용 담아 놓는 변수
    int select_button;
    ImageButton select_button_name, navigation;
    String select_button_mode;

    //하단 분, km 정보 표시 textview
    static TextView bottomSheetTextView;

    //googlemap
    private GoogleMap mMap = null;

    //
    private Polyline currentPolyline;
    LatLng start_latlng ;
    LatLng end_latlng ;


    SupportMapFragment mapFragment;


    RecyclerView route_list;
    ArrayList<RouteList1> route_list_detail;
    RouteAdapter routeAdapter;


    SupportPlaceAutocompleteFragment autocompleteFragmentFrom;
    SupportPlaceAutocompleteFragment autocompleteFragmentTo;


    public static Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources=getResources();

        route_list=findViewById(R.id.route_list);
        route_list_detail=new ArrayList<>();
        routeAdapter=new RouteAdapter(route_list_detail);

        route_list.setAdapter(routeAdapter);
        route_list.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));


        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_for_view);
        mapFragment.getMapAsync(this);

        autocompleteFragmentFrom= (SupportPlaceAutocompleteFragment)getSupportFragmentManager().findFragmentById(R.id.from_location);
        autocompleteFragmentTo = (SupportPlaceAutocompleteFragment)getSupportFragmentManager().findFragmentById(R.id.to_location);


        navigation=findViewById(R.id.navigation);

        //navigation button click
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("navigation","click");

                //navi로 넘기기
                Intent intent=new Intent(MainActivity.this,NavigationActivity.class);

                Double[] latlng=new Double[2];
                latlng[1]=start_latlng.latitude;
                latlng[0]=start_latlng.longitude;


                intent.putExtra("origin",latlng);

                Double[] latlng1=new Double[2];
                latlng1[1]=end_latlng.latitude;
                latlng1[0]=end_latlng.longitude;
                intent.putExtra("destination",latlng1);
                intent.putExtra("mode",select_button_mode);

                startActivity(intent);

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        final BottomSheetBehaviorGoogleMapsLike behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                        Log.d("bottomsheet-", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                        Log.d("bottomsheet-", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                        Log.d("bottomsheet-", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:
                        Log.d("bottomsheet-", "STATE_ANCHOR_POINT");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN:
                        Log.d("bottomsheet-", "STATE_HIDDEN");
                        break;
                    default:
                        Log.d("bottomsheet-", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        MergedAppBarLayout mergedAppBarLayout = findViewById(R.id.mergedappbarlayout);
        MergedAppBarLayoutBehavior mergedAppBarLayoutBehavior = MergedAppBarLayoutBehavior.from(mergedAppBarLayout);

        if(Locale.getDefault().getLanguage().equals("ja")){
            mergedAppBarLayoutBehavior.setToolbarTitle("敬老");
        }else{
            mergedAppBarLayoutBehavior.setToolbarTitle("경로");

        }

        mergedAppBarLayoutBehavior.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT);
            }
        });

        bottomSheetTextView = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_title);

        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);


        walk=findViewById(R.id.walk);
        drive=findViewById(R.id.drive);
        transit=findViewById(R.id.transport);
        bycle=findViewById(R.id.bicycle);
        //imagebutton click listener
        ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.walk :
                        walk.setBackgroundColor(Color.YELLOW);

                        //이전에 선택했던 버튼의 id와 현재 선택한 버튼의 id가 다르다면,
                        if(select_button!=R.id.walk){
                            //기존 선택된 값이 있다면 -> 이전에 선택했던 background 바꾸기
                            if(select_button_name!=null){
                                select_button_name.setBackgroundColor(Color.WHITE);
                            }
                            //현재 선택한 버튼 background 바꾸기
                            walk.setBackgroundColor(Color.YELLOW);
                            //경로 찾기 보내기
                            select_button_mode="walking";
                            new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                                    select_button_mode), select_button_mode);
                            select_button=R.id.walk;
                            select_button_name=walk;



                            navigation.setVisibility(View.VISIBLE);
                        }


                        break ;
                    case R.id.drive :


                        //이전에 선택했던 버튼의 id와 현재 선택한 버튼의 id가 다르다면,
                        if(select_button!=R.id.drive) {
                            //기존 선택된 값이 있다면 -> 이전에 선택했던 background 바꾸기

                            if (select_button_name != null) {
                                select_button_name.setBackgroundColor(Color.WHITE);
                            }
                            //현재 선택한 버튼 background 바꾸기
                            drive.setBackgroundColor(Color.YELLOW);
                            select_button_mode="driving";
                            new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                                    select_button_mode), select_button_mode);
                            select_button = R.id.drive;
                            select_button_name = drive;

                            navigation.setVisibility(View.VISIBLE);


                        }
                        break ;
                    case R.id.transport :
                        //이전에 선택했던 버튼의 id와 현재 선택한 버튼의 id가 다르다면,
                        if(select_button!=R.id.transport) {
                            //기존 선택된 값이 있다면 -> 이전에 선택했던 background 바꾸기

                            if (select_button_name != null) {
                                select_button_name.setBackgroundColor(Color.WHITE);
                            }
                            //현재 선택한 버튼 background 바꾸기
                            transit.setBackgroundColor(Color.YELLOW);
                            select_button_mode="transit";
                            new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                                    select_button_mode), select_button_mode);
                            select_button = R.id.transport;
                            select_button_name = transit;

                            //transit은 navi 지원 안 함.
                            navigation.setVisibility(View.GONE);
                        }
                        break ;
                    case R.id.bicycle :
                        //이전에 선택했던 버튼의 id와 현재 선택한 버튼의 id가 다르다면,
                        if(select_button!=R.id.bicycle) {
                            //기존 선택된 값이 있다면 -> 이전에 선택했던 background 바꾸기
                            if (select_button_name != null) {
                                select_button_name.setBackgroundColor(Color.WHITE);
                            }
                            //현재 선택한 버튼 background 바꾸기
                            bycle.setBackgroundColor(Color.YELLOW);
                            select_button_mode="cycling";
                            new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                                    select_button_mode), select_button_mode);
                            select_button = R.id.bicycle;
                            select_button_name = bycle;

                            navigation.setVisibility(View.VISIBLE);
                        }
                        break ;
                }
            }
        } ;

        walk.setOnClickListener(onClickListener);
        drive.setOnClickListener(onClickListener);
        transit.setOnClickListener(onClickListener);
        bycle.setOnClickListener(onClickListener);

    }


    //출발지와 목적지 위도, 경도값으로
    private void startStop(GoogleMap mMap){

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(start_latlng, 16);
        mMap.moveCamera(cameraUpdate);

        //출발지 마커 정보+출발지 주소 text에 넣기
        String markerSnippet = getCurrentAddress(start_latlng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(start_latlng);
        markerOptions.snippet(markerSnippet);
        autocompleteFragmentFrom.setText(markerSnippet);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap
                (Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.start))
                        .getBitmap(),200,200,false)));


        //목적지 마커 정보+목적지 주소 text에 넣기
        String markerSnippet1 = getCurrentAddress(end_latlng);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(end_latlng);
        markerOptions1.snippet(markerSnippet1);
        autocompleteFragmentTo.setText(markerSnippet1);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap
                (Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.finish))
                        .getBitmap(),200,200,false)));
        //  end_location.setClickable(false);
        // end_location.setText(markerSnippet1);

        mMap.addMarker(markerOptions);
        mMap.addMarker(markerOptions1);




    }


    //googlemap 시작될 때
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("map ready","map ready");
        mMap = googleMap;

        //출발지(현재 위치일 수도 있고, 아니면 그냥 지정되어 있을지도 모른다. 이건 추후에 이야기해야한다.) . 목적지를 받아온다.

        start_latlng =new LatLng(35.6390991,139.8875353);
        end_latlng =new LatLng(35.6810403, 139.7638027);
        //map 출발지, 도착지 marking
        startStop(mMap);
        //default -> 대중교통 선택된 경로 찾기로 설정해 놓는다.
        walk.setBackgroundColor(Color.YELLOW);

        select_button=R.id.walk;

        select_button_name=walk;
        select_button_mode="walking";
        navigation.setVisibility(View.VISIBLE);
        new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                select_button_mode), select_button_mode);




    }

    //출발지, 목적지 주소를 가지고 와서 마커도 하고, 주소창에 입력도 하기 위해서 필요한 method
    public String getCurrentAddress(LatLng latlng) {


        //위치 정보 활요이을 위한 구글 api 객체, Locale.getDefault()-> 운영체제의 기본값을 읽어옴.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        //주소 목록 담기 위한 LIST
        List<Address> addresses;
        try {
            //주소 목록 가지고 오기 -> 위도, 경도, 조회 갯수(1개로 설정)
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "오프라인 상태에서 길찾기 지원이 불가능합니다.", Toast.LENGTH_LONG).show();
            return "오프라인 상태에서 길찾기 지원이 불가능합니다.";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {

            //값이 하나라도 있다면
            //나라, 시, 구, 동 , 지번을 가지고 옴.
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }



    //url 조정을 하기 위해서 필요함. 즉, api 호출을 할 때, 이동 수단 선택을 통해 경로를 제공받고자 parameter 값을 조정해야 함.
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {

        Log.i("geturl",origin+"/"+dest+"/"+directionMode);

        // 출발지 -위도, 경도
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // 도착지 -위도, 경도
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // 이동수단 선택
        String mode = "mode=" + directionMode;
        // 파라미터 값 모음
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // return 값은 json으로 받기
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&language="+Locale.getDefault().getLanguage()+"&key=AIzaSyDRwo7xOLVsPTu3U4RNNwTpYyysPBRyBUs" ;


        if(directionMode.equals("transit")){
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?alternatives=true&" + parameters + "&transit_mode=bus&language="+Locale.getDefault().getLanguage()+"&key=AIzaSyDRwo7xOLVsPTu3U4RNNwTpYyysPBRyBUs" ;

        }

        return url;
    }


    //값을 받아서 지도 폴리라인, 마커, 이동경로
    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null){
            currentPolyline.remove();
            mMap.clear();
            //출발지 시작지 다시 넣기
            startStop(mMap);
        }


        // 결과값이 없을 때
        if(!values[0].equals("ZERO_RESULTS")){
            if(route_list_detail.size()!=0){
                route_list_detail.clear();
            }
            routeAdapter.notifyDataSetChanged();

            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);



            ArrayList<MarkerOptions> value= (ArrayList<MarkerOptions>) values[1];
            ArrayList<String> distance= (ArrayList<String>) values[2];
            ArrayList<String> duration= (ArrayList<String>) values[3];
            ArrayList<String> instruction= (ArrayList<String>) values[4];

            if(!values[5].equals("")&&!values[6].equals("")){
                bottomSheetTextView.setText(values[7]+"("+values[6]+")");
            }



            for (int i = 0; i <value.size() ; i++) {
                mMap.addMarker(value.get(i));

                String instructionreplace=instruction.get(i);
                instructionreplace=instructionreplace.replace("<b>","");
                instructionreplace=instructionreplace.replace("</b>","");
                instructionreplace=instructionreplace.replace("<wbr/>","");
                instructionreplace=instructionreplace.replace("<div style=\"font-size:0.9em\">","");
                instructionreplace=instructionreplace.replace("230&nbsp;m","");
                instructionreplace=instructionreplace.replace("52&nbsp;m","");
                instructionreplace=instructionreplace.replace("</div>","");
                route_list_detail.add(new RouteList1((i+1)+"",instructionreplace,distance.get(i)+"("+duration.get(i)+")"));
            }


        }else{
            if(route_list_detail.size()!=0){
                route_list_detail.clear();
            }
            routeAdapter.notifyDataSetChanged();

            route_list_detail.add(new RouteList1("",getString(R.string.no_route),"0km(0분)"));
        }



        routeAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onResume() {
        super.onResume();
        //자동주소찾기기능
        setupAutoCompleteFragment();
    }


    //autocomplete 사용하는 것.
    private void setupAutoCompleteFragment() {



        //출발지 주소로 지정하는 경우
         autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("start place",place.getAddress()+"/"+place.getName());

                //출발지 선택한 위도, 경로값으로 바꾸기
                start_latlng=place.getLatLng();
                //마커 이동도 해야하므로 함수에 넣어서 다시 뽑아내기
                //경로 기존에 선택한 이동수단으로 다시 뽑아내기
                //경로 찾기 보내기
                startStop(mMap);

                new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                        select_button_mode), select_button_mode);
            }


            @Override
            public void onError(Status status) {
                Log.i("startauto error",status.getStatusMessage());
            }
        });


        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("destination place",place.getAddress()+"/"+place.getName());
                //도착지지 선택한 위도, 경로값으로 바꾸기
                end_latlng=place.getLatLng();
                //마커 이동도 해야하므로 함수에 넣어서 다시 뽑아내기
                startStop(mMap);

                new MoveURL(MainActivity.this).execute(getUrl(start_latlng, end_latlng,
                        select_button_mode), select_button_mode);
            }

            @Override
            public void onError(Status status) {
                Log.i("destination auto error",status.getStatusMessage());
            }
        });
    }

}
