package com.ub.smssender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub.smssender.Main.MainActivity;
import com.ub.smssender.models.JWTBody;
import com.ub.smssender.models.ModelLogin;
import com.ub.smssender.models.ModelUsuario;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class UtilPreferences {

    private static final String USER_MODEL = "USER_MODEL";
    private static final String USER = "usuario";
    private static final String PASS = "password";
    private static final String LOGED = "loged";

    private static final String TOKEN = "token";

    public static ModelLogin getModelLogin(Context context) {
        System.out.println("cargando el usuario logeeado");
        SharedPreferences settings = context.getSharedPreferences(USER_MODEL, 0);
        return new ModelLogin(settings.getString(USER,""), settings.getString(PASS, ""));
    }

    public static void saveLoged(Context context, ModelLogin modelLogin) {
        SharedPreferences settings = context.getSharedPreferences(USER_MODEL, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USER, modelLogin.getCorreo());
        editor.putString(PASS, modelLogin.getContra());
        editor.putBoolean(LOGED, true);
        editor.commit();
    }

    public static boolean isLoged(Context context){
        SharedPreferences settings = context.getSharedPreferences(USER_MODEL, 0);
        return settings.getBoolean(LOGED, false);
    }

    public static void LogOutPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(USER_MODEL, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(LOGED, false);
        editor.commit();
    }

    public static void saveToken(Context context, String token){
        SharedPreferences settings = context.getSharedPreferences(TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }


    public static String loadToken(Context context){
        SharedPreferences settings = context.getSharedPreferences(TOKEN, 0);
        return settings.getString(TOKEN, "");
    }

    public static String loadLogedUserId(Context context){
        try {
            String token = loadToken(context);
            ObjectMapper mapper = new ObjectMapper();
            JWTBody jwtBody = mapper.readValue(JWTDecoder.getBodyString(token), JWTBody.class);
            ModelUsuario user = jwtBody.getSub().get(0);
            return user.get_id();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
