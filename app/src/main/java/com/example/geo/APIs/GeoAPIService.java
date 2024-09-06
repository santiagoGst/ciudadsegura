package com.example.geo.APIs;

import com.example.geo.Models.Reporte;
import com.example.geo.Models.Usuario;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeoAPIService {

    @GET("Usuarios/Nombre")
    Call<Usuario> validarUsuario(@Query("nombre") String user, @Query("pwd") String password);

    @POST("Usuarios")
    Call<Boolean> RegistrarUsuario(@Body Usuario usuario);

    @GET("Reportes")
    Call<ArrayList<Reporte>> consultarReportes();

    @POST("Reportes")
    Call<Boolean> agregarReporte(@Body Reporte reporte);

    @DELETE("Reportes")
    Call<Boolean> invalidarReporte(@Query("id") String id);

}
