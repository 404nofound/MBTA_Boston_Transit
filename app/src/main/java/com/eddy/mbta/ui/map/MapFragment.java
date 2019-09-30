package com.eddy.mbta.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;

import com.eddy.mbta.json.NearbyStationBean;
import com.eddy.mbta.json.TimeScheduleBean;
import com.eddy.mbta.utils.HttpClientUtil;
import com.eddy.mbta.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.data.kml.KmlLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MapFragment extends Fragment implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private Context mContext;

    private GoogleMap mMap;

    private boolean mPermissionDenied = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;


    public static double lat;
    public static double lng;


    private NearbyStationAdapter nearbyStationAdapter;
    private List<NearbyStationBean.IncludedBean> nearbyStationList = new ArrayList<>();



    private MapViewModel mapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        //final TextView textView = root.findViewById(R.id.text_home);

        mContext = getActivity();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        onMapReady(mMap);

                        requestNearbyStations(lat, lng);
                    }
                }
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*mapViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });*/

        //requestTimeSchedule(1,1,"");

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        nearbyStationAdapter = new NearbyStationAdapter(nearbyStationList);
        recyclerView.setAdapter(nearbyStationAdapter);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            mContext, R.raw.style_json));

            if (!success) {
                Log.e("AAA", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("AAA", "Can't find style. Error: ", e);
        }

        /*

            PolylineOptions rectOptions = new PolylineOptions();
            Polyline polyline = mMap.addPolyline(rectOptions
                    .addAll(poly)
                    .width(8)
                    //.color(Color.BLUE)
                    .color(colors[i])
                    //.color(Color.rgb(234,127,1))
                    .geodesic(true));
        */
        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.mbta, mContext);
            layer.addLayerToMap();
        } catch (Exception e) {

        }

        //poly = DecodePolylineUtil.decodePoly("qsaaGfrvpLYLuDxAI?e@MWASBcElAq@LsCZc@HaOfBoOlByB^??yB`@uE|@qBFqA@{AOu@Qo@]iAi@o@a@o@i@k@i@q@u@a@k@]m@c@{@Sc@Uq@_@qAm@wDCSo@sD??G_@Mm@Ge@IeACq@Ac@Ay@AkDAeDEmAMqAI_@Ia@I[Oc@i@iAU]_@_@SO}@g@IAIC{@CaB?uABcAEy@GiAMuAWYGc@KqDu@yHiAgBYO?_AM}@KyA[}@Ui@QmA]kA_@cA_@aA[}Am@??SI]KoBq@_AYoDcAeBc@mASwAOoBEs@?kCL}BDqC@cCFeA@_A@u@Dw@DmADiAF[@sBPyAF??u@DSByAPeAT}@V]Nk@Tm@X_@RcAv@WNo@r@]b@{DtF[h@}A|Bq@hAUZ}B|Cm@d@m@\\\\{@XY@MC_@QqBq@{F_C??g@ScnAl@??qCBie@i[y@c@m@QYIu@Gc@?i@DiARm@TQNa@f@uDbNIX??k@hBq@jBs@vBk@bA_JrQUj@??aDfI??[v@Wn@w@pBa@`Aa@fAwBtFc@~@k@hAsCxDwJ~N]|@Mb@K^CFKn@Gb@C\\\\ATCjAEt@GhBEbA??AXGlAAPM`DYjIM|CO|GM|DG|AGxCa@`MUbHs@fJOfD??ATIrB_Ap^s@jRWlJYdHOhGIpBKjAk@bBe@bAaDnGkBzD??m@pAwMpVgArCsBdGkEbPc@nAYb@a@d@eChDwG|HMPEFwCzNK^KVMJIB_@?iAO??kAOmB]gBGm@A[HSL[^a@|@e@fAo@r@iBh@aB^}@BkAGaNq@am@iDQ???sMUyQ~@uCJsBDo@Pc@Xm@l@m@bAaAvBu@pB]hAOx@UdAKr@??i@dD_@lDKlBOpCQjESvHKtEYbJg@rUGjCRnDFv@NbAPjAHRHLLFt@Nt@Vt@j@f@|@Vt@@DR~B@b@HpAFxN");

        LatLng my = new LatLng(lat, lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my, 15));

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    private void requestTimeSchedule(final int num, int direction, String id) {
        String url = "https://api-v3.mbta.com/predictions?filter[route_type]=0,1&sort=time&filter[stop]=" + id + "&filter[direction_id]=" + direction;

        //String url = "https://api-v3.mbta.com/predictions?filter[route_type]=0,1&sort=stop_sequence,time&filter[stop]=place-brico,place-babck,place-plsgr,place-harvd,place-stplb,place-cool,place-grigg&sort=stop_sequence,time";
        Log.d("HHH", url);
        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                TimeScheduleBean timeScheduleItem = gson.fromJson(response.body().string().trim(), TimeScheduleBean.class);

                int size = timeScheduleItem.getData().size();

                if (size > 2) {
                    size = 2;
                }

                for (int i = 0; i < size; i++) {

                    //timeScheduleItem.getData().get(i).setStationName(stationName);


                    String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                    String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                    String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                    int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();
                    String stop_id = timeScheduleItem.getData().get(i).getRelationships().getStop().getData().getId();

                    Log.d("DETAIL", route_id+","+direction_id+","+
                            stop_id+","+arrTimeData+","+depTimeData);

                    /*scheduleList.get(num).setArrTime(arrTimeData);
                    scheduleList.get(num).setDepTime(depTimeData);
                    scheduleList.get(num).setRoute_id(route_id);
                    scheduleList.get(num).setDirection_id(direction_id);
                    //scheduleList.get(num).setArrTime(arrTimeData);*/

                    //scheduleList.add(timeScheduleItem.getData().get(i));
                    //alertList.add(alertItem.getData().get(i));
                    //String[] station = new String[2];
                    //station[0] = nearbyStationItem.getIncluded().get(i).getAttributes().getName();
                    //station[1] = nearbyStationItem.getIncluded().get(i).getId();
                }
                    //stations.add(station);
                    /*String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                    String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                    String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                    int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();
                    String stop_id = timeScheduleItem.getData().get(i).getRelationships().getStop().getData().getId();

                    Log.d("DETAIL", route_id+","+direction_id+","+
                            stop_id+","+arrTimeData+","+depTimeData);
                }*/

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mContext, nearbyStationItem.getIncluded().get(0).getAttributes().getName(), Toast.LENGTH_LONG).show();
                            nearbyStationAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void requestNearbyStations(double lat, double lng) {
        String url = "https://api-v3.mbta.com/stops?include=parent_station&filter[route_type]=0,1&filter[latitude]=" + lat + "&filter[longitude]=" + lng + "&filter[radius]=0.01&sort=distance";

        //Log.d("QQQQ", url);
        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                NearbyStationBean nearbyStationItem = gson.fromJson(response.body().string().trim(), NearbyStationBean.class);

                nearbyStationList.addAll(nearbyStationItem.getIncluded());
                //String ids = "";

                /*for (int i = 0; i < nearbyStationItem.getIncluded().size(); i++) {

                    String stationName = nearbyStationItem.getIncluded().get(i).getAttributes().getName();
                    String stationId = nearbyStationItem.getIncluded().get(i).getId();

                    Schedule schedule = new Schedule();
                    schedule.setStationName(stationName);
                    schedule.setStationId(stationId);
                    scheduleList.add(schedule);

                    Log.d("AAA", stationName+","+stationId);


                    //requestTimeSchedule(i,0,stationId);
                    //requestTimeSchedule(i,1,stationId);
                } */




                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mContext, nearbyStationItem.getIncluded().get(0).getAttributes().getName(), Toast.LENGTH_LONG).show();
                            nearbyStationAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("TTT", "Clicked");
        return false;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            //requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(mContext, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(mContext, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getFragmentManager(), "dialog");
    }
}