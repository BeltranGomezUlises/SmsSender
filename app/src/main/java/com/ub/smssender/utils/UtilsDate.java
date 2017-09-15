package com.ub.smssender.utils;

import android.widget.AbsListView;

import java.util.Date;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 15/09/17.
 */

public class UtilsDate {

    /**
     * obtiene la diferencia de tiempo en minutos de las fechas proporcionadas
     * @param date1 objeto date del lado izquierdo de la diferecia
     * @param date2 objeto date del lado derecho de la diferencia
     * @return date1 - date2 en minutos
     */
    public static long diferenciaEnMinutos(Date date1, Date date2){
        long diferenciaEnMillis = date1.getTime() - date2.getTime();
        long diferenciaEnSegundos = diferenciaEnMillis / 1000; //diferencia en segundos
        return diferenciaEnSegundos / 60; //diferencia en minutos
    }

}
