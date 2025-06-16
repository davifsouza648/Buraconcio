package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import io.github.buraconcio.Utils.Constants;

public class Flag extends PhysicsEntity {

    public Flag(Vector2 pos, float r) {
        super(pos, new Vector2(r, r), "hole.png");

        CircleShape circle = new CircleShape();
        circle.setRadius(r*Constants.FLAG_LENIENCY);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        circle.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            if (!ball.isAirborne()) {
                ball.getPlayer().score();
                return true;
            }
        }

        return false;
    }

    public Flag(Vector2 pos) {
        this(pos, 1f);
    }
}
