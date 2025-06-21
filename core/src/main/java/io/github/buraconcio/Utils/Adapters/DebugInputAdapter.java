package io.github.buraconcio.Utils.Adapters;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;

public class DebugInputAdapter extends InputAdapter {
    public DebugInputAdapter() {
        super();
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        System.out.println("dbg processor is on");
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
        }


        return true;
    }
}
