package com.eddy.mbta.ui.map;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.NearbyStationBean;
import com.eddy.mbta.json.RouteBean;
import com.eddy.mbta.service.TimeScheduleService;
import com.eddy.mbta.utils.HttpClientUtil;
import com.eddy.mbta.utils.PermissionUtils;
import com.eddy.mbta.utils.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
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
        OnInfoWindowClickListener,
        OnMapReadyCallback,
        NearbyStationAdapter.Listener {

    private View root;
    private Context mContext;

    private boolean mPermissionDenied = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Location lastSearchLocation;
    private Location mLocation;
    //private static double lat;
    //private static double lng;

    private NearbyStationAdapter nearbyStationAdapter;
    private List<NearbyStationBean.IncludedBean> nearbyStationList = new ArrayList<>();

    private List<LatLng> routeList = new ArrayList<>();
    private Polyline polyline;

    public static MapFragment newInstance() {
        Bundle args = new Bundle ();

        MapFragment fragment = new MapFragment ();
        fragment.setArguments (args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_map, container, false);

        mContext = getActivity();

        /*mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        //lat = location.getLatitude();
                        //lng = location.getLongitude();
                        //onMapReady(mMap);

                        //requestNearbyStations(lat, lng);
                    }
                }
            });
        }*/

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Criteria criteria = new Criteria();
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, true);

            Log.d("HEIHEI", provider);
            locationManager.requestLocationUpdates(provider, 5000, 1, this);

            mLocation = locationManager.getLastKnownLocation(provider);
            lastSearchLocation = mLocation;

            //lat = mLocation.getLatitude();
            //lng = mLocation.getLongitude();

            requestNearbyStations(lastSearchLocation.getLatitude(), lastSearchLocation.getLongitude(), 0.01);
        }

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);

        Window window = getActivity().getWindow();

        nearbyStationAdapter = new NearbyStationAdapter(nearbyStationList, window, root);
        nearbyStationAdapter.setListener(this);
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
                Toast.makeText(mContext, "Parse Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(mContext, "KML File Error", Toast.LENGTH_SHORT).show();
        }

        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.mbta, mContext);
            layer.addLayerToMap();
        } catch (Exception e) {
            Toast.makeText(mContext, "Loading Map Error", Toast.LENGTH_SHORT).show();
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

        LatLng my = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my, 13));

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        enableMyLocation();
    }

    private void requestNearbyStations(double mlat, double mlng, final double mradius) {
        //double radius = 0.01;
        String url = "https://api-v3.mbta.com/stops?include=parent_station&filter[route_type]=0,1&filter[latitude]=" + mlat + "&filter[longitude]=" + mlng + "&filter[radius]=" + mradius + "&sort=distance";

        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                final NearbyStationBean nearbyStationItem = gson.fromJson(response.body().string().trim(), NearbyStationBean.class);

                if (nearbyStationItem.getIncluded().size() == 0) {
                    if (mradius >= 0.05) {
                        Snackbar.make(getView(), "It seems like you are away from stations, Please using search station function.", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    } else {
                        requestNearbyStations(lastSearchLocation.getLatitude(), lastSearchLocation.getLongitude(), mradius + 0.02);
                    }
                } else {

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (nearbyStationList.size() == 0) {
                                    nearbyStationList.addAll(nearbyStationItem.getIncluded());
                                    nearbyStationAdapter.notifyItemRangeInserted(0, nearbyStationList.size());
                                } else {
                                    int previousSize = nearbyStationList.size();
                                    nearbyStationList.clear();
                                    nearbyStationAdapter.notifyItemRangeRemoved(0, previousSize);
                                    nearbyStationList.addAll(nearbyStationItem.getIncluded());
                                    nearbyStationAdapter.notifyItemRangeInserted(0, nearbyStationList.size());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        SchedulePopWindow PopWin = new SchedulePopWindow(getActivity(), marker.getTitle(), marker.getSnippet().split("/")[1]);

        PopWin.showAtLocation(root.findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        final Window window = getActivity().getWindow();
        final WindowManager.LayoutParams params = window.getAttributes();

        params.alpha = 0.7f;
        window.setAttributes(params);

        PopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if(SchedulePopWindow.handler!=null){
                    SchedulePopWindow.handler.removeCallbacksAndMessages(null);
                }
                Intent stopIntent = new Intent(mContext, TimeScheduleService.class);
                mContext.stopService(stopIntent);

                AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getService(mContext, 0, stopIntent, 0);
                manager.cancel(pi);

                params.alpha = 1f;
                window.setAttributes(params);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //requestRoute(marker.getPosition().latitude, marker.getPosition().longitude);
        return false;
    }

    @Override
    public void onClick(int position) {
        NearbyStationBean.IncludedBean station = nearbyStationList.get(position);
        requestRoute(station.getAttributes().getLatitude(), station.getAttributes().getLongitude());
    }

    private void requestRoute(final double latitude, final double longitude) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mLocation.getLatitude() + "," + mLocation.getLongitude()
                + "&destination=" + latitude + "," + longitude + "&mode=walking&key=AIzaSyA9EJnO5l1_984auwYgXZRaDychH78sd28";

        Log.d("GOOGLE", url);

        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                RouteBean route = gson.fromJson(response.body().string().trim(), RouteBean.class);

                routeList = Utility.decodePoly(route.getRoutes().get(0).getOverview_polyline().getPoints());

                final String distance = route.getRoutes().get(0).getLegs().get(0).getDistance().getText();
                final String duration = route.getRoutes().get(0).getLegs().get(0).getDuration().getText();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (polyline != null) {
                                polyline.remove();
                            }

                            PolylineOptions rectOptions = new PolylineOptions();
                            polyline = mMap.addPolyline(rectOptions
                                    .addAll(routeList)
                                    .width(8)
                                    .color(Color.BLUE)
                                    .geodesic(true));

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                            builder.include(new LatLng(latitude, longitude));

                            LatLngBounds bounds = builder.build();
                            int padding = 200; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            mMap.animateCamera(cu);

                            Snackbar.make(getView(), "Tips: " + distance + " away, about " + duration, Snackbar.LENGTH_LONG)
                                    .setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
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
                            Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

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
        mLocation = location;
        float dis = location.distanceTo(lastSearchLocation);
        Log.d("HEIHEI", dis+",");

        if (dis >= 20) {
            //nearbyStationList.clear();
            lastSearchLocation = location;
            requestNearbyStations(location.getLatitude(), location.getLongitude(), 0.01);
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        //mMap.animateCamera(cameraUpdate);
        //locationManager.removeUpdates(this);
        Log.d("HEIHEI", lastSearchLocation.getLatitude()+","+mLocation.getLatitude());
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