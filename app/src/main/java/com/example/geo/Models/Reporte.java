package com.example.geo.Models;

import java.io.Serializable;

public class Reporte implements Serializable {

    private String id;
    private String reporte;
    private String calle;
    private String hora;
    private String frecuencia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Reporte() {
    }

    public Reporte(String id, String reporte, String calle, String hora, String frecuencia) {
        this.id = id;
        this.reporte = reporte;
        this.calle = calle;
        this.hora = hora;
        this.frecuencia = frecuencia;
    }
}
