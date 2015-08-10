package com.example.hanjiyeon.maptest;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final LatLng SEOUL = new LatLng(37.56,126.97);
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    String address;

    private DBManager dbManager;
    private Schedule schedule;
    private ArrayList memo_item=null;
    private ArrayList list;
    double lat;
    double lng;
    double lat1;
    double lng1;
    private ArrayList task_item=null;
    double l1,l2;
    boolean MB,SB;
    boolean flag = false;
    ImageButton addMainBtn,selectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //startActivity(new Intent(this, SplashActivity.class));
        if(getIntent().getExtras() == null)  startActivity(new Intent(this, SplashActivity.class));

        addMainBtn = (ImageButton) findViewById(R.id.plus_button);
        selectBtn = (ImageButton) findViewById(R.id.select_button);

        try {
            Intent intent = getIntent();
            MB = intent.getExtras().getBoolean("MB");
            SB = intent.getExtras().getBoolean("SB");


            if (MB == true) {
                addMainBtn.setVisibility(View.VISIBLE);
                selectBtn.setVisibility(View.INVISIBLE);
            } else {
                addMainBtn.setVisibility(View.INVISIBLE);
                selectBtn.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){}

        addMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;

                Toast toast=Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
                ImageView imageView=new ImageView(getApplicationContext());

                imageView.setImageResource(R.drawable.map_notice);

/* 토스트에 뷰 셋팅하기 xml 통째로 넣어도 됨 */
                toast.setView(imageView);

        //위치 지정

                toast.setGravity(Gravity.TOP,50,50);
//여백 지정

                toast.setMargin(0, 3500);
                toast.show();
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),l1+"/"+l2,Toast.LENGTH_SHORT).show();
                Intent intentSubActivity =
                        new Intent(MapsActivity.this, Schedule.class);
                intentSubActivity.putExtra("lat", l1);
                intentSubActivity.putExtra("lng", l2);
                intentSubActivity.putExtra("address", address);
                startActivity(intentSubActivity);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));//초기 위치...수정필요

        dbManager = new DBManager(MapsActivity.this, "WitchNote.db", null, 1);
        dbManager.init();

        ArrayList allList = dbManager.getAllMarker();

        if (allList.size() >= 1) {

            for (int i = 0; i < allList.size(); i++) {
                list=(ArrayList)allList.get(i);
                ScheduleData sd=(ScheduleData)list.get(0);

                LatLng currentPosition = new LatLng(sd.getLat(), sd.getLng());
                map.addMarker(new MarkerOptions().position(currentPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mappoint)));
            }
        }

        ArrayList list = dbManager.getFirstMarker();

        if (list.size() >= 1) {
            ScheduleData sd = (ScheduleData) list.get(0);
            lat = sd.getLat();
            lng = sd.getLng();

            LatLng position = new LatLng(lat, lng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
            map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        } else {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    Looper.prepare();
                    String msg = "lon: " + location.getLongitude() + " -- lat: " + location.getLatitude();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    currentDrawMarker(location);

                }
            };

            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(MapsActivity.this, locationResult);
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                address = findAddress(point.latitude,point.longitude);

                if(flag) {
                    address = findAddress(point.latitude, point.longitude);
                    MarkerOptions markerOptions1 = new MarkerOptions()
                            .position(new LatLng(point.latitude, point.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mappoint));

                    l1 = point.latitude;
                    l2 = point.longitude;

                    map.addMarker(markerOptions1);
                    addMainBtn.setVisibility(View.INVISIBLE);
                    selectBtn.setVisibility(View.VISIBLE);

                    flag = false;
                }
            }
        });

        final Dialog dialog=new Dialog(MapsActivity.this);

        View view = View.inflate(this, R.layout.memo_dialog, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        dialog.setTitle("");

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = AbsoluteLayout.LayoutParams.MATCH_PARENT;
        params.height = AbsoluteLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {


                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));//초기 위치...수정필요
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LatLng latlng = marker.getPosition();
                lat1 = latlng.latitude;
                lng1 = latlng.longitude;

                ArrayList list = dbManager.getSelectMarkerData(lat1, lng1);
                ScheduleData sd = (ScheduleData) list.get(0);
                task_item = new ArrayList<String>();
                if (list.size() >= 2) {
                    for (int i = 1; i < list.size(); i++) {
                        PlanData pd = (PlanData) list.get(i);
                        task_item.add(pd.getContent());

                    }
                }

                TextView placeName = (TextView) dialog.findViewById(R.id.placeName);
                placeName.setText(sd.getPlaceName());
                Button modify_btn = (Button) dialog.findViewById(R.id.modify_btn);
                Button finish_btn = (Button) dialog.findViewById(R.id.finish_btn);

                memo_item = new ArrayList<String>();
                Toast.makeText(getApplicationContext(), task_item.toString(), Toast.LENGTH_LONG).show();
                final ArrayAdapter<String> adapter_dialog = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_multiple_choice, task_item);

                ListView list_dialog = (ListView) dialog.findViewById(R.id.list_dialog);

                list_dialog.setAdapter(adapter_dialog);
                list_dialog.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                modify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 메모 activity로 넘어감
                        Intent intentSubActivity =
                                new Intent(MapsActivity.this, Schedule.class);
                        startActivity(intentSubActivity);
                    }
                });

                finish_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //다음 장소포커스로 이동

                        dbManager.finishMarker(lat1, lng1);
                        ArrayList temp = dbManager.getFirstMarker();
                        if (temp.size() >= 1) {
                            ScheduleData sd = (ScheduleData) temp.get(0);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sd.getLat(), sd.getLng()), 15));


                        }
                    }
                });
                dialog.getWindow().setGravity(Gravity.TOP);
                dialog.show();

                return false;
            }

        });
    }

    private String findAddress(double lat, double lng) {
        StringBuffer bf = new StringBuffer();
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                address = geocoder.getFromLocation(lat, lng, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (address != null && address.size() > 0) {
                    // 주소
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    // 전송할 주소 데이터 (위도/경도 포함 편집)
                    bf.append(currentLocationAddress);
                }
            }

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "주소취득 실패", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return bf.toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        return cLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        showLocations(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {

    }

    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        map.clear();
        while(c.moveToNext()){
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
            markerOptions.position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mappoint));
            markerOptions.title(c.getString(0));
            map.addMarker(markerOptions);
        }
        if(position!=null){
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            map.animateCamera(cameraPosition);
        }
    }

    private void currentDrawMarker(Location location) {
        //기존 마커 지우기
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mappoint))
                .title("현재위치"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}