package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Objects.Arrow;
import io.github.buraconcio.Objects.Flag;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Ball extends PhysicsEntity {
    private Player player;

    private Group labelGroup;

    private Vector2 mouseMovement;

    public Ball(Vector2 pos, float d, Player player) {
        super(pos, new Vector2(d, d), "ballteste.png");

        body.setType(BodyType.DynamicBody);
        body.setLinearDamping(0.5f);
        body.setAngularDamping(1f);

        CircleShape circle = new CircleShape();
        circle.setRadius(d/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density  = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);
        circle.dispose();

        Skin skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        Label labelUserName = new Label(player.getUsername(), skinLabel, "labelPixelyWhite32");
        labelUserName.setAlignment(Align.center);
        labelUserName.pack();

        labelGroup = new Group(); // melhor jeito de escalar texto ... zuado
        labelGroup.addActor(labelUserName);
        labelGroup.setScale(0.02f);

        PhysicsManager.getInstance().addToStage(labelGroup);

        this.player = player;

        mouseMovement = new Vector2(0, 0);
    }

    public boolean isStill()
    {
        return body.getLinearVelocity().len() < Constants.STILL_TOLERANCE;
    }

    public void applyImpulse(Vector2 impulse)
    {
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public Vector2 calculateImpulse(Vector2 mouse1, Vector2 mouse2) {
        Vector2 diff = mouse1.sub(mouse2);

        float magnitude = (diff.len() / Constants.MAX_IMPULSE_DISTANCE) * Constants.MAX_IMPULSE;
        if (magnitude > Constants.MAX_IMPULSE) magnitude = Constants.MAX_IMPULSE;

        diff.setLength(magnitude);

        return diff;
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        labelGroup.setPosition(
            getX() - labelGroup.getWidth()/2,
            getY() + getHeight() + 0.5f
        );

        if (mouseMovement.len() > 0.01f) {
            final float segmentLength = 1f;

            Texture texture = new Texture(Gdx.files.internal("shootingGuideEnd.png"));
            Sprite endSprite = new Sprite(texture);
            endSprite.setSize(segmentLength, segmentLength);
            endSprite.setOriginCenter();

            // prioritize positioning end sprite center at end of line seg
            Vector2 center = new Vector2(getX() + getWidth()/2, getY() + getHeight()/2);

            endSprite.setPosition(center.x - mouseMovement.x - endSprite.getWidth()/2, center.y - mouseMovement.y - endSprite.getHeight()/2);
            endSprite.setRotation(mouseMovement.angleDeg());
            endSprite.draw(batch);

            // prioritize positioning tip to represent actual impulse
            Vector2 impulse = calculateImpulse(new Vector2(0f, 0f), mouseMovement).scl(0.1f);

            texture = new Texture(Gdx.files.internal("shootingGuideTip.png"));
            Sprite tipSprite = new Sprite(texture);
            tipSprite.setSize(segmentLength, segmentLength);
            tipSprite.setOriginCenter();

            tipSprite.setPosition(center.x - impulse.x - tipSprite.getWidth()/2, center.y - impulse.y - tipSprite.getHeight()/2);
            tipSprite.setRotation(mouseMovement.angleDeg());
            tipSprite.draw(batch);

            texture = new Texture(Gdx.files.internal("shootingGuideSegment.png"));
            Sprite segmentSprite = new Sprite(texture);

            Vector2 seg = new Vector2(tipSprite.getX() - endSprite.getX(), tipSprite.getY() - endSprite.getY());
            segmentSprite.setSize(seg.len(), segmentLength);
            segmentSprite.setOriginCenter();
            segmentSprite.setRotation(mouseMovement.angleDeg());


            Vector2 pos = new Vector2(endSprite.getX() + endSprite.getWidth()/2, endSprite.getY() + endSprite.getHeight()/2);
            pos.add(seg.scl(0.5f));
            pos.sub(new Vector2(segmentSprite.getWidth()*0.5f, segmentSprite.getHeight()*0.5f));
            segmentSprite.setPosition(pos.x, pos.y);
            segmentSprite.draw(batch);
        }

        super.draw(batch, parentAlpha);
    }

    public void enterHole() {
        body.setLinearVelocity(new Vector2(0f, 0f));
        body.setAwake(false);

        labelGroup.setVisible(false);

        // body.setTransform(new Vector2(-10f, -10f), 0f);
        setVisible(false);
    }

    public Vector2 getPosition() {
        Vector2 pos = new Vector2(this.getX(), this.getY());
        return pos;
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (entity instanceof Arrow) {
            if (PlayerManager.getInstance().getLocalPlayer().getId() == player.getId()) // player should only die if hit on his own screen
                player.die();
        } else if (entity instanceof Flag) {
            player.score();
        }

        return true;
    }

    public void setShootingGuide(Vector2 mouse1, Vector2 mouse2) {
        //mouseMovement = mouse1.sub(mouse2);
        mouseMovement.x = mouse1.x - mouse2.x;
        mouseMovement.y = mouse1.y - mouse2.y;
    }

    public void resetShootingGuide() {
        mouseMovement.x = mouseMovement.y = 0;
    }

    public void setAngle(float angle) {
        body.setTransform(body.getPosition(), body.getTransform().getRotation() + angle ); // 90 em rad
    }

    public void setPos(Vector2 pos) {
        body.setTransform(pos, body.getTransform().getRotation());
    }

    public void setVelocity(Vector2 vel) {
        body.setLinearVelocity(vel);
    }

}
