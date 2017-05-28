package com.ub.smssender.entities;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 18/05/17.
 */

public class ImeiRealm extends RealmObject{

    @PrimaryKey
    private String imei;
    private int counter;
    private boolean activo;

    public ImeiRealm() {
    }

    public ImeiRealm(String imei) {
        this.imei = imei;
        counter = 0;
    }

    public ImeiRealm(String imei, int counter, boolean activo) {
        this.imei = imei;
        this.counter = counter;
        this.activo = activo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImeiRealm imeiRealm = (ImeiRealm) o;
        return counter == imeiRealm.counter &&
                Objects.equals(imei, imeiRealm.imei);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, counter);
    }

    @Override
    public String toString() {
        return "ImeiRealm{" +
                "imei='" + imei + '\'' +
                ", counter=" + counter +
                ", activo=" + activo +
                '}';
    }
}
