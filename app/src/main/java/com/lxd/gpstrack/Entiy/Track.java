package com.lxd.gpstrack.Entiy;

import android.graphics.PointF;

public class Track {
    private int id;   //轨道id
    private String name; //轨道名
    private float trackLength;//实际长度
    private PointF start;    //起点
    private PointF end;     //终点
    //private int screenDisplayLength;//在屏幕上显示的长度

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(int trackLength) {
        this.trackLength = trackLength;
    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }
    //得到Track在屏幕上显示的像素长度
    public float screenDisplayLength(){
        return end.x - start.x;
    }


}
