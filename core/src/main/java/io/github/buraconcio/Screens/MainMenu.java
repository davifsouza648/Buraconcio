package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Player;

public class MainMenu implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;

    public MainMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // usa fonte padrão

        Label title = new Label("Bem-vindo ao Golfzin!", skin); //trocar por imagem
        title.setFontScale(3f);

        TextButton playButton = new TextButton("Play", skin);
        TextButton hostButton = new TextButton("Host", skin);
        TextButton creditsButton = new TextButton("Credits", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        Table table = new Table();

        table.setFillParent(true);
        table.center();
        table.add(title).padBottom(60);
        table.row();
        table.add(playButton).size(400, 90).pad(15);
        table.row();
        table.add(hostButton).size(400, 90).pad(15);
        table.row();
        table.add(creditsButton).size(400, 90).pad(15);
        table.row();
        table.add(quitButton).size(400, 90).pad(15);
        table.setDebug(true);
        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
