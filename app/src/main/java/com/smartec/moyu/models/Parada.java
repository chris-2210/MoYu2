package com.smartec.moyu.models;

public class Parada {

    private String uID;
    private double lat;
    private double lng;

    public Parada() {
    }

    public Parada(String uID, double lat, double lng) {
        this.uID = uID;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @Override
    public String toString() {
        return "Parada{" +
                "uID='" + uID + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
