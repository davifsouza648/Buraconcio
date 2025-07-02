package io.github.buraconcio.Utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Runnable;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.buraconcio.Objects.Obstacles.Obstacle;
import io.github.buraconcio.Objects.Obstacles.WoodBox;
import io.github.buraconcio.Utils.Common.PhysicsEntity;
import io.github.buraconcio.Objects.Game.Ball;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Constants;
import java.util.Map;
import java.util.Iterator;

// singleton
public class PhysicsManager {
    private static PhysicsManager instance;
    private static final float tickrate = 1 / 60f;

    private World world;
    private Stage stage;
    private ArrayList<Runnable> box2dScheduler;
    private ArrayList<Contact> contactList;
    private HashMap<Integer, PhysicsEntity> entityList;
    private int id;
    private Random random;

    private Rectangle startingAreaRect;

    private HashMap<Integer, Vector2> playerStartPosById;

    public PhysicsManager() {
        instance = this;

        world = new World(new Vector2(0f, 0f), true);
        box2dScheduler = new ArrayList<Runnable>();
        entityList = new HashMap<Integer, PhysicsEntity>();
        id = 0;
        contactList = new ArrayList<Contact>();
        stage = null;

        Vector2 startingAreaSize = new Vector2(4f, 6f);
        Vector2 startingAreaPos = new Vector2(7f, 5f); // center of area

        startingAreaRect = new Rectangle(
                startingAreaPos.x - startingAreaSize.x / 2,
                startingAreaPos.y - startingAreaSize.y / 2,
                startingAreaSize.x,
                startingAreaSize.y);

        PhysicsEntity startingArea = new PhysicsEntity(startingAreaPos, startingAreaSize);

        PolygonShape startingAreaShape = new PolygonShape();
        startingAreaShape.setAsBox(startingAreaRect.width / 2, startingAreaRect.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = startingAreaShape;
        fixtureDef.isSensor = true;

        startingArea.getBody().createFixture(fixtureDef);
        startingAreaShape.dispose();

        playerStartPosById = new HashMap<Integer, Vector2>();

        random = new Random();
    }

    public static synchronized PhysicsManager getInstance() {
        if (instance == null) {
            // instance = new PhysicsManager();
            new PhysicsManager();
        }

        return instance;
    }

    public synchronized void tick() {
        ArrayList<Runnable> copy = new ArrayList<>(box2dScheduler);

        try {
            copy.forEach(Runnable::run);
        } catch (Exception e) {
        }

        clearScheduler();

        // collisions
        Iterator<Contact> it = contactList.iterator();
        while (it.hasNext()) {
            Contact contact = it.next();

            try {

                boolean skipContact = false;

                PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
                PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

                if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
                    if ((entityA instanceof WoodBox) || (entityB instanceof WoodBox)) {
                        skipContact = true;
                    }
                }
                // return true if contact should be removed
                // be carefull not to run same collision logic on both objects
                if (!skipContact) {
                    boolean dA = entityA.contact(entityB);
                    boolean dB = entityB.contact(entityA);
                    if (dA || dB) { // nao iria rodar as duas colisoes se funcoes tivessem dentro do if :(
                        it.remove();
                    }
                }else{
                    it.remove();
                }

            } catch (Exception e) {
            }
        }

        world.step(tickrate, 6, 2);
    }

    public void addEntity(PhysicsEntity entity) {
        entityList.put(id, entity);
        entity.setId(id++);
    }

    public PhysicsEntity getEntity(Object id) {
        return entityList.get(Integer.parseInt(id.toString()));
    }

    public void schedule(Runnable task) {
        box2dScheduler.add(task);
    }

    public void clearScheduler() {
        box2dScheduler.clear();
    }

    public ArrayList<Runnable> getBox2dScheduler() {
        return box2dScheduler;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void destroyBody(Body body) {
        Runnable task = () -> {
            if (body == null || !entityList.containsKey((Integer) body.getUserData())) {

                if(Constants.DEBUG)
                    System.out.println("could not destroy body");

                return;
            }

            world.destroyBody(body);
            entityList.remove((Integer) body.getUserData());
        };
        schedule(task);
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public void addContact(Contact contact) {
        Runnable task = () -> {
            boolean duplicate = false;

            try {
                for (Contact existingContact : contactList) {
                    PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
                    PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

                    PhysicsEntity exentityA = getEntity(existingContact.getFixtureA().getBody().getUserData());
                    PhysicsEntity exentityB = getEntity(existingContact.getFixtureB().getBody().getUserData());

                    if (entityA.getId() == exentityA.getId() && entityB.getId() == exentityB.getId()) {
                        duplicate = true;
                        break;
                    }
                }
            } catch (Exception e) {
            }

            if (!duplicate) {
                contactList.add(contact);
            }
        };

        schedule(task);
    }

    public void removeContact(Contact contact) {

        Runnable task = () -> {

            Iterator<Contact> it = contactList.iterator();
            while (it.hasNext()) {
                Contact exContact = it.next();
                PhysicsEntity entityA, entityB, exentityA, exentityB;
                try {
                    entityA = getEntity(contact.getFixtureA().getBody().getUserData());
                    entityB = getEntity(contact.getFixtureB().getBody().getUserData());

                    exentityA = getEntity(exContact.getFixtureA().getBody().getUserData());
                    exentityB = getEntity(exContact.getFixtureB().getBody().getUserData());

                } catch (Exception e) {
                    return;
                }

                if (entityA == null || entityB == null || exentityA == null || exentityB == null)
                    return;

                if (entityA.getId() == exentityA.getId() && entityB.getId() == exentityB.getId()) {
                    it.remove();
                    break;
                }
            }
        };

        schedule(task);
    }

    public boolean ballsCollide(Vector2 pos1, Vector2 pos2) {
        return pos1.dst2(pos2) < 4 * Constants.BALL_RADIUS * Constants.BALL_RADIUS * 1.1; // 1.1 aumenta espacamento
                                                                                          // entre as bolas um pouco
    }

    public void placePlayer(Player player) {
        for (int i = 0; i < 1000; ++i) { // max 1000
            boolean collides = false;

            Vector2 pos = new Vector2(getRandomFloat(startingAreaRect.x, startingAreaRect.x + startingAreaRect.width),
                    getRandomFloat(startingAreaRect.y, startingAreaRect.y + startingAreaRect.height));

            for (Vector2 otherBallPos : playerStartPosById.values()) {
                if (ballsCollide(pos, otherBallPos)) {
                    collides = true;
                }
            }

            if (!collides) {
                player.setStartingPos(pos);
                playerStartPosById.put(player.getId(), pos);

                return;
            }
        }

        playerStartPosById.put(player.getId(), new Vector2(0f, 0f)); // ensurance
        player.setStartingPos(new Vector2(0f, 0f));
    }

    public void randomizePlayerPositions() {
        playerStartPosById.clear();

        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
            placePlayer(p);
        }
    }

    public float getRandomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public Rectangle getStratingRect() {
        return startingAreaRect;
    }

    public HashMap<Integer, Vector2> getPlayerStartPosList() {
        return playerStartPosById;
    }

    public Vector2 getPlayerStartPosById(int id) {
        return playerStartPosById.get(id);
    }

    public void resetContactList() {
        contactList.clear();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void addToStage(Actor actor) {
        stage.addActor(actor);
    }

    public void preRoundObstacles() {
        for (PhysicsEntity entity : entityList.values()) {
            if (entity instanceof Obstacle) {
                ((Obstacle) entity).preRound();
            }
        }
    }

    public void postRoundObstacles() {
        for (PhysicsEntity entity : entityList.values()) {
            if (entity instanceof Obstacle) {
                ((Obstacle) entity).postRound();
            }
        }
    }

    public void dispose() {
        while (world.isLocked())
            ;

        world.dispose();
        clearScheduler();
        resetContactList();
        entityList.clear();
        playerStartPosById.clear();
    }

    public void clearUnclaimedObstacles() {
        Iterator<Map.Entry<Integer, PhysicsEntity>> iterator = entityList.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, PhysicsEntity> entry = iterator.next();
            PhysicsEntity entity = entry.getValue();

            if (entity instanceof Obstacle obs && !obs.claimed()) {
                entity.destroy();
                iterator.remove();
            }
        }
    }

    public void destroyAllExceptBalls() {
        ArrayList<Integer> toRemove = new ArrayList<>();

        for (PhysicsEntity entity : entityList.values()) {
            if (!(entity instanceof Ball)) {
                toRemove.add(entity.getId());
            }
        }

        for (int id : toRemove) {
            PhysicsEntity entity = entityList.get(id);

            if (entity != null) {
                entity.destroy();
            }
        }
    }

    public static void destroyInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }
}
