package com.example.geo;

import android.Manifest;
// -
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//-----------
import android.content.pm.PackageManager;
import android.os.Looper;
// ----------
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// -------------------------------------------
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geo.APIs.OpenStreetMapAPIService;
import com.example.geo.APIs.OpenStreetMapRespuesta;
import com.example.geo.Views.Inicio.LoginActivity;
import com.example.geo.Views.Mapa.MapaActivity;
import com.example.geo.Views.Reportes.NuevoReporteActivity;
import com.example.geo.Views.Reportes.VerReportesActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
// ------------------------------------------
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private Retrofit retrofitOpenStreetMap, retrofitGeo;
    private EditText et_editCoord;
    private TextView tv_horaUltimaActualizacion, tv_UbicacionTiempoReal;
    private Button btn_verReporte;
    private LinearLayout ll_fondoIndiceDeInseguridad;
    private int auxRol, indicePeligro = 0;
    private String auxNombreCalle;

    public static final int REQUEST_CODE = 1;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -----  Comprobar usuario logueado
        sharedPreferences = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("usuario", "");
        String rol = sharedPreferences.getString("rol", "");
        Boolean sesionActiva = sharedPreferences.getBoolean("SesionActiva", true);
        Toast.makeText(this, "Bienvenido " + usuario, Toast.LENGTH_LONG).show();

        auxRol = Integer.valueOf(sharedPreferences.getString("rol", ""));

        btn_verReporte = findViewById(R.id.btnVerReportes);
        tv_UbicacionTiempoReal = findViewById(R.id.tvUbicacionTiempoReal);
        tv_horaUltimaActualizacion = findViewById(R.id.tvHoraUltimaActualizacion);
        ll_fondoIndiceDeInseguridad = findViewById(R.id.linearFondoIndiceDeInseguridad);

        if(auxRol != 1){
            btn_verReporte.setVisibility(View.GONE);
        }

        solicitarUbicacion();

        // -----  retrofit para apis
        retrofitOpenStreetMap = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitGeo = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();



    }

    public void obtenerUbicacion(){
        try {
            LocationRequest ubicacion = new LocationRequest();
            ubicacion.setInterval(2000);
            ubicacion.setFastestInterval(3000);
            ubicacion.setPriority(ubicacion.PRIORITY_HIGH_ACCURACY);
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(ubicacion, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        LatLng ubicacionActualLatLng = new LatLng(latitud, longitude);
                        buscarCallePorCoordenadas(ubicacionActualLatLng);
                    }
                }

            }, Looper.myLooper());

        }catch (Exception ex){
            Toast.makeText(this, "Error al obtener ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    public void solicitarUbicacion(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {
            obtenerUbicacion();
        }
    }


    // -----  Mostrar menú items  -----
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_geo,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent intento = new Intent(this, LoginActivity.class);
        switch (item.getItemId()){
            case R.id.cerrar_sesion_item:
                editor = sharedPreferences.edit();
                editor.clear().apply();
                intento = new Intent(this, LoginActivity.class);
                startActivity(intento);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }




    // -----   botones para llamar activities  -----
    public void verMapa(View view){
        Intent intento = new Intent(this, MapaActivity.class);
        startActivity(intento);
    }

    public void GenerarReporte(View view){
        Intent intento = new Intent(this, NuevoReporteActivity.class);
        startActivity(intento);
    }

    public void VerReportes(View view){
        Intent intento = new Intent(this, VerReportesActivity.class);
        startActivity(intento);
    }


    // Metodo para consultar calle a partir de coordenadas
    private void buscarCallePorCoordenadas(LatLng latLng) {
        OpenStreetMapAPIService servicioOpenStreetMap = retrofitOpenStreetMap.create(OpenStreetMapAPIService.class);
        Call<OpenStreetMapRespuesta> openStreetMapCall = servicioOpenStreetMap.consultarCalle("jsonv2", ""+latLng.latitude, ""+latLng.longitude);
        openStreetMapCall.enqueue(new Callback<OpenStreetMapRespuesta>() {
            @Override
            public void onResponse(Call<OpenStreetMapRespuesta> call, Response<OpenStreetMapRespuesta> response) {
                if (response.code() == 200) {
                    OpenStreetMapRespuesta openStreetMapRespuesta = response.body();
                    auxNombreCalle = ""+openStreetMapRespuesta.getAddress().getRoad().toString();
                    tv_UbicacionTiempoReal.setText("Ubicación en Tiempo Real: "+openStreetMapRespuesta.getAddress().getRoad().toString());
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String horaActual = sdf.format(calendar.getTime());
                    tv_horaUltimaActualizacion.setText("Hora ult. actualización: "+horaActual);
                } else {
                }
            }

            @Override
            public void onFailure(Call<OpenStreetMapRespuesta> call, Throwable t) {
                Log.e("Error: ", " onFailure: " + t.getMessage());
            }
        });
    }

}