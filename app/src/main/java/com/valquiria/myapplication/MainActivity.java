package com.valquiria.myapplication;
        import android.*;
        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.facebook.AccessToken;
        import com.facebook.AccessTokenTracker;
        import com.facebook.CallbackManager;
        import com.facebook.FacebookCallback;
        import com.facebook.FacebookException;
        import com.facebook.FacebookSdk;
        import com.facebook.GraphRequest;
        import com.facebook.GraphResponse;
        import com.facebook.Profile;
        import com.facebook.ProfileTracker;
        import com.facebook.login.LoginManager;
        import com.facebook.login.LoginResult;
        import com.facebook.login.widget.LoginButton;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FacebookAuthProvider;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserProfileChangeRequest;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    Button login ;
    Button sign;
    Button upc;
    EditText correo;
    EditText contraseña;

    private LoginButton face;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private UserProfileChangeRequest.Builder upcrb = null;
    private String email = null;
    private int cont = 0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public	static	final	String	PATH_USERS="users/";
    Usuarios myUser;
    private static final int localizacion = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        database=	FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login);
        sign = (Button) findViewById(R.id.sign);
        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contraseña);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
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
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    LoginManager.getInstance().logInWithReadPermissions(
                            MainActivity.this, Arrays.asList("email")
                    );

                    final AccessToken accessToken = loginResult.getAccessToken();
                    final Profile profile = Profile.getCurrentProfile();

                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            final JSONObject json = response.getJSONObject();
                            try {
                                if (json != null) {
                                    // System.out.println("email es: "+json.getString("email"));
                                    email = json.getString("email");
                                    if (profile != null && cont == 0) {// acá ya tiene todo aceptado y entra primera vez
                                        cont ++;
                                        registrar(accessToken);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "email");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

        }
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
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    pedirPermisoLocalizacion();

                } else {

                    loadUsers();
                    login(correo.getText().toString(), contraseña.getText().toString());
                }


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

        Intent intent = new Intent(MainActivity.this, Registro.class);
        intent.putExtra("correo", correo.getText().toString());
        intent.putExtra("contraseña", contraseña.getText().toString());
        startActivity(intent);


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

                            if(tipo.equalsIgnoreCase("empresa")){
                                Intent intent = new Intent(MainActivity.this,PrincipalEmpresa.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(MainActivity.this, Menu.class);
                                startActivity(intent);
                            }
                        }
                    }
                });


    }
    public void pedirPermisoLocalizacion()
    {
        int permissionCheck= ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {

            }
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    localizacion);
        }

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

    private void registrar(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final FirebaseUser userr = mAuth.getCurrentUser();
                myRef.child("users/"+mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.hasChildren()){
                                    final Profile profile = Profile.getCurrentProfile();
                                    upcrb = new UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(profile.getName().toUpperCase());
                                    upcrb.setPhotoUri(null);
                                    userr.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                    userr.updateProfile(upcrb.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Usuarios us = new Usuarios();
                                            us.setNombre(profile.getName().toUpperCase());
                                            us.setCorreo(email);
                                            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                                                    .setValue(us).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(MainActivity.this, "Error guardar datos en servidor", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error registrando con Facebook "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

