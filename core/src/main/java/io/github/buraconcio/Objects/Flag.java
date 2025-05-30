package io.github.buraconcio.Objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Objects.PhysicsEntity;

public class Flag extends PhysicsEntity {

    public Flag(Vector2 pos, float r) {
        super(pos, new Vector2(r, r), "flag.png");

        body.setUserData("Flag");

        CircleShape circle = new CircleShape();
        circle.setRadius(r*Constants.FLAG_LENIENCY);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        circle.dispose();
    }

    public Flag(Vector2 pos) {
        this(pos, 1f);
    }
}
