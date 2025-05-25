package io.github.buraconcio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import io.github.buraconcio.Screens.*;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Objects.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Box2D;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private PlayerManager pManager;
    private World world;

    @Override
    public void create() {
        pManager = new PlayerManager();

        Box2D.init();
        Vector2 grav = new Vector2(0, -10);
        world = new World(grav, true);
        setScreen(new LoginMenu(this));

        /* skippar primeira tela para testar mais rapido
        Player player = new Player("test");
        player.setId(0);
        pManager.addPlayer(player);
        setScreen(new MainMenu(this));
        */
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
