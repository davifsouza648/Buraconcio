package io.github.buraconcio.Utils;

import java.io.Serializable;

public class UdpPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private float ballX, ballY, ballVel;
    private float ObsX, ObsY, ObsID;

    public UdpPackage(int id, float x, float y, float vel) {
        this.id = id;
        this.ballX = x;
        this.ballY = y;
        this.ballVel = vel;
    }

    public UdpPackage(int id, float x, float y, int ObsId) {
        this.id = id;
        this.ObsX = x;
        this.ObsY = y;
        this.ObsID = ObsId;
    }

    public int getId() {
        return id;
    }

    public float getBallX() {
        return ballX;
    }

    public float getBallY() {
        return ballY;
    }

    public float getBallVel() {
        return ballVel;
    }

    public float getObsX() {
        return ObsX;
    }

    public float getObsY() {
        return ObsY;
    }

    public String toObjectString() {
        return "UdpPackage{" +
                "id=" + id +
                ", x=" + ballX +
                ", y=" + ballY +
                ", vel=" + ballVel +
                '}';
    }

    public String toBallString() {
        return "UdpPackage{" +
                "id=" + id +
                ", x=" + ObsX +
                ", y=" + ObsY +
                '}';
    }

}
