package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

public class Trampoline extends Obstacle {
    private static final Vector2 size = new Vector2(2f, -1f);

    public Trampoline(Vector2 pos) {
        super(pos, size,
        Auxiliaries.animationFromFiles("obstacles/trampoline/trampoline.png", "obstacles/trampoline/trampoline.json"));
        animacao.pauseAnimation();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x/2, size.x/2, new Vector2(0f, -getHeight()/2 + size.x/2), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        SoundManager.getInstance().loadSound("trampoline", "sounds/obstacle-sounds/trampoline/trampoline.mp3");

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
            SoundManager.getInstance().playProximity("trampoline", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
            ball.jump(2f);


            return true;
        }
        return false;
    }
}

