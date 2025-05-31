package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Button;
import io.github.buraconcio.Utils.PlayerManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import java.util.*;
import java.util.List;
public class LoginMenu implements Screen {

    private Main game;
    private Stage stage;
    private TextField userField, passField;
    private Label warningUserLabel;
    private Skin skinTextField, skinLabel;
    private String username;
    // private String password;

    // String[] avatarVec = {"user1.png", "user2.png", "user3.png", "user3.png", "user4.png", "user5.png", "user6.png", "user7.png", "user8.png", "user9.png", "user10.png"};

    public LoginMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));


        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);
        table.setDebug(true);

        userField = new TextField("", skinTextField, "labelPixelyWhite32");
        userField.setMessageText("Digite seu username");

        Button entrar = new Button();
        ImageButton loginButton = entrar.createButton("enter", "enter");

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                username = userField.getText();

                //verificar melhor momento paa criar o id do player

                Player player = new Player(username);

                Random random = new Random();

                player.setId(username.hashCode() + random.nextInt(25));

                player.setAvatar(random.nextInt(1, 32));

                PlayerManager.getInstance().setLocalPlayer(player);

                System.out.println("Usuario: " + player.getUsername());

                game.setScreen(new MainMenu(game));

            }
        });

        table.add(userField).width(500).pad(10);
        table.row();
        // table.add(passField).width(200).pad(10);
        // table.row();
        table.add(loginButton).size(200, 80).colspan(2).padTop(10);
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
        skinTextField.dispose();
    }

}
