package com.smartec.moyu.maps;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartec.moyu.R;
import com.smartec.moyu.models.Parada;
import com.smartec.moyu.models.Ruta;

import java.util.UUID;

public class MapaInit extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    public Parada inicio;
    public String hraInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_init);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapInit);
        mapFragment.getMapAsync(this);
        inicio  = new Parada();
        inicio.setuID(UUID.randomUUID().toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this,"Moviendo marcador seleccionado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        inicio.setLat(marker.getPosition().latitude);
        inicio.setLng(marker.getPosition().longitude);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Inicio de Ruta").icon(BitmapDescriptorFactory.fromResource(R.drawable.plus)).draggable(true));
        mMap.getProjection().toScreenLocation(latLng);
        inicio.setLat(latLng.latitude);
        inicio.setLng(latLng.longitude);
        Toast.makeText(this, "Nuevo punto marcado", Toast.LENGTH_SHORT).show();
    }

    public void onClickInit(View view){
        if(inicio.getLat() == 0 || inicio.getLng()==0){
            Toast.makeText(this, "Por favor, Marca un punto de inicio para tu ruta", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getApplication(), MapaEnd.class);
            intent.putExtra("inicioLat", inicio.getLat());
            intent.putExtra("inicioLng", inicio.getLng());
            intent.putExtra("hraInit", hraInit);
            startActivity(intent);
        }
    }
}
