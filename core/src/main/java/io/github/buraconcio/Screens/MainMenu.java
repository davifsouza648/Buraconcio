package io.github.buraconcio.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Utils.CursorManager;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Objects.Button;

public class MainMenu implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;
    private ImageButton imageButtonHost, imageButtonQuit, imageButtonJoin, imageButtonCredits;
    private Animation<TextureRegion> buraconcioAnimation, backAnimation;
    private float elapsedTimeName = 0f, elapsedTimeBack = 0f;
    private Image buraconcioImage, backImage;

    public MainMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // usa fonte padrão

        backAnimation = Auxiliaries.animationFromFiles("backgroundMenu.png", "backgroundMenu.json");
        backImage = new Image(new TextureRegionDrawable(backAnimation.getKeyFrame(0)));

        buraconcioAnimation = Auxiliaries.animationFromFiles("nomeMenu.png", "nomeMenu.json");
        buraconcioImage = new Image(new TextureRegionDrawable(buraconcioAnimation.getKeyFrame(0)));

        Button join = new Button();
        Button host = new Button();
        Button credits = new Button();
        Button quit = new Button();
        imageButtonJoin = join.createButton("join", "join");
        imageButtonHost = host.createButton("host", "host");
        imageButtonCredits = credits.createButton("credits", "credits");
        imageButtonQuit = quit.createButton("quit", "quit");

        imageButtonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerManager.getInstance().getLocalPlayer().setHosting(false);

                // game.setScreen(new PhysicsTest(game));

                game.setScreen(new LoadingScreen(game)); // arrumar loading screen

                // game.setScreen(new ServerScreen(game));

            }
        });

        imageButtonHost.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerManager.getInstance().getLocalPlayer().setHosting(true);
                game.setScreen(new ServerScreen(game));
            }
        });

        imageButtonCredits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // TODO: DAVI VAI FAZER A TELA;
                game.setScreen(new CreditsScreen(game));

            }
        });

        imageButtonQuit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        backgroundTable.add(backImage).expand().fill();

        Table table = new Table();

        table.setFillParent(true);
        table.center();
        table.add(buraconcioImage).padBottom(60).width(900).height(160).expandX().center().padRight(-22);
        table.row();
        table.add(imageButtonJoin).pad(15);
        table.row();
        table.add(imageButtonHost).pad(15);
        table.row();
        table.add(imageButtonCredits).pad(15);

        createTestButton(table);

        table.row();
        table.add(imageButtonQuit).pad(15);

        table.setDebug(false);

        stage.addActor(backgroundTable);
        stage.addActor(table);

    }

    public void createTestButton(Table table) {

        TextButton teste = new TextButton("TESTE A PHYSICS AQUI", skin);
        teste.setSize(150, 90);
        teste.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PhysicsTest(game));
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
