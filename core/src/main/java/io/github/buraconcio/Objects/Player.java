package io.github.buraconcio.Objects;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;

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
    private String avatarpath;
    private Obstacle selectedObstacle;
    private boolean canSelect = true;
    private Vector2 startingPos = null;
    private boolean hasStar = false;

    private transient ArrayList<Sound> strokeSounds = new ArrayList<>();

    private transient Ball ball;

    public Player(String username) {
        this.username = username;
        stars = 0;

        initSounds();

        ball = null;
        selectedObstacle = null;
    }

    public Ball createBall() {
        if (startingPos == null) {
            System.out.println("starting position not defined");
            return null;
        }

        ball = new Ball(startingPos, Constants.BALL_RADIUS * 2, this);

        return ball;
    }

    public Ball createBall(Vector2 pos) {
        ball = new Ball(pos, Constants.BALL_RADIUS * 2, this);

        return ball;
    }

    public Ball getBall() {
        return ball;
    }

    public void update(Vector2 ballPos, Vector2 velocity) {
        if (ball == null) {
            System.out.println("ball not yet created");
            return;
        }

        ball.setPos(ballPos);
        ball.setVelocity(velocity);
    }

    public void update(Vector2 obstaclePos, int obsId) {

        Obstacle obstacle = (Obstacle) PhysicsManager.getInstance().getEntity(obsId);

        if (obstacle == null) {
            return;
        }

        selectObstacle(obstacle);

        if (selectedObstacle != null)
            selectedObstacle.move(obstaclePos);

    }

    public void stroke(Vector2 mouse1, Vector2 mouse2) {
        if (!ball.isStill() || !ball.canInteract())
            return;

        if (mouse1.dst(mouse2) < 0.05f)
            return;

        if (strokeSounds == null || strokeSounds.isEmpty()) {
            initSounds();
        }

        strokeSounds.get((int) (Math.random() * strokeSounds.size())).play(1f);
        ball.applyImpulse(ball.calculateImpulse(mouse1, mouse2));

        strokes += 1;
    }

    public void score() {
        ball.enterHole();

        rewardStar();
    }

    public void die()
    {
        ball.enterHole();
    }

    public void rewardStar() {
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
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public boolean canSelect() {
        return canSelect;
    }

    public void selectObstacle(Obstacle obstacle) {

        // disable for testing
        //if (!canSelect)
        //    return;

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

    private void initSounds() {
        strokeSounds = new ArrayList<>();
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit1.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit2.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit3.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit4.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit5.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit6.wav")));
        strokeSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hits/hit7.wav")));
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
