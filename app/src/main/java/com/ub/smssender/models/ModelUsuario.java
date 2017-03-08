package com.ub.smssender.models;

import java.util.List;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 6/03/17.
 */

public class ModelUsuario {

    private String _id;
    private float __v;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private String tipo;
    private String correo;
    private String contra;
    private List<ModelDispositivo> dispositivos;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public float get__v() {
        return __v;
    }

    public void set__v(float __v) {
        this.__v = __v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public List<ModelDispositivo> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<ModelDispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }

    @Override
    public String toString() {
        return "ModelUsuario{" +
                "_id=" + _id +
                ", __v=" + __v +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", tipo='" + tipo + '\'' +
                ", correo='" + correo + '\'' +
                ", contra='" + contra + '\'' +
                ", dispositivos=" + dispositivos +
                '}';
    }
}
