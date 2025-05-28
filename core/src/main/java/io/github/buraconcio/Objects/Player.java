package io.github.buraconcio.Objects;

import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Utils.Constants;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

    private String username;
    private double stars;
    private int id;
    private boolean hosting;
    private String avatarpath;

    private Ball ball;

    public Player(String username){
        this.username = username;
        stars = 0.0;
    }

    public Ball createBall(Vector2 pos, World world) {
        ball = new Ball(pos, Constants.BALL_RADIUS, world, id);

        return ball;
    }

    public void stroke(Vector2 mouse1, Vector2 mouse2) {
        ball.applyImpulse(ball.calculateImpulse(mouse1, mouse2));

        stars += 1.0;
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

    public void setStars(double stars) {
        this.stars = stars;
    }

    public void setAvatar(String newAvatar){
        avatarpath = newAvatar;
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
}
