package io.github.buraconcio.Utils;

import com.badlogic.gdx.Screen;

import io.github.buraconcio.Screens.PhysicsTest;

public class GameManager {

    private static GameManager instance;

    private PhysicsTest physicsScreen;
    // private Screen currentScreen;

    private int mapIndex;
    private Constants.PHASE currentPhase;

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

    // public void setCurrentScreen(Screen screen) {
    //     this.currentScreen = screen;
    // }

    // public Screen getCurrentScreen() {
    //     return currentScreen;
    // }

    public void setMapIndex(int index) {
        this.mapIndex = index;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void dispose() {
        if (physicsScreen != null) {

            physicsScreen.dispose();
            physicsScreen = null;

        }

        // if (currentScreen != null) {

        //     currentScreen.dispose();
        //     currentScreen = null;

        // }

    }
}
