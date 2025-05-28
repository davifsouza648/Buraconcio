package io.github.buraconcio.Objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import io.github.buraconcio.Utils.Constants;

public class Ball extends Actor {
    private Sprite sprite;
    private Body body;

    private World world;

    public Ball(Vector2 pos, float r, World world, int id) {
        super();

        this.world = world;

        setBounds(pos.x, pos.y, 2*r, 2*r);

        Texture texture = new Texture(Gdx.files.internal("ball.png"));
        sprite = new Sprite(texture);
        sprite.setSize(2*r, 2*r);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.linearDamping = 1f;

        body = world.createBody(bodyDef);
        body.setUserData(id);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density  = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);

        circle.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
   }

    public void applyImpulse(Vector2 impulse) {
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public Vector2 calculateImpulse(Vector2 mouse1, Vector2 mouse2) {
        Vector2 diff = mouse1.sub(mouse2);

        float magnitude = (diff.len() / Constants.MAX_IMPULSE_DISTANCE) * Constants.MAX_IMPULSE;
        if (magnitude > Constants.MAX_IMPULSE) magnitude = Constants.MAX_IMPULSE;

        diff.setLength(magnitude);

        return diff;
    }
}
