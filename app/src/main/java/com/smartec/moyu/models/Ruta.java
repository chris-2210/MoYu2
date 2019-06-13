package com.smartec.moyu.models;

import java.util.List;

public class Ruta {

    private String uID;
    private Parada inicio;
    private Parada fin;

    public Ruta() {
    }

    public Ruta(Parada inicio, Parada fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public Parada getInicio() {
        return inicio;
    }

    public void setInicio(Parada inicio) {
        this.inicio = inicio;
    }

    public Parada getFin() {
        return fin;
    }

    public void setFin(Parada fin) {
        this.fin = fin;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "uID='" + uID + '\'' +
                ", inicio=" + inicio +
                ", fin=" + fin +
                '}';
    }
}
