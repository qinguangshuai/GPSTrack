package com.lxd.gpstrack.Entiy;

import android.graphics.PointF;

public class TrackLink {
    private int id;
    private PointF start;
    private PointF end;
    private float trackLinkPxLength;//保存连接线的屏幕显示长度，
    private float trackLinkLength;//保存实际上的连接线轨道长度
    private String oneLinkOne;  //保存连接的两条直线

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTrackLinkLength() {
        return trackLinkLength;
    }

    public void setTrackLinkLength(float trackLinkLength) {
        this.trackLinkLength = trackLinkLength;
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

    public String getoneLinkOne() {
        return oneLinkOne;
    }

    public void setoneLinkOne(String oneLinkOne) {
        this.oneLinkOne = oneLinkOne;
    }

    public float getTrackLinkPxLength() {
        return trackLinkPxLength;
    }

    public void setTrackLinkPxLength(int trackLinkPxLength) {
        this.trackLinkPxLength = trackLinkPxLength;
    }
}
