package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.PhysicsEntity;

public class Arrow extends PhysicsEntity {
    public static final Vector2 arrowSize = new Vector2(0.8f, 0.3f);

    public Arrow(Vector2 pos, float speed, float angle) {
        super(pos, arrowSize, "arrow.png");

        body.setType(BodyType.DynamicBody);

        PolygonShape shapeDef = new PolygonShape();
        shapeDef.setAsBox(arrowSize.x, arrowSize.y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeDef;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shapeDef.dispose();

        body.getTransform().setRotation(angle);
        body.setLinearVelocity(new Vector2(speed, 0).setAngleDeg(angle));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getX() > 100f) this.destroy();

        for (Contact contact : PhysicsManager.getInstance().getContactList()) {
            if (contact.getFixtureA().getBody().getUserData() == this
                || contact.getFixtureB().getBody().getUserData() == this) {

                Object other = contact.getFixtureA().getBody().getUserData();
                if (other == this.toString()) other = contact.getFixtureB().getBody().getUserData();

                try {
                    Player player = PlayerManager.getInstance().getPlayer(Integer.parseInt(other.toString()));

                    if (PlayerManager.getInstance().getLocalPlayer() == player) { // only kill local player
                        Runnable task = () -> {player.die();};
                        PhysicsManager.getInstance().schedule(task);
                    }
                } catch (Exception e) {}

                this.destroy();
            }
        }
    }
}

