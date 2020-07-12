package com.lxd.gpstrack.Entiy;

import android.graphics.Point;

public class Lamp {
    private Point position;
    private Point lampTextPosition;
    private String lampName;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getLampTextPosition() {
        return lampTextPosition;
    }

    public void setLampTextPosition(Point lampTextPosition) {
        this.lampTextPosition = lampTextPosition;
    }

    public String getLampName() {
        return lampName;
    }

    public void setLampName(String lampName) {
        this.lampName = lampName;
    }
}
