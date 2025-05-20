package io.github.poo.game;

import com.badlogic.gdx.Game;

import io.github.poo.game.Screens.*;
import io.github.poo.game.Utils.PlayerManager;
import io.github.poo.game.Objects.*;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private PlayerManager pManager;

    @Override
    public void create() {
        pManager = new PlayerManager();
        setScreen(new LoginMenu(this));
    }

    public PlayerManager getPlayerManager() {

        return pManager;

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
    }
}
