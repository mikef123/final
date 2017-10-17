package com.valquiria.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Principal extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText nombre;
    EditText correo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);
        mAuth = FirebaseAuth.getInstance();
        nombre = (EditText) findViewById(R.id.nombre);
        correo = (EditText) findViewById(R.id.correo);
        FirebaseUser user	=	mAuth.getCurrentUser();
        nombre.setText(user.getDisplayName());
        correo.setText(user.getEmail());


    }
    @Override
    public	boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,	menu);
        return	true;
    }
    @Override
    public	boolean onOptionsItemSelected(MenuItem item){
        int itemClicked =	item.getItemId();
        if(itemClicked ==	R.id.menuLogOut){
            mAuth.signOut();
            Intent intent	=	new	Intent(Principal.this,	MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else	if	(itemClicked ==	R.id.menuSettings){
//Abrir actividad para	configuración etc
        }
        return	super.onOptionsItemSelected(item);
    }
}
