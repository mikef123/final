package com.valquiria.myapplication.adapter;

/**
 * Created by Jhonan on 22/11/2017.
 */

public class Mensaje {

    private String mensaje;
    private String nombre;
    private String fotoPerfil;
    private String hora;

    public Mensaje() {
    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String hora) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.hora = hora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
