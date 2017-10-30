package com.valquiria.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ModificarUsuario extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    EditText nombre;
    EditText apellido;
    EditText correo;
    EditText contraseña;
    ImageView image;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private StorageReference mStorageRef;
    private Uri imageUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    public static final String PATH_USERS = "users/";
    Usuarios myUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contraseña);
        findViewById(R.id.guardar).setOnClickListener(this);
        findViewById(R.id.cancelar).setOnClickListener(this);
        mProgress = new ProgressDialog(this);
        loadUsers();



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guardar:
                registros();
                myRef = database.getReference("message");
                myRef.setValue("Usuario Modificado!");

                break;
            case R.id.cancelar:
                startActivity(new Intent(ModificarUsuario.this, Principal.class));    //o		en	el	listener


        }
    }


    private void registros() {
        mProgress.setMessage("Modificando... Espere por favor...");
        mProgress.show();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {    //Update	user	Info
            UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
            upcrb.setDisplayName(nombre.getText().toString() + "	 " + apellido.getText().toString());
            upcrb.setPhotoUri(Uri.parse("path/to/pic"));//fake	 uri,	real	one	coming	soon
            user.updateProfile(upcrb.build());
            Usuarios myUser = new Usuarios();
            myUser.setNombre(nombre.getText().toString());
            myUser.setApellido(apellido.getText().toString());
            myUser.setCorreo(correo.getText().toString());
            myUser.setContraseña(contraseña.getText().toString());
            myRef = database.getReference(PATH_USERS + user.getUid());
            myRef.setValue(myUser);
            startActivity(new Intent(ModificarUsuario.this, Principal.class));    //o		en	el	listener

        }

    }
    public void loadUsers() {
        myRef = database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    myUser = new Usuarios();
                    myUser = singleSnapshot.getValue(Usuarios.class);
                    nombre.setText(myUser.getNombre());
                    apellido.setText(myUser.getApellido());
                    correo.setText(myUser.getCorreo());
                    contraseña.setText(myUser.getContraseña());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error	en	la	consulta", databaseError.toException());
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
            Intent intent = new Intent(ModificarUsuario.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (itemClicked == R.id.amigos) {
            Intent intent = new Intent(ModificarUsuario.this, Usuario.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//Abrir actividad para	configuración etc
        }
        return super.onOptionsItemSelected(item);
    }
}

