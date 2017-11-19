package com.valquiria.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Recorrido extends AppCompatActivity implements View.OnClickListener {
    ListView list;
    String[]mProjection;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;
    Usuarios myUser;
    Ruta ruta;
    private ProgressDialog mProgress;
    List<String> ejemploLista = new ArrayList<String>();
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PATH_USERS = "users/";
    ListView lista;
    ArrayAdapter<String> adaptador;
    public static final String PATH_RECORRIDOS = "recorridos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorrido);
        mAuth = FirebaseAuth.getInstance();
        lista = (ListView)findViewById(R.id.list);
        findViewById(R.id.unirse).setOnClickListener(this);
        findViewById(R.id.adicionar).setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);
        loadUsers();
    }


    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.unirse:
               //o
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                        String nombre = String.valueOf(lista.getItemAtPosition(posicion));
                        registro(nombre);
                        Toast.makeText(Recorrido.this,"Recorrido Adicionado",Toast.LENGTH_SHORT).show();

                    }
                });
                //startActivity(new Intent(Recorrido.this,	Principal.class));
                break;
            case R.id.adicionar:

                startActivity(new Intent(Recorrido.this,	Principal.class));
                break;


        }
    }

    public void loadUsers() {
        FirebaseUser user	=	mAuth.getCurrentUser();
        myRef = database.getReference(PATH_RECORRIDOS + "ruta");
        ejemploLista.clear();
        myRef.addValueEventListener(new	ValueEventListener()/*; addValueEventListener(new	ValueEventListener()*/	{
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    //myUser = new Usuarios();
                    ruta= new Ruta();
                    ruta = singleSnapshot.getValue(Ruta.class);
                    Localizacion origen = ruta.getOrigen();
                    Localizacion destino = ruta.getDestino();
                    ejemploLista.add(origen.getName() + " - " + destino.getName());                }
                adaptador = new ArrayAdapter<String>(Recorrido.this,android.R.layout.simple_list_item_1,ejemploLista);
                lista.setAdapter(adaptador);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error	en	la	consulta", databaseError.toException());
            }
        });
    }
    private void registro(String nombre)
    {
        mProgress.setMessage("Uniendose a Recorrido... Espere por favor...");
        mProgress.show();



        FirebaseUser user	=	mAuth.getCurrentUser();
        if(user!=null){	//Update	user	Info
            DatabaseReference dbRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child(PATH_RECORRIDOS + "ruta" );



            //dbRef.child("nombre").equalTo(nombre).getRef().setValue(null);
            StringTokenizer tokens=new StringTokenizer(nombre , "-");
            int nDatos=tokens.countTokens();
            String[] datos=new String[nDatos];
            int i=0;
            while(tokens.hasMoreTokens()){
                String str=tokens.nextToken();
                datos[i]=str;
                System.out.println(datos[i]);
                i++;
            }
            //dbRef.orderByChild("name").equalTo(datos[0]). addListenerForSingleValueEvent(new ValueEventListener() {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        //Log.d("User key", child.getKey());
                        //child.getRef().removeValue();// setValue(null)toString());

                        //Log.d("User val", child.getValue().toString());
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

                /*
            Map<String, Object> userUpdates = new HashMap<String, Object>();
            userUpdates.put(PATH_USERS + user.getUid() + "/Amigos/" + nombre, " ");

            dbRef.child(nombre).setValue(null);

            //myRef.child("Amigos").setValue(nombre);
            mProgress.dismiss();
            myRef = database.getReference("message");
            myRef.setValue("Amigo Eliminado!");
*/

            });
        }mProgress.dismiss();
    }
}