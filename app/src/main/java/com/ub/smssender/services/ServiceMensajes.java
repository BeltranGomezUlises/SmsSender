package com.ub.smssender.services;

import com.ub.smssender.models.ModelMensajes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ulises on 18/02/17.
 */

public interface ServiceMensajes {

    public static final String END_POINT = "http://192.168.1.74:45200/api/";

    @GET("mensajes/{usuarioId}/{imei}")
    Call<ModelMensajes> mensajes(@Path("usuarioId") String usuario, @Path("imei") String imei);

    /*
    @POST("users/new")
    Call<User> createUser(@Body User user);
    */
}
