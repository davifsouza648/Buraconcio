package io.github.buraconcio.Objects.Obstacles;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import io.github.buraconcio.Utils.Common.AnimationPlay;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;
import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Objects.Game.Player;

public class Train extends Obstacle {

    private final Vector2 direction;
    private static final Vector2 size = new Vector2(2f, 11.59f);

    public Train(Vector2 pos, int directionIndex) 
    {
        super(pos, size, Auxiliaries.animationFromFiles("obstacles/train/train.png", "obstacles/train/train.json"));

        body.setType(BodyType.KinematicBody);
        this.direction = getDirectionFromIndex(directionIndex).nor().scl(8f);
        body.setLinearVelocity(this.direction);
        body.setBullet(true); 

        PolygonShape shapeDef = new PolygonShape();
        shapeDef.setAsBox(size.x/2, size.y/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeDef;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        shapeDef.dispose();

        switch (directionIndex) 
        {
            case 0: 
                body.setTransform(body.getPosition(), (float) (-Math.PI / 2)); // -90°
                break;
            case 1: 
                body.setTransform(body.getPosition(), 0f);
                break;
            case 2: 
                body.setTransform(body.getPosition(), (float) (Math.PI / 2)); 
                break;
            case 3: 
                body.setTransform(body.getPosition(), (float) Math.PI); 
                break;
        }

        act(0f); 
    }

    private Vector2 getDirectionFromIndex(int dir) {
        switch (dir) {
            case 0: return new Vector2(1, 0);   // direita
            case 1: return new Vector2(0, 1);   // cima
            case 2: return new Vector2(-1, 0);  // esquerda
            case 3: return new Vector2(0, -1);  // baixo
            default: return new Vector2(1, 0);  // padrão: direita
        }
    }

    @Override
    public void act(float delta) 
    {
        super.act(delta);
    }

    @Override
    public boolean contact(PhysicsEntity entity)
    {


        if (entity instanceof Ball) 
        {
            Ball ball = (Ball) entity;

            if (!ball.isAirborne()) 
            {
                ball.getPlayer().die();
                return true;
            }
        }
        else
        {
            this.destroy();
            return true;
        }
        

        return false;
    }
}
