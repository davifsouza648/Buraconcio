package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.AnimationPlay;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.PhysicsEntity;

import java.util.Iterator;

public class Obstacle extends PhysicsEntity {
    public final static int CLOCKWISE = 0;
    public final static int COUNTER_CLOCKWISE = 1;

    protected boolean claimed = false;
    protected boolean active = false;

    public Obstacle(Vector2 pos, Vector2 size, String texturePath) {
        super(pos, size, texturePath);
    }

    public Obstacle(Vector2 pos, Vector2 size, Animation<TextureRegion> animation) {
        super(pos, size, animation);
    }

    public Obstacle(Vector2 pos, Vector2 size, AnimationPlay animation) {
        super(pos, size, animation);
    }

    public Obstacle(Vector2 pos, Vector2 size) {
        super(pos, size);
    }

    public void applyEffect(Player player) {}

    public void preRound() {}

    public void postRound() {}

    // not colliding
    // not in spawn area
    // not in flag area
    public boolean canPlace() {
        if (body == null)
            return false;

        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            if (PhysicsManager.getInstance().getEntity(contact.getFixtureA().getBody().getUserData()).getId() == getId()
                || PhysicsManager.getInstance().getEntity(contact.getFixtureB().getBody().getUserData()).getId() == getId())
                return false;
        }

        // area de spawn / objetivo podem ser sensor fisico colisao ja seria detectada

        return true;
    }

    public void place() {
        active = true;
    }

    public void claim() {
        claimed = true;
    }

    public void unclaim() {
        claimed = false;
    }

    public boolean claimed() {
        return claimed;
    }

    // 0 clock 1 counter
    public void rotate(int direction) {
        body.setTransform(body.getPosition(), body.getTransform().getRotation() + (direction * 2 - 1) * 1.5708f ); // 90 em rad
    }

    public void move(Vector2 pos) {
        body.setTransform(pos, body.getTransform().getRotation());
    }
}
