package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import io.github.buraconcio.Utils.PhysicsManager;
import io.github.buraconcio.Utils.AnimationPlay;

public class PhysicsEntity extends Actor {

    protected Sprite sprite;
    protected Body body;
    protected int id;
    protected AnimationPlay animacao;

    public PhysicsEntity(Vector2 pos, Vector2 size)
    {
        super();

        setPosition(pos.x - size.x/2, pos.y - size.y/2);
        setOrigin(Align.center);

        setSize(size.x, size.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);
        body = PhysicsManager.getInstance().getWorld().createBody(bodyDef);

        PhysicsManager.getInstance().addEntity(this);
    }

    public PhysicsEntity(Vector2 pos, Vector2 size, String texturePath) {
        this(pos, size);

        animacao = new AnimationPlay(texturePath, this);
        PhysicsManager.getInstance().addToStage(this); // choggs pq precisa ser posto dps da animacao

        if (size.y == -1f || size.x == -1f) setSize(animacao.getWidth(), animacao.getHeight());
    }

    public PhysicsEntity(Vector2 pos, Vector2 size, Animation<TextureRegion> animacao) {
        this(pos, size);

        this.animacao = new AnimationPlay(animacao, this);
        PhysicsManager.getInstance().addToStage(this);

        if (size.y == -1f || size.x == -1f) setSize(this.animacao.getWidth(), this.animacao.getHeight());
    }

    public PhysicsEntity(Vector2 pos, Vector2 size, AnimationPlay animacao) {
        this(pos, size);

        this.animacao = animacao;
        PhysicsManager.getInstance().addToStage(this);

        if (size.y == -1f || size.x == -1f) setSize(animacao.getWidth(), animacao.getHeight());
    }

    // Método para destruir a entidade
    public void destroy() {
        PhysicsManager.getInstance().destroyBody(body);

        remove();
        animacao.remove();
    }

    // Método para setar o id
    // uau muito obrigado nao sabia
    public void setId(int id) {
        body.setUserData(id);
        this.id = id;
    }

    // Método para obter o id
    public int getId() {
        return id;
    }

    public boolean contact(PhysicsEntity entity)
    {
        return false;
    }

    // Método para atualizar animação e posição da entidade
    @Override
    public void act(float delta) {
        super.act(delta);

        setOrigin(Align.center);
        this.setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        this.setRotation(body.getAngle() * 180f / 3.14f);
    }

    // Método para obter o corpo físico
    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return new Vector2(this.getX(), this.getY());
    }

    public Vector2 getWorldPosition() {
        return body.getPosition();
    }

    public void applyForce(Vector2 force) {
        body.applyForceToCenter(force, true);
    }
}

