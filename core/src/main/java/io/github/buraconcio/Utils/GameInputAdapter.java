package io.github.buraconcio.Utils;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Ball;
import io.github.buraconcio.Objects.GameCamera;

public class GameInputAdapter extends InputAdapter {
    
    private Vector2 mouse1 = new Vector2();
    private Player p;
    private Ball pBall;
    private GameCamera camera;
    private Stage stage;

    public GameInputAdapter(Player p, Ball pBall, GameCamera camera, Stage stage) 
    {
        super();
        this.p = p;
        this.pBall = pBall;
        this.camera = camera;
        this.stage = stage;

        PhysicsManager.getInstance().getWorld().setContactListener(new ContactListener() {
            @Override
            public void endContact(Contact contact) {
                // PhysicsManager.getInstance().removeContact(contact);
            }

            @Override
            public void beginContact(Contact contact) {
                PhysicsManager.getInstance().addContact(contact);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        mouse1.x = x;
        mouse1.y = y;

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        p = PlayerManager.getInstance().getLocalPlayer();
        Vector3 unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
        mouse1 = new Vector2(unprojected.x, unprojected.y);

        unprojected = camera.unproject(new Vector3(x, y, 0));
        Vector2 mouse2 = new Vector2(unprojected.x, unprojected.y);

        Runnable task = () -> {
            p.stroke(mouse1, mouse2);
        };

        PhysicsManager.getInstance().schedule(task);
        pBall.resetShootingGuide();

        // test

        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
        Actor hitActor = stage.hit(stageCoords.x, stageCoords.y, true);

        Obstacle obstacle = p.getSelectedObstacle();
        if (obstacle != null && obstacle.canPlace()) {
            p.placeObstacle();
            obstacle.preRound();
        } else if (hitActor instanceof Obstacle) {
            Obstacle hitObstacle = (Obstacle) hitActor;
            if (!hitObstacle.claimed())
                p.selectObstacle(hitObstacle);
        }

        return true;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));
        Vector2 currentMouse = new Vector2(unprojected.x, unprojected.y);

        unprojected = camera.unproject(new Vector3(mouse1.x, mouse1.y, 0));
        pBall.setShootingGuide(new Vector2(unprojected.x, unprojected.y), new Vector2(currentMouse));

        return true;
    }

    public boolean mouseMoved(int x, int y) {
        p = PlayerManager.getInstance().getLocalPlayer();
        Vector3 unprojected = camera.unproject(new Vector3(x, y, 0));

        if (p.getSelectedObstacle() != null)
            p.getSelectedObstacle().move(new Vector2(unprojected.x, unprojected.y));

        return true;
    }

    @Override
    public boolean keyDown(int keyCode) 
    {
        p = PlayerManager.getInstance().getLocalPlayer();
        if (keyCode == Keys.Q && p.getSelectedObstacle() != null) 
        {
            p.getSelectedObstacle().rotate(Obstacle.COUNTER_CLOCKWISE);
        } 
        else if (keyCode == Keys.E && p.getSelectedObstacle() != null) 
        {
            p.getSelectedObstacle().rotate(Obstacle.CLOCKWISE);
        }
        return true;
    }



}
