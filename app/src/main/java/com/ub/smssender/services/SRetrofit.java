package com.ub.smssender.services;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ulises on 22/02/17.
 */

public class SRetrofit {

    private SRetrofit(){}

    private static Retrofit instancia;

    public static Retrofit getIntance(){
        if (instancia == null){
            instancia = new Retrofit.Builder()
                    .baseUrl(ServiceMensajes.END_POINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instancia;
    }

}
