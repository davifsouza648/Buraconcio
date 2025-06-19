package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera extends OrthographicCamera {
    private Vector2 targetPos;
    private HUD hud;
    private float lerp = 0.11f;

    public GameCamera() {
        super(23, 13);
        this.zoom = (float) 1.17;
    }

    public void setTarget(Vector2 position) {

        this.targetPos = position;
    }

    public void updateCamera() {
        if (targetPos != null) {
            Vector3 targetVector = new Vector3(targetPos.x, targetPos.y, 0);
            this.position.lerp(targetVector, lerp);
            this.update();
        }
    }

    public void teleportTo(Vector2 position) {
        this.position.set(position.x, position.y, 0);
        this.update();
    }

    public void setCameraLerpSpeed(float speed) {
        this.lerp = speed;
    }
}
