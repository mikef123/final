package com.valquiria.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valquiria.myapplication.adapter.AdapterMensajes;
import com.valquiria.myapplication.adapter.Mensaje;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private CircleImageView fotoUsuario;
    private TextView nombreUsuario;
    private RecyclerView rvMensajes;
    private EditText mensaje;
    private Button enviar;

    private AdapterMensajes adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fotoUsuario = (CircleImageView) findViewById(R.id.fotoUsuairo);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensaje);
        mensaje = (EditText) findViewById(R.id.mensajetxt);
        enviar = (Button) findViewById(R.id.enviar);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chat");
        user = FirebaseAuth.getInstance().getCurrentUser();

        adapter  = new AdapterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        nombreUsuario.setText(user.getDisplayName());

        enviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendario = new GregorianCalendar();
                reference.push().setValue(new Mensaje(mensaje.getText().toString(),nombreUsuario.getText().toString(),"",
                        calendario.get(Calendar.HOUR_OF_DAY)+":"+
                        calendario.get(Calendar.MINUTE)+":"+
                                calendario.get(Calendar.SECOND)));
                mensaje.setText("");
            }
        } );

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensaje m = dataSnapshot.getValue(Mensaje.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

}


