package com.ub.smssender.entities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class MensajeRealm extends RealmObject{

    @PrimaryKey
    private String _id;
    private String mensaje;
    private String tipo;
    private String destino;
    private String envia;
    private String usuarioId;
    private String fechaEnviar;

    //estado 0 = no se ha intentado enviar, estado 1 = se envió a la antena
    private int estado;
    private Date fechaUltimoIntentoEnvio;
    private String __v;

    public Date getFechaUltimoIntentoEnvio() {
        return fechaUltimoIntentoEnvio;
    }

    public void setFechaUltimoIntentoEnvio(Date fechaUltimoIntentoEnvio) {
        this.fechaUltimoIntentoEnvio = fechaUltimoIntentoEnvio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFechaEnviar() {
        return fechaEnviar;
    }

    public void setFechaEnviar(String fechaEnviar) {
        this.fechaEnviar = fechaEnviar;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEnvia() {
        return envia;
    }

    public void setEnvia(String envia) {
        this.envia = envia;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "MensajeRealm{" +
                "_id='" + _id + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", tipo='" + tipo + '\'' +
                ", destino='" + destino + '\'' +
                ", envia='" + envia + '\'' +
                ", usuarioId='" + usuarioId + '\'' +
                ", fechaEnviar='" + fechaEnviar + '\'' +
                ", estado=" + estado +
                '}';
    }
}
