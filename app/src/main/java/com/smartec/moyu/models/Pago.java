package com.smartec.moyu.models;

import java.util.Date;

public class Pago {

    private String uID;
    private double cantidad;
    private Date fecha;
    private String userID;

    public Pago() {
    }

    public Pago(double cantidad, Date fecha, String userID) {
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.userID = userID;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "uID='" + uID + '\'' +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", userID='" + userID + '\'' +
                '}';
    }
}
