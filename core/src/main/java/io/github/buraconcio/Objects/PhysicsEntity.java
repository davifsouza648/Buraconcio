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

import java.lang.Runnable;

public class PhysicsEntity extends Actor {

    protected Sprite sprite;
    protected Body body;
    protected int id;

    public PhysicsEntity(Vector2 pos, Vector2 size, String texturePath) {
        super();

        setPosition(pos.x, pos.y);
        setOrigin(Align.center);

        setSize(size.x, size.y);

        if(texturePath != null)
        {
            Texture texture = new Texture(Gdx.files.internal(texturePath));
            sprite = new Sprite(texture);
            sprite.setSize(size.x, size.x*(sprite.getHeight()/sprite.getWidth()));
            sprite.setOriginCenter();
            setSize(sprite.getWidth() * sprite.getScaleX(), sprite.getHeight() * sprite.getScaleY());
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);

        body = PhysicsManager.getInstance().getWorld().createBody(bodyDef);

        PhysicsManager.getInstance().addEntity(this);
        PhysicsManager.getInstance().addToStage(this);

        setVisible(true);
    }

    public void destroy() {
        Runnable task = () -> {
            PhysicsManager.getInstance().destroyBody(body);
            remove();
        };
        PhysicsManager.getInstance().schedule(task);

        /*
        PhysicsManager.getInstance().destroyBody(body);
        remove();
        */
    }

    public void setId(int id) {
        body.setUserData(id);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // return true if contact should be removed on collission
    public boolean contact(PhysicsEntity entity) {
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sprite == null)
            return;

        sprite.setRotation(getRotation());
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setOrigin(Align.center);
        this.setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        this.setRotation(body.getAngle() * 180f/3.14f);
    }

    public Body getBody() {
        return body;
    }
}
