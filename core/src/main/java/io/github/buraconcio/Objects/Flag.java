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

public class Flag extends Actor {
    private Sprite sprite;
    private Body body;

    private World world;

    public Flag(Vector2 pos, float r, World world) {
        super();

        this.world = world;


        Texture texture = new Texture(Gdx.files.internal("flag.png"));
        sprite = new Sprite(texture);
        sprite.setSize(2*r, 2*r*(sprite.getHeight()/sprite.getWidth()));

        setBounds(pos.x, pos.y, sprite.getWidth(), sprite.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);

        body = world.createBody(bodyDef);
        body.setUserData("Flag");

        CircleShape circle = new CircleShape();
        circle.setRadius(r*Constants.FLAG_LENIENCY);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        circle.dispose();

        this.setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        sprite.setPosition(getX(), getY());
    }

    public Flag(Vector2 pos, World world) {
        this(pos, 1f, world);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }
}

