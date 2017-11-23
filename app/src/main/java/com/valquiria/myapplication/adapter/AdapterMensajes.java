package com.valquiria.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valquiria.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jhonan on 22/11/2017.
 */

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje>{

    private List<Mensaje> listaMensajes;
    private Context c;

    public AdapterMensajes(Context c) {
        listaMensajes = new ArrayList<Mensaje>();
        this.c = c;
    }

    public void addMensaje(Mensaje m){
        listaMensajes.add(m);
        notifyItemInserted(listaMensajes.size());
    }

    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {
        holder.getNombre().setText(listaMensajes.get(position).getNombre());
        holder.getMensaje().setText(listaMensajes.get(position).getMensaje());
        holder.getHora().setText(listaMensajes.get(position).getHora());
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
