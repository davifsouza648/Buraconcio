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

    private float timer;

    public CrossBow(Vector2 pos, Vector2 size) {
        super(pos, size, "crossBow.png");

        PolygonShape fixtureDef = new PolygonShape();
        fixtureDef.setAsBox(size.x, size.y);
        body.createFixture(fixtureDef, 0f);
        fixtureDef.dispose();

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
        float angle = body.getAngle();

        Vector2 arrowPos = new Vector2(getX() + getWidth()*((float) Math.cos(angle) + 0.5f),
            getY() + getHeight()*((float) Math.sin(angle) + 0.5f));
        getStage().addActor(new Arrow(arrowPos, arrowSpeed, angle));
    }
}

