package com.ub.smssender.services;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class BodyResponse {

    private boolean exito;
    private String mensaje;
    private Object datos;

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }

    @Override
    public String toString() {
        return "BodyResponse{" +
                "exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                ", datos=" + datos +
                '}';
    }
}
