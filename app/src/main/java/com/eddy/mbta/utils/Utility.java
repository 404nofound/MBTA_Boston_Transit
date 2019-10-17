package com.eddy.mbta.utils;

import android.text.TextUtils;

import com.eddy.mbta.db.Station;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    public static boolean handleStationResponse(String response, String train) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray stationArray = new JSONArray(response);
                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject obj = stationArray.getJSONObject(i);

                    Station station = new Station();
                    station.setStationName(obj.getString("name"));
                    station.setAlias(obj.getString("id"));
                    station.setTrainName(train);
                    station.setAddress(obj.getString("address"));
                    station.setWheelchair(obj.getInt("wheel"));
                    station.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
