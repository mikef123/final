package com.valquiria.myapplication;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.name;

public class Principal extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText mAddress;
    EditText mAddress1;
    TextView kilometros;
    String la;
    String lo;
    Double latitud;
    Double longitud;
    String nombres;
    String id;
    private static final int localizacion = 3;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    public static final double lowerLeftLatitude = 4.483899;
    public static final double lowerLeftLongitude = -74.098075;
    public static final double upperRightLatitude = 4.836415;
    public static final double upperRigthLongitude = -74.063224;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_LOCATION = "locations/";
    public static final String PATH_USERS = "users/";
    public static final String PATH_RECORRIDOS = "recorridos/";
    Localizacion localiza;
    private ProgressDialog mProgress;
    public	final	static	double	RADIUS_OF_EARTH_KM	 =	6371;
   Spinner spinner;
    String tiempo;
    String[] letra = {" ", "Nublado","Soleado","Lluvioso","Despejado",};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        findViewById(R.id.guardar).setOnClickListener(this);
        findViewById(R.id.recorrido).setOnClickListener(this);
        findViewById(R.id.etPlannedDate).setOnClickListener(this);
        kilometros = (TextView) findViewById(R.id.kilometros);
        mProgress = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        /*nombre.setText(user.getDisplayName());
        correo.setText(user.getEmail());*/
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        check();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location	update	in	the	callback:	" + location);
                if (location != null) {
                    la=String.valueOf(location.getLatitude());
                    lo=String.valueOf(location.getLongitude());

                }
            }
        };
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,	 new
                OnSuccessListener<Location>()	 {
                    @Override
                    public	void	onSuccess(Location	 location)	 {
                        Log.i("LOCATION",	 "onSuccess location");
                        if	(location	 !=	null)	{
                            la=String.valueOf(location.getLatitude());
                            lo=String.valueOf(location.getLongitude());
                        }
                    }
                    });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
               tiempo=(String) adapterView.getItemAtPosition(pos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.guardar:
                registros();
                break;
            case R.id.recorrido:
                registrosRecorrido();
                break;


        }
    }



    private void registros() {
        mProgress.setMessage("Guardado Ruta... Espere por favor...");
        mProgress.show();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {    //Update	user	Info
            UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
            Usuarios myUser = new Usuarios();
            mAddress = (EditText) findViewById(R.id.texto);
            mAddress1 = (EditText) findViewById(R.id.texto1);
            Ruta ruta = new Ruta();
            Localizacion local =  new Localizacion();
            Localizacion local1= new Localizacion();
            String addressString = mAddress.getText().toString();
            String addressString1 = mAddress1.getText().toString();
            final Geocoder mGeocoder = new Geocoder(getBaseContext());
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                List<Address> addresses1 = mGeocoder.getFromLocationName(
                        addressString1, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                Address addressResult = addresses.get(0);
                Address addressResult1 = addresses1.get(0);
                //---------------------------
                local.setLatitude(addressResult.getLatitude());
                local.setLongitude(addressResult.getLongitude());
                local1.setLatitude(addressResult1.getLatitude());
                local1.setLongitude(addressResult1.getLongitude());
                Double dis = distance(addressResult.getLatitude(),addressResult.getLongitude(),addressResult1.getLatitude(),addressResult1.getLongitude());
                ruta.setOrigen(local);
                ruta.setDestino(local1);
                ruta.setDistancia(dis);
                ruta.setFecha(Calendar.getInstance().getTime());
                ruta.setTiempo(tiempo);
                myRef = database.getReference(PATH_USERS + user.getUid());
                myRef.child("ruta").push().setValue(ruta);


                Toast.makeText(Principal.this, "Ruta guardada",	Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                myRef = database.getReference("message");
                myRef.setValue("Ruta guardada!");
            } catch (IOException e) {
            e.printStackTrace();
        }

        }

    }
    private void registrosRecorrido() {
        mProgress.setMessage("Guardado Ruta... Espere por favor...");
        mProgress.show();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {    //Update	user	Info
            UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
            Usuarios myUser = new Usuarios();
            mAddress = (EditText) findViewById(R.id.texto);
            mAddress1 = (EditText) findViewById(R.id.texto1);
            Ruta ruta = new Ruta();
            Localizacion local =  new Localizacion();
            Localizacion local1= new Localizacion();
            String addressString = mAddress.getText().toString();
            String addressString1 = mAddress1.getText().toString();
            final Geocoder mGeocoder = new Geocoder(getBaseContext());
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                List<Address> addresses1 = mGeocoder.getFromLocationName(
                        addressString1, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                Address addressResult = addresses.get(0);
                Address addressResult1 = addresses1.get(0);
                //---------------------------
                local.setLatitude(addressResult.getLatitude());
                local.setLongitude(addressResult.getLongitude());
                local.setName(addressString);
                local1.setLatitude(addressResult1.getLatitude());
                local1.setLongitude(addressResult1.getLongitude());
                local1.setName(addressString1);
                Double dis = distance(addressResult.getLatitude(),addressResult.getLongitude(),addressResult1.getLatitude(),addressResult1.getLongitude());
                ruta.setOrigen(local);
                ruta.setDestino(local1);
                ruta.setDistancia(dis);
                ruta.setFecha(Calendar.getInstance().getTime());
                ruta.setTiempo(tiempo);
                String nombre = user.getDisplayName();
                ruta.setUsuario(nombre);
                myRef = database.getReference(PATH_RECORRIDOS );
                myRef.child("ruta").push().setValue(ruta);


                Toast.makeText(Principal.this, "Recorrido guardado",	Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                myRef = database.getReference("message");
                myRef.setValue("Ruta guardada!");
                Intent intent = new Intent(Principal.this, Recorrido.class);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);     //tasa de	refresco en	milisegundos
        mLocationRequest.setFastestInterval(5000);     //máxima tasa de	refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void check() {

        LocationSettingsRequest.Builder builder	=	new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client	 =	LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task	=	client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this,	 new	OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public	void	onSuccess(LocationSettingsResponse locationSettingsResponse)	 {
                startLocationUpdates();	 //Todas las condiciones para	recibir localizaciones
            }
        });
        task.addOnFailureListener(this,	 new	OnFailureListener()	 {
            @Override
            public	void	onFailure(@NonNull Exception	 e)	{
                int statusCode =	((ApiException)	e).getStatusCode();
                switch	(statusCode)	{
                    case	CommonStatusCodes.RESOLUTION_REQUIRED:
//	Location	settings	are	not	satisfied,	but	this	can	be	fixed	by	showing	the	user	a	dialog.
                        try	{//	Show	the	dialog	by	calling	startResolutionForResult(),	and	check	the	result	in	onActivityResult().
                            ResolvableApiException resolvable	 =	(ResolvableApiException)	 e;
                            resolvable.startResolutionForResult(Principal.this,
                                    localizacion);
                        }	catch	(IntentSender.SendIntentException sendEx)	{
//	Ignore	the	error.
                        }	break;
                    case	LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//	Location	settings	are	not	satisfied.	No	way	to	fix	the	settings	so	we	won't	show	the	dialog.
                        break;
                }
            }
        });

    }

    protected	void	onActivityResult(int requestCode,	 int resultCode,	 Intent data)	 {
        switch	(requestCode)	 {
            case	localizacion:	 {
                if	(resultCode ==	RESULT_OK)	 {
                    startLocationUpdates();	 	//Se	encendió la	localización!!!
                }	else	{
                    Toast.makeText(this,
                            "Sin	acceso a	localización,	hardware	deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private	void	startLocationUpdates()	 {
        if	(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)	 ==
                PackageManager.PERMISSION_GRANTED)	 {				//Verificación de	permiso!!
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,	 mLocationCallback,
                    null);
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadUsers();
        //mMap.setMapStyle(MapStyleOptions
        //      .loadRawResourceStyle(this, R.raw.style_json));
        // Add a marker in Sydney and move the camera
      /*   LatLng lugar2 = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        Marker bogotaAzul = mMap.addMarker(new MarkerOptions().position(lugar2).title(nombres).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bike))
                .snippet("Sabados y Domingos : 8 am - 2pm") //Texto de información
                .alpha(0.5f)); //Transparencia);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar2));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
*/
      /*
      LatLng yo = new LatLng(Float.valueOf(la), Float.valueOf(lo));
        Marker ubicacion = mMap.addMarker(new MarkerOptions().position(yo).title("YO").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bike))
                .snippet("Aqui estoy yo") //Texto de información
                .alpha(0.5f)); //Transparencia);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(yo));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
/*
        LatLng lugar1 = new LatLng(4.6272, -74.0639);
        Marker javeriana = mMap.addMarker(new MarkerOptions().position(lugar1).title("biciJaveriana").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bike))
                .snippet("Lunes a viernes : 8 am - 5pm") //Texto de información
                .alpha(0.5f)); //Transparencia);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar1));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng lugar3 = new LatLng(4.6028, -74.0648);
        Marker andes = mMap.addMarker(new MarkerOptions().position(lugar3).title("biciAndes").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bike))
                .snippet("Lunes a viernes : 8 am - 5pm") //Texto de información
                .alpha(0.5f)); //Transparencia);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar3));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
*/
        final Geocoder mGeocoder = new Geocoder(getBaseContext());
        mAddress = (EditText) findViewById(R.id.texto);
        mAddress1 = (EditText) findViewById(R.id.texto1);
        //set focus and show keyboard
        mAddress.requestFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mAddress1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == R.id.action_custom || actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager) mAddress1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mAddress1.getWindowToken(), 0);
                    busqueda(mGeocoder);
                    handled = true;
                }
                return handled;
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
            Intent intent = new Intent(Principal.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (itemClicked == R.id.menuNocturno) {
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_json));
//Abrir actividad para	configuración etc
        } else if (itemClicked == R.id.menuSettings) {
            Intent intent = new Intent(Principal.this, ModificarUsuario.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//Abrir actividad para	configuración etc
        }
        else if (itemClicked == R.id.amigos) {
            Intent intent = new Intent(Principal.this, Amigo.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//Abrir actividad para	configuración etc
        }
        return super.onOptionsItemSelected(item);
    }

    public void busqueda(Geocoder mGeocoder) {

        //Cuando se realice la busqueda

        String addressString = mAddress.getText().toString();
        String addressString1 = mAddress1.getText().toString();
        if (!addressString.isEmpty() && !addressString1.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                List<Address> addresses1 = mGeocoder.getFromLocationName(
                        addressString1, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty() && addresses1 != null && !addresses1.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    Address addressResult1 = addresses1.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    LatLng position1 = new LatLng(addressResult1.getLatitude(), addressResult1.getLongitude());
                    if (mMap != null) {
                        Marker javeriana1 = mMap.addMarker(new MarkerOptions().position(position).title("origen").icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.bike))
                                .snippet("inicio") //Texto de información
                                .alpha(0.5f)); //Transparencia);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        MarkerOptions marcadorOrigen = new MarkerOptions();
                        marcadorOrigen.position(position);
                        marcadorOrigen.title("Este es tu origen");
                        //marcadorDestino.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_marcador_destino",80,80)));
                        mMap.addMarker(marcadorOrigen);
                        //-------------------------------------------------------------------------------------------------

                        Marker javeriana = mMap.addMarker(new MarkerOptions().position(position1).title("destino").icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.bike))
                                .snippet("llegada") //Texto de información
                                .alpha(0.5f)); //Transparencia);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position1));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        MarkerOptions marcadorDestino = new MarkerOptions();
                        marcadorDestino.position(position1);
                        marcadorDestino.title("Este es tu destino");
                        //marcadorDestino.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_marcador_destino",80,80)));
                        mMap.addMarker(marcadorDestino);

                        String url = obtenerDireccionesURL(javeriana.getPosition(), position);
                        Principal.DownloadTask downloadTask = new Principal.DownloadTask();
                        downloadTask.execute(url);
                        Double dis = distance(addressResult.getLatitude(),addressResult.getLongitude(),addressResult1.getLatitude(),addressResult1.getLongitude());
                        kilometros.setText(String.valueOf(dis));
                    }
                } else {
                    Toast.makeText(Principal.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Principal.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
        }


    }


    private String obtenerDireccionesURL(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";

        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("ERROR AL OBTENER ", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Principal.ParserTask parserTask = new Principal.ParserTask();

            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Principal.DirectionsJSONParser parser = new Principal.DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.rgb(0, 0, 255));
            }
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    public class DirectionsJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(list.get(l).latitude));
                                hm.put("lng", Double.toString(list.get(l).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.toString();//printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void loadUsers() {
        myRef = database.getReference(PATH_LOCATION);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    localiza = new Localizacion();
                    localiza = singleSnapshot.getValue(Localizacion.class);
                    latitud = localiza.getLatitude();
                    longitud = localiza.getLongitude();
                    nombres = localiza.getName();
                    LatLng lugar2 = new LatLng((latitud), (longitud));
                    Marker bogotaAzul = mMap.addMarker(new MarkerOptions().position(lugar2).title(nombres).icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bike))
                            .snippet("Sabados y Domingos : 8 am - 2pm") //Texto de información
                            .alpha(0.5f)); //Transparencia);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar2));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    //Toast.makeText(Principal.this,	name	+	":"	+	age,	Toast.LENGTH_SHORT).show();
                }
                LatLng yo = new LatLng(Float.valueOf(la), Float.valueOf(lo));
                Marker ubicacion = mMap.addMarker(new MarkerOptions().position(yo).title("YO").icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bike))
                        .snippet("Aqui estoy yo") //Texto de información
                        .alpha(0.5f)); //Transparencia);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yo));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error	en	la	consulta", databaseError.toException());
            }
        });
    }
    public	double	distance(double	 lat1,	double	long1,	double	lat2,	double	long2)	{
        double	latDistance =	Math.toRadians(lat1	 - lat2);
        double	lngDistance =	Math.toRadians(long1	 - long2);
        double	a	=	Math.sin(latDistance /	2)	*	Math.sin(latDistance /	2)
                +	Math.cos(Math.toRadians(lat1))	 *	Math.cos(Math.toRadians(lat2))
                *	Math.sin(lngDistance /	2)	*	Math.sin(lngDistance /	2);
        double	c	=	2	*	Math.atan2(Math.sqrt(a),	 Math.sqrt(1	- a));
        double	result	=	RADIUS_OF_EARTH_KM	 *	c;
        return	Math.round(result*100.0)/100.0;
    }





    }








