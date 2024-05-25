package com.giauphan.camerasmart.utils;

public class VideoClip {
    private int startTime;
    private int endTime;

    private int type;

    public VideoClip(int startTime, int endTime,int Type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = Type;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
    public int getType() {
        return type;
    }

}
