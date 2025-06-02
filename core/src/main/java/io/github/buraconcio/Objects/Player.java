package io.github.buraconcio.Objects;

import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Utils.Constants;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private int stars;
    private int id;
    private int strokes;
    private boolean hosting;
    private String avatarpath;

    private transient Ball ball;

    public Player(String username){
        this.username = username;
        stars = 0;
    }

    public Ball createBall(Vector2 pos) {
        ball = new Ball(pos, Constants.BALL_RADIUS, this);

        return ball;
    }

    public void stroke(Vector2 mouse1, Vector2 mouse2) {
        if (!ball.isStill()) return;

        ball.applyImpulse(ball.calculateImpulse(mouse1, mouse2));

        strokes += 1;
    }

    public void score() {
        ball.enterHole();

        rewardStar();
    }

    public void die() {
        // temporary
        ball.enterHole();
    }

    public void rewardStar(){
        if(strokes <= 2){
            stars += 3;
        }else if(strokes <= 5){
            stars += 1;
        }
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public double getStars(){

        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setAvatar(int num){

        avatarpath = "user" + num + ".png";
    }

    public String getAvatar(){
        return avatarpath;
    }

    public void setHosting(boolean tf){
        hosting = tf;
    }

    public Boolean getHosting(){
        return hosting;
    }

    public int getStrokes(){
        return this.strokes;
    }

}
