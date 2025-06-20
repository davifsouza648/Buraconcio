package io.github.buraconcio.Objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

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

        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey1.wav");
        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey2.wav");
        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey3.wav");

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

            if(ball.getBody().getLinearVelocity().len() > Constants.VELOCITY_HONEY)
            {
                Random rand = new Random();
                int idHitSound = rand.nextInt(3) + 1;

                SoundManager.getInstance().playProximity("honey" + String.valueOf(idHitSound), this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

            }

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
