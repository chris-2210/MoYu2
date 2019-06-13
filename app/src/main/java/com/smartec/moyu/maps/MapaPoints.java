package com.smartec.moyu.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartec.moyu.DialogFragment;
import com.smartec.moyu.LoginActivity;
import com.smartec.moyu.MainActivity;
import com.smartec.moyu.R;
import com.smartec.moyu.models.Parada;
import com.smartec.moyu.models.Ruta;
import com.smartec.moyu.models.Utilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MapaPoints extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    //VARIABLES DE FIREBASE
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    int i = 0;

    private GoogleMap mMap;
    Ruta ruta;
    Parada[] paradas = new Parada[5];
    Bundle datos;
    double inicioLat;
    double inicioLng;
    double finLat;
    double finLng;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_points);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        //
        datos = getIntent().getExtras();
        inicioLat = datos.getDouble("inicioLat");
        inicioLng = datos.getDouble("inicioLng");
        finLat = datos.getDouble("finLat");
        finLng = datos.getDouble("finLng");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPoints);
        mapFragment.getMapAsync(this);
        ruta = new Ruta();
        ruta.setuID(UUID.randomUUID().toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /////////////
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        // recorriendo todas las rutas
        for(int i=0;i<Utilidades.routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();
            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);
            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }
            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(4);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Dibujamos las Polilineas en el Google Map para cada ruta
        if(lineOptions == null){
            Toast.makeText(this,"La ruta no se ha podido trazar", Toast.LENGTH_LONG).show();
        }else{
            mMap.addPolyline(lineOptions);
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        LatLng inicio = new LatLng(inicioLat, inicioLng);
        LatLng fin = new LatLng(finLat, finLng);
        mMap.addMarker(new MarkerOptions().position(inicio).title("Inicio de Ruta").icon(BitmapDescriptorFactory.fromResource(R.drawable.plus)));
        mMap.addMarker(new MarkerOptions().position(fin).title("Fin de Ruta").icon(BitmapDescriptorFactory.fromResource(R.drawable.minus)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inicio, 14));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.point)).draggable(true));
        mMap.getProjection().toScreenLocation(latLng);
        if(i > 5){
            Toast.makeText(this, "Sólo puedes seleccionar hasta 5 puntos", Toast.LENGTH_LONG).show();
        } else {
            paradas[i] = new Parada(UUID.randomUUID().toString(), latLng.latitude, latLng.longitude);
            Log.e("\n\n\n\nTHIS\n\n\n", paradas[i].toString() + " - I = "+ i);
            Toast.makeText(this, "Nuevo punto marcado", Toast.LENGTH_SHORT).show();
        }
        i++;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this,"Moviendo marcador seleccionado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        for(int i=0;i<6;i++){
            if(marker.getPosition().latitude == paradas[i].getLat() && marker.getPosition().longitude == paradas[i].getLng()){
                paradas[i].setLat(marker.getPosition().latitude);
                paradas[i].setLng(marker.getPosition().longitude);
            }
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this,"Marcador suelto", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if((marker.getPosition().latitude == inicioLat && marker.getPosition().longitude == inicioLng) ||
                (marker.getPosition().latitude == finLat && marker.getPosition().longitude == finLng)){
            Toast.makeText(this, "El marcador no se puede eliminar", Toast.LENGTH_LONG).show();
        } else {
            for(int i=0;i<paradas.length ;i++){
                if(marker.getPosition().latitude == paradas[i].getLat() && marker.getPosition().longitude == paradas[i].getLng()){
                    paradas[i] = null;
                    marker.remove();
                    Toast.makeText(this, "Marcador removido", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        return true;
    }

    public void onClickFinish(View view){
        databaseReference.child("Conductores").child(firebaseUser.getUid()).child("Rutas").child(ruta.getuID()).setValue(ruta);
        generateRoute();
    }

    public void generateRoute(){
        Parada inicio = new Parada(UUID.randomUUID().toString(),inicioLat,inicioLng);
        Parada fin = new Parada(UUID.randomUUID().toString(), finLat,finLng);
        databaseReference.child("Conductores").child(firebaseUser.getUid()).child("Rutas").child(ruta.getuID()).child("Inicio").setValue(inicio);
        databaseReference.child("Conductores").child(firebaseUser.getUid()).child("Rutas").child(ruta.getuID()).child("Fin").setValue(inicio);
        insertarParadas();
    }

    private void insertarParadas() {
        for(int i=0;i<paradas.length;i++){
            if(paradas[i] != null){
                databaseReference.child("Conductores")
                        .child(firebaseUser.getUid())
                        .child("Rutas")
                        .child(ruta.getuID())
                        .child("Paradas")
                        .child(paradas[i].getuID())
                        .setValue(paradas[i]);
            } else {
                break;
            }
        }
        startActivity(new Intent(this, MainActivity.class));
    }


    /*public PolylineOptions getRoute() {


        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        for(int i=0;i<Utilidades.routes.size();i++){
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = Utilidades.routes.get(i);
            for(int j=0;j<path.size();j++){
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                if(center == null){
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }
            lineOptions.addAll(points);
            lineOptions.width(3);
            lineOptions.color(Color.BLUE);
        }
        return lineOptions;
    }*/

}
