package io.github.buraconcio.Utils.Adapters;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.buraconcio.Objects.Obstacles.Train;
import io.github.buraconcio.Utils.Managers.FlowManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;

public class DebugInputAdapter extends InputAdapter {
    public DebugInputAdapter() {
        super();
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        Vector3 unp = GameManager.getInstance().getPhysicsCamera().unproject(new Vector3(x, y, 0));
        System.out.println(new Vector2(unp.x, unp.y));
        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode == Keys.P) {
            GameManager.getInstance().setPhase(PHASE.PLAY);
        } else if (keyCode == Keys.O) {
            GameManager.getInstance().setPhase(PHASE.SELECT_OBJ);
        } else if (keyCode == Keys.R) {
            GameManager.getInstance().reloadPhysics();
        } else if (keyCode == Keys.W) {
            GameManager.getInstance().getFlow().winPhase();
        }


        return true;
    }
}
