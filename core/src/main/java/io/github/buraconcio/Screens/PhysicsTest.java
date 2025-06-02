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
import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.PhysicsManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;
import java.lang.Runnable;

public class PhysicsTest implements Screen {



    private Main game;
    private Stage stage;
    private Skin skin;

    private Ball testBall;
    private Flag testFlag;
    private Obstacle testObstacle;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    Player p;
    private Ball pBall;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    float scale = 1/32f;

    //server test
    private Client client;

    public PhysicsTest(Main game) {
        this.game = game;

        map = new TmxMapLoader().load("maps/mapa1/teste.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, scale);

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(23, 13);

        

        stage = new Stage(new FitViewport(23, 13));
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);



        PhysicsManager.getInstance().setStage(stage);

        p = PlayerManager.getInstance().getLocalPlayer();

        pBall = p.createBall(new Vector2(3f, 3f));
        pBall.setZIndex(0);

        PlayerManager.getInstance().addPlayer(p);

        testFlag = new Flag(new Vector2(20f, 3f));

        // testObstacle = new CrossBow(new Vector2(10f, 2f), new Vector2(1.5f, 1.5f));
        // testObstacle.rotate(Obstacle.COUNTER_CLOCKWISE);

        // PhysicsEntity wall1 = new PhysicsEntity(new Vector2(stage.getWidth()/2, stage.getHeight()), new Vector2(2f, 2f), "crossBow.png");
        // PolygonShape wallBox = new PolygonShape();
        // wallBox.setAsBox(2f, 2f);
        // wall1.getBody().createFixture(wallBox, 0f);
        // wallBox.dispose();

        // PhysicsEntity wall2 = new PhysicsEntity(new Vector2(stage.getWidth(), 1.5f), new Vector2(2f, 2f),  "crossBow.png");
        // PolygonShape wallBox2 = new PolygonShape();
        // wallBox2.setAsBox(2f, 2f);
        // wall2.getBody().createFixture(wallBox2, 0f);
        // wallBox2.dispose();

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
            }

            @Override
	        public void preSolve(Contact contact, Manifold oldManifold) {}

	        @Override
	        public void postSolve(Contact contact, ContactImpulse impulse) {}

        });

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Tile Layer 1");

        float tileSize = 32 * scale; // 32px em metros

        for (int x = 0; x < layer.getWidth(); x++) 
        {
            for (int y = 0; y < layer.getHeight(); y++) 
            {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && cell.getTile() != null) 
                {
                    int tileId = cell.getTile().getId();

                    if (tileId == 3) 
                    {
                        PhysicsEntity wall1 = new PhysicsEntity(new Vector2((x + 0.5f) * tileSize, (y + 0.5f) * tileSize), new Vector2(tileSize / 2, tileSize / 2), null);
                        PolygonShape wallBox = new PolygonShape();
                        wallBox.setAsBox(tileSize / 2, tileSize / 2);
                        wall1.getBody().createFixture(wallBox, 0f);
                        wallBox.dispose();
                    }
                }
            }
        }


    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);

        if (pBall != null)
        {
            Vector2 ballPos = pBall.getPosition();
            camera.position.set(ballPos.x, ballPos.y, 0);
            camera.update();
        }
        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.act(delta);
        stage.draw();

        debugRenderer.render(PhysicsManager.getInstance().getWorld(), camera.combined);

        PhysicsManager.getInstance().tick();
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
