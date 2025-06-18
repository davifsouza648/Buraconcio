package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.Message;
import io.github.buraconcio.Network.UDPClient;
import io.github.buraconcio.Network.UDPServer;
import io.github.buraconcio.Objects.*;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.Constants.PHASE;
import io.github.buraconcio.Utils.CountdownTimer;
import io.github.buraconcio.Utils.CursorManager;
import io.github.buraconcio.Utils.GameInputAdapter;
import io.github.buraconcio.Utils.GameManager;
import io.github.buraconcio.Utils.MapRenderer;
import io.github.buraconcio.Utils.PhysicsManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.Runnable;
import java.sql.Connection;

public class PhysicsTest implements Screen {
    private Stage stage;
    private Stage hudStage;

    private Skin skin;
    private final Main game;

    private Obstacle testObstacle;

    private Box2DDebugRenderer debugRenderer;
    private GameCamera camera;

    Player p;
    private Ball pBall;

    private HUD hud;

    private MapRenderer mapRenderer;
    float scale = 1 / 32f;

    private boolean paused = false;

    public PhysicsTest(Main game) {

        this.game = game;

        mapRenderer = new MapRenderer("mapa" + GameManager.getInstance().getMapIndex());

        // debugRenderer = new Box2DDebugRenderer();

        stage = new Stage(new ExtendViewport(23, 13));
        hudStage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        // stage.setDebugAll(true);
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

        camera = new GameCamera();
        stage.getViewport().setCamera(camera);

        hud = new HUD(hudStage, PlayerManager.getInstance().getLocalPlayer().getId());

        testObstacle = new CrossBow(new Vector2(10.5f, 2f), new Vector2(3f, 3f));
        testObstacle.rotate(Obstacle.COUNTER_CLOCKWISE);

        new Star(new Vector2(12.5f, 2f), new Vector2(1f, 1f));
        new BoostPad(new Vector2(14.5f, 2f), new Vector2(3f, -1f));
        new BlackHole(new Vector2(16.5f, 2f), new Vector2(3f, -1f));
        new CircularSaw(new Vector2(12.5f, 7f), new Vector2(-1f, 1f));
        new Trampoline(new Vector2(14.5f, 7f), new Vector2(-1f, 1f));

        InputMultiplexer multiplexerInput = new InputMultiplexer();
        multiplexerInput.addProcessor(hudStage);
        multiplexerInput.addProcessor(new GameInputAdapter(p, pBall, camera, stage));

        Gdx.input.setInputProcessor(multiplexerInput);

    }

    @Override
    public void show() {
        paused = false;
        CursorManager.setGameCursor();

        if (ConnectionManager.getInstance().getUdpClient() == null
                || (ConnectionManager.getInstance().getUdpServer() == null && Constants.isHosting())) {
            if (Constants.isHosting()) {

                UDPServer udpServer = new UDPServer();
                ConnectionManager.getInstance().setUDPserver(udpServer);
                udpServer.startUDPServer();
            }

            UDPClient udpClient = new UDPClient();
            ConnectionManager.getInstance().setUDPclient(udpClient);
            udpClient.startUDPClient();
        }

        setListeners();

        CountdownTimer countdown = new CountdownTimer(3, new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {
            }

            @Override
            public void finish() {

                if (Constants.isHosting())
                    ConnectionManager.getInstance().getServer().sendString(Message.Type.PHASE_CHANGE, "show_win");
            }
        });

        countdown.start();
    }

    @Override
    public void render(float delta) {
        if (paused)
            return;

        ScreenUtils.clear(0, 0, 0, 0, true);

        stage.act(delta);

        Obstacle selected = Constants.localP().getSelectedObstacle();
        if (Constants.phase == PHASE.PLAY) {
            camera.setTarget(Constants.localP().getBall().getPosition());
        } else if (Constants.phase == PHASE.SELECT_OBJ && selected != null) {
            camera.setTarget(selected.getPosition());
        }

        camera.updateCamera();

        mapRenderer.setView(camera);
        mapRenderer.render();

        hud.render();

        stage.getViewport().setCamera(camera);
        stage.draw();

        // debugRenderer.render(PhysicsManager.getInstance().getWorld(),
        // camera.combined);

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
        paused = true;
    }

    @Override
    public void dispose() {
        // stage.dispose();
        // skin.dispose();
    }

    public void setListeners() {

        Client client = ConnectionManager.getInstance().getClient();

        client.setGameListener(new Client.GameStageListener() {

            @Override
            public void showWin() {

                Gdx.app.postRunnable(() -> game.setScreen(new VictoryScreen(game)));

            }

            @Override
            public void showPoints() {

                Gdx.app.postRunnable(() -> game.setScreen(new PointsScreen(game)));

            }

        });

    }

}
