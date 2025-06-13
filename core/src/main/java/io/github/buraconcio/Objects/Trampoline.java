package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Align;

import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PhysicsManager;

public class Trampoline extends Obstacle {
    private float elapsedTime = 0f;

    private static final int idleFrames = 4; // por favor ninguem troca a animacao velho tem mt hard code
    private int animationDuration;
    private int framesToMove;
    private float circleRadius;

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
        if (other instanceof Ball) {
            Ball ball = (Ball) other;

            ball.jump(2f);

            return true;
        }
        return false;
    }
}

