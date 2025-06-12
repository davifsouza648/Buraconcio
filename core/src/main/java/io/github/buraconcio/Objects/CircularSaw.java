package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Align;

import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PhysicsManager;

public class CircularSaw extends Obstacle {
    private float elapsedTime = 0f;

    public CircularSaw(Vector2 pos, Vector2 size) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/circularSaw/circularSaw.png", "obstacles/circularSaw/circularSaw.json"));

        animacao.pauseAnimation();

        createSawFixture(0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        elapsedTime += delta;

        if (!active) elapsedTime = 0f;

        float sawPosition = ((float) Math.cos(0.25*elapsedTime/animacao.getFrameDuration())*getWidth()/2) * 0.7f;
        System.out.println(sawPosition);

        /*
        Transform current = body.getTransform();
        body.setTransform(sawPosition + current.getPosition().x, current.getPosition().y, current.getRotation());

        animacao.setPosition(body.getPosition().x - sawPosition, body.getPosition().y);
        */

        body.destroyFixture(body.getFixtureList().get(0));

        createSawFixture(sawPosition);
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (other instanceof Ball) {

            Ball ball = (Ball) other;
            ball.getPlayer().die();

            return true;
        }

        return false;
    }

    @Override
    public boolean canPlace() {
        if (!super.canPlace()) // only the saw hitbox
            return false;

        Rectangle bb = new Rectangle(
            getX(), getY(),
            getWidth(), getHeight()
        );

        if (bb.overlaps(PhysicsManager.getInstance().getStratingRect()))
            return false;

        return true;
    }

    @Override
    public void preRound() {
        animacao.resumeAnimation();
    }

    private void createSawFixture(float sawPosition) {
        CircleShape circle = new CircleShape();
        circle.setRadius(getHeight()/2);
        circle.setPosition(new Vector2(sawPosition, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        circle.dispose();
    }
}

