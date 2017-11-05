package com.valquiria.myapplication;

import java.util.Date;

/**
 * Created by murquijo on 2017-11-05.
 */

public class Ruta {
    Localizacion origen;
    Localizacion destino;
    Date fecha;
    Double distancia;
    String tiempo;

    public Ruta() {
    }

    public Localizacion getOrigen() {
        return origen;
    }

    public void setOrigen(Localizacion origen) {
        this.origen = origen;
    }

    public Localizacion getDestino() {
        return destino;
    }

    public void setDestino(Localizacion destino) {
        this.destino = destino;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }
}

