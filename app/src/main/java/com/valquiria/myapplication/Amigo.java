package com.valquiria.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Amigo extends AppCompatActivity implements View.OnClickListener {
    ListView list;
    String[]mProjection;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;
    Usuarios myUser;
    private ProgressDialog mProgress;
    List<String> ejemploLista = new ArrayList<String>();
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PATH_USERS = "users/";
    ListView lista;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigo);
        mAuth = FirebaseAuth.getInstance();
        lista = (ListView)findViewById(R.id.list);
        findViewById(R.id.adicionar).setOnClickListener(this);
        findViewById(R.id.eliminar).setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);



        loadUsers();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Enviar Mensaje", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                startActivity(new Intent(Amigo.this, Chat.class));

            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.menuLogOut) {
            mAuth.signOut();
            Intent intent = new Intent(Amigo.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (itemClicked == R.id.menuSettings) {
            Intent intent = new Intent(Amigo.this, ModificarUsuario.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//Abrir actividad para	configuración etc
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.adicionar:
                startActivity(new	Intent(Amigo.this,	Usuario.class));	//o
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                        //String nombre = String.valueOf(lista.getItemAtPosition(posicion));
                        //registros(nombre);
                        //Toast.makeText(Amigo.this,"Amigo Adicionado",Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.eliminar:

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                        String nombre = String.valueOf(lista.getItemAtPosition(posicion));
                        eliminar(nombre);
                        ejemploLista.clear();
                        Toast.makeText(Amigo.this,"Amigo Eliminado",Toast.LENGTH_SHORT).show();

                    }
                });
                break;


        }
    }





 /*   public void loadUsers() {
        FirebaseUser user	=	mAuth.getCurrentUser();
        myRef = database.getReference(PATH_USERS + user.getUid());
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    myUser = new Usuarios();
                    myUser = singleSnapshot.getValue(Usuarios.class);
                    ejemploLista.add(myUser.getNombre().toString());
                }            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error	en	la	consulta", databaseError.toException());
            }
        });

    }*/
    public void loadUsers() {
        FirebaseUser user	=	mAuth.getCurrentUser();
        myRef = database.getReference(PATH_USERS + user.getUid() + "/Amigos");
        ejemploLista.clear();
        myRef.addValueEventListener(new	ValueEventListener()/*; addValueEventListener(new	ValueEventListener()*/	{
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    myUser = new Usuarios();
                    myUser = singleSnapshot.getValue(Usuarios.class);
                    ejemploLista.add(myUser.getNombre());
                }
                adaptador = new ArrayAdapter<String>(Amigo.this,android.R.layout.simple_list_item_1,ejemploLista);
                lista.setAdapter(adaptador);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error	en	la	consulta", databaseError.toException());
            }
        });
    }
    private void registros(String nombre)
    {
        mProgress.setMessage("Guardando Amigo... Espere por favor...");
        mProgress.show();



        FirebaseUser user	=	mAuth.getCurrentUser();
        if(user!=null){	//Update	user	Info
            DatabaseReference dbRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child(PATH_USERS + user.getUid());
            dbRef.child("Amigos").push().setValue(nombre);

            //myRef.child("Amigos").setValue(nombre);

            myRef = database.getReference("message");
            myRef.setValue("Amigo guardado!");


        }
    }
    private void eliminar(String nombre)
    {
        mProgress.setMessage("Eliminando Amigo... Espere por favor...");
        mProgress.show();



        FirebaseUser user	=	mAuth.getCurrentUser();
        if(user!=null){	//Update	user	Info
            DatabaseReference dbRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child(PATH_USERS + user.getUid() + "/Amigos" );



            //dbRef.child("nombre").equalTo(nombre).getRef().setValue(null);

            dbRef.orderByChild("nombre").equalTo(nombre). addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        //Log.d("User key", child.getKey());
                        child.getRef().removeValue();// setValue(null)toString());
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