package io.github.buraconcio.Objects;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class BallCamera extends OrthographicCamera 
{
    private Ball playerBall;

    public BallCamera(Ball playerBall)
    {
        super(23,13);
        this.playerBall = playerBall;
    }

    public void updateCamera()
    {
        Vector2 ballPos = playerBall.getPosition();
        this.position.set(ballPos.x, ballPos.y, 0);
        this.update();     
    }
}
