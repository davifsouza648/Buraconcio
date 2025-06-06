package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.UDPClient;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Objects.Flag;
import io.github.buraconcio.Objects.CrossBow;
import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Objects.BallCamera;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.CursorManager;
import io.github.buraconcio.Utils.MapRenderer;
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
    private BallCamera camera;

    Player p;
    private Ball pBall;

    private MapRenderer mapRenderer;
    float scale = 1/32f;

    //server test
    private Client client;

    public PhysicsTest(Main game) {
        this.game = game;

        mapRenderer = new MapRenderer("mapa1");

        debugRenderer = new Box2DDebugRenderer();


        stage = new Stage(new FitViewport(23, 13));
        Gdx.input.setInputProcessor(stage);

        stage.setDebugAll(true);

        PhysicsManager.getInstance().setStage(stage);

        p = PlayerManager.getInstance().getLocalPlayer();

        pBall = p.createBall(new Vector2(3f, 3f));
        pBall.setZIndex(0);

        PlayerManager.getInstance().addPlayer(p);

        camera = new BallCamera(pBall);
        stage.getViewport().setCamera(camera);

        testFlag = new Flag(new Vector2(20f, 3f));

        testObstacle = new CrossBow(new Vector2(10.5f, 2f), new Vector2(3f, 3f));
        testObstacle.rotate(Obstacle.COUNTER_CLOCKWISE);

        Gdx.input.setInputProcessor(new InputAdapter() {
            private Vector2 mouse1 = new Vector2();

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                mouse1.x = x;
                mouse1.y = y;

                return true;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                Vector3 unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
                mouse1 = new Vector2(unprojected.x, unprojected.y);

                unprojected = camera.unproject(new Vector3(x, y, 0));
                Vector2 mouse2 = new Vector2(unprojected.x, unprojected.y);

                Runnable task = () -> {p.stroke(mouse1, mouse2);};
                PhysicsManager.getInstance().schedule(task);
                pBall.resetShootingGuide();

                // test

                Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
                Actor hitActor = stage.hit(stageCoords.x, stageCoords.y, true);

                if (p.getSelectedObstacle() != null) {
                    p.placeObstacle();
                    testObstacle.preRound();
                }

                if (hitActor instanceof Obstacle) {
                    Obstacle hitObstacle = (Obstacle) hitActor;
                    if (!hitObstacle.claimed())
                        p.selectObstacle(testObstacle);
                }

                return true;
            }

            public boolean touchDragged(int x, int y, int pointer) {
                Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
                Vector2 currentMouse = new Vector2(unprojected.x, unprojected.y);

                unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
                pBall.setShootingGuide(new Vector2(unprojected.x, unprojected.y), new Vector2(currentMouse));

                return true;
            }

            public boolean mouseMoved(int x, int y)  {
                Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
                if (p.getSelectedObstacle() != null) p.getSelectedObstacle().move(new Vector2(unprojected.x, unprojected.y));

                return true;
            }

            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Keys.Q) {
                    p.getSelectedObstacle().rotate(Obstacle.COUNTER_CLOCKWISE);
                } else if (keyCode == Keys.E) {
                    p.getSelectedObstacle().rotate(Obstacle.CLOCKWISE);
                }

                return true;
            }

        });

        PhysicsManager.getInstance().getWorld().setContactListener(new ContactListener() {
            @Override
            public void endContact(Contact contact) {
                //PhysicsManager.getInstance().removeContact(contact);
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

        TiledMapTileLayer layer = (TiledMapTileLayer) mapRenderer.getMap().getLayers().get("Tile Layer");

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
                        PhysicsEntity wall1 = new PhysicsEntity(new Vector2((x + 0.5f) * tileSize, (y + 0.5f) * tileSize), new Vector2(tileSize, tileSize));
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
        CursorManager.setGameCursor();

        UDPClient udpClient = new UDPClient();

        udpClient.startUDPClient();

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);

        camera.updateCamera();
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
