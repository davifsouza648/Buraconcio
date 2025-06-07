package io.github.buraconcio.Utils;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class UdpPackage implements Serializable {

    public enum UdpType {
        SELECT_OBJ,
        PLAY
    }

    private static final long serialVersionUID = 1L;

    private int id;
    private float ballX, ballY, ballVX, ballVY;
    private float ObsX, ObsY, ObsID;
    private UdpType type;

    public UdpPackage(int id, float x, float y, Vector2 vel, UdpType type) {
        this.id = id;
        this.ballX = x;
        this.ballY = y;
        this.ballVX = vel.x;
        this.ballVY = vel.y;
        this.type = type;
    }

    public UdpPackage(int id, float x, float y, int ObsId, UdpType type) {
        this.id = id;
        this.ObsX = x;
        this.ObsY = y;
        this.ObsID = ObsId;
        this.type = type;
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

    public float getBallVY() {
        return ballVY;
    }

    public float getBallVX() {
        return ballVX;
    }

    public float getObsX() {
        return ObsX;
    }

    public float getObsY() {
        return ObsY;
    }

    public float getObsId() {
        return ObsID;
    }

    public UdpType getType() {
        return type;
    }

    public String toBallString() {
        return "UdpPackage{" +
                "id=" + id +
                ", x=" + ballX +
                ", y=" + ballY +
                ", velX=" + ballVX +
                ", velY= " + ballVY +
                '}';
    }

    public String toObjectString() {
        return "UdpPackage{" +
                "id=" + id +
                ", x=" + ObsX +
                ", y=" + ObsY +
                ", obsID=" + ObsID +
                '}';
    }

}
