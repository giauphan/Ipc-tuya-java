package com.giauphan.camerasmart.utils;

public class DeviceBeanList {

    private String devName;
    private int devID;

    private boolean status;

    public DeviceBeanList(String devName, int devID,boolean status) {
        this.devName = devName;
        this.devID = devID;
        this.status = status;
    }

    public String getDevName() {
        return devName;
    }

    public int getDevID() {
        return devID;
    }
    public boolean getType() {
        return status;
    }
}
