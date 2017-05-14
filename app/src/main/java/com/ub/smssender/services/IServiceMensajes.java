package com.ub.smssender.services;

import com.ub.smssender.models.ModelBodyResponse;
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

public interface IServiceMensajes {


    //String END_POINT = "http://201.163.30.113:45200/api/";
    String END_POINT = "http://192.168.1.67:45200/api/";

    @GET("mensajes/{usuarioId}/{imei}/{numMensajes}")
    Call<ModelBodyResponse> mensajes(@Header("Authorization") String token, @Path("usuarioId") String usuario, @Path("imei") String imei, @Path("numMensajes") int numSMS);


    @POST("login")
    Call<ModelBodyResponse> login(@Body ModelLogin user);

    @POST("enviado")
    Call<ModelBodyResponse> enviado(@Body ModelEnviado user);
}
