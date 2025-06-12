package io.github.buraconcio.Utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationPlay extends Image {
    private Animation<TextureRegion> animation;
    private float elapsedTime = 0f;
    private Actor parentActor;
    private int running = 1;


    public AnimationPlay(Animation<TextureRegion> animation, Actor parentActor)
    {
        super(new TextureRegionDrawable(animation.getKeyFrame(0)));

        this.parentActor = parentActor;
        this.animation = animation;

        setSize(parentActor.getWidth(), parentActor.getWidth()*getSize().y/getSize().x);

        if (parentActor.getWidth() == -1)
            setSize(parentActor.getHeight()*getSize().x/getSize().y, parentActor.getHeight());

        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        PhysicsManager.getInstance().getStage().addActor(this);
    }

    public AnimationPlay(String texturePath, Actor parentActor) {
        super();

        this.parentActor = parentActor;

        Texture texture = new Texture(Gdx.files.internal(texturePath));
        Sprite sprite = new Sprite(texture);

        Array<TextureRegion> frame = new Array<TextureRegion>();
        frame.add(sprite);

        animation = new Animation<TextureRegion>(100f, frame);

        setDrawable(new TextureRegionDrawable(animation.getKeyFrame(0)));

        setSize(parentActor.getWidth(), parentActor.getWidth()*(sprite.getHeight()/sprite.getWidth()));
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        PhysicsManager.getInstance().getStage().addActor(this);
    }

    public void pauseAnimation() {
        running = 0;
    }

    public void resumeAnimation() {
        running = 1;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(Align.center);
        setPosition(parentActor.getX(), parentActor.getY());
        setRotation(parentActor.getRotation());

        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        elapsedTime += delta * running;
        setDrawable(new TextureRegionDrawable(animation.getKeyFrame(elapsedTime)));
    }

    public boolean isLastFrame() {
        Object[] frames = animation.getKeyFrames();

        return animation.getKeyFrameIndex(elapsedTime) == frames.length - 1;
    }

    public TextureRegion getCurrentFrame()
    {
        return animation.getKeyFrame(elapsedTime);
    }

    public Vector2 getSize() {
        return new Vector2(getPrefWidth(), getPrefHeight());
    }

    public void setFrameDuration(float speed) {
        animation.setFrameDuration(speed);
    }

    public float getFrameDuration() {
        return animation.getFrameDuration();
    }
}
