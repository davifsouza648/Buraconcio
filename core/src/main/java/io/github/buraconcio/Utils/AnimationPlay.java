package io.github.buraconcio.Utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationPlay extends Image
{
    private Animation<TextureRegion> animation;
    private float elapsedTime = 0f;

    public AnimationPlay(Animation<TextureRegion> animation) 
    {
        super(new TextureRegionDrawable(animation.getKeyFrame(0)));
        this.animation = animation;
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void act(float delta) 
    {
        super.act(delta);
        elapsedTime += delta;
        setDrawable(new TextureRegionDrawable(animation.getKeyFrame(elapsedTime)));
    }

    public TextureRegion getCurrentFrame()
    {
        return animation.getKeyFrame(elapsedTime);
    }
    
}
