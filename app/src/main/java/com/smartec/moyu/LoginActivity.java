package com.smartec.moyu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.smartec.moyu.register.Registro;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;
    ProgressBar loading_mail;

    SignInButton login_google;
    Button login_mail;

    GoogleApiClient googleApiClient;

    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Instaciación de layout elements
        email = findViewById(R.id.username_login);
        pass = findViewById(R.id.password_login);
        loading_mail = findViewById(R.id.loading_mail);
        login_google = findViewById(R.id.login_google);
        login_mail = findViewById(R.id.login_mail);
        //AJUSTES DE GOOGLE API
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Instanciación de Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(LoginActivity.this, "Has Iniciado Sesión", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Por favor, Inicia Sesión", Toast.LENGTH_SHORT).show();
                }
            }
        };
        //===================== ACTION BUTTONS ================================
        login_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    email.setError("Falta ingresar tu correo electrónico");
                    email.requestFocus();
                } else if(pass.getText().toString().isEmpty()){
                    pass.setError("Falta ingresar tu correo electrónico");
                    pass.requestFocus();
                } else {
                    login_mail.setVisibility(View.GONE);
                    loading_mail.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión, intente más tarde", Toast.LENGTH_SHORT).show();
                                        login_mail.setVisibility(View.VISIBLE);
                                        loading_mail.setVisibility(View.GONE);
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                }
                            });
                }
            }
        });
        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_google.setVisibility(View.GONE);
                loading_mail.setVisibility(View.VISIBLE);
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
                login_google.setVisibility(View.VISIBLE);
                loading_mail.setVisibility(View.GONE);
            }
        });
        //====================================================================
    }



    public void onClickLogin(View view){
                Intent intent = new Intent(LoginActivity.this, Registro.class);
                startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Ha ocurrido un error con la conexión, intenta más tarde", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            firebaseAuthWithGoogle(result.getSignInAccount());
        }else{
            Toast.makeText(this, "Error obteniendo cuenta", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        loading_mail.setVisibility(View.VISIBLE);
        login_mail.setEnabled(false);
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading_mail.setVisibility(View.INVISIBLE);
                login_mail.setEnabled(true);
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
