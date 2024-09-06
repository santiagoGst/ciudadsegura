package com.example.geo.Views.Reportes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.geo.APIs.GeoAPIService;
import com.example.geo.Models.Reporte;
import com.example.geo.Models.Usuario;
import com.example.geo.R;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerReportesActivity extends AppCompatActivity {

    ListView lv_reportes;
    private Retrofit retrofit;
    private ArrayList<Reporte> listaReportes;
    ArrayList<Reporte> reportesArrayList;
    private ArrayList<String> listaInformacion;

    private ArrayList<String> listaCalles;
    //FragmentManager fragmentManager = getSupportFragmentManager();;
    private Reporte reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reportes);
        lv_reportes = (ListView) findViewById(R.id.lvReportes);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://segu10-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        consultarReportesApi();

    }

    // Metodo para consultar los reportes con la api
    private void consultarReportesApi() {
        GeoAPIService servicioGeo = retrofit.create(GeoAPIService.class);
        Call<ArrayList<Reporte>> arrayListGeoCall = servicioGeo.consultarReportes();
        arrayListGeoCall.enqueue(new Callback<ArrayList<Reporte>>() {
            @Override
            public void onResponse(Call<ArrayList<Reporte>> call, Response<ArrayList<Reporte>> response) {
                if (response.code() == 200) {
                    reportesArrayList = response.body();
                    listaReportes = reportesArrayList;
                    //Una vez que se obtiene la lista de reportes de nuestra API llamamos el método obtenerCalles()
                    obtenerCalles();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reporte>> call, Throwable t) {
                Log.e("Error: ", " onFailure: " + t.getMessage());
            }
        });
    }


    private void obtenerCalles(){
        //Ciclo for para poder obtener todas las calles almacenadas en la API
        listaInformacion = new ArrayList<String>();
        for (int i = 0; i < listaReportes.size(); i++) {
            listaInformacion.add(i + ". " + listaReportes.get(i).getReporte() + " en " + listaReportes.get(i).getCalle());
        }
        //Creamos un adapter para poder llenar el listview para que el usuario sea capaz de visualizar reportes
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInformacion);
        lv_reportes.setAdapter(adaptador);

        lv_reportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Guardamos el reporte en un objeto de tipo reporte
                reporte = listaReportes.get(i);
                Intent intent = new Intent(VerReportesActivity.this, DetallesReporteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reporte", reporte);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }




}