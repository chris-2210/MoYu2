package com.smartec.moyu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.CountDownTimer;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.smartec.moyu.fragments.MiPerfil;
import com.smartec.moyu.fragments.MiVehiculo;
import com.smartec.moyu.fragments.MisPagos;
import com.smartec.moyu.fragments.MisRutas;
import com.smartec.moyu.maps.MapaInit;
import com.smartec.moyu.maps.ShowMapActivity;
import com.smartec.moyu.models.Conductor;
import com.smartec.moyu.models.Pago;
import com.smartec.moyu.models.Parada;
import com.smartec.moyu.models.Ruta;
import com.smartec.moyu.register.Registro;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MiPerfil.OnFragmentInteractionListener,
        MisPagos.OnFragmentInteractionListener,
        MiVehiculo.OnFragmentInteractionListener,
        MisRutas.OnFragmentInteractionListener {

    //private List<Pago> pagos_list = new ArrayList<>();
    //ArrayAdapter<Pago> adapterPago;

    //private List<Ruta> rutas_list = new ArrayList<>();
    //ArrayAdapter<Ruta> adapterRuta;
    private int MY_PERMISSION;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    String UUID;
    Parada ubicacion;
    Conductor conductor;
    ListView pagos_list_layout;
    ListView rutas_list_layout;
    Ruta rutaSelect;

    //VARIABLES DE LA UBICACION
    FusedLocationProviderClient fusedLocation;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        //==============================
        //pagos_list_layout = findViewById(R.id.pagos_list);
        //rutas_list_layout = findViewById(R.id.rutas_list);
        //FIREBASE CONFIGURATION
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    UUID = firebaseUser.getUid();
                    //getUserDataFromFirebase(firebaseUser.getUid());
                }
            }
        };
        //SETEO DE DATOS A LOS ELEMENTOS EN LOS FRAGMENTS

        /*===================AÑADIENDO EVENTOS A ITEMS DE LAS RUTAS ==================
        rutas_list_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                rutaSelect = (Ruta) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, ShowMapActivity.class);
                intent.putExtra("ruta", (Parcelable) rutaSelect);
                startActivity(intent);
            }
        });

        //============================================================================*/
        // OPTION FROM TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //OPTIONS FLOATING BUTTON
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapaInit.class);
                startActivity(intent);
            }
        });
        //DRAWER LAYOUT WITH ACTION BAR
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void myLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION);
            return;
        } else {
            fusedLocation.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        ubicacion.setLat(location.getLatitude());
                        ubicacion.setLng(location.getLongitude());
                        databaseReference.child("Conductores")
                                .child(UUID)
                                .child("UbicacionActual")
                                .setValue(ubicacion);
                        countDownTimer();
                    } else {
                        Toast.makeText(MainActivity.this, "Activar Permisos de Ubicación", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void countDownTimer(){
        new CountDownTimer(30000, 1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                myLocation();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            DialogFragment.newInstance("MoYu", getString(R.string.about)).show(getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        Fragment fragment = null;
        boolean isSelected = false;
        if (id == R.id.routes) {
            fragment = new MisRutas();
            isSelected = true;
        } else if (id == R.id.my_money) {
            fragment = new MisPagos();
            isSelected = true;
        } else if (id == R.id.my_profile) {
            fragment = new MiPerfil();
            isSelected = true;
        } else if (id == R.id.close_session) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(isSelected){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
/*
    private void getUserDataFromFirebase(String UUID){
        conductor = new Conductor();
        databaseReference.child("Conductore").child(UUID);

    }

    private void getVehicleDataFromFirebase(){};

    private void getPaysDataFromFirebase(String UUID){
        databaseReference.child("Conductores").child(UUID).child("Pagos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pagos_list.clear();
                for (DataSnapshot objSnap : dataSnapshot.getChildren()){
                    Pago p = objSnap.getValue(Pago.class);
                    pagos_list.add(p);
                    adapterPago = new ArrayAdapter<Pago>(MainActivity.this, android.R.layout.simple_list_item_1);
                    pagos_list_layout.setAdapter(adapterPago);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRoutesDataFromFirebase(String UUID){
        databaseReference.child("Conductores").child(UUID).child("Rutas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rutas_list.clear();
                for (DataSnapshot objSnap : dataSnapshot.getChildren()){
                    Ruta r = objSnap.getValue(Ruta.class);
                    rutas_list.add(r);
                    adapterRuta = new ArrayAdapter<Ruta>(MainActivity.this, android.R.layout.simple_list_item_1);
                    rutas_list_layout.setAdapter(adapterRuta);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

}
