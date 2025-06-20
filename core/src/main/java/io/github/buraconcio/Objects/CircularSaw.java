package io.github.buraconcio.Objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

public class CircularSaw extends Obstacle {
    private static final Vector2 size = new Vector2(-1f, 2f);
    private float elapsedTime = 0f;

    private static final int idleFrames = 4; // por favor ninguem troca a animacao velho tem mt hard code
    private int animationDuration;
    private int framesToMove;
    private float circleRadius;

    public CircularSaw(Vector2 pos) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/circularSaw/circularSaw.png", "obstacles/circularSaw/circularSaw.json"));

        circleRadius = getHeight()/2;

        animacao.pauseAnimation();
        SoundManager.getInstance().loadLoopSound("saw", "sounds/obstacle-sounds/saw/saw.wav");

        createSawFixture(0f);

        animationDuration = animacao.getNumFrames();
        framesToMove = animationDuration/2 - idleFrames - 1;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        elapsedTime += delta;

        if (!active) elapsedTime = 0f;

        float sawPosition;

        float x0 = getWidth()/2 - circleRadius - 0.15f;
        float xf = -x0;

        int currentFrame = animacao.getFrameIndex();
        float framesOfMovement = currentFrame - idleFrames;

        SoundManager.getInstance().loopProximity("saw", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

        if (currentFrame <= idleFrames ) {
            sawPosition = x0;
        } else if (currentFrame >= idleFrames + framesToMove && currentFrame <= framesToMove + 2*idleFrames) {
            sawPosition = xf;
        } else if (currentFrame > idleFrames && currentFrame < idleFrames + framesToMove) {
            sawPosition = framesOfMovement * (xf - x0) / framesToMove + x0;
        } else {
            sawPosition = (framesOfMovement - 2*idleFrames - framesToMove + 3) * (x0 - xf) / framesToMove + xf;
        }

        body.destroyFixture(body.getFixtureList().get(0));

        createSawFixture(sawPosition);
    }

    @Override
    public boolean contact(PhysicsEntity other) {
        if (!active) return false;

        if (other instanceof Ball) {
            Ball ball = (Ball) other;

            if (!ball.isAirborne()) // coloquei um active aqui fds kkk
                ball.getPlayer().die();

            return true;
        }

        return false;
    }

    @Override
    public boolean canPlace() {
        if (!super.canPlace()) // only the saw hitbox
            return false;

        Rectangle bb = new Rectangle(
            getX(), getY(),
            getWidth(), getHeight()
        );

        if (bb.overlaps(PhysicsManager.getInstance().getStratingRect()))
            return false;

        return true;
    }

    @Override
    public void preRound() {
        animacao.resumeAnimation();
    }

    @Override
    public void postRound(){
        animacao.pauseAnimation();
    }

    private void createSawFixture(float sawPosition) {
        CircleShape circle = new CircleShape();
        circle.setRadius(circleRadius);
        circle.setPosition(new Vector2(sawPosition, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        circle.dispose();
    }
}

