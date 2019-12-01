package com.eddy.mbta.ui.map;

import android.Manifest;
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

import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;
import com.eddy.mbta.json.NearbyStationBean;
import com.eddy.mbta.json.RouteBean;
import com.eddy.mbta.service.TimeScheduleService;
import com.eddy.mbta.utils.HttpClientUtil;
import com.eddy.mbta.utils.LogUtil;
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
        GoogleMap.OnMarkerClickListener,
        OnInfoWindowClickListener,
        //GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private View root;
    private Context mContext;
    private boolean mPermissionDenied = false;
    private LocationManager locationManager;
    private GoogleMap mMap;
    //private KmlLayer layer;
    //private int kml_index = 0;

    private Location mLocation = null;
    private Location lastSearchLocation = null;

    private NearbyStationAdapter nearbyStationAdapter;
    private List<NearbyStationBean.DataBeanX> nearbyStationList = new ArrayList<>();

    private List<LatLng> routeList = new ArrayList<>();
    private Polyline polyline;

    private String provider = null;

    private Location boston;
    private View holderView;

    public static MapFragment newInstance() {
        Bundle args = new Bundle ();

        MapFragment fragment = new MapFragment ();
        fragment.setArguments (args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        LogUtil.d("ViewPager", "MapFragment");

        root = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = getActivity();

        holderView = root.findViewById(R.id.holder_place);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);

        boston = new Location("");
        boston.setLatitude(42.3601);
        boston.setLongitude(-71.0589);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Criteria criteria = new Criteria();
            locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);

            try {
                provider = locationManager.getBestProvider(criteria, true);
            } catch(Exception ex) {}

            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (mLocation == null || l.getAccuracy() < mLocation.getAccuracy()) {
                    mLocation = l;
                }
            }

            if (mLocation != null) {
                if (mLocation.distanceTo(boston) > 16000) {

                    mLocation = null;

                    LogUtil.d("MapFragment", "Location Far Away");

                    holderView.setVisibility(View.VISIBLE);

                    Toast.makeText(MyApplication.getContext(), "You are far away from Boston", Toast.LENGTH_SHORT).show();
                } else {
                    lastSearchLocation = mLocation;
                    requestNearbyStations(lastSearchLocation.getLatitude(), lastSearchLocation.getLongitude(), 0.01);
                }
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Window window = getActivity().getWindow();

        nearbyStationAdapter = new NearbyStationAdapter(nearbyStationList, window, root);
        nearbyStationAdapter.setListener(direListener);
        recyclerView.setAdapter(nearbyStationAdapter);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(mContext, R.raw.style_json));
            if (!success) {
                Toast.makeText(MyApplication.getContext(), "Parse Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(MyApplication.getContext(), "Map Style File Error", Toast.LENGTH_SHORT).show();
        }

        if (MyApplication.NET_STATUS == -1) return;

        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.mbta, MyApplication.getContext());
            layer.addLayerToMap();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getContext(), "Loading Map Error", Toast.LENGTH_SHORT).show();
        }

        loadListeners();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //locationManager.removeUpdates(mListener);
            LogUtil.d("MapFragment", "Location Update Start");
            locationManager.requestLocationUpdates(provider, 5000, 1, mListener);
        }
    }

    public void loadListeners() {
        if (mLocation != null) {

            LogUtil.d("MapFragment", "Map Listeners");

            LatLng my = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my, 13));

            mMap.setOnMarkerClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
            //mMap.setOnCameraIdleListener(this);
            enableMyLocation();
        } else {

            LogUtil.d("MapFragment", "Show Boston");

            LatLng my = new LatLng(boston.getLatitude(), boston.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my, 11));

            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
            //mMap.setOnCameraIdleListener(this);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if (MyApplication.NET_STATUS == -1) {
            Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        SchedulePopWindow PopWin = new SchedulePopWindow(getActivity(), marker.getTitle(), marker.getSnippet().split("/")[1]);
        PopWin.showAtLocation(root.findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        final Window window = getActivity().getWindow();
        final WindowManager.LayoutParams params = window.getAttributes();

        params.alpha = 0.7f;
        window.setAttributes(params);

        PopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if(SchedulePopWindow.handler != null){
                    SchedulePopWindow.handler.removeCallbacksAndMessages(null);
                }

                Intent stopIntent = new Intent(MyApplication.getContext(), TimeScheduleService.class);
                MyApplication.getContext().stopService(stopIntent);

                params.alpha = 1f;
                window.setAttributes(params);
            }
        });
    }

    /*@Override
    public void onCameraIdle() {

        if (MyApplication.NET_STATUS == -1) return;

        LogUtil.d("Camera", mMap.getCameraPosition().zoom+"");

        if (mMap.getCameraPosition().zoom <= 12 && kml_index != 1) {

            kml_index = 1;
            layer.removeLayerFromMap();

            try {
                layer = new KmlLayer(mMap, R.raw.mbta_small, MyApplication.getContext());
                layer.addLayerToMap();
            } catch (Exception e) {
                Toast.makeText(MyApplication.getContext(), "Loading Map Error", Toast.LENGTH_SHORT).show();
            }
        } else if (mMap.getCameraPosition().zoom > 12 && kml_index != 0) {

            kml_index = 0;
            layer.removeLayerFromMap();

            try {
                layer = new KmlLayer(mMap, R.raw.mbta, MyApplication.getContext());
                layer.addLayerToMap();
            } catch (Exception e) {
                Toast.makeText(MyApplication.getContext(), "Loading Map Error", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private NearbyStationAdapter.Listener direListener = new NearbyStationAdapter.Listener() {
        @Override
        public void onClick(int position) {

            if (MyApplication.NET_STATUS == -1) {
                Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }

            NearbyStationBean.DataBeanX station = nearbyStationList.get(position);
            requestRoute(station.getAttributes().getLatitude(), station.getAttributes().getLongitude());
        }
    };

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }

            locationManager.removeUpdates(mListener);
            LogUtil.d("MapFragment", "Location Update Start");
            locationManager.requestLocationUpdates(provider, 5000, 1, mListener);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(MyApplication.getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(MyApplication.getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            locationManager.removeUpdates(mListener);
            locationManager = null;
        }

        mListener = null;
        direListener = null;
    }

    private LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            mLocation = location;
            LogUtil.d("MapFragment", "Update mLocation in Listener");

            if (lastSearchLocation == null) {

                if (mLocation.distanceTo(boston) > 16000) {

                    LogUtil.d("MapFragment", "Far Again, Removed");

                    locationManager.removeUpdates(mListener);
                    return;
                }

                LogUtil.d("MapFragment", "Load Nearby Station/Map Again");

                lastSearchLocation = mLocation;
                //onMapReady(mMap);
                loadListeners();
                requestNearbyStations(lastSearchLocation.getLatitude(), lastSearchLocation.getLongitude(), 0.01);

                return;
            }

            float dis = location.distanceTo(lastSearchLocation);

            if (dis >= 20) {

                LogUtil.d("MapFragment", "Update Nearby Stations");

                lastSearchLocation = location;
                requestNearbyStations(location.getLatitude(), location.getLongitude(), 0.01);
            }
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            //mMap.animateCamera(cameraUpdate);
            //locationManager.removeUpdates(this);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getFragmentManager(), "dialog");
    }

    private void requestNearbyStations(double mlat, double mlng, final double mradius) {

        if (MyApplication.NET_STATUS == -1) {
            Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://api-v3.mbta.com/stops?filter[route_type]=0,1&sort=distance&filter[latitude]=" + mlat + "&filter[longitude]=" + mlng + "&filter[radius]=" + mradius;

        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                final NearbyStationBean nearbyStationItem = gson.fromJson(response.body().string().trim(), NearbyStationBean.class);

                if (nearbyStationItem.getData() != null && nearbyStationItem.getData().size() == 0) {
                    if (mradius > 0.05) {
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

                                holderView.setVisibility(View.GONE);

                                int previousSize = nearbyStationList.size();
                                if (previousSize != 0) {
                                    nearbyStationList.clear();
                                    nearbyStationAdapter.notifyItemRangeRemoved(0, previousSize);
                                }

                                for (int i = 0; i < nearbyStationItem.getData().size() - 1; i += 2) {
                                    nearbyStationList.add(nearbyStationItem.getData().get(i));
                                }

                                nearbyStationAdapter.notifyItemRangeInserted(0, nearbyStationList.size());
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
                            Toast.makeText(MyApplication.getContext(), "Internet Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void requestRoute(final double latitude, final double longitude) {

        if (MyApplication.NET_STATUS == -1) {
            Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mLocation.getLatitude() + "," + mLocation.getLongitude()
                + "&destination=" + latitude + "," + longitude + "&mode=walking&key=AIzaSyA9EJnO5l1_984auwYgXZRaDychH78sd28";

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
}