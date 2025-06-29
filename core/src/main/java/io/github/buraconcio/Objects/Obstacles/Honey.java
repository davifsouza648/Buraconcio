package io.github.buraconcio.Objects.Obstacles;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class Honey extends Obstacle {
    private static final Vector2 size = new Vector2(2f, -1f);
    private static final float damping = 30f;
    private static final float effectDuration = 2000f; //ms

    private HashMap<Integer, Float> playersHoneyTimer;

    public Honey(Vector2 pos) {
        super(pos, size,
        Auxiliaries.animationFromFiles("obstacles/honey/honey.png", "obstacles/honey/honey.json"));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(size.x/2, getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey1.wav");
        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey2.wav");
        SoundManager.getInstance().loadSound("honey1", "sounds/obstacle-sounds/honey/honey3.wav");

        body.createFixture(fixtureDef);

        polygonShape.dispose();

        playersHoneyTimer = new HashMap<Integer, Float>();
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (!active) return false;

        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            ball.getBody().setLinearDamping(damping);

            playersHoneyTimer.put(ball.getPlayer().getId(), effectDuration);

            if(ball.getBody().getLinearVelocity().len() > Constants.VELOCITY_HONEY)
            {
                Random rand = new Random();
                int idHitSound = rand.nextInt(3) + 1;

                SoundManager.getInstance().playProximity("honey" + String.valueOf(idHitSound), this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

            }

            return false;
        }

        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        playersHoneyTimer.forEach((Integer key, Float value) -> {
            if (value > -1f) value -= delta;

            if (delta < 0f)
                PlayerManager.getInstance().getPlayer(key).getBall().resetLinearDamping();
        });
    }
}
