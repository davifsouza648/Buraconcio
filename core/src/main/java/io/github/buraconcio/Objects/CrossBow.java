package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Objects.Arrow;

import java.lang.Math;

public class CrossBow extends Obstacle {
    public static final float arrowSpeed = 3f;
    public static final float frameDuration = 0.05f;
    public static final float shootFrame = 0.05f;

    private boolean canSpawn = true;
    private float timer;

    public CrossBow(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/crossbow/crossbow.png", "obstacles/crossbow/crossbow.json"));
        animacao.setAnimationSpeed(frameDuration);
        animacao.pauseAnimation();

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

        if (animacao.isLastFrame()) {
            if (canSpawn)
                spawnArrow();

            canSpawn = false;
        } else {
            canSpawn = true;
        }
    }

    public void spawnArrow() {
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
        animacao.resumeAnimation();
    }

    @Override
    public void place() {
        super.place();
        body.getFixtureList().forEach(fixture -> {fixture.setSensor(false);});
    }
}

