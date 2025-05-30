package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Contact;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.PhysicsEntity;

import java.util.Iterator;

public class Obstacle extends PhysicsEntity {
    public final static int CLOCKWISE = 0;
    public final static int COUNTER_CLOCKWISE = 1;

   public Obstacle(Vector2 pos, Vector2 size, String texturePath) {
        super(pos, size, texturePath);
    }

    public void applyEffect(Player player) {

    }

    public void erase() {
        PhysicsManager.getInstance().getWorld().destroyBody(body);
        remove();
    }

    // not colliding
    // not in spawn area
    // not in flag area
    public boolean canPlace() {
        if (body == null)
            return false;

        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            if (contact.getFixtureA().getBody().getUserData() == this
                || contact.getFixtureB().getBody().getUserData() == this)
                return false;
        }

        // area de spawn / objetivo podem ser sensor fisico colisao ja seria detectada

        return true;
    }

    public void place() {
        body.getFixtureList().forEach(fixture -> {fixture.setSensor(false);});
    }

    // 0 clock 1 counter
    public void rotate(int direction) {
        body.getTransform().setRotation(body.getTransform().getRotation() + (direction * 2 - 1) * 1.5708f ); // 90 em rad
        PhysicsManager.getInstance().getWorld().step(1/60f, 1, 1);
        System.out.println(body.getTransform().getRotation());
    }

    public void move(Vector2 pos) {
        body.getTransform().setPosition(pos);
    }
}
