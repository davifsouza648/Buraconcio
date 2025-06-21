package io.github.buraconcio.Utils.Adapters;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.buraconcio.Utils.Common.GameCamera;
import io.github.buraconcio.Objects.Obstacles.Obstacle;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;


public class ObstacleInputAdapter extends InputAdapter {
    private static final float GRID_SIZE = 1f; // Tamanho da grid em unidades do mundo (não em pixels)

    public ObstacleInputAdapter() {
        super();
        SoundManager.getInstance().loadSound("invalidPosition", "sounds/obstacle-sounds/errorPlacing.wav");
    }

    private Vector2 snapToGrid(Vector2 worldCoords)
    {
        float snappedX = Math.round((worldCoords.x - 0.5f) / GRID_SIZE) * GRID_SIZE + 0.5f;
        float snappedY = Math.round((worldCoords.y - 0.5f) / GRID_SIZE) * GRID_SIZE + 0.5f;
        return new Vector2(snappedX, snappedY);
    }


    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (GameManager.getInstance().getCurrentPhase() != PHASE.SELECT_OBJ)
            return false;

        Player p = PlayerManager.getInstance().getLocalPlayer();
        Stage stage = GameManager.getInstance().getPhysicsStage();

        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
        Actor hitActor = stage.hit(stageCoords.x, stageCoords.y, true);
        GameCamera camera = GameManager.getInstance().getPhysicsCamera();

        Obstacle obstacle = p.getSelectedObstacle();
        if (obstacle != null)
        {
            Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
            Vector2 worldCoords = new Vector2(unprojected.x, unprojected.y);

            Vector2 snappedPos = snapToGrid(worldCoords);

            obstacle.teleport(snappedPos);

            if (obstacle.canPlace()) {
                p.placeObstacle();
            } else {
                SoundManager.getInstance().playSound("invalidPosition");
                obstacle.flashRed();
            }

            //desativar para que o preround seja feito no flowmanager
            // obstacle.preRound();
        }
        else if (hitActor instanceof Obstacle)
        {
            Obstacle hitObstacle = (Obstacle) hitActor;
            if (!hitObstacle.claimed())
            {
                p.selectObstacle(hitObstacle);
            }
        }

        return true;
    }

    public boolean mouseMoved(int x, int y)
    {
        if (GameManager.getInstance().getCurrentPhase() != PHASE.SELECT_OBJ)
            return false;

        GameCamera camera = GameManager.getInstance().getPhysicsCamera();
        Player p = PlayerManager.getInstance().getLocalPlayer();

        Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
        Vector2 worldCoords = new Vector2(unprojected.x, unprojected.y);

        if (p.getSelectedObstacle() != null)
        {
            Vector2 snappedPos = snapToGrid(worldCoords);
            p.getSelectedObstacle().setTargetPos(snappedPos);
        }

        return true;
    }

    @Override
    public boolean keyDown(int keyCode)
    {
        if (GameManager.getInstance().getCurrentPhase() != PHASE.SELECT_OBJ)
            return false;

        Player p = PlayerManager.getInstance().getLocalPlayer();
        if (keyCode == Keys.Q && p.getSelectedObstacle() != null)
        {
            p.getSelectedObstacle().rotate(Obstacle.COUNTER_CLOCKWISE);
        }
        else if (keyCode == Keys.E && p.getSelectedObstacle() != null)
        {
            p.getSelectedObstacle().rotate(Obstacle.CLOCKWISE);
        }

        return true;
    }
}
