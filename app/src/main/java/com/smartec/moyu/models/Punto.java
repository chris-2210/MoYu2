package com.smartec.moyu.models;

public class Punto {

    private Double latInicial;
    private Double lngInicial;
    private Double latFinal;
    private Double lngFinal;

    public Double getLatInicial() {
        return latInicial;
    }

    public void setLatInicial(Double latInicial) {
        this.latInicial = latInicial;
    }

    public Double getLngInicial() {
        return lngInicial;
    }

    public void setLngInicial(Double lngInicial) {
        this.lngInicial = lngInicial;
    }

    public Double getLatFinal() {
        return latFinal;
    }

    public void setLatFinal(Double latFinal) {
        this.latFinal = latFinal;
    }

    public Double getLngFinal() {
        return lngFinal;
    }

    public void setLngFinal(Double lngFinal) {
        this.lngFinal = lngFinal;
    }

    @Override
    public String toString() {
        return "Punto{" +
                "latInicial=" + latInicial +
                ", lngInicial=" + lngInicial +
                ", latFinal=" + latFinal +
                ", lngFinal=" + lngFinal +
                '}';
    }
}
