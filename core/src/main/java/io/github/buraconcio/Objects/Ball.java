package io.github.buraconcio.Objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Ball extends Actor {
    private int xSpeed, ySpeed;

    private Sprite sprite;

    public Ball(int x, int y, int r, int xSpeed, int ySpeed) {
        super();

        setBounds(x, y, r, r);
        //this.x = x;
        //this.y = y;
        //this.r = r;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;

        Texture texture = new Texture(Gdx.files.internal("ball.png"));
        sprite = new Sprite(texture);
        sprite.setSize(r, r);

        //sprite.setPosition(getX(), getY());
        //sprite.setRotation(0);

        //sprite.setBounds(x, y, r, r);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY());
        sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.setPosition(getX() + this.xSpeed*delta, getY() + this.ySpeed*delta);
   }
}
