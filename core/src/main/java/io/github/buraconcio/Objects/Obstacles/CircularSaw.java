package io.github.buraconcio.Objects.Obstacles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Align;

import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class CircularSaw extends Obstacle {
    private static final Vector2 size = new Vector2(-1f, 2f);
    private final float frameDuration;

    private static final int idleFrames = 4; // por favor ninguem troca a animacao velho tem mt hard code
    private int animationDuration;
    private int framesToMove;
    private float circleRadius;
    private Vector2 origin;

    public CircularSaw(Vector2 pos) {
        super(pos, size,
            Auxiliaries.animationFromFiles("obstacles/circularSaw/circularSaw.png", "obstacles/circularSaw/circularSaw.json"));

        circleRadius = getHeight()/2;

        animacao.pauseAnimation();
        SoundManager.getInstance().loadLoopSound("saw", "sounds/obstacle-sounds/saw/saw.wav");

        body.setType(BodyType.DynamicBody);

        setOrigin(Align.center);
        this.setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        origin = new Vector2(getX(), getY());

        frameDuration = animacao.getFrameDuration();

        animationDuration = animacao.getNumFrames();
        framesToMove = animationDuration/2 - idleFrames - 1;

        CircleShape circle = new CircleShape();
        circle.setRadius(circleRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        teleport(new Vector2(getX() + getWidth() - circleRadius - 0.15f, body.getPosition().y));

        circle.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setOrigin(Align.center);
        setPosition(origin.x, origin.y);

        if (!active){
            return;
        }

        float x0 = getX() + getWidth()/2 - circleRadius - 0.15f;
        float xf = getX() - (x0 - getX());

        int currentFrame = animacao.getFrameIndex();

        SoundManager.getInstance().loopProximity("saw", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());

        if (currentFrame <= idleFrames ) {
            body.setLinearVelocity(new Vector2(0f, 0f));
        } else if (currentFrame >= idleFrames + framesToMove && currentFrame <= framesToMove + 2*idleFrames) {
            body.setLinearVelocity(new Vector2(0f, 0f));
        } else if (currentFrame > idleFrames && currentFrame < idleFrames + framesToMove) {
            body.setLinearVelocity(new Vector2((xf - x0) / (2 * frameDuration * (framesToMove-1)), 0f).rotateDeg(getRotation()));
        } else {
            body.setLinearVelocity(new Vector2((x0 - xf) / (2 * frameDuration * (framesToMove+1)), 0f).rotateDeg(getRotation()));
        }
    }

    @Override
    public void move(Vector2 centerPos) {
        Vector2 cornerPos = centerPos.cpy().sub(new Vector2(getWidth()/2, getHeight()/2));
        origin = cornerPos;

        Vector2 offset = new Vector2(getWidth()/2 - circleRadius - 0.15f, 0f);
        offset.rotateDeg(getRotation());

        super.move(centerPos.cpy().add(offset));
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
    public void place() {
        super.place();

        Vector2 centerPos = origin.cpy().add(new Vector2(getWidth()/2, getHeight()/2));
        Vector2 offset = new Vector2(getWidth()/2 - circleRadius - 0.15f, 0f);
        offset.rotateDeg(getRotation());
        teleport(centerPos.cpy().add(offset));

        body.setType(BodyType.DynamicBody);
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
        super.preRound();
        animacao.resumeAnimation();
        active = true;
    }

    @Override
    public void postRound(){
        super.postRound();
        animacao.pauseAnimation();
    }
}

