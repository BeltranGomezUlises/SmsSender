package com.ub.smssender.models;

/**
 * Created by ulises on 18/02/17.
 */

public class ModelMensajes {

    private String estado;
    private String mensaje;
    private Object datos;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
        return "ModelMensajes{" +
                "estado='" + estado + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", datos=" + datos +
                '}';
    }
}
