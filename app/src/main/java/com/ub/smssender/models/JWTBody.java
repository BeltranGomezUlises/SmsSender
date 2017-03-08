package com.ub.smssender.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 6/03/17.
 */

public class JWTBody {

    private List<ModelUsuario> sub;
    private Date iat;
    private Date exp;

    public List<ModelUsuario> getSub() {
        return sub;
    }

    public void setSub(List<ModelUsuario> sub) {
        this.sub = sub;
    }

    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "JWTBody{" +
                "sub=" + sub +
                ", iat=" + iat +
                ", exp=" + exp +
                '}';
    }
}
