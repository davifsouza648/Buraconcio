package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Objects.Game.Ball;

public class Train extends Obstacle {
    private final Vector2 direction;
    private static final Vector2 size = new Vector2(2f, 11.59f);

    private boolean invincible = true;
    private TrainCollider trainCollider;

    public Train(Vector2 pos, int directionIndex)
    {
        super(pos, size, Auxiliaries.animationFromFiles("obstacles/train/train.png", "obstacles/train/train.json"));

        PolygonShape shapeDef = new PolygonShape();
        shapeDef.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeDef;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        shapeDef.dispose();

        body.setType(BodyType.KinematicBody);
        body.setBullet(true);

        this.direction = getDirectionFromIndex(directionIndex).nor().scl(8f);
        body.setLinearVelocity(this.direction);

        trainCollider = new TrainCollider(pos, size, this.direction, directionIndex, this);

        Timer.schedule(new Task() {
            public void run() {
                invincible = false;
            }
        }, 0.5f);

        switch (directionIndex)
        {
            case 0:
                body.setTransform(body.getPosition(), (float) (-Math.PI / 2)); // -90°
                break;
            case 1:
                body.setTransform(body.getPosition(), 0f);
                break;
            case 2:
                body.setTransform(body.getPosition(), (float) (Math.PI / 2));
                break;
            case 3:
                body.setTransform(body.getPosition(), (float) Math.PI);
                break;
        }

        act(0f);
    }

    private Vector2 getDirectionFromIndex(int dir) {
        switch (dir) {
            case 0: return new Vector2(1, 0);   // direita
            case 1: return new Vector2(0, 1);   // cima
            case 2: return new Vector2(-1, 0);  // esquerda
            case 3: return new Vector2(0, -1);  // baixo
            default: return new Vector2(1, 0);  // padrão: direita
        }
    }

    @Override
    public boolean contact(PhysicsEntity entity)
    {
        if (invincible) return false;

        if (!(entity instanceof Ball || entity instanceof Train ||
            entity instanceof BlackHole || entity instanceof TrainCollider))
        {
            this.destroy();
            trainCollider.destroy();
            return true;
        }


        return false;
    }
}

class TrainCollider extends Obstacle {
    private Train parent;

    public TrainCollider(Vector2 pos, Vector2 size, Vector2 vel, int directionIndex, Train parent) {
        super(pos, size);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x/4, 1f, new Vector2(0f, size.y/2 - 1f), 0f);

        FixtureDef def = new FixtureDef();
        def.isSensor = true;
        def.shape = shape;

        body.createFixture(def);
        shape.dispose();

        body.setType(BodyType.DynamicBody);
        body.setLinearVelocity(vel);

        this.parent = parent;

        switch (directionIndex)
        {
            case 0:
                body.setTransform(body.getPosition(), (float) (-Math.PI / 2)); // -90°
                break;
            case 1:
                body.setTransform(body.getPosition(), 0f);
                break;
            case 2:
                body.setTransform(body.getPosition(), (float) (Math.PI / 2));
                break;
            case 3:
                body.setTransform(body.getPosition(), (float) Math.PI);
                break;
        }

        act(0f);
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        return parent.contact(entity);
    }
}
