package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.UI.Button;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class ConfigScreen implements Screen 
{
    private Main game;
    private Stage stage;
    private Table mainTable;
    private Skin skinLabel, skinSlider;
    private Label labelSliderMusicVolume, labelSliderSoundVolume;
    private Slider sliderMusicVolume, sliderSoundVolume;
    private Button back;

    private Texture texture;
    private TextureRegion backgroundRegion;
    private SpriteBatch batch;

    private float scrollX = 0;
    private float scrollY = 0;
    private float scrollSpeed = 50f;
    private float textureScale = 2f;

    public ConfigScreen(Main game)
    {
        this.game = game;

        stage = new Stage(new ScreenViewport());

        skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        skinSlider = new Skin(Gdx.files.internal("slider/slider.json"));

        batch = new SpriteBatch();

        texture = new Texture(Gdx.files.internal("backgrounds/loginScreen/greenprint.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        backgroundRegion = new TextureRegion(texture);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        backgroundRegion.setRegion(0, 0, width, height);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setVisible(true);
        mainTable.center();

        labelSliderMusicVolume = new Label("Music volume: ", skinLabel, "labelPixelyWhite32");
        labelSliderSoundVolume = new Label("Sound volume: ", skinLabel, "labelPixelyWhite32");

        sliderMusicVolume = new Slider(0f,1f, 0.1f, false, skinSlider, "default-horizontal");
        sliderSoundVolume = new Slider(0f, 1f, 0.1f, false, skinSlider, "default-horizontal");

        sliderMusicVolume.setValue(SoundManager.getInstance().getMasterVolumeMusic());
        sliderSoundVolume.setValue(SoundManager.getInstance().getMasterVolumeSounds());

        back = new Button();
        ImageButton imageButtonBack = back.createButton("back", "back");

        imageButtonBack.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
            {
                SoundManager.getInstance().playSound("buttonClick");
                game.setScreen(new MainMenu(game));
                return true;
            }
        });

        sliderMusicVolume.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                SoundManager.getInstance().setMasterVolumeMusic(sliderMusicVolume.getValue());
            }
        });
        
        sliderSoundVolume.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                SoundManager.getInstance().setMasterVolumeSounds(sliderSoundVolume.getValue());
            }
        });

        Table backButtonTable = new Table();
        backButtonTable.setFillParent(true);
        backButtonTable.setVisible(true);
        backButtonTable.top().left();
        backButtonTable.add(imageButtonBack).size(80, 80).pad(10).center();

        mainTable.add(labelSliderMusicVolume).size(300, 50).padTop(15).padBottom(5).center();
        mainTable.row();
        mainTable.add(sliderMusicVolume).width(300).height(40).padBottom(15).center();
        mainTable.row();
        mainTable.add(labelSliderSoundVolume).size(300, 50).padTop(15).padBottom(5).center();
        mainTable.row();
        mainTable.add(sliderSoundVolume).width(300).height(40).padBottom(25).center();
        mainTable.row();
        
        stage.addActor(mainTable);
        stage.addActor(backButtonTable);


    }

    @Override
    public void show() 
    {
        CursorManager.resetToArrow();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) 
    {

        scrollX += scrollSpeed * delta;
        scrollY += scrollSpeed * delta;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        backgroundRegion.setRegion(
            (int)scrollX, (int)scrollY,
            (int)(width / textureScale),
            (int)(height / textureScale)
        );

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundRegion, 0, 0, width, height);
        batch.end();
        
        stage.act(delta);
        stage.draw();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() 
    {
        stage.dispose();
        skinLabel.dispose();
    }
}
