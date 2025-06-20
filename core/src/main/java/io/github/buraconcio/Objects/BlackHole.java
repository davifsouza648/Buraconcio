package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import io.github.buraconcio.Utils.Auxiliaries;

public class BlackHole extends Obstacle {
    public static final float ACTION_RADIUS = 5f;
    public static final float G = 0.09f;
    public static final float mass = 3000f;
    public static final float killRadius = 0.5f;

    public BlackHole(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/blackhole/blackhole.png", "obstacles/blackhole/blackHole.json"));

        CircleShape circle = new CircleShape();
        circle.setRadius(ACTION_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        circle.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (!active)
            return false;

        Vector2 gravitational = new Vector2(body.getWorldCenter()).sub(other.getBody().getWorldCenter());
        float dist2 = gravitational.len2();

        if (dist2 < killRadius*killRadius) {

            if (other instanceof Ball) {
                Ball ball = (Ball) other;
                ball.getPlayer().die();

            } else {
                other.destroy();
            }

            return true;
        } else {
            gravitational.nor().scl(G*other.getBody().getMass()*mass/dist2);

            other.applyForce(gravitational);
        }

        return false;
    }
}

