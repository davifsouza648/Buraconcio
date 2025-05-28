package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Ball;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PhysicsTest implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;

    private Ball testBall;

    private World world;
    private float tickrate;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    public PhysicsTest(Main game) {
        this.game = game;
        tickrate = 1/60f;

        world = new World(new Vector2(0f, 0f), true);

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(23, 13);

        stage = new Stage(new FitViewport(23, 13));
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);

        //skin = new Skin(Gdx.files.internal("backgroundMenu.json"), new TextureAtlas(Gdx.files.internal("backgroundMenu.jpg"))); // usa fonte padrão

        testBall = new Ball(3f, 3f, 1f, world);

        stage.addActor(testBall);

        BodyDef wallDef = new BodyDef();
        wallDef.position.set(new Vector2(stage.getWidth()/2, stage.getHeight()));
        Body wall = world.createBody(wallDef);
        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(2f, 2f);
        wall.createFixture(wallBox, 0f);
        wallBox.dispose();

        Gdx.input.setInputProcessor(new InputAdapter() {
            private Vector2 mouse1;

            // convert mouse coords (top left) to stage coords (bottom left)
            private void convertCoords(Vector2 pos) {
                pos.y = 0;
            }

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                mouse1 = new Vector2(x, y);

                return true;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                Vector2 mouse2 = new Vector2(x, y);
                testBall.applyImpulse(testBall.calculateImpulse(mouse1, mouse2));

                return true;
            }
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();

        debugRenderer.render(world, camera.combined);

        world.step(tickrate, 6, 2);
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
