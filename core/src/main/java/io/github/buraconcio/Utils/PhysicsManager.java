package io.github.buraconcio.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Runnable;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Objects.PhysicsEntity;
import io.github.buraconcio.Objects.Player;

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

    private Vector2 startingAreaTop;
    private Vector2 startingAreaBot;

    private HashMap<Integer, Vector2> playerStartPosById;

    public PhysicsManager() {
        world = new World(new Vector2(0f, 0f), true);
        box2dScheduler = new ArrayList<Runnable>();
        entityList = new HashMap<Integer, PhysicsEntity>();
        id = 0;
        contactList = new ArrayList<Contact>();
        stage = null;

        Vector2 startingAreaSize = new Vector2(1f, 3f);
        Vector2 startingAreaPos = new Vector2(3f, 3f);

        startingAreaTop = new Vector2(
                startingAreaPos.x - startingAreaSize.x / 2,
                startingAreaPos.y + startingAreaSize.y / 2);

        startingAreaBot = new Vector2(
                startingAreaPos.x + startingAreaSize.x / 2,
                startingAreaPos.y - startingAreaSize.y / 2);

        BodyDef bd = new BodyDef();
        bd.position.set(startingAreaPos);
        Body startingArea = world.createBody(bd);

        PolygonShape startingAreaShape = new PolygonShape();
        startingAreaShape.setAsBox(startingAreaSize.x, startingAreaSize.y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = startingAreaShape;
        fixtureDef.isSensor = true;

        startingArea.createFixture(fixtureDef);
        startingAreaShape.dispose();

        playerStartPosById = new HashMap<Integer, Vector2>();

        random = new Random();
    }

    public static synchronized PhysicsManager getInstance() {
        if (instance == null) {
            instance = new PhysicsManager();
        }

        return instance;
    }

    public synchronized void tick() {
        ArrayList<Runnable> copy = new ArrayList<>(box2dScheduler);
        copy.forEach(Runnable::run);
        clearScheduler();

        // collisions
        Iterator<Contact> it = contactList.iterator();
        while (it.hasNext()) {
            Contact contact = it.next();

            try {

                PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
                PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

                // return true if contact should be removed
                // be carefull not to run same collision logic on both objects
                boolean dA = entityA.contact(entityB);
                boolean dB = entityB.contact(entityA);
                if (dA || dB) { // nao iria rodar as duas colisoes se funcoes tivessem dentro do if :(
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
        // Runnable task = () -> {world.destroyBody(body);};
        // schedule(task);
        world.destroyBody(body);
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
                entityA = getEntity(contact.getFixtureA().getBody().getUserData());
                entityB = getEntity(contact.getFixtureB().getBody().getUserData());

                exentityA = getEntity(exContact.getFixtureA().getBody().getUserData());
                exentityB = getEntity(exContact.getFixtureB().getBody().getUserData());

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
        if (player.getBall() != null) {
            System.out.println("player already has ball");
            return;
        }

        for (int i = 0; i < 1000; ++i) { // max 1000
            boolean collides = false;

            Vector2 pos = new Vector2(getRandomFloat(startingAreaTop.x, startingAreaBot.x),
                    getRandomFloat(startingAreaTop.y, startingAreaBot.y));

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

    public float getRandomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
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

    // TODO: METODO PARA ATUALIZAR VETOR DE ENTIDADES
}
