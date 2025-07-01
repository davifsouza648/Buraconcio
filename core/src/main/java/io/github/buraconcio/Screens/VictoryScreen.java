package io.github.buraconcio.Screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Objects.Game.Podium;
import io.github.buraconcio.Utils.Common.GameCamera;
import io.github.buraconcio.Utils.Common.MapRenderer;
import io.github.buraconcio.Utils.Common.GameCamera.Mode;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class VictoryScreen implements Screen {
    private static final List<Vector2> startingPositions = Arrays.asList(
        new Vector2(15f, 0f), new Vector2(20f, 0f), new Vector2(10f, 0f), new Vector2(5f, 0f)); // from first to fourth

    private static final List<Podium.Type> podiumTypes = Arrays.asList(
        Podium.Type.gold, Podium.Type.silver, Podium.Type.bronze);

    private final MapRenderer mapRenderer;
    private final Main game;
    private final Stage stage;
    private final GameCamera camera;
    private Box2DDebugRenderer debugRenderer;

    private Podium currentPodium;
    private int currentPodiumIndex;
    private Image titleImage;


    public VictoryScreen(Main game){
        this.game = game;

        stage = new Stage(new ExtendViewport(23, 13));
        SoundManager.getInstance().stopMusic();

        debugRenderer = new Box2DDebugRenderer();
        mapRenderer = new MapRenderer("victory");
        mapRenderer.createCollisions();

        PhysicsManager.getInstance().setStage(stage);

        camera = GameManager.getInstance().getPhysicsCamera();
        stage.getViewport().setCamera(camera);

        ArrayList<Player> playerRankings = new ArrayList<Player>(PlayerManager.getInstance().getAllPlayers());

        playerRankings.sort((p1, p2) -> p2.getStars() - p1.getStars() );

        int i = 0;
        for (Player p : playerRankings) {
            Vector2 pos = startingPositions.get(i++).cpy().add(new Vector2(0f, 2f));
            p.getBall().teleport(pos);
            p.getBall().addToStage(stage);
            p.getBall().getBody().setAwake(true);
        }

        currentPodiumIndex = startingPositions.size() - 2;
        Timer.schedule(new Task() {
            @Override
            public void run() {
                currentPodium = new Podium(startingPositions.get(currentPodiumIndex), podiumTypes.get(currentPodiumIndex));
            }
        }, 1f);

        PhysicsManager.getInstance().getWorld().setGravity(new Vector2(0f, -9.8f));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);

        if (currentPodium != null) {
            if (currentPodium.reached()) {
                if (currentPodiumIndex > 0) {
                    --currentPodiumIndex;

                    currentPodium = new Podium(startingPositions.get(currentPodiumIndex), podiumTypes.get(currentPodiumIndex));
                } else {
                    PlayerManager.getInstance().getLocalPlayer().setBallInteractable(true);
                }
            }
        }

        camera.setMode(Mode.ball);
        camera.setTarget(PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

        camera.updateCamera();
        mapRenderer.renderBackground();
        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.draw();

        debugRenderer.render(PhysicsManager.getInstance().getWorld(), camera.combined);

        PhysicsManager.getInstance().tick();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}


