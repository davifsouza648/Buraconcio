package io.github.buraconcio.Objects;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;

public class Honey extends Obstacle{
    private static final float damping = 1000f;
    private static final int effectDuration = 2000; //ms

    private boolean ballIsEffected = false;

    public Honey(Vector2 pos, Vector2 size) {
        super(pos, size,
         Auxiliaries.animationFromFiles("obstacles/honey/honey.png", "obstacles/honey/honey.json"));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (ballIsEffected) return false;

        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            ball.getBody().setLinearDamping(damping);
            ballIsEffected = true;

            new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        ball.resetLinearDamping();
                        ballIsEffected = false;

                        cancel();
                    }
                }, effectDuration);

            return false;
        }

        return false;
    }
}
