package com.example.geo.Views.Reportes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.geo.APIs.GeoAPIService;
import com.example.geo.APIs.OpenStreetMapAPIService;
import com.example.geo.APIs.OpenStreetMapRespuesta;
import com.example.geo.Models.Reporte;
import com.example.geo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NuevoReporteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private Retrofit retrofitOpenStreetMap, retrofitGeo;
    private GoogleMap mapaNuevoReporte;
    private Spinner sp_suceso, sp_frecuencia;
    private EditText et_calle;
    private Reporte nuevoReporte;
    private ArrayList<String> listaSucesos, listaFrecuencia;
    private boolean auxSucesoSeleccionado = false, auxFreuenciaSeleccionada = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_reporte);

        nuevoReporte = new Reporte();

        // -----  Recuperar fragmento que contiene el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMapNuevoReporte);
        mapFragment.getMapAsync(this);

        // -----  Recuperar inputs del formulario
        et_calle = findViewById(R.id.editNuevoReporteCalle);
        sp_suceso = findViewById(R.id.spinnerNuevoReporteSuceso);
        sp_frecuencia = findViewById(R.id.spinnerNuevoReporteFrecuencia);

        // -----  retrofit para apis
        retrofitOpenStreetMap = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitGeo = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        // -----  Llenado de datos a spinners
        obtenerDatosParaSpinners();

        ArrayAdapter<CharSequence> adapterSpinnerSucesos = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSucesos);
        sp_suceso.setAdapter(adapterSpinnerSucesos);
        sp_suceso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    nuevoReporte.setReporte(""+listaSucesos.get(i).toString());
                    auxSucesoSeleccionado = true;
                } else {
                    auxSucesoSeleccionado = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapterSpinnerFrecuencias = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFrecuencia);
        sp_frecuencia.setAdapter(adapterSpinnerFrecuencias);
        sp_frecuencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    nuevoReporte.setFrecuencia(""+listaFrecuencia.get(i).toString());
                    auxFreuenciaSeleccionada = true;
                } else {
                    auxFreuenciaSeleccionada = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }


    // ----------   Metodos del Mapa   ----------
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapaNuevoReporte = googleMap;
        this.mapaNuevoReporte.setOnMapClickListener(this);
        this.mapaNuevoReporte.setOnMapLongClickListener(this);

        LatLng teziutlanLatLng = new LatLng(19.817978666922073, -97.36091088514254);
        mapaNuevoReporte.addMarker(new MarkerOptions().position(teziutlanLatLng).title("Teziutlán"));
        mapaNuevoReporte.moveCamera(CameraUpdateFactory.newLatLng(teziutlanLatLng));
        mapaNuevoReporte.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        CambiarMarcaDeUbicacion(latLng);
        buscarCallePorCoordenadas(latLng);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        CambiarMarcaDeUbicacion(latLng);
        buscarCallePorCoordenadas(latLng);
    }

    private void CambiarMarcaDeUbicacion(LatLng latLng) {
        mapaNuevoReporte.clear();
        LatLng ubicacionSeleccionada = new LatLng(latLng.latitude, latLng.longitude);
        mapaNuevoReporte.addMarker(new MarkerOptions().position(ubicacionSeleccionada).title("Ubicación Seleccionada"));
        mapaNuevoReporte.moveCamera(CameraUpdateFactory.newLatLng(ubicacionSeleccionada));
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
                    et_calle.setText(""+openStreetMapRespuesta.getAddress().getRoad().toString());
                    nuevoReporte.setCalle(""+openStreetMapRespuesta.getAddress().getRoad().toString());
                } else {
                }
            }

            @Override
            public void onFailure(Call<OpenStreetMapRespuesta> call, Throwable t) {
                Log.e("Error: ", " onFailure: " + t.getMessage());
            }
        });
    }

    // Metodo para llenar spinners con datos
    private void obtenerDatosParaSpinners() {
        listaSucesos = new ArrayList<String>();
        listaSucesos.add("Seleccione uno");
        listaSucesos.add("Accidente de Tráfico");
        listaSucesos.add("Calle en Barranco");
        listaSucesos.add("Banda Delictiva");
        listaSucesos.add("Venta de Drogas");
        listaSucesos.add("Asaltos");
        listaSucesos.add("Asesinatos");
        listaSucesos.add("Baches en Calle");
        listaSucesos.add("Sin Alumbrado");
        listaSucesos.add("Sin Banqueta");
        listaSucesos.add("Riñas entre Personas");
        listaSucesos.add("Perros Callejeros");
        listaSucesos.add("Calle Solida");

        listaFrecuencia = new ArrayList<String>();
        listaFrecuencia.add("Seleccione uno");
        listaFrecuencia.add("Única vez");
        listaFrecuencia.add("Ocasionalmente");
        listaFrecuencia.add("Frecuentemente");
        listaFrecuencia.add("Muy Frecuentemente");
    }

    public void EnviarReporte(View view){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String horaActual = sdf.format(calendar.getTime());
        nuevoReporte.setHora(""+horaActual);
        if (this.validarCamposVacios() == true) {
            String cadena = "reporte: " + nuevoReporte.getReporte()
                    + "\ncalle: " + nuevoReporte.getCalle()
                    + "\nhora: " + nuevoReporte.getHora()
                    + "\nFrecuencia: " + nuevoReporte.getFrecuencia();

            Toast.makeText(getApplicationContext(), cadena, Toast.LENGTH_SHORT).show();

            GeoAPIService servicioGeo = retrofitGeo.create(GeoAPIService.class);
            Call<Boolean> enviarReporteCall = servicioGeo.agregarReporte(nuevoReporte);
            enviarReporteCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    finish();
                }
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("Error: ", " onFailure: " + t.getMessage());
                }
            });

        }

        // this.finish();
    }

    private boolean validarCamposVacios() {
        boolean camposLlenos = true;
        if (et_calle.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            camposLlenos = false;
        }
        if (auxSucesoSeleccionado == false) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            camposLlenos = false;
        }
        if(auxFreuenciaSeleccionada == false) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            camposLlenos = false;
        }
        return camposLlenos;
    }
}