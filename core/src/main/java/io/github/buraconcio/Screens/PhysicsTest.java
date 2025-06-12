package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.UDPClient;
import io.github.buraconcio.Network.UDPServer;
import io.github.buraconcio.Objects.*;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.CursorManager;
import io.github.buraconcio.Utils.MapRenderer;
import io.github.buraconcio.Utils.PhysicsManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.Runnable;

public class PhysicsTest implements Screen {
    private Stage stage;
    private Skin skin;

    private Obstacle testObstacle;

    private Box2DDebugRenderer debugRenderer;
    private BallCamera camera;

    Player p;
    private Ball pBall;

    private MapRenderer mapRenderer;
    float scale = 1 / 32f;

    private Client client;

    public PhysicsTest(Main game) {

        mapRenderer = new MapRenderer("mapa1");

        debugRenderer = new Box2DDebugRenderer();

        stage = new Stage(new ExtendViewport(23, 13));
        Gdx.input.setInputProcessor(stage);

        stage.setDebugAll(true);
        PhysicsManager.getInstance().setStage(stage);

        mapRenderer.createCollisions();

        for (Player player : PlayerManager.getInstance().getAllPlayers()) {
            player.createBall();
        }

        pBall = PlayerManager.getInstance().getLocalPlayer().getBall();

        if (pBall == null) { // testing without server
            PhysicsManager.getInstance().placePlayer(PlayerManager.getInstance().getLocalPlayer());
            pBall = PlayerManager.getInstance().getLocalPlayer().createBall();
        }

        camera = new BallCamera(pBall);
        stage.getViewport().setCamera(camera);

        testObstacle = new CrossBow(new Vector2(10.5f, 2f), new Vector2(3f, 3f));
        testObstacle.rotate(Obstacle.COUNTER_CLOCKWISE);

        new Obstacle(new Vector2(12.5f, 2f), new Vector2(1f, 1f),
            Auxiliaries.animationFromFiles("obstacles/star/star.png", "obstacles/star/star.json"));
        new BoostPad(new Vector2(14.5f, 2f), new Vector2(3f, 3f));
        new BlackHole(new Vector2(16.5f, 2f), new Vector2(3f, 3f));

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
                p = PlayerManager.getInstance().getLocalPlayer();
                Vector3 unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
                mouse1 = new Vector2(unprojected.x, unprojected.y);

                unprojected = camera.unproject(new Vector3(x, y, 0));
                Vector2 mouse2 = new Vector2(unprojected.x, unprojected.y);

                Runnable task = () -> {
                    p.stroke(mouse1, mouse2);
                };

                PhysicsManager.getInstance().schedule(task);
                pBall.resetShootingGuide();

                // test

                Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
                Actor hitActor = stage.hit(stageCoords.x, stageCoords.y, true);

                if (p.getSelectedObstacle() != null && p.getSelectedObstacle().canPlace()) {
                    p.placeObstacle();
                    testObstacle.preRound();
                } else if (hitActor instanceof Obstacle) {
                    Obstacle hitObstacle = (Obstacle) hitActor;
                    if (!hitObstacle.claimed())
                        p.selectObstacle(hitObstacle);
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

            public boolean mouseMoved(int x, int y) {
                p = PlayerManager.getInstance().getLocalPlayer();
                Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));

                if (p.getSelectedObstacle() != null)
                    p.getSelectedObstacle().move(new Vector2(unprojected.x, unprojected.y));

                return true;
            }

            @Override
            public boolean keyDown(int keyCode) {
                p = PlayerManager.getInstance().getLocalPlayer();
                if (keyCode == Keys.Q && p.getSelectedObstacle() != null) {
                    p.getSelectedObstacle().rotate(Obstacle.COUNTER_CLOCKWISE);
                } else if (keyCode == Keys.E && p.getSelectedObstacle() != null) {
                    p.getSelectedObstacle().rotate(Obstacle.CLOCKWISE);
                }

                return true;
            }

        });

        PhysicsManager.getInstance().getWorld().setContactListener(new ContactListener() {
            @Override
            public void endContact(Contact contact) {
                // PhysicsManager.getInstance().removeContact(contact);
            }

            @Override
            public void beginContact(Contact contact) {
                PhysicsManager.getInstance().addContact(contact);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }

    @Override
    public void show() {
        CursorManager.setGameCursor();

        if (Constants.isHosting()) {

            UDPServer udpServer = new UDPServer();
            ConnectionManager.getInstance().setUDPserver(udpServer);
            udpServer.startUDPServer();
        }

        UDPClient udpClient = new UDPClient();
        ConnectionManager.getInstance().setUDPclient(udpClient);
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
