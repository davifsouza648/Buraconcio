package io.github.buraconcio.Utils;

import java.util.ArrayList;
import java.lang.Runnable;
import java.util.Hashtable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Contact;

import io.github.buraconcio.Objects.PhysicsEntity;

// singleton
public class PhysicsManager {
    private static PhysicsManager instance;
    private static final float tickrate = 1/60f;

    World world;
    ArrayList<Contact> contactList;
    Hashtable<Integer, PhysicsEntity> entityList;
    int id;
    ArrayList<Runnable> box2dScheduler;

    public PhysicsManager() {
        world = new World(new Vector2(0f, 0f), true);
        box2dScheduler = new ArrayList<Runnable> ();
        entityList = new Hashtable<Integer, PhysicsEntity> ();
        id = 0;
        contactList = new ArrayList<Contact> ();
    }

    public static synchronized PhysicsManager getInstance() {
        if (instance == null) {
            instance = new PhysicsManager();
        }

        return instance;
    }

    public void tick() {
        box2dScheduler.forEach(task -> task.run());
        clearScheduler();

        // collisions
        for (Contact contact : contactList) {

            PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

            entityA.contact(entityB);
            entityB.contact(entityA);
            // be carefull not to run same collision logic on both objects
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

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public void addContact(Contact contact) {
        boolean duplicate = false;

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

        if (!duplicate) {
            Runnable task = () -> {contactList.add(contact);};
            schedule(task);
        }
    }

    public void removeContact(Contact contact) {
        for (int i = 0; i < contactList.size(); ++i) {
            PhysicsEntity entityA = getEntity(contact.getFixtureA().getBody().getUserData());
            PhysicsEntity entityB = getEntity(contact.getFixtureB().getBody().getUserData());

            PhysicsEntity exentityA = getEntity(contactList.get(i).getFixtureA().getBody().getUserData());
            PhysicsEntity exentityB = getEntity(contactList.get(i).getFixtureB().getBody().getUserData());

            if (entityA.getId() == exentityA.getId() && entityB.getId() == exentityB.getId()) {
                final int index = i;

                Runnable task = () -> {contactList.remove(index);};
                schedule(task);
                break;
            }
        }
    }

    public void resetContactList() {
        contactList.clear();
    }
}
