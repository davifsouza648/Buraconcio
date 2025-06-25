package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.TCP.Client;
import io.github.buraconcio.Network.UDP.UDPClient;
import io.github.buraconcio.Network.UDP.UDPServer;
import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.GameCamera;
import io.github.buraconcio.Utils.Common.GameCamera.Mode;
import io.github.buraconcio.Objects.Obstacles.*;
import io.github.buraconcio.Objects.UI.HUD;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.FlowManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Common.MapRenderer;
import io.github.buraconcio.Utils.Managers.PhysicsManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PhysicsTest implements Screen {
    private final Main game;
    private Stage stage;
    private Stage hudStage;

    private Box2DDebugRenderer debugRenderer;
    private GameCamera camera;

    private Ball pBall;

    private HUD hud;

    private MapRenderer mapRenderer;
    float scale = 1 / 32f;

    private boolean paused = false;
    private FlowManager flow;

    public PhysicsTest(Main game) {
        PlayerManager.getInstance().syncLocalPlayer();

        this.game = game;
        mapRenderer = new MapRenderer("mapa" + GameManager.getInstance().getMapIndex());

        debugRenderer = new Box2DDebugRenderer();

        stage = new Stage(new ExtendViewport(23, 13));
        hudStage = new Stage(new FitViewport(1280, 720));

        SoundManager.getInstance().stopMusic();

        //stage.setDebugAll(true);
        PhysicsManager.getInstance().setStage(stage);

        mapRenderer.createCollisions();

        for (Player player : PlayerManager.getInstance().getAllPlayers()) {
            player.createBall();
        }

        pBall = PlayerManager.getInstance().getLocalPlayer().getBall();

        camera = new GameCamera();
        stage.getViewport().setCamera(camera);
        hud = new HUD(hudStage, PlayerManager.getInstance().getLocalPlayer().getId(), game);

        if (pBall == null) { // testing without server
            PhysicsManager.getInstance().placePlayer(PlayerManager.getInstance().getLocalPlayer());
            pBall = PlayerManager.getInstance().getLocalPlayer().createBall();

            new CrossBow(new Vector2(9f, 35f));
            new Star(new Vector2(12f, 35f));
            new BoostPad(new Vector2(15f, 35f));
            new BlackHole(new Vector2(19f, 35f));
            new CircularSaw(new Vector2(9f, 30f));
            new Trampoline(new Vector2(15f, 30f));
            new Mine(new Vector2(17.5f, 30f));
            new Honey(new Vector2(20f, 30f));
            new WoodBox(new Vector2(5f, 25f));
            new LongWoodBox(new Vector2(10f, 25f));
            new MetalBox(new Vector2(15f, 25f));
            new LongMetalBox(new Vector2(20f, 25f));
            new LMetalBox(new Vector2(27f, 25f));
            new Eraser(new Vector2(9f, 30f));
        }

        flow = new FlowManager();

        GameManager.getInstance().addProcessor(hudStage, 0);
        GameManager.getInstance().setGameInputProcessor();
        GameManager.getInstance().getInputAdapter().setHud(hud);
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

    }

    @Override
    public void render(float delta) {
        if (paused)
            return;

        stage.act(delta);

        Obstacle selected = Constants.localP().getSelectedObstacle();
        Ball selectedBall = pBall;

        if (GameManager.getInstance().getCurrentPhase() == PHASE.PLAY) {

            if (pBall.isAlive() != true) {

                for (Player p : PlayerManager.getInstance().getAllPlayers()) {

                    if (p.getBall().isAlive()) {
                        selectedBall = p.getBall();
                    }
                }
            }

            camera.setTarget(selectedBall.getPosition());
            camera.setCameraLerpSpeed(0.11f);
            camera.setMode(Mode.ball);

        } else if (GameManager.getInstance().getCurrentPhase() == PHASE.SELECT_OBJ && selected != null) {
            camera.setMode(Mode.obstacle);
            camera.setTarget(selected.getPosition());
            camera.setCameraLerpSpeed(0.05f);
        }

        camera.updateCamera();
        mapRenderer.renderBackground();
        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.getViewport().setCamera(camera);
        stage.draw();

        hud.render();

        //debugRenderer.render(PhysicsManager.getInstance().getWorld(), camera.combined);
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
        mapRenderer.dispose();
        hudStage.dispose();
        stage.dispose();
        debugRenderer.dispose();
    }

    public GameCamera getCamera() {
        return this.camera;
    }

    public Stage getStage() {
        return this.stage;
    }

    public Main getGame() {
        return this.game;
    }

    public void setListeners() {

        try {
            Client client = ConnectionManager.getInstance().getClient();

            client.setGameListener(new Client.GameStageListener() {

                @Override
                public void showWin() {

                    Gdx.app.postRunnable(
                            () -> GameManager.getInstance().setCurrentScreen(game, new VictoryScreen(game)));

                }

                @Override
                public void showPoints() {

                    Gdx.app.postRunnable(
                            () -> GameManager.getInstance().setCurrentScreen(game, new PointsScreen(game)));
                }

                @Override
                public void GameScreen() {

                    Gdx.app.postRunnable(() -> game.setScreen(GameManager.getInstance().getPhysicsScreen()));

                }

            });

        } catch (Exception e) {
            System.out.println("normal error if testing physics");
        }
    }
}
