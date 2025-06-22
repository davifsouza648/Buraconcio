package io.github.buraconcio.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Objects.UI.Button;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class MainMenu implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;
    private ImageButton imageButtonHost, imageButtonQuit, imageButtonJoin, imageButtonCredits, imageButtonConfig;
    private Animation<TextureRegion> buraconcioAnimation, backAnimation;
    private float elapsedTimeName = 0f, elapsedTimeBack = 0f;
    private Image buraconcioImage, backImage;

    public MainMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // usa fonte padrão
        SoundManager.getInstance().loadMusic("menu", "sounds/songs/menuTheme.mp3");
        SoundManager.getInstance().playMusic("menu");

        backAnimation = Auxiliaries.animationFromFiles("backgrounds/backgroundMenu.png", "backgrounds/backgroundMenu.json");
        backImage = new Image(new TextureRegionDrawable(backAnimation.getKeyFrame(0)));

        buraconcioAnimation = Auxiliaries.animationFromFiles("backgrounds/nomeMenu.png", "backgrounds/nomeMenu.json");
        buraconcioImage = new Image(new TextureRegionDrawable(buraconcioAnimation.getKeyFrame(0)));

        Button join = new Button();
        Button host = new Button();
        Button credits = new Button();
        Button quit = new Button();
        Button config = new Button();

        imageButtonJoin = join.createButton("join", "join");
        imageButtonHost = host.createButton("host", "host");
        imageButtonCredits = credits.createButton("credits", "credits");
        imageButtonQuit = quit.createButton("quit", "quit");
        imageButtonConfig = config.createButton("config", "config");

        imageButtonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerManager.getInstance().getLocalPlayer().setHosting(false);

                // game.setScreen(new PhysicsTest());

                game.setScreen(new LoadingScreen(game)); // arrumar loading screen
                SoundManager.getInstance().playSound("buttonClick");
                // game.setScreen(new ServerScreen(game));

            }
        });

        imageButtonHost.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerManager.getInstance().getLocalPlayer().setHosting(true);
                game.setScreen(new ServerScreen(game));
                SoundManager.getInstance().playSound("buttonClick");
            }
        });

        imageButtonCredits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                game.setScreen(new CreditsScreen(game));
                SoundManager.getInstance().playSound("buttonClick");
            }
        });

        imageButtonQuit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.getInstance().playSound("buttonClick");
                Gdx.app.exit();
            }
        });
        
        imageButtonConfig.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) 
            {
                SoundManager.getInstance().playSound("buttonClick");
                game.setScreen(new ConfigScreen(game));
            }
        });

        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        backgroundTable.add(backImage).expand().fill();

        Table table = new Table();

        table.setFillParent(true);
        table.center();
        table.add(buraconcioImage).padBottom(60).width(1400).height(140).expandX().center().padRight(-22);
        table.row();
        table.add(imageButtonJoin).pad(15);
        table.row();
        table.add(imageButtonHost).pad(15);
        table.row();
        table.add(imageButtonCredits).pad(15);

        //createTestButton(table);

        table.row();
        table.add(imageButtonQuit).pad(15);

        table.row();

        Table tableConfigButton = new Table();
        tableConfigButton.setFillParent(true);
        tableConfigButton.bottom().left();
        tableConfigButton.add(imageButtonConfig).pad(15);

        table.setDebug(false);

        stage.addActor(backgroundTable);
        stage.addActor(table);
        stage.addActor(tableConfigButton);

    }

    public void createTestButton(Table table) {

        TextButton teste = new TextButton("TESTE A PHYSICS AQUI", skin);
        teste.setSize(150, 90);
        teste.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PhysicsTest screen = new PhysicsTest(game);
                GameManager.getInstance().setPhysicsScreen(screen);
                GameManager.getInstance().setCurrentScreen(game, screen);

                stage.dispose();
            }
        });

        table.row();
        table.add(teste).pad(15);

    }

    @Override
    public void show() {
        CursorManager.resetToArrow();

    }

    @Override
    public void render(float delta) {
        elapsedTimeBack += delta * 0.7f;
        elapsedTimeName += delta;

        buraconcioImage.setDrawable(new TextureRegionDrawable(buraconcioAnimation.getKeyFrame(elapsedTimeName)));
        backImage.setDrawable(new TextureRegionDrawable(backAnimation.getKeyFrame(elapsedTimeBack)));

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
    }
}
