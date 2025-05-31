package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import io.github.buraconcio.Objects.CrossBow;
import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.PhysicsManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;
import java.lang.Runnable;

public class PhysicsTest implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;

    private Ball testBall;
    private Flag testFlag;
    private Obstacle testObstacle;

    private float tickrate;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    //server test
    private Client client;

    public PhysicsTest(Main game) {
        this.game = game;
        tickrate = 1/60f;

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(23, 13);

        stage = new Stage(new FitViewport(23, 13));
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);

        Player p = PlayerManager.getInstance().getLocalPlayer();

        Ball pBall = p.createBall(new Vector2(3f, 3f));
        pBall.setZIndex(0);

        PlayerManager.getInstance().addPlayer(p);

        testFlag = new Flag(new Vector2(20f, 3f));

        testObstacle = new CrossBow(new Vector2(10f, 2f), new Vector2(1.5f, 1.5f));
        //testObstacle.rotate(Obstacle.COUNTER_CLOCKWISE);

        stage.addActor(testFlag);
        stage.addActor(pBall);
        stage.addActor(testObstacle);

        BodyDef wallDef = new BodyDef();
        wallDef.position.set(new Vector2(stage.getWidth()/2, stage.getHeight()));
        Body wall = PhysicsManager.getInstance().getWorld().createBody(wallDef);
        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(2f, 2f);
        wall.createFixture(wallBox, 0f);
        wallBox.dispose();

        BodyDef wallDef2 = new BodyDef();
        wallDef2.position.set(new Vector2(stage.getWidth(), 1.5f));
        Body wall2 = PhysicsManager.getInstance().getWorld().createBody(wallDef2);
        PolygonShape wallBox2 = new PolygonShape();
        wallBox2.setAsBox(2f, 2f);
        wall2.createFixture(wallBox2, 0f);
        wallBox2.dispose();




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

                Runnable task = () -> {p.stroke(mouse1, mouse2);};
                PhysicsManager.getInstance().schedule(task);

                return true;
            }
        });

        PhysicsManager.getInstance().getWorld().setContactListener(new ContactListener() {
            @Override
            public void endContact(Contact contact) {
                PhysicsManager.getInstance().removeContact(contact);
            }

            @Override
            public void beginContact(Contact contact) {
                PhysicsManager.getInstance().addContact(contact);

                if ( contact.getFixtureA().getBody().getUserData() == "Flag"
                 || contact.getFixtureB().getBody().getUserData() == "Flag" ) {

                    System.out.println("Gol!");

                    // find wich fixture is ball

                    Object ballId = contact.getFixtureA().getBody().getUserData();
                    if (ballId == "Flag") ballId = contact.getFixtureB().getBody().getUserData();

                    try {
                        Player player = PlayerManager.getInstance().getPlayer(Integer.parseInt(ballId.toString()));

                        Runnable task = () -> {player.score();};
                        PhysicsManager.getInstance().schedule(task);
                    } catch (Exception e) {}
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

        debugRenderer.render(PhysicsManager.getInstance().getWorld(), camera.combined);

        PhysicsManager.getInstance().getBox2dScheduler().forEach(task -> task.run());
        PhysicsManager.getInstance().clearScheduler();

        PhysicsManager.getInstance().getWorld().step(tickrate, 6, 2);
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
