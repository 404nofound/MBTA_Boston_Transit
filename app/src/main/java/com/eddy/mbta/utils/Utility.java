package com.eddy.mbta.utils;

import android.text.TextUtils;

import com.eddy.mbta.db.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean handleTrainLineResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray stationArray = new JSONArray(response);

                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject obj = stationArray.getJSONObject(i);
                    Station station = new Station();
                    station.setStationName(obj.getString(""));
                    station.setTrainName(obj.getString(""));
                    station.setAddress(obj.getString(""));
                    station.setWheelchair(obj.getInt(""));
                    station.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
