package com.example.geo.Models;

public class Calle {

    private String id;
    private String calle;
    private int peligro;
    private String suceso;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getPeligro() {
        return peligro;
    }

    public void setPeligro(int peligro) {
        this.peligro = peligro;
    }

    public String getSuceso() {
        return suceso;
    }

    public void setSuceso(String suceso) {
        this.suceso = suceso;
    }

    public Calle() {
    }

    public Calle(String id, String calle, int peligro, String suceso) {
        this.id = id;
        this.calle = calle;
        this.peligro = peligro;
        this.suceso = suceso;
    }
}
