package io.github.buraconcio.Objects.Obstacles;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class Mine extends Obstacle {
    private static final Vector2 size = new Vector2(1f, 1f);
    private static final float detectionRadiusMultiplyer = 1.25f;
    private static final float ExplosionRadiusMultiplyer = 4f;
    private static final int explosionFrame = 10;


    public Mine(Vector2 pos) {
        super(pos, size,
        Auxiliaries.animationFromFiles("obstacles/mine/mine.png", "obstacles/mine/mine.json"));

        animacao.pauseAnimation();
        animacao.setFrameDuration(animacao.getFrameDuration() * 0.8f);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(detectionRadiusMultiplyer* size.x/2,
            detectionRadiusMultiplyer* size.y/2);

        SoundManager.getInstance().loadSound("explosion", "sounds/obstacle-sounds/mine/explosion.wav");

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    @Override
    public boolean contact(PhysicsEntity entity) {
        if (!active) return false;

        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            if (!ball.isAirborne()) {
                animacao.playOnce();

                return true;
            }
        }

        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (animacao.getFrameIndex() == explosionFrame)
        {
            animacao.setScale(ExplosionRadiusMultiplyer);
            SoundManager.getInstance().playProximity("explosion", this.getPosition(), PlayerManager.getInstance().getLocalPlayer().getBall().getPosition());
        }


        if (animacao.getFrameIndex() == explosionFrame + 1) { // explodiu
            List<Player> players = PlayerManager.getInstance().getAllPlayers();

            for (Player p : players) {
                float dist2 = new Vector2(p.getBall().getWorldCenter()).sub(body.getWorldCenter()).len2();
                float animacaoRadius = animacao.getScaleX()*animacao.getWidth();

                if (dist2 < animacaoRadius*animacaoRadius/4 * 1.25f) {
                    p.die();
                }
            }
        }

        if (animacao.isLastFrame()) {
            animacao.setScale(1);
            disable();
        }
    }

    @Override
    public void postRound() {
        enable();
    }
}
