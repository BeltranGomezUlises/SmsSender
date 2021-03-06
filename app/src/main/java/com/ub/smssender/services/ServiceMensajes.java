package com.ub.smssender.services;

import com.ub.smssender.models.ModelEnviado;
import com.ub.smssender.models.ModelLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ulises on 18/02/17.
 */

public interface ServiceMensajes {

    //servidor 201.163.30.113
    String END_POINT = "http://192.168.3.140:45200/api/";

    @GET("mensajes/{usuarioId}/{imei}")
    Call<BodyResponse> mensajes(@Header("Authorization") String token, @Path("usuarioId") String usuario, @Path("imei") String imei);


    @POST("login")
    Call<BodyResponse> login(@Body ModelLogin user);

    @POST("enviado")
    Call<BodyResponse> enviado(@Body ModelEnviado user);
}
