package io.github.buraconcio.Utils.Common;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.buraconcio.Objects.Obstacles.Train;
import io.github.buraconcio.Utils.Managers.GameManager;

public class TrainSpawner {

    private Vector2 spawnPos;
    private int direction;
    private Timer.Task spawnTask;

    public TrainSpawner(Vector2 spawnPos, int direction) 
    {
        this.spawnPos = spawnPos;
        this.direction = direction;
        startSpawning();
    }

    private void startSpawning() 
    {
        spawnTask = new Timer.Task() 
        {
            @Override
            public void run() 
            {
                spawnTrain();
                Timer.schedule(this, 10f + (float) Math.random() * 5f);
            }
        };
        Timer.schedule(spawnTask, 15f);
    }

    private void spawnTrain() 
    {
        GameManager.getInstance().getPhysicsStage().addActor(new Train(new Vector2(spawnPos), direction));
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
