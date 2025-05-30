package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.CircleShape;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Objects.PhysicsEntity;

public class Ball extends PhysicsEntity {

    public Ball(Vector2 pos, float r, int id) {
        super(pos, new Vector2(r, r), "ball.png");

        body.setType(BodyType.DynamicBody);
        body.setLinearDamping(1f);
        body.setAngularDamping(1f);
        body.setUserData(id);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density  = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);

        circle.dispose();
    }

    public void applyImpulse(Vector2 impulse) {
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public Vector2 calculateImpulse(Vector2 mouse1, Vector2 mouse2) {
        Vector2 diff = mouse1.sub(mouse2);

        float magnitude = (diff.len() / Constants.MAX_IMPULSE_DISTANCE) * Constants.MAX_IMPULSE;
        if (magnitude > Constants.MAX_IMPULSE) magnitude = Constants.MAX_IMPULSE;

        diff.setLength(magnitude);

        return diff;
    }

    public void enterHole() {
        body.setLinearVelocity(new Vector2(0f, 0f));

        body.setTransform(new Vector2(-10f, -10f), 0f);
    }
}
