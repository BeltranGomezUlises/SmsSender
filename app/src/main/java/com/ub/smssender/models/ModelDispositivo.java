package com.ub.smssender.models;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 6/03/17.
 */

class ModelDispositivo {

    private String owner;
    private String imei;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "ModelDispositivo{" +
                "owner='" + owner + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
