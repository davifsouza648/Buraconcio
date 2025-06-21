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

    private int id, obsID;

    private float ballX, ballY, ballVX, ballVY;
    private boolean isAlive, isInHole;

    private float obsX, obsY, obsVel;
    private boolean obsPlaced, flagPlaced;
    private int obsRotationIndex;

    private PackType typeP;

    private boolean default_ = false;

    public UdpPackage(int id, float x, float y, Vector2 vel, boolean isAlive, PackType p) {
        this.id = id;
        this.ballX = x;
        this.ballY = y;
        this.ballVX = vel.x;
        this.ballVY = vel.y;
        this.isAlive = isAlive;
        this.typeP = p;
    }

    public UdpPackage(int id, float x, float y, int ObsId, int obsRotationIndex, PackType p) {
        this.id = id;
        this.obsX = x;
        this.obsY = y;
        this.obsID = ObsId;
        this.typeP = p;
        this.obsRotationIndex = obsRotationIndex;

    }


    public UdpPackage(int id, boolean obsPlaced, boolean flagPlaced, PackType p) {
        this.id = id;
        this.typeP = p;
        this.obsPlaced = obsPlaced;
        this.flagPlaced = flagPlaced;
    }

    public boolean getDefault(){
        return default_;
    }

    public int getId() {
        return id;
    }

    public boolean getflagPlaced(){
        return flagPlaced;
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
        return obsX;
    }

    public float getObsY() {
        return obsY;
    }

    public int getObsId() {
        return obsID;
    }

    public PackType getTypeP(){
        return typeP;
    }

    public boolean getObsPlaced(){
        return obsPlaced;
    }

    public int getObsRotationIndex(){
        return obsRotationIndex;
    }

    public boolean getBallState(){
        return isAlive;
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
                ", x=" + obsX +
                ", y=" + obsY +
                ", obsID=" + obsID +
                '}';
    }

}
