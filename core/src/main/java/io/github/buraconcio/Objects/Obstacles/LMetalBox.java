package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Common.Auxiliaries;

public class LMetalBox extends Obstacle {
    private static String imagePath = "obstacles/box/metal/metall.png";
    private static String jsonPath = "obstacles/box/metal/metall.json";

    private static Vector2 size = new Vector2(6f, 6f);

    public LMetalBox(Vector2 pos) {
        super(pos, size, Auxiliaries.animationFromFiles(imagePath, jsonPath));

        PolygonShape top = new PolygonShape();
        top.setAsBox(size.x/2, size.y/4, new Vector2(0f, size.y/4), 0f);
        PolygonShape bottom = new PolygonShape();
        bottom.setAsBox(size.x/4, size.y/4, new Vector2(size.x/4, -size.y/4), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = top;

        body.createFixture(fixtureDef);

        fixtureDef.shape = bottom;
        body.createFixture(fixtureDef);

        top.dispose();
        bottom.dispose();
    }

    @Override
    public void place() {
        super.place();
        body.getFixtureList().forEach(fixture -> fixture.setSensor(false));
    }
}
