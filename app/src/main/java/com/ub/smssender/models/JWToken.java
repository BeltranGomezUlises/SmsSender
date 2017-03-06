package com.ub.smssender.models;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class JWToken {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JWToken{" +
                "token='" + token + '\'' +
                '}';
    }
}
