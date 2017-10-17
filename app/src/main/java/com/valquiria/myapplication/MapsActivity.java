package com.valquiria.myapplication;

import android.*;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText nombre;
    EditText correo;
    String la;
    String lo;
    private static final int localizacion = 3;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    public static final double lowerLeftLatitude = 4.483899;
    public static final double lowerLeftLongitude= -74.098075;
    public static final double upperRightLatitude= 4.836415;
    public static final double upperRigthLongitude= -74.063224;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mAuth = FirebaseAuth.getInstance();
        nombre = (EditText) findViewById(R.id.nombre);
        correo = (EditText) findViewById(R.id.correo);
        FirebaseUser user	=	mAuth.getCurrentUser();
        nombre.setText(user.getDisplayName());
        correo.setText(user.getEmail());
        mapFragment.getMapAsync(this);
    }

    protected	LocationRequest createLocationRequest()	 {
        LocationRequest mLocationRequest =	new	LocationRequest();
        mLocationRequest.setInterval(10000);	 //tasa de	refresco en	milisegundos
        mLocationRequest.setFastestInterval(5000);	 //máxima tasa de	refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return	mLocationRequest;
    }
    public void check()
    {

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
                            resolvable.startResolutionForResult(MapsActivity.this,
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
                android.Manifest.permission.ACCESS_FINE_LOCATION)	 ==
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
        //mMap.setMapStyle(MapStyleOptions
        //      .loadRawResourceStyle(this, R.raw.style_json));
        // Add a marker in Sydney and move the camera
        LatLng lugar2 = new LatLng(4.6475, -74.1014);
        Marker bogotaAzul = mMap.addMarker(new MarkerOptions().position(lugar2).title("bici Gran Estación").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bike))
                .snippet("Sabados y Domingos : 8 am - 2pm") //Texto de información
                .alpha(0.5f)); //Transparencia);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar2));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

   /*     LatLng yo = new LatLng(Float.valueOf(la), Float.valueOf(lo));
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
*/
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
            Intent intent	=	new	Intent(MapsActivity.this,	MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else	if	(itemClicked ==	R.id.menuSettings){
//Abrir actividad para	configuración etc
        }
        return	super.onOptionsItemSelected(item);
    }
}
