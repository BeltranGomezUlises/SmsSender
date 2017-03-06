package com.ub.smssender.models;

import java.io.Serializable;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class ModelLogin implements Serializable {

    private String contra;
    private String correo;

    public ModelLogin() {
    }

    public ModelLogin(String correo, String contra) {
        this.contra = contra;
        this.correo = correo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "ModelLogin{" +
                "contra='" + contra + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
