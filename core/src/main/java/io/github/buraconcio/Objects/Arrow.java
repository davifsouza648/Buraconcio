package io.github.buraconcio.Objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

import java.util.Random;


public class Arrow extends PhysicsEntity {
    public static final Vector2 arrowSize = new Vector2(1.9f, 0.7f);

    public Arrow(Vector2 pos, float speed, float angle) {
        super(pos, arrowSize, "obstacles/arrow/arrow.png");

        body.setType(BodyType.DynamicBody);

        SoundManager.getInstance().loadSound("arrowHit1", "sounds/obstacle-sounds/crossbow/arrow-hits/hit1.wav");
        SoundManager.getInstance().loadSound("arrowHit2", "sounds/obstacle-sounds/crossbow/arrow-hits/hit2.wav");

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

        Random rand = new Random();
        int idHitSound = rand.nextInt(2) + 1;

        SoundManager.getInstance().playProximity("arrowHit" + String.valueOf(idHitSound), this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            if (!ball.isAirborne()) {
                this.destroy();
                ball.getPlayer().die();

                return true;
            }
        } else if (!(entity instanceof BlackHole)) {
            this.destroy();

            return true;
        }

        return false;
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

