package com.valquiria.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.valquiria.myapplication.R;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jhonan on 22/11/2017.
 */

public class HolderMensaje extends RecyclerView.ViewHolder{

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;
    private CircleImageView fotoUsuario;

    public HolderMensaje(View itemView){
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombreUsuarioMensaje);
        mensaje = (TextView) itemView.findViewById(R.id.mensajeMensaje);
        hora = (TextView) itemView.findViewById(R.id.horaMensaje);
        fotoUsuario = (CircleImageView) itemView.findViewById(R.id.fotoUsuarioMensaje);
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public CircleImageView getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(CircleImageView fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}
