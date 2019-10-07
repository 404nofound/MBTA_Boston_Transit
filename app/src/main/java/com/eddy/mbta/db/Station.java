package com.eddy.mbta.db;

import org.litepal.crud.LitePalSupport;

public class Station extends LitePalSupport {

    private int id;

    private String stationName;

    private String trainName;

    private String address;

    private int wheelchair;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(int wheelchair) {
        this.wheelchair = wheelchair;
    }
}
