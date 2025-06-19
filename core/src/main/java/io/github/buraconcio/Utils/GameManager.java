package io.github.buraconcio.Utils;

import com.badlogic.gdx.Screen;

import io.github.buraconcio.Screens.PhysicsTest;

public class GameManager {

    private static GameManager instance;

    private PhysicsTest physicsScreen;
    // private Screen currentScreen;

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

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void setPhysicsScreen(PhysicsTest screen) {
        this.physicsScreen = screen;
    }

    public PhysicsTest getPhysicsScreen() {
        return physicsScreen;
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

    public PHASE getCurrentPhase() {
        return phase;
    }

    public void dispose() {
        if (physicsScreen != null) {

            physicsScreen.dispose();
            physicsScreen = null;

        }

        // if (currentScreen != null) {

        // currentScreen.dispose();
        // currentScreen = null;

        // }

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

    // public void setCurrentScreen(Screen screen) {
    // this.currentScreen = screen;
    // }

    // public Screen getCurrentScreen() {
    // return currentScreen;
    // }

}
