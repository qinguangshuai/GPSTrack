package com.lxd.gpstrack.Entiy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BJXT-LXD
 * @version 1.0
 * @date 2019/9/18 12:45
 * @description TODO
 */
public class GPSPointF implements Serializable {
    float x;
    float y;

    public GPSPointF(){
        
    }
    public GPSPointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
