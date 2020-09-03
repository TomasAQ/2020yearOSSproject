package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.util.SharedPreferenceData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback ,ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleMap mMap;
    Marker mCurrentMarker = null;

    // 위치 요청시간
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // 위치관련 권한체크에 사용
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱에 필요한 현제 위치 접근 관한
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //
    Location mCurrentLocatiion;
    LatLng mCurrentPosition;

    // 마지막 확인된 위치
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    private View mLayout;

    // 동물병원 마커
    List<Marker> mPlaceMarkerList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 꺼짐 방지/ 화면 계속 켜짐
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.layout_main);

        // 100미터 정도에 도시 단위로 위치 요청할때 : PRIORITY_BALANCED_POWER_ACCURACY
        // 0.5초에서 1초 간격으로 요청
        mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        // 현재 위치 설정을 받기위해서 사용
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        // 마지막 확인된 위치 가져오기
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 프레그먼트에 GoogleMap을 출력해준다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        //동물병원 찾기 버튼 클릭시 근처 동물병원 위치 마커로 표시
        mPlaceMarkerList = new ArrayList<Marker>();

        Button btnPlaceSearch = findViewById(R.id.btn_search_animal_hospital);
        btnPlaceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceInformation(mCurrentPosition);
            }
        });
    }


    /**
     *  페이지 이동을 위한 하단 버튼
     *  홈 , 카테고리 , 정보수정 화면으로 이동
     */
    public void MovePage(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_move_home :
                intent = new Intent(this , MainActivity.class);
                break;
            case R.id.btn_move_counseling :
                intent = new Intent(this , CounselingActivity.class);
                break;
            case R.id.btn_move_news :
                intent = new Intent(this , DailyMissionActivity.class);
                //intent = new Intent(this , UploadTestActivity.class);
                break;
            case R.id.btn_move_info_set :
                intent = new Intent(this , SettingActivity.class);
                break;

            default:
                Log.d(TAG, "MovePage()함수 오류 ");
        }
        startActivity(intent);
    }

    /**
     *  구글맵 준비 되었을때 호출
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;
        // 지도 초기 세팅(남성역)
        setDefaultLocation();
        // 위치관련 권한허가 받은 상태인지 확인
        int checkSelfPermissionFine = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int checkSelfPermissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (checkSelfPermissionFine == PackageManager.PERMISSION_GRANTED && checkSelfPermissionCoarse == PackageManager.PERMISSION_GRANTED){
            // 위치 반영
            startLocationUpdates();
        }else {     // 권한 요청 필요
            // 사용자가 이전에 권한을 거부한 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "구글맵을 사용하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }else {     //권한관련 요청이 처음일때
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: "+latLng);
            }
        });

    }

    /**
     *  현재 위치를 지속적으로 갱신해준다.
     */
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                mLocation = locationList.get(locationList.size() - 1);
                mCurrentPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                String markerTitle = getCurrentAddress(mCurrentPosition);
                String markerSnippet = "위도:" + String.valueOf(mLocation.getLatitude())
                        + " 경도:" + String.valueOf(mLocation.getLongitude());
                Log.d(TAG, "onLocationResult : " + markerSnippet);
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(mLocation, markerTitle, markerSnippet);
                mCurrentLocatiion = mLocation;
            }
        }
    };

    /**
     *  권한모두 수락한 상태일때
     */
    void startLocationUpdates(){
        if(!checkLocationServicesStatus()){
            Log.d(TAG, "startLocationUpdates: ");
            showDialogForLocationServiceSetting();
        }else{
            // 위치관련 권한허가 받은 상태인지 확인
            int checkSelfPermissionFine = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
            int checkSelfPermissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (checkSelfPermissionFine != PackageManager.PERMISSION_GRANTED || checkSelfPermissionCoarse != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "startLocationUpdates: 권한 가지고 있지 않음");
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest , locationCallback , Looper.myLooper());
            if(checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    /**
     *  좌표 값을 주소로 변경
     * @param latLng 좌표값
     * @return 주소값
     */
    public String getCurrentAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(this , Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
        }catch (IOException ioException){

            return "Geocoder 서비스 불가";
        }catch (IllegalArgumentException illegalArgumentException){
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() ==0){
            return "주소 찾지 못함";
        }else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    // 인터넷과 GPS 권한 관련
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)  || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /**
     *  현재 위치를 마커로 표시
     * @param location
     * @param markerTitle
     * @param markerSnippet
     */
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (mCurrentMarker != null) mCurrentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrentMarker = mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }


    public void setDefaultLocation() {
        //디폴트 위치, 남성역
        LatLng DEFAULT_LOCATION = new LatLng(37.48508881655088, 126.97057643844835);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";
        if (mCurrentMarker != null) mCurrentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }

    //런타임 퍼미션 처리을 위한
    private boolean checkPermission() {
        int checkSelfPermissionFine = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int checkSelfPermissionCoarse = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkSelfPermissionFine == PackageManager.PERMISSION_GRANTED && checkSelfPermissionCoarse == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }


    /*
     * 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션있을때
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    //GPS 활성화를 위한 메소드
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 허용하시겠습니까");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


    @Override
    public void onPlacesFailure(PlacesException e) {}
    @Override
    public void onPlacesStart() {}
    @Override
    public void onPlacesFinished() {}

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(noman.googleplaces.Place place : places){
                    final LatLng latLng = new LatLng(place.getLatitude(),place.getLongitude());
                    String markerAddress = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerAddress);
                    Marker item = mMap.addMarker(markerOptions);
                    mPlaceMarkerList.add(item);

                    // todo : 병원마커 클릭시 병원과 내위치 까지에 거리를 빨간줄로 표시
//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            Toast.makeText(MainActivity.this, ""+marker.getTitle()+"\n"+marker.getPosition(), Toast.LENGTH_SHORT).show();
//                            mPolylineOptions = new PolylineOptions();
//                            mPolylineOptions.color(Color.RED);
//                            mPolylineOptions.width(5);
//                            mArrayPoints.add(latLng);
//                            mPolylineOptions.addAll(mArrayPoints);
//                            mMap.addPolyline(mPolylineOptions);
//                            return false;
//                        }
//                    });
                }
                // 중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(mPlaceMarkerList);
                mPlaceMarkerList.clear();
                mPlaceMarkerList.addAll(hashSet);
            }
        });
    }


    public void showPlaceInformation(LatLng location){
        mMap.clear();
        if(mPlaceMarkerList != null)
            mPlaceMarkerList.clear();

        new NRPlaces.Builder().listener(MainActivity.this).key("key")
                .latlng(location.latitude,location.longitude)
                .radius(1500)
                .type(PlaceType.VETERINARY_CARE)
                .build()
                .execute();
    }


}
