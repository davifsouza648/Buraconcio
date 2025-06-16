package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;

public class Trampoline extends Obstacle {
    public Trampoline(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/trampoline/trampoline.png", "obstacles/trampoline/trampoline.json"));
        animacao.pauseAnimation();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (!active) // ate meio desncessario mas fds
            return false;

        if (other instanceof Ball) {
            animacao.playOnce();

            Ball ball = (Ball) other;

            ball.jump(2f);

            return true;
        }
        return false;
    }
}

