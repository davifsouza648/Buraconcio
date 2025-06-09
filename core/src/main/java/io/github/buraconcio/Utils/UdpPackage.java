package io.github.buraconcio.Utils;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class UdpPackage implements Serializable {

    private static final long serialVersionUID = 1L;


    public enum PackType {
        BALL,
        OBSTACLE,
        DEFAULT
    }

    private int id;
    private float ballX, ballY, ballVX, ballVY, isAlive, isInHole;
    private float ObsX, ObsY, ObsID;

    private PackType typeP;

    private boolean default_ = false;

    public UdpPackage(int id, float x, float y, Vector2 vel, PackType p) {
        this.id = id;
        this.ballX = x;
        this.ballY = y;
        this.ballVX = vel.x;
        this.ballVY = vel.y;
        this.typeP = p;
    }

    public UdpPackage(int id, float x, float y, int ObsId, PackType p) {
        this.id = id;
        this.ObsX = x;
        this.ObsY = y;
        this.ObsID = ObsId;
        this.typeP = p;
    }

    public UdpPackage(int id, PackType p) {
        this.id = id;
        this.typeP = p;
    }

    public boolean getDefault(){
        return default_;
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

    public PackType getTypeP(){
        return typeP;
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
