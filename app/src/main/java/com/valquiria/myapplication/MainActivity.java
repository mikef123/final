package com.valquiria.myapplication;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    Button login ;
    Button sign;
    Button upc;
    EditText correo;
    EditText contraseña;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public	static	final	String	PATH_USERS="users/";
    Usuarios myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=	FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //login = (Button) findViewById(R.id.login);
        //sign = (Button) findViewById(R.id.sign);
        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contraseña);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.sign).setOnClickListener(this);
        upc = (Button) findViewById(R.id.upc);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }

                updateUI(user);
            }


        };

        upc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Entro");
                Intent intent = new Intent(MainActivity.this, RegistroEmpresa.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	correo.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            correo.setError("Required.");
            valid	=	false;
        }	else	{
            correo.setError(null);
        }
        String	password	=	contraseña.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            contraseña.setError("Required.");
            valid	=	false;
        }	else	{
            contraseña.setError(null);
        }
        return	valid;
    }

    private	boolean isEmailValid(String	 email)	{
        boolean isValid =	true;
        if	(!email.contains("@")	||	!email.contains(".")	||	email.length()	 <	5){
            isValid =	false;
        Toast.makeText(MainActivity.this,"correo Inválido",Toast.LENGTH_SHORT).show();}
        return	isValid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login:
                loadUsers();
                login(correo.getText().toString(), contraseña.getText().toString());

                break;
            case R.id.sign:
                createAccount(correo.getText().toString(), contraseña.getText().toString());
                break;

        }
    }

    private void updateUI (FirebaseUser user)
    {
        if(user != null)
        {
            //findViewById(R.id.sign).setVisibility(View.GONE);
        }
    }

    private void createAccount(String email, String password)
    {
        Log.d(TAG, "createAccount" + email);
        if(!validateForm()|| !isEmailValid(email))
        {
            return;
        }
    else {
        Intent intent = new Intent(MainActivity.this, Registro.class);
        intent.putExtra("correo", correo.getText().toString());
        intent.putExtra("contraseña", contraseña.getText().toString());
        startActivity(intent);

    }
                        }


    private void login (String email, String password)
    {
        Log.d(TAG,"SignIn: " + email);
        if(!validateForm())
        {
            return;
        }

        if(!isEmailValid(email))
        {
            return;
        }
        mAuth.signInWithEmailAndPassword(correo.getText().toString(), contraseña.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            correo.setText("");
                            contraseña.setText("");
                        }
                        else
                        {
                            DatabaseReference myRef2 = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
                            String tipo = myRef2.child("tipo").toString();

                            if(tipo.equalsIgnoreCase("persona")){
                                Intent intent = new Intent(MainActivity.this,Principal.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(MainActivity.this, PrincipalEmpresa.class);
                                startActivity(intent);
                            }
                        }
                    }
                });


    }

    public	void	loadUsers()	{
        myRef =	database.getReference(PATH_USERS);
        myRef. addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    Usuarios myUser =	singleSnapshot.getValue(Usuarios.class);
                    Log.i(TAG,	"Encontró usuario:	"	+	myUser.getNombre());
                    String	name	=	myUser.getNombre();
                    String	lastName	=	myUser.getApellido();
                   // Toast.makeText(MainActivity.this, "Nombre: " +	name	+	"\nApellido: "	+	lastName,	Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });
    }
}

