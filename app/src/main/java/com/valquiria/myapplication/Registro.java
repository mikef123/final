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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private StorageReference mStorageRef;
    private Uri imageUri;
    private UserProfileChangeRequest.Builder upcrb = null;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    FirebaseDatabase database;
    DatabaseReference myRef;
    public	static	final	String	PATH_USERS="users/";
    Usuarios myUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=	FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        String objeto = getIntent().getExtras().getString("correo");
        String objeto1 = getIntent().getExtras().getString("contraseña");
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
                        imageUri = data.getData();
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
                myRef=database.getReference("message");
                myRef.setValue("Usuario Registrado!");
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
                        upcrb = new UserProfileChangeRequest.Builder();
                        upcrb.setDisplayName(nombre.getText().toString().toUpperCase());
                        if(task.isSuccessful()){

                            Log.d(TAG,	"createUserWithEmail:onComplete:"	+	task.isSuccessful());
                            FirebaseUser user	=	mAuth.getCurrentUser();
                            if(user!=null){	//Update	user	Info
                                final UserProfileChangeRequest.Builder upcrb =	new	UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(nombre.getText().toString()+"	 "+apellido.getText().toString());
                                upcrb.setPhotoUri(Uri.parse("path/to/pic"));//fake	 uri,	real	one	coming	soon
                                user.updateProfile(upcrb.build());
                                uploadFile();
                                Usuarios myUser =	new	Usuarios();
                                myUser.setNombre(nombre.getText().toString());
                                myUser.setApellido(apellido.getText().toString());
                                myUser.setCorreo(correo.getText().toString());
                                myUser.setContraseña(contraseña.getText().toString());
                                myUser.setTipo("Persona");
                                myRef=database.getReference(PATH_USERS+user.getUid());
                                final Uri file = imageUri;

                                upcrb.setDisplayName(nombre.getText().toString().toUpperCase());
                                StorageReference riversRef = mStorageRef.child(mAuth.getCurrentUser().getUid());
                                riversRef.putFile(file)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                upcrb.setPhotoUri(imageUri);
                                               //Toast.makeText(RegistroActivity.this, "Correcto subir imagen", Toast.LENGTH_SHORT).show();
                                               // avanzar(upcrb);
                                               // mProgressDialog.dismiss();
                                                imageUri = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                imageUri = null;
                                                upcrb.setPhotoUri(imageUri);
                                                //Toast.makeText(RegistroActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                //Toast.makeText(RegistroActivity.this, "Seleccione una imagen de perfil", Toast.LENGTH_SHORT).show();
                                //avanzar(upcrb);
                            }
                                myRef.setValue(myUser);
                                startActivity(new	Intent(Registro.this,	Principal.class));	//o		en	el	listener

                        }

                    }
                });


    }
    private void uploadFile() {
        //if there is a file to upload
        if (imageUri != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
           // progressDialog.setTitle("Uploading");
            //progressDialog.show();

            StorageReference riversRef = storageReference.child("images/" + nombre + " " + apellido + ".jpg");
            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                          //  progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.menuLogOut) {
            mAuth.signOut();
            Intent intent = new Intent(Registro.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
