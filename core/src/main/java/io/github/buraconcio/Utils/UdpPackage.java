package io.github.buraconcio.Utils;

import java.io.Serializable;

public class UdpPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private float x, y;

    public UdpPackage(int id, float x, float y) {

        this.id = id;
        this.x = x;
        this.y = y;
    }

    // public static UdpPackage createPackage(int id, float x, float y) {
    //     return new UdpPackage(id, x, y);
    // }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "UdpPackage{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

}
