package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.Constants;

import java.lang.Math;

public class BoostPad extends Obstacle {

    public BoostPad (Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/boostpad/boostpad.png", "obstacles/boostpad/boostpad.json"));

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        shape.setAsBox(size.x/2, size.y/2);
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
        }

        return true;
    }
}
