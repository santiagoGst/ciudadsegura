package com.example.geo.Views.Mapa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.geo.R;
import com.example.geo.Views.Inicio.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mapa;
    LocationManager locationManager;
    String usuario;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // -----  Comprobar usuario logueado
        sharedPref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        usuario = sharedPref.getString("usuario","");


        // -----  Recuperar fragmento que contiene el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMapNuevoReporte);
        mapFragment.getMapAsync(this);
        
        //ObtenerUbicacionActual();
    }



    // ----------   Opciones Menú   ----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_geo,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cerrar_sesion_item:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();
                Intent intento = new Intent(this, LoginActivity.class);
                startActivity(intento);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // ----------   Metodos y funciones de Mapa   ----------
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        this.mapa.setOnMapClickListener(this);
        this.mapa.setOnMapLongClickListener(this);

        LatLng teziutlanLatLng = new LatLng(19.817978666922073, -97.36091088514254);
        //mMap.addMarker(new MarkerOptions().position(teziutlanLatLng).title("Teziutlán"));
        mapa.moveCamera(CameraUpdateFactory.newLatLng(teziutlanLatLng));
        mapa.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(19.80296, -97.371145))
                .radius(50)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(Color.rgb(180,255,166))
                .clickable(true); // In meters

        // Get back the mutable Circle
        mapa.addCircle(circleOptions);

        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(19.803697, -97.370512),
                        new LatLng(19.800841, -97.371681),
                        new LatLng(19.800003, -97.370415))
                .strokeColor(Color.YELLOW)
                .fillColor(Color.rgb(255,255,166));

        // Get back the mutable Polygon
        Polygon polygon = mapa.addPolygon(polygonOptions);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }


}