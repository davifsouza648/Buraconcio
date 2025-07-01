package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Common.AnimationPlay;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;
import io.github.buraconcio.Objects.Game.Ball;

public class Train extends Obstacle {
    private final Vector2 direction;
    private static final Vector2 size = new Vector2(2f, 11.59f);

    private boolean invincible = true;
    private TrainCollider trainCollider;

    int directionIndex;

    public Train(Vector2 pos, int directionIndex)
    {
        super(pos, size, Auxiliaries.animationFromFiles("obstacles/train/train.png", "obstacles/train/train.json"));

        PolygonShape shapeDef = new PolygonShape();
        shapeDef.setAsBox(size.x/2, size.y/2);

        this.directionIndex = directionIndex;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeDef;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        shapeDef.dispose();

        SoundManager.getInstance().loadLoopSound("train-chuck", "sounds/obstacle-sounds/train/train-chuck.wav");

        this.claimed = true;

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

        body.setTransform(body.getPosition(), angleFromIndex(directionIndex));

        act(0f);
    }

    public static float angleFromIndex(int directionIndex) {
        float res = switch (directionIndex)
        {
            case 0 -> (float) (-Math.PI / 2); // -90°
            case 1 -> 0f;
            case 2 -> (float) (Math.PI / 2); // -90°
            case 3 -> (float) Math.PI; // -90°
            default -> 0f;
        };

        return res;
    }

    private static Vector2 getDirectionFromIndex(int dir) {
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
            trainCollider.destroy();

            body.setLinearVelocity(new Vector2(0f, 0f));
            new TrainDeathAnimation(this);

            return true;
        }


        return false;
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        SoundManager.getInstance().loopProximity("train-chuck", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
    }

    public Vector2 getHeadPos() {
        if (trainCollider == null)
            return null;

        return trainCollider.getPosition();
    }

    public int getDirectionIndex() 
    {
    return directionIndex;
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

        body.setTransform(body.getPosition(), Train.angleFromIndex(directionIndex));

        act(0f);
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        return parent.contact(entity);
    }
}

class TrainDeathAnimation extends AnimationPlay {
    private static final int WHITEOUT_FRAME = 4;
    private final Train parentActor;

    public TrainDeathAnimation(Train parentActor) {
        super(
            Auxiliaries.animationFromFiles("obstacles/train/trainDestruction.png",
            "obstacles/train/trainDestruction.json"), 
            parentActor
        );

        this.parentActor = parentActor;

        Vector2 base = parentActor.getPosition(); 
        float angleRad = Train.angleFromIndex(parentActor.getDirectionIndex()); 


        float halfLength = 11.59f / 2f;
        Vector2 offset = new Vector2(0, halfLength).rotateRad(angleRad);

        // Posição do bico = centro + offset rotacionado
        Vector2 head = base.cpy().add(offset);
        setPosition(head.x, head.y);

        pauseAnimation();
        playOnce();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getFrameIndex() == WHITEOUT_FRAME) 
        {
            if (parentActor instanceof PhysicsEntity) 
            {
                PhysicsEntity p = (PhysicsEntity) parentActor;
                try {
                    p.destroy();
                } catch (Error e) {}
            }
        } else if (isLastFrame()) {
            try {
                this.remove();
            } catch (Error e) {}
        }
    }
}

