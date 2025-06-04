package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Objects.Arrow;

import java.lang.Math;

public class CrossBow extends Obstacle {
    public static final float spawnRate = 1f; // seconds
    public static final float arrowSpeed = 3f;

    private boolean spawning = false;
    private float timer;

    public CrossBow(Vector2 pos, Vector2 size) {
        super(pos, size, "crossBow.png");

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        shape.setAsBox(size.x/2, size.y/2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        timer = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        timer += delta;

        if (timer >= spawnRate) {
            spawnArrow();
            timer -= spawnRate;
        }
    }

    public void spawnArrow() {
        if (!spawning)
            return;

        float angle = body.getAngle();

        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        float arrowLen = Arrow.arrowSize.x;

        Vector2 arrowPos = new Vector2(getX() + getWidth()*(cos*0.5f + 0.5f) + arrowLen*cos*1.25f ,
            getY() + getHeight()*(sin*0.5f + 0.5f) + arrowLen*sin*1.25f);

        getStage().addActor(new Arrow(arrowPos, arrowSpeed, angle));
    }

    @Override
    public void preRound() {
        setSpawning(true);
    }

    public void setSpawning(boolean spawning) {
        this.spawning = spawning;
        timer = 0f;
    }

    public boolean getSpawning() {
        return spawning;
    }

    public void toggleSpawning() {
        setSpawning(!spawning);
    }
}

