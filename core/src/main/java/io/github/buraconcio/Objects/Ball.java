package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.Batch;
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

    public Ball(Vector2 pos, float r, Player player) {
        super(pos, new Vector2(r, r), "ballteste.png");

        body.setType(BodyType.DynamicBody);
        body.setLinearDamping(0.5f);
        body.setAngularDamping(1f);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

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
        super.draw(batch, parentAlpha);

        labelGroup.setPosition(
            getX() - labelGroup.getWidth()/2,
            getY() + getHeight() + 0.5f
        );
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
}
