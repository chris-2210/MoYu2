package com.smartec.moyu.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartec.moyu.MainActivity;
import com.smartec.moyu.R;
import com.smartec.moyu.models.Conductor;
import com.smartec.moyu.models.Parada;
import com.smartec.moyu.models.Vehicle;

import java.util.UUID;

public class Registro extends AppCompatActivity implements RegistroGenerales.OnFragmentInteractionListener, RegistroVehiculo.OnFragmentInteractionListener, RegistroPago.OnFragmentInteractionListener {

    private int MY_PERMISSION;
    //VARIABLES DE FIREBASE
    FirebaseAuth firebaseAuth;
    FirebaseDatabase fireData;
    DatabaseReference dataRef;
    FirebaseUser firebaseUser;

    //VARIABLES DE FRAGMENTS
    Fragment fg, fv, fp;

    //VARIABLES PARA LA CREACIÓN DEL CONDUCTOR
    Conductor conductor;
    Vehicle vehiculo;
    Parada ubicacion;

    //DECLARACION DE VARIABLES DE DATOS GENERALES
    EditText nombre;
    EditText app;
    EditText apm;
    EditText edad;
    RadioGroup sexo;
    RadioButton h;
    RadioButton m;
    EditText email;
    EditText password;
    EditText matricula;
    EditText paypal;
    Switch disponible;
    ProgressBar loading_acount;
    Button finish_user;
    //DECLARACION DE VARIABLES DE DATOS DEL VEHICULO
    EditText marca;
    EditText modelo;
    EditText color;
    EditText nAcientos;
    EditText nSeguro;

    //VARIABLES DE LA UBICACION
    FusedLocationProviderClient fusedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        conductor = new Conductor();
        ubicacion = new Parada();
        ubicacion.setuID(UUID.randomUUID().toString());
        initFire();
        fg = new RegistroGenerales();
        fv = new RegistroVehiculo();
        fp = new RegistroPago();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedor, fg).commit();
    }

    private void initFire() {
        //FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fireData = FirebaseDatabase.getInstance();
        dataRef = fireData.getReference();
    }

    public void myLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION);
            return;
        } else {
            fusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null){
                        ubicacion.setLat(17.08725992745382);
                        ubicacion.setLng(-96.74597728997469);
                        Toast.makeText(Registro.this, "Activar Permisos de Ubicación", Toast.LENGTH_LONG).show();
                    }else{
                        ubicacion.setLat(location.getLatitude());
                        ubicacion.setLng(location.getLongitude());
                    }
                }
            });
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onClick(View view){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.nextGenerales: {
                initGenerales();
                if (nombre.getText().toString().isEmpty() || app.getText().toString().isEmpty()
                        || apm.getText().toString().isEmpty() || edad.getText().toString().isEmpty()
                        || email.getText().toString().isEmpty()
                        || password.getText().toString().isEmpty()) {
                    validacionGen();
                } else {
                    buildConductor();
                    ft.replace(R.id.contenedor, fv).addToBackStack(null);
                    vehiculo = new Vehicle();
                    Toast.makeText(this, conductor.toString(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.nextVehiculo: {
                initVehiculo();
                if (marca.getText().toString().isEmpty() || modelo.getText().toString().isEmpty()
                        || color.getText().toString().isEmpty() || nAcientos.getText().toString().isEmpty()
                        || nSeguro.getText().toString().isEmpty()) {
                    validacionVeh();
                } else {
                    buildVehiculo();
                    ft.replace(R.id.contenedor, fp).addToBackStack(null);
                    Toast.makeText(this, vehiculo.toString(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.finishReg:{
                loading_acount = findViewById(R.id.loading_acount);
                finish_user = findViewById(R.id.finishReg);
                paypal = findViewById(R.id.paypal);
                disponible = findViewById(R.id.disponible);
                if (paypal.getText().toString().isEmpty()) {
                    paypal.setError("Indica tu cuenta de PayPal");
                    paypal.requestFocus();
                } else {
                    conductor.setPaypal(paypal.getText().toString());
                    conductor.setDisponible(disponible.isChecked());
                    loading_acount.setVisibility(View.VISIBLE);
                    finish_user.setVisibility(View.GONE);
                    myLocation();
                    storeAuthFirebase();
                }
            }
            break;
        }
        ft.commit();
    }

    public void storeAuthFirebase(){
        firebaseAuth.createUserWithEmailAndPassword(conductor.getEmail(), conductor.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Registro.this, "El registro de usuario ha fallado", Toast.LENGTH_LONG).show();
                            loading_acount.setVisibility(View.INVISIBLE);
                            finish_user.setVisibility(View.VISIBLE);
                        }else{
                            firebaseUser = firebaseAuth.getCurrentUser();
                            conductor.setuID(firebaseUser.getUid());
                            storeDatabase();
                        }
                    }
                });
    }

    public void storeDatabase(){
        dataRef.child("Conductores").child(conductor.getuID()).setValue(conductor).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    storeVehiclePosition();
                } else {
                    Toast.makeText(Registro.this, "Error al almacenar los datos del conductor", Toast.LENGTH_LONG).show();
                    loading_acount.setVisibility(View.GONE);
                    finish_user.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void storeVehiclePosition(){
        dataRef.child("Conductores").child(conductor.getuID()).child("Vehiculo").child(vehiculo.getuID()).setValue(vehiculo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Registro.this, "Chido", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Registro.this, "No se insertó vehículo", Toast.LENGTH_LONG).show();
                }
            }
        });
        dataRef.child("Conductores").child(conductor.getuID()).child("UbicacionActual").child(ubicacion.getuID()).setValue(ubicacion).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Registro.this, MainActivity.class));
                }else{
                    Toast.makeText(Registro.this, "No se insertó ubicación", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void buildVehiculo() {
        vehiculo.setuID(UUID.randomUUID().toString());
        vehiculo.setMarca(marca.getText().toString());
        vehiculo.setColor(color.getText().toString());
        vehiculo.setModelo(modelo.getText().toString());
        vehiculo.setnAcientos(Integer.valueOf(nAcientos.getText().toString()));
        vehiculo.setnSeguro(nSeguro.getText().toString());
        vehiculo.setMatricula(matricula.getText().toString());
    }

    private void initVehiculo() {
        marca = findViewById(R.id.marca);
        modelo = findViewById(R.id.modelo);
        color = findViewById(R.id.color);
        nAcientos = findViewById(R.id.nAcientos);
        nSeguro = findViewById(R.id.nSeguro);
        matricula = findViewById(R.id.matricula);
    }

    private void buildConductor() {
        conductor.setNombre(nombre.getText().toString());
        conductor.setApp(app.getText().toString());
        conductor.setApm(apm.getText().toString());
        conductor.setEdad(edad.getText().toString());
        if(h.isChecked()){
            conductor.setSexo("H");
        } else {
            conductor.setSexo("M");
        }
        conductor.setEmail(email.getText().toString());
        conductor.setPassword(password.getText().toString());
    }

    private void initGenerales() {
        nombre = findViewById(R.id.nombre);
        app = findViewById(R.id.app);
        apm = findViewById(R.id.apm);
        edad = findViewById(R.id.edad);
        sexo = findViewById(R.id.sexo);
        h = findViewById(R.id.hombre);
        m = findViewById(R.id.mujer);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    private void validacionGen(){
        if(nombre.getText().toString().isEmpty()){
            nombre.setError("Por favor inserte su nombre.");
            nombre.requestFocus();
        }else if(app.getText().toString().isEmpty()){
            app.setError("Por favor inserte su apellido paterno.");
            app.requestFocus();
        }else if(apm.getText().toString().isEmpty()){
            apm.setError("Por favor inserte su apellido materno.");
            apm.requestFocus();
        }else if(edad.getText().toString().isEmpty()){
            edad.setError("Por favor inserte su edad.");
            edad.requestFocus();
        }else if(email.getText().toString().isEmpty()) {
            email.setError("Por favor coloque su correo electrónico.");
            email.requestFocus();
        }else if(password.getText().toString().isEmpty()) {
            password.setError("Por favor coloque su contraseña.");
            password.requestFocus();
        }
    }

    private void validacionVeh(){
        if(marca.getText().toString().isEmpty()){
            marca.setError("Por favor inserte la marca.");
            marca.requestFocus();
        }else if(modelo.getText().toString().isEmpty()){
            modelo.setError("Por favor inserte el modelo.");
            modelo.requestFocus();
        }else if(color.getText().toString().isEmpty()){
            color.setError("Por favor inserte el color.");
            color.requestFocus();
        }else if(nAcientos.getText().toString().isEmpty()){
            nAcientos.setError("Por favor inserte el Num. de acientos.");
            nAcientos.requestFocus();
        }else if(nSeguro.getText().toString().isEmpty()) {
            nSeguro.setError("Por favor coloque su Num. de seguro.");
            nSeguro.requestFocus();
        }
    }

}
