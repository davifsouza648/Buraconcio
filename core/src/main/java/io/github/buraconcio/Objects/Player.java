package io.github.buraconcio.Objects;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private int stars;
    private int id;
    private int strokes;
    private boolean hosting;
    private String avatarpath, skinBallPath;
    private Obstacle selectedObstacle;
    private boolean canSelect = true;
    private Vector2 startingPos = null;
    private boolean hasStar = false;
    private boolean hasPlacedObstacle;

    private transient Ball ball;

    public Player(String username) {
        this.username = username;
        stars = 0;

        initSounds();



        ball = null;
        selectedObstacle = null;
    }

    public Ball createBall() 
    {
        if (startingPos == null) {
            System.out.println("starting position not defined");
            return null;
        }

        ball = new Ball(startingPos, Constants.BALL_RADIUS * 2, this, skinBallPath);

        return ball;
    }

    public Ball createBall(Vector2 pos) {
        ball = new Ball(pos, Constants.BALL_RADIUS * 2, this, skinBallPath);

        return ball;
    }

    public Ball getBall() {
        return ball;
    }

    public void update(Vector2 ballPos, Vector2 velocity, boolean state) {
        if (ball == null) {
            System.out.println("ball not yet created");
            return;
        }

        ball.setAlive(state);
        ball.setPos(ballPos);
        ball.setVelocity(velocity);
    }

    public void update(Vector2 obstaclePos, int obsId, int obsRotationIndex) {

        Obstacle obstacle = (Obstacle) PhysicsManager.getInstance().getEntity(obsId);

        if (obstacle == null) {
            return;
        }

        selectObstacle(obstacle);

        if (selectedObstacle != null) {

            selectedObstacle.move(obstaclePos);

            if(obsRotationIndex != selectedObstacle.getRotationIndex())
                selectedObstacle.rotate(obsRotationIndex);

        }
    }

    public void update(boolean placed) {
        // System.out.println("atualizando o placed para: " + placed);
        setHasPlacedObstacle(placed);
    }

    public void stroke(Vector2 mouse1, Vector2 mouse2) {
        if (!ball.isStill() || !ball.canInteract())
            return;

        if (mouse1.dst(mouse2) < 0.05f)
            return;

        int sound = (int) (Math.random() * 7) + 1;
        SoundManager.getInstance().playProximity("ballhit" + sound, this.getBall().getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
        ball.applyImpulse(ball.calculateImpulse(mouse1, mouse2));

        strokes += 1;
    }

    public void score() {
        ball.enterHole();

        rewardStar();
    }

    public void die() {
        ball.enterHole();
    }

    public void rewardStar() {

        stars++;

        if (hasStar)
            stars += 1;

        if (strokes <= 2) {
            stars += 3;
        } else if (strokes <= 5) {
            stars += 1;
        }
    }

    public void collectStar() {
        hasStar = true;
    }

    public void placeObstacle() {
        selectedObstacle.place();
        selectedObstacle = null;

        hasPlacedObstacle = true;
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public boolean canSelect() {
        return canSelect;
    }

    public void selectObstacle(Obstacle obstacle) {

        // disable for testing
        // if (!canSelect)
        // return;

        selectedObstacle = obstacle;
        obstacle.claim();

        canSelect = false;
    }

    public Obstacle getSelectedObstacle() {
        return selectedObstacle;
    }

    public void setStartingPos(Vector2 pos) {
        this.startingPos = pos;
    }

    public void teleportToStartingPos() {
        ball.teleport(startingPos);
    }

    public String getUsername() {
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

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setAvatar(int num) {

        avatarpath = "user" + num + ".png";
    }

    public void setSkinBall(String path){
        this.skinBallPath = path;
    }

    public String getAvatar() {
        return avatarpath;
    }

    public void setHosting(boolean tf) {
        hosting = tf;
    }

    public Boolean getHosting() {
        return hosting;
    }

    public int getStrokes() {
        return this.strokes;
    }

    public Boolean hasPlacedObstacle() {
        return hasPlacedObstacle;
    }

    public void setHasPlacedObstacle(boolean flag) {
        hasPlacedObstacle = flag;
    }

    private void initSounds() {
        SoundManager.getInstance().loadSound("ballhit1", "sounds/hits/hit1.wav");
        SoundManager.getInstance().loadSound("ballhit2", "sounds/hits/hit2.wav");
        SoundManager.getInstance().loadSound("ballhit3", "sounds/hits/hit3.wav");
        SoundManager.getInstance().loadSound("ballhit4", "sounds/hits/hit4.wav");
        SoundManager.getInstance().loadSound("ballhit5", "sounds/hits/hit5.wav");
        SoundManager.getInstance().loadSound("ballhit6", "sounds/hits/hit6.wav");
        SoundManager.getInstance().loadSound("ballhit7", "sounds/hits/hit7.wav");
    }

    public void dispose() {
        if (ball != null) {
            ball.destroy();
            ball.clear();
        }
    }

    public void setBallInteractable(boolean canInteract) {
        if (ball != null) {
            ball.setCanInteract(canInteract);
        }
    }
}
