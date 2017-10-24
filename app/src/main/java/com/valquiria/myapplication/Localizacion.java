package com.valquiria.myapplication;

/**
 * Created by murquijo on 2017-10-24.
 */

public class Localizacion {
    String name;
    Double latitude;
    Double longitude;



    public Localizacion() {
       }

    public String getNombre() {
        return name;
    }

    public void setNombre(String name) {
        this.name = name;
    }

    public Double getLatitud() {
        return latitude;
    }

    public void setLatitud(Double latitud) {
        this.latitude = latitud;
    }

    public Double getLongitud() {
        return longitude;
    }

    public void setLongitud(Double longitud) {
        this.longitude = longitud;
    }
}
