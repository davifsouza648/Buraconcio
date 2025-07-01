package io.github.buraconcio.Utils.Common;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.buraconcio.Utils.Managers.PhysicsManager;

public class AnimationPlay extends Image {
    private Animation<TextureRegion> animation;
    private float elapsedTime = 0f;
    protected Actor parentActor;
    private int running = 1;
    private boolean playOnce = false;
    private float flashDuration = 0.2f;
    private float flashTime = -1f;


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

    public void playOnce() {
        running = 1;
        playOnce = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(Align.center);
        setPosition(parentActor.getX(), parentActor.getY());
        setRotation(parentActor.getRotation());

        super.draw(batch, parentAlpha);
    }

    public void drawImage(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        if (playOnce && isLastFrame()) {
            elapsedTime = 0f;
            running = 0;
        }

        if (flashTime != -1f) {
            flashTime += delta;
            float scl = 1.75f * Math.abs(flashTime - 0.5f) + 0.125f;

            setColor(new Color(1f, scl, scl, 1f));

            if (flashTime - delta >= flashDuration) {
                flashTime = -1f;

                setColor(Color.WHITE);
            }
        }

        elapsedTime += delta * running;
        setDrawable(new TextureRegionDrawable(animation.getKeyFrame(elapsedTime)));
    }

    public void flashRed() {
        flashTime = 0f;
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

    public int getFrameIndex() {
        return animation.getKeyFrameIndex(elapsedTime);
    }

    public int getNumFrames() {
        return (int) (animation.getAnimationDuration() / animation.getFrameDuration());
    }
}
