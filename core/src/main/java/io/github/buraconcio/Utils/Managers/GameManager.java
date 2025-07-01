package io.github.buraconcio.Utils.Managers;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Utils.Common.GameCamera;
import io.github.buraconcio.Objects.Game.Flag;
import io.github.buraconcio.Objects.Obstacles.Obstacle;
import io.github.buraconcio.Screens.PhysicsTest;
import io.github.buraconcio.Screens.VictoryScreen;
import io.github.buraconcio.Utils.Adapters.DebugInputAdapter;
import io.github.buraconcio.Utils.Adapters.ObstacleInputAdapter;
import io.github.buraconcio.Utils.Adapters.PlayInputAdapter;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Common.ObstacleSpawner;
import io.github.buraconcio.Utils.Common.TrainSpawner;

public class GameManager {

    private static GameManager instance;

    private PhysicsTest physicsScreen;
    private Screen currentScreen;
    private ObstacleSpawner obstacleSpawner;
    private ArrayList<TrainSpawner> trainSpawners;
    private Flag flag;

    private FlowManager flow;

    private Rectangle blueprintArea;

    private PlayInputAdapter playInput;

    // gestao de tempo
    private int play_time = 120;
    private int select_time = 30;
    private int points_time = 10;
    private int win_time = 25;
    private int timeToClear = 15; // limpar os obstaculos nao selecionados

    private int arrivalTime;
    // num de obstaculos
    private int obstacleNum = 4;

    private int pointsToWin = 10;

    private int mapIndex;
    public PHASE phase = PHASE.LOOBY;
    private int round = 0;

    public enum PHASE {
        LOOBY,
        PLAY,
        SELECT_OBJ,
        SHOW_POINTS,
        SHOW_WIN, LOBBY,
    }

    InputMultiplexer inputs = new InputMultiplexer();
    private GameCamera camera;
    private Stage stage;

    private Vector2 mapCoords = new Vector2(5,5);

    private GameManager() {
        inputs = new InputMultiplexer();

        playInput = new PlayInputAdapter();
        inputs.addProcessor(new DebugInputAdapter());
        inputs.addProcessor(playInput); // bem gambiarra por enquanto
        inputs.addProcessor(new ObstacleInputAdapter());

        this.flow = new FlowManager();

        // put this here or somewhere else?
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
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        obstacleSpawner = new ObstacleSpawner();
        trainSpawners = new ArrayList<TrainSpawner>();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void addProcessor(InputProcessor processor) {
        inputs.addProcessor(processor);
    }

    public void addProcessor(InputProcessor processor, int index) {
        inputs.addProcessor(index, processor);
    }

    public void setGameInputProcessor() {
        Gdx.input.setInputProcessor(inputs);
    }

    public void setPhysicsScreen(PhysicsTest screen) {
        this.physicsScreen = screen;

        this.stage = screen.getStage();
    }

    public void setCamera(GameCamera camera) {
        this.camera = camera;
    }

    public PhysicsTest getPhysicsScreen() {
        return physicsScreen;
    }

    public void setCurrentScreen(Main game, Screen screen) {
        this.currentScreen = screen;
        game.setScreen(screen);
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public PlayInputAdapter getInputAdapter() {
        return this.playInput;
    }

    public void setMapIndex(int index) {
        this.mapIndex = index;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void setPhase(String msg) {

        try {

            phase = PHASE.valueOf(msg.toUpperCase());

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

        }

    }

    public void setPhase(PHASE phase) {
        this.phase = phase;
    }

    public PHASE getCurrentPhase() {
        return phase;
    }

    public void reloadPhysics() {
        physicsScreen = new PhysicsTest(physicsScreen.getGame());

        setPhysicsScreen(physicsScreen);
        setCurrentScreen(physicsScreen.getGame(), physicsScreen);

        // PhysicsManager.getInstance().dispose();
        // new PhysicsManager();
    }

    public GameCamera getPhysicsCamera() {
        return this.camera;
    }

    public Stage getPhysicsStage() {
        return this.stage;
    }

    public void dispose() {
        if (physicsScreen != null) {

            physicsScreen.dispose();
            physicsScreen = null;

        }

        if (currentScreen != null) {

            currentScreen.dispose();
            currentScreen = null;

        }

    }

    public int getPlayTime() {
        return play_time;
    }

    public int getSelectTime() {
        return select_time;
    }

    public int getWinTime() {
        return win_time;
    }

    public int getPointsTime() {
        return points_time;
    }

    public int getPointsToWin() {
        return pointsToWin;
    }

    public void setPointsToWin(int x) {
        pointsToWin = x;
    }

    public int getTimeToClear() {
        return timeToClear;
    }

    public void setTimeToClear(int x) {
        timeToClear = x;
    }

    public void setupPlayPhase() {
        round++;
        PlayerManager.getInstance().getLocalPlayer().resetStrokes();

        PhysicsManager.getInstance().preRoundObstacles();

        PlayerManager.getInstance().setEveryonePlaced(false);

        PlayerManager.getInstance().setAllBallsAlive(); // setar como vivas as a ideia principal é fazer um respawn
        PhysicsManager.getInstance().randomizePlayerPositions();

        PlayerManager.getInstance().resetAllScoredFlag();

        trainSpawners.forEach(spawner -> {
            spawner.startSpawning();
        });

        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendMessage(Message.Type.PLAYERS_START_POS,
                    PhysicsManager.getInstance().getPlayerStartPosList());

            if (round == 1)
                ConnectionManager.getInstance().getServer().sendMessage(Message.Type.FLAG_POS,
                    getPhysicsScreen().getMapRenderer().getRandomFlagArea());
        }

        Constants.localP().setCanSelect(false);

        float normalLerpSpeed = camera.setCameraLerpSpeed(0f); // freeze camera to show hole

        Timer.schedule(new Task() {
            @Override
            public void run() {
                if (flag != null) {
                    camera.teleportTo(flag.getPosition());
                } else {
                    Timer.schedule(this, 0.05f);
                }
            }
        }, 0.01f); // se rodar instantaneamente nao funciona nao sei por que mas desisto fds

        Timer.schedule(new Task() {
            @Override
            public void run() {
                camera.setCameraLerpSpeed(0.02f);
            }
        }, 1f); // show hole for 1 second

        Runnable whenReached = () -> {
            camera.setCameraLerpSpeed(normalLerpSpeed);
            Constants.localP().setBallInteractable(true);
        };

        camera.onReachTarget(whenReached);
    }

    public void setupSelectObstaclePhase() {
        PhysicsManager.getInstance().postRoundObstacles();

        trainSpawners.forEach(spawner -> {
            spawner.stop();
        });

        // jogar a camera pro centro do espaço reservado
        Rectangle area = blueprintArea;
        Vector2 center = new Vector2();

        area.getCenter(center);
        center.x /= 32;
        center.y /= 32;

        if (area != null) {
            teleportCamera(center);
        }

        if (Constants.isHosting()) {

            ArrayList<String> blueprintObstacles = obstacleSpawner.selectRandomObstacles(obstacleNum);

            ArrayList<ObstacleInfo> obstaclesToSpawn = new ArrayList<>();

            for (String type : blueprintObstacles) {

                // dentro de randomposition ele puxa uma variavel rectangle de gameManager
                Vector2 pos = obstacleSpawner.randomPositionInBlueprint();

                ObstacleInfo info = new ObstacleInfo(type, pos.x, pos.y);
                obstaclesToSpawn.add(info);
            }

            ConnectionManager.getInstance().getServer().sendMessage(Message.Type.SPAWN_OBSTACLES,
                    obstaclesToSpawn);
        }

        PlayerManager.getInstance().setAllCanSelect(true);
        Constants.localP().setBallInteractable(false);
    }

    public void setupWinPhase() {
        PhysicsManager.getInstance().destroyAllExceptBalls();
        trainSpawners.clear();

        // Constants.localP().setBallInteractable(false);
        Main game = physicsScreen.getGame();
        setCurrentScreen(game, new VictoryScreen(game));
    }

    public Obstacle spawnObstacle(String type, Vector2 pos) {
        return obstacleSpawner.spawnObstacle(type, pos);
    }

    public void moveCamera(Vector2 pos) {
        camera.setTarget(pos);
    }

    public void cameraBack()
    {
        camera.setTarget(mapCoords);
    }

    public void teleportCamera(Vector2 pos)
    {
        camera.setTarget(pos);
        camera.teleportTo(pos);
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {
        return this.flag;
    }

    public void startFlow() {
        flow.start();
    }

    public FlowManager getFlow() {
        return flow;
    }

    public void setbluePrintArea(Rectangle p)
    {
        this.blueprintArea = p;
    }

    public Rectangle getBluePrintarea()
    {
        return this.blueprintArea;
    }

    public int getRound()
    {
        return this.round;
    }

    public void setRound(int newRound){
        round = newRound;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    public void addTrainSpawner(TrainSpawner spawner) {
        trainSpawners.add(spawner);
    }
}
