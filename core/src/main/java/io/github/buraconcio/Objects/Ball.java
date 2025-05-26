package io.github.buraconcio.Objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;

public class Ball extends Actor {
    private float xSpeed, ySpeed;

    private Sprite sprite;
    private Body body;

    private World world;

    public Ball(float x, float y, float r, float xSpeed, float ySpeed, World world) {
        super();

        this.world = world;

        setBounds(x, y, r, r);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;

        Texture texture = new Texture(Gdx.files.internal("ball.png"));
        sprite = new Sprite(texture);
        sprite.setSize(2*r, 2*r);

        //sprite.setPosition(getX(), getY());
        //sprite.setRotation(0);

        //sprite.setBounds(x, y, r, r);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density  = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        //fixture = body.createFixture(fixtureDef);
        body.createFixture(fixtureDef);

        circle.dispose();


        body.setLinearVelocity(new Vector2(this.xSpeed, this.ySpeed));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.setPosition(body.getPosition().x - getWidth(), body.getPosition().y - getHeight());
   }
}
