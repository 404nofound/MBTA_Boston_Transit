package com.eddy.mbta.db;

import org.litepal.crud.LitePalSupport;

public class TrainLine extends LitePalSupport {

    private int id;

    private String trainName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
}
