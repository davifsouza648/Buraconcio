package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PhysicsManager;

public class Star extends Obstacle {
    private boolean disabled = false;

    public Star(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/star/star.png", "obstacles/star/star.json"));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth()/2, getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (disabled)
            return true;

        if (other instanceof Ball) {
            Ball ball = (Ball) other;

            ball.getPlayer().collectStar();

            this.disable();
        }

        return true;
    }

    private void disable() {
        disabled = true;
        animacao.remove();
    }

    private void enable() {
        disabled = false;
        PhysicsManager.getInstance().addToStage(animacao);
    }

    @Override
    public void postRound() {
        enable();
    }
}
