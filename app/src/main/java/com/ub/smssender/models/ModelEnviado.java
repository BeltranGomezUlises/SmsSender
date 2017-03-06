package com.ub.smssender.models;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class ModelEnviado {

    private String _id;
    private int estado;
    private String error;

    public ModelEnviado() {
    }

    public ModelEnviado(String _id, int estado) {
        this._id = _id;
        this.estado = estado;
    }

    public ModelEnviado(String _id, int estado, String error) {
        this._id = _id;
        this.estado = estado;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
