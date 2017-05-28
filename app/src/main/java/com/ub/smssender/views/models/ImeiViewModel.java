package com.ub.smssender.views.models;

import java.util.Objects;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 2/04/17.
 */

public class ImeiViewModel {

    private String imei;
    private int counter;
    private boolean activo;

    public ImeiViewModel(String imei, int counter, boolean activo) {
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

    public void setCounter(int contador) {
        this.counter = counter;
    }

    public void incrementCounter(){
        this.counter++;
    }

    public void borrarContador(){
        this.counter = 0;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImeiViewModel that = (ImeiViewModel) o;
        return counter == that.counter &&
                Objects.equals(imei, that.imei);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, counter);
    }

    @Override
    public String toString() {
        return "ImeiViewModel{" +
                "imei='" + imei + '\'' +
                ", counter=" + counter +
                ", activo=" + activo +
                '}';
    }
}
