package io.github.buraconcio.Objects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.SoundManager;

public class Mine extends Obstacle {
    private static final float detectionRadiusMultiplyer = 1.25f;
    private static final float ExplosionRadiusMultiplyer = 4f;
    private static final int explosionFrame = 10;


    public Mine(Vector2 pos, Vector2 size) {
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
        if (entity instanceof Ball) {
            animacao.playOnce();

            return true;
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
            System.out.println("killing");
            System.out.println(animacao.getScaleX()*animacao.getScaleX()*animacao.getWidth()*animacao.getWidth()/4);
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
