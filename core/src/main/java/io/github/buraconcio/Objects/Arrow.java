package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Objects.PhysicsEntity;

import java.lang.Math;

public class Arrow extends PhysicsEntity {
    public static final Vector2 arrowSize = new Vector2(1.6f, 0.6f);

    public Arrow(Vector2 pos, float speed, float angle) {
        super(pos, arrowSize, "obstacles/arrow/arrow.png");

        body.setType(BodyType.DynamicBody);

        PolygonShape shapeDef = new PolygonShape();
        shapeDef.setAsBox(arrowSize.x/2, arrowSize.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeDef;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shapeDef.dispose();

        body.setTransform(body.getPosition(), angle);
        body.setLinearVelocity(new Vector2((float) Math.cos(angle), (float) Math.sin(angle)).scl(speed));

        act(0f); // to update sprite before rendering
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        this.destroy();

        return true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getX() > 200f) this.destroy();
        if (getX() < -200f) this.destroy();
        if (getY() > 200f) this.destroy();
        if (getY() < -200f) this.destroy();
    }
}

