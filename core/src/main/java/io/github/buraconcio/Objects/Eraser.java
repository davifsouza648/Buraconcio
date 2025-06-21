package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.AnimationPlay;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PhysicsManager;

public class Eraser extends Obstacle {
    private static final Vector2 size = new Vector2(1.5f, 1.5f);
    private static final int WHITEOUT_FRAME = 17;

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
        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            PhysicsEntity entityA = PhysicsManager.getInstance().getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = PhysicsManager.getInstance().getEntity(contact.getFixtureB().getBody().getUserData());

            if (entityA.getId() == getId() && entityB instanceof Obstacle) {
                toErase = (Obstacle) entityB;
                return true;
            } else if (entityB.getId() == getId() && entityA instanceof Obstacle) {
                toErase = (Obstacle) entityA;
                return true;
            }
        }

        return false;
    }

    @Override
    public void place() {
        if (toErase != null) {
            eraseAnimation = new AnimationPlay(
                Auxiliaries.animationFromFiles("obstacles/eraser/eraserEffect.png", "obstacles/eraser/eraserEffect.json"), toErase);
            this.disable();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (eraseAnimation != null) {
            if (toErase != null && eraseAnimation.getFrameIndex() == WHITEOUT_FRAME) {
                toErase.destroy();
            } else if (eraseAnimation.isLastFrame()) {
                eraseAnimation.remove();
                this.destroy();
            }
        }
    }
}
