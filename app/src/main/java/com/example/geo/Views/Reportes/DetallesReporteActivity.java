package com.example.geo.Views.Reportes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geo.APIs.GeoAPIService;
import com.example.geo.MainActivity;
import com.example.geo.Models.Reporte;
import com.example.geo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetallesReporteActivity extends AppCompatActivity {

    private TextView calle, reportes, frecuencia, hora;
    private Reporte reporte;
    private Button btn_invalidar;
    private String auxId;
    private Retrofit retrofitGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reporte);

        retrofitGeo = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        calle = (TextView) findViewById(R.id.textView18);
        reportes = (TextView) findViewById(R.id.textView19);
        frecuencia = (TextView) findViewById(R.id.textView20);
        hora = (TextView) findViewById(R.id.textView21);
        btn_invalidar = findViewById(R.id.btnInvalidarReporte);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            reporte = (Reporte) bundle.getSerializable("reporte");
            calle.setText("Dirección: " + reporte.getCalle().toString());
            reportes.setText("Reporte: " + reporte.getReporte().toString());
            frecuencia.setText("Frecuencia: " + reporte.getFrecuencia().toString());
            hora.setText("Hora: " + reporte.getHora());
            auxId = reporte.getId().toString();
        }
    }

    public void invalidarReporte(View view) {
        GeoAPIService servicioGeo = retrofitGeo.create(GeoAPIService.class);
        Call<Boolean> invalidarUsuarioCall = servicioGeo.invalidarReporte(auxId);
        invalidarUsuarioCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.code()==200) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error: ", " onFailure: " + t.getMessage());
            }
        });
    }



}