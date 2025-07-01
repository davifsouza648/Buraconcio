package io.github.buraconcio.Utils.Common;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Objects.Obstacles.Train;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class TrainSpawner {

    private Vector2 spawnPos;
    private int direction;
    private Timer.Task spawnTask;

    public TrainSpawner(Vector2 spawnPos, int direction)
    {
        this.spawnPos = spawnPos;
        this.direction = direction;
        SoundManager.getInstance().loadSound("train-horn", "sounds/obstacle-sounds/train/horn.wav");

        GameManager.getInstance().addTrainSpawner(this);
    }

    public void startSpawning()
    {
        if (!Constants.isHosting())
            return;

        spawnTask = new Timer.Task()
        {
            @Override
            public void run()
            {
                if ((Constants.isHosting() && GameManager.getInstance().getCurrentScreen() == GameManager.getInstance().getPhysicsScreen()) && GameManager.getInstance().getMapIndex() == 2) {
                    Vector3 info = new Vector3(spawnPos.x, spawnPos.y, direction);
                    ConnectionManager.getInstance().getServer().sendMessage(Message.Type.SPAWN_TRAIN, (Object) info);
                }

                Timer.schedule(this, 10f + (float) Math.random() * 5f);
            }
        };
        Timer.schedule(spawnTask, 14f + (float) Math.random() * 1f);
    }

    public static void spawnTrain(Vector2 spawnPos, int direction)
    {
        new Train(new Vector2(spawnPos), direction);
        SoundManager.getInstance().playProximity("train-horn", spawnPos, PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

    }

    public void stop()
    {
        if (spawnTask != null)
        {
            spawnTask.cancel();
            spawnTask = null;
        }
    }
}
