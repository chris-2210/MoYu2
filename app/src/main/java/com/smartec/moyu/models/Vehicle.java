package com.smartec.moyu.models;

public class Vehicle {

    private String uID;
    private String marca;
    private String modelo;
    private String color;
    private int nAcientos;
    private String nSeguro;
    private String matricula;

    public Vehicle(){}

    public Vehicle(String marca, String modelo, String color, int nAcientos, String numSeguro) {
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.nAcientos = nAcientos;
        this.nSeguro = numSeguro;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getnAcientos() {
        return nAcientos;
    }

    public void setnAcientos(int nAcientos) {
        this.nAcientos = nAcientos;
    }

    public String getnSeguro() {
        return nSeguro;
    }

    public void setnSeguro(String nSeguro) {
        this.nSeguro = nSeguro;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "uID='" + uID + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", color='" + color + '\'' +
                ", nAcientos=" + nAcientos +
                ", nSeguro='" + nSeguro + '\'' +
                '}';
    }
}
