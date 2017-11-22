package com.valquiria.myapplication;

import java.util.Date;
import java.util.List;

/**
 * Created by murquijo on 2017-10-23.
 */

public class Usuarios {
    String	nombre;
    String	apellido;
    String	correo;
    String	contraseña;
    Localizacion origen;
    Localizacion destino;
    Date fecha;
    Double distancia;
    String tiempo;
    String tipo;
    String id;

    private List<Recorrido> creados;

    private List<ChatMessage> bandejaEntrada;

    private List<ChatMessage> bandejaSalida;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
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

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void enviarMensaje(String contenido, Usuarios receptor){
        bandejaSalida.add(new ChatMessage(contenido, receptor.getId()));
    }
}
