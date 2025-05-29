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
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Objects.Flag;
import io.github.buraconcio.Utils.PlayerManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PhysicsTest implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;

    private Ball testBall;
    private Flag testFlag;

    private World world;
    private float tickrate;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    //server test
    private Client client;

    // public PhysicsTest(Main game, Player player) {
    // mas ai ja vou mexer pra uma classe mais final
    public PhysicsTest(Main game) {
        this.game = game;
        tickrate = 1/60f;

        world = new World(new Vector2(0f, 0f), true);

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(23, 13);

        stage = new Stage(new FitViewport(23, 13));
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);

        //testBall = new Ball(new Vector2(3f, 3f), 1f, world, 0);

        Player p = PlayerManager.getInstance().getLocalPlayer();

        Ball pBall = p.createBall(new Vector2(3f, 3f), world);

        PlayerManager.getInstance().addPlayer(p);

        testFlag = new Flag(new Vector2(20f, 3f), world);

        stage.addActor(pBall);
        stage.addActor(testFlag);

        BodyDef wallDef = new BodyDef();
        wallDef.position.set(new Vector2(stage.getWidth()/2, stage.getHeight()));
        Body wall = world.createBody(wallDef);
        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(2f, 2f);
        wall.createFixture(wallBox, 0f);
        wallBox.dispose();

        Gdx.input.setInputProcessor(new InputAdapter() {
            private Vector2 mouse1;

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
                mouse1 = new Vector2(unprojected.x, unprojected.y);

                return true;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
                Vector2 mouse2 = new Vector2(unprojected.x, unprojected.y);
                p.stroke(mouse1, mouse2);

                return true;
            }
        });

        world.setContactListener(new ContactListener() {
            @Override
            public void endContact(Contact contact) {}

            @Override
            public void beginContact(Contact contact) {
                if ( contact.getFixtureA().getBody().getUserData() == "Flag"
                 || contact.getFixtureB().getBody().getUserData() == "Flag" ) {
                    System.out.println("Gol!");

                    // find wich fixture is ball
                    Object ballId = contact.getFixtureA().getBody().getUserData();
                    if (ballId == "Flag") ballId = contact.getFixtureB().getBody().getUserData();

                    Player player = PlayerManager.getInstance().getPlayer(Integer.parseInt(ballId.toString()));
                    System.out.println(player.getUsername());

                }
            }

            @Override
	        public void preSolve(Contact contact, Manifold oldManifold) {}

	        @Override
	        public void postSolve(Contact contact, ContactImpulse impulse) {}

        });
    }

    @Override
    public void show() {

        this.client = new Client();
        client.connect();
    }

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
