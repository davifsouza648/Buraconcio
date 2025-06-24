package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.AnimationPlay;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PhysicsManager;

public class Eraser extends Obstacle {
    private static final Vector2 size = new Vector2(1.5f, 1.5f);

    private Obstacle toErase = null;
    AnimationPlay eraseAnimation = null;

    public Eraser(Vector2 pos) {
        super(pos, size, "obstacles/eraser/eraser.png");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public boolean canPlace() {
        return true;
    }

    @Override
    public void place() {
        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            PhysicsEntity entityA = PhysicsManager.getInstance().getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = PhysicsManager.getInstance().getEntity(contact.getFixtureB().getBody().getUserData());

            if (entityA.getId() == getId() && entityB instanceof Obstacle) {
                toErase = (Obstacle) entityB;
                break;
            } else if (entityB.getId() == getId() && entityA instanceof Obstacle) {
                toErase = (Obstacle) entityA;
                break;
            }
        }

        if (toErase != null) {
            new EraserAnimation(toErase);
            this.disable();
        } else {
            new EraserAnimation(this);
            this.disable();
        }
    }
}

class EraserAnimation extends AnimationPlay {
    private static final int WHITEOUT_FRAME = 16;

    public EraserAnimation(Actor parentActor) {
            super( Auxiliaries.animationFromFiles("obstacles/eraser/eraserEffect.png",
                "obstacles/eraser/eraserEffect.json"), parentActor);

            pauseAnimation();
            playOnce();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getFrameIndex() == WHITEOUT_FRAME) {
            if (parentActor instanceof PhysicsEntity) {
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
