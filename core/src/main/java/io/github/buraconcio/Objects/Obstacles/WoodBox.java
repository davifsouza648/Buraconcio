package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.Auxiliaries;

public class WoodBox extends Obstacle {
    protected static String imagePath = "obstacles/box/wood/box32x32.png";
    protected static String jsonPath = "obstacles/box/wood/box32x32.json";

    protected static Vector2 size = new Vector2(3f, 3f);

    public WoodBox(Vector2 pos) {
        this(pos, size, imagePath, jsonPath);
    }

    public WoodBox(Vector2 pos, Vector2 size, String imagePath, String jsonPath) {
        super(pos, size,
            Auxiliaries.animationFromFiles(imagePath, jsonPath));

        animacao.setFrameDuration(0.04f);
        animacao.pauseAnimation();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (!active) return false;
        if (disabled) return true;

        animacao.playOnce();

        return true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (animacao.isLastFrame()) {
            disable();
        }
    }

    @Override
    public void disable() {
        super.disable();
        body.getFixtureList().forEach(fixture -> fixture.setSensor(true));
    }

    @Override
    public void enable() {
        super.enable();
        body.getFixtureList().forEach(fixture -> fixture.setSensor(false));
    }


    @Override
    public void place() {
        super.place();

        body.getFixtureList().forEach(fixture -> fixture.setSensor(false));
    }
}
