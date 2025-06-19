package io.github.buraconcio.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.GameCamera;
import io.github.buraconcio.Screens.PhysicsTest;

public class GameManager {

    private static GameManager instance;

    private PhysicsTest physicsScreen;
    private Screen currentScreen;

    private int play_time = 5;
    private int select_time = 5;
    private int points_time = 5;
    private int win_time = 55;

    private int mapIndex;
    public PHASE phase = PHASE.PLAY;

    public enum PHASE {
        LOOBY,
        PLAY,
        SELECT_OBJ,
        SHOW_POINTS,
        SHOW_WIN,
    }

    InputMultiplexer inputs = new InputMultiplexer();
    private GameCamera camera;
    private Stage stage;

    private GameManager() {
        inputs = new InputMultiplexer();

        inputs.addProcessor(new PlayInputAdapter()); // bem gambiarra por enquanto
        inputs.addProcessor(new ObstacleInputAdapter());
        inputs.addProcessor(new DebugInputAdapter());

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
        this.camera = screen.getCamera();
        this.stage = screen.getStage();
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

        //PhysicsManager.getInstance().dispose();
        //new PhysicsManager();
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
}
