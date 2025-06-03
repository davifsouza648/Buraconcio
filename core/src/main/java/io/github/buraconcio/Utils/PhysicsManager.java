package io.github.buraconcio.Utils;

import java.util.ArrayList;
import java.lang.Runnable;
import java.util.Hashtable;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.buraconcio.Objects.PhysicsEntity;

// singleton
public class PhysicsManager {
    private static PhysicsManager instance;
    private static final float tickrate = 1/60f;

    World world;
    Stage stage;
    ArrayList<Runnable> box2dScheduler;
    ArrayList<Contact> contactList;
    Hashtable<Integer, PhysicsEntity> entityList;
    int id;

    public PhysicsManager() {
        world = new World(new Vector2(0f, 0f), true);
        box2dScheduler = new ArrayList<Runnable> ();
        entityList = new Hashtable<Integer, PhysicsEntity> ();
        id = 0;
        contactList = new ArrayList<Contact> ();
        stage = null;
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

            PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

            // return true if contact should be removed
            // be carefull not to run same collision logic on both objects
            boolean dA = entityA.contact(entityB);
            boolean dB = entityB.contact(entityA);
            if (dA || dB) { // nao iria rodar as duas colisoes se funcoes tivessem dentro do if :(
                it.remove();
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
        //Runnable task = () -> {world.destroyBody(body);};
        //schedule(task);
        world.destroyBody(body);
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public void addContact(Contact contact) {
        Runnable task = () -> {
        boolean duplicate = false;

        for (Contact existingContact : contactList) {
            PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

            PhysicsEntity exentityA = getEntity(existingContact.getFixtureA().getBody().getUserData());
            PhysicsEntity exentityB = getEntity(existingContact.getFixtureB().getBody().getUserData());

            if (entityA.getId() == exentityA.getId() && entityB.getId() == exentityB.getId()) {
                System.out.println("found duplicate");
                duplicate = true;
                break;
            }
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
        while(it.hasNext()) {
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
}
