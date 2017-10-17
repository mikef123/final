package com.valquiria.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Registro extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();
    EditText nombre;
    EditText apellido;
    EditText correo;
    EditText contraseña;
    ImageView image;
    Button boton1;
    Button boton2;
    Button boton3;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private String name = "";
    String mCurrentPhotoPath;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        String objeto = (String) getIntent().getExtras().getString("correo");
        String objeto1 = (String) getIntent().getExtras().getString("contraseña");
        setContentView(R.layout.activity_registro);
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contraseña);
        correo.setText(objeto);
        contraseña.setText(objeto1);
        image = (ImageView) findViewById(R.id.imageView);
        boton1 = (Button) findViewById(R.id.button1);
        boton2 = (Button) findViewById(R.id.button2);
        findViewById(R.id.button3).setOnClickListener(this);
        mProgress = new ProgressDialog(this);
        boton1.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                Intent pickImage = new Intent(Intent.ACTION_PICK);
                pickImage.setType("image/*");
                startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                takePicture();

            }
        });




    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        image.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            case REQUEST_IMAGE_CAPTURE:

                if (requestCode == REQUEST_IMAGE_CAPTURE ) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);
                    Toast.makeText(Registro.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                }

        }

    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button3:
                registros(correo.getText().toString(), contraseña.getText().toString());
                break;


        }
    }

    private void registros(String email, String password)
    {
        mProgress.setMessage("Registrando... Espere por favor...");
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(email,	 password)
                .addOnCompleteListener(this,	 new	OnCompleteListener<AuthResult>()	 {

                    @Override
                    public	void	onComplete(@NonNull Task<AuthResult> task)	{
                        mProgress.dismiss();
                        if(task.isSuccessful()){

                            Log.d(TAG,	"createUserWithEmail:onComplete:"	+	task.isSuccessful());
                            FirebaseUser user	=	mAuth.getCurrentUser();
                            if(user!=null){	//Update	user	Info
                                UserProfileChangeRequest.Builder upcrb =	new	UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(nombre.getText().toString()+"	 "+apellido.getText().toString());
                                upcrb.setPhotoUri(Uri.parse("path/to/pic"));//fake	 uri,	real	one	coming	soon
                                user.updateProfile(upcrb.build());
                                startActivity(new	Intent(Registro.this,	Principal.class));	//o		en	el	listener
                            }
                        }
                        if	(!task.isSuccessful())	 {
                            Toast.makeText(Registro.this,		R.string.auth_failed+	task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG,	task.getException().getMessage());
                        }
                    }
                });
    }
}
