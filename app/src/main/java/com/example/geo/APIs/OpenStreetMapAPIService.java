package com.example.geo.APIs;

import com.example.geo.Models.CallePorCoordenadas;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenStreetMapAPIService {

    @GET("reverse")
    Call<OpenStreetMapRespuesta> consultarCalle(@Query("format") String format, @Query("lat") String latitud, @Query("lon") String longitud);

}
