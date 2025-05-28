package io.github.buraconcio.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Player;

public class MainMenu implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;
    private Animation<TextureRegion> buraconcioAnimation, backAnimation;
    private float elapsedTimeName = 0f, elapsedTimeBack = 0f;
    private Texture spriteSheet1, spriteSheet2;
    private Image buraconcioImage, backImage;

    public MainMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // usa fonte padrão

        spriteSheet1 = new Texture(Gdx.files.internal("backgroundMenu.png"));
        JsonValue json1 = new JsonReader().parse(Gdx.files.internal("backgroundMenu.json"));
        JsonValue framesJson1 = json1.get("frames");

        ArrayList<TextureRegion> frames1 = new ArrayList<>();

        for (int i = 0; i < framesJson1.size; i++) {
            JsonValue frameObj = framesJson1.get(i);
            JsonValue frameData = frameObj.get("frame");
        
            int x = frameData.getInt("x");
            int y = frameData.getInt("y");
            int w = frameData.getInt("w");
            int h = frameData.getInt("h");
        
            TextureRegion region = new TextureRegion(spriteSheet1, x, y, w, h);
            frames1.add(region);
        }

        spriteSheet2 = new Texture(Gdx.files.internal("nomeMenu.png"));
        JsonValue json2 = new JsonReader().parse(Gdx.files.internal("nomeMenu.json"));
        JsonValue framesJson2 = json2.get("frames");

        ArrayList<TextureRegion> frames2 = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String key = "buraconcio " + i + ".ase";
            JsonValue frameData2 = framesJson2.get(key).get("frame");

            int x = frameData2.getInt("x");
            int y = frameData2.getInt("y");
            int w = frameData2.getInt("w");
            int h = frameData2.getInt("h");

            TextureRegion region = new TextureRegion(spriteSheet2, x, y, w, h);
            frames2.add(region);
        }

        backAnimation = new Animation<>(0.1f, frames1.toArray(new TextureRegion[0]));
        backAnimation.setPlayMode(Animation.PlayMode.LOOP);

        backImage = new Image(new TextureRegionDrawable(backAnimation.getKeyFrame(0)));

        buraconcioAnimation = new Animation<>(0.1f, frames2.toArray(new TextureRegion[0]));
        buraconcioAnimation.setPlayMode(Animation.PlayMode.LOOP);

        buraconcioImage = new Image(new TextureRegionDrawable(buraconcioAnimation.getKeyFrame(0)));

        TextButton playButton = new TextButton("Play", skin);
        TextButton hostButton = new TextButton("Host", skin);
        TextButton creditsButton = new TextButton("Credits", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PhysicsTest(game));
            }
        });

        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ServerScreen(game));
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        backImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backImage.setPosition(0, 0);

        Table table = new Table();

        table.setFillParent(true);
        table.center();
        table.add(buraconcioImage).padBottom(60).width(900).height(160).expandX().center().padRight(-22);
        table.row();
        table.add(playButton).size(400, 90).pad(15);
        table.row();
        table.add(hostButton).size(400, 90).pad(15);
        table.row();
        table.add(creditsButton).size(400, 90).pad(15);
        table.row();
        table.add(quitButton).size(400, 90).pad(15);

        table.setDebug(false);

        stage.addActor(backImage);
        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        elapsedTimeBack += delta * 0.7f;
        elapsedTimeName += delta;

        backImage.setDrawable(new TextureRegionDrawable(backAnimation.getKeyFrame(elapsedTimeBack)));
        buraconcioImage.setDrawable(new TextureRegionDrawable(buraconcioAnimation.getKeyFrame(elapsedTimeName)));

        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        spriteSheet2.dispose();
    }
}
