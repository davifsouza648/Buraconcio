package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

import java.lang.Math;

public class BoostPad extends Obstacle {
    private static final Vector2 size = new Vector2(3f, -1f);

    public BoostPad (Vector2 pos) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/boostpad/boostpad.png", "obstacles/boostpad/boostpad.json"));

        SoundManager.getInstance().loadSound("boostPad", "sounds/obstacle-sounds/boostpad/boostPad.wav");

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        shape.setAsBox(size.x/2, getHeight()/2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (other instanceof Ball && active) {
            Ball ball = (Ball) other;
            ball.applyImpulse(
                new Vector2(Constants.BOOST_IMPULSE * (float) Math.cos(body.getAngle()),
                    Constants.BOOST_IMPULSE * (float) Math.sin(body.getAngle())));

            SoundManager.getInstance().playProximity("boostPad", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
        }

        return true;
    }
}
