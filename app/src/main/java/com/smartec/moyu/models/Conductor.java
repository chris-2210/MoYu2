package com.smartec.moyu.models;

import java.util.List;

public class Conductor {

    private String uID;
    private String nombre;
    private String app;
    private String apm;
    private String edad;
    private String sexo;
    private String email;
    private String password;
    private boolean disponible;
    private String paypal;

    public Conductor() {
    }

    public Conductor(String nombre, String app, String apm, String edad, String sexo, String email, String password, boolean disponible, String paypal){
        this.nombre = nombre;
        this.app = app;
        this.apm = apm;
        this.edad = edad;
        this.sexo = sexo;
        this.email = email;
        this.password = password;
        this.disponible = disponible;
        this.paypal = paypal;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApm() {
        return apm;
    }

    public void setApm(String apm) {
        this.apm = apm;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getPaypal() {
        return paypal;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    @Override
    public String toString() {
        return "Conductor{" +
                "uID='" + uID + '\'' +
                ", nombre='" + nombre + '\'' +
                ", app='" + app + '\'' +
                ", apm='" + apm + '\'' +
                ", edad='" + edad + '\'' +
                ", sexo=" + sexo +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", disponible=" + disponible +
                ", paypal='" + paypal + '\'' +
                '}';
    }
}
