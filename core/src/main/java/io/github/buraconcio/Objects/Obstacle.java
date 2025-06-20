package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.AnimationPlay;

public class Obstacle extends PhysicsEntity {
    public final static int CLOCKWISE = 0;
    public final static int COUNTER_CLOCKWISE = 1;
    private int rotationIndex = 0;

    protected boolean claimed = false;
    protected boolean active = false;
    protected boolean disabled = false;

    public Obstacle(Vector2 pos, Vector2 size, String texturePath) {
        super(pos, size, texturePath);
        body.setType(BodyType.DynamicBody);
    }

    public Obstacle(Vector2 pos, Vector2 size, Animation<TextureRegion> animation) {
        super(pos, size, animation);
        body.setType(BodyType.DynamicBody);
    }

    public Obstacle(Vector2 pos, Vector2 size, AnimationPlay animation) {
        super(pos, size, animation);
        body.setType(BodyType.DynamicBody);
    }

    public Obstacle(Vector2 pos, Vector2 size) {
        super(pos, size);
        body.setType(BodyType.DynamicBody);
    }

    public void preRound() {}

    public void postRound() {}

    // not in flag area
    public boolean canPlace() {
        if (body == null)
            return false;

        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            if (PhysicsManager.getInstance().getEntity(contact.getFixtureA().getBody().getUserData()).getId() == getId()
                || PhysicsManager.getInstance().getEntity(contact.getFixtureB().getBody().getUserData()).getId() == getId())
                return false;
        }

        if (getAABB().overlaps(PhysicsManager.getInstance().getStratingRect()))
            return false;

        return true;
    }

    public Rectangle getAABB() {
        Shape shape;
        try {
            shape = body.getFixtureList().get(0).getShape();
        } catch (Exception e) {return null;}

        if (shape instanceof CircleShape) {
            CircleShape circle = (CircleShape) shape;
            Vector2 position = body.getWorldPoint(circle.getPosition());
            float radius = circle.getRadius();

            return new Rectangle(
                position.x - radius,
                position.y - radius,
                radius * 2,
                radius * 2
            );
        }

        if (shape instanceof PolygonShape) {
            PolygonShape poly = (PolygonShape) shape;
            Vector2 lower = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
            Vector2 upper = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
            Vector2 temp = new Vector2();

            for (int i = 0; i < poly.getVertexCount(); i++) {
                poly.getVertex(i, temp);
                temp = new Vector2(body.getPosition()).add(temp);
                if (temp.x < lower.x) lower.x = temp.x;
                if (temp.y < lower.y) lower.y = temp.y;

                if (temp.x > upper.x) upper.x = temp.x;
                if (temp.y > upper.y) upper.y = temp.y;
            }

            return new Rectangle(lower.x, lower.y, upper.x - lower.x, upper.y - lower.y);
        }

        return null;
    }

    public void place() {
        active = true;
        body.setLinearVelocity(new Vector2(0f, 0f));
        body.setBullet(false);
    }

    public void claim() {
        claimed = true;
        body.setBullet(true);
    }

    public void unclaim() {
        claimed = false;
    }

    public boolean claimed() {
        return claimed;
    }

    // 0 clock 1 counter
    public void rotate(int direction) {

        rotationIndex = (rotationIndex + (direction == CLOCKWISE ? 1 : 3)) % 4;

        body.setTransform(body.getPosition(), body.getTransform().getRotation() + (direction * 2 - 1) * 1.5708f ); // 90 em rad
    }

    public void move(Vector2 pos) {
        Vector2 v = new Vector2(pos).sub(body.getPosition());
        if (v.len2() < 0.001f) {
            body.setLinearVelocity(v.scl(new Vector2(0f, 0f)));
        } else if (v.len2() > 25f) {
            body.setLinearVelocity(v.nor().scl(5f));
        } else {
            body.setLinearVelocity(v.scl(10f));
        }
    }

    public void teleport(Vector2 pos) {
        body.setTransform(pos, body.getTransform().getRotation());
    }

    public void disable() {
        disabled = true;
        animacao.remove();
    }

    public void enable() {
        disabled = false;
        PhysicsManager.getInstance().addToStage(animacao);
    }

    public int getRotationIndex(){
        return rotationIndex;
    }
}
