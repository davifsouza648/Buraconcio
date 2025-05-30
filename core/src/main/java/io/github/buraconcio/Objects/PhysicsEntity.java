package io.github.buraconcio.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PhysicsManager;

public class PhysicsEntity extends Actor {
    protected Sprite sprite;
    protected Body body;

    public PhysicsEntity(Vector2 pos, Vector2 size, String texturePath) {
        super();

        setPosition(pos.x, pos.y);
        setOrigin(Align.center);

        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);
        sprite.setSize(2*size.x, 2*size.x*(sprite.getHeight()/sprite.getWidth()));
        sprite.setOriginCenter();

        setSize(sprite.getWidth(), sprite.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);

        body = PhysicsManager.getInstance().getWorld().createBody(bodyDef);
        body.setUserData(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setRotation(getRotation());
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        this.setRotation(body.getAngle() * 180f/3.14f);
   }
}
