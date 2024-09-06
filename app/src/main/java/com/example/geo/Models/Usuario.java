package com.example.geo.Models;

public class Usuario {

    private String id;
    private String nombreC;
    private String nombreU;
    private String sexo;
    private String pwd;
    private int rol;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreC() {
        return nombreC;
    }

    public void setNombreC(String nombreC) {
        this.nombreC = nombreC;
    }

    public String getNombreU() {
        return nombreU;
    }

    public void setNombreU(String nombreU) {
        this.nombreU = nombreU;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public Usuario() {
    }

    public Usuario(String id, String nombreC, String nombreU, String sexo, String pwd, int rol) {
        this.id = id;
        this.nombreC = nombreC;
        this.nombreU = nombreU;
        this.sexo = sexo;
        this.pwd = pwd;
        this.rol = rol;
    }
}
