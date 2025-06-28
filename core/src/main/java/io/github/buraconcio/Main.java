package io.github.buraconcio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import io.github.buraconcio.Screens.*;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Box2D;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private World world;
    @Override
    public void create() {
        Box2D.init();
        Vector2 grav = new Vector2(0, -10);
        world = new World(grav, true);
        CursorManager.loadCursors();
        CursorManager.resetToArrow();
        // skippar primeira tela para testar mais rapido
        // comentar linha de baixo e iniciar menu desejado
        setScreen(new LoginMenu(this));

        // setScreen(new PhysicsTest(this));
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

        if (getScreen() != null) {
            getScreen().dispose();
        }

        GameManager.getInstance().dispose();
    }
}
