package com.valquiria.myapplication;

/**
 * Created by murquijo on 2017-10-24.
 */

public class Localizacion {
    String nombre;
    float latitud;
    float longitud;

    public Localizacion() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }
}
