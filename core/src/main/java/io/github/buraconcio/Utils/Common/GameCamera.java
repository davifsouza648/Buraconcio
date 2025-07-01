package io.github.buraconcio.Utils.Common;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera extends OrthographicCamera {
    private Vector2 targetPos;
    private float lerp = 0.11f;

    private ArrayList<Runnable> onReach;

    public enum Mode {
        ball,
        obstacle
    }

    private Mode mode = Mode.ball;

    public GameCamera() {
        super(23, 13);
        this.zoom = (float) 1.17;
        this.onReach = new ArrayList<Runnable>();
    }

    public void setTarget(Vector2 position) {

        this.targetPos = position;
    }

    public void updateCamera() {
        if (targetPos != null)
        {
            Vector2 diff = new Vector2(position.x, position.y).sub(targetPos);

            if (diff.len2() < 25f && mode == Mode.obstacle) {
                targetPos = null;
                onReach.forEach(task -> task.run());
                onReach.clear();

                return;
            }

            Vector3 targetVector = new Vector3(targetPos.x, targetPos.y, 0);
            this.position.lerp(targetVector, lerp);
            this.update();

            if (diff.len2() < 0.1f) {
                onReach.forEach(task -> task.run());
                onReach.clear();
            }
        }
    }

    public void teleportTo(Vector2 position) {
        this.position.set(position.x, position.y, 0f);
        //translate(position.x - this.position.x, position.y - this.position.y);
        this.update();
    }

    public float setCameraLerpSpeed(float speed) {
        float tmp = this.lerp;
        this.lerp = speed;

        return tmp;
    }

    // will call task when target is reached
    public void onReachTarget(Runnable task) {
        onReach.add(task);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }
}
