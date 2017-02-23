package com.ub.smssender.services;

/**
 * Created by ulises on 22/02/17.
 */

public class WSUtils {

    public static ServiceMensajes webServices(){
        return SRetrofit.getIntance().create(ServiceMensajes.class);
    }
}
