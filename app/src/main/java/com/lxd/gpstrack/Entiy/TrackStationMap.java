package com.lxd.gpstrack.Entiy;

import java.util.List;

public class TrackStationMap {

    //股道数量
    private int trackNum;
    //股道连接数
    private int trackLinkNum;
    private int LampNum;

    private List<Track> tracks;
    private List<TrackLink> trackLinks;
    private List<Lamp> lamps;

    public int getTrackNum() {
        return tracks.size();
    }

    public int getTrackLinkNum() {
        return trackLinks.size();
    }

    public int getLampNum() {
        return lamps.size();
    }


    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<TrackLink> getTrackLinks() {
        return trackLinks;
    }

    public void setTrackLinks(List<TrackLink> trackLinks) {
        this.trackLinks = trackLinks;
    }

    public List<Lamp> getLamps() {
        return lamps;
    }

    public void setLamps(List<Lamp> lamps) {
        this.lamps = lamps;
    }

}
