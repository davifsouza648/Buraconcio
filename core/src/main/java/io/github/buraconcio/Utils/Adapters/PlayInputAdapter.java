package io.github.buraconcio.Utils.Adapters;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Common.GameCamera;
import io.github.buraconcio.Objects.UI.HUD;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;

public class PlayInputAdapter extends InputAdapter {

    private Vector2 mouse1 = new Vector2();
    private HUD hud;

    public PlayInputAdapter() {
        super();
    }

    public void setHud(HUD hud)
    {
        this.hud = hud;
    }


    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        if (GameManager.getInstance().getCurrentPhase() != PHASE.PLAY)
            return false;

        mouse1.x = x;
        mouse1.y = y;
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (GameManager.getInstance().getCurrentPhase() != PHASE.PLAY)
            return false;

        if(hud != null)
        {
            if(hud.isPaused()){return false;}
        }

        GameCamera camera = GameManager.getInstance().getPhysicsCamera();
        Player p = PlayerManager.getInstance().getLocalPlayer();

        Vector3 unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
        mouse1 = new Vector2(unprojected.x, unprojected.y);

        unprojected = camera.unproject(new Vector3(x, y, 0));
        Vector2 mouse2 = new Vector2(unprojected.x, unprojected.y);

        Runnable task = () ->
        {
            p.stroke(mouse1, mouse2);
            hud.updateStrokes();
        };

        PhysicsManager.getInstance().schedule(task);
        p.getBall().resetShootingGuide();

        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        if(hud != null)
            if(hud.isPaused()){return false;}


        if (GameManager.getInstance().getCurrentPhase() != PHASE.PLAY)
            return false;

        GameCamera camera = GameManager.getInstance().getPhysicsCamera();
        Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
        Vector2 currentMouse = new Vector2(unprojected.x, unprojected.y);

        unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
        Constants.localP().getBall().setShootingGuide(new Vector2(unprojected.x, unprojected.y), new Vector2(currentMouse));

        return true;
    }



    @Override
    public boolean keyDown(int keyCode)
    {
        GameCamera camera = GameManager.getInstance().getPhysicsCamera();
        if (keyCode == Keys.NUM_1)
        {
            camera.zoom = 3f;
        }
        else if (keyCode == Keys.NUM_2)
        {
            camera.zoom = 2f;
        }
        else if (keyCode == Keys.NUM_3)
        {
            camera.zoom = 1.16f;
        }
        else if(keyCode == Keys.ESCAPE)
        {
            if(hud!=null)
                hud.togglePaused();
        }

        return false;
    }
}
