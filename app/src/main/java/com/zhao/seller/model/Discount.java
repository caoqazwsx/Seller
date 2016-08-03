package com.zhao.seller.model;

/**
 * Created by zhao on 2016/5/31.
 */
public class Discount {
    private double enough;
    private double reduce;

    public Discount(double enough,double reduce){
        this.enough = enough;
        this.reduce = reduce;
    }

    public void setReduce(double jian) {
        this.reduce = jian;
    }

    public void setEnough(double enghou) {
        this.enough = enghou;
    }

    public double getreduce() {
        return reduce;
    }

    public double getEnough() {
        return enough;
    }
}
