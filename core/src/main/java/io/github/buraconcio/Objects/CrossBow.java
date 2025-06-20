package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

import java.lang.Math;

public class CrossBow extends Obstacle {
    public static final float arrowSpeed = 7.5f;
    public static final float frameDuration = 0.05f;

    private boolean canSpawn = true;

    public CrossBow(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/crossbow/crossbow.png", "obstacles/crossbow/crossbow.json"));

        animacao.setFrameDuration(frameDuration);
        animacao.pauseAnimation();

        SoundManager.getInstance().loadSound("flechada", "sounds/obstacle-sounds/crossbow/arrowshot.wav");

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        shape.setAsBox(size.x/2, size.y/2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (animacao.isLastFrame()) {
            if (canSpawn)
            {
                spawnArrow();
                SoundManager.getInstance().playProximity("flechada", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
            }
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

        Vector2 arrowPos = new Vector2(getX() + getWidth()*(cos*0.5f + 0.5f) + arrowLen*cos*1.05f ,
            getY() + getHeight()*(sin*0.5f + 0.5f) + arrowLen*sin*1.05f);

        getStage().addActor(new Arrow(arrowPos, arrowSpeed, angle));
    }

    @Override
    public void preRound() {
        animacao.resumeAnimation();
    }

    @Override
    public void postRound(){
        animacao.pauseAnimation();
    }

    @Override
    public void place() {
        super.place();
        body.getFixtureList().forEach(fixture -> {fixture.setSensor(false);});
    }
}

