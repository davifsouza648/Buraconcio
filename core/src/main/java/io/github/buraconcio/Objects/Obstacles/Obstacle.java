package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Common.AnimationPlay;

public class Obstacle extends PhysicsEntity {
    public final static int CLOCKWISE = 0;
    public final static int COUNTER_CLOCKWISE = 1;
    private int rotationIndex = 0;
    private Vector2 targetPos = null;

    private boolean teleported = false;
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

    @Override
    public void act(float delta) {
        super.act(delta);

        if (targetPos != null) {
            move(targetPos);

            if (body.getPosition().cpy().sub(targetPos).len2() < 0.01f)
                targetPos = null;
        }
    }

    // not in flag area
    public boolean canPlace() {
        if (body == null)
            return false;

        for (Contact contact : PhysicsManager.getInstance().getContactList())
        {
            Object fixtureA,fixtureB;
            try
            {
                fixtureA = contact.getFixtureA().getBody().getUserData();
                fixtureB = contact.getFixtureB().getBody().getUserData();
            }catch(Exception e){return false;}


            if (fixtureA == null || fixtureB == null)
                continue;

            PhysicsEntity entityA = PhysicsManager.getInstance().getEntity(fixtureA);
            PhysicsEntity entityB = PhysicsManager.getInstance().getEntity(fixtureB);
            if (entityA == null || entityB == null)
                continue;

            if (entityA.getId() == getId() || entityB.getId() == getId())
                return false;
        }

        Rectangle boundingBox = getAABB();
        if (boundingBox != null && boundingBox.overlaps(PhysicsManager.getInstance().getStratingRect()))
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
        body.setType(BodyType.KinematicBody);
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

    public void markAsTeleported(boolean flag){
        teleported = flag;
    }

    public boolean wasTeleported(){
        return teleported;
    }

    // 0 clock 1 counter
    public void rotate(int direction) {

        rotationIndex = (rotationIndex + (direction == CLOCKWISE ? 1 : 3)) % 4;

        body.setTransform(body.getPosition(), body.getTransform().getRotation() + (direction * 2 - 1) * 1.5708f ); // 90 em rad
    }

    public void setTargetPos(Vector2 pos) {
        targetPos = pos;
    }

    public void move(Vector2 pos) {
        Vector2 v = new Vector2(pos).sub(body.getPosition());

        if (v.len2() < 0.01f) {
            body.setLinearVelocity(new Vector2(0f, 0f));
        } else {
            body.setLinearVelocity(v.scl(10f));
        }
    }

    public void flashRed() {
        animacao.flashRed();
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
