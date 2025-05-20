package io.github.poo.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.poo.game.Main;
import io.github.poo.game.Objects.Player;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class LoginMenu implements Screen {

    private Main game;
    private Stage stage;
    private TextField userField, passField;
    private Skin skin;
    private String username;
    // private String password;

    public LoginMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        userField = new TextField("", skin);
        userField.setMessageText("Digite seu username");

        // passField = new TextField("", skin);
        // passField.setMessageText("Digite sua senha");
        // passField.setPasswordMode(true);
        // passField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("Entrar", skin);

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                username = userField.getText();

                //verificar melhor momento paa criar o id do player

                Player player = new Player(username);

                player.setId(0);

                game.getPlayerManager().addPlayer(player);

                System.out.println(player.getUsername());

                game.setScreen(new MainMenu(game));
            }
        });

        table.add(userField).width(200).pad(10);
        table.row();
        // table.add(passField).width(200).pad(10);
        // table.row();
        table.add(loginButton).colspan(2).padTop(10);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

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
    }
}
