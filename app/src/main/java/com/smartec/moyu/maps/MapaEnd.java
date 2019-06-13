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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.smartec.moyu.R;
import com.smartec.moyu.models.Parada;
import com.smartec.moyu.models.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapaEnd extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    public Parada fin;
    Bundle datos;
    public double inicioLat;
    public double inicioLng;
    public String hraInit;
    public String hraEnd;

    Button btn;

    //VARIABLES PARA LA LLAMADA A LA API DE GOOGLE
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_end);
        btn = findViewById(R.id.next_map_end);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fin.getLat() == 0 || fin.getLng()==0){
                    Toast.makeText(MapaEnd.this, "Por favor, Marca un punto final para tu ruta", Toast.LENGTH_LONG).show();
                } else {
                    Utilidades.coordenadas.setLatInicial(inicioLat);
                    Utilidades.coordenadas.setLngInicial(inicioLng);
                    Utilidades.coordenadas.setLatFinal(fin.getLat());
                    Utilidades.coordenadas.setLngFinal(fin.getLng());
                    webServiceGetRoute(String.valueOf(inicioLat), String.valueOf(inicioLng), String.valueOf(fin.getLat()), String.valueOf(fin.getLng()));
                    Intent intent = new Intent(getApplication(), MapaPoints.class);
                    intent.putExtra("inicioLat", inicioLat);
                    intent.putExtra("inicioLng", inicioLng);
                    intent.putExtra("finLat", fin.getLat());
                    intent.putExtra("finLng", fin.getLng());
                    intent.putExtra("hraInit", hraInit);
                    intent.putExtra("hraEnd", hraEnd);
                    startActivity(intent);
                }
            }
        });
        datos = this.getIntent().getExtras();
        inicioLat = datos.getDouble("inicioLat");
        inicioLng = datos.getDouble("inicioLng");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapEnd);
        mapFragment.getMapAsync(this);
        fin = new Parada();
        request = Volley.newRequestQueue(getApplicationContext());
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
        fin.setLat(marker.getPosition().latitude);
        fin.setLng(marker.getPosition().longitude);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Fin de Ruta").icon(BitmapDescriptorFactory.fromResource(R.drawable.minus)).draggable(true));
        mMap.getProjection().toScreenLocation(latLng);
        fin.setLat(latLng.latitude);
        fin.setLng(latLng.longitude);
        Toast.makeText(this, "Nuevo punto marcado", Toast.LENGTH_SHORT).show();
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

   private void webServiceGetRoute(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {

       String url="https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyBv3LJ0kBCqiHAfkD5RZp4ojz-R2GTF8EE&origin="+latitudInicial+","+longitudInicial
               +"&destination="+latitudFinal+","+longitudFinal;

       jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Toast.makeText(MapaEnd.this, response.toString(), Toast.LENGTH_LONG).show();
               //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
               //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
               //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
               JSONArray jRoutes = null;
               JSONArray jLegs = null;
               JSONArray jSteps = null;

               try {

                   jRoutes = response.getJSONArray("routes");

                   /** Traversing all routes */
                   for(int i=0;i<jRoutes.length();i++){
                       jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                       List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                       /** Traversing all legs */
                       for(int j=0;j<jLegs.length();j++){
                           jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                           /** Traversing all steps */
                           for(int k=0;k<jSteps.length();k++){
                               String polyline = "";
                               polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                               List<LatLng> list = decodePoly(polyline);

                               /** Traversing all points */
                               for(int l=0;l<list.size();l++){
                                   HashMap<String, String> hm = new HashMap<String, String>();
                                   hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                   hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                   path.add(hm);
                               }
                           }
                           Utilidades.routes.add(path);
                       }
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }catch (Exception e){
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
               System.out.println();
               Log.d("ERROR: ", error.toString());
           }
       }
       );

       request.add(jsonObjectRequest);
   }
}
