package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class Star extends Obstacle {
    private static final Vector2 size = new Vector2(1f, 1f);

    public Star(Vector2 pos) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/star/star.png", "obstacles/star/star.json"));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth()/2, getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = polygonShape;

        SoundManager.getInstance().loadSound("starPickUp", "sounds/obstacle-sounds/star/startPickUp.wav");

        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (disabled || !active)
            return false;

        if (other instanceof Ball) {
            Ball ball = (Ball) other;

            ball.getPlayer().collectStar();
            SoundManager.getInstance().playProximity("starPickUp", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
            this.disable();
        }

        return true;
    }

    @Override
    public void postRound() {
        enable();
    }
}
